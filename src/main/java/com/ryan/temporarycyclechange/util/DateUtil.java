package com.ryan.temporarycyclechange.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.ryan.temporarycyclechange.domain.enums.BufferDayEnum;
import com.ryan.temporarycyclechange.domain.enums.DayEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author rsapl00
 */
public final class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static final String EXPIRATION_TS = "9999-12-31 00:00:00";
    public static final Integer BUFFER_DAYS = 7;

    /**
     * Gets the nearest effective date of the given start date.
     * 
     * @param startDate     {@link Date} The date where to start searching.
     * @param effectiveDate {@link DayEnum} The date of the desired effective day.
     * @return {@link Date} The nearest effect
     */
    public static Date getEffectiveDate(final Date startDate, final DayEnum effectiveDate) {
        return getEffectiveDate(startDate.toLocalDate(), effectiveDate);
    }

    /**
     * Gets the nearest effective date of the given start date.
     * 
     * @param startDate     {@link LocalDate} The date where to start searching.
     * @param effectiveDate {@link DayEnum} The date of the desired effective day.
     * @return {@link Date} The nearest effect
     */
    public static Date getEffectiveDate(final LocalDate startDate, final DayEnum effectiveDate) {

        // LocalDate start = startDate.plusDays(1); // NOTE: start date cannot be the effective date.
        LocalDate start = startDate;

        while (true) {

            if (DayEnum.getDayEnum(start.getDayOfWeek()).equals(effectiveDate)) {
                return java.sql.Date.valueOf(start);
            }

            start = start.plusDays(1);
        }
    }

    public static String getDayName(final Date date) {
        return DayEnum.getDayEnum(date.toLocalDate().getDayOfWeek()).getDayName();
    }

    public static Timestamp getExpiryTimestamp () {
        return Timestamp.valueOf(EXPIRATION_TS);
    }

    public static Timestamp expireNow() {
        Timestamp expiryDateNow = now();
        
        if (logger.isDebugEnabled()) {
            logger.debug("Expiry Date: " + expiryDateNow.toString());
        }

        return expiryDateNow;
    }

    public static Timestamp now() {
        return Timestamp.valueOf(LocalDateTime.now().minusSeconds(10));
    }

    public static Date getBufferDate(final Date runDate, BufferDayEnum days) {
        LocalDate toProcess = runDate.toLocalDate();

        if (BufferDayEnum.PLUS_BUFFER == days) {
            return Date.valueOf(toProcess.plusDays(days.getDays()));
        } else {
            return Date.valueOf(toProcess.minusDays(days.getDays()));
        }
    }

    public static boolean isSameDate(Date date1, Date date2) {
        return date1.equals(date2);
    }

    public static boolean isBefore(Date from, Date to) {
        return from.toLocalDate().isBefore(to.toLocalDate());
    }

    public static boolean isAfter(Date from, Date to) {
        return from.toLocalDate().isAfter(to.toLocalDate());
    }

    public static boolean isAfter(LocalDate from, LocalDate to) {
        return from.isAfter(to);
    }

    public static boolean isEqual(Date date1, Date date2) {
        return date1.toLocalDate().isEqual(date2.toLocalDate());
    }

    public static boolean isEqual(LocalDate date1, LocalDate date2) {
        return date1.isEqual(date2);
    }

    public static Date convertStringToDate(String date) {
        return Date.valueOf(LocalDate.parse(date));
    }

    public static String convertDateToString(Date date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return formatter.format(date.toLocalDate());
    }
}