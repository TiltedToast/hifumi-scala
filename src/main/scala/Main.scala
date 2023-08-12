import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.{EmbedBuilder, JDABuilder}
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat
import java.time.{Duration, Instant}
import java.util.Date

val dotenv = Dotenv.load()
val now = Instant.now()
val prefix = "h?"
val logger = LoggerFactory.getLogger(Main.getClass)

object Main extends App {
    private val jda = JDABuilder
        .createDefault(dotenv.get("BOT_TOKEN"))
        .enableIntents(
          GatewayIntent.GUILD_MESSAGES,
          GatewayIntent.GUILD_MEMBERS,
          GatewayIntent.GUILD_PRESENCES,
          GatewayIntent.MESSAGE_CONTENT
        )
        .build()
    java.lang.Runtime.getRuntime.addShutdownHook(Thread(() => jda.shutdownNow()))
    jda.addEventListener(
      ReadyListener(),
      MsgListener()
    )
}

final class ReadyListener extends ListenerAdapter {
    override def onReady(event: ReadyEvent): Unit = {
        val readyTime = Instant.now()
        val timeDiff = Duration.between(now, readyTime).toMillis / 1000.0
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val formatted = dateFormat.format(Date.from(readyTime))

        logger.info(s"Started up in $timeDiff seconds on $formatted")
        logger.info("Logged in as:")
        logger.info(event.getJDA.getSelfUser.getName)
        logger.info(event.getJDA.getSelfUser.getId)
        logger.info("------------------")
    }
}

final class MsgListener extends ListenerAdapter {
    override def onMessageReceived(event: MessageReceivedEvent): Unit = {
        if event.getAuthor.isBot then return
        val content = event.getMessage.getContentRaw.split(" ").toSeq

        if !content(0).startsWith(prefix) then return

        content(0).toLowerCase().stripPrefix(prefix) match {
            case "ping" => event.getChannel.sendMessage("pong!").queue()
            case "pfp" => {
                val id = content
                    .lift(1)
                    .getOrElse(event.getAuthor.getId)

                val target = event.getJDA.retrieveUserById(id).complete()

                val url = target.getEffectiveAvatarUrl + "?size=4096"
                val embed = EmbedBuilder()
                    .appendDescription("*Here's your pfp!*")
                    .setImage(url)
                    .setColor(0xce3a9b)
                    .build()

                event.getChannel.sendMessageEmbeds(embed).queue()
            }
            case _ => ()
        }
    }
}
