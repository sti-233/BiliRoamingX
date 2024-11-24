package app.revanced.bilibili.settings.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreferenceCompat
import app.revanced.bilibili.utils.*

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ScrollView

import app.revanced.bilibili.settings.Settings
import app.revanced.bilibili.settings.search.annotation.SettingFragment

@SettingFragment("biliroaming_setting_sponsor")
class SponsorBlockFragment : BiliRoamingBaseSettingFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findPreference<Preference>("sb_about_url")?.onClick {
            val uri = Uri.parse("https://github.com/hanydd/BilibiliSponsorBlock")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            true
        }
}
