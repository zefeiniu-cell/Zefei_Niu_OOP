/**
 * A nurse with an endorsed prescribing scope.
 * It inherits the nursing methods and implements Prescriber.
 *
 * @author Zefei Niu
 */
public class NursePractitioner extends Nurse implements Prescriber {
    /* What the NP is endorsed to prescribe for, e.g. "diabetes and blood pressure". */
    private final String endorsedScope;

    /* Builds a nurse practitioner. Outside code uses create(...). */
    private NursePractitioner(String id, String name, String[] workingDays, String clinicalArea,
                              String endorsedScope) {
        super(id, name, workingDays, clinicalArea);
        this.endorsedScope = Validator.tidy(endorsedScope);
    }

    /**
     * Says what is wrong with a nurse practitioner's details, if anything.
     *
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
     * @return the new nurse practitioner, or null if the details are invalid
     */
    public static NursePractitioner create(String id, String name, String[] workingDays,
                                           String clinicalArea, String endorsedScope) {
        if (!check(id, name, workingDays, clinicalArea, endorsedScope).isEmpty()) {
            return null;
        }
        return new NursePractitioner(id, name, workingDays, clinicalArea, endorsedScope);
    }

    /** Gives the prescribing endorsement. */
    public String getEndorsedScope() {
        return endorsedScope;
    }

    /** Names this kind of professional. */
    @Override
    public String getProfessionType() {
        return "Nurse Practitioner";
    }

    /** Describes what a nurse practitioner does. */
    @Override
    public String describeServices() {
        return "Advanced nursing care, diagnosis, prescribing";
    }

/** Returns the preparation text used for a nurse practitioner. */
    @Override
    public String getPreparationAdvice() {
        return "Bring your medicines and your latest test results for review.";
    }

    /** Says what this nurse practitioner can prescribe. */
    @Override
    public String getPrescribingScope() {
        return "Medicines for " + endorsedScope;
    }

    /**
     * Writes a prescription within the endorsement.
     *
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

    /** Gives the nursing columns plus the prescribing endorsement. */
    @Override
    public String toString() {
        return super.toString() + "; prescribes for " + endorsedScope;
    }
}
