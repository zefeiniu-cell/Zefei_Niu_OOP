/**
 * Arcana Health Centre - Appointment System.
 *
 * A GP with a bulk-billing flag, referral method and prescribing methods.
 *
 * @author Zefei Niu
 */
public class GeneralPractitioner extends HealthProfessional implements Prescriber {
    /* True if the GP bulk bills. */
    private final boolean bulkBilling;

    /** Builds a GP with a given roster. Outside code uses create(...). */
    private GeneralPractitioner(String id, String name, String[] workingDays, boolean bulkBilling) {
        super(id, name, workingDays);
        this.bulkBilling = bulkBilling;
    }

    /** Builds a full-time GP (Monday to Friday). Outside code uses create(...). */
    private GeneralPractitioner(String id, String name, boolean bulkBilling) {
        super(id, name);
        this.bulkBilling = bulkBilling;
    }

    /**
     * Says what is wrong with a GP's details, if anything. A GP has no extra text details, so
     * only the shared checks apply.
     *
     * @return "" if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays) {
        return checkCommonDetails(id, name, workingDays);
    }

    /**
     * Creates a GP with a given roster.
     *
     * @return the new GP, or null if the details are invalid (check(...) says why)
     */
    public static GeneralPractitioner create(String id, String name, String[] workingDays,
                                             boolean bulkBilling) {
        if (!check(id, name, workingDays).isEmpty()) {
            return null;
        }
        return new GeneralPractitioner(id, name, workingDays, bulkBilling);
    }

    /**
     * Creates a GP using the default Monday-to-Friday roster.
     *
     * @return the new GP, or null for invalid details
     */
    public static GeneralPractitioner create(String id, String name, boolean bulkBilling) {
        if (!checkCommonDetails(id, name).isEmpty()) {
            return null;
        }
        return new GeneralPractitioner(id, name, bulkBilling);
    }

    /** Checks whether this GP bulk bills. */
    public boolean isBulkBilling() {
        return bulkBilling;
    }

    /**
     * Writes a referral to a specialist. Only a GP does this.
     *
     * @return the referral, or a warning saying why it wasn't written
     */
    public String writeReferral(String patientName, String specialty) {
        String problems = Validator.addProblem(
                Validator.checkPersonName("Patient name", patientName),
                Validator.checkRequiredText("Specialty", specialty));
        if (!problems.isEmpty()) {
            return "[WARNING] Referral not written. " + problems;
        }
        return "Referral: " + getName() + " refers " + Validator.tidy(patientName)
                + " to a " + Validator.tidy(specialty) + " specialist.";
    }

    /** Names this kind of professional. */
    @Override
    public String getProfessionType() {
        return "General Practitioner";
    }

    /** Describes what a GP does. */
    @Override
    public String describeServices() {
        return "Check-ups, common illnesses, prescriptions, referrals";
    }

    /** Tells a patient how to get ready for a GP visit. */
    @Override
    public String getPreparationAdvice() {
        return "Bring your Medicare card and a list of the medicines you take.";
    }

    /** Says what a GP can prescribe. */
    @Override
    public String getPrescribingScope() {
        return "General medicines for everyday conditions";
    }

    /**
     * Writes a prescription signed by this GP.
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
                + Validator.tidy(medication) + " - signed by " + getName() + ", GP.";
    }

    /** Gives the shared columns plus the GP's billing detail. */
    @Override
    public String toString() {
        return super.toString()
                + (bulkBilling ? "Bulk billing" : "Private billing");
    }
}
