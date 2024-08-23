package gg.compile.basics.services

import com.google.gson.JsonObject


interface SyncHandler {
    /**
     * Handle an incoming [JsonObject] message
     *
     * @param channel the channel where the object was sent in
     * @param object  the object which was sent
     */
    fun incoming(channel: String?, `object`: JsonObject?)

    /**
     * The handleable channels which this handler will handle
     *
     * @return the channels
     */
    val channel: String?
}