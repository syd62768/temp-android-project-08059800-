package com.mydesigns.hamrahekoodak

import com.github.mikephil.charting.data.Entry

/**
 * مادر و پدر عزیز،
 * این فایل، همراه علمی و مهربان شما برای پایش مسیر رشد دلبندتان است.
 * در اینجا جدیدترین داده‌های استاندارد جهانی از سازمان بهداشت جهانی (WHO) برای بازه تولد تا دو سالگی قرار گرفته است.
 * این معیارها، مرجع رسمی وزارت بهداشت ایران نیز بوده و به شما کمک می‌کند با اطمینان و عشق، بالندگی کودک خود را دنبال کنید.
 * به یاد داشته باشید هر کودک منحصر به فرد است و این نمودارها تنها ابزاری برای راهنمایی هستند.
 */
object GrowthDataWHO {

    // داده‌های صدک‌های کلیدی برای هر سن (به ماه)
    // فرمت: listOf(صدک ۳, صدک ۱۵, صدک ۵۰ (میانه), صدک ۸۵, صدک ۹۷)

    private val boyWeightData = mapOf(
        0 to listOf(2.5f, 2.9f, 3.3f, 3.8f, 4.2f), 1 to listOf(3.4f, 3.9f, 4.5f, 5.1f, 5.6f),
        2 to listOf(4.4f, 5.0f, 5.6f, 6.3f, 7.0f), 3 to listOf(5.1f, 5.7f, 6.4f, 7.2f, 7.9f),
        4 to listOf(5.6f, 6.3f, 7.0f, 7.8f, 8.6f), 5 to listOf(6.1f, 6.8f, 7.5f, 8.4f, 9.3f),
        6 to listOf(6.5f, 7.2f, 7.9f, 8.9f, 9.8f), 7 to listOf(6.8f, 7.6f, 8.3f, 9.3f, 10.3f),
        8 to listOf(7.1f, 7.9f, 8.6f, 9.7f, 10.7f), 9 to listOf(7.3f, 8.2f, 9.0f, 10.0f, 11.0f),
        10 to listOf(7.6f, 8.5f, 9.3f, 10.4f, 11.4f), 11 to listOf(7.8f, 8.7f, 9.6f, 10.7f, 11.8f),
        12 to listOf(8.0f, 8.9f, 9.9f, 11.0f, 12.1f), 13 to listOf(8.2f, 9.2f, 10.1f, 11.3f, 12.5f),
        14 to listOf(8.4f, 9.4f, 10.4f, 11.6f, 12.8f), 15 to listOf(8.6f, 9.6f, 10.6f, 11.8f, 13.1f),
        16 to listOf(8.8f, 9.8f, 10.8f, 12.1f, 13.4f), 17 to listOf(9.0f, 10.0f, 11.1f, 12.4f, 13.7f),
        18 to listOf(9.2f, 10.2f, 11.3f, 12.6f, 14.0f), 19 to listOf(9.4f, 10.4f, 11.5f, 12.9f, 14.3f),
        20 to listOf(9.6f, 10.6f, 11.8f, 13.2f, 14.6f), 21 to listOf(9.8f, 10.9f, 12.0f, 13.4f, 14.9f),
        22 to listOf(10.0f, 11.1f, 12.3f, 13.7f, 15.2f), 23 to listOf(10.2f, 11.3f, 12.5f, 14.0f, 15.5f),
        24 to listOf(10.4f, 11.5f, 12.7f, 14.2f, 15.8f)
    )

