/**
 * Arcana Health Centre - Appointment System.
 *
 * Column widths and formatting used by the console tables.
 *
 * @author Zefei Niu
 */
public class Display {
    /* Width of the time column, e.g. "09:30". */
    public static final int TIME_WIDTH = 5;

    /* Width of the ID column (staff IDs and patient numbers). */
    public static final int ID_WIDTH = 6;

    /* Width of the name column. */
    public static final int NAME_WIDTH = 18;

    /* Width of the type column (a patient's mobile goes here too). */
    public static final int TYPE_WIDTH = 20;

    /* Width of the working days column. */
    public static final int DAYS_WIDTH = 15;

    /* Space between two columns. */
    public static final String GAP = "  ";

    /** No instances are needed for these static methods. */
    private Display() {
    }

    /**
     * Pads a value to a column width and adds the gap after it. A value longer than the column
     * is not cut, so no detail gets hidden.
     */
    public static String cell(String text, int width) {
        return String.format("%-" + width + "s", text) + GAP;
    }

    /** Makes the dashes that go under a column title. */
    public static String dashes(int width) {
        String line = "";
        for (int i = 0; i < width; i++) {
            line += '-';
        }
        return line + GAP;
    }

    /** Turns true/false into "Yes"/"No". */
    public static String yesNo(boolean flag) {
        return flag ? "Yes" : "No";
    }

    /** Works out a whole-number percentage. */
    public static int percent(int part, int whole) {
        if (whole == 0) {
            return 0;
        }
        return part * 100 / whole;
    }
}
