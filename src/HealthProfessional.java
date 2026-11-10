/**
 * Abstract base class for every kind of health professional.
 * Holds the information shared by all types: id, name and working days.
 *
 * @author Your Name
 * @version 1.0
 */
public abstract class HealthProfessional {

    /** Used when no valid id is supplied (0 means "no valid id"). */
    private static final int DEFAULT_ID = 0;
    /** Used when no valid name is supplied. */
    private static final String DEFAULT_NAME = "Unknown";
    /** Used when no valid working days are supplied. */
    private static final String[] DEFAULT_DAYS =
            {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    /** Numeric id of the professional. */
    private int id;
    /** Full name of the professional. */
    private String name;
    /** Days of the week this professional works. */
    private String[] workingDays;

    /**
     * Creates a health professional with all details.
     * Invalid values are replaced by defaults, so the object is never half-built.
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
        if (isValidName(name)) {
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
     * Creates a health professional working the default days (Monday to Friday).
     *
     * @param id   positive numeric id
     * @param name non-blank name
     */
    public HealthProfessional(int id, String name) {
        this(id, name, DEFAULT_DAYS);
    }

    /** @return true if the id is a positive number */
    private static boolean isValidId(int id) {
        return id > 0;
    }

    /** @return true if the text is not null and not blank */
    private static boolean isValidName(String text) {
        return text != null && text.trim().length() > 0;
    }

    /** @return true if days is non-empty, has no blank entries and no duplicates */
    private static boolean isValidDays(String[] days) {
        if (days == null || days.length == 0) {
            return false;
        }
        // First pass: every entry must be usable before we compare them.
        for (int i = 0; i < days.length; i++) {
            if (!isValidName(days[i])) {
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

    /** @return a new array holding trimmed copies of the given days */
    private static String[] copyDays(String[] days) {
        String[] copy = new String[days.length];
        for (int i = 0; i < days.length; i++) {
            copy[i] = days[i].trim();
        }
        return copy;
    }

    /** @return the working days as readable text, e.g. "Monday, Friday" */
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

    /** @return the id */
    public int getId() {
        return id;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /** @return a copy of the working days, so callers cannot change the original */
    public String[] getWorkingDays() {
        return copyDays(workingDays);
    }

    /** @return the kind of professional, e.g. "General Practitioner" */
    public abstract String getRole();

    /** @return a description of what this kind of professional does */
    public abstract String performDuty();

    /** @return all details in one readable line; subclasses append their own */
    @Override
    public String toString() {
        return getRole() + " | ID: " + id + " | Name: " + name
                + " | Works: " + daysAsText();
    }

    /**
     * Two professionals are equal when they have the same id.
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

    /** @return hash code based on id, consistent with equals */
    @Override
    public int hashCode() {
        return id;
    }
}