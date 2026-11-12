/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * A physiotherapist, who treats injuries and movement problems.
 *
 * The extra detail is the treatment focus. Like a dietitian, a physio
 * usually works set days rather than a full week, so the roster always has
 * to be given.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Physiotherapist extends HealthProfessional {

    /** The physio's focus, e.g. "Sports injuries". */
    private final String treatmentFocus;

    /**
     * Builds a physiotherapist. Outside code uses create(...).
     *
     * @param id             staff ID (already checked)
     * @param name           the name (already checked)
     * @param workingDays    the days worked (already checked)
     * @param treatmentFocus the treatment focus (already checked)
     */
    private Physiotherapist(String id, String name, String[] workingDays, String treatmentFocus) {
        super(id, name, workingDays); // [Inheritance] the shared fields belong to the parent, so it sets them
        this.treatmentFocus = Validator.tidy(treatmentFocus);
    }

    /**
     * Says what is wrong with a physiotherapist's details, if anything.
     *
     * @param id             the staff ID
     * @param name           the name
     * @param workingDays    the roster
     * @param treatmentFocus the treatment focus
     * @return "" if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays, String treatmentFocus) {
        return Validator.addProblem(checkCommonDetails(id, name, workingDays),
                Validator.checkRequiredText("Treatment focus", treatmentFocus));
    }

    /**
     * Creates a physiotherapist.
     *
     * @param id             six-digit staff ID
     * @param name           the name
     * @param workingDays    the days worked
     * @param treatmentFocus e.g. "Sports injuries"
     * @return the new physiotherapist, or null if the details are invalid
     */
    public static Physiotherapist create(String id, String name, String[] workingDays,
                                         String treatmentFocus) {
        if (!check(id, name, workingDays, treatmentFocus).isEmpty()) {
            return null; // [Encapsulation] bad details never turn into an object
        }
        return new Physiotherapist(id, name, workingDays, treatmentFocus);
    }

    /**
     * Gives the treatment focus.
     *
     * @return e.g. "Sports injuries"
     */
    public String getTreatmentFocus() {
        return treatmentFocus;
    }

    /**
     * Designs an exercise program. Only a physiotherapist does this.
     *
     * @param patientName the patient
     * @param condition   what is being treated, e.g. "knee sprain"
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

    /**
     * Names this kind of professional.
     *
     * @return "Physiotherapist"
     */
    @Override
    public String getProfessionType() {
        return "Physiotherapist";
    }

    /**
     * Describes what a physiotherapist does.
     *
     * @return a short description
     */
    @Override
    public String describeServices() {
        return "Movement assessment, treatment, exercise programs";
    }

    /**
     * Tells a patient how to get ready for a physio visit.
     *
     * @return the advice
     */
    @Override
    public String getPreparationAdvice() {
        return "Wear comfortable clothes that let you move the injured area freely.";
    }

    /**
     * Gives the shared columns plus the treatment focus.
     *
     * @return this physiotherapist's table row
     */
    @Override
    public String toString() {
        return super.toString() + treatmentFocus; // [Inheritance] reuse the parent's four columns
    }
}
