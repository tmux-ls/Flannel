package gg.compile.basics.services.saving.type.redis

import com.google.gson.JsonObject
import gg.compile.basics.services.SyncHandler
import gg.compile.basics.services.json.JsonAppender
import gg.compile.basics.services.json.JsonUtils
import gg.compile.basics.services.saving.type.SyncType
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

abstract class RedisSyncType(hostname: String?, port: Int) : SyncType {
    override val syncHandlers: MutableList<SyncHandler> = ArrayList<SyncHandler>()
    private val pool: JedisPool

    private var authenticate: Boolean
    private var username: String? = null
    private var password: String? = null

    /**
     * Constructor for making a new [RedisSyncType] object
     *
     * @param hostname the hostname of the redis server
     * @param port     the port of the redis server
     */
    init {
        this.pool = JedisPool(hostname, port)
        this.authenticate = false
        this.executeCommand(Consumer<Jedis?> { jedis: Jedis? ->
            jedis?.subscribe(
                pubSub, "core"
            )
        })
    }

    /**
     * Constructor for making a new [RedisSyncType] object with authentication
     *
     * @param hostname the hostname of the redis server
     * @param port     the port of the redis server
     * @param username the username to authenticate to the redis server with
     * @param password the password to authenticate to the redis server with
     */
    constructor(hostname: String?, port: Int, username: String?, password: String?) : this(hostname, port) {
        this.authenticate = true
        this.username = username
        this.password = password
    }

    /**
     * Execute a [Jedis] command through a [Consumer]
     *
     * @param consumer the consumer to execute
     */
    fun executeCommand(consumer: Consumer<Jedis?>) {
        CompletableFuture.runAsync({
            val jedis: Jedis? = pool.getResource()
            if (jedis != null) {
                if (this.authenticate) {
                    jedis.auth(this.password)
                }

                consumer.accept(jedis)
                jedis.close()
            }
        })
    }

    private val pubSub: JedisPubSub
        /**
         * Get a new [JedisPubSub] handler to handle the synchronization
         *
         * @return the pubsub handler
         */
        get() = object : JedisPubSub() {
            override fun onMessage(channel: String?, message: String?) {
                this@RedisSyncType.incoming(message)
            }
        }

    override fun publish(channel: String?, `object`: JsonObject?) {
        this.executeCommand(Consumer<Jedis?> { jedis: Jedis? ->
            jedis?.publish(
                "core",
                JsonAppender(`object`).append("channel", channel).get().toString()
            )
        })
    }

    override fun incoming(message: String?) {
        val `object`: JsonObject = JsonUtils.getParser().parse(message).getAsJsonObject()
        val channel: String = `object`.get("channel").asString

        syncHandlers.stream()
            .filter { handler: SyncHandler -> handler.getChannel().equals(channel) }
            .forEach { handler: SyncHandler -> handler.incoming(channel, `object`) }
    }

     fun registerHandler(handler: SyncHandler) {
        syncHandlers.add(handler)
    }
}