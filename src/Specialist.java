/**
 * Arcana Health Centre - Appointment System.
 *
 * A specialist with a specialty, referral flag, test orders and prescribing methods.
 *
 * @author Zefei Niu
 */
public class Specialist extends HealthProfessional implements Prescriber {
    /* Default for the referral rule: in Australia a Medicare rebate for a specialist needs a GP referral. */
    private static final boolean DEFAULT_REFERRAL_REQUIRED = true;

    /* The medical field, e.g. "Cardiology". */
    private final String specialty;

    /* True if patients must bring a GP referral. */
    private final boolean referralRequired;

    /** Builds a specialist with every detail. */
    private Specialist(String id, String name, String[] workingDays, String specialty,
                       boolean referralRequired) {
        super(id, name, workingDays);
        this.specialty = Validator.tidy(specialty);
        this.referralRequired = referralRequired;
    }

    /** Builds a specialist who needs referrals, the usual case. */
    private Specialist(String id, String name, String[] workingDays, String specialty) {
        this(id, name, workingDays, specialty, DEFAULT_REFERRAL_REQUIRED);
    }

    /**
     * Says what is wrong with a specialist's details, if anything.
     *
     * @return "" if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays, String specialty) {
        return Validator.addProblem(checkCommonDetails(id, name, workingDays),
                Validator.checkRequiredText("Specialty", specialty));
    }

    /**
     * Creates a specialist.
     *
     * @return the new specialist, or null if the details are invalid
     */
    public static Specialist create(String id, String name, String[] workingDays, String specialty,
                                    boolean referralRequired) {
        if (!check(id, name, workingDays, specialty).isEmpty()) {
            return null;
        }
        return new Specialist(id, name, workingDays, specialty, referralRequired);
    }

    /**
     * Creates a specialist who needs a GP referral (the default).
     *
     * @return the new specialist, or null if the details are invalid
     */
    public static Specialist create(String id, String name, String[] workingDays, String specialty) {
        if (!check(id, name, workingDays, specialty).isEmpty()) {
            return null;
        }
        return new Specialist(id, name, workingDays, specialty);
    }

    /** Gives the specialty. */
    public String getSpecialty() {
        return specialty;
    }

    /** Checks whether patients need a GP referral. */
    public boolean isReferralRequired() {
        return referralRequired;
    }

    /**
     * Orders a test for a patient. Only a specialist does this.
     *
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
     * Names this kind of professional. The specialty goes in the details column instead, so
     * the type column stays the same width for everyone.
     */
    @Override
    public String getProfessionType() {
        return "Specialist";
    }

    /** Describes what this specialist does. */
    @Override
    public String describeServices() {
        return "Expert assessment and treatment in " + specialty;
    }

    /** Tells a patient how to get ready. The answer depends on the referral rule. */
    @Override
    public String getPreparationAdvice() {
        if (referralRequired) {
            return "Bring your GP referral letter and any recent test results.";
        }
        return "Bring any recent test results; no referral is needed.";
    }

    /** Says what this specialist can prescribe. */
    @Override
    public String getPrescribingScope() {
        return "Medicines related to " + specialty;
    }

    /**
     * Writes a prescription signed by this specialist.
     *
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

    /** Gives the shared columns plus the specialty and referral rule. */
    @Override
    public String toString() {
        return super.toString()
                + specialty + (referralRequired ? ", referral needed" : ", no referral needed");
    }
}
