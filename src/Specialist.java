/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * A specialist doctor, e.g. a cardiologist or a dermatologist.
 *
 * Why one Specialist class and not Cardiologist, Dermatologist and so on:
 * the specialties differ in their data (the name of the field), not in what
 * they do. Ten classes with the same methods would just be the same code ten
 * times, so the specialty is a field instead.
 *
 * A specialist can prescribe, can order tests, and may need patients to
 * bring a GP referral.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Specialist extends HealthProfessional implements Prescriber {

    /** Default for the referral rule: in Australia a Medicare rebate for a specialist needs a GP referral. */
    private static final boolean DEFAULT_REFERRAL_REQUIRED = true;

    /** The medical field, e.g. "Cardiology". */
    private final String specialty;

    /** True if patients must bring a GP referral. */
    private final boolean referralRequired;

    /**
     * Builds a specialist with every detail. Outside code uses create(...).
     *
     * @param id               staff ID (already checked)
     * @param name             the name (already checked)
     * @param workingDays      the days worked (already checked)
     * @param specialty        the medical field (already checked)
     * @param referralRequired true if a GP referral is needed
     */
    private Specialist(String id, String name, String[] workingDays, String specialty,
                       boolean referralRequired) {
        super(id, name, workingDays); // [Inheritance] the shared fields belong to the parent, so it sets them
        this.specialty = Validator.tidy(specialty);
        this.referralRequired = referralRequired;
    }

    /**
     * Builds a specialist who needs referrals, the usual case.
     * Outside code uses create(...).
     *
     * @param id          staff ID (already checked)
     * @param name        the name (already checked)
     * @param workingDays the days worked (already checked)
     * @param specialty   the medical field (already checked)
     */
    private Specialist(String id, String name, String[] workingDays, String specialty) {
        // a named constant, not a bare true, so the default explains itself
        this(id, name, workingDays, specialty, DEFAULT_REFERRAL_REQUIRED);
    }

    /**
     * Says what is wrong with a specialist's details, if anything.
     *
     * @param id          the staff ID
     * @param name        the name
     * @param workingDays the roster
     * @param specialty   the specialty
     * @return "" if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays, String specialty) {
        return Validator.addProblem(checkCommonDetails(id, name, workingDays),
                Validator.checkRequiredText("Specialty", specialty));
    }

    /**
     * Creates a specialist.
     *
     * @param id               six-digit staff ID
     * @param name             the name
     * @param workingDays      the days worked
     * @param specialty        e.g. "Cardiology"
     * @param referralRequired true if a GP referral is needed
     * @return the new specialist, or null if the details are invalid
     */
    public static Specialist create(String id, String name, String[] workingDays, String specialty,
                                    boolean referralRequired) {
        if (!check(id, name, workingDays, specialty).isEmpty()) {
            return null; // [Encapsulation] bad details never turn into an object
        }
        return new Specialist(id, name, workingDays, specialty, referralRequired);
    }

    /**
     * Creates a specialist who needs a GP referral (the default).
     *
     * @param id          six-digit staff ID
     * @param name        the name
     * @param workingDays the days worked
     * @param specialty   e.g. "Cardiology"
     * @return the new specialist, or null if the details are invalid
     */
    public static Specialist create(String id, String name, String[] workingDays, String specialty) {
        if (!check(id, name, workingDays, specialty).isEmpty()) {
            return null;
        }
        return new Specialist(id, name, workingDays, specialty);
    }

    /**
     * Gives the specialty.
     *
     * @return e.g. "Cardiology"
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * Checks whether patients need a GP referral.
     *
     * @return true if a referral is needed
     */
    public boolean isReferralRequired() {
        return referralRequired;
    }

    /**
     * Orders a test for a patient. Only a specialist does this.
     *
     * @param patientName the patient
     * @param testName    e.g. "Echocardiogram"
     * @return the test order, or a warning saying why it wasn't made
     */
    public String orderTest(String patientName, String testName) {
        String problems = Validator.addProblem(
                Validator.checkPersonName("Patient name", patientName),
                Validator.checkRequiredText("Test name", testName));
        if (!problems.isEmpty()) {
            return "[WARNING] Test not ordered. " + problems;
        }
        return "Test order: " + Validator.tidy(testName) + " for " + Validator.tidy(patientName)
                + ", requested by " + getName() + " (" + specialty + ").";
    }

    /**
     * Names this kind of professional.
     * The specialty goes in the details column instead, so the type column
     * stays the same width for everyone.
     *
     * @return "Specialist"
     */
    @Override
    public String getProfessionType() {
        return "Specialist";
    }

    /**
     * Describes what this specialist does.
     *
     * @return a short description
     */
    @Override
    public String describeServices() {
        return "Expert assessment and treatment in " + specialty;
    }

    /**
     * Tells a patient how to get ready. The answer depends on the referral rule.
     *
     * @return the advice
     */
    @Override
    public String getPreparationAdvice() {
        // same question, different answer: it depends on this object's data
        if (referralRequired) {
            return "Bring your GP referral letter and any recent test results.";
        }
        return "Bring any recent test results; no referral is needed.";
    }

    /**
     * Says what this specialist can prescribe.
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
     * @param patientName the patient
     * @param medication  the medicine and dose
     * @return the prescription, or a warning saying why it wasn't written
     */
    @Override
    public String prescribe(String patientName, String medication) {
        String problems = Validator.addProblem(
                Validator.checkPersonName("Patient name", patientName),
                Validator.checkRequiredText("Medication", medication));
        if (!problems.isEmpty()) {
            return "[WARNING] Prescription not written. " + problems;
        }
        return "Prescription for " + Validator.tidy(patientName) + ": "
                + Validator.tidy(medication) + " - signed by " + getName()
                + ", " + specialty + " specialist.";
    }

    /**
     * Gives the shared columns plus the specialty and referral rule.
     *
     * @return this specialist's table row
     */
    @Override
    public String toString() {
        return super.toString() // [Inheritance] reuse the parent's four columns
                + specialty + (referralRequired ? ", referral needed" : ", no referral needed");
    }
}
