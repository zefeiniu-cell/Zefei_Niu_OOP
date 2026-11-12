/*
 * =============================================================================
 *  System  : Arcana Health Centre - Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : ClinicSchedule.java
 *  Purpose : The fixed daily set of bookable slots, built from a few settings.
 * =============================================================================
 */

import java.util.ArrayList;

/**
 * The fixed daily set of bookable appointment slots.
 * <p>
 * The brief's schedule (08:00-16:00 in 30-minute slots) is not typed out as a
 * list of times. Instead the schedule is generated from four settings:
 * opening time, closing time, slot length and an optional lunch break. To run
 * the clinic on different hours, only these settings change; every other part
 * of the system asks the schedule whether a time is bookable, so it keeps
 * working with the new slots without any change.
 * </p>
 * <p>
 * Interpretation of the brief: a slot must finish by closing time, so with
 * 08:00-16:00 and 30-minute slots the last appointment starts at 15:30.
 * A slot that would overlap the lunch break is not offered.
 * </p>
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class ClinicSchedule {

    /** Opening time of the standard schedule in the brief. */
    public static final String STANDARD_OPENING = "08:00";

    /** Closing time of the standard schedule in the brief. */
    public static final String STANDARD_CLOSING = "16:00";

    /** Slot length of the standard schedule in the brief, in minutes. */
    public static final int STANDARD_SLOT_MINUTES = 30;

    /** Shortest slot length accepted, in minutes. */
    public static final int MIN_SLOT_MINUTES = 5;

    /** Time the clinic opens. */
    private final TimeSlot opening;

    /** Time the clinic closes; no appointment may run past it. */
    private final TimeSlot closing;

    /** Length of every appointment, in minutes. */
    private final int slotMinutes;

    /** Start of the lunch break, or null if there is no break. */
    private final TimeSlot lunchStart;

    /** End of the lunch break, or null if there is no break. */
    private final TimeSlot lunchEnd;

    /** Every bookable start time, earliest first, built once. */
    private final ArrayList<TimeSlot> slots = new ArrayList<>();   // [Encapsulation] only copies ever leave this class

    /**
     * Builds a schedule from settings that have already been checked.
     * Private: use create(...) or standard().
     *
     * @param opening     opening time
     * @param closing     closing time
     * @param slotMinutes slot length in minutes
     * @param lunchStart  start of lunch, or null for no break
     * @param lunchEnd    end of lunch, or null for no break
     */
    private ClinicSchedule(TimeSlot opening, TimeSlot closing, int slotMinutes,
                           TimeSlot lunchStart, TimeSlot lunchEnd) {
        this.opening = opening;
        this.closing = closing;
        this.slotMinutes = slotMinutes;
        this.lunchStart = lunchStart;
        this.lunchEnd = lunchEnd;
        int start = opening.getStartMinute();
        while (start + slotMinutes <= closing.getStartMinute()) {  // a slot is offered only if it finishes by closing time
            TimeSlot slot = TimeSlot.ofMinutes(start);
            if (!overlapsLunch(slot)) {
                slots.add(slot);
            }
            start += slotMinutes;
        }
    }

    /**
     * Explains what, if anything, is wrong with schedule settings that have
     * no lunch break.
     *
     * @param opening     opening time, e.g. "08:00"
     * @param closing     closing time, e.g. "16:00"
     * @param slotMinutes slot length in minutes
     * @return an empty text if the settings work, otherwise every problem found
     */
    public static String check(String opening, String closing, int slotMinutes) {
        String problems = Validator.addProblem(labelled("Opening", TimeSlot.checkText(opening)),
                labelled("Closing", TimeSlot.checkText(closing)));
        if (slotMinutes < MIN_SLOT_MINUTES) {
            problems = Validator.addProblem(problems,
                    "Slot length must be at least " + MIN_SLOT_MINUTES + " minutes.");
        }
        if (!problems.isEmpty()) {
            return problems;                                        // the remaining checks need valid times to compare
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
     * Explains what, if anything, is wrong with schedule settings that
     * include a lunch break.
     *
     * @param opening     opening time, e.g. "08:00"
     * @param closing     closing time, e.g. "16:00"
     * @param slotMinutes slot length in minutes
     * @param lunchStart  start of lunch, e.g. "12:00"
     * @param lunchEnd    end of lunch, e.g. "13:00"
     * @return an empty text if the settings work, otherwise every problem found
     */
    public static String check(String opening, String closing, int slotMinutes,
                               String lunchStart, String lunchEnd) {
        String problems = check(opening, closing, slotMinutes);     // reuse the rules every schedule must meet
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
            return "The lunch break leaves no bookable slots.";    // a trial build is the simplest exact test
        }
        return "";
    }

    /**
     * Creates a schedule without a lunch break, or nothing if the settings
     * do not work.
     *
     * @param opening     opening time, e.g. "08:00"
     * @param closing     closing time, e.g. "16:00"
     * @param slotMinutes slot length in minutes
     * @return the schedule, or null if the settings are invalid (see check(...))
     */
    public static ClinicSchedule create(String opening, String closing, int slotMinutes) {
        if (!check(opening, closing, slotMinutes).isEmpty()) {
            return null;                                            // [Encapsulation] an unusable schedule never becomes an object
        }
        return new ClinicSchedule(TimeSlot.parse(opening), TimeSlot.parse(closing), slotMinutes,
                null, null);
    }

    /**
     * Creates a schedule with a lunch break, or nothing if the settings do
     * not work.
     *
     * @param opening     opening time, e.g. "08:00"
     * @param closing     closing time, e.g. "16:00"
     * @param slotMinutes slot length in minutes
     * @param lunchStart  start of lunch, e.g. "12:00"
     * @param lunchEnd    end of lunch, e.g. "13:00"
     * @return the schedule, or null if the settings are invalid (see check(...))
     */
    public static ClinicSchedule create(String opening, String closing, int slotMinutes,
                                        String lunchStart, String lunchEnd) {
        if (!check(opening, closing, slotMinutes, lunchStart, lunchEnd).isEmpty()) {
            return null;
        }
        return new ClinicSchedule(TimeSlot.parse(opening), TimeSlot.parse(closing), slotMinutes,
                TimeSlot.parse(lunchStart), TimeSlot.parse(lunchEnd));
    }

    /**
     * Creates the schedule given in the brief: 08:00-16:00, 30-minute slots,
     * no lunch break.
     *
     * @return the standard schedule
     */
    public static ClinicSchedule standard() {
        return create(STANDARD_OPENING, STANDARD_CLOSING, STANDARD_SLOT_MINUTES); // the constants are valid, so this is never null
    }

    /**
     * Returns every bookable start time, earliest first, as a new list.
     *
     * @return a copy of the slots
     */
    public ArrayList<TimeSlot> getSlots() {
        return new ArrayList<>(slots);                              // [Encapsulation] callers get a copy, so no one can add or remove a slot
    }

    /**
     * Counts the bookable slots in a day.
     *
     * @return the number of slots
     */
    public int countSlots() {
        return slots.size();
    }

    /**
     * Returns the slot length.
     *
     * @return minutes per appointment
     */
    public int getSlotMinutes() {
        return slotMinutes;
    }

    /**
     * Tells whether this schedule has a lunch break.
     *
     * @return true if there is a break
     */
    public boolean hasLunchBreak() {
        return lunchStart != null;
    }

    /**
     * Tells whether a time is one of the bookable slots.
     *
     * @param slot the time to test, possibly null
     * @return true if it is a bookable slot
     */
    public boolean isBookable(TimeSlot slot) {
        return slot != null && slots.contains(slot);                // contains() relies on TimeSlot.equals()
    }

    /**
     * Explains whether a typed time can be booked, and if not, exactly why.
     *
     * @param timeText the time as typed, e.g. "09:15"
     * @return an empty text if it is a bookable slot, otherwise the reason it is not
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
                + " minutes from " + opening + ".";                // the most specific reason helps the person fix the request
    }

    /**
     * Finds the bookable slot for a typed time.
     *
     * @param timeText the time as typed, e.g. "9:30"
     * @return the slot, or null if the time is not bookable (see checkBookable)
     */
    public TimeSlot findSlot(String timeText) {
        if (!checkBookable(timeText).isEmpty()) {
            return null;
        }
        return slots.get(slots.indexOf(TimeSlot.parse(timeText)));
    }

    /**
     * Returns the first bookable slot of the day.
     *
     * @return the earliest slot
     */
    public TimeSlot getFirstSlot() {
        return slots.get(0);                                        // create(...) guarantees at least one slot
    }

    /**
     * Returns the last bookable slot of the day.
     *
     * @return the latest slot
     */
    public TimeSlot getLastSlot() {
        return slots.get(slots.size() - 1);
    }

    /**
     * Lists times as one line, e.g. "08:00 08:30 09:00".
     *
     * @param times the times to list
     * @return the times separated by spaces
     */
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

    /**
     * Tells whether a slot starting at a given time would overlap lunch.
     *
     * @param slot the slot start time
     * @return true if any part of the slot falls inside the lunch break
     */
    private boolean overlapsLunch(TimeSlot slot) {
        if (!hasLunchBreak()) {
            return false;
        }
        int start = slot.getStartMinute();
        return start < lunchEnd.getStartMinute()
                && start + slotMinutes > lunchStart.getStartMinute(); // partial overlap counts, so a slot never runs into lunch
    }

    /**
     * Puts a label in front of a problem, leaving an empty result empty.
     *
     * @param label   what the problem is about, e.g. "Opening"
     * @param problem the problem, or an empty text
     * @return the labelled problem, or an empty text
     */
    private static String labelled(String label, String problem) {
        if (problem.isEmpty()) {
            return "";
        }
        return label + ": " + problem;
    }

    /**
     * Describes the schedule in one line.
     *
     * @return e.g. "08:00-16:00, 30-minute slots, no lunch break, 16 slots (last starts 15:30)"
     */
    @Override
    public String toString() {
        String lunch = hasLunchBreak() ? "lunch " + lunchStart + "-" + lunchEnd : "no lunch break";
        return opening + "-" + closing + ", " + slotMinutes + "-minute slots, " + lunch + ", "
                + countSlots() + " slots (last starts " + getLastSlot() + ")";
    }
}
