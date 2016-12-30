/*
 * Copyright (c) 2011-Present CauseCode Technologies Pvt Ltd, India.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are not permitted.
 */
package com.causecode.util.date

import spock.lang.Specification

/**
 * This class specifies unit test cases for {@link com.causecode.util.date.DateUtil}
 */
class DateUtilSpec extends Specification {

    DateUtil dateUtil
    Calendar calendar

    def setup() {
        dateUtil = new DateUtil()
        calendar = Calendar.instance
    }

    void 'test setBeginningOfDay method'() {
        when: 'setBeginningOfDay method is called'
        dateUtil.setBeginningOfDay(calendar)

        then: 'Following conditions must be satisfied'
        calendar.get(Calendar.HOUR_OF_DAY) == 0
        calendar.get(Calendar.MINUTE) == 0
        calendar.get(Calendar.SECOND) == 0
        calendar.get(Calendar.MILLISECOND) == 0
    }

    void 'test setEndOfDay method'() {
        when: 'setEndOfDay method is called'
        dateUtil.setEndOfDay()

        then: 'Following conditions must be satisfied'
        dateUtil.c.get(Calendar.HOUR_OF_DAY) == 23
        dateUtil.c.get(Calendar.MINUTE) == 59
        dateUtil.c.get(Calendar.SECOND) == 59
        dateUtil.c.get(Calendar.MILLISECOND) == 0
    }

    void 'test getDateRangeForThisWeek method'() {
        when: 'getDateRangeForThisWeek method is called where include weekend is true'
        Map result = dateUtil.getDateRangeForThisWeek(true)

        then: 'Map containing date range is returned, saturday is included'
        assert !result.isEmpty()
        result['startDate'].toString().contains('Sun')
        result['endDate'].toString().contains('Sat')

        when: 'getDateRangeForThisWeek method is called where include weekend is false'
        result = dateUtil.getDateRangeForThisWeek(false)

        then: 'Map containing date range is returned, saturday is excluded'
        assert !result.isEmpty()
        result['startDate'].toString().contains('Sun')
        result['endDate'].toString().contains('Fri')
    }

    void 'test getWeekDayRange method'() {
        when: 'getWeekDayRange is called'
        Map result = dateUtil.weekDayRange

        then: 'Map containing value of current week range is returned'
        assert !result.isEmpty()
        result['startDate'].toString().contains('Mon')
        result['endDate'].toString().contains('Fri')
    }

    void 'test getDateRangeForComingWeekend method'() {
        when: 'getDateRangeForComingWeekend is called'
        Map result = dateUtil.dateRangeForComingWeekend

        then: 'Map containing values for upcoming weekend is returned'
        assert !result.isEmpty()
        result['startDate'].toString().contains('Sat')
        result['endDate'].toString().contains('Sun')
    }

    void 'test getDateRangeForThisMonth method'() {
        when: 'getDateRangeForThisMonth method is called'
        Map result = dateUtil.dateRangeForThisMonth

        then: 'Map containing values for date range of current month is returned'
        assert !result.isEmpty()
    }

    void 'test getDateRange method'() {
        when: 'getDateRange method is called'
        Map thisWeek = dateUtil.getDateRange('this_week')
        Map thisWeekend = dateUtil.getDateRange('this_weekend')
        Map thisMonth = dateUtil.getDateRange('this_month')

        then: 'Map containing appropriate values for particular operation is returned'
        assert !thisWeek.isEmpty()
        thisWeek['startDate'].toString().contains('Sun')
        thisWeek['endDate'].toString().contains('Sat')

        assert !thisWeekend.isEmpty()
        thisWeekend['startDate'].toString().contains('Sat')
        thisWeekend['endDate'].toString().contains('Sun')

        assert !thisMonth.isEmpty()
    }

    void 'test getDateRange method when rangeFromNow is false'() {
        when: 'getDateRange method is called'
        Map args = [fromNumberOfDays: 5, toNumberOfDays: 5]

        Map result = dateUtil.getDateRange(args, false)

        then: 'Map containing date range for given values is returned'
        !result.isEmpty()

        when: 'getDateRange method is called'
        args = [fromDay: 1, toDay: 30]
        result = dateUtil.getDateRange(args, false)

        then: 'Map containing date range from date 1 to 30 is returned'
        !result.isEmpty()
        result['startDate'].toString().contains('01')
        result['endDate'].toString().contains('30')
    }

    void 'test getDatePart method'() {
        when: 'getDatePart method is called'
        String date = dateUtil.getDatePart(calendar.time, DateUtil.DEFAULT_DATE_FORMAT)

        then: 'String containing only date is returned'
        !date.isEmpty()
        date.matches('[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}')
    }

    void 'test getTimePart method'() {
        when: 'getTimePart method is called'
        String time = dateUtil.getTimePart(calendar.time)

        then: 'String containing only time is returned'
        !time.isEmpty()
        time.matches('[0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}')
    }

    void 'test addTimeTo method'() {
        when: 'addTimeTo method is called'
        Date date = dateUtil.addTimeTo()

        then: 'Date containing added time is returned'
        date && !date.toString().isEmpty()
    }

    void 'test equalsWithoutTime method'() {
        when: 'equalsWithoutTime method is called'
        Date date1 = new Date()
        try {
            Thread.sleep(2000)
        } catch (InterruptedException e) {
            e.printStackTrace(System.err)
        }
        Date date2 = new Date()
        boolean result = dateUtil.equalsWithoutTime(date1, date2)

        then: 'result of comparison is returned'
        result == true

        when: 'equalsWithoutTime method is called'
        date1 = new Date()
        date2 = date1 + 1
        result = dateUtil.equalsWithoutTime(date1, date2)

        then: 'result of comparison is returned'
        result == false
    }

    void 'test parse method'() {
        when: 'parse method is called with year as null'
        Date date = DateUtil.parse(null, 10, 18)

        then: 'Method returns with null'
        date == null

        when: 'parse method is called'
        date = DateUtil.parse(2016, 10, 18)

        then: 'Method returns date'
        !date.toString().isEmpty()
    }

    void 'test parse method which takes dateString as argument'() {
        when: 'parse method is called with null or empty dateString'
        Date date = DateUtil.parse(null, 'UTC')

        then: 'Method returns with null'
        date == null

        when: 'parse method is called with valid parameters'
        date = DateUtil.parse('10/19/2016', 'UTC')

        then: 'Method returns date'
        !date.toString().isEmpty()
    }

    void 'test format method'() {
        when: 'format is called with date as argument'
        String date = DateUtil.format(new Date())

        then: 'formatted date string is returned'
        !date.isEmpty()
        date.matches('[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}')

        when: 'format is called with null date'
        date = DateUtil.format(null, 'UTC', 'MM/dd/yyyy')

        then: 'empty string is returned'
        date.isEmpty()

        when: 'format method is called'
        date = DateUtil.format(new Date(), 'UTC', 'MM/dd/yyyy')

        then: 'Formatted date string is returned'
        !date.isEmpty()
        date.matches('[0-9]{1,2}/[0-9]{1,2}/[0-9]{4}')
    }
}
