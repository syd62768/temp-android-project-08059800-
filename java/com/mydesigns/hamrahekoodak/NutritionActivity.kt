package com.mydesigns.hamrahekoodak

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat

class NutritionActivity : AppCompatActivity() {

    private lateinit var cardsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition)

        // --- دریافت اطلاعات و تنظیمات اولیه ---
        val childName = intent.getStringExtra("CHILD_NAME")
        val birthDate = intent.getStringExtra("BIRTH_DATE")
        val gender = intent.getStringExtra("GENDER")

        val rootLayout = findViewById<CoordinatorLayout>(R.id.nutrition_root_layout)
        val titleTextView = findViewById<TextView>(R.id.textViewNutritionTitle)
        val ageTextView = findViewById<TextView>(R.id.textViewNutritionAge)
        cardsContainer = findViewById(R.id.linearLayout_cards_container)
        val mealPlannerButton = findViewById<CardView>(R.id.card_open_meal_planner)

        titleTextView.text = "سفر خوشمزه $childName قشنگم"
        val ageInDays = PersianCalendarHelper.getAgeInDays(birthDate ?: "")
        ageTextView.text = "سن فعلی: ${PersianCalendarHelper.getAgeTextFromDays(ageInDays)}"

        applyTheme(gender, rootLayout)
        createNutritionCards()

        mealPlannerButton.setOnClickListener {
            val intent = Intent(this, MealPlannerActivity::class.java).apply {
                putExtra("GENDER", gender)
                putExtra("BIRTH_DATE", birthDate)
            }
            startActivity(intent)
        }
    }

    private fun createNutritionCards() {
        // ** لیست کارت‌ها با FAQ به‌روز شده **
        val titles = listOf(
            R.string.nutrition_0_6_title,
            R.string.nutrition_6_8_title,
            R.string.nutrition_8_12_title,
            R.string.nutrition_12_24_title,
            R.string.nutrition_2_6_title,
            R.string.nutrition_info_liquids_title,
            R.string.nutrition_info_brain_title,
            R.string.nutrition_info_faq_title      // استفاده از کارت FAQ جدید و به‌روز
        )
        val contents = listOf(
            R.string.nutrition_0_6_content,
            R.string.nutrition_6_8_content,
            R.string.nutrition_8_12_content,
            R.string.nutrition_12_24_content,
            R.string.nutrition_2_6_content,
            R.string.nutrition_info_liquids_content,
            R.string.nutrition_info_brain_content,
            R.string.nutrition_info_faq_content      // استفاده از محتوای FAQ جدید و به‌روز
        )

        for (i in titles.indices) {
            val cardView = LayoutInflater.from(this).inflate(R.layout.item_expandable_card, cardsContainer, false)
            val headerView = cardView.findViewById<LinearLayout>(R.id.card_header)
            val titleView = cardView.findViewById<TextView>(R.id.card_title)
            val arrowView = cardView.findViewById<ImageView>(R.id.card_arrow)
            val contentView = cardView.findViewById<TextView>(R.id.card_content)
            titleView.text = getString(titles[i])
            contentView.text = Html.fromHtml(getString(contents[i]), Html.FROM_HTML_MODE_LEGACY)
            headerView.setOnClickListener {
                toggleCardContent(contentView, arrowView)
            }
            cardsContainer.addView(cardView)
        }
    }

    private fun toggleCardContent(contentView: View, arrowView: ImageView) {
        val isVisible = contentView.visibility == View.VISIBLE
        contentView.visibility = if (isVisible) View.GONE else View.VISIBLE
        val targetRotation = if (isVisible) 0f else 180f
        ObjectAnimator.ofFloat(arrowView, "rotation", arrowView.rotation, targetRotation)
            .setDuration(300)
            .start()
    }

    private fun applyTheme(gender: String?, rootLayout: View) {
        val (bgColor, statusBarColor) = when (gender) {
            "دختر" -> Pair(R.color.girl_background_light, R.color.girl_colorPrimaryDark)
            "پسر" -> Pair(R.color.boy_background_light, R.color.boy_colorPrimaryDark)
            else -> Pair(R.color.default_background_light, R.color.default_colorPrimaryDark)
        }
        rootLayout.setBackgroundColor(ContextCompat.getColor(this, bgColor))
        window.statusBarColor = ContextCompat.getColor(this, statusBarColor)
    }
}