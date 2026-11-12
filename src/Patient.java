/*
 * =============================================================================
 *  System  : Arcana Health Centre - Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : Patient.java
 *  Purpose : A registered patient: patient number, name and mobile number.
 * =============================================================================
 */

/**
 * A patient registered at the clinic.
 * <p>
 * Patient is its own class rather than two text fields inside Appointment:
 * the same person can hold several appointments, the name and mobile rules
 * live in one place, and an appointment simply has-a Patient (composition).
 * </p>
 * <p>
 * Two kinds of identifier are kept, on purpose of different types and rules.
 * The patient number ("P0001") is issued by the clinic when the patient first
 * books, so it is correct by construction. The mobile number is typed by a
 * person, so it is checked and stored in one plain form: text, not a number,
 * because it starts with 0, is never used in arithmetic, and is often typed
 * with spaces.
 * </p>
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Patient {

    /** Digits in an Australian mobile number. */
    public static final int MOBILE_LENGTH = 10;

    /** Every Australian mobile number starts with these digits. */
    public static final String MOBILE_PREFIX = "04";

    /** First character of every patient number. */
    public static final String NUMBER_PREFIX = "P";

    /** Digits after the prefix in a patient number. */
    public static final int NUMBER_DIGITS = 4;

    /** Clinic-issued patient number, e.g. "P0001". */
    private final String patientNumber;                             // [Encapsulation] all fields final: a patient record cannot be corrupted

    /** Full name, tidied to single spaces. */
    private final String name;

    /** Mobile number as ten plain digits, e.g. "0412345678". */
    private final String mobile;

    /**
     * Builds a patient from checked details. Private: use create(...).
     *
     * @param patientNumber the clinic-issued number, already validated
     * @param name          the patient's name, already validated
     * @param mobile        the mobile number, already validated
     */
    private Patient(String patientNumber, String name, String mobile) {
        this.patientNumber = patientNumber.trim();
        this.name = Validator.tidy(name);
        this.mobile = normaliseMobile(mobile);                      // one stored form, so "0412 345 678" and "0412345678" match
    }

    /**
     * Checks a patient's name using the shared name rule.
     *
     * @param name the name to check
     * @return an empty text if valid, otherwise the reason it is not
     */
    public static String checkName(String name) {
        return Validator.checkPersonName("Patient name", name);
    }

    /**
     * Checks an Australian mobile number. Spaces and hyphens are allowed in
     * the input and ignored.
     *
     * @param mobile the number as typed, e.g. "0412 345 678"
     * @return an empty text if valid, otherwise the reason it is not
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
     * Checks a patient number: "P" followed by four digits.
     *
     * @param patientNumber the number to check
     * @return an empty text if valid, otherwise the reason it is not
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
     * Explains what, if anything, is wrong with a patient's details.
     *
     * @param patientNumber the clinic-issued number to check
     * @param name          the name to check
     * @param mobile        the mobile number to check
     * @return an empty text if valid, otherwise every problem found
     */
    public static String check(String patientNumber, String name, String mobile) {
        String problems = checkPatientNumber(patientNumber);
        problems = Validator.addProblem(problems, checkName(name));
        problems = Validator.addProblem(problems, checkMobile(mobile));
        return problems;
    }

    /**
     * Creates a patient, or nothing if any detail is invalid.
     *
     * @param patientNumber the clinic-issued number, e.g. "P0001"
     * @param name          the patient's name
     * @param mobile        the mobile number, spaces allowed
     * @return the new patient, or null if the details are invalid
     */
    public static Patient create(String patientNumber, String name, String mobile) {
        if (!check(patientNumber, name, mobile).isEmpty()) {
            return null;                                            // [Encapsulation] invalid details never become an object
        }
        return new Patient(patientNumber, name, mobile);
    }

    /**
     * Converts a typed mobile number to its stored form.
     *
     * @param mobile the number as typed, e.g. "0412-345 678"
     * @return the plain digits, e.g. "0412345678"
     */
    public static String normaliseMobile(String mobile) {
        return Validator.removeSeparators(mobile);
    }

    /**
     * Formats a stored mobile number for reading: "0412 345 678".
     *
     * @param mobile ten plain digits
     * @return the grouped number, or the input unchanged if it is not ten digits
     */
    public static String formatMobile(String mobile) {
        if (mobile == null || mobile.length() != MOBILE_LENGTH) {
            return mobile;
        }
        return mobile.substring(0, 4) + " " + mobile.substring(4, 7) + " " + mobile.substring(7);
    }

    /**
     * Returns the patient number.
     *
     * @return e.g. "P0001"
     */
    public String getPatientNumber() {
        return patientNumber;
    }

    /**
     * Returns the name.
     *
     * @return the patient's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the mobile number in its stored form.
     *
     * @return ten plain digits
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Tells whether typed details describe this patient. The name is
     * compared without regard to case or extra spaces, and the mobile number
     * without regard to separators.
     *
     * @param otherName   the name as typed
     * @param otherMobile the mobile number as typed
     * @return true if both match this patient
     */
    public boolean matches(String otherName, String otherMobile) {
        return name.equalsIgnoreCase(Validator.tidy(otherName))
                && mobile.equals(normaliseMobile(otherMobile));
    }

    /**
     * Gives a one-line description used in messages.
     *
     * @return e.g. "Romy Lovell (P0001)"
     */
    public String getSummary() {
        return name + " (" + patientNumber + ")";
    }

    /**
     * Presents the patient as a table row whose columns line up under the
     * ID, name and type columns of a professional's row.
     *
     * @return patient number, name and mobile number
     */
    @Override
    public String toString() {
        return Display.cell(patientNumber, Display.ID_WIDTH)
                + Display.cell(name, Display.NAME_WIDTH)
                + formatMobile(mobile);
    }

    /**
     * Two patient records are the same person when both the name (ignoring
     * case) and the mobile number match. Family members often share one
     * mobile number, so the number alone is not enough.
     *
     * @param other the object to compare with
     * @return true if other is a patient with the same name and mobile
     */
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

    /**
     * Returns a hash code consistent with equals.
     *
     * @return a code built from the lower-case name and the mobile number
     */
    @Override
    public int hashCode() {
        return 31 * name.toLowerCase().hashCode() + mobile.hashCode(); // lower case, because equals() ignores case
    }
}
