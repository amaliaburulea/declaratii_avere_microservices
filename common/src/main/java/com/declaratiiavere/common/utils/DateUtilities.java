package com.declaratiiavere.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Utility class for dates management.
 *
 * @author Razvan Dani
 */
public class DateUtilities {
    public static final String YEAR_MONTH_PATTERN = "yyyyMM";
    public static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";
    public static final String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_PATTERN = "yyyy-MM-dd hh:mm:ss";
    public static final String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_PATTERN_24H_CLOCK = "yyyy-MM-dd HH:mm:ss";
    public static final String YEAR_MONTH_DAY_HOUR_MINUTE_PATTERN_24H_CLOCK = "yyyy-MM-dd HH:mm";
    public static final String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILISENCOND_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * Returns the date formatted with the given pattern.
     *
     * @param date    The date
     * @param pattern The pattern
     * @return The formatted date
     */
    public static String formatDate(Date date, String pattern) {
        return getDateTime(date).toString(pattern);
    }

    /**
     *  Parses given date having given pattern. Returns the date resulting by parsing the given string.
     *
     * @param date - string representation of date that has to be parsed.
     * @param pattern - pattern in which the input string is formatted.
     * @return the date resulted by parsing the given string
     */
    public static Date parseDate(String date, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(pattern);
        return dateTimeFormatter.parseDateTime(date).toDate();
    }

    /**
     *  Parses in UTC the given date having given pattern. Returns the date resulting by parsing the given string.
     *
     * @param date - string representation of date that has to be parsed.
     * @param pattern - pattern in which the input string is formatted.
     * @return the date resulted by parsing the given string, expressed in UTC
     */
    public static Date parseDateUTC(String date, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(pattern);

        return dateTimeFormatter.parseDateTime(date).toDateTime(DateTimeZone.UTC).toDate();
    }

    /**
     * Returns the next day as a Date object.
     *
     * @param date The date
     * @return The next day
     */
    public static Date getNextDay(Date date) {
        return getDateTime(date).plusDays(1).toDate();
    }

    /**
     * Returns the previous date as a Date object.
     *
     * @param date The date
     * @return The previous date
     */
    public static Date getPreviousDay(Date date) {
        return getDateTime(date).minusDays(1).toDate();
    }

    /**
     * Returns a date that has the specified number of days added to the specified date.
     *
     * @param date              The date
     * @param numberOfDaysToAdd The number of days to add
     * @return The resulted date
     */
    public static Date addDaysToDate(Date date, Integer numberOfDaysToAdd) {
        return getDateTime(date).plusDays(numberOfDaysToAdd).toDate();
    }

    /**
     * Returns a date that has the specified number of years added to the specified date.
     *
     * @param date               The date
     * @param numberOfYearsToAdd The number of years to add
     * @return The resulted date
     */
    public static Date addYearsToDate(Date date, Integer numberOfYearsToAdd) {
        return getDateTime(date).plusYears(numberOfYearsToAdd).toDate();
    }

    /**
     * Returns the date that represents the end of month for the given date.
     *
     * @param date The date
     * @return The end of month date
     */
    public static Date getEndOfMonthForDate(Date date) {
        return getDateTime(date).dayOfMonth().withMaximumValue().toDate();
    }

    /**
     * Returns the day that represents the end of month for the given date.
     *
     * @param date The date
     * @return The end of month day
     */
    public static Integer getEndOfMonthDayForDate(Date date) {
        return getDateTime(date).dayOfMonth().withMaximumValue().getDayOfMonth();
    }

    /**
     * Returns -1 if the date part (without time) of the firstDate is smaller than the date part of the secondDate,
     * 0 if the date parts are equal and 1 if the date part of the firstDate is grater than the date part of the
     * secondDate.
     *
     * @param firstDate  The first date
     * @param secondDate The second date
     * @return The comparison result
     */
    public static int compareDatePart(Date firstDate, Date secondDate) {
        LocalDate localFirstDate = getLocalDate(firstDate);
        LocalDate localSecondDate = getLocalDate(secondDate);

        return localFirstDate.toDate().compareTo(localSecondDate.toDate());
    }

    /**
     * Returns -1 if the timestamp part (without timezone) of the firstTimestamp is smaller than the timestamp part of the secondTimestamp,
     * 0 if the timestamp parts are equal and 1 if the timestamp part of the firstTimestamp is grater than the timestamp part of the
     * secondTimestamp.
     *
     * @param firstTimestamp  The first timestamp
     * @param secondTimestamp The second timestamp
     * @return The comparison result
     */
    public static int compareTimestamp(Date firstTimestamp, Date secondTimestamp) {
        DateTime firstDateTime = getDateTime(firstTimestamp);
        DateTime secondDateTime = getDateTime(secondTimestamp);

        return firstDateTime.toDate().compareTo(secondDateTime.toDate());
    }

    /**
     * Resets the time of a date to midnight.
     *
     * @param date The date
     * @return The Date object with the time reset to midnight
     */
    public static Date resetTimeToMidnight(Date date) {
        return getDateTime(date).withTimeAtStartOfDay().toDate();
    }

