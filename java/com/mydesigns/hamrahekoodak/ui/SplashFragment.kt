package com.mydesigns.hamrahekoodak.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mydesigns.hamrahekoodak.R
import com.mydesigns.hamrahekoodak.data.SessionManager
import com.mydesigns.hamrahekoodak.databinding.FragmentSplashBinding // ایمپورت جدید

class SplashFragment : Fragment() {

    // اضافه کردن View Binding برای دسترسی امن به ویوها
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // استفاده از View Binding برای ساخت ویو
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- بخش اضافه شده برای انیمیشن ---
        // 1. بارگذاری انیمیشن از منابع
        val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
        fadeInAnimation.duration = 1200 // افزایش زمان انیمیشن برای ظاهری نرم‌تر

        // 2. اجرای انیمیشن روی متن
        binding.tvAppName.startAnimation(fadeInAnimation)
        // --- پایان بخش انیمیشن ---


        Handler(Looper.getMainLooper()).postDelayed({
            val sessionManager = SessionManager(requireContext())
            if (sessionManager.fetchAuthToken() != null) {
                // User is logged in, navigate to Dashboard
                findNavController().navigate(R.id.action_splashFragment_to_dashboardFragment)
            } else {
                // User is not logged in, navigate to Login
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }, 2000) // 2 ثانیه تاخیر برای نمایش اسپلش
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // جلوگیری از نشت حافظه
        _binding = null
    }
}
