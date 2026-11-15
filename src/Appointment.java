/**
 * Arcana Health Centre - Appointment System.
 *
 * One booking linking a patient, a professional and a start time.
 * Clinic checks this booking against the rest of the day.
 *
 * @author Zefei Niu
 */
public class Appointment implements Comparable<Appointment> {
    /* The patient who booked. */
    private final Patient patient;

    /* The professional being seen, of any type. */
    private final HealthProfessional professional;

    /* When the appointment starts. */
    private final TimeSlot timeSlot;

    /** Builds an appointment from parts that were already checked. Outside code uses create(...). */
    private Appointment(Patient patient, HealthProfessional professional, TimeSlot timeSlot) {
        this.patient = patient;
        this.professional = professional;
        this.timeSlot = timeSlot;
    }

    /**
     * Says which parts of an appointment are missing, if any.
     *
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
     * @return the new appointment, or null if a part is missing
     */
    public static Appointment create(Patient patient, HealthProfessional professional,
                                     TimeSlot timeSlot) {
        if (!check(patient, professional, timeSlot).isEmpty()) {
            return null;
        }
        return new Appointment(patient, professional, timeSlot);
    }

    /**
     * Gives the heading for the appointments table. Each appointment takes two lines (the
     * professional, then the patient), so the heading has two lines of titles too.
     */
    public static String tableHeader() {
        return Display.cell("TIME", Display.TIME_WIDTH) + HealthProfessional.headerTitles() + "\n"
                + Display.cell("", Display.TIME_WIDTH) + Display.cell("PAT.", Display.ID_WIDTH)
                + Display.cell("PATIENT", Display.NAME_WIDTH) + "MOBILE" + "\n"
                + Display.dashes(Display.TIME_WIDTH) + HealthProfessional.headerRule();
    }

    /** Gives the patient. */
    public Patient getPatient() {
        return patient;
    }

    /** Gives the professional. */
    public HealthProfessional getProfessional() {
        return professional;
    }

    /** Gives the start time. */
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    /** Checks whether this appointment is with a professional. */
    public boolean isWith(HealthProfessional other) {
        return professional.equals(other);
    }

    /** Checks whether this appointment is with the professional who has a staff ID. */
    public boolean isWith(String professionalId) {
        return professionalId != null && professional.getId().equals(professionalId.trim());
    }

    /** Checks whether this appointment belongs to a patient. */
    public boolean isFor(Patient other) {
        return patient.equals(other);
    }

    /** Checks whether this appointment was booked under a mobile number. */
    public boolean isBookedUnder(String mobile) {
        return patient.getMobile().equals(Patient.normaliseMobile(mobile));
    }

    /** Checks whether this appointment starts at a time. */
    public boolean startsAt(TimeSlot slot) {
        return timeSlot.equals(slot);
    }

    /** Gives a one-line description for messages. */
    public String getSummary() {
        return timeSlot + " " + patient.getSummary() + " with " + professional.getName();
    }

    /** Orders bookings by start time. Equal times compare as zero. */
    @Override
    public int compareTo(Appointment other) {
        return timeSlot.compareTo(other.timeSlot);
    }

    /**
     * Gives the appointment as two table lines: the time and the professional's full row
     * first, then the patient on the next line.
     */
    @Override
    public String toString() {
        return Display.cell(timeSlot.toString(), Display.TIME_WIDTH)
                + professional
                + "\n" + Display.cell("", Display.TIME_WIDTH)
                + patient;
    }
}
