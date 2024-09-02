import com.google.gson.JsonObject
import gg.compile.basics.services.SyncHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor

class StringSyncHandler : SyncHandler {

    override val channel: String
        get() = "message"

    override fun incoming(channel: String?, `object`: JsonObject?) {
        if (`object` != null) {
            val message = `object`.get("message")?.asString ?: return
            val permission = `object`.get("permission")?.asString

            if (permission != null) {
                Bukkit.getOnlinePlayers()
                    .filter { player -> player.hasPermission(permission) }
                    .forEach { player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)) }
            } else {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message))
            }
        }
    }
}
