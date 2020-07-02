package com.support.dateClass

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


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

    fun isValidDate(inDate: String, mSeperator: String): Boolean {
        val mPattern = "dd" + mSeperator + "MM" + mSeperator + "yyyy"
        val dateFormat =
            SimpleDateFormat(mPattern, Locale.getDefault())
        dateFormat.isLenient = false
        try {
            dateFormat.parse(inDate.trim { it <= ' ' })
        } catch (pe: ParseException) {
            return false
        }
        return true
    }

    fun getRandomNumber(maxNumber: Int, isFromZero: Boolean): Int {
        return if (isFromZero) {
            Random().nextInt(maxNumber)
        } else {
            Random().nextInt(maxNumber) + 1
        }
    }

    fun getTimeDifference(
        fromMilliSecond: String,
        toMilliSecond: String,
        mUnitType: Int
    ): Int {
        //0 = Seconds
        //1 = Minutes
        //2 = Hours
        //3 = Days
        val localCreatedDate = SimpleDateFormat("dd-M-yyyy HH:mm:ss aa")
            .format(Date(toMilliSecond.toLong()))
        val localExpireDate = SimpleDateFormat("dd-M-yyyy HH:mm:ss aa")
            .format(Date(fromMilliSecond.toLong()))
        //Log.i(TAG, "get_time_difference: localCreatedDate " + localCreatedDate);
        //Log.i(TAG, "get_time_difference: localExpireDate " + localExpireDate);
        val dateFormat =
            SimpleDateFormat("dd-M-yyyy HH:mm:ss aa")
        var Created_convertedDate: Date? = null
        var Expire_CovertedDate: Date? = null
        var todayWithZeroTime: Date? = null
        try {
            Created_convertedDate = dateFormat.parse(localCreatedDate)
            Expire_CovertedDate = dateFormat.parse(localExpireDate)
            val today = Date()
            todayWithZeroTime = dateFormat.parse(dateFormat.format(today))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val timeDiff = Expire_CovertedDate!!.time - Created_convertedDate!!.time
        return when (mUnitType) {
            0 -> {
                TimeUnit.SECONDS.convert(
                    timeDiff,
                    TimeUnit.MILLISECONDS
                ).toInt()
            }
            1 -> {
                TimeUnit.MINUTES.convert(
                    timeDiff,
                    TimeUnit.MILLISECONDS
                ).toInt()
            }
            2 -> {
                TimeUnit.HOURS.convert(
                    timeDiff,
                    TimeUnit.MILLISECONDS
                ).toInt()
            }
            3 -> {
                TimeUnit.DAYS.convert(
                    timeDiff,
                    TimeUnit.MILLISECONDS
                ).toInt()
            }
            else -> {
                TimeUnit.MINUTES.convert(
                    timeDiff,
                    TimeUnit.MILLISECONDS
                ).toInt()
            }
        }
    }

    fun getTimeDifference(
        fromMilliSecond: Long,
        toMilliSecond: Long,
        mUnitType: Int
    ): Int {
        return getTimeDifference(fromMilliSecond.toString(), toMilliSecond.toString(), mUnitType)
    }

    fun parseStringDateToCalendar(
        inputString: String?,
        formatPattern: String?
    ): Calendar? {
        // EEE MMM dd HH:mm:ss z yyyy
        // Mon Mar 14 16:02:37 GMT 2011
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat(formatPattern)
        try {
            calendar.time = simpleDateFormat.parse(inputString) // all done
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return calendar
    }

    fun parseDateFromMilliseconds(
        milliSecond: Long,
        formatPattern: String?
    ): String? {
        //dd-M-yyyy hh:mm:ss
        return SimpleDateFormat(formatPattern, Locale.getDefault())
            .format(Date(milliSecond))
    }

    fun parseMillisFromString(formatPattern: String?): Long {
        //String date_ = date;
        val sdf = SimpleDateFormat("dd-M-yyyy HH:mm:ss")
        return try {
            val mDate = sdf.parse(formatPattern)
            val timeInMilliseconds = mDate.time
            println("Date in milli :: $timeInMilliseconds")
            timeInMilliseconds
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            0
        }
    }

    fun getCalendarDifference(
        mFirstCalendar: Calendar,
        mSecondCalendar: Calendar,
        mTimeUnit: Int
    ): Long {
        //0 = Seconds
        //1 = Minutes
        //2 = Hours
        //3 = Days
        when (mTimeUnit) {
            0 -> {
                return TimeUnit.MILLISECONDS.toSeconds(mFirstCalendar.timeInMillis - mSecondCalendar.timeInMillis)
            }
            1 -> {
                return TimeUnit.MILLISECONDS.toMinutes(mFirstCalendar.timeInMillis - mSecondCalendar.timeInMillis)
            }
            2 -> {
                return TimeUnit.MILLISECONDS.toHours(mFirstCalendar.timeInMillis - mSecondCalendar.timeInMillis)
            }
            3 -> {
                return TimeUnit.MILLISECONDS.toDays(mFirstCalendar.timeInMillis - mSecondCalendar.timeInMillis)
            }
        }
        return 0
    }

    fun getParsedTodayDateTime(pattern: String?): String? {
        return parseDateFromMilliseconds(System.currentTimeMillis(), pattern)
    }

    fun getTodayDateTime(separator: String="_"): String? {
        val mPattern = "dd" + separator + "MM" + separator + "yyyy:HH_mm_ss" // dd-mm-yyyy
        return parseDateFromMilliseconds(System.currentTimeMillis(), mPattern)
    }

    fun getDateTimeMillis(mSeperator: String, millis: Long): String? {
        val mPattern = "dd" + mSeperator + "MM" + mSeperator + "yyyy" // dd-mm-yyyy
        return parseDateFromMilliseconds(millis, mPattern)
    }

    fun getCalendarMillis(millis: Long): Calendar {
        val mCalendar = Calendar.getInstance()
        mCalendar.timeInMillis = millis
        return mCalendar
    }

    fun getDateMillis(millis: Long): Date? {
        return getCalendarMillis(millis).time
    }

    fun getGregorianMillis(millis: Long): GregorianCalendar {
        val gregorianCalendar = GregorianCalendar()
        gregorianCalendar.timeInMillis = millis
        return gregorianCalendar
    }

    fun addYearToMillis(timeInMillis: Long, years: Int): Long {
        val gregorian = getGregorianMillis(timeInMillis)
        gregorian.add(Calendar.YEAR, years)
        return gregorian.timeInMillis
    }

    fun addMonthToMillis(timeInMillis: Long, months: Int): Long {
        val gregorian = getGregorianMillis(timeInMillis)
        gregorian.add(Calendar.MONTH, months)
        return gregorian.timeInMillis
    }

    fun addDayToMillis(timeInMillis: Long, days: Int): Long {
        val gregorian = getGregorianMillis(timeInMillis)
        gregorian.add(Calendar.DAY_OF_MONTH, days)
        return gregorian.timeInMillis
    }

    fun getYearMillis(timeInMillis: Long): Int {
        //Log.i(TAG, "getYearMillis: year " + getCalendarMillis(timeInMillis).get(Calendar.YEAR));
        return getCalendarMillis(timeInMillis)[Calendar.YEAR]
    }

    fun getAge(millis: Long?): Int {
        val now = Calendar.getInstance()
        val dob = Calendar.getInstance()
        dob.timeInMillis = millis!!
        require(!dob.after(now)) { "Can't be born in the future" }
        val year1 = now[Calendar.YEAR]
        val year2 = dob[Calendar.YEAR]
        var age = year1 - year2
        val month1 = now[Calendar.MONTH]
        val month2 = dob[Calendar.MONTH]
        if (month2 > month1) {
            age--
        } else if (month1 == month2) {
            val day1 = now[Calendar.DAY_OF_MONTH]
            val day2 = dob[Calendar.DAY_OF_MONTH]
            if (day2 > day1) {
                age--
            }
        }
        return age
    }


    fun addDate(initialDate: Date?, daysToAdd: Int): Date? {
        val mGregorian =
            GregorianCalendar(TimeZone.getTimeZone("GMT"))
        mGregorian.time = initialDate
        mGregorian.add(Calendar.DATE, daysToAdd)
        return mGregorian.time
    }

    fun addDate(initialDate: Long, daysToAdd: Int): Date? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = initialDate
        return addDate(calendar.time, daysToAdd)
    }

    fun addDate(initialDate: String, daysToAdd: Int): Date? {
        return addDate(initialDate.toLong(), daysToAdd)
    }

    fun addDate(initialDate: Calendar, daysToAdd: Int): Date? {
        return addDate(initialDate.time, daysToAdd)
    }

    fun getStringDateFromMillis(
        milliSecond: Long,
        formatPattern: String?
    ): String? {
        //dd-M-yyyy hh:mm:ss
        return SimpleDateFormat(formatPattern, Locale.getDefault())
            .format(Date(milliSecond))
    }

}