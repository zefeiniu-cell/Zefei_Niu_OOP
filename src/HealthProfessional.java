/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * The base of every health professional in the clinic.
 *
 * Think of this class as the ground floor. It only holds what every type
 * shares: a staff ID, a name and the days they work. Each subclass builds
 * its own floor on top of it with super(...).
 *
 * Why abstract: a real clinic never has a "health professional" with no
 * profession, so Java shouldn't let me create one either. The abstract
 * methods also force every type to answer the same three questions in its
 * own way: what are you, what do you offer, how should a patient prepare.
 *
 * The six supplied concrete types check their details before create(...)
 * constructs an object. Their constructors are private. A future subclass
 * must follow the same rule; abstract alone does not validate its input.
 * The ID and name cannot change. The roster can be edited before joining
 * a clinic, then stays fixed for that registered clinic session.
 *
 * Each professional prints as one table row: ID, name, type, days, then the
 * details that only its own type has.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public abstract class HealthProfessional { // [Abstraction] a professional with no profession can't be created

    /** Digits in a staff ID, e.g. "100101". */
    public static final int ID_LENGTH = 6;

    /** Three or more working days in a row print as a range, e.g. "Mon-Fri". */
    private static final int MIN_DAYS_FOR_RANGE = 3;

    /** Roster used when none is given: a normal full-time week. */
    private static final String[] FULL_TIME_DAYS =
            {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    /** Staff ID given by the clinic. */
    private final String id; // a label, not a number: int would turn "012345" into 12345

    /** Full name. */
    private final String name; // [Encapsulation] final with no setter, so it can't change after creation

    /** Working days: picture 7 boxes, Monday first. A ticked box (true) means a working day. */
    private final boolean[] workingDays = new boolean[Week.DAYS_IN_WEEK]; // one yes/no box per day

    /** Keeps the registered session's availability from changing behind its bookings. */
    private boolean rosterLocked;

    /**
     * Sets up the shared part of a professional.
     * Subclasses call this after validating their details. The constructor
     * itself trusts that contract, so a new subclass must also validate first.
     *
     * @param id          six-digit staff ID (already checked)
     * @param name        the name (already checked)
     * @param workingDays the days worked (already checked)
     */
    public HealthProfessional(String id, String name, String[] workingDays) {
        this.id = id.trim();
        this.name = Validator.tidy(name);
        markWorkingDays(workingDays);
    }

    /**
     * Sets up the shared part of a full-time professional (Monday to Friday).
     *
     * @param id   six-digit staff ID (already checked)
     * @param name the name (already checked)
     */
    public HealthProfessional(String id, String name) {
        this(id, name, FULL_TIME_DAYS); // one constructor does the set-up, so the two can't end up different
    }

    /**
     * Checks a staff ID: digits only, exactly six of them.
     *
     * @param id the ID to check
     * @return "" if valid, otherwise the reason
     */
    public static String checkId(String id) {
        return Validator.checkDigitCode("Professional ID", id, ID_LENGTH);
    }

    /**
     * Checks a professional's name.
     *
     * @param name the name to check
     * @return "" if valid, otherwise the reason
     */
    public static String checkName(String name) {
        return Validator.checkPersonName("Professional name", name);
    }

    /**
     * Checks a roster: at least one day, and every entry is a real day.
     * Listing a day twice is fine, because ticking a box twice changes nothing.
     *
     * @param days the days to check, e.g. {"Monday", "Thu"}
     * @return "" if valid, otherwise the reason
     */
    public static String checkWorkingDays(String[] days) {
        if (days == null || days.length == 0) {
            return "At least one working day is required."; // someone with no days could never be booked
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
     * @param id          the staff ID
     * @param name        the name
     * @param workingDays the roster
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
     * @param id   the staff ID
     * @param name the name
     * @return "" if both are valid, otherwise every problem found
     */
    public static String checkCommonDetails(String id, String name) {
        return Validator.addProblem(checkId(id), checkName(name));
    }

    /**
     * Gives the titles and dashes for the top of the professionals table.
     *
     * @return two lines: titles, then dashes
     */
    public static String tableHeader() {
        return headerTitles() + "\n" + headerRule();
    }

    /**
     * Gives the column titles of the professionals table.
     *
     * @return one line of titles
     */
    public static String headerTitles() {
        return Display.cell("ID", Display.ID_WIDTH) + Display.cell("NAME", Display.NAME_WIDTH)
                + Display.cell("TYPE", Display.TYPE_WIDTH) + Display.cell("DAYS", Display.DAYS_WIDTH)
                + "DETAILS";
    }

    /**
     * Gives the dashes that go under the column titles.
     *
     * @return one line of dashes
     */
    public static String headerRule() {
        return Display.dashes(Display.ID_WIDTH) + Display.dashes(Display.NAME_WIDTH)
                + Display.dashes(Display.TYPE_WIDTH) + Display.dashes(Display.DAYS_WIDTH)
                + "--------------------";
    }

    /**
     * Gives the staff ID.
     *
     * @return the staff ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gives the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gives the working days in week order.
     *
     * @return e.g. {"Monday", "Wednesday", "Friday"}, as a new array
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
        return names; // [Encapsulation] a new array each time: changing it can't touch the real roster
    }

    /**
     * Counts the days worked per week.
     *
     * @return 1 to 7
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
     * Checks whether this professional works on a day.
     *
     * @param day e.g. "Wednesday" or "wed"
     * @return true if they work that day; false for an invalid day
     */
    public boolean worksOn(String day) {
        int index = Week.indexOf(day);
        return index >= 0 && workingDays[index]; // test the index first, so workingDays[-1] never runs
    }

    /**
     * Lists the working days in full, for messages.
     *
     * @return e.g. "Monday, Wednesday, Friday"
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
     * Lists the working days in short form, for tables.
     * Three or more days in a row become a range, which keeps the column narrow.
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
                    last++; // walk to the end of this run of working days
                }
                if (last - day + 1 >= MIN_DAYS_FOR_RANGE) {
                    text = joinWithSpace(text, Week.shortNameAt(day) + "-" + Week.shortNameAt(last));
                } else {
                    for (int i = day; i <= last; i++) {
                        text = joinWithSpace(text, Week.shortNameAt(i)); // "Mon Tue", not "Mon-Tue"
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
     * Gives a one-line description for messages.
     *
     * @return e.g. "Dr Elias Magee (General Practitioner, ID 100101)"
     */
    public String getSummary() {
        return name + " (" + getProfessionType() + ", ID " + id + ")";
    }

    /**
     * Changes the roster before the professional joins a clinic session.
     * A registered roster or a bad replacement is refused, keeping the old days.
     *
     * @param newDays the new working days
     * @return "" if changed, otherwise why it was refused
     */
    public String updateWorkingDays(String[] newDays) {
        if (rosterLocked) {
            return "Roster unchanged: it is fixed for this registered clinic session.";
        }
        String problem = checkWorkingDays(newDays);
        if (!problem.isEmpty()) {
            return problem; // [Encapsulation] the only way to change the roster checks it first
        }
        markWorkingDays(newDays);
        return "";
    }

    /**
     * Keeps the weekly roster fixed once a clinic registers this object.
     * A new session needing a different roster should use a new staff object.
     */
    public final void lockWorkingDays() {
        rosterLocked = true;
    }

    /**
     * Ticks the boxes for days that were already checked.
     * It is private on purpose: the constructor calls it, and if a subclass
     * could override it, subclass code would run before the subclass is ready.
     *
     * @param days valid day names
     */
    private void markWorkingDays(String[] days) {
        for (int i = 0; i < Week.DAYS_IN_WEEK; i++) {
            workingDays[i] = false; // a new roster replaces the old one, it doesn't add to it
        }
        for (int i = 0; i < days.length; i++) {
            workingDays[Week.indexOf(days[i])] = true;
        }
    }

    /**
     * Adds a word to a list separated by spaces.
     *
     * @param text the list so far
     * @param word the word to add
     * @return the longer list
     */
    private static String joinWithSpace(String text, String word) {
        if (text.isEmpty()) {
            return word;
        }
        return text + " " + word;
    }

    /**
     * Names the kind of professional, e.g. "General Practitioner".
     *
     * @return the profession type
     */
    public abstract String getProfessionType(); // [Abstraction] declared here, answered by every subclass

    /**
     * Describes the services this kind of professional gives.
     *
     * @return a short description
     */
    public abstract String describeServices();

    /**
     * Tells a patient how to get ready for a visit.
     * It is shown when a booking is made.
     *
     * @return the advice
     */
    public abstract String getPreparationAdvice();

    /**
     * Gives the shared columns of this professional's table row.
     * Each subclass adds its own details after these.
     *
     * @return ID, name, type and days, padded to the columns
     */
    @Override
    public String toString() {
        return Display.cell(id, Display.ID_WIDTH)
                + Display.cell(name, Display.NAME_WIDTH)
                + Display.cell(getProfessionType(), Display.TYPE_WIDTH) // [Polymorphism] each type answers
                + Display.cell(getWorkingDaysShort(), Display.DAYS_WIDTH);
    }

    /**
     * Two professionals are the same person when they have the same staff ID.
     * It is final, so no subclass can change what "same person" means.
     *
     * @param other the object to compare with
     * @return true if it is a professional with the same ID
     */
    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HealthProfessional)) { // also covers null
            return false;
        }
        HealthProfessional that = (HealthProfessional) other;
        return id.equals(that.id); // staff IDs are unique, so the ID alone decides
    }

    /**
     * Gives a hash code that matches equals: same ID, same code.
     *
     * @return the hash code of the staff ID
     */
    @Override
    public final int hashCode() {
        return id.hashCode(); // built from the same field as equals, or contains() could miss a match
    }
}
