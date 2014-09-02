/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.util.date

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date;

/**
 * DateUtil groovy class provides generic methods for {@link Calendar}
 * @author Shashank Agrawal
 *
 */
class DateUtil {

    public static final String DEFAULT_DATE_FORMAT = "MM/dd/yyyy"

    Calendar c = Calendar.getInstance()

    /**
     * Sets beginning of the day
     * @param calendar The {@link Calendar} object
     * @return {@link Calendar}
     */
    def setBeginningOfDay(Calendar calendar=c) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Sets end of the day
     * @return {@link Calendar}
     */
    def setEndOfDay() {
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Returns current week date range i.e. Start date and End Date of week.
     * @param includeWeekend Boolean value used to specify whether to include weekend or not.
     * @return Map containing Start date and End Date of current week.
     */
    Map getDateRangeForThisWeek(boolean includeWeekend = true) {
        setBeginningOfDay()
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        def sunday = c.getTime()
        setEndOfDay()
        if(includeWeekend)
            c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        else
            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        def saturday = c.getTime()
        [startDate: sunday, endDate: saturday]
    }

    /**
     * Returns current week date range between [Monday - Friday] . i.e. Start date and End Date of week.
     * @return Map containing Start date and End Date of current week between [Monday - Friday].
     */
    Map getWeekDayRange() {
        setBeginningOfDay()
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        def monday = c.getTime()
        setEndOfDay()
        c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        def friday = c.getTime()
        [startDate: monday, endDate: friday]
    }

    /**
     * Returns current week date range for [Saturday - Sunday] . i.e. Saturday and Sunday of current week.
     * @return Map containing Saturday and Sunday of current week.
     */
    def getDateRangeForComingWeekend() {
        setBeginningOfDay()
        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        def comingSaturday = c.getTime()
        setEndOfDay()
        c.add(Calendar.DAY_OF_YEAR, 1)
        def comingSunday = c.getTime()
        [startDate: comingSaturday, endDate: comingSunday]
    }

    /**
     * Returns current month date range i.e. Start day and Last day of current month.
     * @return Map containing Start day and Last day of current month.
     */
    def getDateRangeForThisMonth() {
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
        setEndOfDay()
        def lastDayOfTheMonth = c.getTime()
        return [startDate: new Date(), endDate: lastDayOfTheMonth]
    }

    /**
     * Returns date range depends on time period passed.
     * @param timePeriod String containing type of date range.
     * @return Map containing date range.
     */
    def getDateRange(String timePeriod='this_week') {
        def dateRange
        switch(timePeriod) {
            case 'this_week':
                dateRange = getDateRangeForThisWeek()
                break
            case 'this_weekend':
                dateRange = getDateRangeForComingWeekend()
                break
            case 'this_month':
                dateRange = getDateRangeForThisMonth()
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
        if(!rangeFromNow) {
            if(args.fromNumberOfDays)
                c.add(Calendar.DAY_OF_MONTH, -args.fromNumberOfDays)
            else if(args.fromDay)
                c.set(Calendar.DAY_OF_MONTH, args.fromDay)
        }

        Date startDate = c.getTime()
        if(args.toNumberOfDays)
            c.add(Calendar.DAY_OF_MONTH, Calendar.DAY_OF_MONTH + args.toNumberOfDays)
        else if(args.toDay)
            c.set(Calendar.DAY_OF_MONTH, args.toDay)
        setEndOfDay()
        Date endDate = c.getTime()
        return [startDate: startDate, endDate: endDate]
    }

    /**
     * Sets Date calender instance.
     * @param Date The {@link Date} Object
     * @return {@link Calendar}
     */
    def setDate(def Date) {
        c.setTime(new Date(date))
    }

    /**
     * Returns String containing only date from {@link Date}
     * @param date The {@link Date} Object
     * @param format Default date format in which result is returned
     * @return Sting containing only Date.
     */
    String getDatePart(def date = c.getTime(), String format = DEFAULT_DATE_FORMAT) {
        return new SimpleDateFormat(format).format(date)
    }

    /**
     * Returns String containing only time from {@link Date}
     * @param date The {@link Date} Object
     * @return Sting containing only Time.
     */
    def getTimePart(def date = c.getTime()) {
        return new SimpleDateFormat("MM/dd/yyyy").format(date)
    }

    /**
     * Returns String containing Date with given time.
     * @param date The {@link Date} Object
     * @param time String containing time.
     * @return {@link Date}.
     */
    def addTimeTo(def date=c.getTime(),def time="9:00 AM") {
        def stringDate = new SimpleDateFormat("MM/dd/yyyy").format(date)
        stringDate = stringDate+" "+time
        return parseDateTime(stringDate)
    }

    /**
     * Returns date after parsing to format ["MM/dd/yyyy h:m a"]
     * @param dateTime Sting containing date, Default set to empty string.
     * @return Formatted {@link Date}.
     */
    Date parseDateTime(String dateTime = "") {
        return new SimpleDateFormat("MM/dd/yyyy h:m a").parse(dateTime)
    }

    /**
     * Compares Dates without time and returns boolean.
     * @param a {@link Date} Object
     * @param b {@link Date} Object
     * @return Boolean value for both date comparison without time.
     */
    def equalsWithoutTime(Date a, Date b) {
        c.setDate(a)
        Calendar secondCalendar = Calendar.getInstance()
        this.setBeginningOfDay()
        this.setBeginningOfDay(secondCalendar)
        return c.equals(secondCalendar)
    }

    /**
     * Returns {@link Date} object by parsing parameters received.
     * @param year Sting value, if not specified returns null.
     * @param month String value, Default set to one.
     * @param day String value, Default set to one.
     * @return Parsed {@link Date} Object.
     */
    static Date parseDate(String year, String month, String day) {
        if(!year) return null;
        String dateString = (month ?: "01") + "/" + (day ?: "01") + "/" + year
        return parseDate(dateString)
    }

    /**
     * Return Formatted Date by given string date and format.
     * @param dateString String date to be parsed.
     * @param format Format in which date is being parsed. Default set to ["MM/dd/yyyy"]
     * @return Parsed {@link Date} Object with given format.
     */
    static Date parseDate(String dateString, String format = DEFAULT_DATE_FORMAT) {
        if(!dateString)  return null;
        return new SimpleDateFormat(format).parse(dateString)
    }

    /**
     * Returns formatted date with given time zone and format. If time zone not specified date will be 
     * parsed with defaul time zone.
     * @param dateString String date to be parsed.
     * @param timezone Sting value containing time zone.
     * @param format Format in which date is being parsed. Default set to ["MM/dd/yyyy"].
     * @return Parsed Date with given time zone and format.
     */
    static Date parseDateTime(String dateString, String timezone, String format = DEFAULT_DATE_FORMAT) {
        if(!dateString)  return null;
        def dateFormat = new SimpleDateFormat(format)
        if(timezone)
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezone))
        return dateFormat.parse(dateString)
    }

    /**
     * Return Formatted Date String from given {@link Date} and format.
     * @param date Date to be parsed.
     * @param format Format in which date is being parsed. Default set to ["MM/dd/yyyy"]
     * @return Parsed string Date with given format.
     */
    static String formatDate(Date date, String format = DEFAULT_DATE_FORMAT) {
        if(!date) return "";
        return new SimpleDateFormat(format).format(date)
    }

    /**
     * Returns formatted date string with given time zone and format. If time zone not specified date will be
     * parsed with defaul time zone.
     * @param date Date to be parsed.
     * @param timezone Sting value containing time zone.
     * @param format Format in which date is being parsed.
     * @return Parsed string Date with given time zone and format.
     */
    static String formatDate(Date date, String timezone, String format) {
        if(!date) return "";
        def dateFormat = new SimpleDateFormat(format)
        if(timezone)
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezone))
        return dateFormat.format(date)
    }

}