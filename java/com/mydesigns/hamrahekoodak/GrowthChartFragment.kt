package com.mydesigns.hamrahekoodak

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GrowthChartFragment : Fragment() {

    companion object {
        private const val TAG = "GrowthChartFragment"
    }

    private lateinit var textViewTitle: TextView
    private lateinit var textViewCurrentAge: TextView
    private lateinit var fabAddRecord: FloatingActionButton

    // کارت تحلیل (اختیاری‌سازی همه)
    private var analysisCard: MaterialCardView? = null
    private var statusLabel: TextView? = null
    private var statusText: TextView? = null
    private var statusIcon: ImageView? = null

    // گیج
    private var gaugeContainer: View? = null
    private var needle: View? = null
    private var needleKnob: View? = null
    private var needleLabel: TextView? = null
    private var btnMore: View? = null

    // سوییچر شاخص‌ها
    private var chipGroup: ChipGroup? = null
    private var chipWeight: Chip? = null
    private var chipHeight: Chip? = null
    private var chipHead: Chip? = null

    private var childBirthDate: String? = null
    private var childGender: String? = null
    private val growthRecords = ArrayList<GrowthRecord>()
    private var currentAgeInDays: Long = 0

    private enum class Status { GREEN, YELLOW, RED }
    private enum class Metric { WEIGHT, HEIGHT, HEAD }
    private var currentMetric: Metric = Metric.WEIGHT

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_growth_chart, container, false)
        initViewsSafely(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val childName = arguments?.getString("CHILD_NAME")
        childGender = arguments?.getString("GENDER")
        childBirthDate = arguments?.getString("BIRTH_DATE")

        textViewTitle.text = "ماجرای قد کشیدن ${childName ?: "دلبندت"}"
        calculateAndDisplayAge()
        applyTheme(childGender)
        setupMetricSwitcher()
        updateAnalysisCard()

        fabAddRecord.setOnClickListener { showAddRecordDialog() }
    }

    // --- Helpers to find views safely ---
    private fun <T : View> v(root: View, id: Int): T? = root.findViewById(id)

    private fun initViewsSafely(root: View) {
        // حیاتی‌ها (در همان fragment_growth_chart.xml)
        textViewTitle = root.findViewById(R.id.textViewGrowthTitle)
        textViewCurrentAge = root.findViewById(R.id.textViewCurrentAge)
        fabAddRecord = root.findViewById(R.id.fab_add_record)

        // کارت تحلیل (ممکن است به هر دلیلی پیدا نشود؛ امن می‌گیریم)
        analysisCard = v(root, R.id.cardAnalysis)
        if (analysisCard == null) {
            Log.w(TAG, "cardAnalysis not found on fragment root. Check include id.")
        }

        statusLabel = v(root, R.id.textViewGrowthStatusLabel)
        statusText  = v(root, R.id.textViewGrowthStatus)
        statusIcon  = v(root, R.id.imageViewGrowthStatus)

        // گیج
        gaugeContainer = v(root, R.id.gaugeContainer)
        needle         = v(root, R.id.gaugeNeedle)
        needleKnob     = v(root, R.id.gaugeKnob)       // اختیاری
        needleLabel    = v(root, R.id.gaugeNeedleLabel)
        btnMore        = v(root, R.id.btnMore)

        // چیپ‌ها
        chipGroup  = v(root, R.id.chipMetricGroup)
        chipWeight = v(root, R.id.chip_weight)
        chipHeight = v(root, R.id.chip_height)
        chipHead   = v(root, R.id.chip_head)
    }

    private fun setupMetricSwitcher() {
        currentMetric = Metric.WEIGHT
        chipGroup?.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                chipWeight?.id -> currentMetric = Metric.WEIGHT
                chipHeight?.id -> currentMetric = Metric.HEIGHT
                chipHead?.id   -> currentMetric = Metric.HEAD
            }
            updateAnalysisCard()
        }
    }

    private fun showAddRecordDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_growth_record, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        val weightInput = dialogView.findViewById<EditText>(R.id.editTextDialogWeight)
        val heightInput = dialogView.findViewById<EditText>(R.id.editTextDialogHeight)
        val headInput   = dialogView.findViewById<EditText>(R.id.editTextDialogHead)
        val confirmButton = dialogView.findViewById<Button>(R.id.buttonDialogConfirm)

        confirmButton.setOnClickListener {
            val w = weightInput.text.toString()
            val h = heightInput.text.toString()
            val hd = headInput.text.toString()
            if (w.isBlank() || h.isBlank() || hd.isBlank()) {
                Toast.makeText(context, "لطفاً تمام موارد را پر کن", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                calculateAndDisplayAge()
                val record = GrowthRecord(currentAgeInDays, w.toFloat(), h.toFloat(), hd.toFloat())
                growthRecords.add(record)
                updateAnalysisCard()
                dialog.dismiss()
            } catch (_: NumberFormatException) {
                Toast.makeText(context, "لطفاً مقادیر عددی صحیح وارد کن", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun updateAnalysisCard() {
        val sLabel = statusLabel
        val sText  = statusText
        val sIcon  = statusIcon
        val card   = analysisCard

        // اگر کارت یا المان‌هایش موجود نبودند، امن رد بشه
        if (card == null || sLabel == null || sText == null || sIcon == null) {
            Log.w(TAG, "Analysis card views are missing. Skipping update.")
            return
        }

        val latestRecord = growthRecords.lastOrNull()
        if (latestRecord == null) {
            sLabel.text = "وضعیت رشد"
            sText.text  = "برای مشاهده تحلیل، اطلاعات را با دکمه + ثبت کن."
            sIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.default_button))
            updateGauge(50, Status.YELLOW)
            btnMore?.setOnClickListener(null)
            return
        }

        val ageInMonths = (latestRecord.ageInDays / 30).toInt()
        val (metricKey, value, whoText) = when (currentMetric) {
            Metric.WEIGHT -> Triple("weight", latestRecord.weight, "وزن")
            Metric.HEIGHT -> Triple("height", latestRecord.height, "قد")
            Metric.HEAD   -> Triple("head", latestRecord.headCirc, "دور سر")
        }

        val p = GrowthDataWHO.getPercentilesForAge(childGender, metricKey, ageInMonths)
        if (p == null || p.size < 5) {
            sLabel.text = "تحلیل در دسترس نیست"
            sText.text  = "لطفاً بعداً دوباره امتحان کن یا با پزشک مشورت کن."
            updateGauge(50, Status.YELLOW)
            btnMore?.setOnClickListener(null)
            return
        }

        val (status, title, sub, approxPct) = getMotherFriendlyAnalysis(metricKey, value, p, whoText)
        sLabel.text = title
        sText.text  = sub
        applyStatusUI(card, sIcon, status)
        updateGauge(approxPct, status)

        btnMore?.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("توضیحات بیشتر")
                .setMessage(getMoreText(metricKey, status))
                .setPositiveButton("باشه", null)
                .show()
        }
    }

    private fun applyStatusUI(card: MaterialCardView, icon: ImageView, status: Status) {
        val strokeHex = "#E2E8F0"
        val (bgHex, iconHex) = when (status) {
            Status.GREEN  -> "#E9F7EF" to "#2FA66A"
            Status.YELLOW -> "#FFF7CC" to "#C9A400"
            Status.RED    -> "#FDEDEC" to "#B71C1C"
        }
        card.strokeColor = Color.parseColor(strokeHex)
        card.setCardBackgroundColor(Color.parseColor(bgHex))
        icon.setColorFilter(Color.parseColor(iconHex))
    }

    private fun getMotherFriendlyAnalysis(
        metric: String,
        value: Float,
        p: List<Float>,
        whoText: String
    ): Quadruple<Status, String, String, Int> {
        // p: [P5, P15, P50, P85, P95]
        val status = when {
            value >= p[1] && value <= p[3] -> Status.GREEN
            (value > p[3] && value <= p[4]) || (value < p[1] && value >= p[0]) -> Status.YELLOW
            else -> Status.RED
        }

        val (title, sub) = when (metric) {
            "weight" -> when (status) {
                Status.GREEN  -> "$whoText طبیعی ✅" to "همه‌چیز خوب پیش می‌ره. 🌱"
                Status.YELLOW -> "$whoText نزدیک مرز ⚠️" to "ثبت‌های بعدی را ادامه بده و با آرامش پیگیری کن."
                Status.RED    -> "نیاز به بررسی $whoText ❗" to "برای اطمینان با پزشک/کارشناس مشورت کن."
            }
            "height" -> when (status) {
                Status.GREEN  -> "$whoText طبیعی ✅" to "روند قدی مناسب است."
                Status.YELLOW -> "$whoText نزدیک مرز ⚠️" to "کمی از میانه فاصله دارد؛ ثبت‌ها را ادامه بده."
                Status.RED    -> "نیاز به بررسی $whoText ❗" to "بررسی تخصصی توصیه می‌شود."
            }
            else -> when (status) { // head
                Status.GREEN  -> "$whoText طبیعی ✅" to "نشانهٔ خوبی از رشد مغزی است."
                Status.YELLOW -> "$whoText مرزی ⚠️"  to "در مرزهای طبیعی است؛ ثبت‌های بعدی را ادامه بده."
                Status.RED    -> "نیاز به بررسی $whoText ❗" to "برای ارزیابی دقیق‌تر با پزشک مشورت کن."
            }
        }

        val approxPercentile = when {
            status == Status.GREEN  && value < p[2] -> 35
            status == Status.GREEN  && value >= p[2] -> 65
            status == Status.YELLOW && value < p[1] -> 12
            status == Status.YELLOW && value > p[3] -> 88
            status == Status.RED    && value < p[0] -> 5
            status == Status.RED    && value > p[4] -> 95
            else -> 50
        }
        return Quadruple(status, title, sub, approxPercentile)
    }

    data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    private fun updateGauge(percentile: Int, status: Status) {
        val pct = percentile.coerceIn(0, 100)
        val labelText = when (status) {
            Status.GREEN  -> "طبیعی"
            Status.YELLOW -> "نزدیک مرز"
            Status.RED    -> "نیاز به بررسی"
        }
        needleLabel?.text = labelText

        val container = gaugeContainer ?: return
        container.doOnLayout {
            val width = container.width
            val nx = width * (pct / 100f)

            needle?.animate()
                ?.translationX(nx - (needle?.width ?: 0) / 2f)
                ?.setDuration(350L)
                ?.start()

            needleKnob?.animate()
                ?.translationX(nx - (needleKnob?.width ?: 0) / 2f)
                ?.setDuration(350L)
                ?.start()

            needleLabel?.animate()
                ?.translationX(nx - (needleLabel?.width ?: 0) / 2f)
                ?.setDuration(350L)
                ?.start()
        }
    }

    private fun getMoreText(metric: String, status: Status): String =
        when (metric) {
            "weight" -> when (status) {
                Status.GREEN  -> "وزن‌گیری طبیعی است. هر ۴–۸ هفته ثبت کن تا روند دقیق دیده شود."
                Status.YELLOW -> "وزن نزدیک مرز است. ثبت‌های منظم را ادامه بده؛ اگر در سه ثبت پیاپی شیب رو به پایین شد، با کارشناس صحبت کن."
                Status.RED    -> "وزن خارج از محدودهٔ معمول است. برای اطمینان ارزیابی پزشکی/تغذیه‌ای توصیه می‌شود."
            }
            "height" -> when (status) {
                Status.GREEN  -> "قد طبیعی است. ثابت ماندن روی مسیر خودش علامت خوبیه."
                Status.YELLOW -> "قد نزدیک مرز است. ژنتیک و تغذیه مؤثرند؛ ثبت‌ها را ادامه بده."
                Status.RED    -> "قد خارج از محدودهٔ معمول است. بررسی تخصصی و احتمالا آزمایش‌های تکمیلی نیاز است."
            }
            else -> when (status) {
                Status.GREEN  -> "دور سر طبیعی است و با رشد مغزی همخوانی دارد."
                Status.YELLOW -> "دور سر در مرزهاست؛ اندازه‌گیری دقیق و تکراری را ادامه بده."
                Status.RED    -> "دور سر نیاز به ارزیابی تخصصی دارد؛ جهت اطمینان با پزشک مشورت کن."
            }
        }

    private fun calculateAndDisplayAge() {
        currentAgeInDays = PersianCalendarHelper.getAgeInDays(childBirthDate ?: "")
        textViewCurrentAge.text = "سن فعلی: ${PersianCalendarHelper.getAgeText(childBirthDate ?: "")}"
    }

    private fun applyTheme(gender: String?) {
        val fabColor = when (gender) {
            "دختر" -> R.color.girl_button
            "پسر"  -> R.color.boy_button
            else   -> R.color.default_button
        }
        fabAddRecord.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), fabColor)
    }
}
