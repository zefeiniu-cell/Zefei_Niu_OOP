/**
 * A clock time stored as minutes after midnight.
 * It can be compared in time order and displayed as HH:mm.
 *
 * @author Zefei Niu
 */
public class TimeSlot implements Comparable<TimeSlot> {
    /** Minutes in an hour. */
    public static final int MINUTES_PER_HOUR = 60;

    /** Minutes in a day. A start time must be less than this. */
    public static final int MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR;

    /* Minutes since midnight, e.g. 570 for 09:30. */
    private final int startMinute;

    /* Builds a time from minutes since midnight. Outside code uses ofMinutes(...), of(...) or parse(...). */
    private TimeSlot(int startMinute) {
        this.startMinute = startMinute;
    }

    /**
     * Creates a time from minutes since midnight.
     *
     * @return the time, or null if it is outside one day
     */
    public static TimeSlot ofMinutes(int minutes) {
        if (minutes < 0 || minutes >= MINUTES_PER_DAY) {
            return null;
        }
        return new TimeSlot(minutes);
    }

    /**
     * Creates a time from an hour and a minute (24-hour clock).
     *
     * @return the time, or null if either part is out of range
     */
    public static TimeSlot of(int hour, int minute) {
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            return null;
        }
        return new TimeSlot(hour * MINUTES_PER_HOUR + minute);
    }

    /**
     * Says what is wrong with a typed time, if anything. "09:30" and "9:30" are both accepted
     * (24-hour clock).
     *
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
        // Both parts have already passed the length and digit checks.
        int hour = Integer.parseInt(hourText);
        int minute = Integer.parseInt(minuteText);
        if (hour > 23 || minute > 59) {
            return "Time \"" + time + "\" is not a real time of day.";
        }
        return "";
    }

    /**
     * Reads a typed time such as "09:30".
     *
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

    /** Gives the minutes since midnight. */
    public int getStartMinute() {
        return startMinute;
    }

    /** Gives the hour (24-hour clock). */
    public int getHour() {
        return startMinute / MINUTES_PER_HOUR;
    }

    /** Gives the minute within the hour. */
    public int getMinute() {
        return startMinute % MINUTES_PER_HOUR;
    }

    /** Checks whether this time is earlier than another. */
    public boolean isBefore(TimeSlot other) {
        return compareTo(other) < 0;
    }

    /** Puts times in clock order, earliest first. */
    @Override
    public int compareTo(TimeSlot other) {
        return Integer.compare(startMinute, other.startMinute);
    }

    /** Two times are equal when they are the same minute of the day. */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TimeSlot)) {
            return false;
        }
        return startMinute == ((TimeSlot) other).startMinute;
    }

    /** Gives a hash code that matches equals. */
    @Override
    public int hashCode() {
        return startMinute;
    }

    /** Shows the time with two digits each, e.g. "09:30". */
    @Override
    public String toString() {
        return String.format("%02d:%02d", getHour(), getMinute());
    }
}
