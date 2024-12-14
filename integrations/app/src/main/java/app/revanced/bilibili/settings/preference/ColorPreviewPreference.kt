package app.revanced.bilibili.settings.preference

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.view.View

class ShowColorPreference(
    context: Context,
    attrs: AttributeSet
) : Preference(context, attrs) {
    private var color: Int = Color.TRANSPARENT

    init {
        layoutResource = R.layout.biliroaming_preferece_color
    }

    fun setColor(color: Int) {
        this.color = color
        notifyDependencyChange(false)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.findViewById<ImageView>(R.id.biliroaming_bg_show_color)?.setBackgroundColor(color)
    }

    fun setTitle(title: String) {
        val titleView = findViewById<TextView>(R.id.biliroaming_color_title)
        titleView?.text = title
    }

    fun setSummary(summary: String) {
        val summaryView = findViewById<TextView>(R.id.prefbiliroaming_color_summary)
        summaryView?.text = summary
    }

}
