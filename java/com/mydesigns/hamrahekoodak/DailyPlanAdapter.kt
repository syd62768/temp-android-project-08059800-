package com.mydesigns.hamrahekoodak

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class DailyPlanAdapter(
    private val dailyPlans: List<DailyPlan>,
    private val gender: String?
) : RecyclerView.Adapter<DailyPlanAdapter.DailyPlanViewHolder>() {

    // ** ویوهای جدید کارت را اینجا تعریف می‌کنیم **
    class DailyPlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayHeader: LinearLayout = itemView.findViewById(R.id.day_header)
        val dayIcon: TextView = itemView.findViewById(R.id.textViewDayIcon)
        val dayName: TextView = itemView.findViewById(R.id.textViewDayName)
        val breakfast: TextView = itemView.findViewById(R.id.textViewBreakfast)
        val lunch: TextView = itemView.findViewById(R.id.textViewLunch)
        val snack: TextView = itemView.findViewById(R.id.textViewSnack)
        val dinner: TextView = itemView.findViewById(R.id.textViewDinner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyPlanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daily_plan, parent, false)
        return DailyPlanViewHolder(view)
    }

    override fun getItemCount(): Int = dailyPlans.size

    override fun onBindViewHolder(holder: DailyPlanViewHolder, position: Int) {
        val plan = dailyPlans[position]

        // ** مقداردهی ویوهای جدید **
        holder.dayIcon.text = plan.dayIcon
        holder.dayName.text = plan.dayName
        holder.breakfast.text = plan.breakfast.name
        holder.lunch.text = plan.lunch.name
        holder.snack.text = plan.snack.name
        holder.dinner.text = plan.dinner.name

        // تغییر رنگ هدر کارت بر اساس جنسیت
        val themeColorRes = when (gender) {
            "دختر" -> R.color.girl_button
            "پسر" -> R.color.boy_button
            else -> R.color.default_button
        }
        val themeColor = ContextCompat.getColor(holder.itemView.context, themeColorRes)
        holder.dayHeader.setBackgroundColor(themeColor)
    }
}