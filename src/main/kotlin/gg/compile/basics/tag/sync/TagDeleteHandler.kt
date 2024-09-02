package gg.compile.basics.tag.sync

import com.google.gson.JsonObject
import gg.compile.basics.services.SyncHandler
import gg.compile.basics.tag.service.TagService
import java.util.*

class TagDeleteHandler(private val tagService: TagService) : SyncHandler {

    override fun incoming(channel: String?, `object`: JsonObject?) {
        if (channel == "tag-delete" && `object` != null) {
            val tagUuid = UUID.fromString(`object`.get("uuid").asString)
            val tag = tagService.find(tagUuid)

            tag?.let {
                tagService.deleteTag(it)
            }
        }
    }

    override val channel: String
        get() = "tag-delete"
}
