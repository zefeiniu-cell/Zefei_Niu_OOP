/*
 * =============================================================================
 *  System  : Health Clinic Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : Nurse.java
 *  Purpose : Abstract middle level holding what every kind of nurse shares.
 * =============================================================================
 */

/**
 * The shared middle level of the nursing branch.
 * <p>
 * Every nurse in the clinic is either a RegisteredNurse or a
 * NursePractitioner; there is no "plain" nurse, so this class is abstract.
 * It holds what all nurses share, so it is written once: a clinical area and
 * the ability to take a blood sample. Being abstract also keeps the design
 * safe without exceptions: only subclasses can call its constructors, and
 * each subclass validates through its own create(...) method first.
 * This creates a multi-level hierarchy:
 * HealthProfessional, then Nurse, then RegisteredNurse or NursePractitioner.
 * </p>
 *
 * @author Zefei Niu
 * @version 1.0
 */
public abstract class Nurse extends HealthProfessional {

    /** The area the nurse works in, e.g. "Immunisation" or "Wound care". */
    private final String clinicalArea;

    /**
     * Builds the nursing part with an explicit roster.
     * Reachable only from subclasses, which validate first.
     *
     * @param id           six-digit staff ID, already validated
     * @param name         the nurse's name, already validated
     * @param workingDays  the days the nurse works, already validated
     * @param clinicalArea the nurse's clinical area, already validated
     */
    public Nurse(String id, String name, String[] workingDays, String clinicalArea) {
        super(id, name, workingDays);                               // [Inheritance] HealthProfessional sets the shared part
        this.clinicalArea = Validator.tidy(clinicalArea);
    }

    /**
     * Builds the nursing part for a full-time nurse (Monday to Friday).
     * Reachable only from subclasses, which validate first.
     *
     * @param id           six-digit staff ID, already validated
     * @param name         the nurse's name, already validated
     * @param clinicalArea the nurse's clinical area, already validated
     */
    public Nurse(String id, String name, String clinicalArea) {
        super(id, name);                                            // [Reuse] default full-time week
        this.clinicalArea = Validator.tidy(clinicalArea);
    }

    /**
     * Explains what, if anything, is wrong with a nurse's details.
     *
     * @param id           the staff ID to check
     * @param name         the name to check
     * @param workingDays  the roster to check
     * @param clinicalArea the clinical area to check
     * @return an empty text if valid, otherwise every problem found
     */
    public static String checkNurseDetails(String id, String name, String[] workingDays,
                                           String clinicalArea) {
        return Validator.addProblem(checkCommonDetails(id, name, workingDays),
                Validator.checkRequiredText("Clinical area", clinicalArea));
    }

    /**
     * Explains what, if anything, is wrong with a full-time nurse's details.
     *
     * @param id           the staff ID to check
     * @param name         the name to check
     * @param clinicalArea the clinical area to check
     * @return an empty text if valid, otherwise every problem found
     */
    public static String checkNurseDetails(String id, String name, String clinicalArea) {
        return Validator.addProblem(checkCommonDetails(id, name),
                Validator.checkRequiredText("Clinical area", clinicalArea));
    }

    /**
     * Returns the clinical area.
     *
     * @return e.g. "Immunisation"
     */
    public String getClinicalArea() {
        return clinicalArea;
    }

    /**
     * Takes a blood sample. Written once here and inherited by every kind
     * of nurse, because all nurses in the clinic can do it.
     *
     * @param patientName the patient giving the sample
     * @return a record of the sample, or a warning if the name is invalid
     */
    public String takeBloodSample(String patientName) {
        String problem = Validator.checkPersonName("Patient name", patientName);
        if (!problem.isEmpty()) {
            return "[WARNING] Blood sample not taken: " + problem;  // [Guard] unknown patient
        }
        return "Blood sample: taken from " + Validator.tidy(patientName) + " by " + getName()
                + " (" + getProfessionType() + ") and labelled for pathology.";
    }

    /**
     * Gives the preparation advice that suits most nursing visits.
     * A subclass may override it when its visits differ.
     *
     * @return the preparation advice
     */
    @Override
    public String getPreparationAdvice() {
        return "Wear a short-sleeved top. Fast for 8 hours if a blood test is planned.";
    }

    /**
     * Presents the shared details followed by the clinical area.
     *
     * @return the details, one per line
     */
    @Override
    public String toString() {
        return super.toString()                                     // [Reuse] HealthProfessional's lines first
                + Display.line("Clinical area", clinicalArea);
    }
}
