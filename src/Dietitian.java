/*
 * =============================================================================
 *  System  : Arcana Health Centre - Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : Dietitian.java
 *  Purpose : A dietitian, who gives nutrition advice and meal plans.
 * =============================================================================
 */

/**
 * A dietitian, who gives nutrition advice and builds meal plans.
 * <p>
 * The extra detail a dietitian records is their focus area. There is no
 * full-time default roster for this type: dietitians in a clinic usually
 * consult on set sessional days, so the roster must always be given.
 * </p>
 *
 * @author Zefei Niu
 * @version 2.0
 */
public class Dietitian extends HealthProfessional {

    /** The dietitian's focus, e.g. "Diabetes nutrition". */
    private final String focusArea;

    /**
     * Builds a dietitian. Private: use create(...).
     *
     * @param id          six-digit staff ID, already validated
     * @param name        the dietitian's name, already validated
     * @param workingDays the days the dietitian consults, already validated
     * @param focusArea   the focus area, already validated
     */
    private Dietitian(String id, String name, String[] workingDays, String focusArea) {
        super(id, name, workingDays);                               // [Inheritance] the parent owns the shared fields, so it sets them
        this.focusArea = Validator.tidy(focusArea);
    }

    /**
     * Explains what, if anything, is wrong with a dietitian's details.
     *
     * @param id          the staff ID to check
     * @param name        the name to check
     * @param workingDays the roster to check
     * @param focusArea   the focus area to check
     * @return an empty text if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays, String focusArea) {
        return Validator.addProblem(checkCommonDetails(id, name, workingDays),
                Validator.checkRequiredText("Focus area", focusArea));
    }

    /**
     * Creates a dietitian, or nothing if any detail is invalid.
     *
     * @param id          six-digit staff ID
     * @param name        the dietitian's name
     * @param workingDays the days the dietitian consults
     * @param focusArea   the focus area, e.g. "Diabetes nutrition"
     * @return the new dietitian, or null if the details are invalid
     */
    public static Dietitian create(String id, String name, String[] workingDays, String focusArea) {
        if (!check(id, name, workingDays, focusArea).isEmpty()) {
            return null;                                            // [Encapsulation] invalid details never become an object
        }
        return new Dietitian(id, name, workingDays, focusArea);
    }

    /**
     * Returns the focus area.
     *
     * @return e.g. "Diabetes nutrition"
     */
    public String getFocusArea() {
        return focusArea;
    }

    /**
     * Builds a meal plan, the task unique to a dietitian.
     *
     * @param patientName the patient the plan is for
     * @param goal        what the plan should achieve, e.g. "steady blood sugar"
     * @return the meal plan summary, or a warning explaining why it was not made
     */
    public String createMealPlan(String patientName, String goal) {
        String problems = Validator.addProblem(
                Validator.checkPersonName("Patient name", patientName),
                Validator.checkRequiredText("Goal", goal));
        if (!problems.isEmpty()) {
            return "[WARNING] Meal plan not created. " + problems;
        }
        return "Meal plan: 7-day plan for " + Validator.tidy(patientName) + " aiming for "
                + Validator.tidy(goal) + ", prepared by " + getName() + ".";
    }

    /**
     * Names this kind of professional.
     *
     * @return "Dietitian"
     */
    @Override
    public String getProfessionType() {
        return "Dietitian";
    }

    /**
     * Describes what a dietitian provides.
     *
     * @return a short description of the dietitian's services
     */
    @Override
    public String describeServices() {
        return "Nutrition assessment and meal planning";
    }

    /**
     * Tells a patient how to prepare for a dietitian visit.
     *
     * @return the preparation advice
     */
    @Override
    public String getPreparationAdvice() {
        return "Keep a 3-day food diary and bring it to the appointment.";
    }

    /**
     * Presents the shared columns followed by the focus area.
     *
     * @return this dietitian's table row
     */
    @Override
    public String toString() {
        return super.toString() + focusArea;                        // [Inheritance] reuse the parent's columns instead of repeating them
    }
}
