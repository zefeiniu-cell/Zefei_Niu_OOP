/*
 * =============================================================================
 *  System  : Arcana Health Centre - Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : HealthProfessional.java
 *  Purpose : Abstract base class holding what every health professional shares.
 * =============================================================================
 */

/**
 * The common foundation of every health professional in the clinic.
 * <p>
 * It holds only what is genuinely shared by all types: a clinic-issued staff
 * ID, a name and the days of the week the person works. The class is
 * abstract because a "plain" health professional with no profession does not
 * exist in a real clinic: every object must be a specific type, and every type
 * is forced to answer the three abstract questions below in its own way.
 * </p>
 * <p>
 * Validity is guaranteed without exceptions (exception handling is Module 5).
 * The static check methods explain what is wrong with a set of details, and
 * every concrete subclass hides its constructors and builds objects only
 * through a static create(...) method that runs these checks first. An invalid
 * professional therefore can never exist, and after creation the ID and name
 * can never change. The roster can change, but only through
 * {@link #updateWorkingDays(String[])}, which refuses invalid rosters.
 * </p>
 * <p>
 * Every professional prints as one table row: ID, name, type, days, then the
 * details that only its own type has. This class prints the shared columns
 * and each subclass appends its details through super.toString().
 * </p>
 *
 * @author Zefei Niu
 * @version 3.0
 */
public abstract class HealthProfessional {                         // [Abstraction] abstract: a profession-less professional cannot exist

    /** Number of digits in every staff ID, e.g. "100101". */
    public static final int ID_LENGTH = 6;

    /** A run of at least this many working days is shown as a range, e.g. "Mon-Fri". */
    private static final int MIN_DAYS_FOR_RANGE = 3;

