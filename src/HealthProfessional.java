/*
 * =============================================================================
 *  System  : Health Clinic Appointment System
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
 *
 * @author Zefei Niu
 * @version 2.0
 */
public abstract class HealthProfessional {

    /** Number of digits in every staff ID, e.g. "100101". */
    public static final int ID_LENGTH = 6;

    /** Roster used when none is supplied: a standard full-time week. */
    private static final String[] FULL_TIME_DAYS =
            {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    /**
     * Staff ID issued by the clinic. Stored as text rather than int: it is a
     * label that is never used in arithmetic, and text keeps leading zeros.
     */
    private final String id;                                        // [Encapsulation] private + final: readable, never changed

    /** Full name, tidied to single spaces. */
    private final String name;                                      // [Encapsulation] no setter exists

    /** One flag per day, index 0 = Monday; true means the person works that day. */
    private final boolean[] workingDays = new boolean[Week.DAYS_IN_WEEK];

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
        this.name = Validator.tidy(name);                           // store one clean spelling
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
        this(id, name, FULL_TIME_DAYS);                             // [Reuse] constructor chaining with this(...)
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
        return Validator.addProblem(checkId(id), checkName(name));  // overload: no roster to check
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
        return names;                                               // [Encapsulation] a fresh array, so callers cannot edit the roster
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
        return index >= 0 && workingDays[index];                    // && stops before using index -1
    }

    /**
     * Lists the working days as one readable text.
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
     * Gives a one-line description used in lists and messages.
     *
     * @return e.g. "Dr Alice Wong (General Practitioner, ID 100101)"
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
            return problem;                                         // [Encapsulation] refused: roster stays as it was
        }
        markWorkingDays(newDays);
        return "";
    }

    /**
     * Sets the day flags from a list of day names that has already been checked.
     * It is private, so it cannot be overridden or called from outside.
     *
     * @param days valid day names
     */
    private void markWorkingDays(String[] days) {
        for (int i = 0; i < Week.DAYS_IN_WEEK; i++) {
            workingDays[i] = false;                                 // start from an empty roster
        }
        for (int i = 0; i < days.length; i++) {
            workingDays[Week.indexOf(days[i])] = true;              // a repeated day just sets the same flag
        }
    }

    /**
     * Names the kind of professional, e.g. "General Practitioner".
     * Every concrete type must supply its own answer.
     *
     * @return the profession type
     */
    public abstract String getProfessionType();                     // [Abstraction] declared here, defined by each type

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
     * Presents the shared details in a readable block. Subclasses add their
     * own lines by calling super.toString() and appending to it.
     *
     * @return the details, one per line
     */
    @Override
    public String toString() {
        return "[" + getProfessionType() + "] " + name              // [Polymorphism] each type fills in its own label
                + Display.line("Staff ID", id)
                + Display.line("Working days", getWorkingDaysText())
                + Display.line("Services", describeServices());
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
        if (this == other) {                                        // same object in memory
            return true;
        }
        if (!(other instanceof HealthProfessional)) {               // also handles null
            return false;
        }
        HealthProfessional that = (HealthProfessional) other;
        return id.equals(that.id);
    }

    /**
     * Returns a hash code consistent with equals: equal IDs give equal codes.
     *
     * @return the hash code of the staff ID
     */
    @Override
    public final int hashCode() {
        return id.hashCode();
    }
}
