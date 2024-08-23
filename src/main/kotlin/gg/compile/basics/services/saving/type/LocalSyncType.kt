package gg.compile.basics.services.saving.type

import com.google.gson.JsonObject
import gg.compile.basics.services.SyncHandler
import gg.compile.basics.services.json.JsonAppender
import gg.compile.basics.services.json.JsonUtils

class LocalSyncType : SyncType {

    private val syncHandler: MutableList<SyncHandler> = mutableListOf()

    override fun publish(channel: String?, `object`: JsonObject?) {
        this.incoming(JsonAppender(`object`).append("channel", channel).toString())
    }

    override fun incoming(message: String?) {
        val jsonObject = message?.let { JsonUtils.getJsonFromString(it) }
        val channel = jsonObject?.get("channel")?.asString ?: return
        syncHandlers?.filterIsInstance<SyncHandler>()?.filter { it.channel == channel }?.forEach { handler ->
            handler.incoming(channel, jsonObject)
        }
    }

    override fun registerHandler(handler: SyncHandler?) {
    }

    override val syncHandlers: List<Any?>?
        get() = TODO("Not yet implemented")
}