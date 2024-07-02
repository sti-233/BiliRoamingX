// 定义包名，用于组织代码结构
package app.revanced.bilibili.patches.okhttp.hooks

// 导入所需的类和包
import android.content.pm.PackageManager  // 用于获取包信息
import app.revanced.bilibili.integrations.BuildConfig  // 导入项目的构建配置
import app.revanced.bilibili.patches.okhttp.ApiHook  // 导入 ApiHook 类
import app.revanced.bilibili.settings.Settings  // 导入设置相关的类
import app.revanced.bilibili.utils.*  // 导入项目中的工具类
import org.json.JSONArray  // 导入 JSON 数组类
import org.json.JSONObject  // 导入 JSON 对象类
import java.net.URL  // 导入 URL 类，用于处理网络地址

/**
 * versionSum format: "$version $versionCode $patchVersion $patchVersionCode $sn $size $md5 publishTime"
 *
 *版本发布格式:
 *[0]bilibili版本 eg. "8.3.0"
 *[1]bilibili版本代号 eg. "8030200"
 *[2]BiliromingX大版本 eg. "1.22.4"
 *[3]BiliromingX大服本号 eg. "10224"
 *[4]Patch后Bilibili包内AndroidManifest.xml第5546行下的BUILD_SN eg. "15279103"
 *[5]Patch后Bilibili包的字节大小 eg. "143162913"
 *[6]Patch后Bilibili包的md5 eg. "afaf4efb4582deb65f61f993ff55fd38"
 *[7]发布时间的时间戳 https://www.uutils.com/network/timestamp.htm eg. "1719894572"
 *
 * eg. "7.66.0 7660300 1.17 10170 14056308 135819602 2c2e2008ecb46c927981078811402151 1709975253"
 */
// 定义 BUpgradeInfo 类，表示升级信息
class BUpgradeInfo(
    versionSum: String,  // 接收一个包含版本信息的字符串
    val url: String,  // 升级包的下载链接
    val changelog: String,  // 升级的变更日志
) {
    // 将版本信息字符串按空格分割成多个部分并存储在 versionInfo 列表中
    private val versionInfo = versionSum.split(' ')

    // 从 versionInfo 列表中提取各个字段并定义只读属性
    val version get() = versionInfo[0]  // 版本号
    val versionCode get() = versionInfo[1].toLong()  // 版本代码
    val patchVersion get() = versionInfo[2]  // 补丁版本
    val patchVersionCode get() = versionInfo[3].toInt()  // 补丁版本代码
    val sn get() = versionInfo[4].toLong()  // 序列号
    val size get() = versionInfo[5].toLong()  // 升级包大小
    val md5 get() = versionInfo[6]  // 升级包的 MD5 校验值
    val publishTime get() = versionInfo[7].toLong()  // 发布时间
}

// 定义一个名为 Upgrade 的对象，继承自 ApiHook 类
object Upgrade : ApiHook() {
    // 常量，表示升级检查的API URL
    private const val UPGRADE_CHECK_API = "https://github.com/sti-233/Bilix-PreBuilds/releases"
    
    // 正则表达式，用于匹配版本信息
    private val changelogRegex = Regex("""版本信息：(.*?)\n(.*)""", RegexOption.DOT_MATCHES_ALL)
    
    // 变量，表示是否是自定义更新
    var fromSelf = true  // 将 fromSelf 设置为 true，以启用自定义更新
    var isOsArchArm64 = true
    var isPrebuilt = true

    // 方法，检查是否进行自定义更新
    fun customUpdate(fromSelf: Boolean = true): Boolean {  // 默认参数改为 true
        //返回 true，如果 fromSelf 为 true。否则，返回 Settings.CustomUpdate() 的结果
        //首先，计算 (fromSelf || Settings.CustomUpdate())：
        //如果 fromSelf 为 true，则 (fromSelf || Settings.CustomUpdate()) 为 true，且不会调用 Settings.CustomUpdate()
        //如果 fromSelf 为 false，则 (fromSelf || Settings.CustomUpdate()) 的值取决于 Settings.CustomUpdate() 的结果
        //然后，计算 (fromSelf || Settings.CustomUpdate()) 的结果与 isOsArchArm64 和 isPrebuilt 的逻辑与操作
        //如果 (fromSelf || Settings.CustomUpdate()) 为 false，则整个表达式为 false，不再计算 isOsArchArm64 和 isPrebuilt
        //如果 (fromSelf || Settings.CustomUpdate()) 为 true，则继续计算 isOsArchArm64 && isPrebuilt，并返回其结果
        //仅当 (fromSelf || Settings.CustomUpdate()) 为 true，且 isOsArchArm64 和 isPrebuilt 都为 true 时，返回 true
        //否则，返回 false。
        return (fromSelf) && isOsArchArm64 && isPrebuilt
    }

    // 重写 shouldHook 方法，判断是否需要进行 hook
    override fun shouldHook(url: String, status: Int): Boolean {
        return (customUpdate(fromSelf = fromSelf))
                && url.contains("/x/v2/version/fawkes/upgrade")
    }

