package com.mydesigns.hamrahekoodak

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.io.Serializable


private const val ARG_METRIC = "metric"
private const val ARG_RECORDS = "records"
private const val ARG_GENDER = "gender"
private const val CHILD_DATA_TAG = "CHILD_DATA_SET"
private const val PULSATING_DOT_TAG = "PULSATING_DOT_TAG"

class ChartFragment : Fragment() {
    private var metric: String? = null
    private var records: ArrayList<GrowthRecord>? = null
    private var gender: String? = null
    private lateinit var lineChart: LineChart

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        lineChart = view.findViewById(R.id.lineChartFragment)

        arguments?.let {
            metric = it.getString(ARG_METRIC)
            gender = it.getString(ARG_GENDER)

            records = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable(ARG_RECORDS, ArrayList::class.java) as? ArrayList<GrowthRecord>
            } else {
                @Suppress("DEPRECATION")
                it.getSerializable(ARG_RECORDS) as? ArrayList<GrowthRecord>
            }
        }

        setupChart()
        drawStandardCurves()
        drawChildData()

        return view
    }

    private fun setupChart() {
        val (desc, unit) = when (metric) {
            "weight" -> Pair("نمودار وزن (۰ تا ۲۴ ماهگی)", "کیلوگرم")
            "height" -> Pair("نمودار قد (۰ تا ۲۴ ماهگی)", "سانتی‌متر")
            else -> Pair("نمودار دور سر (۰ تا ۲۴ ماهگی)", "سانتی‌متر")
        }

        lineChart.apply {
            description.text = desc
            description.textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
            description.textSize = 9f
            setTouchEnabled(true)
            setPinchZoom(true)

            val gradient = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(Color.parseColor("#F5F7FA"), Color.WHITE)
            )
            background = gradient
            setDrawGridBackground(false)

            setExtraOffsets(30f, 15f, 20f, 15f)

            legend.textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
            legend.textSize = 8f

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
                textSize = 9f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String = "${value.toInt()} ماه"
                }
                granularity = 2f
                setDrawGridLines(false)
                axisMinimum = 0f
                axisMaximum = 24f
            }

            axisLeft.apply {
                textColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)
                textSize = 9f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String = "$value $unit"
                }
                setDrawGridLines(true)
                gridColor = Color.parseColor("#E0E0E0")
            }
            axisRight.isEnabled = false
        }
    }

    private fun drawStandardCurves() {
        // --- شروع تغییرات: بهبود برچسب‌های منحنی‌ها ---
        val percentileLabels = listOf(
            "مرز پایینی رشد (صدک ۳)",
            "مسیر رشد طبیعی (صدک ۱۵)",
            "مسیر رشد میانه (صدک ۵۰)",
            "مسیر رشد طبیعی (صدک ۸۵)",
            "مرز بالایی رشد (صدک ۹۷)"
        )
        // --- پایان تغییرات ---
        val percentileColors = listOf(
            Color.parseColor("#F44336"), // Red
            Color.parseColor("#FFC107"), // Amber
            Color.BLACK,
            Color.parseColor("#FFC107"), // Amber
            Color.parseColor("#F44336")  // Red
        )

        val dataSets = mutableListOf<ILineDataSet>()
        val allEntries = (0..4).map { GrowthDataWHO.getPercentileEntries(gender, metric!!, it) }

        for (i in allEntries.indices) {
            val dataSet = LineDataSet(allEntries[i], percentileLabels[i]).apply {
                color = percentileColors[i]
                setDrawCircles(false)

                if (i == 2) {
                    lineWidth = 2.2f
                } else {
                    lineWidth = 1.5f
                    enableDashedLine(10f, 8f, 0f)
                }
                setDrawValues(false)
            }
            dataSets.add(dataSet)
        }

        lineChart.data = LineData(dataSets)
        lineChart.invalidate()
    }

    private fun getRecordStatusColor(record: GrowthRecord): Int {
        val ageInMonths = (record.ageInDays / 30).toInt()
        val value = when (metric) {
            "weight" -> record.weight
            "height" -> record.height
            else -> record.headCirc
        }
        val percentiles = GrowthDataWHO.getPercentilesForAge(gender, metric!!, ageInMonths) ?: return Color.GRAY
        return when {
            value > percentiles[4] || value < percentiles[0] -> Color.parseColor("#F44336") // قرمز
            value > percentiles[3] || value < percentiles[1] -> Color.parseColor("#FFC107") // زرد
            else -> Color.parseColor("#4CAF50") // سبز
        }
    }

    private fun getInterpolatedMedianValue(ageInMonthsFloat: Float): Float? {
        val medianEntries = GrowthDataWHO.getPercentileEntries(gender, metric!!, 2)
        if (medianEntries.size < 2) return medianEntries.firstOrNull()?.y

        var p1: Entry? = null
        var p2: Entry? = null
        for (i in 0 until medianEntries.size - 1) {
            if (medianEntries[i].x <= ageInMonthsFloat && medianEntries[i+1].x >= ageInMonthsFloat) {
                p1 = medianEntries[i]
                p2 = medianEntries[i+1]
                break
            }
        }
        if (p1 == null || p2 == null) {
            return medianEntries.lastOrNull { it.x <= ageInMonthsFloat }?.y
        }
        if (p1.x == p2.x) return p1.y
        return p1.y + (ageInMonthsFloat - p1.x) * (p2.y - p1.y) / (p2.x - p1.x)
    }

    private fun drawChildData() {
        val existingData = lineChart.data ?: return

        existingData.dataSets.removeAll { it.label == CHILD_DATA_TAG }

        val childRecords = records?.filterNotNull()?.sortedBy { it.ageInDays }
        if (childRecords.isNullOrEmpty()) {
            lineChart.notifyDataSetChanged()
            lineChart.invalidate()
            return
        }

        childRecords.forEach { record ->
            val ageInMonthsFloat = record.ageInDays / 30.44f
            val value = when (metric) {
                "weight" -> record.weight
                "height" -> record.height
                else -> record.headCirc
            }
            val color = getRecordStatusColor(record)

            val pointEntry = Entry(ageInMonthsFloat, value)
            val pointDataSet = LineDataSet(listOf(pointEntry), CHILD_DATA_TAG).apply {
                this.color = Color.TRANSPARENT
                setCircleColor(color)
                circleHoleColor = Color.WHITE
                circleRadius = 6f
                setDrawCircleHole(true)
                setDrawValues(false)
            }
            existingData.addDataSet(pointDataSet)

            val medianValue = getInterpolatedMedianValue(ageInMonthsFloat)
            if (medianValue != null) {
                val lineEntries = listOf(
                    Entry(ageInMonthsFloat, medianValue),
                    Entry(ageInMonthsFloat, value)
                )
                val lineDataSet = LineDataSet(lineEntries, CHILD_DATA_TAG).apply {
                    this.color = color
                    lineWidth = 1.5f
                    enableDashedLine(8f, 6f, 0f)
                    setDrawCircles(false)
                    setDrawValues(false)
                }
                existingData.addDataSet(lineDataSet)
            }
        }

        lineChart.notifyDataSetChanged()
        lineChart.invalidate()

        // *** شروع تغییرات برای انیمیشن ***
        childRecords.lastOrNull()?.let { lastRecord ->
            // از post استفاده می‌کنیم تا مطمئن شویم نمودار قبل از اجرای انیمیشن به طور کامل رسم شده است
            view?.post { addPulsatingDotToLastEntry(lastRecord) }
        }
        // *** پایان تغییرات برای انیمیشن ***
    }

    // *** تابع جدید برای افزودن نقطه انیمیشنی ***
    private fun addPulsatingDotToLastEntry(record: GrowthRecord) {
        val rootView = view as? FrameLayout ?: run {
            Log.e("ChartFragment", "خطا: لایه اصلی باید FrameLayout باشد تا انیمیشن کار کند.")
            return
        }

        // حذف نقطه انیمیشنی قبلی (اگر وجود داشته باشد)
        rootView.findViewWithTag<View>(PULSATING_DOT_TAG)?.let {
            rootView.removeView(it)
        }

        val ageInMonthsFloat = record.ageInDays / 30.44f
        val value = when (metric) {
            "weight" -> record.weight
            "height" -> record.height
            else -> record.headCirc
        }

        // دریافت مختصات دقیق نقطه روی صفحه
        val pixelCoords = lineChart.getPixelForValues(ageInMonthsFloat, value, lineChart.axisLeft.axisDependency)

        // اگر مختصات نامعتبر بود، از تابع خارج شو
        if (pixelCoords.x.isNaN() || pixelCoords.y.isNaN()) return

        // ساخت View برای نقطه
        val dot = View(requireContext()).apply {
            tag = PULSATING_DOT_TAG
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(getRecordStatusColor(record))
            }
        }

        val dotSize = 45 // اندازه نقطه به پیکسل
        val layoutParams = FrameLayout.LayoutParams(dotSize, dotSize).apply {
            // تنظیم موقعیت نقطه با در نظر گرفتن مرکز آن
            leftMargin = (pixelCoords.x - dotSize / 2).toInt()
            topMargin = (pixelCoords.y - dotSize / 2).toInt()
        }
        dot.layoutParams = layoutParams

        try {
            // بارگذاری و اجرای انیمیشن
            val pulsateAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.pulsate)
            dot.startAnimation(pulsateAnimation)
            rootView.addView(dot)
            dot.bringToFront() // اطمینان از اینکه نقطه روی همه چیز نمایش داده می‌شود
        } catch (e: Exception) {
            Log.e("ChartFragment", "خطا در بارگذاری انیمیشن. آیا فایل res/anim/pulsate.xml وجود دارد؟", e)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(metric: String, gender: String, records: ArrayList<GrowthRecord>) =
            ChartFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_METRIC, metric)
                    putString(ARG_GENDER, gender)
                    putSerializable(ARG_RECORDS, records)
                }
            }
    }
}


