package gg.compile.basics.services

import java.util.*
import java.util.function.Consumer

class ServiceHandler(vararg services: Service) {
    private val services: MutableList<Service?> = ArrayList()

    init {
        Arrays.stream(services).forEach { service: Service? ->
            this.register(service)
        }
        instance = this
    }

    fun <T : Service?> register(service: T?) {
        requireNotNull(service) { "Provided service is null @ " + System.currentTimeMillis() }
        services.add(service)
    }

    fun <T : Service?> find(clazz: Class<out T>): T {
        return clazz.cast(
            services.stream()
                .filter { current: Service? ->
                    current!!.javaClass == clazz || current.javaClass.isAssignableFrom(clazz)
                }
                .findFirst().orElse(null)
        )
    }

    fun loadAll() {
        services.forEach(Consumer { obj: Service? -> obj!!.load() })
    }

    fun unloadAll() {
        services.forEach(Consumer { obj: Service? -> obj!!.unload() })
    }

    companion object {
        private lateinit var instance: ServiceHandler

        @JvmStatic
        fun getInstance(): ServiceHandler {
            return instance
        }
    }
}