/*
 * =============================================================================
 *  System  : Arcana Health Centre - Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : Clinic.java
 *  Purpose : Manages one clinic's staff, patients and appointments for today.
 * =============================================================================
 */

import java.util.ArrayList;
import java.util.Collections;

/**
 * Manages one clinic's staff, patients and appointments for the current day.
 * <p>
 * This is the only place that sees every appointment at once, so it owns
 * every rule that depends on the whole day: a professional cannot be
 * double-booked, a patient cannot be in two places at once, only staff who
 * work today can be booked, and only bookable slots can be used.
 * </p>
 * <p>
 * Following the real process at a front desk, the clinic first registers its
 * staff (each staff ID only once). A patient is registered automatically on
 * their first booking and given a patient number; later bookings with the
 * same name and mobile number are recognised as the same patient.
 * </p>
 * <p>
 * Every operation returns a message instead of printing it, starting with
 * "[OK]" or "[WARNING]". A warning always says why the action could not be
 * performed, and the clinic's data is never changed by a refused action.
 * The day is passed in when the clinic is created, so the same system can be
 * run for any day (and the demonstration gives the same result every time).
 * </p>
 * <p>
 * Appointments are stored in an ArrayList: the number of bookings grows and
 * shrinks during the day, the list keeps the booking order, and it can be
 * filtered with a loop and sorted with Collections.sort, which uses the
 * natural order defined by Appointment.compareTo.
 * </p>
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Clinic {

    /** Start of every successful result. */
    public static final String OK = "[OK] ";

    /** Start of every refusal. */
    public static final String WARNING = "[WARNING] ";

    /** Name of the clinic, e.g. "Arcana Health Centre". */
    private final String name;

    /** The day these bookings are for, e.g. "Wednesday". */
    private final String today;

    /** The day's bookable slots. */
    private final ClinicSchedule schedule;

    /** Registered staff; any type of professional. */
    private final ArrayList<HealthProfessional> staff = new ArrayList<>();   // [Polymorphism] one list for every type of professional

    /** Registered patients, in registration order. */
    private final ArrayList<Patient> patients = new ArrayList<>();

    /** Today's appointments, in booking order. */
    private final ArrayList<Appointment> appointments = new ArrayList<>(); // [Encapsulation] private: changed only through the rules below

    /** The number the next new patient will receive. */
    private int nextPatientNumber = 1;

    /**
     * Builds a clinic from checked details. Private: use create(...).
     *
     * @param name     the clinic's name, already validated
     * @param today    the day of the bookings, already validated
     * @param schedule the day's schedule, not null
     */
    private Clinic(String name, String today, ClinicSchedule schedule) {
        this.name = Validator.tidy(name);
        this.today = Week.normalise(today);                         // "wed" and "Wednesday" must mean the same day everywhere
        this.schedule = schedule;
    }

    /**
     * Explains what, if anything, is wrong with a clinic's details.
     *
     * @param name     the clinic's name
     * @param today    the day of the bookings
     * @param schedule the day's schedule
     * @return an empty text if valid, otherwise every problem found
     */
    public static String check(String name, String today, ClinicSchedule schedule) {
        String problems = Validator.checkRequiredText("Clinic name", name);
        if (!Week.isValidDay(today)) {
            problems = Validator.addProblem(problems, "\"" + today + "\" is not a day of the week.");
        }
        if (schedule == null) {
            problems = Validator.addProblem(problems, "A valid schedule is required.");
        }
        return problems;
    }

    /**
     * Opens a clinic for a day, or nothing if any detail is invalid.
     *
     * @param name     the clinic's name
     * @param today    the day of the bookings, e.g. "Wednesday"
     * @param schedule the day's schedule
     * @return the clinic, or null if the details are invalid
     */
    public static Clinic create(String name, String today, ClinicSchedule schedule) {
        if (!check(name, today, schedule).isEmpty()) {
            return null;                                            // [Encapsulation] a clinic without a valid day or schedule cannot exist
        }
        return new Clinic(name, today, schedule);
    }

    /**
     * Returns the clinic's name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the day of the bookings.
     *
     * @return e.g. "Wednesday"
     */
    public String getToday() {
        return today;
    }

    /**
     * Returns the day's schedule.
     *
     * @return the schedule (immutable, so it is safe to share)
     */
    public ClinicSchedule getSchedule() {
        return schedule;
    }

    // =========================================================================
    //  Staff registration
    // =========================================================================

    /**
     * Registers a professional of any type as a member of staff.
     *
     * @param professional the professional, possibly null if create(...) refused it
     * @return a confirmation, or a warning explaining the refusal
     */
    public String registerProfessional(HealthProfessional professional) {
        if (professional == null) {
            return WARNING + "Registration refused. No professional was supplied: invalid details never "
                    + "create an object.";
        }
        if (staff.contains(professional)) {                         // contains() uses equals(), so this catches a reused staff ID
            HealthProfessional holder = staff.get(staff.indexOf(professional));
            return WARNING + "Registration refused. Staff ID " + professional.getId()
                    + " already belongs to " + holder.getName() + ".";
        }
        staff.add(professional);
        return OK + "Registered " + professional.getSummary() + ".";
    }

    /**
     * Finds a registered professional by staff ID.
     *
     * @param professionalId the staff ID, e.g. "100101"
     * @return the professional, or null if no one on staff has that ID
     */
    public HealthProfessional findProfessional(String professionalId) {
        for (HealthProfessional professional : staff) {
            if (professional.getId().equals(Validator.tidy(professionalId))) {
                return professional;
            }
        }
        return null;
    }

    /**
     * Returns every registered professional, as a new list.
     *
     * @return a copy of the staff list
     */
    public ArrayList<HealthProfessional> getStaff() {
        return new ArrayList<>(staff);                              // [Encapsulation] a copy, so outside code cannot add unchecked staff
    }

    /**
     * Returns the registered professionals who work today, as a new list.
     *
     * @return the staff on duty today
     */
    public ArrayList<HealthProfessional> getStaffOnDuty() {
        ArrayList<HealthProfessional> onDuty = new ArrayList<>();
        for (HealthProfessional professional : staff) {
            if (professional.worksOn(today)) {
                onDuty.add(professional);
            }
        }
        return onDuty;
    }

    /**
     * Shows the staff on duty today as a table, followed by who is off.
     *
     * @return the roster for today
     */
    public String viewStaffOnDuty() {
        ArrayList<HealthProfessional> onDuty = getStaffOnDuty();
        if (onDuty.isEmpty()) {
            return WARNING + "No registered staff work on " + today + ".";
        }
        String text = "On duty " + today + " (" + onDuty.size() + " of " + staff.size() + " staff):\n"
                + HealthProfessional.tableHeader();
        for (HealthProfessional professional : onDuty) {
            text += "\n" + professional;                            // [Polymorphism] each type prints its own details column
        }
        String offToday = "";
        for (HealthProfessional professional : staff) {
            if (!professional.worksOn(today)) {
                offToday += (offToday.isEmpty() ? "" : ", ") + professional.getName();
            }
        }
        if (!offToday.isEmpty()) {
            text += "\nNot working today: " + offToday;
        }
        return text;
    }

    // =========================================================================
    //  Patients
    // =========================================================================

    /**
     * Finds a registered patient from typed details.
     *
     * @param patientName the name as typed
     * @param mobile      the mobile number as typed
     * @return the patient, or null if no registered patient matches both
     */
    public Patient findPatient(String patientName, String mobile) {
        for (Patient patient : patients) {
            if (patient.matches(patientName, mobile)) {
                return patient;
            }
        }
        return null;
    }

    /**
     * Returns every registered patient, as a new list.
     *
     * @return a copy of the patient register
     */
    public ArrayList<Patient> getPatients() {
        return new ArrayList<>(patients);
    }

    /**
     * Registers a new patient and issues the next patient number.
     * Private: the clinic decides when registration happens.
     *
     * @param patientName the name, already validated
     * @param mobile      the mobile number, already validated
     * @return the new patient
     */
    private Patient registerPatient(String patientName, String mobile) {
        String number = String.format("%s%0" + Patient.NUMBER_DIGITS + "d",
                Patient.NUMBER_PREFIX, nextPatientNumber);          // the clinic issues numbers, so they are unique by construction
        Patient patient = Patient.create(number, patientName, mobile);
        patients.add(patient);
        nextPatientNumber++;
        return patient;
    }

    // =========================================================================
    //  Booking
    // =========================================================================

    /**
     * Books an appointment with a chosen professional, as an online booking
     * page would after the patient picks someone. The parameter accepts any
     * type of professional.
     *
     * @param patientName  the patient's name
     * @param mobile       the patient's mobile number, spaces allowed
     * @param professional the chosen professional, of any type
     * @param time         the requested time, e.g. "09:30"
     * @return a confirmation with preparation advice, or a warning explaining the refusal
     */
    public String bookAppointment(String patientName, String mobile, HealthProfessional professional,
                                  String time) {
        String problems = Patient.checkName(patientName);           // every input problem is reported at once
        problems = Validator.addProblem(problems, Patient.checkMobile(mobile));
        if (professional == null) {
            problems = Validator.addProblem(problems, "A health professional is required.");
        }
        problems = Validator.addProblem(problems, schedule.checkBookable(time));
        if (!problems.isEmpty()) {
            return WARNING + "Booking refused. " + problems;
        }

        if (!staff.contains(professional)) {
            return WARNING + "Booking refused. " + professional.getSummary() + " is not registered at "
                    + name + ".";
        }
        HealthProfessional booked = staff.get(staff.indexOf(professional)); // use the registered record, not a look-alike object
        if (!booked.worksOn(today)) {
            return WARNING + "Booking refused. " + booked.getName() + " does not work on " + today
                    + " (works " + booked.getWorkingDaysText() + ").";
        }
        TimeSlot slot = schedule.findSlot(time);
        if (findAppointment(booked, slot) != null) {
            return WARNING + "Booking refused. " + booked.getName() + " is already booked at " + slot
                    + "." + suggestNextFreeSlot(booked, slot);
        }
        Patient patient = findPatient(patientName, mobile);
        if (patient != null) {
            Appointment clash = findAppointment(patient, slot);
            if (clash != null) {
                return WARNING + "Booking refused. " + patient.getName() + " already has an appointment at "
                        + slot + " with " + clash.getProfessional().getName() + ".";
            }
        }

        String registration = "returning patient";
        if (patient == null) {
            patient = registerPatient(patientName, mobile);         // first visit: registration is part of booking, as at a real front desk
            registration = "new patient";
        }
        appointments.add(Appointment.create(patient, booked, slot));
        return OK + "Booked " + registration + " " + patient.getSummary() + " with "
                + booked.getSummary() + " at " + slot + ".\n    Before the visit: "
                + booked.getPreparationAdvice();                    // [Polymorphism] each type gives its own advice
    }

    /**
     * Books an appointment by staff ID, as a receptionist would type it.
     * It finds the professional and then applies exactly the same rules as
     * the other bookAppointment method.
     *
     * @param patientName    the patient's name
     * @param mobile         the patient's mobile number, spaces allowed
     * @param professionalId the professional's staff ID, e.g. "100101"
     * @param time           the requested time, e.g. "09:30"
     * @return a confirmation with preparation advice, or a warning explaining the refusal
     */
    public String bookAppointment(String patientName, String mobile, String professionalId,
                                  String time) {
        String problem = HealthProfessional.checkId(professionalId);
        if (!problem.isEmpty()) {
            return WARNING + "Booking refused. " + problem;
        }
        HealthProfessional professional = findProfessional(professionalId);
        if (professional == null) {
            return WARNING + "Booking refused. No one with staff ID " + professionalId.trim()
                    + " works at " + name + ".";
        }
        return bookAppointment(patientName, mobile, professional, time); // one place holds the booking rules, so they cannot drift apart
    }

    // =========================================================================
    //  Viewing, filtering and sorting
    // =========================================================================

    /**
     * Returns every appointment in booking order, as a new list.
     *
     * @return a copy of today's appointments
     */
    public ArrayList<Appointment> getAppointments() {
        return new ArrayList<>(appointments);                       // [Encapsulation] callers can sort or edit the copy, never the real list
    }

    /**
     * Returns the appointments with one professional, as a new list.
     *
     * @param professionalId the staff ID, e.g. "100101"
     * @return the matching appointments in booking order (empty if none)
     */
    public ArrayList<Appointment> filterByProfessional(String professionalId) {
        ArrayList<Appointment> matches = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.isWith(professionalId)) {
                matches.add(appointment);
            }
        }
        return matches;
    }

    /**
     * Returns the appointments booked under one mobile number, as a new list.
     * A shared family number returns every family member's appointments.
     *
     * @param mobile the mobile number, spaces allowed
     * @return the matching appointments in booking order (empty if none)
     */
    public ArrayList<Appointment> filterByPatientMobile(String mobile) {
        ArrayList<Appointment> matches = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.isBookedUnder(mobile)) {
                matches.add(appointment);
            }
        }
        return matches;
    }

    /**
     * Sorts any list of appointments from earliest to latest, so the same
     * method works for all appointments or for a filtered subset.
     *
     * @param list the appointments to sort; it is not changed
     * @return a new list in time order
     */
    public static ArrayList<Appointment> sortByTime(ArrayList<Appointment> list) {
        ArrayList<Appointment> sorted = new ArrayList<>(list);      // sort a copy, so the caller's list keeps its order
        Collections.sort(sorted);                                   // uses Appointment.compareTo (Comparable)
        return sorted;
    }

    /**
     * Shows every appointment in the order they were booked.
     *
     * @return the appointments table, or a warning if there are none
     */
    public String viewAppointmentsInBookingOrder() {
        if (appointments.isEmpty()) {
            return WARNING + "There are no appointments today.";
        }
        return formatAppointments("All appointments, in booking order", getAppointments());
    }

    /**
     * Shows every appointment from earliest to latest.
     *
     * @return the appointments table, or a warning if there are none
     */
    public String viewAppointmentsByTime() {
        if (appointments.isEmpty()) {
            return WARNING + "There are no appointments today.";
        }
        return formatAppointments("All appointments, by time", sortByTime(getAppointments()));
    }

    /**
     * Shows one professional's appointments from earliest to latest.
     *
     * @param professionalId the staff ID, e.g. "100101"
     * @return the appointments table, or a warning explaining why there is nothing to show
     */
    public String viewAppointmentsForProfessional(String professionalId) {
        String problem = HealthProfessional.checkId(professionalId);
        if (!problem.isEmpty()) {
            return WARNING + "Cannot filter. " + problem;
        }
        HealthProfessional professional = findProfessional(professionalId);
        if (professional == null) {
            return WARNING + "Cannot filter. No one with staff ID " + professionalId.trim()
                    + " works at " + name + ".";
        }
        if (!professional.worksOn(today)) {
            return WARNING + professional.getName() + " does not work on " + today
                    + ", so there are no appointments to show.";
        }
        ArrayList<Appointment> matches = filterByProfessional(professionalId);
        if (matches.isEmpty()) {
            return WARNING + professional.getName() + " has no appointments today.";
        }
        return formatAppointments("Appointments with " + professional.getName() + ", by time",
                sortByTime(matches));                               // filter first, then sort only the subset
    }

    /**
     * Shows the appointments booked under one mobile number, earliest first.
     *
     * @param mobile the mobile number, spaces allowed
     * @return the appointments table, or a warning explaining why there is nothing to show
     */
    public String viewAppointmentsForPatient(String mobile) {
        String problem = Patient.checkMobile(mobile);
        if (!problem.isEmpty()) {
            return WARNING + "Cannot filter. " + problem;
        }
        ArrayList<Appointment> matches = filterByPatientMobile(mobile);
        if (matches.isEmpty()) {
            return WARNING + "No appointments are booked under "
                    + Patient.formatMobile(Patient.normaliseMobile(mobile)) + ".";
        }
        return formatAppointments("Appointments under "
                + Patient.formatMobile(Patient.normaliseMobile(mobile)) + ", by time", sortByTime(matches));
    }

    /**
     * Shows the free slots of one professional today.
     *
     * @param professionalId the staff ID, e.g. "100101"
     * @return the free times, or a warning explaining why there are none
     */
    public String viewFreeSlots(String professionalId) {
        String problem = HealthProfessional.checkId(professionalId);
        if (!problem.isEmpty()) {
            return WARNING + problem;
        }
        HealthProfessional professional = findProfessional(professionalId);
        if (professional == null) {
            return WARNING + "No one with staff ID " + professionalId.trim() + " works at " + name + ".";
        }
        if (!professional.worksOn(today)) {
            return WARNING + professional.getName() + " does not work on " + today + ".";
        }
        ArrayList<TimeSlot> free = getFreeSlots(professional);
        if (free.isEmpty()) {
            return WARNING + professional.getName() + " is fully booked today.";
        }
        return OK + professional.getName() + " has " + free.size() + " of " + schedule.countSlots()
                + " slots free: " + ClinicSchedule.listTimes(free);
    }

    /**
     * Returns the slots still free with a professional today.
     *
     * @param professional the professional, of any type
     * @return the free times, earliest first (empty if the person is not working today)
     */
    public ArrayList<TimeSlot> getFreeSlots(HealthProfessional professional) {
        ArrayList<TimeSlot> free = new ArrayList<>();
        if (professional == null || !professional.worksOn(today)) {
            return free;                                            // no one off duty has free slots to offer
        }
        for (TimeSlot slot : schedule.getSlots()) {
            if (findAppointment(professional, slot) == null) {
                free.add(slot);
            }
        }
        return free;
    }

    // =========================================================================
    //  Cancelling and rescheduling
    // =========================================================================

    /**
     * Cancels the appointment a professional has at a time.
     *
     * @param professionalId the staff ID, e.g. "100101"
     * @param time           the appointment time, e.g. "09:00"
     * @return a confirmation, or a warning if nothing could be cancelled
     */
    public String cancelAppointment(String professionalId, String time) {
        String problems = Validator.addProblem(HealthProfessional.checkId(professionalId),
                TimeSlot.checkText(time));
        if (!problems.isEmpty()) {
            return WARNING + "Nothing cancelled. " + problems;
        }
        HealthProfessional professional = findProfessional(professionalId);
        if (professional == null) {
            return WARNING + "Nothing cancelled. No one with staff ID " + professionalId.trim()
                    + " works at " + name + ".";
        }
        TimeSlot slot = TimeSlot.parse(time);
        Appointment match = findAppointment(professional, slot);
        if (match == null) {
            return WARNING + "Nothing cancelled. " + professional.getName() + " has no appointment at "
                    + slot + ".";
        }
        appointments.remove(match);                                 // removed after the search, so the list is never changed mid-loop
        return OK + "Cancelled " + match.getSummary() + ". That slot is free again.";
    }

    /**
     * Cancels the appointment booked under a mobile number. If the number
     * has several appointments (for example a family sharing one phone),
     * nothing is cancelled, because the clinic cannot know which one is
     * meant; the warning lists them so one can be cancelled by staff ID and time.
     *
     * @param mobile the mobile number, spaces allowed
     * @return a confirmation, or a warning if nothing could be cancelled
     */
    public String cancelAppointment(String mobile) {
        String problem = Patient.checkMobile(mobile);
        if (!problem.isEmpty()) {
            return WARNING + "Nothing cancelled. " + problem;
        }
        String shown = Patient.formatMobile(Patient.normaliseMobile(mobile));
        ArrayList<Appointment> matches = sortByTime(filterByPatientMobile(mobile));
        if (matches.isEmpty()) {
            return WARNING + "Nothing cancelled. No appointment is booked under " + shown + ".";
        }
        if (matches.size() > 1) {
            String list = "";
            for (Appointment appointment : matches) {
                list += "\n    - " + appointment.getSummary() + " (staff ID "
                        + appointment.getProfessional().getId() + ")";
            }
            return WARNING + "Nothing cancelled. " + matches.size() + " appointments are booked under "
                    + shown + "; cancel one by staff ID and time instead:" + list; // guessing could cancel the wrong person's visit
        }
        Appointment match = matches.get(0);
        appointments.remove(match);
        return OK + "Cancelled " + match.getSummary() + ". That slot is free again.";
    }

    /**
     * Moves an appointment to another time with the same professional,
     * applying the same rules as a new booking.
     *
     * @param professionalId the staff ID, e.g. "100101"
     * @param currentTime    the current appointment time
     * @param newTime        the requested new time
     * @return a confirmation, or a warning explaining why nothing changed
     */
    public String rescheduleAppointment(String professionalId, String currentTime, String newTime) {
        String problems = Validator.addProblem(HealthProfessional.checkId(professionalId),
                TimeSlot.checkText(currentTime));
        problems = Validator.addProblem(problems, schedule.checkBookable(newTime));
        if (!problems.isEmpty()) {
            return WARNING + "Not rescheduled. " + problems;
        }
        HealthProfessional professional = findProfessional(professionalId);
        if (professional == null) {
            return WARNING + "Not rescheduled. No one with staff ID " + professionalId.trim()
                    + " works at " + name + ".";
        }
        Appointment current = findAppointment(professional, TimeSlot.parse(currentTime));
        if (current == null) {
            return WARNING + "Not rescheduled. " + professional.getName() + " has no appointment at "
                    + TimeSlot.parse(currentTime) + ".";
        }
        TimeSlot newSlot = schedule.findSlot(newTime);
        if (current.startsAt(newSlot)) {
            return WARNING + "Not rescheduled. The appointment is already at " + newSlot + ".";
        }
        if (findAppointment(professional, newSlot) != null) {
            return WARNING + "Not rescheduled. " + professional.getName() + " is already booked at "
                    + newSlot + "." + suggestNextFreeSlot(professional, newSlot);
        }
        Appointment patientClash = findAppointment(current.getPatient(), newSlot);
        if (patientClash != null) {
            return WARNING + "Not rescheduled. " + current.getPatient().getName()
                    + " already has an appointment at " + newSlot + " with "
                    + patientClash.getProfessional().getName() + ".";
        }
        appointments.remove(current);
        appointments.add(Appointment.create(current.getPatient(), professional, newSlot)); // appointments are immutable, so a move is a replacement
        return OK + "Moved " + current.getPatient().getSummary() + " with " + professional.getName()
                + " from " + current.getTimeSlot() + " to " + newSlot + ". " + current.getTimeSlot()
                + " is free again.";
    }

    // =========================================================================
    //  Prescriptions and the daily summary
    // =========================================================================

    /**
     * Issues a repeat prescription for a registered patient, signed by the
     * first prescriber on duty today. The clinic only needs the capability,
     * so it works through the Prescriber interface and never needs to know
     * whether that person is a GP, a specialist or a nurse practitioner.
     *
     * @param patientName the patient's name
     * @param mobile      the patient's mobile number
     * @param medication  the medicine and dose
     * @return the prescription, or a warning explaining why none was issued
     */
    public String issueRepeatPrescription(String patientName, String mobile, String medication) {
        String problems = Validator.addProblem(Patient.checkName(patientName), Patient.checkMobile(mobile));
        problems = Validator.addProblem(problems, Validator.checkRequiredText("Medication", medication));
        if (!problems.isEmpty()) {
            return WARNING + "No prescription issued. " + problems;
        }
        Patient patient = findPatient(patientName, mobile);
        if (patient == null) {
            return WARNING + "No prescription issued. " + Validator.tidy(patientName)
                    + " is not a registered patient here; repeat prescriptions are for existing patients.";
        }
        for (HealthProfessional professional : staff) {
            if (professional instanceof Prescriber && professional.worksOn(today)) {
                Prescriber prescriber = (Prescriber) professional;  // [Abstraction] the clinic relies on the capability, not the class
                return OK + prescriber.prescribe(patient.getName(), medication);
            }
        }
        return WARNING + "No prescription issued. No one who can prescribe works on " + today + ".";
    }

    /**
     * Summarises the day: how busy each professional on duty is, plus totals.
     *
     * @return the summary table
     */
    public String getDailySummary() {
        int slotsPerPerson = schedule.countSlots();
        String text = name + " - " + today + " - " + schedule + "\n"
                + Display.cell("ID", Display.ID_WIDTH) + Display.cell("NAME", Display.NAME_WIDTH)
                + Display.cell("TYPE", Display.TYPE_WIDTH) + "BOOKED  FREE  BUSY\n"
                + Display.dashes(Display.ID_WIDTH) + Display.dashes(Display.NAME_WIDTH)
                + Display.dashes(Display.TYPE_WIDTH) + "------  ----  ----";
        int bookedSlots = 0;
        ArrayList<HealthProfessional> onDuty = getStaffOnDuty();
        for (HealthProfessional professional : onDuty) {
            int booked = filterByProfessional(professional.getId()).size();
            bookedSlots += booked;
            text += "\n" + Display.cell(professional.getId(), Display.ID_WIDTH)
                    + Display.cell(professional.getName(), Display.NAME_WIDTH)
                    + Display.cell(professional.getProfessionType(), Display.TYPE_WIDTH)
                    + String.format("%6d  %4d  %3d%%", booked, slotsPerPerson - booked,
                            Display.percent(booked, slotsPerPerson));
        }
        int totalSlots = onDuty.size() * slotsPerPerson;
        text += "\nTotal: " + appointments.size() + " appointments, " + bookedSlots + " of " + totalSlots
                + " staff slots booked (" + Display.percent(bookedSlots, totalSlots) + "%), "
                + patients.size() + " patients registered, " + onDuty.size() + " of " + staff.size()
                + " staff on duty.";
        return text;
    }

    // =========================================================================
    //  Private helpers
    // =========================================================================

    /**
     * Finds a professional's appointment at a time.
     *
     * @param professional the professional
     * @param slot         the time
     * @return the appointment, or null if that slot is free
     */
    private Appointment findAppointment(HealthProfessional professional, TimeSlot slot) {
        for (Appointment appointment : appointments) {
            if (appointment.isWith(professional) && appointment.startsAt(slot)) {
                return appointment;                                 // equals() on both: same staff ID and same minute
            }
        }
        return null;
    }

    /**
     * Finds a patient's appointment at a time, with anyone.
     *
     * @param patient the patient
     * @param slot    the time
     * @return the appointment, or null if the patient is free then
     */
    private Appointment findAppointment(Patient patient, TimeSlot slot) {
        for (Appointment appointment : appointments) {
            if (appointment.isFor(patient) && appointment.startsAt(slot)) {
                return appointment;
            }
        }
        return null;
    }

    /**
     * Suggests the next free slot after a taken one, for a helpful warning.
     *
     * @param professional the professional who is busy
     * @param after        the taken slot
     * @return a sentence naming the next free slot, or saying there is none
     */
    private String suggestNextFreeSlot(HealthProfessional professional, TimeSlot after) {
        for (TimeSlot slot : getFreeSlots(professional)) {
            if (after.isBefore(slot)) {
                return " Next free slot: " + slot + ".";            // a refusal with an alternative is what a real receptionist gives
            }
        }
        return " No later slot is free today.";
    }

    /**
     * Formats a list of appointments as a table under a title.
     *
     * @param title the table title
     * @param list  the appointments to show, in the order given
     * @return the title, heading and two lines per appointment
     */
    private static String formatAppointments(String title, ArrayList<Appointment> list) {
        String text = title + " (" + list.size() + "):\n" + Appointment.tableHeader();
        for (Appointment appointment : list) {
            text += "\n" + appointment;
        }
        return text;
    }
}
