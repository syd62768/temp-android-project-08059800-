package com.mydesigns.hamrahekoodak

import java.util.Calendar
import java.util.GregorianCalendar

object PersianCalendarHelper {

    // --- توابع کمکی ---

    private fun isPersianLeap(year: Int): Boolean {
        val pattern = intArrayOf(1, 5, 9, 13, 17, 22, 26, 30)
        return pattern.contains(year % 33)
    }

    private fun daysInPersianMonth(year: Int, month: Int): Int {
        return when {
            month in 1..6 -> 31
            month in 7..11 -> 30
            else -> if (isPersianLeap(year)) 30 else 29
        }
    }

    private fun gregorianToPersian(g_y: Int, g_m: Int, g_d: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy = g_y - 1600
        val gm = g_m - 1
        val gd = g_d - 1

        var gDayNo = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400

        for (i in 0 until gm) {
            gDayNo += gDaysInMonth[i]
        }
        if (gm > 1 && (g_y % 4 == 0 && g_y % 100 != 0 || g_y % 400 == 0)) {
            gDayNo++
        }
        gDayNo += gd

        var jDayNo = gDayNo - 79

        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var i = 0
        while (i < 11 && jDayNo >= jDaysInMonth[i]) {
            jDayNo -= jDaysInMonth[i]
            i++
        }
        val jm = i + 1
        val jd = jDayNo + 1

        return Triple(jy, jm, jd)
    }


    fun getTodayPersian(): Triple<Int, Int, Int> {
        val cal = GregorianCalendar.getInstance()
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)
        return gregorianToPersian(gy, gm, gd)
    }

    fun getAgeInDays(birthDate: String): Long {
        if (birthDate.isBlank()) return 0L

        val parts = birthDate.split('/').mapNotNull { it.toIntOrNull() }
        if (parts.size != 3) return 0L

        val (by, bm, bd) = parts
        val (ty, tm, td) = getTodayPersian()

        if (by > ty || (by == ty && bm > tm) || (by == ty && bm == tm && bd > td)) {
            return 0L
        }

        fun daysSinceEpoch(year: Int, month: Int, day: Int): Long {
            var totalDays = 0L
            for (y in 1 until year) {
                totalDays += if (isPersianLeap(y)) 366 else 365
            }
            for (m in 1 until month) {
                totalDays += daysInPersianMonth(year, m)
            }
            totalDays += day
            return totalDays
        }

        val birthDaysSinceEpoch = daysSinceEpoch(by, bm, bd)
        val todayDaysSinceEpoch = daysSinceEpoch(ty, tm, td)

        return todayDaysSinceEpoch - birthDaysSinceEpoch
    }


    data class Age(val years: Int, val months: Int, val days: Int)

    fun calculateAge(birthDate: String): Age? {
        if (birthDate.isBlank()) return null

        val parts = birthDate.split('/').mapNotNull { it.toIntOrNull() }
        if (parts.size != 3) return null

        val (by, bm, bd) = parts
        val (ty, tm, td) = getTodayPersian()

        if (by > ty || (by == ty && bm > tm) || (by == ty && bm == tm && bd > td)) {
            return Age(0, 0, 0)
        }

        var y = ty - by
        var m = tm - bm
        var d = td - bd

        if (d < 0) {
            m--
            val prevMonth = if (tm - 1 >= 1) tm - 1 else 12
            val yearOfPrev = if (tm - 1 >= 1) ty else ty - 1
            d += daysInPersianMonth(yearOfPrev, prevMonth)
        }

        if (m < 0) {
            m += 12
            y--
        }

        return Age(y, m, d)
    }

    fun getAgeText(birthDate: String): String {
        val age = calculateAge(birthDate) ?: return "تاریخ نامعتبر"
        return formatAge(age.years, age.months, age.days)
    }

    /**
     * **تابع جدید:** این تابع سن را از روی تعداد کل روزها محاسبه کرده و به متن تبدیل می‌کند.
     * این تابع برای استفاده در انیمیشن سن اضافه شده است.
     */
    fun getAgeTextFromDays(totalDaysInput: Long): String {
        if (totalDaysInput < 0) return "تاریخ نامعتبر"
        val age = convertDaysToAge(totalDaysInput)
        return formatAge(age.years, age.months, age.days)
    }

    /**
     * **تابع جدید:** این تابع تعداد کل روزها را به صورت دقیق به سال، ماه و روز تبدیل می‌کند.
     */
    private fun convertDaysToAge(totalDaysInput: Long): Age {
        if (totalDaysInput == 0L) return Age(0, 0, 0)
        var totalDays = totalDaysInput
        var year = 1
        // محاسبه سال
        while (totalDays >= (if (isPersianLeap(year)) 366 else 365)) {
            totalDays -= (if (isPersianLeap(year)) 366 else 365)
            year++
        }
        val finalYear = year - 1

        // محاسبه ماه
        var month = 1
        while (totalDays >= daysInPersianMonth(year, month)) {
            totalDays -= daysInPersianMonth(year, month)
            month++
        }

        return Age(finalYear, month - 1, totalDays.toInt())
    }

    /**
     * یک تابع کمکی خصوصی برای فرمت کردن متن خروجی سن تا از تکرار کد جلوگیری شود.
     * این نسخه اصلاح شده تا همیشه روز را به درستی نمایش دهد.
     */
    private fun formatAge(y: Int, m: Int, d: Int): String {
        if (y == 0 && m == 0 && d == 0) return "امروز به دنیا اومده!"

        val parts = mutableListOf<String>()
        if (y > 0) parts.add("$y سال")
        if (m > 0) parts.add("$m ماه")
        if (d > 0) {
            if (y == 0 && m == 0) {
                return "$d روزه"
            }
            parts.add("$d روز")
        }

        return parts.joinToString(" و ")
    }
}
