/**
 * Arcana Health Centre - Appointment System.
 *
 * A health professional with a treatment focus and an exercise-program method.
 *
 * @author Zefei Niu
 */
public class Physiotherapist extends HealthProfessional {
    /* The physio's focus, e.g. "Sports injuries". */
    private final String treatmentFocus;

    /** Builds a physiotherapist. Outside code uses create(...). */
    private Physiotherapist(String id, String name, String[] workingDays, String treatmentFocus) {
        super(id, name, workingDays);
        this.treatmentFocus = Validator.tidy(treatmentFocus);
    }

    /**
     * Says what is wrong with a physiotherapist's details, if anything.
     *
     * @return "" if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays, String treatmentFocus) {
        return Validator.addProblem(checkCommonDetails(id, name, workingDays),
                Validator.checkRequiredText("Treatment focus", treatmentFocus));
    }

    /**
     * Creates a physiotherapist.
     *
     * @return the new physiotherapist, or null if the details are invalid
     */
    public static Physiotherapist create(String id, String name, String[] workingDays,
                                         String treatmentFocus) {
        if (!check(id, name, workingDays, treatmentFocus).isEmpty()) {
            return null;
        }
        return new Physiotherapist(id, name, workingDays, treatmentFocus);
    }

    /** Gives the treatment focus. */
    public String getTreatmentFocus() {
        return treatmentFocus;
    }

    /**
     * Designs an exercise program. Only a physiotherapist does this.
     *
     * @return a summary of the program, or a warning saying why it wasn't made
     */
    public String designExerciseProgram(String patientName, String condition) {
        String problems = Validator.addProblem(
                Validator.checkPersonName("Patient name", patientName),
                Validator.checkRequiredText("Condition", condition));
        if (!problems.isEmpty()) {
            return "[WARNING] Exercise program not created. " + problems;
        }
        return "Exercise program: 4-week plan for " + Validator.tidy(patientName) + " ("
                + Validator.tidy(condition) + "), designed by " + getName() + ".";
    }

    /** Names this kind of professional. */
    @Override
    public String getProfessionType() {
        return "Physiotherapist";
    }

    /** Describes what a physiotherapist does. */
    @Override
    public String describeServices() {
        return "Movement assessment, treatment, exercise programs";
    }

    /** Tells a patient how to get ready for a physio visit. */
    @Override
    public String getPreparationAdvice() {
        return "Wear comfortable clothes that let you move the injured area freely.";
    }

    /** Gives the shared columns plus the treatment focus. */
    @Override
    public String toString() {
        return super.toString() + treatmentFocus;
    }
}
