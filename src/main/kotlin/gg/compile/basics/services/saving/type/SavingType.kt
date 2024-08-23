package gg.compile.basics.services.saving.type

import com.google.gson.JsonObject
import gg.compile.basics.services.Service
import java.util.*
import java.util.concurrent.CompletableFuture

interface SavingType : Service {
    override fun load() {}

    override fun unload() {}

    fun getJsonObjects(collection: String?): List<JsonObject?>?

    fun getJsonObject(uuid: UUID?, collection: String?): Optional<JsonObject?>?

    fun saveJsonObject(`object`: JsonObject?, collection: String?): Boolean

    fun saveJsonObjectAsync(`object`: JsonObject?, collection: String?)

    fun deleteFromCollection(uuid: UUID?, collection: String?): CompletableFuture<Boolean?>?
}