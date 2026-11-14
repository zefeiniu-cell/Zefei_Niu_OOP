/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * A registered nurse (RN), the most common kind of nurse in a clinic.
 *
 * The clinical area and blood samples come from Nurse. The one extra detail
 * is whether the nurse is an authorised immuniser. I added it because it
 * changes what the nurse is allowed to do: an authorised immuniser can give
 * a vaccine on their own, and any other RN needs a doctor's order first.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class RegisteredNurse extends Nurse {

    /** True if the nurse can vaccinate without a doctor's order. */
    private final boolean authorisedImmuniser;

    /**
     * Builds a registered nurse with a given roster. Outside code uses create(...).
     *
     * @param id                  staff ID (already checked)
     * @param name                the name (already checked)
     * @param workingDays         the days worked (already checked)
     * @param clinicalArea        the clinical area (already checked)
     * @param authorisedImmuniser true if they can vaccinate on their own
     */
    private RegisteredNurse(String id, String name, String[] workingDays, String clinicalArea,
                            boolean authorisedImmuniser) {
        // [Inheritance] Nurse sets its part, then HealthProfessional sets the rest
        super(id, name, workingDays, clinicalArea);
        this.authorisedImmuniser = authorisedImmuniser;
    }

    /**
     * Builds a full-time registered nurse. Outside code uses create(...).
     *
     * @param id                  staff ID (already checked)
     * @param name                the name (already checked)
     * @param clinicalArea        the clinical area (already checked)
     * @param authorisedImmuniser true if they can vaccinate on their own
     */
    private RegisteredNurse(String id, String name, String clinicalArea, boolean authorisedImmuniser) {
        super(id, name, clinicalArea);
        this.authorisedImmuniser = authorisedImmuniser;
    }

    /**
     * Says what is wrong with a registered nurse's details, if anything.
     *
     * @param id           the staff ID
     * @param name         the name
     * @param workingDays  the roster
     * @param clinicalArea the clinical area
     * @return "" if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays, String clinicalArea) {
        return checkNurseDetails(id, name, workingDays, clinicalArea);
    }

    /**
     * Creates a registered nurse with a given roster.
     *
     * @param id                  six-digit staff ID
     * @param name                the name
     * @param workingDays         the days worked
     * @param clinicalArea        e.g. "Wound care"
     * @param authorisedImmuniser true if they can vaccinate on their own
     * @return the new nurse, or null if the details are invalid
     */
    public static RegisteredNurse create(String id, String name, String[] workingDays,
                                         String clinicalArea, boolean authorisedImmuniser) {
        if (!check(id, name, workingDays, clinicalArea).isEmpty()) {
            return null; // [Encapsulation] bad details never turn into an object
        }
        return new RegisteredNurse(id, name, workingDays, clinicalArea, authorisedImmuniser);
    }

    /**
     * Creates a full-time registered nurse (Monday to Friday).
     * Practice nurses usually work full time, so this is the default.
     *
     * @param id                  six-digit staff ID
     * @param name                the name
     * @param clinicalArea        the clinical area
     * @param authorisedImmuniser true if they can vaccinate on their own
     * @return the new nurse, or null if the details are invalid
     */
    public static RegisteredNurse create(String id, String name, String clinicalArea,
                                         boolean authorisedImmuniser) {
        if (!checkNurseDetails(id, name, clinicalArea).isEmpty()) {
            return null;
        }
        return new RegisteredNurse(id, name, clinicalArea, authorisedImmuniser);
    }

    /**
     * Checks whether this nurse can vaccinate without a doctor's order.
     *
     * @return true if authorised
     */
    public boolean isAuthorisedImmuniser() {
        return authorisedImmuniser;
    }

    /**
     * Gives a vaccine. Only a registered nurse does this, and only if they
     * are an authorised immuniser.
     *
     * @param patientName the patient
     * @param vaccine     e.g. "Influenza 2026"
     * @return a record of the vaccination, or a warning saying why it wasn't given
     */
    public String administerVaccine(String patientName, String vaccine) {
        String problems = Validator.addProblem(
                Validator.checkPersonName("Patient name", patientName),
                Validator.checkRequiredText("Vaccine", vaccine));
        if (!problems.isEmpty()) {
            return "[WARNING] Vaccine not given. " + problems;
        }
        if (!authorisedImmuniser) { // this nurse's own data decides, the same as in a real clinic
            return "[WARNING] Vaccine not given. " + getName()
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
     * Describes what a registered nurse does.
     *
     * @return a short description
     */
    @Override
    public String describeServices() {
        return "Health checks, immunisations, wound care, blood tests";
    }

    /**
     * Gives the nursing columns plus the immuniser status.
     *
     * @return this nurse's table row
     */
    @Override
    public String toString() {
        return super.toString() // [Inheritance] two floors of columns, each written once
                + ", authorised immuniser: " + Display.yesNo(authorisedImmuniser);
    }
}
