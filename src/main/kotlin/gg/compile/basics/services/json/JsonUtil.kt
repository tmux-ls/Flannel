package gg.compile.basics.services.json

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bson.Document
object JsonUtils {

    private val parser: JsonParser = JsonParser()

    fun getParser(): JsonParser {
        return parser
    }
    /**
     * Get a [JsonObject] from a [String]
     *
     * @param string the string to get the json object from
     * @return the parsed JsonObject
     */
    fun getJsonFromString(string: String): JsonObject {
        if (string.isEmpty()) {
            throw IllegalArgumentException("String could not be parsed because it's empty")
        }

        return parser.parse(string).asJsonObject
    }

    /**
     * Get a [JsonObject] from a [Map<String, String>]
     *
     * @param map the map to get the json object from
     * @return the json object
     */
    fun getFromMap(map: Map<String, String>): JsonObject {
        return JsonObject().apply {
            map.forEach { (key, value) ->
                addProperty(key, value)
            }
        }
    }

    /**
     * Get a [JsonObject] from a [Document]
     *
     * @param document the document to get the json object from
     * @return the json object
     */
    fun getFromDocument(document: Document?): JsonObject? {
        return document?.let {
            JsonObject().apply {
                document.forEach { (key, value) ->
                    if (value is String) {
                        addProperty(key, value)
                    }
                }
            }
        }
    }

    /**
     * Get a [Document] from a [JsonObject]
     *
     * @param object the object to get the document from
     * @return the document
     */
    fun toDocument(jsonObject: JsonObject): Document {
        return Document().apply {
            jsonObject.entrySet().forEach { entry ->
                put(entry.key, entry.value.asString)
            }
        }
    }

    /**
     * Get a [Map] from a [JsonObject]
     *
     * @param object the [JsonObject] to get the [Map] from
     * @return the [Map]
     */
    fun getMap(jsonObject: JsonObject): Map<String, JsonElement> {
        return jsonObject.entrySet().associate { (key, value) ->
            key to value
        }
    }
}