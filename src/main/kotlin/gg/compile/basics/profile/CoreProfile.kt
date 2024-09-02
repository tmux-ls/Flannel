package gg.compile.basics.core.profile

import com.google.gson.JsonObject
import gg.compile.basics.profile.ProfileService
import gg.compile.basics.services.ServiceHandler
import gg.compile.basics.services.json.JsonAppender
import gg.compile.basics.tag.Tag
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class CoreProfile {
    private val profileService: ProfileService = ServiceHandler.getInstance().find(ProfileService::class.java)

    private val uuid: UUID
    private var name: String?
    private var ipAddress: String? = null

    private var activeTag: Tag? = null

    fun getName(): String? {
        return name
    }

    fun getUuid(): UUID {
        return uuid
    }

    fun getActiveTag(): Tag? {
        return activeTag
    }

    fun setActiveTag(tag: Tag?) {
        this.activeTag = tag
    }

    constructor(uuid: UUID, name: String?) {
        val jsonObject: Optional<JsonObject?>? =
            profileService.getSavingService().getSavingType().getJsonObject(uuid, "profiles")

        this.uuid = uuid
        this.name = name

        if (jsonObject!!.isPresent) {
            CoreProfile(jsonObject.get())
        } else {
            profileService.registerProfile(this)
        }
    }

    constructor(`object`: JsonObject) {
        this.uuid = UUID.fromString(`object`.get("uuid").asString)
        this.name = `object`.get("name").asString

        profileService.registerProfile(this)
    }

    fun toJson(): JsonAppender {
        val `object`: JsonObject = JsonObject()

        if (this.ipAddress != null) {
            `object`.addProperty("ipAddress", this.ipAddress)
        }

        return JsonAppender(`object`)
            .append("uuid", uuid.toString())
            .append("name", this.name)
    }

    val player: Player
        get() {
            return Bukkit.getPlayer(this.uuid)
        }

    val isComplete: Boolean
        get() {
            return this.uuid != null && this.name!! >= 1.toString()
        }

    fun toJsdn(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("uuid", uuid.toString())
        jsonObject.addProperty("name", name)
        return jsonObject
    }
}