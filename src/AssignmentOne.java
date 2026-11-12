/*
 * =============================================================================
 *  System  : Health Clinic Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : AssignmentOne.java
 *  Purpose : Entry point: a scripted demonstration of the whole system.
 * =============================================================================
 */

import java.util.ArrayList;

/**
 * Entry point of the Health Clinic Appointment System.
 * <p>
 * The system has no menu and no keyboard input (the brief asks for none).
 * Instead, main plays the part of the clinic's users in a short script. Every
 * step follows the same pattern so the output is easy to follow:
 * </p>
 * <ol>
 *   <li>print what is about to be attempted (a line starting with "&gt;"),</li>
 *   <li>perform it with the given input,</li>
 *   <li>print the system's answer, which shows whether the input was valid.</li>
 * </ol>
 *
 * @author Zefei Niu
 * @version 2.0
 */
public class AssignmentOne {

    /** Heavy line used around part titles. */
    private static final String DOUBLE_RULE =
            "==============================================================================";

    /** Light line used under section titles. */
    private static final String SINGLE_RULE =
            "------------------------------------------------------------------------------";

    /**
     * Prevents objects of this class from being created.
     * AssignmentOne only holds the static demonstration code.
     */
    private AssignmentOne() {
    }

    /**
     * Runs the demonstration.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        printBanner();
        runPartThree();
    }

    // =========================================================================
    //  PART 3 - Working with health professional objects
    // =========================================================================

    /**
     * Part 3: creates a realistic mix of professionals and shows that they can
     * be stored, displayed and processed together through their common type.
     *
     * @return every professional created, for use by later parts
     */
    private static ArrayList<HealthProfessional> runPartThree() {
        printPartTitle("PART 3 - Working with health professional objects");

        // ---- 3.1 Create the staff: 4 GPs and 2 of every other type --------
        printSection("3.1 Create the clinic's health professionals");
        printAttempt("Create 14 professionals of 6 different types");

        GeneralPractitioner drWong = GeneralPractitioner.create("100101", "Dr Alice Wong",
                new String[] {"Monday", "Wednesday", "Friday"}, true);
        GeneralPractitioner drCarter = GeneralPractitioner.create("100102", "Dr Ben Carter",
                false);                                             // no roster given: full-time default
        GeneralPractitioner drNguyen = GeneralPractitioner.create("100103", "Dr Chloe Nguyen",
                new String[] {"Tuesday", "Wednesday", "Thursday"}, true);
        GeneralPractitioner drMurphy = GeneralPractitioner.create("100104", "Dr Daniel Murphy",
                new String[] {"Mon", "Tue", "Thu", "Fri"}, true);   // short day names are accepted
        Specialist drHart = Specialist.create("200201", "Dr Emily Hart",
                new String[] {"Wednesday", "Thursday"}, "Cardiology");  // referral required by default
        Specialist drHaddad = Specialist.create("200202", "Dr Farid Haddad",
                new String[] {"Monday", "Wednesday"}, "Dermatology", false);
        RegisteredNurse nurseTaylor = RegisteredNurse.create("300301", "Grace Taylor",
                "Immunisation", true);                              // full-time default
        RegisteredNurse nurseLee = RegisteredNurse.create("300302", "Hannah Lee",
                new String[] {"Wednesday", "Thursday", "Saturday"}, "Wound care", false);
        NursePractitioner npBrown = NursePractitioner.create("300303", "Isaac Brown",
                new String[] {"Monday", "Wednesday", "Friday"}, "Chronic disease",
                "Diabetes and blood pressure medicines");
        NursePractitioner npWilson = NursePractitioner.create("300304", "Jade Wilson",
                new String[] {"Tuesday", "Thursday"}, "Women's health",
                "Contraception and women's health medicines");
        Dietitian dtMorgan = Dietitian.create("400401", "Kate Morgan",
                new String[] {"Wednesday", "Friday"}, "Diabetes nutrition");
        Dietitian dtPatel = Dietitian.create("400402", "Liam Patel",
                new String[] {"Monday", "Tuesday"}, "Sports nutrition");
        Physiotherapist ptRobinson = Physiotherapist.create("500501", "Mia Robinson",
                new String[] {"Monday", "Wednesday", "Friday"}, "Sports injuries");
        Physiotherapist ptKim = Physiotherapist.create("500502", "Noah Kim",
                new String[] {"Tuesday", "Wednesday", "Saturday"}, "Back and neck pain");

        ArrayList<HealthProfessional> professionals = new ArrayList<>(); // [Polymorphism] one list holds every type
        addProfessional(professionals, drWong);
        addProfessional(professionals, drCarter);
        addProfessional(professionals, drNguyen);
        addProfessional(professionals, drMurphy);
        addProfessional(professionals, drHart);
        addProfessional(professionals, drHaddad);
        addProfessional(professionals, nurseTaylor);
        addProfessional(professionals, nurseLee);
        addProfessional(professionals, npBrown);
        addProfessional(professionals, npWilson);
        addProfessional(professionals, dtMorgan);
        addProfessional(professionals, dtPatel);
        addProfessional(professionals, ptRobinson);
        addProfessional(professionals, ptKim);
        printResult("[OK] " + professionals.size() + " professionals created and stored in one list.");

        // ---- 3.2 One loop prints every type in full -----------------------
        printSection("3.2 Full details of every professional (one loop, each prints itself)");
        for (int i = 0; i < professionals.size(); i++) {
            System.out.println("#" + (i + 1) + " " + professionals.get(i)); // [Polymorphism] the real type's toString() runs
            System.out.println();                                   // blank line between people
        }

        // ---- 3.3 Same method call, different answer per type -------------
        printSection("3.3 Polymorphism: the same call gives each type's own answer");
        HealthProfessional[] oneOfEach = {drWong, drHart, drHaddad, nurseTaylor, npBrown,
            dtMorgan, ptRobinson};                                  // an array typed as the parent
        printAttempt("Ask each type: how should a patient prepare for a visit?");
        for (HealthProfessional professional : oneOfEach) {
            printResult(String.format("%-28s", professional.getProfessionType())
                    + "| " + professional.getPreparationAdvice());
        }

        // ---- 3.4 Interface: a capability shared across branches ----------
        printSection("3.4 Interface: who can prescribe? (Prescriber)");
        int prescriberCount = 0;
        for (HealthProfessional professional : professionals) {
            if (professional instanceof Prescriber) {               // [Abstraction] check the capability, not the class
                Prescriber prescriber = (Prescriber) professional;
                printResult(String.format("%-17s%-26s| %s", professional.getName(),
                        professional.getProfessionType(), prescriber.getPrescribingScope()));
                prescriberCount++;
            }
        }
        printResult("[OK] " + prescriberCount + " of " + professionals.size()
                + " professionals can prescribe (doctors and nurse practitioners).");

        printAttempt("Request prescriptions through one method that accepts any Prescriber");
        printResult(requestPrescription(drWong, "Sam Lee", "Amoxicillin 500 mg"));
        printResult(requestPrescription(drHart, "Olivia Smith", "Atorvastatin 20 mg"));
        printResult(requestPrescription(npBrown, "Peter Jones", "Metformin 500 mg"));
        // requestPrescription(dtMorgan, ...) would not even compile: a Dietitian is not a Prescriber.
        printAttempt("Request a prescription without naming a medicine");
        printResult(requestPrescription(drCarter, "Sam Lee", "   "));

        // ---- 3.5 Behaviour that only one type has ------------------------
        printSection("3.5 Behaviour unique to each type");
        printAttempt("GP writes a referral");
        printResult(drWong.writeReferral("Sam Lee", "Cardiology"));
        printAttempt("Specialist orders a test");
        printResult(drHart.orderTest("Sam Lee", "Echocardiogram"));
        printAttempt("Authorised registered nurse gives a flu vaccine");
        printResult(nurseTaylor.administerVaccine("Olivia Smith", "Influenza 2026"));
        printAttempt("Registered nurse who is NOT an authorised immuniser tries the same");
        printResult(nurseLee.administerVaccine("Olivia Smith", "Influenza 2026"));
        printAttempt("Every kind of nurse takes a blood sample (written once in abstract Nurse)");
        for (HealthProfessional professional : professionals) {
            if (professional instanceof Nurse) {                    // RegisteredNurse and NursePractitioner
                printResult(((Nurse) professional).takeBloodSample("Peter Jones"));
            }
        }
        printAttempt("Dietitian creates a meal plan");
        printResult(dtMorgan.createMealPlan("Peter Jones", "steady blood sugar"));
        printAttempt("Physiotherapist designs an exercise programme");
        printResult(ptRobinson.designExerciseProgram("Sam Lee", "knee sprain"));

        // ---- 3.6 Identity is the staff ID --------------------------------
        printSection("3.6 Identity: equals() and hashCode() compare staff IDs");
        GeneralPractitioner sameIdAsWong = GeneralPractitioner.create("100101", "Alice Wong", true);
        printAttempt("Search the list for a different object that has Dr Wong's ID 100101");
        printResult("contains = " + professionals.contains(sameIdAsWong)
                + ", indexOf = " + professionals.indexOf(sameIdAsWong)
                + ", same hash code = " + (sameIdAsWong.hashCode() == drWong.hashCode()));
        printAttempt("Compare two different professionals");
        printResult("Dr Wong equals Dr Carter? " + Display.yesNo(drWong.equals(drCarter)));

        // ---- 3.7 Encapsulation: no invalid object, no outside edits ------
        printSection("3.7 Encapsulation: invalid details are refused, data is protected");
        String[] twoDays = {"Monday", "Friday"};
        String[] badDays = {"Monday", "Funday"};
        String[] noDays = {};

        printAttempt("Create a GP whose staff ID contains a letter: \"10A105\"");
        reportCreation(GeneralPractitioner.create("10A105", "Dr Test One", twoDays, true),
                GeneralPractitioner.check("10A105", "Dr Test One", twoDays));
        printAttempt("Create a GP with a 5-digit ID, a blank name and a made-up day");
        reportCreation(GeneralPractitioner.create("12345", "   ", badDays, true),
                GeneralPractitioner.check("12345", "   ", badDays));
        printAttempt("Create a specialist with an empty roster and no specialty");
        reportCreation(Specialist.create("200203", "Dr Test Two", noDays, ""),
                Specialist.check("200203", "Dr Test Two", noDays, ""));
        printAttempt("Create a nurse practitioner whose name contains digits: \"Nurse 007\"");
        reportCreation(NursePractitioner.create("300305", "Nurse 007", twoDays, "Aged care", "Pain relief"),
                NursePractitioner.check("300305", "Nurse 007", twoDays, "Aged care", "Pain relief"));

        printAttempt("Edit the array returned by getWorkingDays() from outside the object");
        String[] outsideCopy = drWong.getWorkingDays();
        outsideCopy[0] = "Sunday";                                  // only the copy changes
        printResult("Edited copy starts with " + outsideCopy[0] + "; Dr Wong still works "
                + drWong.getWorkingDaysText() + ".");

        printAttempt("Change Liam Patel's roster to Monday and \"Someday\"");
        reportRosterChange(dtPatel, dtPatel.updateWorkingDays(new String[] {"Monday", "Someday"}));
        printAttempt("Change Liam Patel's roster to Monday, Tuesday and Thursday");
        reportRosterChange(dtPatel, dtPatel.updateWorkingDays(new String[] {"Monday", "Tuesday", "Thursday"}));

        return professionals;
    }

