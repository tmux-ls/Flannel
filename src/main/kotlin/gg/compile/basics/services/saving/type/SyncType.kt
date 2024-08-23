package gg.compile.basics.services.saving.type

import com.google.gson.JsonObject
import gg.compile.basics.services.SyncHandler

interface SyncType {
    fun publish(channel: String?, `object`: JsonObject?)

    fun incoming(message: String?)

    fun registerHandler(handler: SyncHandler?)

    val syncHandlers: List<Any?>?
}