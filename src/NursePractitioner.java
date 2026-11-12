/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * A nurse practitioner (NP): an advanced nurse who is allowed to prescribe.
 *
 * This is where inheritance and an interface meet. An NP is a Nurse, so the
 * clinical area and blood samples come for free. An NP can also prescribe,
 * which is something doctors do in a different branch of the tree. One
 * parent class can't give both, so the prescribing part comes from the
 * Prescriber interface.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class NursePractitioner extends Nurse implements Prescriber { // [Abstraction] one parent, one ability

    /** What the NP is endorsed to prescribe for, e.g. "diabetes and blood pressure". */
    private final String endorsedScope;

    /**
     * Builds a nurse practitioner. Outside code uses create(...).
     *
     * @param id            staff ID (already checked)
     * @param name          the name (already checked)
     * @param workingDays   the days worked (already checked)
     * @param clinicalArea  the clinical area (already checked)
     * @param endorsedScope the prescribing endorsement (already checked)
     */
    private NursePractitioner(String id, String name, String[] workingDays, String clinicalArea,
                              String endorsedScope) {
        // [Inheritance] two floors up: Nurse, then HealthProfessional
        super(id, name, workingDays, clinicalArea);
        this.endorsedScope = Validator.tidy(endorsedScope);
    }

    /**
     * Says what is wrong with a nurse practitioner's details, if anything.
     *
     * @param id            the staff ID
     * @param name          the name
     * @param workingDays   the roster
     * @param clinicalArea  the clinical area
     * @param endorsedScope the prescribing endorsement
     * @return "" if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays, String clinicalArea,
                               String endorsedScope) {
        return Validator.addProblem(checkNurseDetails(id, name, workingDays, clinicalArea),
                Validator.checkRequiredText("Endorsed scope", endorsedScope));
    }

    /**
     * Creates a nurse practitioner.
     *
     * @param id            six-digit staff ID
     * @param name          the name
     * @param workingDays   the days worked
     * @param clinicalArea  e.g. "Chronic disease"
     * @param endorsedScope e.g. "diabetes and blood pressure"
     * @return the new nurse practitioner, or null if the details are invalid
     */
    public static NursePractitioner create(String id, String name, String[] workingDays,
                                           String clinicalArea, String endorsedScope) {
        if (!check(id, name, workingDays, clinicalArea, endorsedScope).isEmpty()) {
            return null; // [Encapsulation] bad details never turn into an object
        }
        return new NursePractitioner(id, name, workingDays, clinicalArea, endorsedScope);
    }

    /**
     * Gives the prescribing endorsement.
     *
     * @return e.g. "diabetes and blood pressure"
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
     * Describes what a nurse practitioner does.
     *
     * @return a short description
     */
    @Override
    public String describeServices() {
        return "Advanced nursing care, diagnosis, prescribing";
    }

    /**
     * Replaces the general nursing advice.
     * An NP visit is usually a review of medicines, not a procedure, so
     * "wear a short-sleeved top" would be the wrong advice here.
     *
     * @return the advice
     */
    @Override
    public String getPreparationAdvice() {
        return "Bring your medicines and your latest test results for review.";
    }

    /**
     * Says what this nurse practitioner can prescribe.
     *
     * @return the prescribing scope
     */
    @Override
    public String getPrescribingScope() {
        return "Medicines for " + endorsedScope;
    }

    /**
     * Writes a prescription within the endorsement.
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
                + ", Nurse Practitioner (" + endorsedScope + ").";
    }

    /**
     * Gives the nursing columns plus the prescribing endorsement.
     *
     * @return this nurse practitioner's table row
     */
    @Override
    public String toString() {
        // [Inheritance] only adds what Nurse doesn't print
        return super.toString() + "; prescribes for " + endorsedScope;
    }
}
