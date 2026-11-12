/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * The seven days of the week, kept in one place.
 *
 * Why this class: every professional has working days and every clinic has
 * a "today", so the system needs one agreed way to write a day. Without it,
 * "Mon", "monday" and "Monday" would be three different Strings and a
 * simple equals() check would fail. An enum would also solve this, but enum
 * is not taught in Modules 1-3, so I use a private array and static methods.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Week {

    /** Days in a week, also the size of every per-day array. */
    public static final int DAYS_IN_WEEK = 7;

    /** Length of a short day name, e.g. "Mon". */
    public static final int SHORT_NAME_LENGTH = 3;

    /** The day names, Monday first. */
    private static final String[] DAY_NAMES = { // [Encapsulation] private, so no other class can rename a day
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    /**
     * Stops anyone creating a Week object.
     * Everything here is static, so an object would have nothing to do.
     */
    private Week() {
    }

    /**
     * Finds where a day sits in the week.
     * Case and extra spaces don't matter, and "Mon", "Tue" etc. work too.
     *
     * @param dayName the day, e.g. "Wednesday" or "wed"
     * @return 0 for Monday up to 6 for Sunday, or -1 if it is not a day
     */
    public static int indexOf(String dayName) {
        if (dayName == null) {
            return -1;
        }
        String text = dayName.trim();
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            String shortName = DAY_NAMES[i].substring(0, SHORT_NAME_LENGTH);
            if (DAY_NAMES[i].equalsIgnoreCase(text) || shortName.equalsIgnoreCase(text)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks whether a text is a day of the week.
     *
     * @param dayName the text to check
     * @return true if it is a full or short day name
     */
    public static boolean isValidDay(String dayName) {
        return indexOf(dayName) >= 0;
    }

    /**
     * Gives the full name of the day at a position.
     *
     * @param index 0 for Monday up to 6 for Sunday
     * @return the day name, or "" if the position is outside the week
     */
    public static String nameAt(int index) {
        if (index < 0 || index >= DAYS_IN_WEEK) {
            return ""; // an empty name prints fine; a bad index would crash
        }
        return DAY_NAMES[index];
    }

    /**
     * Gives the three-letter name of the day at a position.
     *
     * @param index 0 for Monday up to 6 for Sunday
     * @return e.g. "Wed", or "" if the position is outside the week
     */
    public static String shortNameAt(int index) {
        String name = nameAt(index);
        if (name.isEmpty()) {
            return "";
        }
        return name.substring(0, SHORT_NAME_LENGTH);
    }

    /**
     * Turns any accepted spelling of a day into its full name.
     *
     * @param dayName the day as typed, e.g. " wed "
     * @return e.g. "Wednesday", or "" if it is not a day
     */
    public static String normalise(String dayName) {
        return nameAt(indexOf(dayName)); // nameAt(-1) already returns "", so no extra check
    }
}
