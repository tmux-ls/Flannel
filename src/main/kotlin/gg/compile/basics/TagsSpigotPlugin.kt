package gg.compile.basics

import StringSyncHandler
import gg.compile.basics.profile.ProfileService
import gg.compile.basics.profile.handler.ProfileDeleteHandler
import gg.compile.basics.services.Service
import gg.compile.basics.services.ServiceHandler
import gg.compile.basics.services.saving.SavingService
import gg.compile.basics.services.saving.type.mongo.MongoSavingType
import gg.compile.basics.services.saving.type.redis.RedisSyncType
import gg.compile.basics.tag.service.TagService
import gg.compile.basics.tag.sync.TagDeleteHandler
import gg.compile.basics.tag.sync.TagSyncHandler
import gg.compile.basics.util.CC
import gg.compile.basics.util.ConfigFile
import gg.pots.basics.core.profile.sync.ProfileSyncHandler
import org.apache.commons.lang.Validate
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@Suppress("INACCESSIBLE_TYPE")
class CoreSpigotPlugin : JavaPlugin() {
    val handler: ServiceHandler = ServiceHandler()

    var messagesFile: ConfigFile? = null
    var broadcastsFile: ConfigFile? = null

    override fun onEnable() {
        instance = this
        val startUp = System.currentTimeMillis()

        this.saveDefaultConfig()
        this.messagesFile = ConfigFile(this, "messages.yml")
        this.broadcastsFile = ConfigFile(this, "broadcasts.yml")

        ServiceHandler.getInstance()

        val savingService: SavingService = SavingService(
            MongoSavingType("127.0.0.1", 27017, "core"),
            RedisSyncType("127.0.0.1", 6379)
        ).register(handler)

        val profileService: ProfileService = ProfileService(savingService).register(this.handler)
        val tagService: TagService = TagService(savingService).register(this.handler)

        savingService.getSyncType().registerHandler(ProfileSyncHandler(profileService))
        savingService.getSyncType().registerHandler(ProfileDeleteHandler(profileService))
        savingService.getSyncType().registerHandler(TagSyncHandler(tagService))
        savingService.getSyncType().registerHandler(TagDeleteHandler(tagService))
        savingService.getSyncType().registerHandler(StringSyncHandler())

//        commandHandler.registerTypeAdapter(Tag::class.java, TagTypeAdapter())

//        commandHandler.registerCommand(TagCommands())

        handler.loadAll()
        server.pluginManager.registerEvents(PlayerListener(), this)

    }

    override fun onDisable() {
        handler.unloadAll()

        Bukkit.getOnlinePlayers()
            .forEach { player: Player? -> player?.sendMessage(CC.translate("&cThe server is restarting.")) }

    }

    companion object {
        var instance: CoreSpigotPlugin? = null
    }
}