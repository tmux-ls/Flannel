package gg.compile.basics.profile.handler

import com.google.gson.JsonObject
import gg.compile.basics.core.profile.CoreProfile
import gg.compile.basics.profile.ProfileService
import gg.compile.basics.services.SyncHandler
import java.util.*

class ProfileDeleteHandler(private val profileService: ProfileService) : SyncHandler {

    override fun incoming(channel: String?, `object`: JsonObject?) {
        if (channel == "profile-delete" && `object` != null) {
            val profileUuid = UUID.fromString(`object`["uuid"].asString)
            val profile: CoreProfile? = profileService.find(profileUuid)

            if (profile != null) {
                profileService.deleteProfile(profile)
            }
        }
    }

    override val channel: String
        get() = "profile-delete"
}
