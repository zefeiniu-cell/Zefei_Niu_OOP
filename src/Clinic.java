import java.util.ArrayList;
import java.util.Collections;

/**
 * Arcana Health Centre - Appointment System.
 *
 * Keeps one day's staff, patients and appointments.
 * Booking changes return a result message. Refused changes leave the records as they were.
 *
 * @author Zefei Niu
 */
public class Clinic {
    /** Start of every successful message. */
    public static final String OK = "[OK] ";

    /** Start of every refusal. */
    public static final String WARNING = "[WARNING] ";

    /* Clinic name, e.g. "Arcana Health Centre". */
    private final String name;

    /* The day these bookings are for, e.g. "Wednesday". */
    private final String today;

    /* Today's bookable times. */
    private final ClinicSchedule schedule;

    /* Registered staff, of any type. */
    private final ArrayList<HealthProfessional> staff = new ArrayList<>();

    /* Registered patients, in the order they first came. */
    private final ArrayList<Patient> patients = new ArrayList<>();

    /* Today's appointments, in booking order. Only the booking rules below change it. */
    private final ArrayList<Appointment> appointments = new ArrayList<>();

    /* The number the next new patient gets. */
    private int nextPatientNumber = 1;

    /** Builds a clinic from checked details. Outside code uses create(...). */
    private Clinic(String name, String today, ClinicSchedule schedule) {
        this.name = Validator.tidy(name);
        this.today = Week.normalise(today);
        this.schedule = schedule;
    }

    /**
     * Says what is wrong with a clinic's details, if anything.
     *
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
     * @return the clinic, or null if the details are invalid
     */
    public static Clinic create(String name, String today, ClinicSchedule schedule) {
        if (!check(name, today, schedule).isEmpty()) {
            return null;
        }
        return new Clinic(name, today, schedule);
    }

    /** Gives the clinic name. */
    public String getName() {
        return name;
    }

    /** Gives the day of the bookings. */
    public String getToday() {
        return today;
    }

    /** Gives today's schedule. */
    public ClinicSchedule getSchedule() {
        return schedule;
    }

    /**
     * Registers a professional of any type as staff.
     *
     * @return a confirmation, or a warning saying why not
     */
    public String registerProfessional(HealthProfessional professional) {
        if (professional == null) {
            return WARNING + "Registration refused. No professional was supplied: invalid details never "
                    + "create an object.";
        }
        if (staff.contains(professional)) {
            HealthProfessional holder = staff.get(staff.indexOf(professional));
            return WARNING + "Registration refused. Staff ID " + professional.getId()
                    + " already belongs to " + holder.getName() + ".";
        }
        staff.add(professional);
        professional.lockWorkingDays(); // Keep the registered roster fixed.
        return OK + "Registered " + professional.getSummary() + ".";
    }

    /**
     * Finds a staff member by staff ID.
     *
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

    /** Gives every staff member. */
    public ArrayList<HealthProfessional> getStaff() {
        return new ArrayList<>(staff);
    }

    /** Gives the staff who work today. */
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
     * @return the staff table, or a [WARNING] message when no registered staff work today
     */
    public String viewStaffOnDuty() {
        ArrayList<HealthProfessional> onDuty = getStaffOnDuty();
        if (onDuty.isEmpty()) {
            return WARNING + "No registered staff work on " + today + ".";
        }
        String text = "On duty " + today + " (" + onDuty.size() + " of " + staff.size() + " staff):\n"
                + HealthProfessional.tableHeader();
        for (HealthProfessional professional : onDuty) {
            text += "\n" + professional;
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

    /**
     * Finds a registered patient from typed details.
     *
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

    /** Gives every registered patient. */
    public ArrayList<Patient> getPatients() {
        return new ArrayList<>(patients);
    }

    /**
     * Registers a new patient and gives them the next patient number. It is private because
     * the clinic decides when this happens: on a patient's first booking.
     */
    private Patient registerPatient(String patientName, String mobile) {
        String number = String.format("%s%0" + Patient.NUMBER_DIGITS + "d",
                Patient.NUMBER_PREFIX, nextPatientNumber);
        Patient patient = Patient.create(number, patientName, mobile);
        patients.add(patient);
        nextPatientNumber++;
        return patient;
    }

    /**
     * Checks the patient, staff availability and time conflicts before adding a booking.
     *
     * @return a booking confirmation, or a warning without changing the records
     */
    public String bookAppointment(String patientName, String mobile, HealthProfessional professional,
                                  String time) {
        String problems = Patient.checkName(patientName);
        problems = Validator.addProblem(problems, Patient.checkMobile(mobile));
        if (professional == null) {
            problems = Validator.addProblem(problems, "A health professional is required.");
        }
        problems = Validator.addProblem(problems, schedule.checkBookable(time));
        if (professional == null || !problems.isEmpty()) {
            return WARNING + "Booking refused. " + problems;
        }

        if (!staff.contains(professional)) {
            return WARNING + "Booking refused. " + professional.getSummary() + " is not registered at "
                    + name + ".";
        }

        // Use the registered details when another object has the same staff ID.
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
                + booked.getPreparationAdvice();
    }

    /**
     * Finds the staff member by ID, then uses the object-based booking method.
     *
     * @return a booking confirmation, or a warning without changing the records
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

        return bookAppointment(patientName, mobile, professional, time);
    }

    /** Gives every appointment in booking order. */
    public ArrayList<Appointment> getAppointments() {
        return new ArrayList<>(appointments);
    }

    /** Gives the appointments with one professional. */
    public ArrayList<Appointment> filterByProfessional(String professionalId) {
        ArrayList<Appointment> matches = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.isWith(professionalId)) {
                matches.add(appointment);
            }
        }
        return matches;
    }

