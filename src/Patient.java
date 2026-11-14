/**
 * A patient record reused by their appointments.
 * The mobile is stored as digits so spaces and hyphens do not change a match.
 *
 * @author Zefei Niu
 */
public class Patient {
    /** Digits in an Australian mobile number. */
    public static final int MOBILE_LENGTH = 10;

    /** Every Australian mobile number starts with these two digits. */
    public static final String MOBILE_PREFIX = "04";

    /** First character of a patient number. */
    public static final String NUMBER_PREFIX = "P";

    /** Digits after the "P" in a patient number. */
    public static final int NUMBER_DIGITS = 4;

    /* Patient number given by the clinic, e.g. "P0001". */
    private final String patientNumber;

    /* Full name. */
    private final String name;

    /* Mobile number as 10 plain digits, e.g. "0412345678". */
    private final String mobile;

    /* Builds a patient from checked details. Outside code uses create(...). */
    private Patient(String patientNumber, String name, String mobile) {
        this.patientNumber = patientNumber.trim();
        this.name = Validator.tidy(name);
        this.mobile = normaliseMobile(mobile);
    }

    /**
     * Checks a patient's name.
     *
     * @return "" if valid, otherwise the reason
     */
    public static String checkName(String name) {
        return Validator.checkPersonName("Patient name", name);
    }

    /**
     * Checks an Australian mobile number. Spaces and hyphens are fine and get removed.
     *
     * @return "" if valid, otherwise the reason
     */
    public static String checkMobile(String mobile) {
        String digits = normaliseMobile(mobile);
        String problem = Validator.checkDigitCode("Mobile number", digits, MOBILE_LENGTH);
        if (!problem.isEmpty()) {
            return problem;
        }
        if (!digits.startsWith(MOBILE_PREFIX)) {
            return "Mobile number must start with " + MOBILE_PREFIX + " (got \"" + digits + "\").";
        }
        return "";
    }

    /**
     * Checks a patient number: "P" and then four digits.
     *
     * @return "" if valid, otherwise the reason
     */
    public static String checkPatientNumber(String patientNumber) {
        if (Validator.isBlank(patientNumber)) {
            return "Patient number is required.";
        }
        String text = patientNumber.trim();
        if (!text.startsWith(NUMBER_PREFIX)
                || !Validator.isAllDigits(text.substring(NUMBER_PREFIX.length()))
                || text.length() != NUMBER_PREFIX.length() + NUMBER_DIGITS) {
            return "Patient number must look like P0001 (got \"" + text + "\").";
        }
        return "";
    }

    /**
     * Says what is wrong with a patient's details, if anything.
     *
     * @return "" if valid, otherwise every problem found
     */
    public static String check(String patientNumber, String name, String mobile) {
        String problems = checkPatientNumber(patientNumber);
        problems = Validator.addProblem(problems, checkName(name));
        problems = Validator.addProblem(problems, checkMobile(mobile));
        return problems;
    }

    /**
     * Creates a patient.
     *
     * @return the new patient, or null if the details are invalid
     */
    public static Patient create(String patientNumber, String name, String mobile) {
        if (!check(patientNumber, name, mobile).isEmpty()) {
            return null;
        }
        return new Patient(patientNumber, name, mobile);
    }

    /** Turns a typed mobile number into the stored form. */
    public static String normaliseMobile(String mobile) {
        return Validator.removeSeparators(mobile);
    }

    /** Groups a stored mobile number so it is easy to read: "0412 345 678". */
    public static String formatMobile(String mobile) {
        if (mobile == null || mobile.length() != MOBILE_LENGTH) {
            return mobile;
        }
        return mobile.substring(0, 4) + " " + mobile.substring(4, 7) + " " + mobile.substring(7);
    }

    /** Gives the patient number. */
    public String getPatientNumber() {
        return patientNumber;
    }

    /** Gives the name. */
    public String getName() {
        return name;
    }

    /** Gives the mobile number in its stored form. */
    public String getMobile() {
        return mobile;
    }

    /**
     * Checks whether typed details belong to this patient. Upper/lower case and extra spaces
     * in the name don't matter, and neither do separators in the mobile number.
     */
    public boolean matches(String otherName, String otherMobile) {
        return name.equalsIgnoreCase(Validator.tidy(otherName))
                && mobile.equals(normaliseMobile(otherMobile));
    }

    /** Gives a one-line description for messages. */
    public String getSummary() {
        return name + " (" + patientNumber + ")";
    }

    /**
     * Gives the patient as a table row. The columns line up under the ID, name and type
     * columns of a professional's row.
     */
    @Override
    public String toString() {
        return Display.cell(patientNumber, Display.ID_WIDTH)
                + Display.cell(name, Display.NAME_WIDTH)
                + formatMobile(mobile);
    }

    /** Compares the recorded name and mobile; patient numbers belong to a clinic. */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Patient)) {
            return false;
        }
        Patient that = (Patient) other;
        return matches(that.name, that.mobile);
    }

    /** Gives a hash code that matches equals. */
    @Override
    public int hashCode() {
        // Equal patients share this mobile. Different names may still have the same hash.
        return mobile.hashCode();
    }
}
