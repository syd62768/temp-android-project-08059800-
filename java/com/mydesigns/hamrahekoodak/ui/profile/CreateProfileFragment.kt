package com.mydesigns.hamrahekoodak.ui.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mydesigns.hamrahekoodak.R
import com.mydesigns.hamrahekoodak.databinding.FragmentCreateProfileBinding
import com.mydesigns.hamrahekoodak.ui.auth.Result
import java.text.DecimalFormat

class CreateProfileFragment : Fragment() {

    private var _binding: FragmentCreateProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    private enum class Gender { BOY, GIRL }
    private var selectedGender: Gender? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
        updateTheme(null) // Set default theme initially
    }

    private fun setupListeners() {
        binding.etBirthDate.setOnClickListener { showSimplePersianDatePicker() }
        binding.buttonSave.setOnClickListener { saveProfile() }

        binding.radioGroupGender.setOnCheckedChangeListener { _, checkedId ->
            selectedGender = when (checkedId) {
                R.id.radioButtonGirl -> Gender.GIRL
                R.id.radioButtonBoy -> Gender.BOY
                else -> null
            }
            updateTheme(selectedGender)
        }

        binding.etBirthWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val grams = s.toString().toDoubleOrNull()
                if (grams != null) {
                    val kg = grams / 1000.0
                    binding.tilBirthWeight.helperText = "معادل: ${DecimalFormat("#.##").format(kg)} کیلوگرم"
                } else {
                    binding.tilBirthWeight.helperText = "مثال: 3200"
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.updateStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> setLoadingState(true)
                is Result.Success -> {
                    setLoadingState(false)
                    Toast.makeText(requireContext(), "پروفایل ${result.data.name} با موفقیت ساخته شد!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_createProfileFragment_to_dashboardFragment)
                }
                is Result.Error -> {
                    setLoadingState(false)
                    Toast.makeText(requireContext(), "خطا: ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveProfile() {
        val childName = binding.etChildName.text.toString().trim()
        val birthDate = binding.etBirthDate.text.toString().trim()
        val birthWeight = binding.etBirthWeight.text.toString().toIntOrNull()

        if (childName.isBlank()) {
            binding.tilChildName.error = "نام کودک نمی‌تواند خالی باشد"
            return
        } else {
            binding.tilChildName.error = null
        }

        if (birthDate.isBlank()) {
            binding.tilBirthDate.error = "تاریخ تولد را انتخاب کنید"
            return
        } else {
            binding.tilBirthDate.error = null
        }

        if (selectedGender == null) {
            Toast.makeText(requireContext(), "لطفاً جنسیت را انتخاب کنید!", Toast.LENGTH_LONG).show()
            return
        }

        viewModel.updateUserProfile(childName, birthDate, selectedGender!!.name, birthWeight)
    }

    private fun updateTheme(gender: Gender?) {
        val (bgColor, statusBarColor, buttonDrawable) = when (gender) {
            Gender.GIRL -> Triple(R.color.girl_background_light, R.color.girl_colorPrimaryDark, R.drawable.button_background_girl)
            Gender.BOY -> Triple(R.color.boy_background_light, R.color.boy_colorPrimaryDark, R.drawable.button_background_boy)
            else -> Triple(R.color.default_background_light, R.color.default_colorPrimaryDark, R.drawable.button_background_default)
        }
        binding.rootLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), bgColor))
        binding.buttonSave.setBackgroundResource(buttonDrawable)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), statusBarColor)
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.buttonSave.isEnabled = !isLoading
        binding.etChildName.isEnabled = !isLoading
        binding.etBirthDate.isEnabled = !isLoading
        binding.etBirthWeight.isEnabled = !isLoading
        // Disabling radio buttons individually
        binding.radioButtonBoy.isEnabled = !isLoading
        binding.radioButtonGirl.isEnabled = !isLoading
    }

    private fun showSimplePersianDatePicker() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_persian_date_picker, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        val yearPicker = dialogView.findViewById<NumberPicker>(R.id.picker_year)
        val monthPicker = dialogView.findViewById<NumberPicker>(R.id.picker_month)
        val dayPicker = dialogView.findViewById<NumberPicker>(R.id.picker_day)
        val confirmButton = dialogView.findViewById<Button>(R.id.button_confirm_date)

        val currentYear = 1403
        yearPicker.minValue = 1380
        yearPicker.maxValue = 1410
        yearPicker.value = currentYear

        val months = arrayOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند")
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.displayedValues = months

        dayPicker.minValue = 1
        dayPicker.maxValue = 31

        confirmButton.setOnClickListener {
            val selectedDate = "%04d/%02d/%02d".format(yearPicker.value, monthPicker.value, dayPicker.value)
            binding.etBirthDate.setText(selectedDate)
            binding.tilBirthDate.error = null
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}