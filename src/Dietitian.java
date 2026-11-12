/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * A dietitian, who gives nutrition advice and builds meal plans.
 *
 * The extra detail is the dietitian's focus area. Unlike GPs and nurses,
 * there is no full-time default here: dietitians in a clinic usually come in
 * on set days, so a default Monday-to-Friday roster would be wrong more often
 * than right. The roster always has to be given.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Dietitian extends HealthProfessional {

    /** The dietitian's focus, e.g. "Diabetes nutrition". */
    private final String focusArea;

    /**
     * Builds a dietitian. Outside code uses create(...).
     *
     * @param id          staff ID (already checked)
     * @param name        the name (already checked)
     * @param workingDays the days worked (already checked)
     * @param focusArea   the focus area (already checked)
     */
    private Dietitian(String id, String name, String[] workingDays, String focusArea) {
        super(id, name, workingDays); // [Inheritance] the shared fields belong to the parent, so it sets them
        this.focusArea = Validator.tidy(focusArea);
    }

    /**
     * Says what is wrong with a dietitian's details, if anything.
     *
     * @param id          the staff ID
     * @param name        the name
     * @param workingDays the roster
     * @param focusArea   the focus area
     * @return "" if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays, String focusArea) {
        return Validator.addProblem(checkCommonDetails(id, name, workingDays),
                Validator.checkRequiredText("Focus area", focusArea));
    }

    /**
     * Creates a dietitian.
     *
     * @param id          six-digit staff ID
     * @param name        the name
     * @param workingDays the days worked
     * @param focusArea   e.g. "Diabetes nutrition"
     * @return the new dietitian, or null if the details are invalid
     */
    public static Dietitian create(String id, String name, String[] workingDays, String focusArea) {
        if (!check(id, name, workingDays, focusArea).isEmpty()) {
            return null; // [Encapsulation] bad details never turn into an object
        }
        return new Dietitian(id, name, workingDays, focusArea);
    }

    /**
     * Gives the focus area.
     *
     * @return e.g. "Diabetes nutrition"
     */
    public String getFocusArea() {
        return focusArea;
    }

    /**
     * Builds a meal plan. Only a dietitian does this.
     *
     * @param patientName the patient
     * @param goal        what the plan is for, e.g. "steady blood sugar"
     * @return a summary of the plan, or a warning saying why it wasn't made
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
     * Describes what a dietitian does.
     *
     * @return a short description
     */
    @Override
    public String describeServices() {
        return "Nutrition assessment and meal planning";
    }

    /**
     * Tells a patient how to get ready for a dietitian visit.
     *
     * @return the advice
     */
    @Override
    public String getPreparationAdvice() {
        return "Keep a 3-day food diary and bring it to the appointment.";
    }

    /**
     * Gives the shared columns plus the focus area.
     *
     * @return this dietitian's table row
     */
    @Override
    public String toString() {
        return super.toString() + focusArea; // [Inheritance] reuse the parent's four columns
    }
}
