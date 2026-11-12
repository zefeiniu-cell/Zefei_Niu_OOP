/*
 * =============================================================================
 *  System  : Health Clinic Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : Display.java
 *  Purpose : Shared helpers that format details as readable console text.
 * =============================================================================
 */

/**
 * Shared helpers that format details as readable console text.
 * <p>
 * Every class that presents itself (professionals, and later patients,
 * appointments and reports) uses the same "label : value" layout from here,
 * so the whole system prints in one consistent style and the layout is
 * defined in a single place.
 * </p>
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Display {

    /** Width every label is padded to, so that the colons line up. */
    public static final int LABEL_WIDTH = 15;

    /**
     * Prevents objects of this class from being created.
     * Display only offers static helpers, so an instance would have no purpose.
     */
    private Display() {
    }

    /**
     * Formats one detail on its own line, indented by two spaces,
     * e.g. a line break followed by "  Working days  : Monday, Friday".
     *
     * @param label the name of the detail, e.g. "Working days"
     * @param value the value to show
     * @return the formatted detail, starting with a line break
     */
    public static String line(String label, String value) {
        return "\n  " + String.format("%-" + LABEL_WIDTH + "s", label) + ": " + value;
    }

    /**
     * Turns a true/false flag into "Yes" or "No" for display.
     *
     * @param flag the flag to show
     * @return "Yes" when the flag is true, otherwise "No"
     */
    public static String yesNo(boolean flag) {
        return flag ? "Yes" : "No";                                 // ternary operator (Module 1)
    }
}
