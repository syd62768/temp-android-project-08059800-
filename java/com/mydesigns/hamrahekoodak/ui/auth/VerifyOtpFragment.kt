package com.mydesigns.hamrahekoodak.ui.auth

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mydesigns.hamrahekoodak.R
import com.mydesigns.hamrahekoodak.data.SessionManager
import com.mydesigns.hamrahekoodak.databinding.FragmentVerifyOtpBinding
import java.util.concurrent.TimeUnit

class VerifyOtpFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private var _binding: FragmentVerifyOtpBinding? = null
    private val binding get() = _binding!!
    private val args: VerifyOtpFragmentArgs by navArgs()
    private var timer: CountDownTimer? = null
    private val countdownDuration = TimeUnit.MINUTES.toMillis(3)
    private val countdownInterval = 1000L
    private lateinit var otpFields: List<EditText>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVerifyOtpBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSubtitle.text = "کد ۴ رقمی ارسال شده به شماره\n${args.phoneNumber} را وارد کنید."

        otpFields = listOf(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)
        setupOtpInput()

        setupClickListeners()
        observeViewModel()
        startTimer()
    }

    private fun setupOtpInput() {
        otpFields.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // با شروع تایپ، حالت خطا را پاک کن
                    if (s?.isNotEmpty() == true) {
                        resetOtpBoxesBackground()
                    }
                    if (s?.length == 1 && index < otpFields.size - 1) {
                        otpFields[index + 1].requestFocus()
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && event.action == android.view.KeyEvent.ACTION_DOWN) {
                    if (editText.text.isEmpty() && index > 0) {
                        otpFields[index - 1].requestFocus()
                    }
                }
                false
            }
        }
    }


    private fun setupClickListeners() {
        binding.btnVerify.setOnClickListener {
            val otp = otpFields.joinToString("") { it.text.toString() }
            if (otp.length == 4) {
                viewModel.verifyOtp(args.phoneNumber, otp)
            }
        }
        binding.tvResendCode.setOnClickListener {
            viewModel.requestOtp(args.phoneNumber)
        }
    }

    private fun observeViewModel() {
        viewModel.verifyStatus.observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe

            when (result) {
                is Result.Loading -> setLoadingState(true)
                is Result.Success -> {
                    setLoadingState(false)
                    setOtpBoxesBackground(R.drawable.otp_box_underline_success) // سبز کردن باکس‌ها
                    // تاخیر کوتاه برای نمایش حالت موفقیت قبل از انتقال
                    Handler(Looper.getMainLooper()).postDelayed({
                        val sessionManager = SessionManager(requireContext())
                        sessionManager.saveAuthToken(result.data.token)
                        sessionManager.saveUserProfile(result.data.user)

                        if (result.data.isNewUser) {
                            findNavController().navigate(VerifyOtpFragmentDirections.actionVerifyOtpFragmentToWelcomeFragment())
                        } else {
                            findNavController().navigate(VerifyOtpFragmentDirections.actionVerifyOtpFragmentToDashboardFragment())
                        }
                    }, 500) // 500 میلی‌ثانیه تاخیر
                }
                is Result.Error -> {
                    setLoadingState(false)
                    setOtpBoxesBackground(R.drawable.otp_box_underline_error) // قرمز کردن باکس‌ها
                }
            }
        }

        viewModel.otpRequestStatus.observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe
            when (result) {
                is Result.Loading -> setLoadingState(true, isResend = true)
                is Result.Success -> {
                    setLoadingState(false)
                    startTimer()
                }
                is Result.Error -> {
                    setLoadingState(false)
                }
            }
        }
    }

    private fun startTimer() {
        binding.tvResendCode.isVisible = false
        binding.tvTimer.isVisible = true
        timer?.cancel()
        timer = object : CountDownTimer(countdownDuration, countdownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(minutes)
                binding.tvTimer.text = String.format("ارسال مجدد تا %02d:%02d", minutes, seconds)
            }
            override fun onFinish() {
                binding.tvTimer.isVisible = false
                binding.tvResendCode.isVisible = true
            }
        }.start()
    }

    private fun setLoadingState(isLoading: Boolean, isResend: Boolean = false) {
        binding.progressBar.isVisible = isLoading
        if (!isResend) {
            binding.btnVerify.isEnabled = !isLoading
            otpFields.forEach { it.isEnabled = !isLoading }
        }
        binding.tvResendCode.isEnabled = !isLoading
    }

    private fun setOtpBoxesBackground(drawableResId: Int) {
        otpFields.forEach {
            it.background = ContextCompat.getDrawable(requireContext(), drawableResId)
        }
    }

    private fun resetOtpBoxesBackground() {
        otpFields.forEach {
            it.background = ContextCompat.getDrawable(requireContext(), R.drawable.otp_box_underline_background)
        }
        // فوکوس را به اولین باکسی که خالی است منتقل کن
        val firstEmpty = otpFields.firstOrNull { it.text.isEmpty() }
        firstEmpty?.requestFocus() ?: otpFields.last().requestFocus()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        viewModel.resetStates()
        _binding = null
    }
}
