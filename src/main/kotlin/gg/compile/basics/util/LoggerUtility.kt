package gg.compile.basics.util

import org.bukkit.Bukkit

object LoggerUtility {
    fun sendMessage(message: String?) {
        Bukkit.getConsoleSender().sendMessage(message?.let { CC.translate(it) })
    }
}
