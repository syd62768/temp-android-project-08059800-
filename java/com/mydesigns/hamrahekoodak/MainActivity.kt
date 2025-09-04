package com.mydesigns.hamrahekoodak

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.mydesigns.hamrahekoodak.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // استفاده از View Binding برای اتصال layout به اکتیویتی
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // پیدا کردن NavHostFragment. تمام منطق ناوبری در nav_graph.xml مدیریت می‌شود.
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // MainActivity دیگر نیازی به کدهای مربوط به ساخت پروفایل ندارد.
        // آن کدها باید به فرگمنت مربوطه منتقل شوند.
    }
}