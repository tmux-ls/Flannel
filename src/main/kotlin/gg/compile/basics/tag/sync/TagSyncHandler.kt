package gg.compile.basics.tag.sync

import com.google.gson.JsonObject
import gg.compile.basics.services.SyncHandler
import gg.compile.basics.tag.service.TagService
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*

class TagSyncHandler(private val tagService: TagService) : SyncHandler {

    override fun incoming(channel: String?, `object`: JsonObject?) {
        if (channel == "tags" && `object` != null) {
            val tagUuid = UUID.fromString(`object`.get("uuid").asString)
            val tagName = `object`.get("name").asString
            val tag = tagService.findOrMake(tagUuid, tagName) ?: return

            `object`.get("name")?.let { tag.setName(it.asString) }
            `object`.get("displayName")?.let { tag.setDisplayName(it.asString) }
            `object`.get("prefix")?.let { tag.setPrefix(it.asString) }
            `object`.get("permission")?.let { tag.setPermission(it.asString) }
            `object`.get("icon")?.let {
                val material = Material.valueOf(it.asString)
                tag.setIcon(ItemStack(material))
                // Convert ItemStack to String representation of Material enum
                `object`.addProperty("icon", material.name)
            }
        }
    }

    override val channel: String
        get() = "tags"
}
