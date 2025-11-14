package com.love.compatibility.core.helper

import android.annotation.SuppressLint
import com.love.compatibility.data.model.status.Quadruple
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.*

object DateHelper {
    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(Date())
    }

    // Trả về số ngày từ date (millis) đến ngày hiện tại
    fun daysFrom(date: Long): Long {
        val now = System.currentTimeMillis()
        val diff = now - date
        return TimeUnit.MILLISECONDS.toDays(diff)
    }

    //Format date (millis) thành MM.dd.yyyy
    fun formatDot(date: Long): String {
        val sdf = SimpleDateFormat("MM.dd.yyyy", Locale.getDefault())
        return sdf.format(Date(date))
    }

    // Format date (millis) thành MM/dd/yyyy
    fun formatSlash(date: Long): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return sdf.format(Date(date))
    }

    fun diffExactYearMonthWeekDay(date: Long): Quadruple<Int, Int, Int, Int> {

        val now = System.currentTimeMillis()

        // Nếu date > hiện tại thì trả về 0 hết
        if (date > now) {
            return Quadruple(0, 0, 0, 0)
        }

        val startCal = Calendar.getInstance().apply { timeInMillis = date }
        val nowCal = Calendar.getInstance()

        var years = nowCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)
        var months = nowCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH)
        var days = nowCal.get(Calendar.DAY_OF_MONTH) - startCal.get(Calendar.DAY_OF_MONTH)

        // Điều chỉnh nếu ngày âm
        if (days < 0) {
            months -= 1
            val tempCal = Calendar.getInstance().apply {
                timeInMillis = nowCal.timeInMillis
                add(Calendar.MONTH, -1)
            }
            val lastMonthDays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            days = lastMonthDays + days
        }

        // Điều chỉnh nếu tháng âm
        if (months < 0) {
            years -= 1
            months += 12
        }

        // Tách tuần và ngày
        val weeks = days / 7
        val remainingDays = days % 7

        // Đảm bảo không âm
        return Quadruple(
            maxOf(years, 0),
            maxOf(months, 0),
            maxOf(weeks, 0),
            maxOf(remainingDays, 0)
        )
    }

    fun checkDateValid(date: Long): Boolean {
        val now = System.currentTimeMillis()
        return now < date
    }

    fun formatLongToDate(timeInMillis: Long, pattern: String = "MM/dd/yyyy"): String {
        val sdf = SimpleDateFormat(pattern, Locale.US)
        return sdf.format(Date(timeInMillis))
    }

    fun calculateYearsFromLong(pastTimeInMillis: Long): Int {
        val today = Calendar.getInstance()
        val past = Calendar.getInstance().apply { timeInMillis = pastTimeInMillis }

        var years = today.get(Calendar.YEAR) - past.get(Calendar.YEAR)

        // Nếu ngày tháng hiện tại chưa tới sinh nhật trong năm thì trừ đi 1
        if (today.get(Calendar.DAY_OF_YEAR) < past.get(Calendar.DAY_OF_YEAR)) {
            years--
        }

        return years
    }

}