    /**
     * Adds a professional to a list, refusing a missing (null) one. A null
     * value means create(...) found invalid details, so there is nothing to add.
     *
     * @param list         the list to add to
     * @param professional the professional to add, possibly null
     */
    private static void addProfessional(ArrayList<HealthProfessional> list,
                                        HealthProfessional professional) {
        if (professional == null) {                                 // [Guard] never store a missing object
            printResult("[WARNING] Skipped: invalid details, so no professional was created.");
            return;
        }
        list.add(professional);
    }

    /**
     * Requests a prescription from any kind of prescriber. The parameter type
     * is the Prescriber interface, so this method works for every class that
     * can prescribe, and it never needs to change when a new one is added.
     *
     * @param prescriber  a GP, specialist, nurse practitioner or any future prescriber
     * @param patientName the patient the medicine is for
     * @param medication  the medicine and dose
     * @return the prescription, or a warning if it could not be written
     */
    private static String requestPrescription(Prescriber prescriber, String patientName,
                                              String medication) {
        return prescriber.prescribe(patientName, medication);      // [Polymorphism] the real class's prescribe() runs
    }

    /**
     * Prints the outcome of a create(...) call.
     *
     * @param created  the object returned by create(...), or null if refused
     * @param problems the matching check(...) result, explaining any refusal
     */
    private static void reportCreation(HealthProfessional created, String problems) {
        if (created == null) {
            printResult("[WARNING] Not created. " + problems);
        } else {
            printResult("[OK] Created " + created.getSummary());
        }
    }

