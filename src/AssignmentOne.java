import java.util.ArrayList;
import java.util.Scanner;

/**
 * Arcana Health Centre - Appointment System.
 *
 * Runs patient input or the scripted examples.
 * The character names follow the Major Arcana theme explained in README.
 * Each patient uses their own mobile number in this model.
 * A mobile cannot be registered to another name in the same clinic session.
 *
 * @author Zefei Niu
 */
public class AssignmentOne {
    /* Clinic name used in the demonstration. */
    private static final String CLINIC_NAME = "Arcana Health Centre";

    /* Fixed weekday used by the demonstration. */
    private static final String DEMO_DAY = "Wednesday";

    /* Heavy line around part titles. */
    private static final String DOUBLE_RULE =
            "==============================================================================";

    /* Light line under section titles. */
    private static final String SINGLE_RULE =
            "------------------------------------------------------------------------------";

    /**
     * No instances are needed for these static demonstration methods.
     */
    private AssignmentOne() {
    }

    /** Chooses how to run the same appointment model. */
    public static void main(String[] args) {
        printBanner();
        if (args.length > 0 && "--demo".equals(args[0])) {
            runPartSix(runPartThree(true));
            return;
        }
        Scanner input = new Scanner(System.in);
        String mode = readMode(input);
        if ("1".equals(mode)) {
            String day = readClinicDay(input);
            if (day != null) {
                Clinic clinic = Clinic.create(CLINIC_NAME, day, ClinicSchedule.standard());
                for (HealthProfessional professional : runPartThree(false)) {
                    clinic.registerProfessional(professional);
                }
                PatientConsole console = new PatientConsole(clinic, input);
                console.run();
            }
        } else if ("2".equals(mode)) {
            runPartSix(runPartThree(true));
        }
        input.close();
    }

    /** Reads the launch choice without parsing an unchecked number. */
    private static String readMode(Scanner input) {
        while (true) {
            System.out.println("1 Patient booking");
            System.out.println("2 Code demonstration");
            System.out.println("0 Exit");
            System.out.print("Choose: ");
            if (!input.hasNextLine()) {
                return "0";
            }
            String choice = input.nextLine().trim();
            if ("1".equals(choice) || "2".equals(choice) || "0".equals(choice)) {
                return choice;
            }
            System.out.println("[WARNING] Choose 1, 2 or 0.");
        }
    }

    /**
     * Sets today's weekday for this single-day session.
     *
     * @return the full weekday name, or null when 0 is entered or input ends
     */
    private static String readClinicDay(Scanner input) {
        while (true) {
            System.out.print("Today's weekday (Mon-Sun, 0 to exit): ");
            if (!input.hasNextLine()) {
                return null;
            }
            String day = input.nextLine().trim();
            if ("0".equals(day)) {
                return null;
            }
            if (Week.isValidDay(day)) {
                return Week.normalise(day);
            }
            System.out.println("[WARNING] Enter a weekday such as Saturday or Sat.");
        }
    }

