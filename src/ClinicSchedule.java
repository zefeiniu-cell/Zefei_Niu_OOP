import java.util.ArrayList;

/**
 * Generates the day's start times from the opening hours and slot length.
 * Each appointment must finish by closing time and avoid any lunch break.
 *
 * @author Zefei Niu
 */
public class ClinicSchedule {
    /** Opening time in the brief. */
    public static final String STANDARD_OPENING = "08:00";

    /** Closing time in the brief. */
    public static final String STANDARD_CLOSING = "16:00";

    /** Slot length in the brief, in minutes. */
    public static final int STANDARD_SLOT_MINUTES = 30;

    /** Shortest slot length allowed, in minutes. */
    public static final int MIN_SLOT_MINUTES = 5;

    /* When the clinic opens. */
    private final TimeSlot opening;

    /* When the clinic closes. No appointment may run past this. */
    private final TimeSlot closing;

    /* Length of every appointment, in minutes. */
    private final int slotMinutes;

    /* Start of lunch, or null if there is no lunch break. */
    private final TimeSlot lunchStart;

    /* End of lunch, or null if there is no lunch break. */
    private final TimeSlot lunchEnd;

    /* Every bookable start time, earliest first. */
    private final ArrayList<TimeSlot> slots = new ArrayList<>();

    /*
     * Builds a schedule from settings that were already checked. Outside code uses create(...)
     * or standard().
     */
    private ClinicSchedule(TimeSlot opening, TimeSlot closing, int slotMinutes,
                           TimeSlot lunchStart, TimeSlot lunchEnd) {
        this.opening = opening;
        this.closing = closing;
        this.slotMinutes = slotMinutes;
        this.lunchStart = lunchStart;
        this.lunchEnd = lunchEnd;
        int start = opening.getStartMinute();
        // Include a start only when the whole appointment fits.
        while (start + slotMinutes <= closing.getStartMinute()) {
            TimeSlot slot = TimeSlot.ofMinutes(start);
            if (!overlapsLunch(slot)) {
                slots.add(slot);
            }
            start += slotMinutes;
        }
    }

    /**
     * Says what is wrong with schedule settings that have no lunch break.
     *
     * @return "" if the settings work, otherwise every problem found
     */
    public static String check(String opening, String closing, int slotMinutes) {
        String problems = Validator.addProblem(labelled("Opening", TimeSlot.checkText(opening)),
                labelled("Closing", TimeSlot.checkText(closing)));
        if (slotMinutes < MIN_SLOT_MINUTES || slotMinutes > TimeSlot.MINUTES_PER_DAY) {
            problems = Validator.addProblem(problems,
                    "Slot length must be between " + MIN_SLOT_MINUTES + " and "
                    + TimeSlot.MINUTES_PER_DAY + " minutes.");
        }
        if (!problems.isEmpty()) {
            return problems;
        }
        TimeSlot open = TimeSlot.parse(opening);
        TimeSlot close = TimeSlot.parse(closing);
        if (!open.isBefore(close)) {
            return "Opening time must be before closing time.";
        }
        if (open.getStartMinute() + slotMinutes > close.getStartMinute()) {
            return "No appointment fits between opening and closing time.";
        }
        return "";
    }

    /**
     * Says what is wrong with schedule settings that include a lunch break.
     *
     * @return "" if the settings work, otherwise every problem found
     */
    public static String check(String opening, String closing, int slotMinutes,
                               String lunchStart, String lunchEnd) {
        String problems = check(opening, closing, slotMinutes);
        problems = Validator.addProblem(problems, labelled("Lunch start", TimeSlot.checkText(lunchStart)));
        problems = Validator.addProblem(problems, labelled("Lunch end", TimeSlot.checkText(lunchEnd)));
        if (!problems.isEmpty()) {
            return problems;
        }
        TimeSlot open = TimeSlot.parse(opening);
        TimeSlot close = TimeSlot.parse(closing);
        TimeSlot start = TimeSlot.parse(lunchStart);
        TimeSlot end = TimeSlot.parse(lunchEnd);
        if (!start.isBefore(end)) {
            return "Lunch must start before it ends.";
        }
        if (start.isBefore(open) || close.isBefore(end)) {
            return "Lunch must fall within opening hours.";
        }

        if (new ClinicSchedule(open, close, slotMinutes, start, end).countSlots() == 0) {
            return "The lunch break leaves no bookable slots.";
        }
        return "";
    }

