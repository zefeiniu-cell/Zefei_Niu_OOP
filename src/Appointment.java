/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * One booking for today: a patient, a health professional and a time.
 *
 * An appointment has-a Patient, has-a HealthProfessional and has-a TimeSlot.
 * The professional is stored as the parent type HealthProfessional, so this
 * one class works for a GP, a physio, or a type that doesn't exist yet.
 *
 * How it can never be half-made: the constructor is private and create(...)
 * refuses any missing part. The three parts are already valid on their own
 * (each has its own create), and all fields are final, so a booking can't be
 * changed afterwards. Rescheduling makes a new appointment instead.
 *
 * Why the "no double-booking" rule is not in here: an appointment only knows
 * about itself. To know that Dr Magee is already busy at 09:00, you have to
 * look at all of today's bookings, and only the Clinic can see those.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Appointment implements Comparable<Appointment> {

    /** The patient who booked. */
    private final Patient patient;

    /** The professional being seen, of any type. */
    private final HealthProfessional professional; // [Polymorphism] the parent type fits every kind

    /** When the appointment starts. */
    private final TimeSlot timeSlot;

    /**
     * Builds an appointment from parts that were already checked.
     * Outside code uses create(...).
     *
     * @param patient      the patient, not null
     * @param professional the professional, not null
     * @param timeSlot     the time, not null
     */
    private Appointment(Patient patient, HealthProfessional professional, TimeSlot timeSlot) {
        this.patient = patient;
        this.professional = professional;
        this.timeSlot = timeSlot;
    }

    /**
     * Says which parts of an appointment are missing, if any.
     *
     * @param patient      the patient, maybe null
     * @param professional the professional, maybe null
     * @param timeSlot     the time, maybe null
     * @return "" if nothing is missing, otherwise what is missing
     */
    public static String check(Patient patient, HealthProfessional professional, TimeSlot timeSlot) {
        String problems = "";
        if (patient == null) {
            problems = Validator.addProblem(problems, "A patient is required.");
        }
        if (professional == null) {
            problems = Validator.addProblem(problems, "A health professional is required.");
        }
        if (timeSlot == null) {
            problems = Validator.addProblem(problems, "A time slot is required.");
        }
        return problems;
    }

    /**
     * Creates an appointment.
     *
     * @param patient      the patient
     * @param professional any type of health professional
     * @param timeSlot     the start time
     * @return the new appointment, or null if a part is missing
     */
    public static Appointment create(Patient patient, HealthProfessional professional,
                                     TimeSlot timeSlot) {
        if (!check(patient, professional, timeSlot).isEmpty()) {
            return null; // [Encapsulation] a half-made booking can't exist
        }
        return new Appointment(patient, professional, timeSlot);
    }

    /**
     * Gives the heading for the appointments table.
     * Each appointment takes two lines (the professional, then the patient),
     * so the heading has two lines of titles too.
     *
     * @return three lines: professional titles, patient titles, dashes
     */
    public static String tableHeader() {
        return Display.cell("TIME", Display.TIME_WIDTH) + HealthProfessional.headerTitles() + "\n"
                + Display.cell("", Display.TIME_WIDTH) + Display.cell("PAT.", Display.ID_WIDTH)
                + Display.cell("PATIENT", Display.NAME_WIDTH) + "MOBILE" + "\n"
                + Display.dashes(Display.TIME_WIDTH) + HealthProfessional.headerRule();
    }

    /**
     * Gives the patient.
     *
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Gives the professional.
     *
     * @return the professional, as the parent type
     */
    public HealthProfessional getProfessional() {
        return professional;
    }

    /**
     * Gives the start time.
     *
     * @return the time
     */
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    /**
     * Checks whether this appointment is with a professional.
     *
     * @param other the professional to compare with
     * @return true if it is the same professional (same staff ID)
     */
    public boolean isWith(HealthProfessional other) {
        // equals() compares staff IDs, so any object for that person matches
        return professional.equals(other);
    }

    /**
     * Checks whether this appointment is with the professional who has a staff ID.
     *
     * @param professionalId the staff ID, e.g. "100101"
     * @return true if the IDs match
     */
    public boolean isWith(String professionalId) {
        return professionalId != null && professional.getId().equals(professionalId.trim());
    }

    /**
     * Checks whether this appointment belongs to a patient.
     *
     * @param other the patient to compare with
     * @return true if it is the same person
     */
    public boolean isFor(Patient other) {
        return patient.equals(other);
    }

    /**
     * Checks whether this appointment was booked under a mobile number.
     *
     * @param mobile the number as typed, spaces allowed
     * @return true if the patient's mobile matches
     */
    public boolean isBookedUnder(String mobile) {
        return patient.getMobile().equals(Patient.normaliseMobile(mobile));
    }

    /**
     * Checks whether this appointment starts at a time.
     *
     * @param slot the time to compare with
     * @return true if the times are the same
     */
    public boolean startsAt(TimeSlot slot) {
        return timeSlot.equals(slot);
    }

    /**
     * Gives a one-line description for messages.
     *
     * @return e.g. "09:00 Romy Lovell (P0001) with Dr Elias Magee"
     */
    public String getSummary() {
        return timeSlot + " " + patient.getSummary() + " with " + professional.getName();
    }

    /**
     * Puts appointments in time order, earliest first.
     * Two appointments at the same time (with different people) count as
     * equal here. The clinic sorts with Collections.sort, which keeps equal
     * items in the order they were booked.
     *
     * @param other the appointment to compare with
     * @return negative if this is earlier, 0 if the same time, positive if later
     */
    @Override
    public int compareTo(Appointment other) {
        return timeSlot.compareTo(other.timeSlot); // TimeSlot already knows clock order, so I reuse it
    }

    /**
     * Gives the appointment as two table lines: the time and the
     * professional's full row first, then the patient on the next line.
     *
     * @return the two lines
     */
    @Override
    public String toString() {
        return Display.cell(timeSlot.toString(), Display.TIME_WIDTH)
                + professional // [Polymorphism] each professional prints its own full row
                + "\n" + Display.cell("", Display.TIME_WIDTH)
                + patient; // the patient gets its own line, under the matching columns
    }
}