    private val girlWeightData = mapOf(
        0 to listOf(2.4f, 2.8f, 3.2f, 3.7f, 4.2f), 1 to listOf(3.2f, 3.6f, 4.2f, 4.8f, 5.3f),
        2 to listOf(3.9f, 4.5f, 5.1f, 5.8f, 6.4f), 3 to listOf(4.5f, 5.1f, 5.8f, 6.6f, 7.2f),
        4 to listOf(5.0f, 5.6f, 6.4f, 7.2f, 7.9f), 5 to listOf(5.4f, 6.1f, 6.9f, 7.7f, 8.5f),
        6 to listOf(5.8f, 6.5f, 7.3f, 8.2f, 9.0f), 7 to listOf(6.1f, 6.8f, 7.6f, 8.6f, 9.5f),
        8 to listOf(6.3f, 7.1f, 7.9f, 8.9f, 9.9f), 9 to listOf(6.6f, 7.3f, 8.2f, 9.2f, 10.2f),
        10 to listOf(6.8f, 7.6f, 8.5f, 9.5f, 10.5f), 11 to listOf(7.0f, 7.8f, 8.7f, 9.8f, 10.8f),
        12 to listOf(7.2f, 8.1f, 9.0f, 10.1f, 11.2f), 13 to listOf(7.4f, 8.3f, 9.2f, 10.4f, 11.5f),
        14 to listOf(7.6f, 8.5f, 9.5f, 10.6f, 11.8f), 15 to listOf(7.8f, 8.7f, 9.7f, 10.9f, 12.1f),
        16 to listOf(8.0f, 8.9f, 10.0f, 11.2f, 12.4f), 17 to listOf(8.2f, 9.2f, 10.2f, 11.4f, 12.7f),
        18 to listOf(8.4f, 9.4f, 10.4f, 11.7f, 13.0f), 19 to listOf(8.6f, 9.6f, 10.7f, 12.0f, 13.3f),
        20 to listOf(8.8f, 9.8f, 10.9f, 12.2f, 13.6f), 21 to listOf(9.0f, 10.0f, 11.2f, 12.5f, 13.9f),
        22 to listOf(9.2f, 10.2f, 11.4f, 12.8f, 14.2f), 23 to listOf(9.4f, 10.5f, 11.6f, 13.1f, 14.5f),
        24 to listOf(9.6f, 10.7f, 11.9f, 13.3f, 14.8f)
    )

    private val boyHeightData = mapOf(
        0 to listOf(46.1f, 47.8f, 49.9f, 52.0f, 53.7f), 1 to listOf(50.8f, 52.8f, 54.7f, 56.7f, 58.6f),
        2 to listOf(54.4f, 56.4f, 58.4f, 60.4f, 62.4f), 3 to listOf(57.3f, 59.2f, 61.4f, 63.7f, 65.5f),
        4 to listOf(59.7f, 61.7f, 63.9f, 66.2f, 68.0f), 5 to listOf(61.7f, 63.8f, 65.9f, 68.2f, 70.1f),
        6 to listOf(63.3f, 65.5f, 67.6f, 70.0f, 71.9f), 7 to listOf(64.8f, 67.0f, 69.2f, 71.5f, 73.5f),
        8 to listOf(66.2f, 68.4f, 70.6f, 73.0f, 75.0f), 9 to listOf(67.5f, 69.7f, 72.0f, 74.4f, 76.5f),
        10 to listOf(68.7f, 71.0f, 73.3f, 75.7f, 77.9f), 11 to listOf(69.9f, 72.2f, 74.5f, 77.0f, 79.2f),
        12 to listOf(71.0f, 73.4f, 75.7f, 78.3f, 80.5f), 13 to listOf(72.1f, 74.5f, 76.9f, 79.5f, 81.8f),
        14 to listOf(73.1f, 75.6f, 78.0f, 80.7f, 83.1f), 15 to listOf(74.1f, 76.6f, 79.1f, 81.8f, 84.2f),
        16 to listOf(75.0f, 77.6f, 80.2f, 82.9f, 85.4f), 17 to listOf(76.0f, 78.6f, 81.2f, 84.0f, 86.5f),
        18 to listOf(76.9f, 79.5f, 82.3f, 85.1f, 87.7f), 19 to listOf(77.7f, 80.4f, 83.2f, 86.1f, 88.8f),
        20 to listOf(78.6f, 81.3f, 84.2f, 87.1f, 89.9f), 21 to listOf(79.4f, 82.1f, 85.1f, 88.0f, 90.9f),
        22 to listOf(80.2f, 83.0f, 86.0f, 89.0f, 91.9f), 23 to listOf(81.0f, 83.8f, 86.9f, 89.9f, 92.9f),
        24 to listOf(81.7f, 84.5f, 87.8f, 90.8f, 93.8f)
    )

