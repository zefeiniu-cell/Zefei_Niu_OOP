/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * A patient registered at the clinic.
 *
 * Why Patient is its own class and not two Strings inside Appointment: one
 * person can hold several appointments in a day. As a class, the name and
 * mobile rules live in one place, the patient is registered once, and every
 * appointment just points to the same Patient (has-a, like a BankAccount
 * has-a Customer in Activity 2.3).
 *
 * The two identifiers follow different rules on purpose:
 * - The patient number ("P0001") is made by the clinic, so it can't be wrong.
 * - The mobile number is typed by a person, so it is checked and then stored
 *   in one plain form. It is a String, not a number: it starts with 0, nobody
 *   does maths with it, and people type it with spaces.
 *
 * @author Zefei Niu
 * @version 1.0
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

    /** Patient number given by the clinic, e.g. "P0001". */
    private final String patientNumber; // [Encapsulation] all fields final: a record can't change by mistake

    /** Full name. */
    private final String name;

    /** Mobile number as 10 plain digits, e.g. "0412345678". */
    private final String mobile;

    /**
     * Builds a patient from checked details. Outside code uses create(...).
     *
     * @param patientNumber the clinic's number (already checked)
     * @param name          the name (already checked)
     * @param mobile        the mobile number (already checked)
     */
    private Patient(String patientNumber, String name, String mobile) {
        this.patientNumber = patientNumber.trim();
        this.name = Validator.tidy(name);
        this.mobile = normaliseMobile(mobile); // one stored form, so "0412 345 678" and "0412345678" match
    }

    /**
     * Checks a patient's name.
     *
     * @param name the name to check
     * @return "" if valid, otherwise the reason
     */
    public static String checkName(String name) {
        return Validator.checkPersonName("Patient name", name);
    }

    /**
     * Checks an Australian mobile number. Spaces and hyphens are fine and get removed.
     *
     * @param mobile the number as typed, e.g. "0412 345 678"
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
     * @param patientNumber the number to check
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
     * @param patientNumber the clinic's number
     * @param name          the name
     * @param mobile        the mobile number
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
     * @param patientNumber the clinic's number, e.g. "P0001"
     * @param name          the name
     * @param mobile        the mobile number, spaces allowed
     * @return the new patient, or null if the details are invalid
     */
    public static Patient create(String patientNumber, String name, String mobile) {
        if (!check(patientNumber, name, mobile).isEmpty()) {
            return null; // [Encapsulation] bad details never turn into an object
        }
        return new Patient(patientNumber, name, mobile);
    }

    /**
     * Turns a typed mobile number into the stored form.
     *
     * @param mobile e.g. "0412-345 678"
     * @return the plain digits, e.g. "0412345678"
     */
    public static String normaliseMobile(String mobile) {
        return Validator.removeSeparators(mobile);
    }

    /**
     * Groups a stored mobile number so it is easy to read: "0412 345 678".
     *
     * @param mobile 10 plain digits
     * @return the grouped number, or the input as it was if it isn't 10 digits
     */
    public static String formatMobile(String mobile) {
        if (mobile == null || mobile.length() != MOBILE_LENGTH) {
            return mobile;
        }
        return mobile.substring(0, 4) + " " + mobile.substring(4, 7) + " " + mobile.substring(7);
    }

    /**
     * Gives the patient number.
     *
     * @return e.g. "P0001"
     */
    public String getPatientNumber() {
        return patientNumber;
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
     * Gives the mobile number in its stored form.
     *
     * @return 10 plain digits
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Checks whether typed details belong to this patient.
     * Upper/lower case and extra spaces in the name don't matter, and
     * neither do separators in the mobile number.
     *
     * @param otherName   the name as typed
     * @param otherMobile the mobile number as typed
     * @return true if both match
     */
    public boolean matches(String otherName, String otherMobile) {
        return name.equalsIgnoreCase(Validator.tidy(otherName))
                && mobile.equals(normaliseMobile(otherMobile));
    }

    /**
     * Gives a one-line description for messages.
     *
     * @return e.g. "Romy Lovell (P0001)"
     */
    public String getSummary() {
        return name + " (" + patientNumber + ")";
    }

    /**
     * Gives the patient as a table row. The columns line up under the ID,
     * name and type columns of a professional's row.
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
     * Two patient records are the same person when the name (any case) and
     * the mobile number both match.
     * Why not the mobile alone: families often share one phone. Romy and Theo
     * Lovell in the demo share a number but are clearly two different people.
     *
     * @param other the object to compare with
     * @return true if it is a patient with the same name and mobile
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
     * Gives a hash code that matches equals.
     *
     * @return a code based on the mobile, which all equal patient objects share
     */
    @Override
    public int hashCode() {
        return mobile.hashCode(); // equal patients must share this value, including Unicode case pairs
    }
}
