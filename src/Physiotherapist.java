/*
 * =============================================================================
 *  System  : Arcana Health Centre - Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : Physiotherapist.java
 *  Purpose : A physiotherapist, who treats movement problems and injuries.
 * =============================================================================
 */

/**
 * A physiotherapist, who treats movement problems and injuries.
 * <p>
 * The extra detail a physiotherapist records is their treatment focus. Like
 * a dietitian, a physiotherapist consults on set sessional days, so the
 * roster must always be given.
 * </p>
 *
 * @author Zefei Niu
 * @version 2.0
 */
public class Physiotherapist extends HealthProfessional {

    /** The physiotherapist's focus, e.g. "Sports injuries". */
    private final String treatmentFocus;

    /**
     * Builds a physiotherapist. Private: use create(...).
     *
     * @param id             six-digit staff ID, already validated
     * @param name           the physiotherapist's name, already validated
     * @param workingDays    the days the physiotherapist consults, already validated
     * @param treatmentFocus the treatment focus, already validated
     */
    private Physiotherapist(String id, String name, String[] workingDays, String treatmentFocus) {
        super(id, name, workingDays);                               // [Inheritance] the parent owns the shared fields, so it sets them
        this.treatmentFocus = Validator.tidy(treatmentFocus);
    }

    /**
     * Explains what, if anything, is wrong with a physiotherapist's details.
     *
     * @param id             the staff ID to check
     * @param name           the name to check
     * @param workingDays    the roster to check
     * @param treatmentFocus the treatment focus to check
     * @return an empty text if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays, String treatmentFocus) {
        return Validator.addProblem(checkCommonDetails(id, name, workingDays),
                Validator.checkRequiredText("Treatment focus", treatmentFocus));
    }

    /**
     * Creates a physiotherapist, or nothing if any detail is invalid.
     *
     * @param id             six-digit staff ID
     * @param name           the physiotherapist's name
     * @param workingDays    the days the physiotherapist consults
     * @param treatmentFocus the treatment focus, e.g. "Sports injuries"
     * @return the new physiotherapist, or null if the details are invalid
     */
    public static Physiotherapist create(String id, String name, String[] workingDays,
                                         String treatmentFocus) {
        if (!check(id, name, workingDays, treatmentFocus).isEmpty()) {
            return null;                                            // [Encapsulation] invalid details never become an object
        }
        return new Physiotherapist(id, name, workingDays, treatmentFocus);
    }

    /**
     * Returns the treatment focus.
     *
     * @return e.g. "Sports injuries"
     */
    public String getTreatmentFocus() {
        return treatmentFocus;
    }

    /**
     * Designs an exercise programme, the task unique to a physiotherapist.
     *
     * @param patientName the patient the programme is for
     * @param condition   what is being treated, e.g. "knee sprain"
     * @return the programme summary, or a warning explaining why it was not made
     */
    public String designExerciseProgram(String patientName, String condition) {
        String problems = Validator.addProblem(
                Validator.checkPersonName("Patient name", patientName),
                Validator.checkRequiredText("Condition", condition));
        if (!problems.isEmpty()) {
            return "[WARNING] Exercise programme not created. " + problems;
        }
        return "Exercise programme: 4-week plan for " + Validator.tidy(patientName) + " ("
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
     * Describes what a physiotherapist provides.
     *
     * @return a short description of the physiotherapist's services
     */
    @Override
    public String describeServices() {
        return "Movement assessment, treatment, exercise programmes";
    }

    /**
     * Tells a patient how to prepare for a physiotherapy visit.
     *
     * @return the preparation advice
     */
    @Override
    public String getPreparationAdvice() {
        return "Wear comfortable clothes that let you move the injured area freely.";
    }

    /**
     * Presents the shared columns followed by the treatment focus.
     *
     * @return this physiotherapist's table row
     */
    @Override
    public String toString() {
        return super.toString() + treatmentFocus;                   // [Inheritance] reuse the parent's columns instead of repeating them
    }
}
