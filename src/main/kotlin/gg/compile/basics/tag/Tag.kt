package gg.compile.basics.tag

import com.google.gson.JsonObject
import gg.compile.basics.services.ServiceHandler
import gg.compile.basics.services.json.JsonAppender
import gg.compile.basics.tag.service.TagService
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

data class Tag(
    val uuid: UUID,
    var name: String,
    var displayName: String = name,
    var prefix: String = "",
    var permission: String = "",
    var icon: ItemStack = ItemStack(Material.ITEM_FRAME)
) {
    private val tagService: TagService = ServiceHandler.getInstance().find(TagService::class.java)

    init {
        tagService.createTag(this)
    }

    constructor(`object`: JsonObject) : this(
        UUID.fromString(`object`["uuid"].asString),
        `object`["name"].asString,
        `object`["displayName"].asString,
        `object`["prefix"].asString,
        `object`["permission"].asString,
        ItemStack(Material.valueOf(`object`["icon"].asString))
    )

    fun toJson(): JsonObject {
        return JsonAppender()
            .append("uuid", uuid.toString())
            .append("name", this.name)
            .append("displayName", this.displayName)
            .append("prefix", this.prefix)
            .append("permission", this.permission)
            .append("icon", icon.type.name).get()
    }


    fun getPrefix(): String {
        return ChatColor.translateAlternateColorCodes('&', prefix.replace("_", " "))
    }

    fun getName(): String {
        return this.name
    }

    fun getUuid(): UUID {
        return this.uuid
    }

    fun getDisplayName(): String {
        return ChatColor.translateAlternateColorCodes('&', displayName.replace("_", " "))
    }

    fun getPermission(): String {
        return this.permission
    }

    fun getIcon(): ItemStack {
        return this.icon
    }

    fun setPrefix(prefix: String) {
        this.prefix = prefix
    }

    fun setName(name: String) {
        this.name = name
    }

    fun setDisplayName(displayName: String) {
        this.displayName = displayName
    }

    fun setPermission(permission: String) {
        this.permission = permission
    }

    fun setIcon(icon: ItemStack) {
        this.icon = icon
    }

    fun delete() {
        tagService.deleteTag(this)
    }
}
