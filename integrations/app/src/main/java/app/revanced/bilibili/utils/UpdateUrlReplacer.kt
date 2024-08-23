package app.revanced.bilibili.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class UpdateUrlReplacer(private val context: Context) {

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    // 获取当前选中的更新源名称
    fun getCurrentUpdateSource(): String? {
        return preferences.getString("custom_update_source", null)
    }

    // 设置更新源 URL
    fun setUpdateSource(url: String) {
        preferences.edit().putString("custom_update_source", url).apply()
    }

    // 获取更新源 URL 根据当前选中的更新源
    fun getUpdateUrl(): String {
        // 获取当前选中的更新源名称，若为空则返回默认URL
        val selectedSource = getCurrentUpdateSource()

        // 显示日志，方便调试
        Logger.debug { "Selected Update Source: $selectedSource" }

        return selectedSource?.let {
            // 使用 when 语句，根据选中的更新源返回对应的 URL
            when (it) {
                "Bilix-Release" -> getUpdateUrlFromPreferences("biliroaming_release_host")
                "Bilix-Nightly" -> getUpdateUrlFromPreferences("biliroaming_nightly_host")
                "BiliRoamingX/BiliRoamingX-Prebuilds" -> getUpdateUrlFromPreferences("biliroaming_original_host")
                else -> {
                    Logger.warn { "Unknown update source selected: $it. Using default URL." }
                    getDefaultUpdateUrl()
                }
            }
        } ?: run {
            Logger.warn { "No update source selected. Using default URL." }
            getDefaultUpdateUrl()
        }
    }

    // 根据key获取更新源 URL，如果未设置则返回默认URL
    private fun getUpdateUrlFromPreferences(key: String): String {
        val url = preferences.getString(key, "") ?: ""
        if (url.isBlank()) {
            Logger.warn { "Update URL for key $key is blank. Using default URL." }
            return getDefaultUpdateUrl()
        }
        Logger.debug { "Update URL retrieved for key $key: $url" }
        return url
    }

    // 默认更新 URL
    private fun getDefaultUpdateUrl(): String {
        return "https://api.github.com/repos/sti-233/Bilix-PreBuilds/releases"
    }
}