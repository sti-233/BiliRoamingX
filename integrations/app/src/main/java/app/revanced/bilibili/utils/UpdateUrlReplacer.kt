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
        val selectedSource = getCurrentUpdateSource() ?: return getDefaultUpdateUrl()

        return when (selectedSource) {
            "Bilix-Release" -> preferences.getString("biliroaming_release_host", "") ?: getDefaultUpdateUrl()
            "Bilix-Nightly" -> preferences.getString("biliroaming_nightly_host", "") ?: getDefaultUpdateUrl()
            "BiliRoamingX/BiliRoamingX-Prebuilds" -> preferences.getString("biliroaming_original_host", "") ?: getDefaultUpdateUrl()
            else -> getDefaultUpdateUrl()
        }
    }

    // 默认更新 URL
    private fun getDefaultUpdateUrl(): String {
        return "https://api.github.com/repos/sti-233/Bilix-PreBuilds/releases"
    }
}
