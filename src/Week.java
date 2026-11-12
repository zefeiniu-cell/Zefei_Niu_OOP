/*
 * =============================================================================
 *  System  : Health Clinic Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : Week.java
 *  Purpose : The fixed set of seven day names and their positions in the week.
 * =============================================================================
 */

/**
 * The fixed set of the seven days of the week.
 * <p>
 * Enum is not part of Modules 1-3, so the day names live in one private
 * constant array, and every other class works with days only through the
 * static methods below. Because every conversion goes through this one class,
 * a day can never be spelled two different ways in the system:
 * "monday", " Mon " and "MONDAY" all become "Monday".
 * </p>
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Week {

    /** Number of days in a week; also the size of every per-day array. */
    public static final int DAYS_IN_WEEK = 7;

    /** Length of a short day name such as "Mon". */
    private static final int SHORT_NAME_LENGTH = 3;

    /** The fixed day names, Monday first. Private, so no other class can change them. */
    private static final String[] DAY_NAMES = {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    /**
     * Prevents objects of this class from being created.
     * Week only offers static helpers, so an instance would have no purpose.
     */
    private Week() {
    }

    /**
     * Finds the position of a day in the week.
     * Case and surrounding spaces are ignored, and the short form
     * ("Mon", "Tue", ...) is accepted as well as the full name.
     *
     * @param dayName the day to look up, e.g. "Wednesday" or "wed"
     * @return 0 for Monday up to 6 for Sunday, or -1 if it is not a day
     */
    public static int indexOf(String dayName) {
        if (dayName == null) {                                      // [Guard] nothing to look up
            return -1;
        }
        String text = dayName.trim();
        for (int i = 0; i < DAYS_IN_WEEK; i++) {
            String shortName = DAY_NAMES[i].substring(0, SHORT_NAME_LENGTH);
            if (DAY_NAMES[i].equalsIgnoreCase(text) || shortName.equalsIgnoreCase(text)) {
                return i;                                           // found its position
            }
        }
        return -1;                                                  // not a day of the week
    }

    /**
     * Tells whether a text names a day of the week.
     *
     * @param dayName the text to test, e.g. "Friday"
     * @return true if it is a full or short day name
     */
    public static boolean isValidDay(String dayName) {
        return indexOf(dayName) >= 0;
    }

    /**
     * Returns the full name of the day at a position.
     *
     * @param index 0 for Monday up to 6 for Sunday
     * @return the day name, or an empty text if the position is outside the week
     */
    public static String nameAt(int index) {
        if (index < 0 || index >= DAYS_IN_WEEK) {                   // [Guard] outside Monday..Sunday
            return "";
        }
        return DAY_NAMES[index];
    }

    /**
     * Converts any accepted spelling of a day into its standard full name.
     *
     * @param dayName the day as typed, e.g. " wed "
     * @return the standard name, e.g. "Wednesday", or an empty text if it is not a day
     */
    public static String normalise(String dayName) {
        return nameAt(indexOf(dayName));                            // -1 becomes "" through nameAt
    }
}
