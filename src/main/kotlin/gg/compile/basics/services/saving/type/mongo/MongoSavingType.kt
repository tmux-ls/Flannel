package gg.compile.basics.services.saving.type.mongo

import com.google.gson.JsonObject
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import gg.compile.basics.services.json.JsonAppender
import gg.compile.basics.services.json.JsonUtils
import gg.compile.basics.services.saving.type.SavingType
import org.bson.Document
import java.util.*
import java.util.concurrent.CompletableFuture

class MongoSavingType(hostname: String, port: Int, database: String) : SavingType {
    private val database: MongoDatabase = MongoClient(hostname, port).getDatabase(database)

    override fun getJsonObjects(collection: String?): List<JsonObject?>? {
        if (collection == null) return null

        val objects: MutableList<JsonObject?> = mutableListOf()
        database.getCollection(collection).find()
            .forEach { document ->
                JsonUtils.getFromDocument(document)?.let { objects.add(it) }
            }

        return objects
    }

    override fun getJsonObject(uuid: UUID?, collection: String?): Optional<JsonObject?> {
        if (uuid == null || collection == null) return Optional.empty()

        val document = database.getCollection(collection).find(Filters.eq("uuid", uuid.toString())).first()
        val jsonObject: JsonObject? = JsonUtils.getFromDocument(document)

        return Optional.ofNullable(jsonObject)
    }

    override fun saveJsonObject(`object`: JsonObject?, collection: String?): Boolean {
        if (`object` == null || collection == null || !`object`.has("uuid")) {
            return false
        }

        val document: Document = JsonUtils.toDocument(`object`)
        val uuid = UUID.fromString(document.getString("uuid"))

        return database.getCollection(collection).replaceOne(
            Filters.eq("uuid", uuid.toString()),
            document,
            ReplaceOptions().upsert(true)
        ).wasAcknowledged()
    }

    override fun saveJsonObjectAsync(`object`: JsonObject, collection: String?) {
        if (collection == null) return

        CompletableFuture.runAsync {
            if (!`object`.has("uuid")) {
                return@runAsync
            }
            val document: Document = JsonUtils.toDocument(`object`)
            val uuid = UUID.fromString(document.getString("uuid"))
            database.getCollection(collection).replaceOne(
                Filters.eq("uuid", uuid.toString()),
                document,
                ReplaceOptions().upsert(true)
            )
        }.exceptionally { exception ->
            exception.printStackTrace()
            null
        }
    }

    override fun deleteFromCollection(uuid: UUID?, collection: String?): CompletableFuture<Boolean?>? {
        if (uuid == null || collection == null) {
            return CompletableFuture.completedFuture(null)
        }

        return CompletableFuture.supplyAsync {
            val result = database.getCollection(collection).deleteOne(Filters.eq("uuid", uuid.toString()))
            result.wasAcknowledged()
        }
    }

    fun saveJsonObject(`object`: JsonObject, collectionName: String): Boolean {
        if (!`object`.has("uuid")) {
            return false
        }

        val document: Document = JsonUtils.toDocument(`object`)
        val uuid = UUID.fromString(document.getString("uuid"))

        return database.getCollection(collectionName).replaceOne(
            Filters.eq("uuid", uuid.toString()),
            document,
            ReplaceOptions().upsert(true)
        ).wasAcknowledged()
    }

    fun deleteFromCollection(uuid: UUID, collection: String): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val result =
                database.getCollection(collection).deleteOne(Filters.eq("uuid", uuid.toString()))
            result.wasAcknowledged()
        }
    }

    override fun CoreProfile() {
        // Implementation details for CoreProfile
    }
}