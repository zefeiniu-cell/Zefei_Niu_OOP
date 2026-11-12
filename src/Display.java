/*
 * =============================================================================
 *  System  : Arcana Health Centre - Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : Display.java
 *  Purpose : One table layout shared by every class that prints itself.
 * =============================================================================
 */

/**
 * One table layout shared by every class that prints itself.
 * <p>
 * Professionals, patients and appointments all print as rows of the same
 * table, so the column widths are defined once here. A patient's line can
 * then sit exactly under the matching columns of a professional's line, and
 * changing a width changes every table in the system at once.
 * Only plain ASCII characters are used, because a marker's console may not
 * display box-drawing or tick symbols correctly.
 * </p>
 *
 * @author Zefei Niu
 * @version 2.0
 */
public class Display {

    /** Width of the time column, e.g. "09:30". */
    public static final int TIME_WIDTH = 5;

    /** Width of the ID column: staff IDs and patient numbers. */
    public static final int ID_WIDTH = 6;

    /** Width of the name column. */
    public static final int NAME_WIDTH = 18;

    /** Width of the type column (also used for a patient's mobile number). */
    public static final int TYPE_WIDTH = 20;

    /** Width of the working-days column. */
    public static final int DAYS_WIDTH = 15;

    /** Space between two columns. */
    public static final String GAP = "  ";

    /**
     * Prevents objects of this class from being created.
     * Display only offers static helpers, so an instance would have no purpose.
     */
    private Display() {
    }

    /**
     * Pads a value to a column width and adds the gap before the next column.
     * Long values are never cut, so no detail is ever hidden from the reader.
     *
     * @param text  the value to show
     * @param width the column width
     * @return the padded value followed by the column gap
     */
    public static String cell(String text, int width) {
        return String.format("%-" + width + "s", text) + GAP;
    }

    /**
     * Draws the underline for one column.
     *
     * @param width the column width
     * @return a run of dashes as wide as the column, followed by the column gap
     */
    public static String dashes(int width) {
        String line = "";
        for (int i = 0; i < width; i++) {
            line += '-';
        }
        return line + GAP;
    }

    /**
     * Turns a true/false flag into "Yes" or "No" for display.
     *
     * @param flag the flag to show
     * @return "Yes" when the flag is true, otherwise "No"
     */
    public static String yesNo(boolean flag) {
        return flag ? "Yes" : "No";
    }

    /**
     * Works out a whole-number percentage without dividing by zero.
     *
     * @param part  the part, e.g. booked slots
     * @param whole the whole, e.g. all slots
     * @return the rounded-down percentage, or 0 when the whole is 0
     */
    public static int percent(int part, int whole) {
        if (whole == 0) {
            return 0;                                               // an empty schedule must not crash the summary
        }
        return part * 100 / whole;
    }
}
