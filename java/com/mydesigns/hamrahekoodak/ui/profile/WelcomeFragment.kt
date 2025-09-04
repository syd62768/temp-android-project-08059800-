package com.mydesigns.hamrahekoodak.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mydesigns.hamrahekoodak.R
import com.mydesigns.hamrahekoodak.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // انیمیشن ساده برای فلش راهنما
        val bounceAnimation = AnimationUtils.loadAnimation(context, R.anim.bounce)
        binding.ivArrow.startAnimation(bounceAnimation)

        binding.btnCreateProfile.setOnClickListener {
            // هدایت به صفحه ساخت پروفایل
            // مطمئن شوید این action در nav_graph.xml تعریف شده باشد
            findNavController().navigate(R.id.action_welcomeFragment_to_createProfileFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}