    /**
     * Part 3: creates a realistic mix of professionals and shows that they can be stored,
     * printed and used together through their parent type.
     */
    private static ArrayList<HealthProfessional> runPartThree(boolean showDemo) {
        if (showDemo) {
            printPartTitle("PART 3 - Working with health professional objects");
            printSection("3.1 Create the clinic's health professionals");
            printAttempt("Create 14 professionals of 6 types");
        }

        GeneralPractitioner drMagee = GeneralPractitioner.create("100101", "Dr Elias Magee",
                true);
        GeneralPractitioner drReyes = GeneralPractitioner.create("100102", "Dr Solana Reyes",
                new String[] {"Monday", "Wednesday", "Friday"}, true);
        GeneralPractitioner drCalloway = GeneralPractitioner.create("100103", "Dr Rex Calloway",
                new String[] {"Mon", "Tue", "Thu", "Fri"}, false);
        GeneralPractitioner drMundy = GeneralPractitioner.create("100104", "Dr Orla Mundy",
                new String[] {"Tuesday", "Wednesday", "Thursday"}, true);
        Specialist drHart = Specialist.create("200201", "Dr Leona Hart",
                new String[] {"Wednesday", "Thursday"}, "Cardiology");
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
        if (!showDemo) {
            return professionals;
        }
        printResult("[OK] " + professionals.size() + " professionals created and stored in one list.");

        printSection("3.2 Full details of every professional (one loop, each object prints itself)");
        System.out.println(HealthProfessional.tableHeader());
        for (HealthProfessional professional : professionals) {
            System.out.println(professional);
        }

        printSection("3.3 Polymorphism: the same call gives each type's own answer");
        HealthProfessional[] oneOfEach = {drMagee, drHart, drMarsh, rnRivers, npSterling,
            dtLawson, ptRhodes};
        printAttempt("Ask each type: what do you offer, and how should a patient prepare?");
        System.out.println(Display.cell("TYPE", Display.TYPE_WIDTH) + "SERVICES / BEFORE YOUR VISIT");
        System.out.println(Display.dashes(Display.TYPE_WIDTH) + "----------------------------");
        for (HealthProfessional professional : oneOfEach) {
            System.out.println(Display.cell(professional.getProfessionType(), Display.TYPE_WIDTH)
                    + professional.describeServices());
            System.out.println(Display.cell("", Display.TYPE_WIDTH) + professional.getPreparationAdvice());
        }

        printSection("3.4 Interface: who can prescribe? (Prescriber)");
        System.out.println(Display.cell("NAME", Display.NAME_WIDTH)
                + Display.cell("TYPE", Display.TYPE_WIDTH) + "MAY PRESCRIBE");
        System.out.println(Display.dashes(Display.NAME_WIDTH) + Display.dashes(Display.TYPE_WIDTH)
                + "-------------");
        int prescriberCount = 0;
        for (HealthProfessional professional : professionals) {
            if (professional instanceof Prescriber) {
                Prescriber prescriber = (Prescriber) professional;
                System.out.println(Display.cell(professional.getName(), Display.NAME_WIDTH)
                        + Display.cell(professional.getProfessionType(), Display.TYPE_WIDTH)
                        + prescriber.getPrescribingScope());
                prescriberCount++;
            }
        }
        printResult("[OK] " + prescriberCount + " of " + professionals.size()
                + " can prescribe: doctors and nurse practitioners, from two branches of the hierarchy.");
        Prescriber[] prescribers = {drMagee, drHart, npSterling};
        printAttempt("Write a prescription through each Prescriber in an array of the interface type");
        printResult(prescribers[0].prescribe("Vincent Pope", "Atorvastatin 20 mg"));
        printResult(prescribers[1].prescribe("Tara Towers", "Bisoprolol 2.5 mg"));
        printResult(prescribers[2].prescribe("Devlin Sweet", "Metformin 500 mg"));
        printAttempt("Write a prescription without naming a medicine");
        printResult(prescribers[0].prescribe("Vincent Pope", "   "));

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

        Nurse[] nurses = {rnRivers, rnAshworth, npSterling, npEmpey};
        for (Nurse nurse : nurses) {
            printResult(nurse.takeBloodSample("Devlin Sweet"));
        }
        printAttempt("Dietitian creates a meal plan");
        printResult(dtLawson.createMealPlan("Devlin Sweet", "steady blood sugar"));
        printAttempt("Physiotherapist designs an exercise program");
        printResult(ptRhodes.designExerciseProgram("Theo Lovell", "knee sprain"));

        printSection("3.6 Identity: equals() and hashCode() compare staff IDs");
        GeneralPractitioner sameIdAsMagee = GeneralPractitioner.create("100101", "Elias Magee", true);
        printAttempt("Search the list for a different object that has Dr Magee's ID 100101");
        printResult("contains = " + professionals.contains(sameIdAsMagee)
                + ", indexOf = " + professionals.indexOf(sameIdAsMagee)
                + ", same hash code = " + (sameIdAsMagee.hashCode() == drMagee.hashCode()));
        printAttempt("Compare two different professionals");
        printResult("Dr Magee equals Dr Reyes? " + Display.yesNo(drMagee.equals(drReyes)));

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
        outsideCopy[0] = "Sunday"; // The clinic still uses the original roster.
        printResult("The edited copy now starts with " + outsideCopy[0] + "; Dr Reyes still works "
                + drReyes.getWorkingDaysText() + ".");

        printAttempt("Change Rowan Wheeler's roster to Monday and \"Someday\"");
        reportRosterChange(dtWheeler, dtWheeler.updateWorkingDays(new String[] {"Monday", "Someday"}));
        printAttempt("Change Rowan Wheeler's roster to Monday, Tuesday and Thursday");
        reportRosterChange(dtWheeler, dtWheeler.updateWorkingDays(
                new String[] {"Monday", "Tuesday", "Thursday"}));

        return professionals;
    }