    private val girlHeightData = mapOf(
        0 to listOf(45.4f, 47.1f, 49.1f, 51.2f, 52.9f), 1 to listOf(49.8f, 51.7f, 53.7f, 55.6f, 57.4f),
        2 to listOf(53.0f, 55.0f, 57.1f, 59.1f, 60.9f), 3 to listOf(55.6f, 57.5f, 59.8f, 62.1f, 64.0f),
        4 to listOf(57.8f, 59.7f, 62.1f, 64.4f, 66.4f), 5 to listOf(59.6f, 61.7f, 64.0f, 66.5f, 68.5f),
        6 to listOf(61.2f, 63.3f, 65.7f, 68.2f, 70.3f), 7 to listOf(62.7f, 64.9f, 67.3f, 69.9f, 71.9f),
        8 to listOf(64.0f, 66.2f, 68.7f, 71.2f, 73.3f), 9 to listOf(65.3f, 67.5f, 70.1f, 72.6f, 74.7f),
        10 to listOf(66.5f, 68.8f, 71.4f, 73.9f, 76.1f), 11 to listOf(67.7f, 70.0f, 72.7f, 75.3f, 77.5f),
        12 to listOf(68.9f, 71.2f, 74.0f, 76.6f, 78.9f), 13 to listOf(70.0f, 72.3f, 75.2f, 77.8f, 80.2f),
        14 to listOf(71.0f, 73.4f, 76.4f, 79.1f, 81.5f), 15 to listOf(72.0f, 74.5f, 77.5f, 80.2f, 82.7f),
        16 to listOf(73.0f, 75.5f, 78.5f, 81.3f, 83.9f), 17 to listOf(74.0f, 76.5f, 79.6f, 82.4f, 85.0f),
        18 to listOf(74.9f, 77.5f, 80.6f, 83.5f, 86.2f), 19 to listOf(75.8f, 78.4f, 81.6f, 84.5f, 87.3f),
        20 to listOf(76.7f, 79.4f, 82.6f, 85.5f, 88.4f), 21 to listOf(77.5f, 80.3f, 83.5f, 86.5f, 89.4f),
        22 to listOf(78.4f, 81.2f, 84.5f, 87.5f, 90.5f), 23 to listOf(79.2f, 82.0f, 85.3f, 88.4f, 91.5f),
        24 to listOf(80.0f, 82.8f, 86.2f, 89.3f, 92.4f)
    )

    private val boyHeadData = mapOf(
        0 to listOf(32.1f, 33.1f, 34.5f, 35.9f, 36.9f), 1 to listOf(35.1f, 36.2f, 37.6f, 39.0f, 40.0f),
        2 to listOf(37.0f, 38.1f, 39.5f, 40.9f, 41.9f), 3 to listOf(38.3f, 39.3f, 40.8f, 42.2f, 43.3f),
        4 to listOf(39.3f, 40.4f, 41.8f, 43.3f, 44.3f), 5 to listOf(40.1f, 41.2f, 42.6f, 44.1f, 45.2f),
        6 to listOf(40.8f, 41.9f, 43.3f, 44.7f, 45.8f), 7 to listOf(41.4f, 42.5f, 43.9f, 45.4f, 46.4f),
        8 to listOf(41.9f, 43.0f, 44.4f, 45.9f, 47.0f), 9 to listOf(42.3f, 43.5f, 44.9f, 46.4f, 47.5f),
        10 to listOf(42.7f, 43.9f, 45.3f, 46.8f, 47.9f), 11 to listOf(43.1f, 44.2f, 45.7f, 47.1f, 48.3f),
        12 to listOf(43.4f, 44.5f, 46.0f, 47.5f, 48.6f), 13 to listOf(43.7f, 44.8f, 46.3f, 47.8f, 48.9f),
        14 to listOf(43.9f, 45.1f, 46.5f, 48.0f, 49.2f), 15 to listOf(44.2f, 45.3f, 46.8f, 48.3f, 49.4f),
        16 to listOf(44.4f, 45.5f, 47.0f, 48.5f, 49.7f), 17 to listOf(44.6f, 45.8f, 47.2f, 48.7f, 49.9f),
        18 to listOf(44.8f, 46.0f, 47.4f, 49.0f, 50.1f), 19 to listOf(45.0f, 46.2f, 47.6f, 49.2f, 50.3f),
        20 to listOf(45.2f, 46.3f, 47.8f, 49.3f, 50.5f), 21 to listOf(45.3f, 46.5f, 48.0f, 49.5f, 50.7f),
        22 to listOf(45.5f, 46.7f, 48.2f, 49.7f, 50.9f), 23 to listOf(45.6f, 46.8f, 48.3f, 49.9f, 51.1f),
        24 to listOf(45.8f, 47.0f, 48.5f, 50.0f, 51.2f)
    )

