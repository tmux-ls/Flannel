package gg.compile.basics.services.saving

import gg.compile.basics.services.Service
import gg.compile.basics.services.saving.type.SavingType
import gg.compile.basics.services.saving.type.SyncType

class SavingService(savingType: SavingType, syncType: SyncType) : Service {
    private val savingType: SavingType = savingType
    private val syncType: SyncType = syncType

    override fun load() {
        savingType.load()
    }

    override fun unload() {
        savingType.unload()
    }

    fun getSavingType(): SavingType {
        return this.savingType
    }

    fun getSyncType(): SyncType {
        return this.syncType
    }
}