    /** Runs booking, filtering, sorting, cancellation and rescheduling examples. */
    private static void runPartSix(ArrayList<HealthProfessional> professionals) {
        printPartTitle("PART 6 - Working with appointments (" + CLINIC_NAME + ", " + DEMO_DAY + ")");

        printSection("6.1 Open the clinic with the brief's schedule");
        printAttempt("Open " + CLINIC_NAME + " on " + DEMO_DAY + ": 08:00-16:00 in 30-minute slots");
        ClinicSchedule schedule = ClinicSchedule.standard();
        Clinic clinic = Clinic.create(CLINIC_NAME, DEMO_DAY, schedule);
        printResult("[OK] " + clinic.getName() + " is open on " + clinic.getToday() + ": " + schedule + ".");
        printResult("Bookable slots: " + ClinicSchedule.listTimes(schedule.getSlots()));

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

        printSection("6.3 Book appointments across every type of professional");
        bookChosen(clinic, "Romy Lovell", "0412 345 678", clinic.findProfessional("100101"), "09:00");
        book(clinic, "Theo Lovell", "0488 123 456", "500501", "10:30");
        book(clinic, "Devlin Sweet", "0445 678 901", "400401", "13:00");
        book(clinic, "Tara Towers", "0423-456-789", "200201", "08:30");
        book(clinic, "Hugo Mann", "0434 567 890", "100104", "10:00");
        book(clinic, "Vincent Pope", "0467 890 123", "300303", "11:30");
        book(clinic, "romy lovell", "0412345678", "300301", "14:30");
        book(clinic, "Justine Fairley", "0456 789 012", "300302", "15:30");
        book(clinic, "Tara Towers", "0423 456 789", "500502", "11:00");
        book(clinic, "Hugo Mann", "0434 567 890", "200202", "12:00");
        bookChosen(clinic, "Vincent Pope", "0467 890 123", clinic.findProfessional("100102"), "8:00");
        book(clinic, "Devlin Sweet", "0445 678 901", "100101", "15:00");

        printSection("6.4 Bookings that must be refused");
        book(clinic, "Theo Lovell", "0412 345 678", "100102", "10:30"); // This mobile belongs to Romy.
        book(clinic, "Tara Towers", "0423 456 789", "100101", "09:00");
        book(clinic, "Romy Lovell", "0412 345 678", "100104", "09:00");
        book(clinic, "Hugo Mann", "0434 567 890", "100103", "09:30");
        book(clinic, "Devlin Sweet", "0445 678 901", "100102", "09:15");
        book(clinic, "Devlin Sweet", "0445 678 901", "100102", "16:00");
        book(clinic, "Devlin Sweet", "0445 678 901", "100102", "07:30");
        book(clinic, "Devlin Sweet", "0445 678 901", "100102", "9am");
        book(clinic, "Finn F0ley", "0412 34A 678", "100102", "10:00");
        book(clinic, "Devlin Sweet", "0445 678 901", "999999", "10:00");
        book(clinic, "Devlin Sweet", "0445 678 901", "10O101", "10:00");

        bookChosen(clinic, "Devlin Sweet", "0445 678 901",
                GeneralPractitioner.create("100199", "Dr Finn Foley", true), "10:00");

        printSection("6.5 View all appointments, then sort them by time");
        System.out.println(clinic.viewAppointmentsInBookingOrder());
        System.out.println();
        System.out.println(clinic.viewAppointmentsByTime());

        printSection("6.6 Filter by staff ID or by patient mobile (each result sorted by time)");
        printAttempt("Filter by staff ID 100101 (Dr Magee)");
        System.out.println(clinic.viewAppointmentsForProfessional("100101"));
        printAttempt("Filter by mobile \"0412 345 678\" (Romy's two appointments)");
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

        printSection("6.7 Cancel appointments, then reuse a freed slot");
        printAttempt("Cancel by staff ID and time: 200201 at 08:30 (Tara Towers with Dr Hart)");
        printResult(clinic.cancelAppointment("200201", "08:30"));
        book(clinic, "Justine Fairley", "0456 789 012", "200201", "08:30");
        printAttempt("Cancel by staff ID and time: 200201 at 09:00 (nothing booked)");
        printResult(clinic.cancelAppointment("200201", "09:00"));
        printAttempt("Cancel by mobile \"0423 456 789\" (Tara Towers, one appointment left)");
        printResult(clinic.cancelAppointment("0423 456 789"));
        printAttempt("Cancel by mobile \"0412 345 678\" (Romy has two appointments; select one explicitly)");
        printResult(clinic.cancelAppointment("0412 345 678"));
        printAttempt("Cancel by mobile \"0499 000 111\" (no bookings)");
        printResult(clinic.cancelAppointment("0499 000 111"));
        printAttempt("Cancel by staff ID \"ABC\" at \"25:00\"");
        printResult(clinic.cancelAppointment("ABC", "25:00"));

        printSection("6.8 Reschedule appointments");
        printAttempt("Move Hugo Mann's 10:00 with Dr Mundy (100104) to 14:00");
        printResult(clinic.rescheduleAppointment("100104", "10:00", "14:00"));
        printAttempt("Move Vincent Pope's 08:00 with Dr Reyes (100102) to 11:30");
        printResult(clinic.rescheduleAppointment("100102", "08:00", "11:30"));
        printAttempt("Move Devlin Sweet's 15:00 with Dr Magee (100101) to 09:00");
        printResult(clinic.rescheduleAppointment("100101", "15:00", "09:00"));
        printAttempt("Move Hugo Mann's 12:00 with Dr Marsh (200202) to 12:00");
        printResult(clinic.rescheduleAppointment("200202", "12:00", "12:00"));

        printSection("6.9 Find free slots");
        printAttempt("Free slots with Dr Magee (100101)");
        printResult(clinic.viewFreeSlots("100101"));
        printAttempt("Free slots with Dr Calloway (100103)");
        printResult(clinic.viewFreeSlots("100103"));

        printSection("6.10 Repeat prescriptions (the clinic only relies on Prescriber)");
        printAttempt("Vincent Pope (registered) asks for a repeat of Atorvastatin 20 mg");
        printResult(clinic.issueRepeatPrescription("Vincent Pope", "0467 890 123", "Atorvastatin 20 mg"));
        printAttempt("Finn Foley (never registered) asks for a repeat prescription");
        printResult(clinic.issueRepeatPrescription("Finn Foley", "0400 000 000", "Codeine 30 mg"));

        printSection("6.11 The updated day, by time, and the daily summary");
        System.out.println(clinic.viewAppointmentsByTime());
        System.out.println();
        System.out.println(clinic.getDailySummary());

        runWeekendClinic(professionals);
    }

