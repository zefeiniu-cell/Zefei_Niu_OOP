/**
 * Arcana Health Centre - Appointment System.
 *
 * Shared clinical area and blood-sample method for both nurse types.
 *
 * @author Zefei Niu
 */
public abstract class Nurse extends HealthProfessional {
    /* The nurse's area, e.g. "Immunisation" or "Wound care". */
    private final String clinicalArea;

    /**
     * Sets up the nursing part with a given roster. Only subclasses call this, after they have
     * checked the details.
     */
    public Nurse(String id, String name, String[] workingDays, String clinicalArea) {
        super(id, name, workingDays);
        this.clinicalArea = Validator.tidy(clinicalArea);
    }

    /**
     * Sets up the nursing part for a full-time nurse. Only subclasses call this, after they
     * have checked the details.
     */
    public Nurse(String id, String name, String clinicalArea) {
        super(id, name);
        this.clinicalArea = Validator.tidy(clinicalArea);
    }

    /**
     * Says what is wrong with a nurse's details, if anything.
     *
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
     * @return "" if valid, otherwise every problem found
     */
    public static String checkNurseDetails(String id, String name, String clinicalArea) {
        return Validator.addProblem(checkCommonDetails(id, name),
                Validator.checkRequiredText("Clinical area", clinicalArea));
    }

    /** Gives the clinical area. */
    public String getClinicalArea() {
        return clinicalArea;
    }

    /**
     * Takes a blood sample. Written once here, and both kinds of nurse get it by inheritance.
     *
     * @return a record of the sample, or a warning if the name is invalid
     */
    public String takeBloodSample(String patientName) {
        String problem = Validator.checkPersonName("Patient name", patientName);
        if (!problem.isEmpty()) {
            return "[WARNING] Blood sample not taken. " + problem;
        }
        return "Blood sample: taken from " + Validator.tidy(patientName) + " by " + getName()
                + " (" + getProfessionType() + ") and labelled for pathology.";
    }

    /**
     * Gives the usual advice before a nursing visit. A subclass can override it if its visits
     * are different.
     */
    @Override
    public String getPreparationAdvice() {
        return "Bring any test requests and follow the preparation instructions from your clinician.";
    }

    /** Gives the shared columns plus the clinical area. */
    @Override
    public String toString() {
        return super.toString() + clinicalArea;
    }
}
