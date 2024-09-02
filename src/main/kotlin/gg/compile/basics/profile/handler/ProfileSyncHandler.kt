package gg.pots.basics.core.profile.sync

import com.google.gson.JsonObject
import gg.compile.basics.core.profile.CoreProfile
import gg.compile.basics.profile.ProfileService
import gg.compile.basics.services.SyncHandler
import org.bukkit.ChatColor
import java.util.*

class ProfileSyncHandler(private val profileService: ProfileService) : SyncHandler {

    override fun incoming(channel: String?, `object`: JsonObject?) {
        if (channel == "profiles" && `object` != null) {
            val profileUuid = UUID.fromString(`object`.get("uuid").asString)
            val profileName = `object`.get("name").asString
            val profile = profileService.find(profileUuid) ?: CoreProfile(profileUuid, profileName)

            }
    }

    override val channel: String
        get() = "profiles"
}
