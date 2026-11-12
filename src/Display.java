/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * Column widths and small helpers for printing tables.
 *
 * Why one class for this: professionals, patients and appointments all
 * print as rows of the same table. If each class kept its own widths,
 * making one column wider would mean editing three files, and missing one
 * would break the alignment. With the widths here, a patient's line also
 * sits exactly under the matching columns of a professional's line.
 *
 * Only plain ASCII is used, because a Windows console can turn box-drawing
 * symbols into question marks.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Display {

    /** Width of the time column, e.g. "09:30". */
    public static final int TIME_WIDTH = 5;

    /** Width of the ID column (staff IDs and patient numbers). */
    public static final int ID_WIDTH = 6;

    /** Width of the name column. */
    public static final int NAME_WIDTH = 18;

    /** Width of the type column (a patient's mobile goes here too). */
    public static final int TYPE_WIDTH = 20;

    /** Width of the working days column. */
    public static final int DAYS_WIDTH = 15;

    /** Space between two columns. */
    public static final String GAP = "  ";

    /**
     * Stops anyone creating a Display object.
     * Everything here is static, so an object would have nothing to do.
     */
    private Display() {
    }

    /**
     * Pads a value to a column width and adds the gap after it.
     * A value longer than the column is not cut, so no detail gets hidden.
     *
     * @param text  the value to show
     * @param width the column width
     * @return the padded value plus the gap
     */
    public static String cell(String text, int width) {
        return String.format("%-" + width + "s", text) + GAP;
    }

    /**
     * Makes the dashes that go under a column title.
     *
     * @param width the column width
     * @return that many dashes plus the gap
     */
    public static String dashes(int width) {
        String line = "";
        for (int i = 0; i < width; i++) {
            line += '-';
        }
        return line + GAP;
    }

    /**
     * Turns true/false into "Yes"/"No".
     *
     * @param flag the value to show
     * @return "Yes" or "No"
     */
    public static String yesNo(boolean flag) {
        return flag ? "Yes" : "No";
    }

    /**
     * Works out a whole-number percentage.
     *
     * @param part  e.g. booked slots
     * @param whole e.g. all slots
     * @return the percentage rounded down, or 0 if whole is 0
     */
    public static int percent(int part, int whole) {
        if (whole == 0) {
            return 0; // a clinic with no slots would otherwise divide by zero
        }
        return part * 100 / whole;
    }
}
