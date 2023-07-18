import net.dv8tion.jda.api.{JDABuilder, JDA}
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.entities.Message
import java.time.{Duration, LocalDateTime, Instant}
import java.text.SimpleDateFormat
import java.util.Date

val dotenv = Dotenv.load()
val now = Instant.now()
val prefix = "h?"

object Main extends App {
    val jda: JDA = JDABuilder
        .createDefault(dotenv.get("BOT_TOKEN"))
        .enableIntents(
          GatewayIntent.GUILD_MESSAGES,
          GatewayIntent.GUILD_MEMBERS,
          GatewayIntent.GUILD_PRESENCES,
          GatewayIntent.MESSAGE_CONTENT
        )
        .build()
    jda.addEventListener(
      ReadyListener(),
      MsgListener()
    )
    java.lang.Runtime.getRuntime().addShutdownHook(Thread(() => jda.shutdownNow()))
}

final class ReadyListener extends ListenerAdapter {
    override def onReady(event: ReadyEvent): Unit = {
        val readyTime = Instant.now()
        val timeDiff = Duration.between(now, readyTime).toMillis() / 1000.0
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val formatted = dateFormat.format(Date.from(readyTime))

        println()
        println(s"Started up in $timeDiff seconds on $formatted")
        println(s"Logged in as:")
        println(event.getJDA().getSelfUser().getName())
        println(event.getJDA().getSelfUser().getId())
        println("------------------")
    }

}

final class MsgListener extends ListenerAdapter {
    override def onMessageReceived(event: MessageReceivedEvent): Unit = {
        if event.getAuthor().isBot() then return
        val content = event.getMessage().getContentRaw().split(" ").toSeq

        if !content(0).startsWith(prefix) then return

        content(0).toLowerCase().stripPrefix(prefix) match {
            case "ping" => event.getChannel().sendMessage("pong!").queue()
            case "pfp" => {
                val id = event
                    .getMessage()
                    .getContentRaw()
                    .split(" ")
                    .lift(1)
                    .getOrElse(event.getAuthor().getId())

                val target = event.getJDA().getUserById(id)

                val url = target.getAvatarUrl() + "?size=4096"
                val embed = EmbedBuilder()
                    .appendDescription("*Here's your pfp!*")
                    .setImage(url)
                    .setColor(0xce3a9b)
                    .build()

                event.getChannel().sendMessageEmbeds(embed).queue()
            }
            case _ => ()
        }
    }
}
