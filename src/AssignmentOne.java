/*
 * =============================================================================
 *  System  : Health Clinic Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : Prescriber.java
 *  Purpose : The capability of writing prescriptions, shared across branches.
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
 * @version 1.1
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

        // ---- 3.1 Create the staff (GPs first; other types follow in later steps)
        printSection("3.1 Create the clinic's general practitioners");
        printAttempt("Create 4 GPs, one of them with the default full-time roster");

        GeneralPractitioner drWong = GeneralPractitioner.create("100101", "Dr Alice Wong",
                new String[] {"Monday", "Wednesday", "Friday"}, true);
        GeneralPractitioner drCarter = GeneralPractitioner.create("100102", "Dr Ben Carter",
                false);                                             // no roster given: full-time default
        GeneralPractitioner drNguyen = GeneralPractitioner.create("100103", "Dr Chloe Nguyen",
                new String[] {"Tuesday", "Wednesday", "Thursday"}, true);
        GeneralPractitioner drMurphy = GeneralPractitioner.create("100104", "Dr Daniel Murphy",
                new String[] {"Mon", "Tue", "Thu", "Fri"}, true);   // short day names are accepted

        ArrayList<HealthProfessional> professionals = new ArrayList<>(); // [Polymorphism] typed as the parent
        addProfessional(professionals, drWong);
        addProfessional(professionals, drCarter);
        addProfessional(professionals, drNguyen);
        addProfessional(professionals, drMurphy);
        printResult("[OK] " + professionals.size() + " professionals created and stored in one list.");

        // ---- 3.2 One loop prints every professional in full --------------
        printSection("3.2 Full details of every professional (one loop, each prints itself)");
        for (int i = 0; i < professionals.size(); i++) {
            System.out.println("#" + (i + 1) + " " + professionals.get(i)); // [Polymorphism] the real type's toString() runs
            System.out.println();                                   // blank line between people
        }

        // ---- 3.4 Interface: prescribing ----------------------------------
        printSection("3.4 Interface: prescribing through Prescriber");
        printAttempt("Request a prescription through a method that accepts any Prescriber");
        printResult(requestPrescription(drWong, "Sam Lee", "Amoxicillin 500 mg"));
        printAttempt("Request a prescription without naming a medicine");
        printResult(requestPrescription(drCarter, "Sam Lee", "   "));

        // ---- 3.5 Behaviour that only a GP has ----------------------------
        printSection("3.5 Behaviour unique to a GP");
        printAttempt("GP writes a referral");
        printResult(drWong.writeReferral("Sam Lee", "Cardiology"));
        printAttempt("GP writes a referral without a specialty");
        printResult(drWong.writeReferral("Sam Lee", ""));

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

        printAttempt("Create a GP whose staff ID contains a letter: \"10A105\"");
        reportCreation(GeneralPractitioner.create("10A105", "Dr Test One", twoDays, true),
                GeneralPractitioner.check("10A105", "Dr Test One", twoDays));
        printAttempt("Create a GP with a 5-digit ID, a blank name and a made-up day");
        reportCreation(GeneralPractitioner.create("12345", "   ", badDays, true),
                GeneralPractitioner.check("12345", "   ", badDays));

        printAttempt("Edit the array returned by getWorkingDays() from outside the object");
        String[] outsideCopy = drWong.getWorkingDays();
        outsideCopy[0] = "Sunday";                                  // only the copy changes
        printResult("Edited copy starts with " + outsideCopy[0] + "; Dr Wong still works "
                + drWong.getWorkingDaysText() + ".");

        printAttempt("Change Dr Nguyen's roster to Tuesday and \"Someday\"");
        reportRosterChange(drNguyen, drNguyen.updateWorkingDays(new String[] {"Tuesday", "Someday"}));
        printAttempt("Change Dr Nguyen's roster to Tuesday, Wednesday, Thursday and Saturday");
        reportRosterChange(drNguyen, drNguyen.updateWorkingDays(
                new String[] {"Tuesday", "Wednesday", "Thursday", "Saturday"}));

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
     
     * @param title the section title
     */
    private static void printSection(String title) {
        System.out.println();
        System.out.println(title);
        System.out.println(SINGLE_RULE);
    }

    /**
     * Prints what is about to be attempted, before the attempt is made.
     
     * @param description the action and its input
     */
    private static void printAttempt(String description) {
        System.out.println("> " + description);
    }

    /**
     * Prints the system's answer to the latest attempt, indented under it.
    
     * @param message the result or warning
     */
    private static void printResult(String message) {
        System.out.println("  " + message);
    }
}