    /**
     * Creates a schedule with no lunch break.
     *
     * @return the schedule, or null if the settings don't work (check(...) says why)
     */
    public static ClinicSchedule create(String opening, String closing, int slotMinutes) {
        if (!check(opening, closing, slotMinutes).isEmpty()) {
            return null;
        }
        return new ClinicSchedule(TimeSlot.parse(opening), TimeSlot.parse(closing), slotMinutes,
                null, null);
    }

    /**
     * Creates a schedule with a lunch break.
     *
     * @return the schedule, or null if the settings don't work (check(...) says why)
     */
    public static ClinicSchedule create(String opening, String closing, int slotMinutes,
                                        String lunchStart, String lunchEnd) {
        if (!check(opening, closing, slotMinutes, lunchStart, lunchEnd).isEmpty()) {
            return null;
        }
        return new ClinicSchedule(TimeSlot.parse(opening), TimeSlot.parse(closing), slotMinutes,
                TimeSlot.parse(lunchStart), TimeSlot.parse(lunchEnd));
    }

    /** Creates the schedule from the brief: 08:00-16:00, 30-minute slots, no lunch. */
    public static ClinicSchedule standard() {
        return create(STANDARD_OPENING, STANDARD_CLOSING, STANDARD_SLOT_MINUTES);
    }

    /** Gives every bookable start time, earliest first. */
    public ArrayList<TimeSlot> getSlots() {
        return new ArrayList<>(slots);
    }

    /** Counts the bookable slots in a day. */
    public int countSlots() {
        return slots.size();
    }

    /** Gives the slot length. */
    public int getSlotMinutes() {
        return slotMinutes;
    }

    /** Checks whether there is a lunch break. */
    public boolean hasLunchBreak() {
        return lunchStart != null;
    }

    /** Checks whether a time is one of the bookable slots. */
    public boolean isBookable(TimeSlot slot) {
        return slot != null && slots.contains(slot);
    }

    /**
     * Says whether a typed time can be booked, and if not, the exact reason.
     *
     * @return "" if bookable, otherwise the reason
     */
    public String checkBookable(String timeText) {
        String problem = TimeSlot.checkText(timeText);
        if (!problem.isEmpty()) {
            return problem;
        }
        TimeSlot slot = TimeSlot.parse(timeText);
        if (slots.contains(slot)) {
            return "";
        }

        if (slot.isBefore(opening)) {
            return slot + " is before the clinic opens at " + opening + ".";
        }
        if (slot.getStartMinute() + slotMinutes > closing.getStartMinute()) {
            return slot + " is too late: the clinic closes at " + closing
                    + ", so the last appointment starts at " + getLastSlot() + ".";
        }
        if (overlapsLunch(slot)) {
            return slot + " falls in the lunch break (" + lunchStart + "-" + lunchEnd + ").";
        }
        return slot + " is not a start time: appointments start every " + slotMinutes
                + " minutes from " + opening + ".";
    }

    /**
     * Finds the bookable slot for a typed time.
     *
     * @return the slot, or null if it is not bookable (checkBookable says why)
     */
    public TimeSlot findSlot(String timeText) {
        if (!checkBookable(timeText).isEmpty()) {
            return null;
        }
        return slots.get(slots.indexOf(TimeSlot.parse(timeText)));
    }

    /** Gives the first bookable slot of the day. */
    public TimeSlot getFirstSlot() {
        return slots.get(0);
    }

    /** Gives the last bookable slot of the day. */
    public TimeSlot getLastSlot() {
        return slots.get(slots.size() - 1);
    }

    /** Lists times on one line, e.g. "08:00 08:30 09:00". */
    public static String listTimes(ArrayList<TimeSlot> times) {
        String text = "";
        for (TimeSlot time : times) {
            if (!text.isEmpty()) {
                text += " ";
            }
            text += time;
        }
        return text;
    }

    /* Checks whether a slot starting at this time would run into lunch. */
    private boolean overlapsLunch(TimeSlot slot) {
        if (!hasLunchBreak()) {
            return false;
        }
        int start = slot.getStartMinute();

        // A partial overlap with lunch also excludes the slot.
        return start < lunchEnd.getStartMinute()
                && start + slotMinutes > lunchStart.getStartMinute();
    }

    /*
     * Puts a label in front of a problem; an empty result stays empty.
     */
    private static String labelled(String label, String problem) {
        if (problem.isEmpty()) {
            return "";
        }
        return label + ": " + problem;
    }

    /** Describes the schedule in one line. */
    @Override
    public String toString() {
        String lunch = hasLunchBreak() ? "lunch " + lunchStart + "-" + lunchEnd : "no lunch break";
        return opening + "-" + closing + ", " + slotMinutes + "-minute slots, " + lunch + ", "
                + countSlots() + " slots (last starts " + getLastSlot() + ")";
    }
}
