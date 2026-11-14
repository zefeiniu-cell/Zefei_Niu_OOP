/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * The middle floor of the nursing branch:
 * HealthProfessional -> Nurse -> RegisteredNurse or NursePractitioner.
 *
 * Why abstract: in this clinic every nurse is either a registered nurse or a
 * nurse practitioner. A "plain" nurse doesn't exist, so it can't be created.
 *
 * Why have this level at all: both kinds of nurse share a clinical area and
 * both take blood samples. Putting that here means it is written once. If it
 * lived in both subclasses, a change to blood samples would have to be made
 * twice, and sooner or later the two copies would differ.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public abstract class Nurse extends HealthProfessional { // [Abstraction] there is no "plain" nurse

    /** The nurse's area, e.g. "Immunisation" or "Wound care". */
    private final String clinicalArea;

    /**
     * Sets up the nursing part with a given roster.
     * Only subclasses call this, after they have checked the details.
     *
     * @param id           staff ID (already checked)
     * @param name         the name (already checked)
     * @param workingDays  the days worked (already checked)
     * @param clinicalArea the clinical area (already checked)
     */
    public Nurse(String id, String name, String[] workingDays, String clinicalArea) {
        super(id, name, workingDays); // [Inheritance] each floor sets only its own fields
        this.clinicalArea = Validator.tidy(clinicalArea);
    }

    /**
     * Sets up the nursing part for a full-time nurse.
     * Only subclasses call this, after they have checked the details.
     *
     * @param id           staff ID (already checked)
     * @param name         the name (already checked)
     * @param clinicalArea the clinical area (already checked)
     */
    public Nurse(String id, String name, String clinicalArea) {
        super(id, name);
        this.clinicalArea = Validator.tidy(clinicalArea);
    }

    /**
     * Says what is wrong with a nurse's details, if anything.
     *
     * @param id           the staff ID
     * @param name         the name
     * @param workingDays  the roster
     * @param clinicalArea the clinical area
     * @return "" if valid, otherwise every problem found
     */
    public static String checkNurseDetails(String id, String name, String[] workingDays,
                                           String clinicalArea) {
        return Validator.addProblem(checkCommonDetails(id, name, workingDays),
                Validator.checkRequiredText("Clinical area", clinicalArea));
    }

    /**
     * Says what is wrong with a full-time nurse's details, if anything.
     *
     * @param id           the staff ID
     * @param name         the name
     * @param clinicalArea the clinical area
     * @return "" if valid, otherwise every problem found
     */
    public static String checkNurseDetails(String id, String name, String clinicalArea) {
        return Validator.addProblem(checkCommonDetails(id, name),
                Validator.checkRequiredText("Clinical area", clinicalArea));
    }

    /**
     * Gives the clinical area.
     *
     * @return e.g. "Immunisation"
     */
    public String getClinicalArea() {
        return clinicalArea;
    }

    /**
     * Takes a blood sample.
     * Written once here, and both kinds of nurse get it by inheritance.
     *
     * @param patientName the patient
     * @return a record of the sample, or a warning if the name is invalid
     */
    public String takeBloodSample(String patientName) {
        String problem = Validator.checkPersonName("Patient name", patientName);
        if (!problem.isEmpty()) {
            return "[WARNING] Blood sample not taken. " + problem; // no name, no label for the tube
        }
        return "Blood sample: taken from " + Validator.tidy(patientName) + " by " + getName()
                + " (" + getProfessionType() + ") and labelled for pathology.";
    }

    /**
     * Gives the usual advice before a nursing visit.
     * A subclass can override it if its visits are different.
     *
     * @return the advice
     */
    @Override
    public String getPreparationAdvice() {
        return "Bring any test requests and follow the preparation instructions from your clinician.";
    }

    /**
     * Gives the shared columns plus the clinical area.
     *
     * @return the nursing part of the table row
     */
    @Override
    public String toString() {
        return super.toString() + clinicalArea; // the floor above adds its own detail to this
    }
}
