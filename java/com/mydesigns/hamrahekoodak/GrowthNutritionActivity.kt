package com.mydesigns.hamrahekoodak

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class GrowthNutritionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_growth_nutrition)

        val viewPager: ViewPager2 = findViewById(R.id.view_pager_growth_nutrition)
        val tabLayout: TabLayout = findViewById(R.id.tab_layout_growth_nutrition)
        val backButton: android.widget.ImageView = findViewById(R.id.iv_back)

        // دریافت اطلاعات ارسال شده از داشبورد
        val childName = intent.getStringExtra("CHILD_NAME")
        val childGender = intent.getStringExtra("GENDER")
        val childBirthDate = intent.getStringExtra("BIRTH_DATE")

        // ساخت آداپتور و ارسال اطلاعات به آن
        val adapter = GrowthNutritionPagerAdapter(this, childName, childGender, childBirthDate)
        viewPager.adapter = adapter

        // ==================== راه حل قطعی: غیرفعال کردن اسلاید ====================
        // این خط به ViewPager اصلی دستور می‌دهد که به هیچ‌وجه با کشیدن انگشت
        // جابجا نشود. این کار تداخل را به طور کامل از بین می‌برد.
        viewPager.isUserInputEnabled = false
        // =======================================================================

        // اتصال TabLayout به ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "نمودار رشد"
                1 -> "برنامه غذایی"
                else -> null
            }
        }.attach()

        backButton.setOnClickListener {
            finish() // بستن این Activity و بازگشت به صفحه قبلی
        }
    }
}

