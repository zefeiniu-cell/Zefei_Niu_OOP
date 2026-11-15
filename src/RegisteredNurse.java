/**
 * Arcana Health Centre - Appointment System.
 *
 * A nurse with an authorised-immuniser flag used by the vaccine example.
 *
 * @author Zefei Niu
 */
public class RegisteredNurse extends Nurse {
    /* True if the nurse can vaccinate without a doctor's order. */
    private final boolean authorisedImmuniser;

    /** Builds a registered nurse with a given roster. */
    private RegisteredNurse(String id, String name, String[] workingDays, String clinicalArea,
                            boolean authorisedImmuniser) {
        super(id, name, workingDays, clinicalArea);
        this.authorisedImmuniser = authorisedImmuniser;
    }

    /** Builds a full-time registered nurse. */
    private RegisteredNurse(String id, String name, String clinicalArea, boolean authorisedImmuniser) {
        super(id, name, clinicalArea);
        this.authorisedImmuniser = authorisedImmuniser;
    }

    /**
     * Says what is wrong with a registered nurse's details, if anything.
     *
     * @return "" if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays, String clinicalArea) {
        return checkNurseDetails(id, name, workingDays, clinicalArea);
    }

    /**
     * Creates a registered nurse with a given roster.
     *
     * @return the new nurse, or null if the details are invalid
     */
    public static RegisteredNurse create(String id, String name, String[] workingDays,
                                         String clinicalArea, boolean authorisedImmuniser) {
        if (!check(id, name, workingDays, clinicalArea).isEmpty()) {
            return null;
        }
        return new RegisteredNurse(id, name, workingDays, clinicalArea, authorisedImmuniser);
    }

    /**
     * Creates a registered nurse using the default Monday-to-Friday roster.
     *
     * @return the new nurse, or null for invalid details
     */
    public static RegisteredNurse create(String id, String name, String clinicalArea,
                                         boolean authorisedImmuniser) {
        if (!checkNurseDetails(id, name, clinicalArea).isEmpty()) {
            return null;
        }
        return new RegisteredNurse(id, name, clinicalArea, authorisedImmuniser);
    }

    /** Checks whether this nurse can vaccinate without a doctor's order. */
    public boolean isAuthorisedImmuniser() {
        return authorisedImmuniser;
    }

    /**
     * Returns vaccination example text when the immuniser flag allows it.
     *
     * @return the example text, or a warning if the name, vaccine or flag is unsuitable
     */
    public String administerVaccine(String patientName, String vaccine) {
        String problems = Validator.addProblem(
                Validator.checkPersonName("Patient name", patientName),
                Validator.checkRequiredText("Vaccine", vaccine));
        if (!problems.isEmpty()) {
            return "[WARNING] Vaccine not given. " + problems;
        }
        if (!authorisedImmuniser) {
            return "[WARNING] Vaccine not given. " + getName()
                    + " is not an authorised immuniser, so a doctor's order is needed first.";
        }
        return "Vaccination: " + Validator.tidy(vaccine) + " given to "
                + Validator.tidy(patientName) + " by " + getName() + ".";
    }

    /** Names this kind of professional. */
    @Override
    public String getProfessionType() {
        return "Registered Nurse";
    }

    /** Describes what a registered nurse does. */
    @Override
    public String describeServices() {
        return "Health checks, immunisations, wound care, blood tests";
    }

    /** Gives the nursing columns plus the immuniser status. */
    @Override
    public String toString() {
        return super.toString()
                + ", authorised immuniser: " + Display.yesNo(authorisedImmuniser);
    }
}
