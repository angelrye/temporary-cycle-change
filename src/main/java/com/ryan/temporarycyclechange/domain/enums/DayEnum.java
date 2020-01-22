package com.ryan.temporarycyclechange.domain.enums;

import java.sql.Date;
import java.time.DayOfWeek;

import com.ryan.temporarycyclechange.util.DateUtil;

/**
 * 
 * @author rsapl00
 */
public enum DayEnum {

    NO_RUNDAY("0", "NO RUNDAY"), SUNDAY("1", "SUNDAY"), MONDAY("2", "MONDAY"), TUESDAY("3", "TUESDAY"),
    WEDNESDAY("4", "WEDNESDAY"), THURSDAY("5", "THURSDAY"), FRIDAY("6", "FRIDAY"), SATURDAY("7", "SATURDAY");

    private String dayNumber;
    private String dayName;

    private DayEnum(final String dayNumber, final String dayName) {
        this.dayNumber = dayNumber;
        this.dayName = dayName;
    }

    public String getDayNumber() {
        return this.dayNumber;
    }

    public Integer getDayNumberInteger() {
        return Integer.valueOf(this.dayNumber);
    }

    public String getDayName() {
        return this.dayName;
    }

    public static DayEnum getDayEnum(final DayOfWeek dayOfWeek) {

        int dayNum = 0;
        // if DayOfWeek is Sunday
        if (dayOfWeek.getValue() == 7) {
            dayNum = 1; // Make Sunday as 1 per PSCYCSCH table run day
        } else {
            dayNum = dayOfWeek.getValue() + 1;
        }

        return DayEnum.getDayEnum(String.valueOf(dayNum));
    }

    public static DayEnum getDayEnum(final String dayNumber) {

        for (DayEnum day : DayEnum.values()) {
            if (day.getDayNumber().equals(dayNumber)) {
                return day;
            }
        }

        // default Day
        return DayEnum.NO_RUNDAY;
    }

    public static DayEnum getDayEnum(final Date date) {
        for (DayEnum day : DayEnum.values()) {
            if (day.getDayName().equals(DateUtil.getDayName(date))) {
                return day;
            }
        }

        return NO_RUNDAY;
    }

    public static DayEnum getDayEnumByDayName(final String name) {
        for (DayEnum day : DayEnum.values()) {
            if (day.getDayName().equals(name)) {
                return day;
            }
        }

        return NO_RUNDAY;
    }
}
