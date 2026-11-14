/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Reads a patient's booking, cancellation and time-change choices for one day.
 * Clinic keeps the records and checks the rules; this class only reads
 * choices and shows the result. Changing patient keeps the same clinic.
 *
 * @author Zefei Niu
 */
public class PatientConsole {

    /** The same clinic is used until the whole booking session ends. */
    private final Clinic clinic;

    /** The reader opened by main; this class does not close it. */
    private final Scanner input;

    /** Stops the loops if input ends partway through an operation. */
    private boolean inputEnded;

    /**
     * Connects the booking desk to the open clinic and keyboard reader.
     *
     * @param clinic the day's clinic, with its staff already registered
     * @param input the shared keyboard reader
     */
    public PatientConsole(Clinic clinic, Scanner input) {
        this.clinic = clinic;
        this.input = input;
    }

    /**
     * Lets patients take turns booking into the same day.
     */
    public void run() {
        System.out.println("\n" + clinic.getName() + " | " + clinic.getToday());
        System.out.println(clinic.getSchedule());
        System.out.println("One mobile belongs to one patient in this model.");
        System.out.println("Records stay for this session and clear when the program closes.");
        while (!inputEnded) {
            String mobile = readMobile();
            if (mobile == null) {
                break;
            }
            String name = readName(mobile);
            if (name != null) {
                servePatient(name, mobile);
            }
        }
        System.out.println("Booking session closed.");
    }

    /**
     * Reads and checks the mobile before any booking is attempted.
     *
     * @return the normalised mobile, or null to finish the session
     */
    private String readMobile() {
        while (!inputEnded) {
            String mobile = read("\nPatient mobile (04xxxxxxxx, 0 to end session): ");
            if (back(mobile)) {
                return null;
            }
            String problem = Patient.checkMobile(mobile);
            if (problem.isEmpty()) {
                return Patient.normaliseMobile(mobile);
            }
            System.out.println(Clinic.WARNING + problem);
        }
        return null;
    }

    /**
     * Reads a name and checks an existing mobile's record if there is one.
     * This selects a record in a local simulation, rather than logging in.
     *
     * @param mobile the checked mobile
     * @return the patient's name, or null to choose a different mobile
     */
    private String readName(String mobile) {
        while (!inputEnded) {
            String name = read("Patient name (0 to go back): ");
            if (back(name)) {
                return null;
            }
            String problem = Patient.checkName(name);
            if (!problem.isEmpty()) {
                System.out.println(Clinic.WARNING + problem);
                continue;
            }
            Patient existing = clinic.findPatientByMobile(mobile);
            if (existing != null && !existing.matches(name, mobile)) {
                System.out.println("[WARNING] This mobile already has a record under another name.");
                System.out.println("Use the recorded name, or enter 0 to choose a different mobile.");
                continue;
            }
            if (existing != null) {
                return existing.getName();
            }
            System.out.println("Your patient record is created when a booking succeeds.");
            return Validator.tidy(name);
        }
        return null;
    }

    /**
     * Keeps one patient's choices together until they leave the desk.
     *
     * @param name the checked name
     * @param mobile the checked mobile
     */
    private void servePatient(String name, String mobile) {
        while (!inputEnded) {
            System.out.println("\n" + name + " | " + Patient.formatMobile(mobile));
            System.out.println("1 Book  2 My appointments  3 Doctors  4 Cancel  5 Change time  0 Change patient");
            String choice = read("Choose: ");
            if (back(choice)) {
                return;
            }
            switch (choice) {
                case "1":
                    book(name, mobile);
                    break;
                case "2":
                    showAppointments(mobile);
                    break;
                case "3":
                    showDoctors();
                    break;
                case "4":
                    cancel(mobile);
                    break;
                case "5":
                    move(mobile);
                    break;
                default:
                    System.out.println("[WARNING] Choose one of the numbers shown.");
            }
        }
    }

    /**
     * Shows today's staff together so the patient can compare them.
     */
    private void showDoctors() {
        ArrayList<HealthProfessional> onDuty = clinic.getStaffOnDuty();
        System.out.println("\nProfessionals working " + clinic.getToday());
        System.out.println(Display.cell("ID", 6) + Display.cell("NAME", 22)
                + Display.cell("TYPE", 22) + "FREE SLOTS");
        for (HealthProfessional professional : onDuty) {
            System.out.println(Display.cell(professional.getId(), 6)
                    + Display.cell(professional.getName(), 22)
                    + Display.cell(professional.getProfessionType(), 22)
                    + clinic.getFreeSlots(professional).size());
        }
        if (onDuty.isEmpty()) {
            System.out.println("[WARNING] No staff work today.");
        }
    }

    /**
     * Reads the professional and time, then asks before making the booking.
     *
     * @param name the current patient's name
     * @param mobile the current patient's mobile
     */
    private void book(String name, String mobile) {
        showDoctors();
        String id = read("Professional ID (0 to go back): ");
        if (back(id)) {
            return;
        }
        HealthProfessional professional = clinic.findProfessional(id);
        if (professional == null || !professional.worksOn(clinic.getToday())) {
            System.out.println("[WARNING] Choose an ID from today's staff table.");
            return;
        }
        ArrayList<TimeSlot> free = clinic.getFreeSlots(professional);
        if (free.isEmpty()) {
            System.out.println("[WARNING] This professional is fully booked today.");
            return;
        }
        System.out.println(HealthProfessional.tableHeader());
        System.out.println(professional); // [Polymorphism] the selected type prints its own details
        System.out.println("Services: " + professional.describeServices());
        showFreeSlots(free);
        String time = read("Start time (HH:mm, 0 to go back): ");
        if (back(time)) {
            return;
        }
        String problem = clinic.getSchedule().checkBookable(time);
        if (!problem.isEmpty()) {
            System.out.println(Clinic.WARNING + problem);
            return;
        }
        if (confirm("Book " + name + " with " + professional.getName() + " at " + time + "?")) {
            String result = clinic.bookAppointment(name, mobile, professional, time);
            System.out.println(result);
            if (result.startsWith(Clinic.OK)) {
                showAppointments(mobile);
            }
        }
    }