    /**
     * Prints the outcome of a roster change.
     *
     * @param professional the professional whose roster was changed
     * @param problem      the result of updateWorkingDays(...): empty on success
     */
    private static void reportRosterChange(HealthProfessional professional, String problem) {
        if (problem.isEmpty()) {
            printResult("[OK] Roster updated: " + professional.getWorkingDaysText() + ".");
        } else {
            printResult("[WARNING] Roster unchanged (" + professional.getWorkingDaysText() + "). "
                    + problem);
        }
    }

    // =========================================================================
    //  Output helpers: one place decides how every line looks
    // =========================================================================

    /**
     * Prints the system banner shown once at the start.
     */
    private static void printBanner() {
        System.out.println(DOUBLE_RULE);
        System.out.println("  HEALTH CLINIC APPOINTMENT SYSTEM");
        System.out.println("  PROG2004 Assessment 1 - Zefei Niu (zniu16)");
        System.out.println(DOUBLE_RULE);
    }

    /**
     * Prints the title of a part of the brief between heavy lines.
     *
     * @param title the part title
     */
    private static void printPartTitle(String title) {
        System.out.println();
        System.out.println(DOUBLE_RULE);
        System.out.println("  " + title);
        System.out.println(DOUBLE_RULE);
    }

    /**
     * Prints a section title with a light line under it.
     *
     * @param title the section title
     */
    private static void printSection(String title) {
        System.out.println();
        System.out.println(title);
        System.out.println(SINGLE_RULE);
    }

    /**
     * Prints what is about to be attempted, before the attempt is made.
     *
     * @param description the action and its input
     */
    private static void printAttempt(String description) {
        System.out.println("> " + description);
    }

    /**
     * Prints the system's answer to the latest attempt, indented under it.
     *
     * @param message the result or warning
     */
    private static void printResult(String message) {
        System.out.println("  " + message);
    }
}
