package gg.compile.basics.util

import net.md_5.bungee.api.ChatColor

class CC {
    companion object {
        @JvmStatic
        fun translate(text: String): String {
            return ChatColor.translateAlternateColorCodes('&', text)
        }
    }
}