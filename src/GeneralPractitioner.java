/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * A general practitioner (GP), the doctor most patients see first.
 *
 * A GP is a HealthProfessional (so it gets the ID, name and roster) and also
 * a Prescriber. The extra detail I record is bulk billing, because it is the
 * first thing an Australian patient asks: will Medicare cover the whole fee?
 * The job only a GP does here is writing referrals, since in Australia a
 * specialist visit usually starts with one.
 *
 * The constructors are private. The only way to get a GP is create(...),
 * which checks the details first and gives back null if they are wrong.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class GeneralPractitioner extends HealthProfessional implements Prescriber {

    /** True if the GP bulk bills. */
    private final boolean bulkBilling;

    /**
     * Builds a GP with a given roster. Outside code uses create(...).
     *
     * @param id          staff ID (already checked)
     * @param name        the GP's name (already checked)
     * @param workingDays the days worked (already checked)
     * @param bulkBilling true if the GP bulk bills
     */
    private GeneralPractitioner(String id, String name, String[] workingDays, boolean bulkBilling) {
        super(id, name, workingDays); // [Inheritance] the shared fields belong to the parent, so it sets them
        this.bulkBilling = bulkBilling;
    }

    /**
     * Builds a full-time GP (Monday to Friday). Outside code uses create(...).
     *
     * @param id          staff ID (already checked)
     * @param name        the GP's name (already checked)
     * @param bulkBilling true if the GP bulk bills
     */
    private GeneralPractitioner(String id, String name, boolean bulkBilling) {
        super(id, name); // the parent already knows the default roster
        this.bulkBilling = bulkBilling;
    }

    /**
     * Says what is wrong with a GP's details, if anything.
     * A GP has no extra text details, so only the shared checks apply.
     *
     * @param id          the staff ID
     * @param name        the name
     * @param workingDays the roster
     * @return "" if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays) {
        return checkCommonDetails(id, name, workingDays);
    }

    /**
     * Creates a GP with a given roster.
     *
     * @param id          six-digit staff ID, e.g. "100101"
     * @param name        the GP's name
     * @param workingDays the days worked
     * @param bulkBilling true if the GP bulk bills
     * @return the new GP, or null if the details are invalid (check(...) says why)
     */
    public static GeneralPractitioner create(String id, String name, String[] workingDays,
                                             boolean bulkBilling) {
        if (!check(id, name, workingDays).isEmpty()) {
            return null; // [Encapsulation] bad details never turn into an object
        }
        return new GeneralPractitioner(id, name, workingDays, bulkBilling);
    }

    /**
     * Creates a full-time GP (Monday to Friday).
     * Most GPs in a clinic work full time, so this saves typing the same five
     * days again and again.
     *
     * @param id          six-digit staff ID
     * @param name        the GP's name
     * @param bulkBilling true if the GP bulk bills
     * @return the new GP, or null if the details are invalid
     */
    public static GeneralPractitioner create(String id, String name, boolean bulkBilling) {
        if (!checkCommonDetails(id, name).isEmpty()) {
            return null;
        }
        return new GeneralPractitioner(id, name, bulkBilling);
    }

    /**
     * Checks whether this GP bulk bills.
     *
     * @return true if they bulk bill
     */
    public boolean isBulkBilling() {
        return bulkBilling;
    }

    /**
     * Writes a referral to a specialist. Only a GP does this.
     *
     * @param patientName the patient being referred
     * @param specialty   the kind of specialist, e.g. "Cardiology"
     * @return the referral, or a warning saying why it wasn't written
     */
    public String writeReferral(String patientName, String specialty) {
        String problems = Validator.addProblem(
                Validator.checkPersonName("Patient name", patientName),
                Validator.checkRequiredText("Specialty", specialty));
        if (!problems.isEmpty()) {
            return "[WARNING] Referral not written. " + problems; // no specialty means nowhere to send it
        }
        return "Referral: " + getName() + " refers " + Validator.tidy(patientName)
                + " to a " + Validator.tidy(specialty) + " specialist.";
    }

    /**
     * Names this kind of professional.
     *
     * @return "General Practitioner"
     */
    @Override
    public String getProfessionType() {
        return "General Practitioner";
    }

    /**
     * Describes what a GP does.
     *
     * @return a short description
     */
    @Override
    public String describeServices() {
        return "Check-ups, common illnesses, prescriptions, referrals";
    }

    /**
     * Tells a patient how to get ready for a GP visit.
     *
     * @return the advice
     */
    @Override
    public String getPreparationAdvice() {
        return "Bring your Medicare card and a list of the medicines you take.";
    }

    /**
     * Says what a GP can prescribe.
     *
     * @return the prescribing scope
     */
    @Override
    public String getPrescribingScope() {
        return "General medicines for everyday conditions";
    }

    /**
     * Writes a prescription signed by this GP.
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
                + Validator.tidy(medication) + " - signed by " + getName() + ", GP.";
    }

    /**
     * Gives the shared columns plus the GP's billing detail.
     *
     * @return this GP's table row
     */
    @Override
    public String toString() {
        return super.toString() // [Inheritance] reuse the parent's four columns, only add the GP's part
                + (bulkBilling ? "Bulk billing" : "Private billing");
    }
}
