package gg.compile.basics.services

interface Service {
    fun load() {

    }

    fun unload() {}

    fun <T : Service?> register(serviceHandler: ServiceHandler): T {
        serviceHandler.register(this)
        return this as T
    }

    fun CoreProfile()
}