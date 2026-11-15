/**
 * Arcana Health Centre - Appointment System.
 *
 * Shared ID, name and weekly working days for all professionals.
 * Each profession supplies its own type, services and preparation advice.
 * The concrete classes validate their details before calling these constructors.
 *
 * @author Zefei Niu
 */
public abstract class HealthProfessional {
    /** Digits in a staff ID, e.g. "100101". */
    public static final int ID_LENGTH = 6;

    /* Three or more working days in a row print as a range, e.g. "Mon-Fri". */
    private static final int MIN_DAYS_FOR_RANGE = 3;

    /* Roster used when none is given: a normal full-time week. */
    private static final String[] FULL_TIME_DAYS =
            {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    /* Staff ID given by the clinic. */
    // A String keeps any leading zeroes.
    private final String id;

    /* Full name. */
    private final String name;

    /* Working days: picture 7 boxes, Monday first. A ticked box (true) means a working day. */
    private final boolean[] workingDays = new boolean[Week.DAYS_IN_WEEK];

    /* Keeps the registered session's availability from changing behind its bookings. */
    private boolean rosterLocked;

    /**
     * Sets up the shared part of a professional. Subclasses call this after validating their
     * details. The constructor itself trusts that contract, so a new subclass must also
     * validate first.
     */
    public HealthProfessional(String id, String name, String[] workingDays) {
        this.id = id.trim();
        this.name = Validator.tidy(name);
        markWorkingDays(workingDays);
    }

    /** Sets up the shared part of a full-time professional (Monday to Friday). */
    public HealthProfessional(String id, String name) {
        this(id, name, FULL_TIME_DAYS);
    }

    /**
     * Checks a staff ID: digits only, exactly six of them.
     *
     * @return "" if valid, otherwise the reason
     */
    public static String checkId(String id) {
        return Validator.checkDigitCode("Professional ID", id, ID_LENGTH);
    }

    /**
     * Checks a professional's name.
     *
     * @return "" if valid, otherwise the reason
     */
    public static String checkName(String name) {
        return Validator.checkPersonName("Professional name", name);
    }

    /**
     * Checks a roster: at least one day, and every entry is a real day. Listing a day twice is
     * fine, because ticking a box twice changes nothing.
     *
     * @return "" if valid, otherwise the reason
     */
    public static String checkWorkingDays(String[] days) {
        if (days == null || days.length == 0) {
            return "At least one working day is required.";
        }
        for (int i = 0; i < days.length; i++) {
            if (!Week.isValidDay(days[i])) {
                return "\"" + days[i] + "\" is not a day of the week.";
            }
        }
        return "";
    }

    /**
     * Checks all the shared details and reports every problem together.
     *
     * @return "" if all are valid, otherwise every problem found
     */
    public static String checkCommonDetails(String id, String name, String[] workingDays) {
        String problems = checkId(id);
        problems = Validator.addProblem(problems, checkName(name));
        problems = Validator.addProblem(problems, checkWorkingDays(workingDays));
        return problems;
    }

    /**
     * Checks the shared details when the default full-time roster is used.
     *
     * @return "" if both are valid, otherwise every problem found
     */
    public static String checkCommonDetails(String id, String name) {
        return Validator.addProblem(checkId(id), checkName(name));
    }

    /** Gives the titles and dashes for the top of the professionals table. */
    public static String tableHeader() {
        return headerTitles() + "\n" + headerRule();
    }

    /** Gives the column titles of the professionals table. */
    public static String headerTitles() {
        return Display.cell("ID", Display.ID_WIDTH) + Display.cell("NAME", Display.NAME_WIDTH)
                + Display.cell("TYPE", Display.TYPE_WIDTH) + Display.cell("DAYS", Display.DAYS_WIDTH)
                + "DETAILS";
    }

    /** Gives the dashes that go under the column titles. */
    public static String headerRule() {
        return Display.dashes(Display.ID_WIDTH) + Display.dashes(Display.NAME_WIDTH)
                + Display.dashes(Display.TYPE_WIDTH) + Display.dashes(Display.DAYS_WIDTH)
                + "--------------------";
    }

    /** Gives the staff ID. */
    public String getId() {
        return id;
    }

    /** Gives the name. */
    public String getName() {
        return name;
    }

    /** Gives the working days in week order. */
    public String[] getWorkingDays() {
        String[] names = new String[countWorkingDays()];
        int next = 0;
        for (int i = 0; i < Week.DAYS_IN_WEEK; i++) {
            if (workingDays[i]) {
                names[next] = Week.nameAt(i);
                next++;
            }
        }
        return names;
    }

    /** Counts the days worked per week. */
    public int countWorkingDays() {
        int count = 0;
        for (int i = 0; i < Week.DAYS_IN_WEEK; i++) {
            if (workingDays[i]) {
                count++;
            }
        }
        return count;
    }

    /** Checks whether this professional works on a day. */
    public boolean worksOn(String day) {
        int index = Week.indexOf(day);
        return index >= 0 && workingDays[index];
    }

    /** Lists the working days in full, for messages. */
    public String getWorkingDaysText() {
        String text = "";
        for (int i = 0; i < Week.DAYS_IN_WEEK; i++) {
            if (workingDays[i]) {
                if (!text.isEmpty()) {
                    text += ", ";
                }
                text += Week.nameAt(i);
            }
        }
        return text;
    }

    /**
     * Lists the working days in short form, for tables. Three or more days in a row become a
     * range, which keeps the column narrow.
     */
    public String getWorkingDaysShort() {
        String text = "";
        int day = 0;
        while (day < Week.DAYS_IN_WEEK) {
            if (workingDays[day]) {
                int last = day;
                while (last + 1 < Week.DAYS_IN_WEEK && workingDays[last + 1]) {
                    last++;
                }
                if (last - day + 1 >= MIN_DAYS_FOR_RANGE) {
                    text = joinWithSpace(text, Week.shortNameAt(day) + "-" + Week.shortNameAt(last));
                } else {
                    for (int i = day; i <= last; i++) {
                        text = joinWithSpace(text, Week.shortNameAt(i));
                    }
                }
                day = last + 1;
            } else {
                day++;
            }
        }
        return text;
    }

    /** Gives a one-line description for messages. */
    public String getSummary() {
        return name + " (" + getProfessionType() + ", ID " + id + ")";
    }

    /**
     * Changes the roster before the professional joins a clinic session. A registered roster
     * or a bad replacement is refused, keeping the old days.
     *
     * @return "" if changed, otherwise why it was refused
     */
    public String updateWorkingDays(String[] newDays) {
        if (rosterLocked) {
            return "Roster unchanged: it is fixed for this registered clinic session.";
        }
        String problem = checkWorkingDays(newDays);
        if (!problem.isEmpty()) {
            return problem;
        }
        markWorkingDays(newDays);
        return "";
    }

    /**
     * Keeps the weekly roster fixed once a clinic registers this object. A new session needing
     * a different roster should use a new staff object.
     */
    public final void lockWorkingDays() {
        rosterLocked = true;
    }

    /** Stores the validated day names in the weekly roster. */
    private void markWorkingDays(String[] days) {
        // Replace the old roster rather than adding days to it.
        for (int i = 0; i < Week.DAYS_IN_WEEK; i++) {
            workingDays[i] = false;
        }
        for (int i = 0; i < days.length; i++) {
            workingDays[Week.indexOf(days[i])] = true;
        }
    }

    /** Adds a word to a list separated by spaces. */
    private static String joinWithSpace(String text, String word) {
        if (text.isEmpty()) {
            return word;
        }
        return text + " " + word;
    }

    /** Names the kind of professional, e.g. "General Practitioner". */
    public abstract String getProfessionType();

    /** Describes the services this kind of professional gives. */
    public abstract String describeServices();

    /** Tells a patient how to get ready for a visit. It is shown when a booking is made. */
    public abstract String getPreparationAdvice();

    /**
     * Gives the shared columns of this professional's table row. Each subclass adds its own
     * details after these.
     */
    @Override
    public String toString() {
        return Display.cell(id, Display.ID_WIDTH)
                + Display.cell(name, Display.NAME_WIDTH)
                + Display.cell(getProfessionType(), Display.TYPE_WIDTH)
                + Display.cell(getWorkingDaysShort(), Display.DAYS_WIDTH);
    }

    /**
     * Two professionals are the same person when they have the same staff ID. It is final, so
     * no subclass can change what "same person" means.
     */
    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HealthProfessional)) {
            return false;
        }
        HealthProfessional that = (HealthProfessional) other;
        return id.equals(that.id);
    }

    /** Gives a hash code that matches equals: same ID, same code. */
    @Override
    public final int hashCode() {
        return id.hashCode();
    }
}
