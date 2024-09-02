package gg.compile.basics.services.saving.type

import com.google.gson.JsonObject
import gg.compile.basics.services.SyncHandler
import gg.compile.basics.services.json.JsonAppender

interface SyncType {
    fun publish(channel: String?, `object`: JsonObject)

    fun incoming(message: String?)

    fun registerHandler(handler: SyncHandler?)
    fun publish(channel: String, toJson: JsonAppender)

    val syncHandlers: List<Any?>?
}