package com.support.dateClass

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*





object DateUtils {

    val dd_mm_yyy: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    val hh_mm_a: SimpleDateFormat = SimpleDateFormat("hh:mm a")
    val yyyy_MM_dd_T_HH_mm_ss: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    /*Pass in long as @dateInLong
        returns date to be displayed as Time / Yesterday / Date
    */
    fun getDateStringForFcsScreen(dateInLong: Long): String {

        if (DateUtils.isToday(dateInLong)) {
            return hh_mm_a.format(Date(dateInLong))
        } else if (isDateYesterday(dateInLong)) {
            return "Yesterday"
        } else
            return dd_mm_yyy.format(Date(dateInLong))
    }


    fun isDateYesterday(dateInLong: Long): Boolean {

        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)

        val givenDate = Calendar.getInstance()
        givenDate.time = Date(dateInLong)

        return yesterday.get(Calendar.YEAR) == givenDate.get(Calendar.YEAR) && yesterday.get(Calendar.DAY_OF_YEAR) == givenDate.get(
            Calendar.DAY_OF_YEAR
        )
    }

    fun parseDateInFormat(date: String): Date? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val timeZone = Calendar.getInstance().timeZone.id
        val newDate = spf.parse(date)
        spf = SimpleDateFormat("MM-dd-yyyy hh:mm:ss a")
        return Date(newDate.time + TimeZone.getTimeZone(timeZone).getOffset(newDate.time))
    }
    fun parseDateOnlyDateForDisplay(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        var newDate = spf.parse(date)
        val timeZone = Calendar.getInstance().timeZone.id
        newDate = Date(newDate.time + TimeZone.getTimeZone(timeZone).getOffset(newDate.time))
        spf = SimpleDateFormat("MM-dd-yyyy")
        var date = spf.format(newDate)
        return date;
    }
    fun parseDateOnlyForDisplay(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        var newDate = spf.parse(date)
        val timeZone = Calendar.getInstance().timeZone.id
        newDate = Date(newDate.time + TimeZone.getTimeZone(timeZone).getOffset(newDate.time))
        spf = SimpleDateFormat("MMM-dd-yyyy")
        var date = spf.format(newDate)
        return date;
    }

    fun parseDateOnlyForData(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        var newDate = spf.parse(date)
        val timeZone = Calendar.getInstance().timeZone.id
        newDate = Date(newDate.time + TimeZone.getTimeZone(timeZone).getOffset(newDate.time))
        spf = SimpleDateFormat("MMM-dd-yyyy")
        var date = spf.format(newDate)
        return date;
    }

    fun parseDateOnlyforSendAPI(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val newDate = spf.parse(date)
        spf = SimpleDateFormat("yyyy-MM-dd")
        var date = spf.format(newDate)
        return date;
    }

    fun parseOnlyTime12hours(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        var newDate = spf.parse(date)
        val timeZone = Calendar.getInstance().timeZone.id
        newDate = Date(newDate.time + TimeZone.getTimeZone(timeZone).getOffset(newDate.time))
        spf = SimpleDateFormat("hh:mm:ss a")
        var date = spf.format(newDate)
        return date;
    }

    fun parseOnlyTime24Hours(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        var newDate = spf.parse(date)
        val timeZone = Calendar.getInstance().timeZone.id
        newDate = Date(newDate.time + TimeZone.getTimeZone(timeZone).getOffset(newDate.time))
        spf = SimpleDateFormat("HH:mm:ss")
        var date = spf.format(newDate)
        return date;
    }

    fun parseOnlyTime24HoursToUTC(date: String): String? {
        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val newDate = sdf.parse(date)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(newDate)
    }

    fun parseOnlyDayFromUTC(date: String): String? {
        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val newDate = sdf.parse(date)
        val dateLocal = Date(newDate.time + TimeZone.getTimeZone("UTC").getOffset(newDate.time))
        sdf = SimpleDateFormat("EEE")
        return sdf.format(dateLocal)
    }

    fun parseOnlyDay(date: String): String? {
        var spf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        var newDate = spf.parse(date)
        val timeZone = Calendar.getInstance().timeZone.id
        newDate = Date(newDate.time + TimeZone.getTimeZone(timeZone).getOffset(newDate.time))
        spf = SimpleDateFormat("EEE")
        var date = spf.format(newDate)
        return date;
    }


    fun parseDateOnlyForDisplayFromUTC(date: String): String? {
        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val newDate = sdf.parse(date)
        val dateLocal = Date(newDate.time + TimeZone.getTimeZone("UTC").getOffset(newDate.time))
        sdf = SimpleDateFormat("MMM-dd-yyyy")
        var date = sdf.format(newDate)
        return date;
    }


    fun parseOnlyTime12hoursFromUTC(date: String): String? {
        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val newDate = sdf.parse(date)
        val dateLocal = Date(newDate.time + TimeZone.getTimeZone("UTC").getOffset(newDate.time))
        sdf = SimpleDateFormat("hh:mm:ss a")
        var date = sdf.format(newDate)
        return date;
    }

    fun parseDate(Date: String, CurrentPattern: String, OutputPattern: String): String {
        val sdf = SimpleDateFormat(CurrentPattern, Locale.getDefault())
        try {
            val startDate = sdf.parse(Date)
            sdf.applyPattern(OutputPattern)
            return sdf.format(startDate)
        } catch (e: Exception) {
            return ""
        }
    }

    fun addToCalender(startDate: Long, endDate: Long, description: String, context: Context?) {
        val intent = Intent(Intent.ACTION_EDIT)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        intent.type = "vnd.android.cursor.item/event"
        intent.putExtra("beginTime", startDate)
        intent.putExtra("allDay", true)
        // intent.putExtra("rrule", "FREQ=YEARLY")
        intent.putExtra("endTime", endDate)
        intent.putExtra("title", description)
        context?.startActivity(intent)
    }

}