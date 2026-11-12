/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * A start time of day, like 09:30.
 *
 * Why I store minutes since midnight (09:30 is 570) and not the text
 * "09:30": text is compared letter by letter, so "10:00" comes before "9:30"
 * because '1' is smaller than '9'. Whole numbers compare the way a clock
 * does, and that is what sorting appointments by time depends on.
 * LocalTime would also work, but java.time is not in Modules 1-3.
 *
 * A TimeSlot never changes after it is made, so one object can safely be
 * shared by the schedule and by many appointments.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class TimeSlot implements Comparable<TimeSlot> {

    /** Minutes in an hour. */
    public static final int MINUTES_PER_HOUR = 60;

    /** Minutes in a day. A start time must be less than this. */
    public static final int MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR;

    /** Minutes since midnight, e.g. 570 for 09:30. */
    private final int startMinute; // [Encapsulation] final with no setter: a time never changes

    /**
     * Builds a time from minutes since midnight.
     * Outside code uses ofMinutes(...), of(...) or parse(...).
     *
     * @param startMinute minutes since midnight (already checked)
     */
    private TimeSlot(int startMinute) {
        this.startMinute = startMinute;
    }

    /**
     * Creates a time from minutes since midnight.
     *
     * @param minutes 0 to 1439
     * @return the time, or null if it is outside one day
     */
    public static TimeSlot ofMinutes(int minutes) {
        if (minutes < 0 || minutes >= MINUTES_PER_DAY) {
            return null; // refused, not wrapped round: 1500 minutes is not "01:00 tomorrow" here
        }
        return new TimeSlot(minutes);
    }

    /**
     * Creates a time from an hour and a minute (24-hour clock).
     *
     * @param hour   0 to 23
     * @param minute 0 to 59
     * @return the time, or null if either part is out of range
     */
    public static TimeSlot of(int hour, int minute) {
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            return null;
        }
        return new TimeSlot(hour * MINUTES_PER_HOUR + minute);
    }

    /**
     * Says what is wrong with a typed time, if anything.
     * "09:30" and "9:30" are both accepted (24-hour clock).
     *
     * @param text the time as typed
     * @return "" if it is a real time, otherwise the reason
     */
    public static String checkText(String text) {
        if (Validator.isBlank(text)) {
            return "Time is required.";
        }
        String time = text.trim();
        int colon = time.indexOf(':');
        if (colon < 1 || colon > 2 || time.length() != colon + 3) {
            return "Time \"" + time + "\" must look like 09:30 (24-hour clock).";
        }
        String hourText = time.substring(0, colon);
        String minuteText = time.substring(colon + 1);
        if (!Validator.isAllDigits(hourText) || !Validator.isAllDigits(minuteText)) {
            return "Time \"" + time + "\" must look like 09:30 (24-hour clock).";
        }
        int hour = Integer.parseInt(hourText); // safe: only digits get here, so parseInt can't fail
        int minute = Integer.parseInt(minuteText);
        if (hour > 23 || minute > 59) {
            return "Time \"" + time + "\" is not a real time of day.";
        }
        return "";
    }

    /**
     * Reads a typed time such as "09:30".
     *
     * @param text the time as typed
     * @return the time, or null if the text is not a real time (checkText says why)
     */
    public static TimeSlot parse(String text) {
        if (!checkText(text).isEmpty()) {
            return null;
        }
        String time = text.trim();
        int colon = time.indexOf(':');
        return of(Integer.parseInt(time.substring(0, colon)),
                Integer.parseInt(time.substring(colon + 1)));
    }

    /**
     * Gives the minutes since midnight.
     *
     * @return e.g. 570 for 09:30
     */
    public int getStartMinute() {
        return startMinute;
    }

    /**
     * Gives the hour (24-hour clock).
     *
     * @return 0 to 23
     */
    public int getHour() {
        return startMinute / MINUTES_PER_HOUR;
    }

    /**
     * Gives the minute within the hour.
     *
     * @return 0 to 59
     */
    public int getMinute() {
        return startMinute % MINUTES_PER_HOUR;
    }

    /**
     * Checks whether this time is earlier than another.
     *
     * @param other the time to compare with
     * @return true if this one comes first
     */
    public boolean isBefore(TimeSlot other) {
        return compareTo(other) < 0;
    }

    /**
     * Puts times in clock order, earliest first.
     *
     * @param other the time to compare with
     * @return negative if this is earlier, 0 if the same, positive if later
     */
    @Override
    public int compareTo(TimeSlot other) {
        // compare numbers, not text: as text, "9:30" would sort after "10:00"
        return Integer.compare(startMinute, other.startMinute);
    }

    /**
     * Two times are equal when they are the same minute of the day.
     *
     * @param other the object to compare with
     * @return true if it is a TimeSlot at the same minute
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TimeSlot)) {
            return false;
        }
        return startMinute == ((TimeSlot) other).startMinute; // so a typed "9:30" equals the schedule's 09:30
    }

    /**
     * Gives a hash code that matches equals.
     *
     * @return the minutes since midnight
     */
    @Override
    public int hashCode() {
        return startMinute;
    }

    /**
     * Shows the time with two digits each, e.g. "09:30".
     *
     * @return the time as text
     */
    @Override
    public String toString() {
        return String.format("%02d:%02d", getHour(), getMinute());
    }
}
