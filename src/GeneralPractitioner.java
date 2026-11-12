/*
 * =============================================================================
 *  System  : Arcana Health Centre - Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : GeneralPractitioner.java
 *  Purpose : A general practitioner (GP): the patient's first point of care.
 * =============================================================================
 */

/**
 * A general practitioner (GP): the patient's first point of care.
 * <p>
 * A GP is a health professional (is-a, so it inherits the shared details) and
 * also a Prescriber (a capability promised through an interface). The extra
 * detail a GP records is whether they bulk bill, meaning Medicare covers the
 * whole fee. A GP's unique task is writing referrals to specialists.
 * </p>
 * <p>
 * Objects are created only through the static create(...) methods, which
 * check every detail first and return null instead of building an invalid
 * object. The constructors are private, so this check cannot be skipped.
 * </p>
 *
 * @author Zefei Niu
 * @version 3.0
 */
public class GeneralPractitioner extends HealthProfessional implements Prescriber {

    /** True if the GP bulk bills, so the patient pays nothing out of pocket. */
    private final boolean bulkBilling;

    /**
     * Builds a GP with an explicit roster. Private: use create(...).
     *
     * @param id          six-digit staff ID, already validated
     * @param name        the GP's name, already validated
     * @param workingDays the days the GP works, already validated
     * @param bulkBilling true if the GP bulk bills
     */
    private GeneralPractitioner(String id, String name, String[] workingDays, boolean bulkBilling) {
        super(id, name, workingDays);                               // [Inheritance] the parent owns the shared fields, so it sets them
        this.bulkBilling = bulkBilling;
    }

    /**
     * Builds a full-time GP (Monday to Friday). Private: use create(...).
     *
     * @param id          six-digit staff ID, already validated
     * @param name        the GP's name, already validated
     * @param bulkBilling true if the GP bulk bills
     */
    private GeneralPractitioner(String id, String name, boolean bulkBilling) {
        super(id, name);                                            // the parent knows the default roster, so it is not repeated here
        this.bulkBilling = bulkBilling;
    }

    /**
     * Explains what, if anything, is wrong with a GP's details.
     * A GP adds no text details of its own, so only the shared rules apply.
     *
     * @param id          the staff ID to check
     * @param name        the name to check
     * @param workingDays the roster to check
     * @return an empty text if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays) {
        return checkCommonDetails(id, name, workingDays);
    }

    /**
     * Creates a GP with an explicit roster, or nothing if any detail is invalid.
     *
     * @param id          six-digit staff ID, e.g. "100101"
     * @param name        the GP's name
     * @param workingDays the days the GP works
     * @param bulkBilling true if the GP bulk bills
     * @return the new GP, or null if the details are invalid (see check(...))
     */
    public static GeneralPractitioner create(String id, String name, String[] workingDays,
                                             boolean bulkBilling) {
        if (!check(id, name, workingDays).isEmpty()) {
            return null;                                            // [Encapsulation] invalid details never become an object
        }
        return new GeneralPractitioner(id, name, workingDays, bulkBilling);
    }

    /**
     * Creates a full-time GP (Monday to Friday), or nothing if any detail is
     * invalid. Most GPs in a clinic work full time, so this is a sensible default.
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
     * Tells whether this GP bulk bills.
     *
     * @return true if Medicare covers the whole fee
     */
    public boolean isBulkBilling() {
        return bulkBilling;
    }

    /**
     * Writes a referral to a specialist, the task that is unique to a GP.
     *
     * @param patientName the patient being referred
     * @param specialty   the kind of specialist needed, e.g. "Cardiology"
     * @return the referral text, or a warning explaining why it was not written
     */
    public String writeReferral(String patientName, String specialty) {
        String problems = Validator.addProblem(
                Validator.checkPersonName("Patient name", patientName),
                Validator.checkRequiredText("Specialty", specialty));
        if (!problems.isEmpty()) {
            return "[WARNING] Referral not written. " + problems;   // a referral with a missing name is useless to the specialist
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
     * Describes what a GP provides.
     *
     * @return a short description of a GP's services
     */
    @Override
    public String describeServices() {
        return "Check-ups, common illnesses, prescriptions, referrals";
    }

    /**
     * Tells a patient how to prepare for a GP visit.
     *
     * @return the preparation advice
     */
    @Override
    public String getPreparationAdvice() {
        return "Bring your Medicare card and a list of the medicines you take.";
    }

    /**
     * Describes what a GP may prescribe.
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
            return "[WARNING] Prescription not written. " + problems;
        }
        return "Prescription for " + Validator.tidy(patientName) + ": "
                + Validator.tidy(medication) + " - signed by " + getName() + ", GP.";
    }

    /**
     * Presents the shared columns followed by the GP's billing detail.
     *
     * @return this GP's table row
     */
    @Override
    public String toString() {
        return super.toString()                                     // [Inheritance] reuse the parent's columns instead of repeating them
                + (bulkBilling ? "Bulk billing" : "Private billing");
    }
}
