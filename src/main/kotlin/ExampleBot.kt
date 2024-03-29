import kotlinx.coroutines.runBlocking
import net.ayataka.kordis.DiscordClient
import net.ayataka.kordis.Kordis
import net.ayataka.kordis.event.EventHandler
import net.ayataka.kordis.event.events.message.MessageReceiveEvent
import java.awt.Color

fun main(args: Array<String>) = runBlocking {
    ExampleBot().run()
}

class ExampleBot {
    lateinit var client: DiscordClient

    suspend fun run() {
        client = Kordis.create {
            token = "NjA0NDU5NTI4OTc2ODU5MTM3.XTzSTQ.Y5vsaYzdjPrBUA6tgsoUgI79qbY"
            addListener(this@ExampleBot)
        }
    }

    @EventHandler
    suspend fun onMessageReceived(event: MessageReceiveEvent) {
        //送信されたメッセージの情報を表示する
        val member = event.message.member ?: return
        val channel = event.message.serverChannel ?: return
        val message = event.message

        //自分自身、Botユーザーは無視する
        if (member.bot) return



        if (message.content == "hello") {
            channel.send("hello, ${message.author?.name ?: "ななし"}")
        } else {
            channel.send {
                embed {
                    //"役職メンバーの表示"がオンの役職の中で最も優先度が高い色
                    color = member.roles.filter { it.hoist }.maxBy { it.position }?.color ?: Color(0, 0, 0)
                    //MemberName#0000 と横にアイコンを表示
                    author("${member.name}#${member.discriminator}", iconUrl = member.avatar?.url ?: "")
                    //メッセージの内容、1024文字より多い場合は表記不可なので切り捨てて"..."を最後につける
                    description = if (message.content.length > 1024) "${message.content.substring(
                        0,
                        1021
                    )}..." else message.content
                    //メッセージの情報
                    field(
                        "Message Info",
                        "**Message ID** ${message.id}\n" +
                                "**Member ID** ${member.id}"
                    )
                    //何時何分、何曜日　など時刻の表現を追加
                    timestamp = message.timestamp
                }
            }
        }
    }
}