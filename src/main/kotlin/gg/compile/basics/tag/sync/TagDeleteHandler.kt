package gg.compile.basics.tag.sync

import com.google.gson.JsonObject
import gg.compile.basics.services.SyncHandler
import gg.compile.basics.tag.Tag
import gg.compile.basics.tag.service.TagService
import java.util.*

abstract class TagDeleteHandler : SyncHandler {
    private val tagService: TagService? = null

    fun incoming(channel: String, `object`: JsonObject) {
        if (channel == "tag-delete") {
            val tagUuid = UUID.fromString(`object`["uuid"].asString)
            val tag: Tag? = tagService?.find(tagUuid)

            if (tag != null) {
                tagService?.deleteTag(tag)
            }
        }
    }

    override val channel: String
        get() = "tag-delete"
}