    /**
     * Returns the month of the given date.
     *
     * @param date The date
     * @return The month
     */
    public static Integer getMonthForDate(Date date) {
        return getDateTime(date).getMonthOfYear();
    }

    /**
     * Returns the number of days between 2 dates.
     *
     * @param firstDate  The first date
     * @param secondDate The second date
     * @return The number of days between dates
     */
    public static Integer getNumberOfDaysBetweenDates(Date firstDate, Date secondDate) {
        LocalDate localFirstDate = getLocalDate(firstDate);
        LocalDate localSecondDate = getLocalDate(secondDate);

        return Days.daysBetween(localFirstDate, localSecondDate).getDays();
    }

    /**
     * Returns the day of month for the specified date.
     *
     * @param date The date
     * @return The day of month for date
     */
    public static Integer getDayOfMonthForDate(Date date) {
        return getDateTime(date).getDayOfMonth();
    }

    private static DateTime getDateTime(Date date) {
        if (date == null) {
            throw new NullPointerException();
        } else {
            return new DateTime(date);
        }
    }

    private static LocalDate getLocalDate(Date date) {
        if (date == null) {
            throw new NullPointerException();
        } else {
            return new LocalDate(date);
        }
    }

    /**
     * Adds the specified number of seconds to the specified date.
     *
     * @param timestamp The timestamp
     * @param seconds   The seconds to add
     * @return The resulted date
     */
    public static Date addSecondsToTimestamp(Date timestamp, Integer seconds) {
        return new DateTime(timestamp).plusSeconds(seconds).toDate();
    }

    /**
     * Adds the specified number of milliseconds to the specified date.
     *
     * @param timestamp      The timestamp
     * @param milliseconds   The milliseconds to add
     * @return The resulted date
     */
    public static Date addMillisecondsToTimestamp(Date timestamp, Integer milliseconds) {
        return new DateTime(timestamp).plusMillis(milliseconds).toDate();
    }

    /**
     * Adds the specified number of hours to the specified date.
     *
     * @param timestamp The timestamp
     * @param hours     The hours to add
     * @return The resulted date
     */
    public static Date addHoursToTimestamp(Date timestamp, Integer hours) {
        return new DateTime(timestamp).plusHours(hours).toDate();
    }

    public static Date addMinutesToTimestamp(Date timestamp, Integer hours) {
        return new DateTime(timestamp).plusMinutes(hours).toDate();
    }

    /**
     * Returns the number of the day in the week for a given date.
     *
     * @param date The date
     * @return The day of week
     */
    public static int getDayOfWeekForDate(Date date) {
        return getDateTime(date).getDayOfWeek();
    }

    /**
     * Returns the current timezone offset to UTC, in hours.
     *
     * @return the offset in hours
     */
    public static int getCurrentTimeZoneOffsetToUTC() {
        DateTimeZone tz = DateTimeZone.getDefault();
        Long instant = DateTime.now().getMillis();

        long offsetInMilliseconds = tz.getOffset(instant);
        return (int) TimeUnit.MILLISECONDS.toHours(offsetInMilliseconds);
    }

    /**
     * Returns the year of the specified date.
     *
     * @param date  The date
     * @return      The year
     */
    public static int getYearForDate(Date date) {
        return getDateTime(date).getYear();
    }

    /**
     * Returns current date (without timestamp).
     *
     * @return  The current date
     */
    public static Date getCurrentDate() {
        return new LocalDate().toDate();
    }

    /**
     * Returns the current timestamp in UTC.
     *
     * @return  The current timestamp in UTC
     */
    public static Date getCurrentTimestampUTC() {
        int utcOffset = getCurrentTimeZoneOffsetToUTC();

        return addHoursToTimestamp(new Date(), -utcOffset);
    }

    /**
     * Get a list of dates between the two given dates.
     *
     * @param startdate The start date
     * @param enddate   The end date
     * @return          The List of dates
     */
    public static List<Date> getDaysBetweenDates(Date startdate, Date enddate) {
        List<Date> dates = new ArrayList<Date>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate)) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }

        return dates;
    }

    /**
     * Check if a date falls between two others.
     *
     * @param date The date to check.
     * @param startDate The start date of the range.
     * @param endDate The end date of the range.
     * @return true of the date falls within range.
     */
    public static boolean isDateBetweenTwoDates(Date date, Date startDate, Date endDate) {
        return compareDatePart(date, startDate) > -1 && compareDatePart(date, endDate) < 1;
    }

    public static XMLGregorianCalendar convertDateToXMLGregorianCalendar(Date date) throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(DateUtilities.formatDate(date, DateUtilities.YEAR_MONTH_DAY_PATTERN));
    }

    public static Date toDate(XMLGregorianCalendar calendar){
        if(calendar == null) {
            return null;
        }
        return calendar.toGregorianCalendar().getTime();
    }

    public static Date stringToDate(String stringDate){
        if(stringDate == null){
            return null;
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat(YEAR_MONTH_DAY_HOUR_MINUTE_PATTERN_24H_CLOCK);
            Date date = null;
            try {
                date = sdf.parse(stringDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date;
        }
    }


}
