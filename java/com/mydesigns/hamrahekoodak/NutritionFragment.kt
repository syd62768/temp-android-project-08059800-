package com.mydesigns.hamrahekoodak

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class NutritionFragment : Fragment() {

    private lateinit var cardsContainer: LinearLayout
    private var birthDate: String? = null
    private var childName: String? = null
    private var gender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            birthDate = it.getString("BIRTH_DATE")
            childName = it.getString("CHILD_NAME")
            gender = it.getString("GENDER")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // از لایوت activity_nutrition که از قبل داشتید، استفاده می‌کنیم
        return inflater.inflate(R.layout.activity_nutrition, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- تنظیمات اولیه View ها ---
        val titleTextView = view.findViewById<TextView>(R.id.textViewNutritionTitle)
        val ageTextView = view.findViewById<TextView>(R.id.textViewNutritionAge)
        cardsContainer = view.findViewById(R.id.linearLayout_cards_container)
        val mealPlannerButton = view.findViewById<CardView>(R.id.card_open_meal_planner)

        // حذف Toolbar از دید، چون در Activity اصلی Toolbar داریم
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_nutrition)
        toolbar.visibility = View.GONE

        titleTextView.text = "سفر خوشمزه $childName قشنگم"

        // ==================== FIX START ====================
        // متد اشتباه با متد صحیح جایگزین شد
        ageTextView.text = "سن فعلی: ${PersianCalendarHelper.getAgeText(birthDate ?: "")}"
        // ===================== FIX END =====================

        // بخش تغییر تم حذف شد تا با تم اصلی صفحه هماهنگ باشد
        createNutritionCards()

        mealPlannerButton.setOnClickListener {
            val intent = Intent(activity, MealPlannerActivity::class.java).apply {
                putExtra("GENDER", gender)
                putExtra("BIRTH_DATE", birthDate)
            }
            startActivity(intent)
        }
    }

    private fun createNutritionCards() {
        // این تابع بدون تغییر باقی می‌ماند و کارت‌های بازشونده را می‌سازد
        val titles = listOf(
            R.string.nutrition_0_6_title,
            R.string.nutrition_6_8_title,
            R.string.nutrition_8_12_title,
            R.string.nutrition_12_24_title,
            R.string.nutrition_2_6_title,
            R.string.nutrition_info_liquids_title,
            R.string.nutrition_info_brain_title,
            R.string.nutrition_info_faq_title
        )
        val contents = listOf(
            R.string.nutrition_0_6_content,
            R.string.nutrition_6_8_content,
            R.string.nutrition_8_12_content,
            R.string.nutrition_12_24_content,
            R.string.nutrition_2_6_content,
            R.string.nutrition_info_liquids_content,
            R.string.nutrition_info_brain_content,
            R.string.nutrition_info_faq_content
        )

        // قبل از اضافه کردن کارت‌های جدید، کانتینر را خالی می‌کنیم
        cardsContainer.removeAllViews()

        for (i in titles.indices) {
            val cardView = LayoutInflater.from(context).inflate(R.layout.item_expandable_card, cardsContainer, false)
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
}