    // 重写 hook 方法，处理 API 请求和响应
    override fun hook(url: String, status: Int, request: String, response: String): String {
        return if (customUpdate(fromSelf = fromSelf))
            // 尝试进行升级检查，如果失败，返回错误消息
            (runCatchingOrNull { checkUpgrade().toString() }
                ?: """{"code":-1,"message":"检查更新失败，请稍后再试/(ㄒoㄒ)/~~""")
                .also { fromSelf = true }
        // 如果设置为阻止更新，返回自定义的阻止更新消息
        //else if (Settings.BlockUpdate())
            //"""{"code":-1,"message":"哼，休想要我更新！<(￣︶￣)>"}"""
        // 否则，返回原始响应
        else response
    }

    // 定义一个私有方法 checkUpgrade，用于检查升级信息
    private fun checkUpgrade(): JSONObject {
        var page = 1  // 初始化页码为1
        var result: JSONObject?  // 定义一个变量 result 用于存储检查结果
        do {
            // 调用 pagingCheck 方法检查当前页的升级信息，将页码递增
            result = pagingCheck(page++)
        } while (result == null)  // 如果结果为 null，则继续检查下一页
        return result  // 返回非空的结果
    }
    // 定义一个私有方法 pagingCheck，用于检查特定页码的升级信息
    private fun pagingCheck(page: Int): JSONObject? {
        // 获取应用上下文
        val context = Utils.getContext()
    
        // 获取应用的构建序列号（BUILD_SN）
        val sn = context.packageManager.getApplicationInfo(
            context.packageName, PackageManager.GET_META_DATA
        ).metaData.getInt("BUILD_SN").toLong()
    
        // 获取当前补丁版本和补丁版本代码
        val patchVersion = BuildConfig.VERSION_NAME
        val patchVersionCode = BuildConfig.VERSION_CODE
    
        // 构造分页请求的 URL
        val pageUrl = "$UPGRADE_CHECK_API?page=$page&per_page=100"
    
        // 发送 HTTP 请求并解析响应为 JSON 数组
        val response = JSONArray(URL(pageUrl).readText())
    
        // 获取移动应用标识
        val mobiApp = Utils.getMobiApp()
    
        // 遍历响应中的每个数据项
        for (data in response) {
            // 检查标签名是否以移动应用标识开头，如果不是则跳过
            if (!data.optString("tag_name").startsWith("$mobiApp-"))
                continue
        
            // 获取变更日志并进行格式替换
            val body = data.optString("body").replace("\r\n", "\n")
        
            // 使用正则表达式提取版本信息和变更日志
            val values = changelogRegex.matchEntire(body)?.groupValues ?: break
            val versionSum = values[1]
            val changelog = values[2].trim()
        
            // 获取下载链接
            val url = data.optJSONArray("assets")
                ?.optJSONObject(0)?.optString("browser_download_url") ?: break
        
            // 输出调试信息
            Logger.debug { "Upgrade, versionSum: $versionSum, changelog: $changelog, url: $url" }
        
            // 创建 BUpgradeInfo 对象
            val info = BUpgradeInfo(versionSum, url, changelog)
        
            // 检查是否有新版本可用
            if (sn < info.sn || (sn == info.sn && patchVersionCode < info.patchVersionCode)) {
                val sameApp = sn == info.sn
                val samePatch = patchVersion == info.patchVersion
                val newChangelog = StringBuilder(info.changelog)
            
                // 构建版本变更信息
                val appVersionChange =
                    if (sameApp) "" else "APP版本：$versionName($versionCode) --> ${info.version}(${info.versionCode})"
                val patchVersionChange =
                    if (samePatch) "" else "漫游X版本：$patchVersion --> ${info.patchVersion}"
                val changeSum = arrayOf(appVersionChange, patchVersionChange)
                    .filterNot { it.isEmpty() }.joinToString(separator = "\n")
            
                // 将变更信息附加到变更日志中
                if (changeSum.isNotEmpty()) {
                    newChangelog.append("\n\n")
                    newChangelog.append(changeSum)
                }
            
                // 构建并返回包含升级信息的 JSON 对象
                return mapOf(
                    "code" to 0,
                    "message" to "0",
                    "ttl" to 1,
                    "data" to mapOf(
                        "title" to "新版 Bilix",
                        "content" to newChangelog.toString(),
                        "version" to info.version,
                        "version_code" to if (sameApp) info.versionCode + 1 else info.versionCode,
                        "url" to speedupGhUrl(info.url),
                        "size" to info.size,
                        "md5" to info.md5,
                        "silent" to 0,
                        "upgrade_type" to 1,
                        "cycle" to 1,
                        "policy" to 0,
                        "policy_url" to "",
                        "ptime" to info.publishTime,
                    )
                ).toJSONObject().also {
                    Logger.debug { "Upgrade check result: $it" }
                }
            } else {
                // 如果没有新版本，返回提示信息
                return mapOf("code" to -1, "message" to "未发现新版 Bilix ！").toJSONObject()
            }
        }
        // 如果没有找到合适的版本，返回 null
        return null
    }
}