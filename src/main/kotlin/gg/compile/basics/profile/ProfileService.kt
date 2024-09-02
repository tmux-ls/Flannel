package gg.compile.basics.profile

import gg.compile.basics.core.profile.CoreProfile
import gg.compile.basics.services.Service
import gg.compile.basics.services.json.JsonAppender
import gg.compile.basics.services.saving.SavingService
import gg.compile.basics.tag.Tag
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream

class ProfileService : Service {
    private val profileMap: MutableMap<UUID, CoreProfile> = HashMap()

    private val savingService: SavingService? = null

    override fun load() {
        savingService?.getSavingType()?.getJsonObjects("profiles")?.forEach { CoreProfile() }
    }

    override fun unload() {
        profileMap.values.forEach(Consumer { profile: CoreProfile ->
            savingService
                ?.getSavingType()
                ?.saveJsonObjectAsync(profile.toJson(), "profiles")
        })

        profileMap.clear()
    }

    override fun CoreProfile() {
        TODO("Not yet implemented")
    }

    fun stream(): Stream<CoreProfile> {
        return profileMap.values.stream()
    }

    fun registerProfile(profile: CoreProfile) {
        requireNotNull(profile) { "The provided profile to register is null" }

        profileMap[profile.getUuid()] = profile
    }

    fun deleteProfile(profile: CoreProfile) {
        requireNotNull(profile) { "The provided profile to delete is null" }

        profileMap.remove(profile.getUuid())
        savingService?.getSyncType()
            ?.publish("profile-delete", JsonAppender().append("uuid", profile.getUuid().toString()).get())

        savingService?.getSavingType()?.deleteFromCollection(profile.getUuid(), "profiles")
    }

    fun saveProfileAsync(profile: CoreProfile) {
        requireNotNull(profile) { "The provided profile to save is null" }

        savingService?.getSavingType()?.saveJsonObjectAsync(profile.toJson(), "profiles")
    }

    fun syncProfile(profile: CoreProfile) {
        savingService?.getSyncType()?.publish("profiles", profile.toJson())
    }

    fun find(uuid: UUID): CoreProfile? {
        return profileMap[uuid]
    }

    fun find(name: String?): CoreProfile? {
        for (coreProfile in profileMap.values) {
            if (coreProfile.getName()?.equals(name, ignoreCase = true) == true) {
                return coreProfile
            }
        }
        return null
    }

    fun findOrElseMake(uuid: UUID, name: String?): CoreProfile {
        var coreProfile = profileMap[uuid]

        if (coreProfile == null) {
            coreProfile = CoreProfile(uuid, name)
            profileMap[uuid] = coreProfile
        }

        return coreProfile
    }

    fun getSavingService(): SavingService {
        return savingService!!
    }
}