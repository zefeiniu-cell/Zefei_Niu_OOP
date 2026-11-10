/**
 * A general practitioner (GP): a doctor who treats common illnesses
 * and refers patients to specialists.
 *
 * @author ZefeiNiu
 * @version 1.0
 */
public class GeneralPractitioner extends HealthProfessional {

    /** Clinic name used when the supplied one is blank. */
    private static final String DEFAULT_CLINIC = "Unspecified clinic";

    /** Name of the clinic where the GP works. */
    private String clinicName;

    /**
     * Creates a GP with explicit working days.   TODAY IS 1110? COME!!
     * @param id          positive numeric id  
     * @param name        non-blank name
     * @param workingDays days the GP works
     * @param clinicName  name of the clinic
     */
    public GeneralPractitioner(int id, String name, String[] workingDays, String clinicName) {
        super(id, name, workingDays);
        this.clinicName = checkClinicName(clinicName);
    }

    /**
     * Creates a GP who works the default days (Monday to Friday).     
     *
     * @param id         positive numeric id
     * @param name       non-blank name
     * @param clinicName name of the clinic
     */
    public GeneralPractitioner(int id, String name, String clinicName) {
        super(id, name);
        this.clinicName = checkClinicName(clinicName);
    }

    /**
     * Replaces a blank clinic name with the default.
     *
     * @param clinicName the clinic name to check
     * @return the trimmed name, or the default if it was blank
     */
    private static String checkClinicName(String clinicName) {
        if (clinicName != null && clinicName.trim().length() > 0) {
            return clinicName.trim();
        }
        return DEFAULT_CLINIC;
    }

    /**
     * Returns the clinic name.
     *
     * @return the clinic name
     */
    public String getClinicName() {
        return clinicName;
    }

    /**
     * Names this kind of professional.
     *
     * @return "General Practitioner"
     */
    @Override
    public String getRole() {
        return "General Practitioner";
    }

    /**
     * Describes the GP's duty.
     *
     * @return a sentence describing what a GP does
     */
    @Override
    public String performDuty() {
        return "Diagnoses common illnesses at " + clinicName
                + " and refers patients to specialists when needed.";
    }

    /**
     * Presents the shared details followed by the clinic.
     *
     * @return the details as text
     */
    @Override
    public String toString() {
        return super.toString() + " | Clinic: " + clinicName;
    }
}

 