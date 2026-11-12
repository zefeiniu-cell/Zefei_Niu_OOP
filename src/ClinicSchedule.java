/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

import java.util.ArrayList;

/**
 * The clinic's fixed set of bookable times for a day.
 *
 * Why I generate the slots instead of typing out "08:00", "08:30", ...:
 * the brief asks for a schedule that can change without breaking anything.
 * Here the whole day comes from four settings: opening time, closing time,
 * slot length and an optional lunch break. Change one setting and the list
 * of slots changes with it. Every other class only asks the schedule "is
 * this time bookable?", so nothing else needs to be edited. The Saturday
 * clinic in the demo proves it: it has a lunch break and no other code was
 * changed for it.
 *
 * How I read the brief: an appointment must finish by closing time. With
 * 08:00-16:00 and 30-minute slots, the last one starts at 15:30, because a
 * 16:00 appointment would end at 16:30, after the clinic has closed.
 * A slot that would run into lunch is not offered either.
 *
 * @author Zefei Niu
 * @version 1.0
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

    /** When the clinic opens. */
    private final TimeSlot opening;

    /** When the clinic closes. No appointment may run past this. */
    private final TimeSlot closing;

    /** Length of every appointment, in minutes. */
    private final int slotMinutes;

    /** Start of lunch, or null if there is no lunch break. */
    private final TimeSlot lunchStart;

    /** End of lunch, or null if there is no lunch break. */
    private final TimeSlot lunchEnd;

    /** Every bookable start time, earliest first. */
    private final ArrayList<TimeSlot> slots = new ArrayList<>(); // [Encapsulation] only copies ever leave

    /**
     * Builds a schedule from settings that were already checked.
     * Outside code uses create(...) or standard().
     *
     * @param opening     opening time
     * @param closing     closing time
     * @param slotMinutes slot length in minutes
     * @param lunchStart  start of lunch, or null for none
     * @param lunchEnd    end of lunch, or null for none
     */
    private ClinicSchedule(TimeSlot opening, TimeSlot closing, int slotMinutes,
                           TimeSlot lunchStart, TimeSlot lunchEnd) {
        this.opening = opening;
        this.closing = closing;
        this.slotMinutes = slotMinutes;
        this.lunchStart = lunchStart;
        this.lunchEnd = lunchEnd;
        int start = opening.getStartMinute();
        while (start + slotMinutes <= closing.getStartMinute()) { // a slot must end by closing time
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
     * @param opening     opening time, e.g. "08:00"
     * @param closing     closing time, e.g. "16:00"
     * @param slotMinutes slot length in minutes
     * @return "" if the settings work, otherwise every problem found
     */
    public static String check(String opening, String closing, int slotMinutes) {
        String problems = Validator.addProblem(labelled("Opening", TimeSlot.checkText(opening)),
                labelled("Closing", TimeSlot.checkText(closing)));
        if (slotMinutes < MIN_SLOT_MINUTES) {
            problems = Validator.addProblem(problems,
                    "Slot length must be at least " + MIN_SLOT_MINUTES + " minutes.");
        }
        if (!problems.isEmpty()) {
            return problems; // the checks below compare the times, so the times must be real first
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
     * @param opening     opening time, e.g. "08:00"
     * @param closing     closing time, e.g. "16:00"
     * @param slotMinutes slot length in minutes
     * @param lunchStart  start of lunch, e.g. "12:00"
     * @param lunchEnd    end of lunch, e.g. "13:00"
     * @return "" if the settings work, otherwise every problem found
     */
    public static String check(String opening, String closing, int slotMinutes,
                               String lunchStart, String lunchEnd) {
        String problems = check(opening, closing, slotMinutes); // the basic rules come first
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
        // the simplest exact test: build the schedule and count its slots
        if (new ClinicSchedule(open, close, slotMinutes, start, end).countSlots() == 0) {
            return "The lunch break leaves no bookable slots.";
        }
        return "";
    }

    /**
     * Creates a schedule with no lunch break.
     *
     * @param opening     opening time, e.g. "08:00"
     * @param closing     closing time, e.g. "16:00"
     * @param slotMinutes slot length in minutes
     * @return the schedule, or null if the settings don't work (check(...) says why)
     */
    public static ClinicSchedule create(String opening, String closing, int slotMinutes) {
        if (!check(opening, closing, slotMinutes).isEmpty()) {
            return null; // [Encapsulation] a schedule that can't work never turns into an object
        }
        return new ClinicSchedule(TimeSlot.parse(opening), TimeSlot.parse(closing), slotMinutes,
                null, null);
    }

    /**
     * Creates a schedule with a lunch break.
     *
     * @param opening     opening time, e.g. "08:00"
     * @param closing     closing time, e.g. "16:00"
     * @param slotMinutes slot length in minutes
     * @param lunchStart  start of lunch, e.g. "12:00"
     * @param lunchEnd    end of lunch, e.g. "13:00"
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

    /**
     * Creates the schedule from the brief: 08:00-16:00, 30-minute slots, no lunch.
     *
     * @return the standard schedule
     */
    public static ClinicSchedule standard() {
        // never null: these constants are valid
        return create(STANDARD_OPENING, STANDARD_CLOSING, STANDARD_SLOT_MINUTES);
    }

    /**
     * Gives every bookable start time, earliest first.
     *
     * @return a new list of the slots
     */
    public ArrayList<TimeSlot> getSlots() {
        return new ArrayList<>(slots); // [Encapsulation] a copy, so outside code can't add or remove slots
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
     * Gives the slot length.
     *
     * @return minutes per appointment
     */
    public int getSlotMinutes() {
        return slotMinutes;
    }

    /**
     * Checks whether there is a lunch break.
     *
     * @return true if there is one
     */
    public boolean hasLunchBreak() {
        return lunchStart != null;
    }

    /**
     * Checks whether a time is one of the bookable slots.
     *
     * @param slot the time to check, possibly null
     * @return true if it is bookable
     */
    public boolean isBookable(TimeSlot slot) {
        return slot != null && slots.contains(slot); // contains() works because TimeSlot has equals()
    }

    /**
     * Says whether a typed time can be booked, and if not, the exact reason.
     *
     * @param timeText the time as typed, e.g. "09:15"
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
        // From here on the time is real but not bookable; give the most specific reason
        // so the person knows what to change: too early, too late, lunch, or off the grid.
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
     * @param timeText the time as typed, e.g. "9:30"
     * @return the slot, or null if it is not bookable (checkBookable says why)
     */
    public TimeSlot findSlot(String timeText) {
        if (!checkBookable(timeText).isEmpty()) {
            return null;
        }
        return slots.get(slots.indexOf(TimeSlot.parse(timeText)));
    }

    /**
     * Gives the first bookable slot of the day.
     *
     * @return the earliest slot
     */
    public TimeSlot getFirstSlot() {
        return slots.get(0); // safe: create(...) refuses a schedule with no slots
    }

    /**
     * Gives the last bookable slot of the day.
     *
     * @return the latest slot
     */
    public TimeSlot getLastSlot() {
        return slots.get(slots.size() - 1);
    }

    /**
     * Lists times on one line, e.g. "08:00 08:30 09:00".
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
     * Checks whether a slot starting at this time would run into lunch.
     *
     * @param slot the slot's start time
     * @return true if any part of the slot is inside the lunch break
     */
    private boolean overlapsLunch(TimeSlot slot) {
        if (!hasLunchBreak()) {
            return false;
        }
        int start = slot.getStartMinute();
        // A partial overlap counts too: with lunch at 12:15, an 11:30-12:00 slot is fine,
        // but a 12:00-12:30 slot would eat into lunch, so it is not offered.
        return start < lunchEnd.getStartMinute()
                && start + slotMinutes > lunchStart.getStartMinute();
    }

    /**
     * Puts a label in front of a problem; an empty result stays empty.
     *
     * @param label   what the problem is about, e.g. "Opening"
     * @param problem the problem, or ""
     * @return the labelled problem, or ""
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
