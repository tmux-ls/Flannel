package gg.compile.basics.commands

import gg.compile.basics.profile.ProfileService
import gg.compile.basics.services.ServiceHandler
import gg.compile.basics.tag.Tag
import gg.compile.basics.tag.service.TagService
import gg.compile.basics.util.CC
import gg.pots.basics.bukkit.command.tag.menu.TagsMenu
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.UUID

class TagCommands : CommandExecutor {

    private val profileService: ProfileService = ServiceHandler.getInstance().find(ProfileService::class.java)
    private val tagService: TagService = ServiceHandler.getInstance().find(TagService::class.java)

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(CC.translate("&cOnly players can use this command."))
            return true
        }

        val player = sender as Player

        when (label.toLowerCase()) {
            "tags" -> {
                TagsMenu(player).open()
                return true
            }
            "tag" -> {
                if (args.isEmpty()) {
                    // Show help
                    return true
                }
                handleSubcommands(player, args)
                return true
            }
            "cleartag" -> {
                clearTag(player)
                return true
            }
            else -> return false
        }
    }

    private fun handleSubcommands(player: Player, args: Array<String>) {
        val subcommand = args[0].toLowerCase()
        when (subcommand) {
            "create" -> {
                if (args.size < 2) {
                    player.sendMessage(CC.translate("&cUsage: /tag create <name>"))
                    return
                }
                createTag(player, args[1])
            }
            "delete" -> {
                if (args.size < 2) {
                    player.sendMessage(CC.translate("&cUsage: /tag delete <name>"))
                    return
                }
                deleteTag(player, args[1])
            }
            "setdisplayname" -> {
                if (args.size < 3) {
                    player.sendMessage(CC.translate("&cUsage: /tag setdisplayname <name> <displayName>"))
                    return
                }
                setDisplayName(player, args[1], args[2])
            }
            "setprefix" -> {
                if (args.size < 3) {
                    player.sendMessage(CC.translate("&cUsage: /tag setprefix <name> <prefix>"))
                    return
                }
                setPrefix(player, args[1], args[2])
            }
            "seticon" -> {
                if (args.size < 2) {
                    player.sendMessage(CC.translate("&cUsage: /tag seticon <name>"))
                    return
                }
                setIcon(player, args[1])
            }
            "setpermission" -> {
                if (args.size < 3) {
                    player.sendMessage(CC.translate("&cUsage: /tag setpermission <name> <permission>"))
                    return
                }
                setPermission(player, args[1], args[2])
            }
            "info" -> {
                if (args.size < 2) {
                    player.sendMessage(CC.translate("&cUsage: /tag info <name>"))
                    return
                }
                information(player, args[1])
            }
            "list" -> list(player)
            else -> player.sendMessage(CC.translate("&cUnknown subcommand."))
        }
    }

    private fun createTag(player: Player, name: String) {
        if (tagService.find(name) != null) {
            player.sendMessage(CC.translate("&cA tag with that name already exists."))
            return
        }

        val tag = Tag(UUID.randomUUID(), name)
        tagService.syncTag(tag)

        player.sendMessage(CC.translate("&fSuccessfully created a tag with the name &a$name&f."))
    }

    private fun deleteTag(player: Player, name: String) {
        val tag = tagService.find(name)
        if (tag == null) {
            player.sendMessage(CC.translate("&cTag not found."))
            return
        }

        tagService.deleteTag(tag)
        player.sendMessage(CC.translate("&fSuccessfully deleted the tag with the name &a${tag.getDisplayName()}&f."))
    }

    private fun setDisplayName(player: Player, name: String, displayName: String) {
        val tag = tagService.find(name)
        if (tag == null) {
            player.sendMessage(CC.translate("&cTag not found."))
            return
        }

        tag.setDisplayName(displayName)
        tagService.syncTag(tag)

        player.sendMessage(CC.translate("&fSuccessfully set the display name of the tag &a${tag.getName()}&f to &a$displayName&f."))
    }

    private fun setPrefix(player: Player, name: String, prefix: String) {
        val tag = tagService.find(name)
        if (tag == null) {
            player.sendMessage(CC.translate("&cTag not found."))
            return
        }

        tag.setPrefix(prefix)
        tagService.syncTag(tag)

        player.sendMessage(CC.translate("&fSuccessfully set the prefix of the tag &a${tag.getDisplayName()}&f to &a$prefix&f."))
    }

    private fun setIcon(player: Player, name: String) {
        val tag = tagService.find(name)
        if (tag == null) {
            player.sendMessage(CC.translate("&cTag not found."))
            return
        }

        if (player.itemInHand == null) {
            player.sendMessage(CC.translate("&cYou must be holding an item in your hand."))
            return
        }

        tag.setIcon(player.itemInHand)
        tagService.syncTag(tag)
        player.sendMessage(CC.translate("&fSuccessfully set the icon of the tag &a${tag.getDisplayName()}&f."))
    }

    private fun setPermission(player: Player, name: String, permission: String) {
        val tag = tagService.find(name)
        if (tag == null) {
            player.sendMessage(CC.translate("&cTag not found."))
            return
        }

        tag.setPermission(permission)
        tagService.syncTag(tag)

        player.sendMessage(CC.translate("&fSuccessfully set the permission of the tag &a${tag.getDisplayName()}&f to &a$permission&f."))
    }

    private fun information(player: Player, name: String) {
        val tag = tagService.find(name)
        if (tag == null) {
            player.sendMessage(CC.translate("&cTag not found."))
            return
        }

        player.sendMessage(CC.translate("&7&m----------------------------------------"))
        player.sendMessage(CC.translate("&eTag Information for ${tag.getDisplayName()}"))
        player.sendMessage("")
        player.sendMessage(CC.translate("&fName: &a${tag.getName()}"))
        player.sendMessage(CC.translate("&fPrefix: &a${tag.getPrefix()}"))
        player.sendMessage(CC.translate("&fPermission: &a${tag.getPermission()}"))
        player.sendMessage(CC.translate("&7&m----------------------------------------"))
    }

    private fun list(player: Player) {
        tagService.stream()
            .map { tag -> CC.translate("&f- &a${tag?.getName()} &7(&a${tag?.getDisplayName()}&7)") }
            .forEach { message -> player.sendMessage(message) }
    }

    private fun clearTag(player: Player) {
        val coreProfile = profileService.find(player.name)

        if (coreProfile != null) {
            if (coreProfile.getActiveTag() == null) {
                player.sendMessage(CC.translate("&cYou do not have an active tag."))
            } else {
                coreProfile.setActiveTag(null)
                player.sendMessage(CC.translate("&eSuccessfully cleared your active tag."))
            }
        }
    }

    companion object {
        fun registerCommands() {
            val tagCommands = TagCommands()
            Bukkit.getPluginCommand("tags")?.setExecutor(tagCommands)
            Bukkit.getPluginCommand("tag")?.setExecutor(tagCommands)
            Bukkit.getPluginCommand("cleartag")?.setExecutor(tagCommands)
        }
    }
}