    /**
     * Shows only the current patient's appointments, from earliest to latest.
     *
     * @param mobile the current patient's mobile
     * @return the sorted copies used for the numbered selection
     */
    private ArrayList<Appointment> showAppointments(String mobile) {
        ArrayList<Appointment> list = Clinic.sortByTime(clinic.filterByPatientMobile(mobile));
        if (list.isEmpty()) {
            System.out.println("[WARNING] You have no appointments today.");
            return list;
        }
        System.out.println("Your appointments, by time:");
        for (int i = 0; i < list.size(); i++) {
            Appointment appointment = list.get(i);
            System.out.println((i + 1) + ". " + appointment.getTimeSlot() + " | "
                    + appointment.getProfessional().getName() + " | "
                    + appointment.getProfessional().getProfessionType());
        }
        return list;
    }

    /**
     * Selects from this patient's list, not the clinic's full booking list.
     *
     * @param mobile the current patient's mobile
     * @return the chosen appointment, or null to go back
     */
    private Appointment chooseAppointment(String mobile) {
        ArrayList<Appointment> list = showAppointments(mobile);
        if (list.isEmpty()) {
            return null;
        }
        while (!inputEnded) {
            String choice = read("Appointment number (0 to go back): ");
            if (back(choice)) {
                return null;
            }
            for (int i = 0; i < list.size(); i++) {
                if (String.valueOf(i + 1).equals(choice)) {
                    return list.get(i); // only a displayed number can select a booking
                }
            }
            System.out.println("[WARNING] Choose a number from your appointment list.");
        }
        return null;
    }

    /**
     * Confirms the patient's selection before removing it.
     *
     * @param mobile the current patient's mobile
     */
    private void cancel(String mobile) {
        Appointment appointment = chooseAppointment(mobile);
        if (appointment != null && confirm("Cancel " + appointment.getSummary() + "?")) {
            System.out.println(clinic.cancelAppointment(mobile,
                    appointment.getProfessional().getId(), appointment.getTimeSlot().toString()));
            showAppointments(mobile);
        }
    }

    /**
     * Reads a new time without removing the old appointment first.
     *
     * @param mobile the current patient's mobile
     */
    private void move(String mobile) {
        Appointment appointment = chooseAppointment(mobile);
        if (appointment == null) {
            return;
        }
        ArrayList<TimeSlot> free = clinic.getFreeSlots(appointment.getProfessional());
        if (free.isEmpty()) {
            System.out.println("[WARNING] No alternative slots are free with this professional today.");
            return;
        }
        System.out.println("Current booking: " + appointment.getSummary());
        showFreeSlots(free);
        String time = read("New start time (HH:mm, 0 to go back): ");
        if (back(time)) {
            return;
        }
        String problem = clinic.getSchedule().checkBookable(time);
        if (!problem.isEmpty()) {
            System.out.println(Clinic.WARNING + problem);
            return;
        }
        if (confirm("Move " + appointment.getSummary() + " to " + time + "?")) {
            System.out.println(clinic.rescheduleAppointment(mobile,
                    appointment.getProfessional().getId(), appointment.getTimeSlot().toString(), time));
            showAppointments(mobile);
        }
    }

    /**
     * Keeps the free-time display the same for booking and changing time.
     *
     * @param free the available start times, in order
     */
    private void showFreeSlots(ArrayList<TimeSlot> free) {
        System.out.println("Free start times:");
        for (int i = 0; i < free.size(); i++) {
            System.out.print(free.get(i) + "  ");
            if ((i + 1) % 8 == 0 || i == free.size() - 1) {
                System.out.println();
            }
        }
    }

    /**
     * Prints a prompt before reading and stops cleanly at end of input.
     *
     * @param prompt the text shown before input
     * @return the trimmed input, or null if input has ended
     */
    private String read(String prompt) {
        System.out.print(prompt);
        if (!input.hasNextLine()) {
            inputEnded = true;
            return null;
        }
        return input.nextLine().trim();
    }

    /**
     * Checks for the shared back choice before using an input value.
     *
     * @param value the input, possibly null
     * @return true for 0 or end of input
     */
    private boolean back(String value) {
        return value == null || "0".equals(value);
    }

    /**
     * Requires a clear yes before changing the appointment collection.
     *
     * @param question the proposed change
     * @return true only when the patient enters y
     */
    private boolean confirm(String question) {
        while (!inputEnded) {
            String answer = read(question + " (y/n): ");
            if (back(answer) || "n".equalsIgnoreCase(answer)) {
                System.out.println("Unchanged.");
                return false;
            }
            if ("y".equalsIgnoreCase(answer)) {
                return true;
            }
            System.out.println("[WARNING] Enter y or n.");
        }
        return false;
    }
}
