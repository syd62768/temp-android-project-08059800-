package com.mydesigns.hamrahekoodak

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.lang.NumberFormatException

// <<<< تعریف تکراری AnalysisInfo از اینجا حذف شد >>>>

class GrowthChartActivity : AppCompatActivity() {

    private lateinit var rootLayout: CoordinatorLayout
    private lateinit var textViewTitle: TextView
    private lateinit var textViewCurrentAge: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var fabAddRecord: FloatingActionButton
    private lateinit var analysisCard: androidx.cardview.widget.CardView
    private lateinit var statusLabel: TextView
    private lateinit var statusText: TextView
    private lateinit var statusIcon: ImageView

    private var childBirthDate: String? = null
    private var childGender: String? = null
    private val growthRecords = ArrayList<GrowthRecord>()
    private var currentAgeInDays: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_growth_chart)

        initViews()

        val childName = intent.getStringExtra("CHILD_NAME")
        childGender = intent.getStringExtra("GENDER")
        childBirthDate = intent.getStringExtra("BIRTH_DATE")

        textViewTitle.text = "ماجرای قد کشیدن $childName"
        calculateAndDisplayAge()
        applyTheme(childGender)
        setupViewPager()
        updateAnalysisCard(0)

        fabAddRecord.setOnClickListener {
            showAddRecordDialog()
        }
    }

    private fun initViews() {
        rootLayout = findViewById(R.id.growth_root_layout)
        textViewTitle = findViewById(R.id.textViewGrowthTitle)
        textViewCurrentAge = findViewById(R.id.textViewCurrentAge)
        viewPager = findViewById(R.id.viewPagerCharts)
        tabLayout = findViewById(R.id.tabLayoutDots)
        fabAddRecord = findViewById(R.id.fab_add_record)
        analysisCard = findViewById(R.id.cardAnalysis)
        statusLabel = findViewById(R.id.textViewGrowthStatusLabel)
        statusText = findViewById(R.id.textViewGrowthStatus)
        statusIcon = findViewById(R.id.imageViewGrowthStatus)
    }

    private fun setupViewPager() {
        val adapter = ChartFragmentAdapter(this, childGender ?: "دختر", growthRecords)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        if (viewPager.getTag(R.id.viewPagerCharts) == null) {
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateAnalysisCard(position)
                }
            })
            viewPager.setTag(R.id.viewPagerCharts, true)
        }
    }

    private fun showAddRecordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_growth_record, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        val weightInput = dialogView.findViewById<EditText>(R.id.editTextDialogWeight)
        val heightInput = dialogView.findViewById<EditText>(R.id.editTextDialogHeight)
        val headInput = dialogView.findViewById<EditText>(R.id.editTextDialogHead)
        val confirmButton = dialogView.findViewById<Button>(R.id.buttonDialogConfirm)

        confirmButton.setOnClickListener {
            val weightStr = weightInput.text.toString()
            val heightStr = heightInput.text.toString()
            val headStr = headInput.text.toString()

            if (weightStr.isBlank() || heightStr.isBlank() || headStr.isBlank()) {
                Toast.makeText(this, "لطفاً تمام موارد را پر کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                calculateAndDisplayAge()

                val record = GrowthRecord(
                    ageInDays = currentAgeInDays,
                    weight = weightStr.toFloat(),
                    height = heightStr.toFloat(),
                    headCirc = headStr.toFloat()
                )
                growthRecords.add(record)

                val currentItem = viewPager.currentItem
                setupViewPager()
                viewPager.setCurrentItem(currentItem, false)

                updateAnalysisCard(viewPager.currentItem)
                dialog.dismiss()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "لطفاً مقادیر عددی صحیح وارد کنید", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun updateAnalysisCard(position: Int) {
        val latestRecord = growthRecords.lastOrNull()
        if (latestRecord == null) {
            statusLabel.text = "وضعیت رشد"
            statusText.text = "برای مشاهده تحلیل، اطلاعات را با دکمه + ثبت کنید."
            statusIcon.setColorFilter(ContextCompat.getColor(this, R.color.default_button))
            return
        }

        val ageInMonths = (latestRecord.ageInDays / 30).toInt()

        val info = when (position) {
            0 -> AnalysisInfo("weight", latestRecord.weight, "وزن", "وزن یکی از بهترین شاخص‌های سلامت و تغذیه کودک است.")
            1 -> AnalysisInfo("height", latestRecord.height, "قد", "قد کشیدن نشان‌دهنده سلامت عمومی و رشد استخوانی است.")
            else -> AnalysisInfo("head", latestRecord.headCirc, "دور سر", "رشد دور سر نشانگر رشد مغز کودک شماست.")
        }

        val percentiles = GrowthDataWHO.getPercentilesForAge(childGender, info.metric, ageInMonths) ?: return

        val (labelText, analysisText, color) = when {
            info.value > percentiles[4] -> Triple("بالاتر از حد معمول", "${info.whoText} رشدش سریع‌تر از ۹۷٪ هم‌سن‌هاشه. برای اطمینان، بررسی تخصصی توصیه می‌شود.", "#FFC107") // زرد
            info.value < percentiles[0] -> Triple("کمتر از حد استاندارد", "${info.whoText} نیاز به توجه بیشتری داره. نگران نباشید، اما پیشنهاد می‌شود با پزشک مشورت کنید.", "#F44336") // قرمز
            else -> Triple("رشد در محدوده نرمال", "${info.whoText} آفرین! همه چیز عالی و طبق استاندارد جهانی پیش میره.", "#4CAF50") // سبز
        }

        statusLabel.text = "وضعیت ${info.unit}: $labelText"
        statusText.text = analysisText
        statusIcon.setColorFilter(Color.parseColor(color))
    }

    private fun calculateAndDisplayAge() {
        currentAgeInDays = PersianCalendarHelper.getAgeInDays(childBirthDate ?: "")
        textViewCurrentAge.text = "سن فعلی: ${PersianCalendarHelper.getAgeText(childBirthDate ?: "")}"
    }

    private fun applyTheme(gender: String?) {
        val (bgColor, statusBarColor, fabColor) = when (gender) {
            "دختر" -> Triple(R.color.girl_background_light, R.color.girl_colorPrimaryDark, R.color.girl_button)
            "پسر" -> Triple(R.color.boy_background_light, R.color.boy_colorPrimaryDark, R.color.boy_button)
            else -> Triple(R.color.default_background_light, R.color.default_colorPrimaryDark, R.color.default_button)
        }
        rootLayout.setBackgroundColor(ContextCompat.getColor(this, bgColor))
        window.statusBarColor = ContextCompat.getColor(this, statusBarColor)
        fabAddRecord.backgroundTintList = ContextCompat.getColorStateList(this, fabColor)
    }
}

