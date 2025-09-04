package com.mydesigns.hamrahekoodak

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ChartFragmentAdapter(
    fragmentActivity: FragmentActivity,
    private val gender: String,
    private val records: ArrayList<GrowthRecord> // دریافت لیست در کانستراکتور
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val metric = when (position) {
            0 -> "weight"
            1 -> "height"
            else -> "head"
        }
        // ارسال لیست رکوردها به هر فرگمنت جدید
        return ChartFragment.newInstance(metric, gender, records)
    }

    // این دو متد برای به‌روزرسانی صحیح فرگمنت‌ها ضروری هستند
    override fun getItemId(position: Int): Long = position.toLong()
    override fun containsItem(itemId: Long): Boolean = itemId >= 0 && itemId < 3
}
