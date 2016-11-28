/*
 * Copyright (c) 2016, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.util.date

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * DateUtil groovy class provides generic methods for {@link Calendar}
 * @author Shashank Agrawal
 *
 */
class DateUtil {

    static final String DEFAULT_DATE_FORMAT = 'MM/dd/yyyy'

    private final Calendar c = Calendar.instance
    /**
     * Sets beginning of the day
     * @param calendar The {@link Calendar} object
     * @return {@link Calendar}
     */
    private void setBeginningOfDay(Calendar calendar = c) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    /**
     * Sets end of the day
     * @return {@link Calendar}
     */
    private void setEndOfDay() {
        int fiftyNine = 59
        c.set(Calendar.HOUR_OF_DAY, 23)
        c.set(Calendar.MINUTE, fiftyNine)
        c.set(Calendar.SECOND, fiftyNine)
        c.set(Calendar.MILLISECOND, 0)
    }

    /**
     * Returns current week date range i.e. Start date and End Date of week.
     * @param includeWeekend Boolean value used to specify whether to include weekend or not.
     * @return Map containing Start date and End Date of current week.
     */
    Map getDateRangeForThisWeek(boolean includeWeekend = true) {
        setBeginningOfDay()
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        Date sunday = c.time

        setEndOfDay()
        if (includeWeekend) {
            c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        } else {
            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        }

        Date saturday = c.time
        return [startDate: sunday, endDate: saturday]
    }

    /**
     * Returns current week date range between [Monday - Friday] . i.e. Start date and End Date of week.
     * @return Map containing Start date and End Date of current week between [Monday - Friday].
     */
    Map getWeekDayRange() {
        setBeginningOfDay()
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        Date monday = c.time

        setEndOfDay()
        c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        Date friday = c.time

        return [startDate: monday, endDate: friday]
    }

    /**
     * Returns current week date range for [Saturday - Sunday] . i.e. Saturday and Sunday of current week.
     * @return Map containing Saturday and Sunday of current week.
     */
    Map getDateRangeForComingWeekend() {
        setBeginningOfDay()
        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        Date comingSaturday = c.time

        setEndOfDay()
        c.add(Calendar.DAY_OF_YEAR, 1)
        Date comingSunday = c.time

        return [startDate: comingSaturday, endDate: comingSunday]
    }

    /**
     * Returns current month date range i.e. Start day and Last day of current month.
     * @return Map containing Start day and Last day of current month.
     */
    Map getDateRangeForThisMonth() {
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
        setEndOfDay()
        Date lastDayOfTheMonth = c.time

        return [startDate: new Date(), endDate: lastDayOfTheMonth]
    }

    /**
     * Returns date range depends on time period passed.
     * @param timePeriod String containing type of date range.
     * @return Map containing date range.
     */
    Map getDateRange(String timePeriod = 'this_week') {
        Map dateRange
        switch (timePeriod) {
            case 'this_week':
                dateRange = dateRangeForThisWeek
                break
            case 'this_weekend':
                dateRange = dateRangeForComingWeekend
                break
            case 'this_month':
                dateRange = dateRangeForThisMonth
                break
        }

        return dateRange
    }

    /**
     * A generic method to get date range according to given arguments
     * @param REQUIRED args
     * @param rangeFromNow set this to false if do not wants date range from
     * current date, else startDate will be current date. args.from* parameters
     * will only be valid if this is set to false.
     * @param args.fromNumberOfDays the number of days before from today.
     * @param args.fromDay the day from which date range needs to be calculated.
     * @param REQUIRED args.toNumberOfDays the number of days from today.
     *
     * @return
     */
    Map<String, Date> getDateRange(Map args, boolean rangeFromNow = true) {
        setBeginningOfDay()
        if (!rangeFromNow) {
            if (args.fromNumberOfDays) {
                c.add(Calendar.DAY_OF_MONTH, -args.fromNumberOfDays)
            } else {
                if (args.fromDay) {
                    c.set(Calendar.DAY_OF_MONTH, args.fromDay)
                }
            }
        }

        Date startDate = c.time
        if (args.toNumberOfDays) {
            c.add(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_MONTH + args.toNumberOfDays)
        } else {
            if (args.toDay) {
                c.set(Calendar.DAY_OF_MONTH, args.toDay)
            }
        }

        setEndOfDay()
        Date endDate = c.time
        return [startDate: startDate, endDate: endDate]
    }

