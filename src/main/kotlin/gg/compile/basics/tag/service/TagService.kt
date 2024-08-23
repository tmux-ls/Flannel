package gg.compile.basics.tag.service

import gg.compile.basics.services.Service
import gg.compile.basics.services.json.JsonAppender
import gg.compile.basics.services.saving.SavingService
import gg.compile.basics.tag.Tag
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.stream.Stream

class TagService(savingService: SavingService) : Service {
    private val tags: MutableList<Tag?> = ArrayList<Tag?>()
    private val savingService: SavingService = savingService

    override fun load() {
        savingService.getSavingType().getJsonObjects("tags")?.forEach { jsonObject ->
            tags.add(jsonObject?.let { Tag(it) })
        }
    }

    // make a unload method
    override fun unload() {
        this.stream().forEach(Consumer { tag ->
            savingService.getSavingType()
                .saveJsonObject(tag?.toJson(), "tags")
        })
    }

    /**
     * Create a new [Tag]
     *
     * @param tag the tag.
     */
    fun createTag(tag: Tag?) {
        requireNotNull(tag) { "The provided tag to create is null" }

        tags.add(tag)
    }

    /**
     * Delete a [Tag]
     *
     * @param tag the tag.
     */
    fun deleteTag(tag: Tag?) {
        requireNotNull(tag) { "The provided tag to delete is null" }

        tags.remove(tag)
        savingService.getSyncType().publish("tag-delete", JsonAppender().append("uuid", tag.getUuid().toString()).get())

        savingService.getSavingType().deleteFromCollection(tag.getUuid(), "tags")
    }

    /**
     * Sync a [Tag]
     *
     * @param tag the tag.
     */
    fun syncTag(tag: Tag) {
        savingService.getSyncType().publish("tags", tag.toJson())
    }

    /**
     * Stream the current [TagService] object.
     *
     * @return the stream of tags.
     */
    fun stream(): Stream<Tag?> {
        return tags.stream()
    }

    /**
     * Find a [Tag] by a [String]
     *
     * @param name the name.
     * @return the tag or null.
     */
    fun find(name: String?): Tag? {
        return tags.stream()
            .filter { current -> current?.getName()?.equals(name, ignoreCase = true) == true }
            .findFirst().orElse(null)

    }
    fun find(uuid: UUID?): Tag? {
        return tags.stream()
            .filter(Predicate<Tag?> { current: Tag? -> current?.getUuid()!!.equals(uuid) })
            .findFirst().orElse(null)
    }

    fun findOrMake(tagUuid: UUID?, tagName: String?): Tag? {
        return tags.stream()
            .filter { current -> current?.getUuid() == tagUuid }
            .findFirst()
            .orElseGet { Tag(tagUuid ?: UUID.randomUUID(), tagName ?: "defaultName") }
    }
}
