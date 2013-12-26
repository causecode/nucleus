/*
 * Copyright (c) 2011, CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */

package com.cc.util.date

import java.text.SimpleDateFormat

class DateUtil {

    public static final String DEFAULT_DATE_FORMAT = "MM/dd/yyyy"

    Calendar c = Calendar.getInstance()

    def setBeginningOfDay(Calendar calendar=c) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    def setEndOfDay() {
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 0);
    }

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

    Map getWeekDayRange() {
        setBeginningOfDay()
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        def monday = c.getTime()
        setEndOfDay()
        c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
        def friday = c.getTime()
        [startDate: monday, endDate: friday]
    }

    def getDateRangeForComingWeekend() {
        setBeginningOfDay()
        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        def comingSaturday = c.getTime()
        setEndOfDay()
        c.add(Calendar.DAY_OF_YEAR, 1)
        def comingSunday = c.getTime()
        [startDate: comingSaturday, endDate: comingSunday]
    }

    def getDateRangeForThisMonth() {
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
        setEndOfDay()
        def lastDayOfTheMonth = c.getTime()
        return [startDate: new Date(), endDate: lastDayOfTheMonth]
    }

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

    def setDate(def Date) {
        c.setTime(new Date(date))
    }

    String getDatePart(def date = c.getTime(), String format = DEFAULT_DATE_FORMAT) {
        return new SimpleDateFormat(format).format(date)
    }

    def getTimePart(def date = c.getTime()) {
        return new SimpleDateFormat("MM/dd/yyyy").format(date)
    }

    def addTimeTo(def date=c.getTime(),def time="9:00 AM") {
        def stringDate = new SimpleDateFormat("MM/dd/yyyy").format(date)
        stringDate = stringDate+" "+time
        return parseDateTime(stringDate)
    }

    Date parseDateTime(String dateTime = "") {
        return new SimpleDateFormat("MM/dd/yyyy h:m a").parse(dateTime)
    }

    def equalsWithoutTime(Date a, Date b) {
        c.setDate(a)
        Calendar secondCalendar = Calendar.getInstance()
        this.setBeginningOfDay()
        this.setBeginningOfDay(secondCalendar)
        return c.equals(secondCalendar)
    }

    static Date parseDate(String year, String month, String day) {
        if(!year) return null;
        String dateString = (month ?: "01") + "/" + (day ?: "01") + "/" + year
        return parseDate(dateString)
    }

    static Date parseDate(String dateString, String format = DEFAULT_DATE_FORMAT) {
        if(!dateString)  return null;
        return new SimpleDateFormat(format).parse(dateString)
    }

    static Date parseDateTime(String dateString, String timezone, String format = DEFAULT_DATE_FORMAT) {
        if(!dateString)  return null;
        def dateFormat = new SimpleDateFormat(format)
        if(timezone)
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezone))
        return dateFormat.parse(dateString)
    }

    static String formatDate(Date date, String format = DEFAULT_DATE_FORMAT) {
        if(!date) return "";
        return new SimpleDateFormat(format).format(date)
    }

    static String formatDate(Date date, String timezone, String format) {
        if(!date) return "";
        def dateFormat = new SimpleDateFormat(format)
        if(timezone)
            dateFormat.setTimeZone(TimeZone.getTimeZone(timezone))
        return dateFormat.format(date)
    }

}