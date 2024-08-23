package app.revanced.bilibili.patches.okhttp.hooks

import android.content.pm.PackageManager
import app.revanced.bilibili.integrations.BuildConfig
import app.revanced.bilibili.patches.okhttp.ApiHook
import app.revanced.bilibili.settings.Settings
import app.revanced.bilibili.utils.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

/**
 * versionSum format: "$version $versionCode $patchVersion $patchVersionCode $sn $size $md5 publishTime"
 *
 * eg. "7.66.0 7660300 1.17 10170 14056308 135819602 2c2e2008ecb46c927981078811402151 1709975253"
 */
class BUpgradeInfo(
    versionSum: String,
    val url: String,
    val changelog: String,
) {
    private val versionInfo = versionSum.split(' ')
    val version: String get() = versionInfo[0]
    val versionCode: Long get() = versionInfo[1].toLong()
    val patchVersion: String get() = versionInfo[2]
    val patchVersionCode: Int get() = versionInfo[3].toInt()
    val sn: Long get() = versionInfo[4].toLong()
    val size: Long get() = versionInfo[5].toLong()
    val md5: String get() = versionInfo[6]
    val publishTime: Long get() = versionInfo[7].toLong()
}

object Upgrade : ApiHook() {
    private val upgradeCheckApi: String by lazy {
        UpdateUrlReplacer(Utils.getContext()).getUpdateUrl()
    }

    private val changelogRegex = Regex("""版本信息：(.*?)\n(.*)""", RegexOption.DOT_MATCHES_ALL)
    private var fromSelf = true
    private var isPrebuilt = true
    private var isOsArchArm64 = true

    fun customUpdate(): Boolean {
        return fromSelf && isPrebuilt && isOsArchArm64
    }

    override fun shouldHook(url: String, status: Int): Boolean {
        return (Settings.BlockUpdate() || customUpdate()) && url.contains("/x/v2/version/fawkes/upgrade")
    }

    override fun hook(url: String, status: Int, request: String, response: String): String {
        return if (customUpdate()) {
            checkUpgrade()?.toString() ?: """{"code":-1,"message":"检查更新失败，请稍后再试/(ㄒoㄒ)/~~"}"""
        } else {
            response
        }.also { fromSelf = true }
    }

    private fun checkUpgrade(): JSONObject? {
        var page = 1
        var result: JSONObject? = null
        while (result == null) {
            result = pagingCheck(page++)
        }
        return result
    }

    private fun pagingCheck(page: Int): JSONObject? {
        val context = Utils.getContext()
        val sn = context.getBuildSn()
        val patchVersion = BuildConfig.VERSION_NAME
        val patchVersionCode = BuildConfig.VERSION_CODE
        val pageUrl = "$upgradeCheckApi?page=$page&per_page=100"
        val response = JSONArray(URL(pageUrl).readText())
        val mobiApp = Utils.getMobiApp()

        for (data in response) {
            val tagName = data.optString("tag_name")
            if (!tagName.startsWith("$mobiApp-")) continue

            val (versionSum, changelog, url) = extractUpdateInfo(data) ?: continue
            val upgradeInfo = BUpgradeInfo(versionSum, url, changelog)

            if (shouldUpgrade(sn, patchVersionCode, upgradeInfo)) {
                return createUpgradeResponse(upgradeInfo, patchVersion)
            }
        }
        return createNoUpgradeResponse()
    }

    private fun extractUpdateInfo(data: JSONObject): Triple<String, String, String>? {
        val body = data.optString("body").replace("\r\n", "\n")
        val values = changelogRegex.matchEntire(body)?.groupValues ?: return null
        val url = data.optJSONArray("assets")?.optJSONObject(0)?.optString("browser_download_url") ?: return null
        return Triple(values[1], values[2].trim(), url)
    }

    private fun shouldUpgrade(currentSn: Long, currentPatchVersionCode: Int, upgradeInfo: BUpgradeInfo): Boolean {
        return currentSn < upgradeInfo.sn || (currentSn == upgradeInfo.sn && currentPatchVersionCode < upgradeInfo.patchVersionCode)
    }

    private fun createUpgradeResponse(info: BUpgradeInfo, currentPatchVersion: String): JSONObject {
        val changelogBuilder = StringBuilder(info.changelog)

        val appVersionChange = if (currentPatchVersion != info.patchVersion) {
            "漫游X版本：$currentPatchVersion --> ${info.patchVersion}"
        } else ""

        if (appVersionChange.isNotEmpty()) {
            changelogBuilder.append("\n\n").append(appVersionChange)
        }

        return mapOf(
            "code" to 0,
            "message" to "0",
            "ttl" to 1,
            "data" to mapOf(
                "title" to "新版 Bilix",
                "content" to changelogBuilder.toString(),
                "version" to info.version,
                "version_code" to info.versionCode,
                "url" to speedupGhUrl(info.url),
                "size" to info.size,
                "md5" to info.md5,
                "silent" to 0,
                "upgrade_type" to 1,
                "cycle" to 1,
                "policy" to 0,
                "policy_url" to "",
                "ptime" to info.publishTime
            )
        ).toJSONObject().also {
            Logger.debug { "Upgrade check result: $it" }
        }
    }

    private fun createNoUpgradeResponse(): JSONObject {
        return mapOf("code" to -1, "message" to "未发现新版 Bilix！").toJSONObject()
    }

    private fun Context.getBuildSn(): Long {
        return this.packageManager.getApplicationInfo(this.packageName, PackageManager.GET_META_DATA)
            .metaData.getInt("BUILD_SN").toLong()
    }
}