    /** Finds this patient's bookings using the normalised mobile. */
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
     * Sorts any list of appointments by time, earliest first. The same method works for all of
     * today or for a filtered part of it.
     */
    public static ArrayList<Appointment> sortByTime(ArrayList<Appointment> list) {
        ArrayList<Appointment> sorted = new ArrayList<>(list); // Keep the original order.
        Collections.sort(sorted);
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
                sortByTime(matches));
    }

    /**
     * Shows the appointments under one mobile number, by time.
     *
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

    /** Gives the times still free with a professional today. */
    public ArrayList<TimeSlot> getFreeSlots(HealthProfessional professional) {
        ArrayList<TimeSlot> free = new ArrayList<>();
        if (professional == null || !staff.contains(professional)) {
            return free;
        }
        HealthProfessional registered = staff.get(staff.indexOf(professional));
        if (!registered.worksOn(today)) {
            return free;
        }
        for (TimeSlot slot : schedule.getSlots()) {
            if (findAppointment(registered, slot) == null) {
                free.add(slot);
            }
        }
        return free;
    }

    /**
     * Cancels the selection only if it is booked under this patient's mobile.
     *
     * @return a confirmation, or a warning with nothing changed
     */
    public String cancelAppointment(String mobile, String professionalId, String time) {
        String problem = checkPatientAppointment(mobile, professionalId, time);
        if (!problem.isEmpty()) {
            return WARNING + "Nothing cancelled. " + problem;
        }
        return cancelAppointment(professionalId, time);
    }

    /**
     * Cancels a professional's appointment at a time.
     *
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
        appointments.remove(match);
        return OK + "Cancelled " + match.getSummary() + ". That slot is free again.";
    }

    /**
     * Cancels by mobile when exactly one appointment matches.
     *
     * @return a confirmation, or a warning if there is no match or more than one
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
     * Moves the selection only if it is booked under this patient's mobile. All time and
     * conflict checks still happen in the existing operation.
     *
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
     * Moves an appointment to another time with the same professional. The new time goes
     * through the same rules as a new booking.
     *
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
        // All checks have passed before the old booking is replaced.
        appointments.remove(current);

        appointments.add(Appointment.create(current.getPatient(), professional, newSlot));
        return OK + "Moved " + current.getPatient().getSummary() + " with " + professional.getName()
                + " from " + current.getTimeSlot() + " to " + newSlot + ". " + current.getTimeSlot()
                + " is free again.";
    }

    /**
     * Returns example prescription text from the first on-duty Prescriber.
     *
     * @return the example text, or a warning if the details or prescriber are unavailable
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
                Prescriber prescriber = (Prescriber) professional;
                return OK + prescriber.prescribe(patient.getName(), medication);
            }
        }
        return WARNING + "No prescription issued. No one who can prescribe works on " + today + ".";
    }

    /** Sums up the day: how busy each person on duty is, plus totals. */
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

    /**
     * Checks the selected staff ID and time against this patient's bookings.
     *
     * @return an empty string for a matching booking, otherwise a problem message
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
     * @return the matching appointment, or null if the professional is free at that time
     */
    private Appointment findAppointment(HealthProfessional professional, TimeSlot slot) {
        for (Appointment appointment : appointments) {
            if (appointment.isWith(professional) && appointment.startsAt(slot)) {
                return appointment;
            }
        }
        return null;
    }

    /**
     * Finds a patient's appointment at a time, with anyone.
     *
     * @return the matching appointment, or null if the patient has no booking at that time
     */
    private Appointment findAppointment(Patient patient, TimeSlot slot) {
        for (Appointment appointment : appointments) {
            if (appointment.isFor(patient) && appointment.startsAt(slot)) {
                return appointment;
            }
        }
        return null;
    }

    /** Finds the first free start later than the refused time. */
    private String suggestNextFreeSlot(HealthProfessional professional, TimeSlot after) {
        for (TimeSlot slot : getFreeSlots(professional)) {
            if (after.isBefore(slot)) {
                return " Next free slot: " + slot + ".";
            }
        }
        return " No later slot is free today.";
    }

    /** Puts a list of appointments into a table under a title. */
    private static String formatAppointments(String title, ArrayList<Appointment> list) {
        String text = title + " (" + list.size() + "):\n" + Appointment.tableHeader();
        for (Appointment appointment : list) {
            text += "\n" + appointment;
        }
        return text;
    }
}
