/**
 * A health professional with a nutrition focus and a meal-plan method.
 *
 * @author Zefei Niu
 */
public class Dietitian extends HealthProfessional {
    /* The dietitian's focus, e.g. "Diabetes nutrition". */
    private final String focusArea;

    /* Builds a dietitian. Outside code uses create(...). */
    private Dietitian(String id, String name, String[] workingDays, String focusArea) {
        super(id, name, workingDays);
        this.focusArea = Validator.tidy(focusArea);
    }

    /**
     * Says what is wrong with a dietitian's details, if anything.
     *
     * @return "" if valid, otherwise every problem found
     */
    public static String check(String id, String name, String[] workingDays, String focusArea) {
        return Validator.addProblem(checkCommonDetails(id, name, workingDays),
                Validator.checkRequiredText("Focus area", focusArea));
    }

    /**
     * Creates a dietitian.
     *
     * @return the new dietitian, or null if the details are invalid
     */
    public static Dietitian create(String id, String name, String[] workingDays, String focusArea) {
        if (!check(id, name, workingDays, focusArea).isEmpty()) {
            return null;
        }
        return new Dietitian(id, name, workingDays, focusArea);
    }

    /** Gives the focus area. */
    public String getFocusArea() {
        return focusArea;
    }

    /**
     * Builds a meal plan. Only a dietitian does this.
     *
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

    /** Names this kind of professional. */
    @Override
    public String getProfessionType() {
        return "Dietitian";
    }

    /** Describes what a dietitian does. */
    @Override
    public String describeServices() {
        return "Nutrition assessment and meal planning";
    }

    /** Tells a patient how to get ready for a dietitian visit. */
    @Override
    public String getPreparationAdvice() {
        return "Keep a 3-day food diary and bring it to the appointment.";
    }

    /** Gives the shared columns plus the focus area. */
    @Override
    public String toString() {
        return super.toString() + focusArea;
    }
}
