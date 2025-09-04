package com.mydesigns.hamrahekoodak.ui.dashboard

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import com.mydesigns.hamrahekoodak.GrowthNutritionActivity
import com.mydesigns.hamrahekoodak.PersianCalendarHelper
import com.mydesigns.hamrahekoodak.R
import com.mydesigns.hamrahekoodak.data.SessionManager
import com.mydesigns.hamrahekoodak.databinding.FragmentDashboardBinding
import com.mydesigns.hamrahekoodak.databinding.DialogGrowthBudBinding
import com.mydesigns.hamrahekoodak.ui.auth.Result
import java.util.Calendar

class DashboardFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarAndDrawer()
        observeViewModel()
        setupDailyMessage()
        setupClickListeners() // تابع جدید برای مدیریت کلیک‌ها

        // ===== استایل دهی نهایی کارت نکته روز در کد =====
        binding.wonderCard.setCardBackgroundColor(Color.TRANSPARENT)
        binding.wonderCard.setBackgroundResource(R.drawable.dashboard_wonder_card_bg)
        binding.tvWonderText.setTextColor(ContextCompat.getColor(requireContext(), R.color.wonder_card_gold_text))
        binding.ivWonderIcon.colorFilter = null
    }

    private fun setupToolbarAndDrawer() {
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
            activity, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(requireContext(), R.color.primary_text_color)
        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> { /* Handle loading state */ }
                is Result.Success -> {
                    val state = result.data
                    binding.tvChildName.text = state.childName
                    binding.tvChildAge.text = state.childAge

                    val avatarResId = if ("GIRL".equals(state.gender, ignoreCase = true)) {
                        R.drawable.avatar_girl
                    } else {
                        R.drawable.avatar_boy
                    }
                    binding.ivAvatar.setImageResource(avatarResId)

                    val headerView = binding.navView.getHeaderView(0)
                    headerView.findViewById<TextView>(R.id.tv_header_child_name).text = state.childName
                    headerView.findViewById<TextView>(R.id.tv_header_child_age).text = state.childAge

                    val stageTitle = getGrowthStageTitle(state.birthDate)
                    val highlightMessage = getDevelopmentalHighlight(state.birthDate, state.childName)

                    binding.tvGrowthBudTitle.text = stageTitle
                    binding.growthBudContainer.setOnClickListener {
                        showGrowthBudDialog(highlightMessage)
                    }

                    setupGlowingTextAnimation()
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ==================== CHANGE START ====================
    // منطق کلیک کارت رشد و تغذیه به این تابع منتقل شد
    private fun setupClickListeners() {
        binding.cardGrowthNutrition.setOnClickListener {
            // دریافت اطلاعات کاربر از ViewModel
            val currentState = viewModel.uiState.value
            if (currentState is Result.Success) {
                val userData = currentState.data
                val intent = Intent(activity, GrowthNutritionActivity::class.java).apply {
                    putExtra("CHILD_NAME", userData.childName)
                    putExtra("GENDER", userData.gender)
                    putExtra("BIRTH_DATE", userData.birthDate)
                }
                startActivity(intent)
            } else {
                // اگر هنوز داده‌ها لود نشده‌اند، یک پیام نمایش بده
                Toast.makeText(context, "لطفا کمی صبر کنید...", Toast.LENGTH_SHORT).show()
            }
        }

        // می‌توانید کلیک‌های دیگر را هم در آینده اینجا اضافه کنید
    }
    // ===================== CHANGE END =====================

    private fun setupDailyMessage() {
        val messages = resources.getStringArray(R.array.daily_messages)
        if (messages.isNotEmpty()) {
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val messageIndex = dayOfYear % messages.size
            binding.tvWonderText.text = messages[messageIndex]
        }
    }


    private fun getGrowthStageTitle(birthDate: String?): String {
        if (birthDate.isNullOrBlank()) return "باغچه رشد"
        val ageInDays = PersianCalendarHelper.getAgeInDays(birthDate)
        return when {
            ageInDays < 365 -> "غنچه رشد"
            ageInDays in 365..1095 -> "نهال رشد"
            else -> "درخت رشد"
        }
    }

    private fun getDevelopmentalHighlight(birthDate: String?, childName: String?): String {
        if (birthDate.isNullOrBlank() || childName.isNullOrBlank()) {
            return "هر روز یک شگفتی تازه در راه است."
        }
        val ageInDays = PersianCalendarHelper.getAgeInDays(birthDate)
        val months = ageInDays / 30

        return when {
            months < 2 -> "$childName عزیز در حال شناختن دنیای اطراف با چشم‌های کنجکاو خود است."
            months < 4 -> "به زودی منتظر اولین لبخندهای شیرین و اجتماعی $childName باشید!"
            months < 7 -> "آیا می‌دانستید $childName در این سن شروع به غلت زدن و کشف دنیای جدیدش می‌کند؟"
            months < 10 -> "$childName کم کم برای نشستن بدون کمک آماده می‌شود. تشویقش کنید!"
            months < 12 -> "مرحله هیجان‌انگیز چهار دست و پا رفتن نزدیک است. خانه را برای ماجراجویی‌های $childName امن کنید!"
            months < 18 -> "اولین قدم‌ها در راهند! $childName به زودی راه رفتن را شروع می‌کند و استقلالش را جشن می‌گیرد."
            months < 24 -> "دایره لغات $childName در حال گسترش است. با او زیاد صحبت کنید و برایش کتاب بخوانید."
            months < 36 -> "$childName حالا می‌تواند بدود و از پله‌ها بالا برود. انرژی بی‌پایانش را تحسین کنید!"
            else -> "خلاقیت $childName در حال شکوفایی است. با بازی و نقاشی به رشد او کمک کنید."
        }
    }

    private fun showGrowthBudDialog(milestone: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogBinding = DialogGrowthBudBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        val titleTextView = dialogBinding.root.findViewById<TextView>(R.id.tv_dialog_title)
        titleTextView.text = "شکوفایی فرشته زندگی" // عنوان ثابت

        dialogBinding.tvMilestone.text = milestone

        dialogBinding.btnAwesome.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        dialog.show()
    }

    private fun setupGlowingTextAnimation() {
        val textView = binding.tvChildName
        val shadowColor = Color.WHITE

        textView.setShadowLayer(0f, 0f, 0f, shadowColor)
        val glowAnimator = ObjectAnimator.ofFloat(textView, "shadowRadius", 0f, 20f, 0f)
        glowAnimator.duration = 3000
        glowAnimator.repeatMode = ObjectAnimator.REVERSE
        glowAnimator.repeatCount = ObjectAnimator.INFINITE
        glowAnimator.interpolator = AccelerateDecelerateInterpolator()
        glowAnimator.start()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> Toast.makeText(context, "پروفایل", Toast.LENGTH_SHORT).show()
            R.id.nav_logout -> {
                SessionManager(requireContext()).clearSession()
                findNavController().navigate(R.id.action_dashboardFragment_to_splashFragment)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

