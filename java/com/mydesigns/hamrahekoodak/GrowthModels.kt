package com.mydesigns.hamrahekoodak

import java.io.Serializable

/**
 * این کلاس اطلاعات تحلیل شده برای نمایش در کارت وضعیت رشد را نگهداری می‌کند.
 */
data class AnalysisInfo(val metric: String, val value: Float, val unit: String, val whoText: String)

/**
 * این کلاس یک رکورد واحد از اطلاعات رشد کودک (قد، وزن و دور سر) را در یک سن مشخص نشان می‌دهد.
 * این کلاس Serializable است تا بتوان آن را بین فرگمنت‌ها و اکتیویتی‌ها جابجا کرد.
 */
data class GrowthRecord(
    val ageInDays: Long,
    val weight: Float,
    val height: Float,
    val headCirc: Float
) : Serializable
