/*
 * =============================================================================
 *  System  : Health Clinic Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : Specialist.java
 *  Purpose : A specialist doctor, e.g. a cardiologist or a dermatologist.
 * =============================================================================
 */

/**
 * A specialist doctor, such as a cardiologist, paediatrician or dermatologist.
 * <p>
 * One Specialist class with a specialty field is used instead of one class per
 * specialty: the specialties differ in their data (the name of the field), not
 * in their behaviour, so separate classes would only repeat the same code.
 * A specialist also records whether a GP referral is required, can prescribe
 * (Prescriber) and can order diagnostic tests.
 * </p>
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Specialist extends HealthProfessional implements Prescriber {

    /**
     * Used when the caller does not say: in Australia a patient needs a GP
     * referral to claim the Medicare rebate for a specialist visit.
     */
    private static final boolean DEFAULT_REFERRAL_REQUIRED = true;

    /** The medical field, e.g. "Cardiology". */
    private final String specialty;

    /** True if patients must bring a GP referral. */
    private final boolean referralRequired;

    /**
     * Builds a specialist with every detail. Private: use create(...).
     *
     * @param id               six-digit staff ID, already validated
     * @param name             the specialist's name, already validated
     * @param workingDays      the days the specialist consults, already validated
     * @param specialty        the medical field, already validated
     * @param referralRequired true if a GP referral is needed
     */
    private Specialist(String id, String name, String[] workingDays, String specialty,
                       boolean referralRequired) {
        super(id, name, workingDays);                               // [Inheritance] shared details set by the parent
        this.specialty = Validator.tidy(specialty);
        this.referralRequired = referralRequired;
    }

    /**
     * Builds a specialist who needs referrals (the usual case). Private: use create(...).
     *
     * @param id          six-digit staff ID, already validated
     * @param name        the specialist's name, already validated
     * @param workingDays the days the specialist consults, already validated
     * @param specialty   the medical field, already validated
     */
    private Specialist(String id, String name, String[] workingDays, String specialty) {
        this(id, name, workingDays, specialty, DEFAULT_REFERRAL_REQUIRED); // [Reuse] chain to the full constructor
    }

    /**
     * Explains what, if anything, is wrong with a specialist's details.
     *
     * @param id          the staff ID to check
     * @param name        the name to check
     * @param workingDays the roster to check
     * @param specialty   the specialty to check
     * @return an empty text if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays, String specialty) {
        return Validator.addProblem(checkCommonDetails(id, name, workingDays),
                Validator.checkRequiredText("Specialty", specialty));
    }

    /**
     * Creates a specialist, or nothing if any detail is invalid.
     *
     * @param id               six-digit staff ID
     * @param name             the specialist's name
     * @param workingDays      the days the specialist consults
     * @param specialty        the medical field, e.g. "Cardiology"
     * @param referralRequired true if a GP referral is needed
     * @return the new specialist, or null if the details are invalid
     */
    public static Specialist create(String id, String name, String[] workingDays, String specialty,
                                    boolean referralRequired) {
        if (!check(id, name, workingDays, specialty).isEmpty()) {
            return null;                                            // [Guard] invalid details: no object is built
        }
        return new Specialist(id, name, workingDays, specialty, referralRequired);
    }

    /**
     * Creates a specialist who requires a GP referral (the default),
     * or nothing if any detail is invalid.
     *
     * @param id          six-digit staff ID
     * @param name        the specialist's name
     * @param workingDays the days the specialist consults
     * @param specialty   the medical field, e.g. "Cardiology"
     * @return the new specialist, or null if the details are invalid
     */
    public static Specialist create(String id, String name, String[] workingDays, String specialty) {
        if (!check(id, name, workingDays, specialty).isEmpty()) {
            return null;                                            // [Guard] invalid details: no object is built
        }
        return new Specialist(id, name, workingDays, specialty);
    }

    /**
     * Returns the medical field.
     *
     * @return the specialty, e.g. "Cardiology"
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * Tells whether patients need a GP referral.
     *
     * @return true if a referral is required
     */
    public boolean isReferralRequired() {
        return referralRequired;
    }

    /**
     * Orders a diagnostic test, the task that is unique to a specialist.
     *
     * @param patientName the patient to be tested
     * @param testName    the test, e.g. "Echocardiogram"
     * @return the test order, or a warning explaining why it was not written
     */
    public String orderTest(String patientName, String testName) {
        String problems = Validator.addProblem(
                Validator.checkPersonName("Patient name", patientName),
                Validator.checkRequiredText("Test name", testName));
        if (!problems.isEmpty()) {
            return "[WARNING] Test not ordered: " + problems;       // [Guard] incomplete request
        }
        return "Test order: " + Validator.tidy(testName) + " for " + Validator.tidy(patientName)
                + ", requested by " + getName() + " (" + specialty + ").";
    }

    /**
     * Names this kind of professional, including the specialty.
     *
     * @return e.g. "Specialist - Cardiology"
     */
    @Override
    public String getProfessionType() {
        return "Specialist - " + specialty;
    }

    /**
     * Describes what a specialist provides.
     *
     * @return a short description of the specialist's services
     */
    @Override
    public String describeServices() {
        return "Expert assessment and treatment in " + specialty;
    }

    /**
     * Tells a patient how to prepare; the advice depends on the referral rule.
     *
     * @return the preparation advice
     */
    @Override
    public String getPreparationAdvice() {
        if (referralRequired) {
            return "Bring your GP referral letter and any recent test results.";
        }
        return "Bring any recent test results; no referral is needed.";
    }

    /**
     * Describes what a specialist may prescribe.
     *
     * @return the prescribing scope
     */
    @Override
    public String getPrescribingScope() {
        return "Medicines related to " + specialty;
    }

    /**
     * Writes a prescription signed by this specialist.
     *
     * @param patientName the patient the medicine is for
     * @param medication  the medicine and dose
     * @return the prescription text, or a warning explaining why it was not written
     */
    @Override
    public String prescribe(String patientName, String medication) {
        String problems = Validator.addProblem(
                Validator.checkPersonName("Patient name", patientName),
                Validator.checkRequiredText("Medication", medication));
        if (!problems.isEmpty()) {
            return "[WARNING] Prescription not written: " + problems;
        }
        return "Prescription for " + Validator.tidy(patientName) + ": "
                + Validator.tidy(medication) + " - signed by " + getName()
                + ", " + specialty + " specialist.";
    }

    /**
     * Presents the shared details followed by the specialist's own details.
     *
     * @return the details, one per line
     */
    @Override
    public String toString() {
        return super.toString()                                     // [Reuse] parent's lines first
                + Display.line("Specialty", specialty)
                + Display.line("Referral", referralRequired ? "Required" : "Not required");
    }
}
