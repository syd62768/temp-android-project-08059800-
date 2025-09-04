package com.mydesigns.hamrahekoodak

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class MealPlannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_planner)

        // --- دریافت اطلاعات و تنظیمات اولیه ---
        val birthDate = intent.getStringExtra("BIRTH_DATE")
        val gender = intent.getStringExtra("GENDER")

        val rootLayout = findViewById<CoordinatorLayout>(R.id.meal_planner_root_layout)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDailyPlans)
        val emptyTextView = findViewById<TextView>(R.id.textViewEmptyPlan)

        applyTheme(gender, rootLayout)

        // --- دریافت و نمایش برنامه غذایی ---
        val ageInDays = PersianCalendarHelper.getAgeInDays(birthDate ?: "")

        // ** تغییر اصلی و رفع خطا: نام تابع به getPlanForCurrentWeek اصلاح شد **
        val weeklyPlan = NutritionData.getPlanForCurrentWeek(ageInDays)

        if (weeklyPlan.isEmpty()) {
            // اگر برنامه‌ای برای این سن وجود نداشت، پیام مناسب نمایش داده می‌شود
            recyclerView.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
        } else {
            // اگر برنامه وجود داشت، آداپتور ساخته و به RecyclerView متصل می‌شود
            recyclerView.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE
            recyclerView.adapter = DailyPlanAdapter(weeklyPlan, gender)
        }
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