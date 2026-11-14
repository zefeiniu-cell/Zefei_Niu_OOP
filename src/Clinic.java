/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

import java.util.ArrayList;
import java.util.Collections;

/**
 * One clinic's staff, patients and appointments for one day.
 *
 * Why the booking rules live here and not in Appointment: every rule that
 * needs the whole day can only be checked by something that sees the whole
 * day. "Is Dr Magee already busy at 09:00?" and "Is Romy already booked with
 * someone else at 09:00?" both need every appointment, and only the clinic
 * has them all.
 *
 * I followed how a real front desk works:
 * First the clinic registers its staff (a staff ID can only be used once).
 * Then a patient books. On their first booking they are registered and get
 * a patient number; next time, the same name and mobile are recognised.
 * For this model, one mobile belongs to one patient; a different name
 * cannot silently create another record under that mobile.
 * After that the day keeps changing: cancel, reschedule, rebook a freed slot.
 *
 * Booking, cancellation and time changes return result messages rather than
 * printing. Query methods return objects, copied lists or formatted text.
 * Refused actions leave the records unchanged. The day is passed in when
 * the clinic is created, so the same code
 * works for any day, and the demo gives the same output every time it runs.
 *
 * Why ArrayList for the appointments:
 * ArrayList, not an array: bookings come and go all day, and an array can't change size.
 * ArrayList, not HashSet: I want the booking order kept, and a set has no order.
 * It can also be filtered with a loop and sorted with Collections.sort.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Clinic {

    /** Start of every successful message. */
    public static final String OK = "[OK] ";

    /** Start of every refusal. */
    public static final String WARNING = "[WARNING] ";

    /** Clinic name, e.g. "Arcana Health Centre". */
    private final String name;

    /** The day these bookings are for, e.g. "Wednesday". */
    private final String today;

    /** Today's bookable times. */
    private final ClinicSchedule schedule;

    /** Registered staff, of any type. */
    private final ArrayList<HealthProfessional> staff = new ArrayList<>(); // [Polymorphism] every type fits

    /** Registered patients, in the order they first came. */
    private final ArrayList<Patient> patients = new ArrayList<>();

    /** Today's appointments, in booking order. Only the booking rules below change it. */
    private final ArrayList<Appointment> appointments = new ArrayList<>(); // [Encapsulation] no setter

    /** The number the next new patient gets. */
    private int nextPatientNumber = 1;

    /**
     * Builds a clinic from checked details. Outside code uses create(...).
     *
     * @param name     the clinic name (already checked)
     * @param today    the day of the bookings (already checked)
     * @param schedule today's schedule, not null
     */
    private Clinic(String name, String today, ClinicSchedule schedule) {
        this.name = Validator.tidy(name);
        this.today = Week.normalise(today); // "wed" and "Wednesday" must be the same day everywhere
        this.schedule = schedule;
    }

    /**
     * Says what is wrong with a clinic's details, if anything.
     *
     * @param name     the clinic name
     * @param today    the day of the bookings
     * @param schedule today's schedule
     * @return "" if valid, otherwise every problem found
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
     * Opens a clinic for a day.
     *
     * @param name     the clinic name
     * @param today    the day of the bookings, e.g. "Wednesday"
     * @param schedule today's schedule
     * @return the clinic, or null if the details are invalid
     */
    public static Clinic create(String name, String today, ClinicSchedule schedule) {
        if (!check(name, today, schedule).isEmpty()) {
            return null; // [Encapsulation] a clinic with no real day or schedule can't exist
        }
        return new Clinic(name, today, schedule);
    }

    /**
     * Gives the clinic name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gives the day of the bookings.
     *
     * @return e.g. "Wednesday"
     */
    public String getToday() {
        return today;
    }

    /**
     * Gives today's schedule.
     *
     * @return the schedule (it can't be changed, so sharing it is safe)
     */
    public ClinicSchedule getSchedule() {
        return schedule;
    }

    // ===== Staff =====

    /**
     * Registers a professional of any type as staff.
     *
     * @param professional the professional; null if create(...) refused the details
     * @return a confirmation, or a warning saying why not
     */
    public String registerProfessional(HealthProfessional professional) {
        if (professional == null) {
            return WARNING + "Registration refused. No professional was supplied: invalid details never "
                    + "create an object.";
        }
        if (staff.contains(professional)) { // contains() uses equals(), so a reused staff ID is caught here
            HealthProfessional holder = staff.get(staff.indexOf(professional));
            return WARNING + "Registration refused. Staff ID " + professional.getId()
                    + " already belongs to " + holder.getName() + ".";
        }
        staff.add(professional);
        professional.lockWorkingDays(); // keep the availability advertised for this session
        return OK + "Registered " + professional.getSummary() + ".";
    }

    /**
     * Finds a staff member by staff ID.
     *
     * @param professionalId e.g. "100101"
     * @return the professional, or null if nobody here has that ID
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
     * Gives every staff member.
     *
     * @return a new list of the staff
     */
    public ArrayList<HealthProfessional> getStaff() {
        return new ArrayList<>(staff); // [Encapsulation] a copy, so nobody can add staff without registering
    }

    /**
     * Gives the staff who work today.
     *
     * @return a new list of the staff on duty
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
     * Shows today's staff as a table, then who is off today.
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
            text += "\n" + professional; // [Polymorphism] each type fills in its own details column
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

    // ===== Patients =====

    /**
     * Finds a registered patient from typed details.
     *
     * @param patientName the name as typed
     * @param mobile      the mobile as typed
     * @return the patient, or null if nobody registered matches both
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
     * Finds the record that already uses this mobile.
     *
     * @param mobile the number as typed, with optional spaces or hyphens
     * @return the matching patient, or null
     */
    public Patient findPatientByMobile(String mobile) {
        String normalised = Patient.normaliseMobile(mobile);
        for (Patient patient : patients) {
            if (patient.getMobile().equals(normalised)) {
                return patient;
            }
        }
        return null;
    }

    /**
     * Gives every registered patient.
     *
     * @return a new list of the patients
     */
    public ArrayList<Patient> getPatients() {
        return new ArrayList<>(patients);
    }

    /**
     * Registers a new patient and gives them the next patient number.
     * It is private because the clinic decides when this happens: on a
     * patient's first booking.
     *
     * @param patientName the name (already checked)
     * @param mobile      the mobile (already checked)
     * @return the new patient
     */
    private Patient registerPatient(String patientName, String mobile) {
        // the clinic counts up, so two patients never share a number
        String number = String.format("%s%0" + Patient.NUMBER_DIGITS + "d",
                Patient.NUMBER_PREFIX, nextPatientNumber);
        Patient patient = Patient.create(number, patientName, mobile);
        patients.add(patient);
        nextPatientNumber++;
        return patient;
    }

    // ===== Booking =====

    /**
     * Books with a professional the patient picked, like on a booking website.
     * The parameter is the parent type, so it takes any kind of professional.
     *
     * @param patientName  the patient's name
     * @param mobile       the patient's mobile, spaces allowed
     * @param professional the chosen professional, of any type
     * @param time         the time asked for, e.g. "09:30"
     * @return a confirmation with advice for the visit, or a warning saying why not
     */
    public String bookAppointment(String patientName, String mobile, HealthProfessional professional,
                                  String time) {
        // First look at all the input together, so the person sees every mistake at once.
        String problems = Patient.checkName(patientName);
        problems = Validator.addProblem(problems, Patient.checkMobile(mobile));
        if (professional == null) {
            problems = Validator.addProblem(problems, "A health professional is required.");
        }
        problems = Validator.addProblem(problems, schedule.checkBookable(time));
        if (professional == null || !problems.isEmpty()) {
            return WARNING + "Booking refused. " + problems;
        }

        // Then the clinic's rules: registered? works today? slot free? patient free?
        if (!staff.contains(professional)) {
            return WARNING + "Booking refused. " + professional.getSummary() + " is not registered at "
                    + name + ".";
        }
        // use the registered object, not a look-alike with the same ID
        HealthProfessional booked = staff.get(staff.indexOf(professional));
        if (!booked.worksOn(today)) {
            return WARNING + "Booking refused. " + booked.getName() + " does not work on " + today
                    + " (works " + booked.getWorkingDaysText() + ").";
        }
        TimeSlot slot = schedule.findSlot(time);
        if (findAppointment(booked, slot) != null) {
            return WARNING + "Booking refused. " + booked.getName() + " is already booked at " + slot
                    + "." + suggestNextFreeSlot(booked, slot);
        }
        Patient patient = findPatientByMobile(mobile);
        if (patient != null && !patient.matches(patientName, mobile)) {
            return WARNING + "Booking refused. This mobile is already registered to another name. "
                    + "Use the recorded name or a different mobile.";
        }
        if (patient != null) {
            Appointment clash = findAppointment(patient, slot);
            if (clash != null) {
                return WARNING + "Booking refused. " + patient.getName() + " already has an appointment at "
                        + slot + " with " + clash.getProfessional().getName() + ".";
            }
        }

        // Last, book it. A first-time patient is registered here, like at a real front desk.
        String registration = "returning patient";
        if (patient == null) {
            if (String.valueOf(nextPatientNumber).length() > Patient.NUMBER_DIGITS) {
                return WARNING + "Booking refused. No patient numbers remain for this clinic session.";
            }
            patient = registerPatient(patientName, mobile);
            registration = "new patient";
        }
        appointments.add(Appointment.create(patient, booked, slot));
        return OK + "Booked " + registration + " " + patient.getSummary() + " with "
                + booked.getSummary() + " at " + slot + ".\n    Before the visit: "
                + booked.getPreparationAdvice(); // [Polymorphism] each type gives its own advice
    }

    /**
     * Books by staff ID, the way a receptionist would type it.
     * It finds the professional, then uses exactly the same rules as the
     * other bookAppointment.
     *
     * @param patientName    the patient's name
     * @param mobile         the patient's mobile, spaces allowed
     * @param professionalId the staff ID, e.g. "100101"
     * @param time           the time asked for, e.g. "09:30"
     * @return a confirmation with advice for the visit, or a warning saying why not
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
        // the rules are written once, so both ways book the same
        return bookAppointment(patientName, mobile, professional, time);
    }

    // ===== Viewing, filtering and sorting =====

    /**
     * Gives every appointment in booking order.
     *
     * @return a new list of today's appointments
     */
    public ArrayList<Appointment> getAppointments() {
        return new ArrayList<>(appointments); // [Encapsulation] a copy; the real list can't be touched
    }

    /**
     * Gives the appointments with one professional.
     *
     * @param professionalId the staff ID, e.g. "100101"
     * @return a new list in booking order (empty if none)
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
     * Gives the appointments booked under one mobile number.
     * A family phone gives back the whole family's appointments.
     *
     * @param mobile the mobile, spaces allowed
     * @return a new list in booking order (empty if none)
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
     * Sorts any list of appointments by time, earliest first.
     * The same method works for all of today or for a filtered part of it.
     *
     * @param list the appointments to sort (this list is not changed)
     * @return a new list in time order
     */
    public static ArrayList<Appointment> sortByTime(ArrayList<Appointment> list) {
        ArrayList<Appointment> sorted = new ArrayList<>(list); // sort a copy, so the caller's order is kept
        Collections.sort(sorted); // uses Appointment.compareTo
        return sorted;
    }

    /**
     * Shows every appointment in the order they were booked.
     *
     * @return the table, or a warning if there are none
     */
    public String viewAppointmentsInBookingOrder() {
        if (appointments.isEmpty()) {
            return WARNING + "There are no appointments today.";
        }
        return formatAppointments("All appointments, in booking order", getAppointments());
    }

    /**
     * Shows every appointment by time.
     *
     * @return the table, or a warning if there are none
     */
    public String viewAppointmentsByTime() {
        if (appointments.isEmpty()) {
            return WARNING + "There are no appointments today.";
        }
        return formatAppointments("All appointments, by time", sortByTime(getAppointments()));
    }

    /**
     * Shows one professional's appointments by time.
     *
     * @param professionalId the staff ID, e.g. "100101"
     * @return the table, or a warning saying why there is nothing to show
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
                sortByTime(matches)); // filter first, then sort just that part
    }

    /**
     * Shows the appointments under one mobile number, by time.
     *
     * @param mobile the mobile, spaces allowed
     * @return the table, or a warning saying why there is nothing to show
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
     * Shows a professional's free times today.
     *
     * @param professionalId the staff ID, e.g. "100101"
     * @return the free times, or a warning saying why there are none
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
     * Gives the times still free with a professional today.
     *
     * @param professional the professional, of any type
     * @return the free times, earliest first (empty if unregistered or off today)
     */
    public ArrayList<TimeSlot> getFreeSlots(HealthProfessional professional) {
        ArrayList<TimeSlot> free = new ArrayList<>();
        if (professional == null || !staff.contains(professional)) {
            return free;
        }
        HealthProfessional registered = staff.get(staff.indexOf(professional));
        if (!registered.worksOn(today)) {
            return free; // someone who is off today has no free slots to offer
        }
        for (TimeSlot slot : schedule.getSlots()) {
            if (findAppointment(registered, slot) == null) {
                free.add(slot);
            }
        }
        return free;
    }

    // ===== Cancelling and rescheduling =====

    /**
     * Cancels the selection only if it is booked under this patient's mobile.
     *
     * @param mobile the current patient's mobile
     * @param professionalId the selected staff ID
     * @param time the selected appointment time
     * @return a confirmation, or a warning with nothing changed
     */
    public String cancelAppointment(String mobile, String professionalId, String time) {
        String problem = checkPatientAppointment(mobile, professionalId, time);
        if (!problem.isEmpty()) {
            return WARNING + "Nothing cancelled. " + problem;
        }
        return cancelAppointment(professionalId, time); // reuse the existing removal rule
    }

    /**
     * Cancels a professional's appointment at a time.
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
        appointments.remove(match); // removed after the search loop, so the list never changes mid-loop
        return OK + "Cancelled " + match.getSummary() + ". That slot is free again.";
    }

    /**
     * Cancels the appointment booked under a mobile number.
     * If the patient has more than one appointment,
     * nothing is cancelled: the clinic can't know which one is meant, and
     * guessing could cancel the wrong visit. The warning lists them
     * so the right one can be cancelled by staff ID and time.
     *
     * @param mobile the mobile, spaces allowed
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
                    + shown + "; cancel one by staff ID and time instead:" + list;
        }
        Appointment match = matches.get(0);
        appointments.remove(match);
        return OK + "Cancelled " + match.getSummary() + ". That slot is free again.";
    }

    /**
     * Moves the selection only if it is booked under this patient's mobile.
     * All time and conflict checks still happen in the existing operation.
     *
     * @param mobile the current patient's mobile
     * @param professionalId the selected staff ID
     * @param currentTime the currently booked time
     * @param newTime the requested new time
     * @return a confirmation, or a warning with the original booking kept
     */
    public String rescheduleAppointment(String mobile, String professionalId,
                                       String currentTime, String newTime) {
        String problem = checkPatientAppointment(mobile, professionalId, currentTime);
        if (!problem.isEmpty()) {
            return WARNING + "Not rescheduled. " + problem;
        }
        return rescheduleAppointment(professionalId, currentTime, newTime);
    }

    /**
     * Moves an appointment to another time with the same professional.
     * The new time goes through the same rules as a new booking.
     *
     * @param professionalId the staff ID, e.g. "100101"
     * @param currentTime    the appointment's time now
     * @param newTime        the time asked for
     * @return a confirmation, or a warning saying why nothing changed
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
        // an Appointment can't change, so moving it means replacing it
        appointments.add(Appointment.create(current.getPatient(), professional, newSlot));
        return OK + "Moved " + current.getPatient().getSummary() + " with " + professional.getName()
                + " from " + current.getTimeSlot() + " to " + newSlot + ". " + current.getTimeSlot()
                + " is free again.";
    }

    // ===== Prescriptions and the end of the day =====

    /**
     * Writes a repeat prescription for a registered patient, signed by the
     * first prescriber who works today.
     * The clinic only cares that someone can prescribe, so it works through
     * the Prescriber interface. It never needs to know if that person is a
     * GP, a specialist or a nurse practitioner.
     *
     * @param patientName the patient's name
     * @param mobile      the patient's mobile
     * @param medication  the medicine and dose
     * @return the prescription, or a warning saying why none was written
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
                // [Abstraction] the clinic needs the ability, not the class
                Prescriber prescriber = (Prescriber) professional;
                return OK + prescriber.prescribe(patient.getName(), medication);
            }
        }
        return WARNING + "No prescription issued. No one who can prescribe works on " + today + ".";
    }

    /**
     * Sums up the day: how busy each person on duty is, plus totals.
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

    // ===== Private helpers =====

    /**
     * Checks the selected staff ID and time against this patient's bookings.
     *
     * @param mobile the patient's mobile
     * @param professionalId the selected staff ID
     * @param time the selected appointment time
     * @return an empty string for a match, otherwise the reason
     */
    private String checkPatientAppointment(String mobile, String professionalId, String time) {
        String problems = Patient.checkMobile(mobile);
        problems = Validator.addProblem(problems, HealthProfessional.checkId(professionalId));
        problems = Validator.addProblem(problems, TimeSlot.checkText(time));
        if (!problems.isEmpty()) {
            return problems;
        }
        TimeSlot slot = TimeSlot.parse(time);
        for (Appointment appointment : filterByPatientMobile(mobile)) {
            if (appointment.isWith(professionalId) && appointment.startsAt(slot)) {
                return "";
            }
        }
        return "No matching appointment is booked under this mobile.";
    }

    /**
     * Finds a professional's appointment at a time.
     *
     * @param professional the professional
     * @param slot         the time
     * @return the appointment, or null if that time is free
     */
    private Appointment findAppointment(HealthProfessional professional, TimeSlot slot) {
        for (Appointment appointment : appointments) {
            // both use equals(): same staff ID, same minute
            if (appointment.isWith(professional) && appointment.startsAt(slot)) {
                return appointment;
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
     * Suggests the next free time after a taken one, for a more helpful warning.
     * A real receptionist doesn't just say "taken", they offer another time.
     *
     * @param professional the busy professional
     * @param after        the taken time
     * @return a sentence with the next free time, or saying there isn't one
     */
    private String suggestNextFreeSlot(HealthProfessional professional, TimeSlot after) {
        for (TimeSlot slot : getFreeSlots(professional)) {
            if (after.isBefore(slot)) {
                return " Next free slot: " + slot + ".";
            }
        }
        return " No later slot is free today.";
    }

    /**
     * Puts a list of appointments into a table under a title.
     *
     * @param title the table title
     * @param list  the appointments, in the order to show them
     * @return the title, the heading and two lines per appointment
     */
    private static String formatAppointments(String title, ArrayList<Appointment> list) {
        String text = title + " (" + list.size() + "):\n" + Appointment.tableHeader();
        for (Appointment appointment : list) {
            text += "\n" + appointment;
        }
        return text;
    }
}