    /**
     * Returns String containing only date from {@link Date}
     * @param date The {@link Date} Object
     * @param format Default date format in which result is returned
     * @return Sting containing only Date.
     */
    String getDatePart(Date date = c.getTime(), String format = DEFAULT_DATE_FORMAT) {
        return new SimpleDateFormat(format, Locale.ENGLISH).format(date)
    }

    /**
     * Returns String containing only time from {@link Date}
     * @param date The {@link Date} Object
     * @return Sting containing only Time.
     */
    String getTimePart(Date date = c.time) {
        return new SimpleDateFormat('HH:mm:ss', Locale.ENGLISH).format(date)
    }

    /**
     * Returns String containing Date with given time.
     * @param date The {@link Date} Object
     * @param time String containing time.
     * @return {@link Date}.
     */
    Date addTimeTo(Date date = c.time, String time = '9:00 AM') {
        String stringDate = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.ENGLISH).format(date)
        stringDate = stringDate + ' ' + time
        return parse(stringDate)
    }

    /**
     * Compares Dates without time and returns boolean.
     * @param a {@link Date} Object
     * @param b {@link Date} Object
     * @return Boolean value for both date comparison without time.
     */
    boolean equalsWithoutTime(Date a, Date b) {
        c.setTime(a)
        Calendar secondCalendar = Calendar.instance
        secondCalendar.setTime(b)
        this.setBeginningOfDay()
        this.setBeginningOfDay(secondCalendar)

        return (c == secondCalendar)
    }

    /**
     * Returns {@link Date} object by parsing parameters received.
     * @param year Sting value, if not specified returns null.
     * @param month String value, Default set to one.
     * @param day String value, Default set to one.
     * @return Parsed {@link Date} Object.
     */
    static Date parse(Integer year, Integer month, Integer day) {
        if (!year) {
            return null
        }
        String slash = '/'
        String one = '01'
        String dateString = (month ?: one) + slash + (day ?: one) + slash + year
        return parse(dateString, null, DEFAULT_DATE_FORMAT)
    }

    /**
     * Returns formatted date with given time zone and format. If time zone not specified date will be
     * parsed with default time zone.
     * @param dateString String date to be parsed.
     * @param timezone Sting value containing time zone.
     * @param format Format in which date is being parsed. Default set to ["MM/dd/yyyy"].
     * @return Parsed Date with given time zone and format.
     */
    static Date parse(String dateString, String timezone = TimeZone.getDefault(), String format = DEFAULT_DATE_FORMAT) {
        if (!dateString) {
            return null
        }

        DateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH)
        if (timezone) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezone))
        }

        return dateFormat.parse(dateString)
    }

    /**
     * Return Formatted Date String from given {@link Date} and format.
     * @param date Date to be parsed.
     * @param dateFormat Format in which date is being parsed. Default set to ["MM/dd/yyyy"]
     * @return Parsed string Date with given format.
     */
    static String format(Date date, String dateFormat = DEFAULT_DATE_FORMAT) {
        return format(date, null, dateFormat)
    }

    /**
     * Returns formatted date string with given time zone and format. If time zone not specified date will be
     * parsed with default time zone.
     * @param date Date to be parsed.
     * @param timezone Sting value containing time zone.
     * @param format Format in which date is being parsed.
     * @return Parsed string Date with given time zone and format.
     */
    static String format(Date date, String timezone, String format) {
        if (!date) {
            return ''
        }

        DateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH)
        if (timezone) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezone))
        }

        return dateFormat.format(date)
    }
}
