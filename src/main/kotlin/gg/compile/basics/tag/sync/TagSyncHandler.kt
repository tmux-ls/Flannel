package gg.compile.basics.tag.sync

import com.google.gson.JsonObject
import gg.compile.basics.services.SyncHandler
import gg.compile.basics.tag.service.TagService
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class TagSyncHandler : SyncHandler {
    private val tagService: TagService? = null

    fun incoming(channel: String, `object`: JsonObject) {
        if (channel == this.channel) {
            val tagUuid = UUID.fromString(`object`["uuid"].asString)
            val tagName = `object`["name"].asString
            val tag = tagService?.findOrMake(tagUuid, tagName) ?: return

            tag.setName(`object`["name"].asString)
            tag.setDisplayName(`object`["displayName"].asString)
            tag.setPrefix(`object`["prefix"].asString)
            tag.setPermission(`object`["permission"].asString)
            tag.setIcon(ItemStack(Material.valueOf(`object`["icon"].asString)))

            // Convert ItemStack to String representation of Material enum
            `object`.addProperty("icon", tag.getIcon().getType().name)
        }
    }

    override val channel: String
        get() = "tags"
}