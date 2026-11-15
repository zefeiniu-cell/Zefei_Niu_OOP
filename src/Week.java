/**
 * Arcana Health Centre - Appointment System.
 *
 * Weekday names and conversion between full and short names.
 *
 * @author Zefei Niu
 */
public class Week {
    /* Days in a week, also the size of every per-day array. */
    public static final int DAYS_IN_WEEK = 7;

    /* Length of a short day name, e.g. "Mon". */
    public static final int SHORT_NAME_LENGTH = 3;

    /* The day names, Monday first. */
    private static final String[] DAY_NAMES = {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    /** No instances are needed for these static methods. */
    private Week() {
    }

    /**
     * Finds where a day sits in the week. Case and extra spaces don't matter, and "Mon", "Tue"
     * etc. work too.
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

    /** Checks whether a text is a day of the week. */
    public static boolean isValidDay(String dayName) {
        return indexOf(dayName) >= 0;
    }

    /**
     * Gives the full name of the day at a position.
     *
     * @return the day name, or "" if the position is outside the week
     */
    public static String nameAt(int index) {
        if (index < 0 || index >= DAYS_IN_WEEK) {
            return "";
        }
        return DAY_NAMES[index];
    }

    /**
     * Gives the three-letter name of the day at a position.
     *
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
     * @return e.g. "Wednesday", or "" if it is not a day
     */
    public static String normalise(String dayName) {
        return nameAt(indexOf(dayName));
    }
}