    /**
     * Shows that the schedule can change without changing any other code: a Saturday clinic
     * with a lunch break runs on exactly the same classes.
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
                registered++;
            }
        }
        printResult("[OK] " + registered + " registered; on duty Saturday: "
                + weekend.getStaffOnDuty().size() + ".");
        book(weekend, "Justine Fairley", "0456 789 012", "300302", "12:00");
        book(weekend, "Justine Fairley", "0456 789 012", "300302", "13:00");
        book(weekend, "Romy Lovell", "0412 345 678", "100101", "09:00");
        book(weekend, "Romy Lovell", "0412 345 678", "500502", "09:00");
        System.out.println();
        System.out.println(weekend.viewAppointmentsByTime());
    }

    /**
     * Adds a professional to a list, but skips a null one. A null means create(...) refused
     * the details, so there is no object to add.
     */
    private static void addProfessional(ArrayList<HealthProfessional> list,
                                        HealthProfessional professional) {
        if (professional == null) {
            printResult("[WARNING] Skipped: invalid details, so no professional was created.");
            return;
        }
        list.add(professional);
    }

    /**
     * Books by staff ID, the way a receptionist would type it. The request is printed before
     * the clinic's answer.
     */
    private static void book(Clinic clinic, String patientName, String mobile, String professionalId,
                             String time) {
        printAttempt("Book " + patientName + " (" + mobile + ") with staff " + professionalId
                + " at " + time);
        printResult(clinic.bookAppointment(patientName, mobile, professionalId, time));
    }

    /**
     * Books with a professional the patient picked, like on a booking website. The request is
     * printed before the clinic's answer.
     */
    private static void bookChosen(Clinic clinic, String patientName, String mobile,
                                   HealthProfessional professional, String time) {
        printAttempt("Book " + patientName + " (" + mobile + ") with " + professional.getName()
                + " (chosen online) at " + time);

        printResult(clinic.bookAppointment(patientName, mobile, professional, time));
    }

    /** Prints what happened to a create(...) call. */
    private static void reportCreation(HealthProfessional created, String problems) {
        if (created == null) {
            printResult("[WARNING] Not created. " + problems);
        } else {
            printResult("[OK] Created " + created.getSummary() + ".");
        }
    }

    /** Prints what happened to a roster change. */
    private static void reportRosterChange(HealthProfessional professional, String problem) {
        if (problem.isEmpty()) {
            printResult("[OK] Roster updated: " + professional.getWorkingDaysText() + ".");
        } else {
            printResult("[WARNING] Roster unchanged (" + professional.getWorkingDaysText() + "). "
                    + problem);
        }
    }

    /** Prints the system banner once at the start. */
    private static void printBanner() {
        System.out.println(DOUBLE_RULE);
        System.out.println("  " + CLINIC_NAME.toUpperCase() + "  -  Appointment System");
        System.out.println("  PROG2004 Assessment 1  |  Zefei Niu (zniu16)");
        System.out.println(DOUBLE_RULE);
    }

    /** Prints a part title between two heavy lines. */
    private static void printPartTitle(String title) {
        System.out.println();
        System.out.println(DOUBLE_RULE);
        System.out.println("  " + title);
        System.out.println(DOUBLE_RULE);
    }

    /** Prints a section title with a light line under it. */
    private static void printSection(String title) {
        System.out.println();
        System.out.println(title);
        System.out.println(SINGLE_RULE);
    }

    /** Prints what is about to be tried, before it is tried. */
    private static void printAttempt(String description) {
        System.out.println("> " + description);
    }

    /** Prints the system's answer, indented under the attempt. */
    private static void printResult(String message) {
        System.out.println("  " + message);
    }
}
