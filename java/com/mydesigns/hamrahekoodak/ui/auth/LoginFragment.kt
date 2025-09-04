package com.mydesigns.hamrahekoodak.ui.auth

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mydesigns.hamrahekoodak.R
import com.mydesigns.hamrahekoodak.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up_fade_in)
        binding.llContentSection.startAnimation(animation)

        setupPhoneNumberValidation()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupPhoneNumberValidation() {
        binding.etPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    triggerHapticFeedback()
                }

                val phoneNumber = s.toString()
                // تغییر اصلی در اینجا اعمال شده است
                // اکنون پس‌زمینه‌ها به درستی بر اساس اعتبار شماره تنظیم می‌شوند
                if (phoneNumber.isEmpty()) {
                    // حالت عادی با دور کادر خاکستری
                    binding.phoneInputContainer.setBackgroundResource(R.drawable.phone_input_bg_normal)
                } else if (isValidPhoneNumber(phoneNumber)) {
                    // حالت موفق با دور کادر سبز
                    binding.phoneInputContainer.setBackgroundResource(R.drawable.phone_input_bg_success)
                } else {
                    // حالت خطا با دور کادر قرمز
                    binding.phoneInputContainer.setBackgroundResource(R.drawable.phone_input_bg_error)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListeners() {
        binding.btnSendCode.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            if (isValidPhoneNumber(phoneNumber)) {
                viewModel.requestOtp(phoneNumber)
            } else {
                Toast.makeText(requireContext(), "شماره موبایل معتبر وارد کنید", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.otpRequestStatus.observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe

            when (result) {
                is Result.Loading -> setLoadingState(true)
                is Result.Success -> {
                    setLoadingState(false)
                    Toast.makeText(requireContext(), "کد تایید ارسال شد (کد تست: 1234)", Toast.LENGTH_LONG).show()
                    val action = LoginFragmentDirections.actionLoginFragmentToVerifyOtpFragment(
                        phoneNumber = binding.etPhoneNumber.text.toString().trim()
                    )
                    findNavController().navigate(action)
                }
                is Result.Error -> {
                    setLoadingState(false)
                    Toast.makeText(requireContext(), "خطا: ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.btnSendCode.isEnabled = !isLoading
        binding.etPhoneNumber.isEnabled = !isLoading

        val targetAlpha = if (isLoading) 0.4f else 1.0f
        binding.llContentSection.animate().alpha(targetAlpha).setDuration(300).start()
        binding.ivBackgroundIllustration.animate().alpha(targetAlpha).setDuration(300).start()
    }

    private fun isValidPhoneNumber(number: String): Boolean {
        return number.length == 11 && number.startsWith("09")
    }

    private fun triggerHapticFeedback() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (vibrator?.hasVibrator() == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetStates()
        _binding = null
    }
}