    /** Roster used when none is supplied: a standard full-time week. */
    private static final String[] FULL_TIME_DAYS =
            {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    /**
     * Staff ID issued by the clinic. Stored as text rather than int: it is a
     * label that is never used in arithmetic, and text keeps leading zeros.
     */
    private final String id;                                        // [Encapsulation] final and no setter: identity can never change

    /** Full name, tidied to single spaces. */
    private final String name;

    /** One flag per day, index 0 = Monday; true means the person works that day. */
    private final boolean[] workingDays = new boolean[Week.DAYS_IN_WEEK]; // a fixed set of 7 values fits a fixed-size array

    /**
     * Builds the shared part of a professional with a given roster.
     * <p>
     * The constructor is public, but this class is abstract, so it can only be
     * reached from a subclass constructor through super(...). Each subclass
     * calls {@link #checkCommonDetails(String, String, String[])} in its
     * create(...) method before constructing, so the values arriving here
     * have already been validated.
     * </p>
     *
     * @param id          six-digit staff ID, already validated
     * @param name        the professional's name, already validated
     * @param workingDays the days this person works, already validated
     */
    public HealthProfessional(String id, String name, String[] workingDays) {
        this.id = id.trim();
        this.name = Validator.tidy(name);                           // one stored spelling, so names always print and compare the same
        markWorkingDays(workingDays);
    }

    /**
     * Builds the shared part of a professional who works a standard
     * full-time week (Monday to Friday), for callers that supply no roster.
     *
     * @param id   six-digit staff ID, already validated
     * @param name the professional's name, already validated
     */
    public HealthProfessional(String id, String name) {
        this(id, name, FULL_TIME_DAYS);                             // chaining keeps the set-up code in one constructor only
    }

    /**
     * Checks a staff ID: required, digits only, exactly six digits.
     *
     * @param id the ID to check
     * @return an empty text if valid, otherwise the reason it is not
     */
    public static String checkId(String id) {
        return Validator.checkDigitCode("Professional ID", id, ID_LENGTH);
    }

    /**
     * Checks a professional's name using the shared name rule.
     *
     * @param name the name to check
     * @return an empty text if valid, otherwise the reason it is not
     */
    public static String checkName(String name) {
        return Validator.checkPersonName("Professional name", name);
    }

    /**
     * Checks a roster: at least one day, and every entry must be a real day.
     * Listing the same day twice is harmless, because each day is a single
     * true/false flag.
     *
     * @param days the days to check, e.g. {"Monday", "Thu"}
     * @return an empty text if valid, otherwise the reason it is not
     */
    public static String checkWorkingDays(String[] days) {
        if (days == null || days.length == 0) {
            return "At least one working day is required.";        // a professional with no days could never be booked
        }
        for (int i = 0; i < days.length; i++) {
            if (!Week.isValidDay(days[i])) {
                return "\"" + days[i] + "\" is not a day of the week.";
            }
        }
        return "";
    }

    /**
     * Checks every shared detail at once and reports all problems together.
     *
     * @param id          the staff ID to check
     * @param name        the name to check
     * @param workingDays the roster to check
     * @return an empty text if all are valid, otherwise every problem found
     */
    public static String checkCommonDetails(String id, String name, String[] workingDays) {
        String problems = checkId(id);
        problems = Validator.addProblem(problems, checkName(name));
        problems = Validator.addProblem(problems, checkWorkingDays(workingDays));
        return problems;
    }

    /**
     * Checks the shared details when the default full-time roster will be used.
     *
     * @param id   the staff ID to check
     * @param name the name to check
     * @return an empty text if both are valid, otherwise every problem found
     */
    public static String checkCommonDetails(String id, String name) {
        return Validator.addProblem(checkId(id), checkName(name));  // the default roster is known to be valid, so it is not checked
    }

    /**
     * Returns the heading row and underline of the professionals table,
     * matching the columns that {@link #toString()} prints.
     *
     * @return two lines: column titles and dashes
     */
    public static String tableHeader() {
        return headerTitles() + "\n" + headerRule();
    }

    /**
     * Returns the column titles of the professionals table.
     *
     * @return one line of column titles
     */
    public static String headerTitles() {
        return Display.cell("ID", Display.ID_WIDTH) + Display.cell("NAME", Display.NAME_WIDTH)
                + Display.cell("TYPE", Display.TYPE_WIDTH) + Display.cell("DAYS", Display.DAYS_WIDTH)
                + "DETAILS";
    }

    /**
     * Returns the underline of the professionals table.
     *
     * @return one line of dashes under each column
     */
    public static String headerRule() {
        return Display.dashes(Display.ID_WIDTH) + Display.dashes(Display.NAME_WIDTH)
                + Display.dashes(Display.TYPE_WIDTH) + Display.dashes(Display.DAYS_WIDTH)
                + "--------------------";
    }

    /**
     * Returns the staff ID.
     *
     * @return the six-digit staff ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name.
     *
     * @return the professional's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the working days in week order, as a new array.
     *
     * @return the day names, e.g. {"Monday", "Wednesday", "Friday"}
     */
    public String[] getWorkingDays() {
        String[] names = new String[countWorkingDays()];
        int next = 0;
        for (int i = 0; i < Week.DAYS_IN_WEEK; i++) {
            if (workingDays[i]) {
                names[next] = Week.nameAt(i);
                next++;
            }
        }
        return names;                                               // [Encapsulation] a fresh array: editing it cannot change the roster
    }

    /**
     * Counts how many days a week this professional works.
     *
     * @return a number from 1 to 7
     */
    public int countWorkingDays() {
        int count = 0;
        for (int i = 0; i < Week.DAYS_IN_WEEK; i++) {
            if (workingDays[i]) {
                count++;
            }
        }
        return count;
    }

    /**
     * Tells whether this professional works on a given day.
     *
     * @param day the day to ask about, e.g. "Wednesday" or "wed"
     * @return true if the person works that day; false for any invalid day
     */
    public boolean worksOn(String day) {
        int index = Week.indexOf(day);
        return index >= 0 && workingDays[index];                    // && stops before index -1 could be used
    }

    /**
     * Lists the working days in full, for messages.
     *
     * @return the days joined by commas, e.g. "Monday, Wednesday, Friday"
     */
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
     * Lists the working days compactly, for tables. Three or more days in a
     * row are shown as a range.
     *
     * @return e.g. "Mon-Fri", "Mon Wed Fri" or "Tue-Thu Sat"
     */
    public String getWorkingDaysShort() {
        String text = "";
        int day = 0;
        while (day < Week.DAYS_IN_WEEK) {
            if (workingDays[day]) {
                int last = day;
                while (last + 1 < Week.DAYS_IN_WEEK && workingDays[last + 1]) {
                    last++;                                         // find where this run of working days ends
                }
                if (last - day + 1 >= MIN_DAYS_FOR_RANGE) {
                    text = joinWithSpace(text, Week.shortNameAt(day) + "-" + Week.shortNameAt(last));
                } else {
                    for (int i = day; i <= last; i++) {
                        text = joinWithSpace(text, Week.shortNameAt(i)); // two days are clearer written out than as a range
                    }
                }
                day = last + 1;
            } else {
                day++;
            }
        }
        return text;
    }

    /**
     * Gives a one-line description used in messages.
     *
     * @return e.g. "Dr Elias Magee (General Practitioner, ID 100101)"
     */
    public String getSummary() {
        return name + " (" + getProfessionType() + ", ID " + id + ")";
    }

    /**
     * Replaces the roster, for example when a professional changes their
     * working days. An invalid roster is refused and nothing changes.
     *
     * @param newDays the new working days
     * @return an empty text if the roster was changed, otherwise why it was refused
     */
    public String updateWorkingDays(String[] newDays) {
        String problem = checkWorkingDays(newDays);
        if (!problem.isEmpty()) {
            return problem;                                         // [Encapsulation] the only way in refuses bad data, so the roster stays valid
        }
        markWorkingDays(newDays);
        return "";
    }

    /**
     * Sets the day flags from day names that have already been checked.
     * Private so that it cannot be overridden: it is called by the
     * constructor, and a constructor must not run subclass code.
     *
     * @param days valid day names
     */
    private void markWorkingDays(String[] days) {
        for (int i = 0; i < Week.DAYS_IN_WEEK; i++) {
            workingDays[i] = false;                                 // a roster change replaces the old roster, it does not add to it
        }
        for (int i = 0; i < days.length; i++) {
            workingDays[Week.indexOf(days[i])] = true;              // a repeated day sets the same flag again, so duplicates are harmless
        }
    }

    /**
     * Adds a word to a space-separated list.
     *
     * @param text the list so far
     * @param word the word to add
     * @return the list with the word added
     */
    private static String joinWithSpace(String text, String word) {
        if (text.isEmpty()) {
            return word;
        }
        return text + " " + word;
    }

    /**
     * Names the kind of professional, e.g. "General Practitioner".
     * Every concrete type must supply its own answer.
     *
     * @return the profession type
     */
    public abstract String getProfessionType();                     // [Abstraction] every type must answer, so no type can forget

    /**
     * Describes the services this kind of professional provides.
     * Every concrete type must supply its own answer.
     *
     * @return a short description of the services
     */
    public abstract String describeServices();

    /**
     * Tells a patient how to prepare for a visit with this professional.
     * Shown when an appointment is confirmed.
     *
     * @return the preparation advice
     */
    public abstract String getPreparationAdvice();

    /**
     * Presents the shared columns of this professional's table row.
     * Each subclass appends its own details after them.
     *
     * @return ID, name, type and days, padded to the table columns
     */
    @Override
    public String toString() {
        return Display.cell(id, Display.ID_WIDTH)
                + Display.cell(name, Display.NAME_WIDTH)
                + Display.cell(getProfessionType(), Display.TYPE_WIDTH) // [Polymorphism] the object's own type answers here
                + Display.cell(getWorkingDaysShort(), Display.DAYS_WIDTH);
    }

    /**
     * Two professionals are the same person when they have the same staff ID.
     * The method is final so that no subclass can redefine identity.
     *
     * @param other the object to compare with
     * @return true if other is a health professional with the same staff ID
     */
    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HealthProfessional)) {               // also answers false for null
            return false;
        }
        HealthProfessional that = (HealthProfessional) other;
        return id.equals(that.id);                                  // the staff ID is unique, so it alone decides identity
    }

    /**
     * Returns a hash code consistent with equals: equal IDs give equal codes.
     *
     * @return the hash code of the staff ID
     */
    @Override
    public final int hashCode() {
        return id.hashCode();                                       // same field as equals(), or equal objects could hash differently
    }
}
