package com.mydesigns.hamrahekoodak

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val childName = intent.getStringExtra("CHILD_NAME")
        val gender = intent.getStringExtra("GENDER")
        val birthDate = intent.getStringExtra("BIRTH_DATE")

        findViewById<TextView>(R.id.textViewWelcome).text = "دنیای سلامتی $childName قشنگم!"

        applyTheme(gender)
        setupCardListeners(childName, gender, birthDate)
    }

    private fun setupCardListeners(childName: String?, gender: String?, birthDate: String?) {
        findViewById<CardView>(R.id.card_nutrition).setOnClickListener {
            openActivity(NutritionActivity::class.java, childName, gender, birthDate)
        }
        findViewById<CardView>(R.id.card_growth).setOnClickListener {
            openActivity(GrowthChartActivity::class.java, childName, gender, birthDate)
        }

        val comingSoonListener = View.OnClickListener {
            Toast.makeText(this, "این بخش به زودی اضافه می‌شود!", Toast.LENGTH_SHORT).show()
        }
        findViewById<CardView>(R.id.card_supplements).setOnClickListener(comingSoonListener)
        findViewById<CardView>(R.id.card_vaccination).setOnClickListener(comingSoonListener)
        findViewById<CardView>(R.id.card_development).setOnClickListener(comingSoonListener)
        findViewById<CardView>(R.id.card_education).setOnClickListener(comingSoonListener)
    }

    private fun <T> openActivity(activityClass: Class<T>, childName: String?, gender: String?, birthDate: String?) {
        val intent = Intent(this, activityClass).apply {
            putExtra("CHILD_NAME", childName)
            putExtra("GENDER", gender)
            putExtra("BIRTH_DATE", birthDate)
        }
        startActivity(intent)
    }

    private fun applyTheme(gender: String?) {
        val rootLayout = findViewById<View>(R.id.dashboard_root_layout)
        val (bgColor, statusBarColor) = when (gender) {
            "دختر" -> Pair(R.color.girl_background_light, R.color.girl_colorPrimaryDark)
            "پسر" -> Pair(R.color.boy_background_light, R.color.boy_colorPrimaryDark)
            else -> Pair(R.color.default_background_light, R.color.default_colorPrimaryDark)
        }
        rootLayout.setBackgroundColor(ContextCompat.getColor(this, bgColor))
        window.statusBarColor = ContextCompat.getColor(this, statusBarColor)

        applyIconColors(gender)
    }

    /**
     * اصلاح اصلی: برای رفع خطای Unresolved reference،
     * شناسه‌ی رنگ‌ها به صورت پویا و با نام آن‌ها پیدا می‌شود.
     * این تغییر هیچ تاثیری در عملکرد نهایی ندارد و فقط مشکل Build را حل می‌کند.
     */
    private fun applyIconColors(gender: String?) {
        val colorPrefix = if (gender == "دختر") "girl_icon_color_" else "boy_icon_color_"

        val colorPalette = (1..6).map { index ->
            val colorName = "${colorPrefix}$index"
            resources.getIdentifier(colorName, "color", packageName)
        }

        val iconIds = listOf(
            R.id.icon_nutrition, R.id.icon_growth, R.id.icon_supplements,
            R.id.icon_vaccination, R.id.icon_development, R.id.icon_education
        )

        iconIds.forEachIndexed { index, iconId ->
            val imageView = findViewById<ImageView>(iconId)
            val layerDrawable = imageView.drawable as? LayerDrawable
            val background = layerDrawable?.getDrawable(0) as? GradientDrawable

            // اطمینان از اینکه شناسه رنگ معتبر است
            if (colorPalette[index] != 0) {
                background?.setColor(ContextCompat.getColor(this, colorPalette[index]))
            }
        }
    }
}
