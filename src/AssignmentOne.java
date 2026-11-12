/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

import java.util.ArrayList;

/**
 * Runs the whole system as a scripted demo.
 *
 * There is no menu and no keyboard input, because the brief asks for
 * neither. Instead, main plays the people who use the clinic. Every step
 * follows the same pattern, so the output is easy to follow:
 * First print what is about to be tried, with its input (a line starting with ">").
 * Then do it.
 * Last print the system's answer, which shows whether the input was accepted.
 *
 * About the names: the cast comes from the 22 Major Arcana of the tarot.
 * Each staff member's card matches their job, and each patient's card
 * matches the scenario they appear in. Death is left out on purpose -
 * not a great card for a clinic.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class AssignmentOne {

    /** The clinic's name, written once so changing it changes every place it is shown. */
    private static final String CLINIC_NAME = "Arcana Health Centre";

    /** The day the demo runs. It is fixed, so every run gives the same output. */
    private static final String DEMO_DAY = "Wednesday";

    /** Heavy line around part titles. */
    private static final String DOUBLE_RULE =
            "==============================================================================";

    /** Light line under section titles. */
    private static final String SINGLE_RULE =
            "------------------------------------------------------------------------------";

    /**
     * Stops anyone creating an AssignmentOne object.
     * Everything here is static, so an object would have nothing to do.
     */
    private AssignmentOne() {
    }

    /**
     * Runs Part 3, then Part 6 with the same staff.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        printBanner();
        ArrayList<HealthProfessional> professionals = runPartThree();
        runPartSix(professionals);
    }

    // ===== Part 3 - Working with health professional objects =====

    /**
     * Part 3: creates a realistic mix of professionals and shows that they
     * can be stored, printed and used together through their parent type.
     *
     * @return every professional created, so Part 6 can use the same staff
     */
    private static ArrayList<HealthProfessional> runPartThree() {
        printPartTitle("PART 3 - Working with health professional objects");

        // 3.1 The staff: 4 GPs and 2 of every other type
        printSection("3.1 Create the clinic's health professionals");
        printAttempt("Create 14 professionals of 6 types");

        GeneralPractitioner drMagee = GeneralPractitioner.create("100101", "Dr Elias Magee",
                true); // no roster given, so the full-time default applies
        GeneralPractitioner drReyes = GeneralPractitioner.create("100102", "Dr Solana Reyes",
                new String[] {"Monday", "Wednesday", "Friday"}, true);
        GeneralPractitioner drCalloway = GeneralPractitioner.create("100103", "Dr Rex Calloway",
                new String[] {"Mon", "Tue", "Thu", "Fri"}, false);  // short day names are accepted as typed
        GeneralPractitioner drMundy = GeneralPractitioner.create("100104", "Dr Orla Mundy",
                new String[] {"Tuesday", "Wednesday", "Thursday"}, true);
        Specialist drHart = Specialist.create("200201", "Dr Leona Hart",
                new String[] {"Wednesday", "Thursday"}, "Cardiology"); // referral rule left to its default
        Specialist drMarsh = Specialist.create("200202", "Dr Selene Marsh",
                new String[] {"Monday", "Wednesday"}, "Dermatology", false);
        RegisteredNurse rnRivers = RegisteredNurse.create("300301", "Stella Rivers",
                "Immunisation", true);
        RegisteredNurse rnAshworth = RegisteredNurse.create("300302", "Jude Ashworth",
                new String[] {"Wednesday", "Thursday", "Saturday"}, "Wound care", false);
        NursePractitioner npSterling = NursePractitioner.create("300303", "Priya Sterling",
                new String[] {"Monday", "Wednesday", "Friday"}, "Chronic disease",
                "diabetes and blood pressure");
        NursePractitioner npEmpey = NursePractitioner.create("300304", "Imogen Empey",
                new String[] {"Tuesday", "Thursday"}, "Women's health",
                "contraception and menopause");
        Dietitian dtLawson = Dietitian.create("400401", "Tempe Lawson",
                new String[] {"Wednesday", "Friday"}, "Diabetes nutrition");
        Dietitian dtWheeler = Dietitian.create("400402", "Rowan Wheeler",
                new String[] {"Monday", "Tuesday"}, "Sports nutrition");
        Physiotherapist ptRhodes = Physiotherapist.create("500501", "Charlie Rhodes",
                new String[] {"Monday", "Wednesday", "Friday"}, "Sports injuries");
        Physiotherapist ptHermansen = Physiotherapist.create("500502", "Ingrid Hermansen",
                new String[] {"Tuesday", "Wednesday", "Saturday"}, "Back and neck pain");

        // [Polymorphism] the list is typed as the parent, so it holds all six types
        ArrayList<HealthProfessional> professionals = new ArrayList<>();
        addProfessional(professionals, drMagee);
        addProfessional(professionals, drReyes);
        addProfessional(professionals, drCalloway);
        addProfessional(professionals, drMundy);
        addProfessional(professionals, drHart);
        addProfessional(professionals, drMarsh);
        addProfessional(professionals, rnRivers);
        addProfessional(professionals, rnAshworth);
        addProfessional(professionals, npSterling);
        addProfessional(professionals, npEmpey);
        addProfessional(professionals, dtLawson);
        addProfessional(professionals, dtWheeler);
        addProfessional(professionals, ptRhodes);
        addProfessional(professionals, ptHermansen);
        printResult("[OK] " + professionals.size() + " professionals created and stored in one list.");

        // 3.2 One loop prints every type in full
        printSection("3.2 Full details of every professional (one loop, each object prints itself)");
        System.out.println(HealthProfessional.tableHeader());
        for (HealthProfessional professional : professionals) {
            System.out.println(professional); // [Polymorphism] each object runs its own toString()
        }

        // 3.3 Same method call, a different answer from each type
        printSection("3.3 Polymorphism: the same call gives each type's own answer");
        HealthProfessional[] oneOfEach = {drMagee, drHart, drMarsh, rnRivers, npSterling,
            dtLawson, ptRhodes}; // an array typed as the parent works the same way as the list
        printAttempt("Ask each type: what do you offer, and how should a patient prepare?");
        System.out.println(Display.cell("TYPE", Display.TYPE_WIDTH) + "SERVICES / BEFORE YOUR VISIT");
        System.out.println(Display.dashes(Display.TYPE_WIDTH) + "----------------------------");
        for (HealthProfessional professional : oneOfEach) {
            System.out.println(Display.cell(professional.getProfessionType(), Display.TYPE_WIDTH)
                    + professional.describeServices());
            System.out.println(Display.cell("", Display.TYPE_WIDTH) + professional.getPreparationAdvice());
        }

        // 3.4 Interface: a capability shared across two branches
        printSection("3.4 Interface: who can prescribe? (Prescriber)");
        System.out.println(Display.cell("NAME", Display.NAME_WIDTH)
                + Display.cell("TYPE", Display.TYPE_WIDTH) + "MAY PRESCRIBE");
        System.out.println(Display.dashes(Display.NAME_WIDTH) + Display.dashes(Display.TYPE_WIDTH)
                + "-------------");
        int prescriberCount = 0;
        for (HealthProfessional professional : professionals) {
            // [Abstraction] ask "can you prescribe?", not "which class are you?"
            if (professional instanceof Prescriber) {
                // instanceof + cast, as in Activity 3.8; the newer pattern syntax is not in Modules 1-3
                Prescriber prescriber = (Prescriber) professional;
                System.out.println(Display.cell(professional.getName(), Display.NAME_WIDTH)
                        + Display.cell(professional.getProfessionType(), Display.TYPE_WIDTH)
                        + prescriber.getPrescribingScope());
                prescriberCount++;
            }
        }
        printResult("[OK] " + prescriberCount + " of " + professionals.size()
                + " can prescribe: doctors and nurse practitioners, from two branches of the hierarchy.");
        Prescriber[] prescribers = {drMagee, drHart, npSterling};  // three classes, one interface type
        printAttempt("Write a prescription through each Prescriber in an array of the interface type");
        printResult(prescribers[0].prescribe("Vincent Pope", "Atorvastatin 20 mg"));
        printResult(prescribers[1].prescribe("Tara Towers", "Bisoprolol 2.5 mg"));
        printResult(prescribers[2].prescribe("Devlin Sweet", "Metformin 500 mg"));
        printAttempt("Write a prescription without naming a medicine");
        printResult(prescribers[0].prescribe("Vincent Pope", "   "));

        // 3.5 Behaviour that only one type has
        printSection("3.5 Behaviour unique to each type");
        printAttempt("GP writes a referral");
        printResult(drMagee.writeReferral("Hugo Mann", "Cardiology"));
        printAttempt("Specialist orders a test");
        printResult(drHart.orderTest("Hugo Mann", "Echocardiogram"));
        printAttempt("Authorised registered nurse gives a flu vaccine");
        printResult(rnRivers.administerVaccine("Romy Lovell", "Influenza 2026"));
        printAttempt("Registered nurse who is NOT an authorised immuniser tries the same");
        printResult(rnAshworth.administerVaccine("Romy Lovell", "Influenza 2026"));
        printAttempt("Every kind of nurse takes a blood sample (written once, in abstract Nurse)");
        // [Inheritance] the abstract Nurse works as a type for both kinds of nurse
        Nurse[] nurses = {rnRivers, rnAshworth, npSterling, npEmpey};
        for (Nurse nurse : nurses) {
            printResult(nurse.takeBloodSample("Devlin Sweet")); // no cast: every Nurse has this
        }
        printAttempt("Dietitian creates a meal plan");
        printResult(dtLawson.createMealPlan("Devlin Sweet", "steady blood sugar"));
        printAttempt("Physiotherapist designs an exercise program");
        printResult(ptRhodes.designExerciseProgram("Theo Lovell", "knee sprain"));

        // 3.6 Identity is the staff ID
        printSection("3.6 Identity: equals() and hashCode() compare staff IDs");
        GeneralPractitioner sameIdAsMagee = GeneralPractitioner.create("100101", "Elias Magee", true);
        printAttempt("Search the list for a different object that has Dr Magee's ID 100101");
        printResult("contains = " + professionals.contains(sameIdAsMagee)
                + ", indexOf = " + professionals.indexOf(sameIdAsMagee)
                + ", same hash code = " + (sameIdAsMagee.hashCode() == drMagee.hashCode()));
        printAttempt("Compare two different professionals");
        printResult("Dr Magee equals Dr Reyes? " + Display.yesNo(drMagee.equals(drReyes)));

        // 3.7 Encapsulation: no invalid object, no outside edits
        printSection("3.7 Encapsulation: invalid details are refused, data is protected");
        String[] twoDays = {"Monday", "Friday"};
        String[] badDays = {"Monday", "Funday"};
        String[] noDays = {};

        printAttempt("Create a GP whose staff ID contains a letter: \"10A105\"");
        reportCreation(GeneralPractitioner.create("10A105", "Dr Finn Foley", twoDays, true),
                GeneralPractitioner.check("10A105", "Dr Finn Foley", twoDays));
        printAttempt("Create a GP with a 5-digit ID, a blank name and a made-up day");
        reportCreation(GeneralPractitioner.create("12345", "   ", badDays, true),
                GeneralPractitioner.check("12345", "   ", badDays));
        printAttempt("Create a specialist with an empty roster and no specialty");
        reportCreation(Specialist.create("200203", "Dr Finn Foley", noDays, ""),
                Specialist.check("200203", "Dr Finn Foley", noDays, ""));
        printAttempt("Create a nurse practitioner named \"Finn F0ley\" (a zero instead of an O)");
        reportCreation(NursePractitioner.create("300305", "Finn F0ley", twoDays, "Aged care", "pain relief"),
                NursePractitioner.check("300305", "Finn F0ley", twoDays, "Aged care", "pain relief"));

        printAttempt("Edit the array returned by getWorkingDays() from outside the object");
        String[] outsideCopy = drReyes.getWorkingDays();
        outsideCopy[0] = "Sunday"; // only the copy changes; the real roster stays the same
        printResult("The edited copy now starts with " + outsideCopy[0] + "; Dr Reyes still works "
                + drReyes.getWorkingDaysText() + ".");

        printAttempt("Change Rowan Wheeler's roster to Monday and \"Someday\"");
        reportRosterChange(dtWheeler, dtWheeler.updateWorkingDays(new String[] {"Monday", "Someday"}));
        printAttempt("Change Rowan Wheeler's roster to Monday, Tuesday and Thursday");
        reportRosterChange(dtWheeler, dtWheeler.updateWorkingDays(
                new String[] {"Monday", "Tuesday", "Thursday"}));

        return professionals;
    }

    // ===== Part 6 - Working with appointments =====

    /**
     * Part 6: one day at the clinic from start to finish. Every operation is
     * shown working and also refusing bad input, then the same code runs a
     * second clinic on a different schedule.
     *
     * @param professionals the staff created in Part 3
     */
    private static void runPartSix(ArrayList<HealthProfessional> professionals) {
        printPartTitle("PART 6 - Working with appointments (" + CLINIC_NAME + ", " + DEMO_DAY + ")");

        // 6.1 Open the clinic
        printSection("6.1 Open the clinic with the brief's schedule");
        printAttempt("Open " + CLINIC_NAME + " on " + DEMO_DAY + ": 08:00-16:00 in 30-minute slots");
        ClinicSchedule schedule = ClinicSchedule.standard();
        Clinic clinic = Clinic.create(CLINIC_NAME, DEMO_DAY, schedule);
        printResult("[OK] " + clinic.getName() + " is open on " + clinic.getToday() + ": " + schedule + ".");
        printResult("Bookable slots: " + ClinicSchedule.listTimes(schedule.getSlots()));

        // 6.2 Register staff
        printSection("6.2 Register staff (each staff ID only once)");
        printAttempt("Register the " + professionals.size() + " professionals from Part 3");
        int registered = 0;
        for (HealthProfessional professional : professionals) {
            if (clinic.registerProfessional(professional).startsWith(Clinic.OK)) {
                registered++;
            }
        }
        printResult("[OK] " + registered + " professionals registered.");
        printAttempt("Register \"Dr Finn Foley\" with staff ID 100101, which Dr Magee already holds");
        printResult(clinic.registerProfessional(GeneralPractitioner.create("100101", "Dr Finn Foley", true)));
        printAttempt("Register a GP whose ID \"10O101\" has the letter O instead of a zero");
        printResult(clinic.registerProfessional(GeneralPractitioner.create("10O101", "Dr Finn Foley", true)));
        System.out.println();
        System.out.println(clinic.viewStaffOnDuty());

        // 6.3 Bookings across every type
        printSection("6.3 Book appointments across every type of professional");
        bookChosen(clinic, "Romy Lovell", "0412 345 678", clinic.findProfessional("100101"), "09:00");
        book(clinic, "Theo Lovell", "0412345678", "500501", "10:30");       // same phone, different person
        book(clinic, "Devlin Sweet", "0445 678 901", "400401", "13:00");
        book(clinic, "Tara Towers", "0423-456-789", "200201", "08:30");     // hyphens are removed
        book(clinic, "Hugo Mann", "0434 567 890", "100104", "10:00");
        book(clinic, "Vincent Pope", "0467 890 123", "300303", "11:30");
        book(clinic, "romy lovell", "0412345678", "300301", "14:30");       // different case, same patient
        book(clinic, "Justine Fairley", "0456 789 012", "300302", "15:30"); // the last slot of the day
        book(clinic, "Tara Towers", "0423 456 789", "500502", "11:00");
        book(clinic, "Hugo Mann", "0434 567 890", "200202", "12:00");
        bookChosen(clinic, "Vincent Pope", "0467 890 123", clinic.findProfessional("100102"), "8:00");
        book(clinic, "Devlin Sweet", "0445 678 901", "100101", "15:00");

        // 6.4 Bookings that must be refused
        printSection("6.4 Bookings that must be refused");
        book(clinic, "Tara Towers", "0423 456 789", "100101", "09:00");     // Dr Magee is taken at 09:00
        book(clinic, "Romy Lovell", "0412 345 678", "100104", "09:00");     // Romy is already booked at 09:00
        book(clinic, "Hugo Mann", "0434 567 890", "100103", "09:30");       // Dr Calloway is off Wednesdays
        book(clinic, "Devlin Sweet", "0445 678 901", "100102", "09:15");
        book(clinic, "Devlin Sweet", "0445 678 901", "100102", "16:00");
        book(clinic, "Devlin Sweet", "0445 678 901", "100102", "07:30");
        book(clinic, "Devlin Sweet", "0445 678 901", "100102", "9am");
        book(clinic, "Finn F0ley", "0412 34A 678", "100102", "10:00");      // two mistakes, both reported
        book(clinic, "Devlin Sweet", "0445 678 901", "999999", "10:00");
        book(clinic, "Devlin Sweet", "0445 678 901", "10O101", "10:00");
        // a valid GP who was never registered
        bookChosen(clinic, "Devlin Sweet", "0445 678 901",
                GeneralPractitioner.create("100199", "Dr Finn Foley", true), "10:00");

        // 6.5 View and sort
        printSection("6.5 View all appointments, then sort them by time");
        System.out.println(clinic.viewAppointmentsInBookingOrder());
        System.out.println();
        System.out.println(clinic.viewAppointmentsByTime());

        // 6.6 Filter (each result sorted by time)
        printSection("6.6 Filter by staff ID or by patient mobile (each result sorted by time)");
        printAttempt("Filter by staff ID 100101 (Dr Magee)");
        System.out.println(clinic.viewAppointmentsForProfessional("100101"));
        printAttempt("Filter by mobile \"0412 345 678\" (the Lovell family's shared phone)");
        System.out.println(clinic.viewAppointmentsForPatient("0412 345 678"));
        printAttempt("Filter by staff ID 100103 (Dr Calloway, off today)");
        printResult(clinic.viewAppointmentsForProfessional("100103"));
        printAttempt("Filter by staff ID \"12345\"");
        printResult(clinic.viewAppointmentsForProfessional("12345"));
        printAttempt("Filter by staff ID 888888");
        printResult(clinic.viewAppointmentsForProfessional("888888"));
        printAttempt("Filter by mobile \"0499 000 111\"");
        printResult(clinic.viewAppointmentsForPatient("0499 000 111"));
        printAttempt("Filter by mobile \"12345\"");
        printResult(clinic.viewAppointmentsForPatient("12345"));

        // 6.7 Cancel, and reuse the freed slot
        printSection("6.7 Cancel appointments, then reuse a freed slot");
        printAttempt("Cancel by staff ID and time: 200201 at 08:30 (Tara Towers with Dr Hart)");
        printResult(clinic.cancelAppointment("200201", "08:30"));
        book(clinic, "Justine Fairley", "0456 789 012", "200201", "08:30");  // the freed slot is open again
        printAttempt("Cancel by staff ID and time: 200201 at 09:00 (nothing booked)");
        printResult(clinic.cancelAppointment("200201", "09:00"));
        printAttempt("Cancel by mobile \"0423 456 789\" (Tara Towers, one appointment left)");
        printResult(clinic.cancelAppointment("0423 456 789"));
        printAttempt("Cancel by mobile \"0412 345 678\" (the shared Lovell phone)");
        printResult(clinic.cancelAppointment("0412 345 678"));
        printAttempt("Cancel by mobile \"0499 000 111\" (no bookings)");
        printResult(clinic.cancelAppointment("0499 000 111"));
        printAttempt("Cancel by staff ID \"ABC\" at \"25:00\"");
        printResult(clinic.cancelAppointment("ABC", "25:00"));

        // 6.8 Reschedule
        printSection("6.8 Reschedule appointments");
        printAttempt("Move Hugo Mann's 10:00 with Dr Mundy (100104) to 14:00");
        printResult(clinic.rescheduleAppointment("100104", "10:00", "14:00"));
        printAttempt("Move Vincent Pope's 08:00 with Dr Reyes (100102) to 11:30");
        printResult(clinic.rescheduleAppointment("100102", "08:00", "11:30"));
        printAttempt("Move Devlin Sweet's 15:00 with Dr Magee (100101) to 09:00");
        printResult(clinic.rescheduleAppointment("100101", "15:00", "09:00"));
        printAttempt("Move Hugo Mann's 12:00 with Dr Marsh (200202) to 12:00");
        printResult(clinic.rescheduleAppointment("200202", "12:00", "12:00"));

        // 6.9 Free slots
        printSection("6.9 Find free slots");
        printAttempt("Free slots with Dr Magee (100101)");
        printResult(clinic.viewFreeSlots("100101"));
        printAttempt("Free slots with Dr Calloway (100103)");
        printResult(clinic.viewFreeSlots("100103"));

        // 6.10 Repeat prescriptions through the interface
        printSection("6.10 Repeat prescriptions (the clinic only relies on Prescriber)");
        printAttempt("Vincent Pope (registered) asks for a repeat of Atorvastatin 20 mg");
        printResult(clinic.issueRepeatPrescription("Vincent Pope", "0467 890 123", "Atorvastatin 20 mg"));
        printAttempt("Finn Foley (never registered) asks for a repeat prescription");
        printResult(clinic.issueRepeatPrescription("Finn Foley", "0400 000 000", "Codeine 30 mg"));

        // 6.11 End of the day
        printSection("6.11 The updated day, by time, and the daily summary");
        System.out.println(clinic.viewAppointmentsByTime());
        System.out.println();
        System.out.println(clinic.getDailySummary());

        // 6.12 Same system, different schedule
        runWeekendClinic(professionals);
    }

    /**
     * Shows that the schedule can change without changing any other code:
     * a Saturday clinic with a lunch break runs on exactly the same classes.
     *
     * @param professionals the staff created in Part 3
     */
    private static void runWeekendClinic(ArrayList<HealthProfessional> professionals) {
        printSection("6.12 Same system, different schedule: a Saturday clinic with a lunch break");
        printAttempt("Create a schedule that closes before it opens (16:00-08:00)");
        printResult("[WARNING] Schedule refused. " + ClinicSchedule.check("16:00", "08:00", 30));
        printAttempt("Create a schedule whose lunch break is outside opening hours");
        printResult("[WARNING] Schedule refused. "
                + ClinicSchedule.check("08:00", "16:00", 30, "17:00", "18:00"));

        printAttempt("Open a Saturday clinic, 08:00-16:00 in 30-minute slots, lunch 12:00-13:00");
        ClinicSchedule lunchSchedule = ClinicSchedule.create("08:00", "16:00", 30, "12:00", "13:00");
        Clinic weekend = Clinic.create("Arcana Weekend Clinic", "Saturday", lunchSchedule);
        printResult("[OK] " + weekend.getName() + " is open on " + weekend.getToday() + ": "
                + lunchSchedule + ".");
        printResult("Bookable slots: " + ClinicSchedule.listTimes(lunchSchedule.getSlots()));
        printAttempt("Register the same " + professionals.size() + " professionals at the weekend clinic");
        int registered = 0;
        for (HealthProfessional professional : professionals) {
            if (weekend.registerProfessional(professional).startsWith(Clinic.OK)) {
                registered++; // the same staff objects work for a second clinic
            }
        }
        printResult("[OK] " + registered + " registered; on duty Saturday: "
                + weekend.getStaffOnDuty().size() + ".");
        book(weekend, "Justine Fairley", "0456 789 012", "300302", "12:00"); // inside the lunch break
        book(weekend, "Justine Fairley", "0456 789 012", "300302", "13:00");
        book(weekend, "Romy Lovell", "0412 345 678", "100101", "09:00");    // Dr Magee is off Saturdays
        book(weekend, "Romy Lovell", "0412 345 678", "500502", "09:00");
        System.out.println();
        System.out.println(weekend.viewAppointmentsByTime());
    }

    // ===== Helpers for the demo =====

    /**
     * Adds a professional to a list, but skips a null one.
     * A null means create(...) refused the details, so there is no object to add.
     *
     * @param list         the list to add to
     * @param professional the professional, maybe null
     */
    private static void addProfessional(ArrayList<HealthProfessional> list,
                                        HealthProfessional professional) {
        if (professional == null) {
            printResult("[WARNING] Skipped: invalid details, so no professional was created.");
            return; // one null in the list would crash every loop after it
        }
        list.add(professional);
    }

    /**
     * Books by staff ID, the way a receptionist would type it.
     * The request is printed before the clinic's answer.
     *
     * @param clinic         the clinic to book at
     * @param patientName    the patient's name
     * @param mobile         the patient's mobile
     * @param professionalId the staff ID
     * @param time           the time asked for
     */
    private static void book(Clinic clinic, String patientName, String mobile, String professionalId,
                             String time) {
        printAttempt("Book " + patientName + " (" + mobile + ") with staff " + professionalId
                + " at " + time);
        printResult(clinic.bookAppointment(patientName, mobile, professionalId, time));
    }

    /**
     * Books with a professional the patient picked, like on a booking website.
     * The request is printed before the clinic's answer.
     *
     * @param clinic       the clinic to book at
     * @param patientName  the patient's name
     * @param mobile       the patient's mobile
     * @param professional the chosen professional, of any type
     * @param time         the time asked for
     */
    private static void bookChosen(Clinic clinic, String patientName, String mobile,
                                   HealthProfessional professional, String time) {
        printAttempt("Book " + patientName + " (" + mobile + ") with " + professional.getName()
                + " (chosen online) at " + time);
        // [Polymorphism] the parameter is the parent type, so any kind fits
        printResult(clinic.bookAppointment(patientName, mobile, professional, time));
    }

    /**
     * Prints what happened to a create(...) call.
     *
     * @param created  what create(...) gave back, or null if it refused
     * @param problems the matching check(...) result, which says why
     */
    private static void reportCreation(HealthProfessional created, String problems) {
        if (created == null) {
            printResult("[WARNING] Not created. " + problems);
        } else {
            printResult("[OK] Created " + created.getSummary() + ".");
        }
    }

    /**
     * Prints what happened to a roster change.
     *
     * @param professional the professional whose roster was changed
     * @param problem      what updateWorkingDays(...) gave back: "" means it worked
     */
    private static void reportRosterChange(HealthProfessional professional, String problem) {
        if (problem.isEmpty()) {
            printResult("[OK] Roster updated: " + professional.getWorkingDaysText() + ".");
        } else {
            printResult("[WARNING] Roster unchanged (" + professional.getWorkingDaysText() + "). "
                    + problem);
        }
    }

    // ===== Output helpers: one place decides how every line looks =====

    /**
     * Prints the system banner once at the start.
     */
    private static void printBanner() {
        System.out.println(DOUBLE_RULE);
        System.out.println("  " + CLINIC_NAME.toUpperCase() + "  -  Appointment System");
        System.out.println("  PROG2004 Assessment 1  |  Zefei Niu (zniu16)");
        System.out.println(DOUBLE_RULE);
    }

    /**
     * Prints a part title between two heavy lines.
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
     * Prints what is about to be tried, before it is tried.
     *
     * @param description the action and its input
     */
    private static void printAttempt(String description) {
        System.out.println("> " + description);
    }

    /**
     * Prints the system's answer, indented under the attempt.
     *
     * @param message the result or warning
     */
    private static void printResult(String message) {
        System.out.println("  " + message);
    }
}
