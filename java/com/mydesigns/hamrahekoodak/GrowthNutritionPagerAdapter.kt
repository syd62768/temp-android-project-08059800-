package com.mydesigns.hamrahekoodak

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class GrowthNutritionPagerAdapter(
    activity: AppCompatActivity,
    private val childName: String?,
    private val childGender: String?,
    private val childBirthDate: String?
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> createGrowthChartFragment()
            // ==================== FIX START ====================
            1 -> createNutritionFragment() // از تابع جدید استفاده می‌کنیم
            // ===================== FIX END =====================
            else -> throw IllegalStateException("Invalid position $position")
        }
    }

    private fun createGrowthChartFragment(): GrowthChartFragment {
        return GrowthChartFragment().apply {
            arguments = Bundle().apply {
                putString("CHILD_NAME", childName)
                putString("GENDER", childGender)
                putString("BIRTH_DATE", childBirthDate)
            }
        }
    }

    // ==================== FIX START ====================
    // این تابع اضافه شد تا اطلاعات به فرگمنت تغذیه هم ارسال شود
    private fun createNutritionFragment(): NutritionFragment {
        return NutritionFragment().apply {
            arguments = Bundle().apply {
                putString("CHILD_NAME", childName)
                putString("GENDER", childGender)
                putString("BIRTH_DATE", childBirthDate)
            }
        }
    }
    // ===================== FIX END =====================
}

