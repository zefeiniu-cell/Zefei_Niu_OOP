/*
 * =============================================================================
 *  System  : Health Clinic Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : RegisteredNurse.java
 *  Purpose : A registered nurse (RN), the most common kind of nurse in a clinic.
 * =============================================================================
 */

/**
 * A registered nurse (RN), the most common kind of nurse in a clinic.
 * <p>
 * It inherits the clinical area and blood-sample skill from Nurse and adds
 * one detail: whether the nurse is an authorised immuniser, meaning they may
 * give vaccines without a doctor's order. Giving vaccines is the task unique
 * to this type.
 * </p>
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class RegisteredNurse extends Nurse {

    /** True if the nurse may vaccinate without a doctor's order. */
    private final boolean authorisedImmuniser;

    /**
     * Builds a registered nurse with an explicit roster. Private: use create(...).
     *
     * @param id                  six-digit staff ID, already validated
     * @param name                the nurse's name, already validated
     * @param workingDays         the days the nurse works, already validated
     * @param clinicalArea        the nurse's clinical area, already validated
     * @param authorisedImmuniser true if the nurse may vaccinate independently
     */
    private RegisteredNurse(String id, String name, String[] workingDays, String clinicalArea,
                            boolean authorisedImmuniser) {
        super(id, name, workingDays, clinicalArea);                 // [Inheritance] Nurse, then HealthProfessional
        this.authorisedImmuniser = authorisedImmuniser;
    }

    /**
     * Builds a full-time registered nurse. Private: use create(...).
     *
     * @param id                  six-digit staff ID, already validated
     * @param name                the nurse's name, already validated
     * @param clinicalArea        the nurse's clinical area, already validated
     * @param authorisedImmuniser true if the nurse may vaccinate independently
     */
    private RegisteredNurse(String id, String name, String clinicalArea, boolean authorisedImmuniser) {
        super(id, name, clinicalArea);                              // [Reuse] default full-time week
        this.authorisedImmuniser = authorisedImmuniser;
    }

    /**
     * Explains what, if anything, is wrong with a registered nurse's details.
     *
     * @param id           the staff ID to check
     * @param name         the name to check
     * @param workingDays  the roster to check
     * @param clinicalArea the clinical area to check
     * @return an empty text if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays, String clinicalArea) {
        return checkNurseDetails(id, name, workingDays, clinicalArea);
    }

    /**
     * Creates a registered nurse with an explicit roster, or nothing if any
     * detail is invalid.
     *
     * @param id                  six-digit staff ID
     * @param name                the nurse's name
     * @param workingDays         the days the nurse works
     * @param clinicalArea        the nurse's clinical area, e.g. "Wound care"
     * @param authorisedImmuniser true if the nurse may vaccinate independently
     * @return the new nurse, or null if the details are invalid
     */
    public static RegisteredNurse create(String id, String name, String[] workingDays,
                                         String clinicalArea, boolean authorisedImmuniser) {
        if (!check(id, name, workingDays, clinicalArea).isEmpty()) {
            return null;                                            // [Guard] invalid details: no object is built
        }
        return new RegisteredNurse(id, name, workingDays, clinicalArea, authorisedImmuniser);
    }

    /**
     * Creates a full-time registered nurse (Monday to Friday), or nothing if
     * any detail is invalid. Practice nurses usually work full time.
     *
     * @param id                  six-digit staff ID
     * @param name                the nurse's name
     * @param clinicalArea        the nurse's clinical area
     * @param authorisedImmuniser true if the nurse may vaccinate independently
     * @return the new nurse, or null if the details are invalid
     */
    public static RegisteredNurse create(String id, String name, String clinicalArea,
                                         boolean authorisedImmuniser) {
        if (!checkNurseDetails(id, name, clinicalArea).isEmpty()) {
            return null;                                            // [Guard] invalid details: no object is built
        }
        return new RegisteredNurse(id, name, clinicalArea, authorisedImmuniser);
    }

    /**
     * Tells whether this nurse may vaccinate without a doctor's order.
     *
     * @return true if authorised
     */
    public boolean isAuthorisedImmuniser() {
        return authorisedImmuniser;
    }

    /**
     * Gives a vaccine, the task unique to a registered nurse. A nurse who is
     * not an authorised immuniser is refused, as in a real clinic.
     *
     * @param patientName the patient being vaccinated
     * @param vaccine     the vaccine, e.g. "Influenza 2026"
     * @return a record of the vaccination, or a warning explaining the refusal
     */
    public String administerVaccine(String patientName, String vaccine) {
        String problems = Validator.addProblem(
                Validator.checkPersonName("Patient name", patientName),
                Validator.checkRequiredText("Vaccine", vaccine));
        if (!problems.isEmpty()) {
            return "[WARNING] Vaccine not given: " + problems;      // [Guard] incomplete request
        }
        if (!authorisedImmuniser) {
            return "[WARNING] Vaccine not given: " + getName()
                    + " is not an authorised immuniser, so a doctor's order is needed first.";
        }
        return "Vaccination: " + Validator.tidy(vaccine) + " given to "
                + Validator.tidy(patientName) + " by " + getName() + ".";
    }

    /**
     * Names this kind of professional.
     *
     * @return "Registered Nurse"
     */
    @Override
    public String getProfessionType() {
        return "Registered Nurse";
    }

    /**
     * Describes what a registered nurse provides.
     *
     * @return a short description of the nurse's services
     */
    @Override
    public String describeServices() {
        return "Health checks, immunisations, wound care and blood tests";
    }

    /**
     * Presents the nursing details followed by the immuniser status.
     *
     * @return the details, one per line
     */
    @Override
    public String toString() {
        return super.toString()                                     // [Reuse] Nurse's lines, which include the parent's
                + Display.line("Immuniser", Display.yesNo(authorisedImmuniser));
    }
}