    private val girlHeadData = mapOf(
        0 to listOf(31.7f, 32.7f, 33.9f, 35.2f, 36.2f), 1 to listOf(34.2f, 35.2f, 36.5f, 37.9f, 38.9f),
        2 to listOf(35.9f, 37.0f, 38.3f, 39.7f, 40.7f), 3 to listOf(37.2f, 38.2f, 39.8f, 41.2f, 42.3f),
        4 to listOf(38.2f, 39.3f, 40.7f, 42.1f, 43.2f), 5 to listOf(39.0f, 40.1f, 41.5f, 42.9f, 44.0f),
        6 to listOf(39.8f, 40.9f, 42.2f, 43.6f, 44.7f), 7 to listOf(40.4f, 41.5f, 42.8f, 44.2f, 45.4f),
        8 to listOf(40.9f, 42.0f, 43.4f, 44.8f, 45.9f), 9 to listOf(41.4f, 42.5f, 43.9f, 45.3f, 46.4f),
        10 to listOf(41.8f, 42.9f, 44.3f, 45.7f, 46.8f), 11 to listOf(42.2f, 43.3f, 44.7f, 46.1f, 47.2f),
        12 to listOf(42.5f, 43.6f, 45.0f, 46.4f, 47.6f), 13 to listOf(42.8f, 43.9f, 45.3f, 46.8f, 47.9f),
        14 to listOf(43.1f, 44.2f, 45.6f, 47.1f, 48.2f), 15 to listOf(43.3f, 44.5f, 45.9f, 47.3f, 48.5f),
        16 to listOf(43.5f, 44.7f, 46.1f, 47.6f, 48.7f), 17 to listOf(43.8f, 44.9f, 46.3f, 47.8f, 49.0f),
        18 to listOf(44.0f, 45.1f, 46.5f, 48.0f, 49.2f), 19 to listOf(44.2f, 45.3f, 46.7f, 48.2f, 49.4f),
        20 to listOf(44.3f, 45.5f, 46.9f, 48.4f, 49.6f), 21 to listOf(44.5f, 45.7f, 47.1f, 48.6f, 49.8f),
        22 to listOf(44.7f, 45.8f, 47.3f, 48.8f, 50.0f), 23 to listOf(44.8f, 46.0f, 47.4f, 48.9f, 50.1f),
        24 to listOf(45.0f, 46.1f, 47.5f, 49.0f, 50.3f)
    )

    /**
     * این تابع نقاط داده‌ی یک صدک مشخص را برای رسم منحنی در نمودار برمی‌گرداند.
     */
    fun getPercentileEntries(gender: String?, metric: String, percentileIndex: Int): List<Entry> {
        val data = when (metric) {
            "weight" -> if (gender == "پسر") boyWeightData else girlWeightData
            "height" -> if (gender == "پسر") boyHeightData else girlHeightData
            else -> if (gender == "پسر") boyHeadData else girlHeadData
        }
        val entries = mutableListOf<Entry>()
        for (age in data.keys.sorted()) {
            entries.add(Entry(age.toFloat(), data[age]!![percentileIndex]))
        }
        return entries
    }

    /**
     * این تابع محدوده‌های استاندارد (صدک‌ها) را برای یک سن مشخص برمی‌گرداند
     * تا وضعیت رشد کودک (قرمز، زرد یا سبز) مشخص شود.
     */
    fun getPercentilesForAge(gender: String?, metric: String, ageInMonths: Int): List<Float>? {
        val data = when (metric) {
            "weight" -> if (gender == "پسر") boyWeightData else girlWeightData
            "height" -> if (gender == "پسر") boyHeightData else girlHeightData
            else -> if (gender == "پسر") boyHeadData else girlHeadData
        }
        // با توجه به اینکه داده‌ها برای تمام ماه‌ها موجود است، مستقیماً به آن دسترسی پیدا می‌کنیم.
        return data[ageInMonths]
    }
}
