package gg.pots.basics.bukkit.command.tag.menu

import gg.compile.basics.profile.ProfileService
import gg.compile.basics.services.ServiceHandler
import gg.compile.basics.tag.Tag
import gg.compile.basics.tag.service.TagService
import gg.compile.basics.util.CC
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class TagsMenu(private val player: Player) {

    private val profileService: ProfileService = ServiceHandler.getInstance().find(ProfileService::class.java)
    private val tagService: TagService = ServiceHandler.getInstance().find(TagService::class.java)
    private val inventory: Inventory = Bukkit.createInventory(null, 27, "Tags")

    init {
        setupMenu()
    }

    private fun setupMenu() {
        val previousPageButton = ItemStack(Material.CARPET, 1, DyeColor.RED.woolData.toShort())
        val nextPageButton = ItemStack(Material.CARPET, 1, DyeColor.GREEN.woolData.toShort())

        val previousMeta = previousPageButton.itemMeta
        previousMeta.displayName = CC.translate("&cPrevious Page")
        previousPageButton.itemMeta = previousMeta

        val nextMeta = nextPageButton.itemMeta
        nextMeta.displayName = CC.translate("&aNext Page")
        nextPageButton.itemMeta = nextMeta

        inventory.setItem(18, previousPageButton)
        inventory.setItem(26, nextPageButton)

        for (i in tagService.tags.indices) {
            val tag = tagService.tags[i]
            val tagItem = tag?.icon?.clone()
            val meta = tagItem!!.itemMeta
            meta.displayName = tag.displayName
            meta.lore = getLore()
            tagItem.itemMeta = meta

            inventory.setItem(i, tagItem)
        }
    }

    fun open() {
        player.openInventory(inventory)
    }

    fun handleMenuClick(event: InventoryClickEvent) {
        if (event.clickedInventory != inventory) return

        event.isCancelled = true
        val clickedItem = event.currentItem ?: return

        val tag = tagService.tags.find { it?.icon!!.isSimilar(clickedItem) } ?: return
        val hasPermission = player.hasPermission(tag.permission)

        if (hasPermission) {
            val coreProfile = profileService.find(player.uniqueId)

            if (coreProfile == null) {
                player.sendMessage(CC.translate("&cYou do not have a profile."))
                return
            }

            coreProfile.setActiveTag(tag)
            player.sendMessage(arrayOf(
                CC.translate("&eYou have set your active tag to ${tag.displayName}"),
                CC.translate("&cIf you wish to remove your active tag, please use /cleartag.")
            ))
        } else {
            player.sendMessage(CC.translate("&cYou do not have permission to use this tag."))
        }
    }

    private fun getLore(): List<String> {
        val coreProfile = profileService.find(player.uniqueId)

        return listOf(
            CC.translate(""),
            CC.translate(coreProfile?.getActiveTag()?.let { "&aYour active tag is: &f${coreProfile.getActiveTag()}" }
                ?: "&cYou do not have an active tag.")
        )
    }
}