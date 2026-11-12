/*
 * =============================================================================
 *  System  : Arcana Health Centre - Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : Appointment.java
 *  Purpose : One booking: a patient, a health professional and a time today.
 * =============================================================================
 */

/**
 * One booking for today: a patient, a health professional and a time slot.
 * <p>
 * An appointment has-a Patient, has-a HealthProfessional and has-a TimeSlot
 * (composition). The professional is held through the abstract
 * HealthProfessional type, so one Appointment class works for every type of
 * professional, including types added in the future.
 * </p>
 * <p>
 * An appointment can never be incomplete: the constructor is private and
 * {@link #create(Patient, HealthProfessional, TimeSlot)} refuses any missing
 * part. All three parts are themselves valid by construction, and all fields
 * are final, so a booking cannot change after it is made; rescheduling
 * replaces it with a new one. Rules that need the whole day's bookings (no
 * double-booking) belong to the Clinic, which can see all appointments.
 * </p>
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Appointment implements Comparable<Appointment> {

    /** The patient who booked. */
    private final Patient patient;

    /** The professional being seen; any concrete type. */
    private final HealthProfessional professional;                  // [Polymorphism] the parent type accepts every kind of professional

    /** The start time of the appointment. */
    private final TimeSlot timeSlot;

    /**
     * Builds an appointment from parts that have been checked. Private: use create(...).
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
     * Explains which parts of an appointment are missing.
     *
     * @param patient      the patient, possibly null
     * @param professional the professional, possibly null
     * @param timeSlot     the time, possibly null
     * @return an empty text if all parts are present, otherwise what is missing
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
     * Creates an appointment, or nothing if any part is missing.
     *
     * @param patient      the patient
     * @param professional any type of health professional
     * @param timeSlot     the start time
     * @return the new appointment, or null if a part is missing
     */
    public static Appointment create(Patient patient, HealthProfessional professional,
                                     TimeSlot timeSlot) {
        if (!check(patient, professional, timeSlot).isEmpty()) {
            return null;                                            // [Encapsulation] a half-made booking can never exist
        }
        return new Appointment(patient, professional, timeSlot);
    }

    /**
     * Returns the heading rows and underline of the appointments table.
     * Each appointment takes two lines, so the heading has two title rows.
     *
     * @return three lines: professional titles, patient titles and dashes
     */
    public static String tableHeader() {
        return Display.cell("TIME", Display.TIME_WIDTH) + HealthProfessional.headerTitles() + "\n"
                + Display.cell("", Display.TIME_WIDTH) + Display.cell("PAT.", Display.ID_WIDTH)
                + Display.cell("PATIENT", Display.NAME_WIDTH) + "MOBILE" + "\n"
                + Display.dashes(Display.TIME_WIDTH) + HealthProfessional.headerRule();
    }

    /**
     * Returns the patient.
     *
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Returns the professional.
     *
     * @return the professional, as its general type
     */
    public HealthProfessional getProfessional() {
        return professional;
    }

    /**
     * Returns the time.
     *
     * @return the start time
     */
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    /**
     * Tells whether this appointment is with a given professional.
     *
     * @param other the professional to compare with
     * @return true if it is the same professional (same staff ID)
     */
    public boolean isWith(HealthProfessional other) {
        return professional.equals(other);                          // equals() compares staff IDs, so any object for that person matches
    }

    /**
     * Tells whether this appointment is with the professional holding a staff ID.
     *
     * @param professionalId the staff ID, e.g. "100101"
     * @return true if the IDs match
     */
    public boolean isWith(String professionalId) {
        return professionalId != null && professional.getId().equals(professionalId.trim());
    }

    /**
     * Tells whether this appointment belongs to a given patient.
     *
     * @param other the patient to compare with
     * @return true if it is the same person
     */
    public boolean isFor(Patient other) {
        return patient.equals(other);
    }

    /**
     * Tells whether this appointment was booked under a mobile number.
     *
     * @param mobile the number as typed, spaces allowed
     * @return true if the patient's mobile number matches
     */
    public boolean isBookedUnder(String mobile) {
        return patient.getMobile().equals(Patient.normaliseMobile(mobile));
    }

    /**
     * Tells whether this appointment starts at a given time.
     *
     * @param slot the time to compare with
     * @return true if the times are equal
     */
    public boolean startsAt(TimeSlot slot) {
        return timeSlot.equals(slot);
    }

    /**
     * Gives a one-line description used in messages.
     *
     * @return e.g. "09:00 Romy Lovell (P0001) with Dr Elias Magee"
     */
    public String getSummary() {
        return timeSlot + " " + patient.getSummary() + " with " + professional.getName();
    }

    /**
     * Orders appointments from earliest to latest. Two appointments at the
     * same time (with different professionals) compare as equal; the sort
     * used by the Clinic is stable, so they keep their booking order.
     *
     * @param other the appointment to compare with
     * @return a negative number if this is earlier, 0 if at the same time, positive if later
     */
    @Override
    public int compareTo(Appointment other) {
        return timeSlot.compareTo(other.timeSlot);                  // the time knows how to order itself, so it is reused, not repeated
    }

    /**
     * Presents the appointment in two table lines: the time and the
     * professional's full row, then the patient on the next line.
     *
     * @return the two lines of this appointment
     */
    @Override
    public String toString() {
        return Display.cell(timeSlot.toString(), Display.TIME_WIDTH)
                + professional                                      // [Polymorphism] each professional prints its own full row
                + "\n" + Display.cell("", Display.TIME_WIDTH)
                + patient;                                          // the patient goes on its own line, under the matching columns
    }
}
