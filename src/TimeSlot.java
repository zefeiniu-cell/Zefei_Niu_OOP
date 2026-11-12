/*
 * =============================================================================
 *  System  : Arcana Health Centre - Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : TimeSlot.java
 *  Purpose : A start time of day, stored so that times sort correctly.
 * =============================================================================
 */

/**
 * A start time of day, such as 09:30, used for appointment slots.
 * <p>
 * The time is stored as minutes since midnight (09:30 is 570), not as text.
 * Text sorts letter by letter, so "10:00" would come before "9:30"; whole
 * numbers sort correctly, which is what lets appointments be listed from
 * earliest to latest. LocalTime would also work, but java.time is not part of
 * Modules 1-3, and this small class gives the same safety.
 * </p>
 * <p>
 * A TimeSlot is immutable: once made it never changes, so it can be shared
 * safely between the schedule and many appointments.
 * </p>
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class TimeSlot implements Comparable<TimeSlot> {

    /** Minutes in one hour. */
    public static final int MINUTES_PER_HOUR = 60;

    /** Minutes in one day; a start time must be below this. */
    public static final int MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR;

    /** Minutes since midnight, e.g. 570 for 09:30. */
    private final int startMinute;                                  // [Encapsulation] final with no setter: a time never changes

    /**
     * Builds a time from minutes since midnight. Private: use the factories.
     *
     * @param startMinute minutes since midnight, already checked
     */
    private TimeSlot(int startMinute) {
        this.startMinute = startMinute;
    }

    /**
     * Creates a time from minutes since midnight.
     *
     * @param minutes minutes since midnight, 0 to 1439
     * @return the time, or null if it is outside one day
     */
    public static TimeSlot ofMinutes(int minutes) {
        if (minutes < 0 || minutes >= MINUTES_PER_DAY) {
            return null;                                            // an impossible time is refused, never wrapped around
        }
        return new TimeSlot(minutes);
    }

    /**
     * Creates a time from an hour and a minute on the 24-hour clock.
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
     * Explains what, if anything, is wrong with a typed time.
     * Accepted forms are "09:30" and "9:30" on the 24-hour clock.
     *
     * @param text the time as typed
     * @return an empty text if it is a real time, otherwise the reason it is not
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
        int hour = Integer.parseInt(hourText);                      // safe: only digits reach this line, so parseInt cannot fail
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
     * @return the time, or null if the text is not a real time (see checkText)
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
     * Returns the minutes since midnight.
     *
     * @return e.g. 570 for 09:30
     */
    public int getStartMinute() {
        return startMinute;
    }

    /**
     * Returns the hour on the 24-hour clock.
     *
     * @return 0 to 23
     */
    public int getHour() {
        return startMinute / MINUTES_PER_HOUR;
    }

    /**
     * Returns the minute within the hour.
     *
     * @return 0 to 59
     */
    public int getMinute() {
        return startMinute % MINUTES_PER_HOUR;
    }

    /**
     * Tells whether this time is earlier than another.
     *
     * @param other the time to compare with
     * @return true if this time comes first
     */
    public boolean isBefore(TimeSlot other) {
        return compareTo(other) < 0;
    }

    /**
     * Orders times from earliest to latest.
     *
     * @param other the time to compare with
     * @return a negative number if this is earlier, 0 if equal, positive if later
     */
    @Override
    public int compareTo(TimeSlot other) {
        return Integer.compare(startMinute, other.startMinute);    // whole minutes give true clock order
    }

    /**
     * Two times are equal when they are the same minute of the day.
     *
     * @param other the object to compare with
     * @return true if other is a TimeSlot at the same minute
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TimeSlot)) {
            return false;
        }
        return startMinute == ((TimeSlot) other).startMinute;       // lets contains() find a slot typed separately, e.g. "9:30"
    }

    /**
     * Returns a hash code consistent with equals.
     *
     * @return the minutes since midnight
     */
    @Override
    public int hashCode() {
        return startMinute;
    }

    /**
     * Shows the time on the 24-hour clock with two digits each.
     *
     * @return e.g. "09:30"
     */
    @Override
    public String toString() {
        return String.format("%02d:%02d", getHour(), getMinute());
    }
}
