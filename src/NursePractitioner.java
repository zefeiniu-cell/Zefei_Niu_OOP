/*
 * =============================================================================
 *  System  : Arcana Health Centre - Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : NursePractitioner.java
 *  Purpose : A nurse practitioner: an advanced nurse who may also prescribe.
 * =============================================================================
 */

/**
 * A nurse practitioner: an advanced-practice nurse endorsed to prescribe.
 * <p>
 * This class shows inheritance and an interface working together. It is-a
 * Nurse (so it inherits the clinical area and the blood-sample skill without
 * repeating them) and it also implements Prescriber, a capability it shares
 * with doctors in a completely different branch of the hierarchy. Single
 * inheritance alone could not express both.
 * </p>
 *
 * @author Zefei Niu
 * @version 2.0
 */
public class NursePractitioner extends Nurse implements Prescriber { // [Abstraction] one parent class, plus a capability from an interface

    /** The medicines the practitioner is endorsed to prescribe. */
    private final String endorsedScope;

    /**
     * Builds a nurse practitioner. Private: use create(...).
     *
     * @param id            six-digit staff ID, already validated
     * @param name          the practitioner's name, already validated
     * @param workingDays   the days the practitioner works, already validated
     * @param clinicalArea  the practitioner's clinical area, already validated
     * @param endorsedScope the prescribing endorsement, already validated
     */
    private NursePractitioner(String id, String name, String[] workingDays, String clinicalArea,
                              String endorsedScope) {
        super(id, name, workingDays, clinicalArea);                 // [Inheritance] two levels up: Nurse, then HealthProfessional
        this.endorsedScope = Validator.tidy(endorsedScope);
    }

    /**
     * Explains what, if anything, is wrong with a nurse practitioner's details.
     *
     * @param id            the staff ID to check
     * @param name          the name to check
     * @param workingDays   the roster to check
     * @param clinicalArea  the clinical area to check
     * @param endorsedScope the prescribing endorsement to check
     * @return an empty text if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays, String clinicalArea,
                               String endorsedScope) {
        return Validator.addProblem(checkNurseDetails(id, name, workingDays, clinicalArea),
                Validator.checkRequiredText("Endorsed scope", endorsedScope));
    }

    /**
     * Creates a nurse practitioner, or nothing if any detail is invalid.
     *
     * @param id            six-digit staff ID
     * @param name          the practitioner's name
     * @param workingDays   the days the practitioner works
     * @param clinicalArea  the clinical area, e.g. "Chronic disease"
     * @param endorsedScope what they may prescribe, e.g. "diabetes, blood pressure"
     * @return the new nurse practitioner, or null if the details are invalid
     */
    public static NursePractitioner create(String id, String name, String[] workingDays,
                                           String clinicalArea, String endorsedScope) {
        if (!check(id, name, workingDays, clinicalArea, endorsedScope).isEmpty()) {
            return null;                                            // [Encapsulation] invalid details never become an object
        }
        return new NursePractitioner(id, name, workingDays, clinicalArea, endorsedScope);
    }

    /**
     * Returns the prescribing endorsement.
     *
     * @return e.g. "diabetes, blood pressure"
     */
    public String getEndorsedScope() {
        return endorsedScope;
    }

    /**
     * Names this kind of professional.
     *
     * @return "Nurse Practitioner"
     */
    @Override
    public String getProfessionType() {
        return "Nurse Practitioner";
    }

    /**
     * Describes what a nurse practitioner provides.
     *
     * @return a short description of the practitioner's services
     */
    @Override
    public String describeServices() {
        return "Advanced nursing care, diagnosis, prescribing";
    }

    /**
     * Overrides the general nursing advice, because these visits are usually
     * medication reviews rather than procedures.
     *
     * @return the preparation advice
     */
    @Override
    public String getPreparationAdvice() {
        return "Bring your medicines and your latest test results for review.";
    }

    /**
     * Describes what this practitioner may prescribe.
     *
     * @return the prescribing scope
     */
    @Override
    public String getPrescribingScope() {
        return "Medicines for " + endorsedScope;
    }

    /**
     * Writes a prescription within the practitioner's endorsement.
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
                + Validator.tidy(medication) + " - signed by " + getName()
                + ", Nurse Practitioner (" + endorsedScope + ").";
    }

    /**
     * Presents the nursing columns followed by the prescribing endorsement.
     *
     * @return this practitioner's table row
     */
    @Override
    public String toString() {
        return super.toString() + "; prescribes for " + endorsedScope; // [Inheritance] adds only what Nurse does not already print
    }
}
