import net.dv8tion.jda.api.{JDABuilder, JDA}
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.entities.Message

val dotenv = Dotenv.load()
val now = java.time.LocalDateTime.now()

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
        val readyTime = java.time.LocalDateTime.now()
        val timeDiff = java.time.Duration.between(now, readyTime)
        println()
        println(s"Ready in ${timeDiff.toMillis()}ms")
        println(s"Logged in as ${event.getJDA().getSelfUser().getAsTag()}")
    }

}

final class MsgListener extends ListenerAdapter {
    override def onMessageReceived(event: MessageReceivedEvent): Unit = {
        if event.getMessage().getContentRaw().equals("ping") then {
            event.getChannel().sendMessage("pong!").queue()
        } else if event.getMessage().getContentRaw().startsWith("pfp") then {
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
    }
}
