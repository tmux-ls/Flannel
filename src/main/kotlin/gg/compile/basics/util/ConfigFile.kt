package gg.compile.basics.util

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File


class ConfigFile(plugin: JavaPlugin, fileName: String?) {
    val configFile: File
    var configuration: YamlConfiguration
        private set

    /**
     * Constructor to create a new [ConfigFile]
     *
     * @param plugin The [JavaPlugin] instance.
     * @param fileName The name of the file to create.
     */
    init {
        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdir()
        }

        this.configFile = File(plugin.dataFolder, fileName)
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false)
        }

        this.configuration = YamlConfiguration.loadConfiguration(this.configFile)
    }

    /**
     * Save a [ConfigFile] to disk.
     */
    fun saveFile() {
        try {
            configuration.save(this.configFile)
        } catch (exception: Exception) {
            LoggerUtility.sendMessage("&7[&9&lConfig&7] &cAn error has occurred while saving the file, " + this.configuration)
        }
    }

    /**
     * Reload a [ConfigFile] from disk.
     */
    fun reloadFile(save: Boolean) {
        if (save) {
            this.saveFile()
        }

        this.configuration = YamlConfiguration.loadConfiguration(this.configFile)
    }

    /**
     * Get a [ConfigFile] from a [File].
     *
     * @param path The path of the file to get.
     * @param value The value of the file to get.
     */
    fun set(path: String?, value: Any?) {
        configuration[path] = value
        this.saveFile()
    }

    /**
     * Get a [ConfigFile] from a [File].
     *
     * @param path The path of the file to get.
     * @return The value of the file to get.
     */
    fun getString(path: String?): String? {
        if (configuration.contains(path)) {
            return configuration.getString(path)
        }

        return null
    }

    /**
     * Get a [ConfigFile] from a [File].
     *
     * @param path The path of the file to get.
     * @return The value of the file to get.
     */
    fun getStringList(path: String?): Array<String>? {
        if (configuration.contains(path)) {
            return configuration.getStringList(path).toTypedArray<String>()
        }

        return null
    }

    /**
     * Get a [ConfigFile] from a [File].
     *
     * @param path The path of the file to get.
     * @return The value of the file to get.
     */
    fun getInt(path: String?): Int {
        if (configuration.contains(path)) {
            return configuration.getInt(path)
        }

        return 0
    }

    /**
     * Get a [ConfigFile] from a [File].
     *
     * @param path The path of the file to get.
     * @return The value of the file to get.
     */
    fun getBoolean(path: String?): Boolean {
        if (configuration.contains(path)) {
            return configuration.getBoolean(path)
        }

        return false
    }

    /**
     * Get a [ConfigFile] from a [File].
     *
     * @param path The path of the file to get.
     * @return The value of the file to get.
     */
    fun getDouble(path: String?): Double {
        if (configuration.contains(path)) {
            return configuration.getDouble(path)
        }

        return 0.0
    }

    /**
     * Get a [ConfigFile] from a [File].
     *
     * @param path The path of the file to get.
     * @return The value of the file to get.
     */
    fun getLong(path: String?): Long {
        if (configuration.contains(path)) {
            return configuration.getLong(path)
        }

        return 0
    }
}