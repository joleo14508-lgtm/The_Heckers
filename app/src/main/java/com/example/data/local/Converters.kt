package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.OnionGrade
import com.example.data.model.SizeCategory
import com.example.data.model.VerificationStatus

class Converters {
    @TypeConverter
    fun fromOnionGrade(grade: OnionGrade): String = grade.name

    @TypeConverter
    fun toOnionGrade(value: String): OnionGrade = runCatching {
        OnionGrade.valueOf(value)
    }.getOrDefault(OnionGrade.GRADE_A)

    @TypeConverter
    fun fromSizeCategory(cat: SizeCategory): String = cat.name

    @TypeConverter
    fun toSizeCategory(value: String): SizeCategory = runCatching {
        SizeCategory.valueOf(value)
    }.getOrDefault(SizeCategory.MEDIUM)

    @TypeConverter
    fun fromVerificationStatus(status: VerificationStatus): String = status.name

    @TypeConverter
    fun toVerificationStatus(value: String): VerificationStatus = runCatching {
        VerificationStatus.valueOf(value)
    }.getOrDefault(VerificationStatus.AI_ASSESSED)
}
