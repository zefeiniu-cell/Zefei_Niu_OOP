/**
 * Abstract base class for every kind of health professional.
 * It holds the information shared by all types: a numeric id, a name
 * and the days of the week the professional works.
 *
 * @author ZefeiNiu
 * @version 1.0
 */
public abstract class HealthProfessional {

    /** Id used when the supplied id is not valid (0 means "no valid id"). */
    private static final int DEFAULT_ID = 0;

    /** Name used when the supplied name is blank. */
    private static final String DEFAULT_NAME = "Unknown";

    /** Working days used when none are supplied or the supplied ones are invalid. */
    private static final String[] DEFAULT_DAYS =
            {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    /** Numeric id that identifies the professional. */
    private int id;

    /** Full name of the professional. */
    private String name;

    /** Days of the week the professional works. */
    private String[] workingDays;
    /**
     * Creates a health professional with all details.
     * Invalid values are replaced by defaults, so an object is never half-built.
     *
     * @param id          positive numeric id
     * @param name        non-blank name
     * @param workingDays non-empty array of distinct, non-blank day names
     */
    public HealthProfessional(int id, String name, String[] workingDays) {
        if (isValidId(id)) {
            this.id = id;
        } else {
            this.id = DEFAULT_ID;
        }
        if (isNotBlank(name)) {
            this.name = name.trim();
        } else {
            this.name = DEFAULT_NAME;
        }
        if (isValidDays(workingDays)) {
            this.workingDays = copyDays(workingDays);
        } else {
            this.workingDays = copyDays(DEFAULT_DAYS);
        }
    }

    /**
     * Creates a health professional who works the default days (Monday to Friday).
     *
     * @param id   positive numeric id
     * @param name non-blank name
     */
    public HealthProfessional(int id, String name) {
        this(id, name, DEFAULT_DAYS);
    }


    /**
     * Checks whether an id is acceptable.
     *
     * @param id the id to check
     * @return true if the id is a positive number
     */
    private static boolean isValidId(int id) {
        return id > 0;
    }

    /**
     * Checks that a text is present and not just spaces.
     *
     * @param text the text to check
     * @return true if the text is not null and not blank
     */
    private static boolean isNotBlank(String text) {
        return text != null && text.trim().length() > 0;
    }

    /**
     * Checks that the working days are usable.
     *
     * @param days the days to check
     * @return true if days is non-empty, has no blank entries and no duplicates
     */
    private static boolean isValidDays(String[] days) {
        if (days == null || days.length == 0) {
            return false;
        }
        // First pass: every entry must be usable before we compare them.
        for (int i = 0; i < days.length; i++) {
            if (!isNotBlank(days[i])) {
                return false;
            }
        }
        // Second pass: no day may appear twice.
        for (int i = 0; i < days.length; i++) {
            for (int j = i + 1; j < days.length; j++) {
                if (days[i].trim().equals(days[j].trim())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Makes an independent copy of a days array.
     *
     * @param days the days to copy
     * @return a new array holding trimmed copies of the given days
     */
    private static String[] copyDays(String[] days) {
        String[] copy = new String[days.length];
        for (int i = 0; i < days.length; i++) {
            copy[i] = days[i].trim();
        }
        return copy;
    }

    /**
     * Joins the working days into one readable text.
     *
     * @return the days separated by commas, e.g. "Monday, Friday"
     */
    private String daysAsText() {
        String text = "";
        for (int i = 0; i < workingDays.length; i++) {
            if (i > 0) {
                text += ", ";
            }
            text += workingDays[i];
        }
        return text;
    }



    /**
     * Returns the id.
     *
     * @return the numeric id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a copy of the working days, so callers cannot change the original.
     *
     * @return a copy of the working days
     */
    public String[] getWorkingDays() {
        return copyDays(workingDays);
    }

    /**
     * Returns the kind of professional, e.g. "General Practitioner".
     *
     * @return the role name
     */
    public abstract String getRole();

    /**
     * Describes what this kind of professional does.
     * Each subclass answers in its own way.
     *
     * @return a description of the professional's duty
     */
    public abstract String performDuty();

    /**
     * Presents all shared details in one readable line.
     * Subclasses call this and append their own details.
     *
     * @return the details as text
     */
    @Override
    public String toString() {
        return getRole() + " | ID: " + id + " | Name: " + name
                + " | Works: " + daysAsText();
    }

    /**
     * Two professionals are the same person when they have the same id.
     *
     * @param obj the object to compare with
     * @return true if obj is a health professional with the same id
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HealthProfessional)) {
            return false;
        }
        HealthProfessional other = (HealthProfessional) obj;
        return id == other.id;
    }

    /**
     * Returns a hash code consistent with equals.
     *
     * @return the id, because equality is based on the id
     */
    @Override
    public int hashCode() {
        return id;
    }
}