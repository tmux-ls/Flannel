package gg.compile.basics.services.saving.type.mongo

import com.google.gson.JsonObject
import com.mongodb.Block
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import gg.compile.basics.services.json.JsonUtils
import gg.compile.basics.services.saving.type.SavingType
import org.bson.Document
import java.util.*
import java.util.concurrent.CompletableFuture

abstract class MongoSavingType(hostname: String, port: Int, database: String) : SavingType {
    private val database: MongoDatabase = MongoClient(hostname, port).getDatabase(database)

     fun getJsonObjects(collection: String): List<JsonObject> {
        val objects: MutableList<JsonObject> = ArrayList()
        database.getCollection(collection).find()
            .forEach(Block<Document?> { document: Document? -> JsonUtils.getFromDocument(document)
                ?.let { objects.add(it) } } as Block<in Document?>)

        return objects
    }

     fun getJsonObject(uuid: UUID, collection: String): Optional<JsonObject> {
        val `object`: JsonObject? = JsonUtils.getFromDocument(
            database.getCollection(collection).find(Filters.eq<String>("uuid", uuid.toString())).first()
        )

        return if (`object` == null) Optional.empty() else Optional.of(`object`)
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

     fun saveJsonObjectAsync(`object`: JsonObject, collection: String) {
        try {
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
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

     fun deleteFromCollection(uuid: UUID, collection: String): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            val result =
                database.getCollection(collection).deleteOne(Filters.eq("uuid", uuid.toString()))
            result.wasAcknowledged()
        }
    }
}