package gg.compile.basics.services.json

import com.google.gson.JsonObject
import org.bukkit.ChatColor

class JsonAppender @JvmOverloads constructor(`object`: JsonObject? = null) {
    var `object`: JsonObject = `object` ?: JsonObject()

    @Throws(IllegalArgumentException::class)
    fun <V> append(key: String?, value: V): JsonAppender {
        if (value is Number) {
            `object`.addProperty(key, value as Number)
        } else if (value is Boolean) {
            `object`.addProperty(key, value as Boolean)
        } else if (value is Char) {
            `object`.addProperty(key, value as Char)
        } else if (value is String) {
            `object`.addProperty(key, value as String)
        } else if (value is ChatColor) {
            `object`.addProperty(key, (value as ChatColor).name)
        } else {
            throw IllegalArgumentException("Unsupported value type: ")
        }

        return this
    }

    fun get(): JsonObject {
        return this.`object`
    }
}