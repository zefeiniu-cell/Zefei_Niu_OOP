/*
 * =============================================================================
 *  System  : Health Clinic Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : Validator.java
 *  Purpose : Input rules shared by every class that accepts text from outside.
 * =============================================================================
 */

/**
 * Input rules shared by every class that accepts text from outside.
 * <p>
 * All check methods follow one convention: they return an empty text when the
 * input is acceptable, and otherwise a sentence explaining what is wrong.
 * Returning the reason (instead of throwing an exception, which is Module 5)
 * lets the caller both refuse bad input and tell the user exactly why.
 * Keeping the rules here means a person's name, for example, is checked in
 * exactly the same way for a professional and for a patient.
 * </p>
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Validator {

    /** Longest person name accepted, in characters. */
    public static final int MAX_NAME_LENGTH = 60;

    /** Longest free-text detail accepted (e.g. a specialty), in characters. */
    public static final int MAX_TEXT_LENGTH = 80;

    /**
     * Prevents objects of this class from being created.
     * Validator only offers static helpers, so an instance would have no purpose.
     */
    private Validator() {
    }

    /**
     * Tells whether a text is missing, empty or only spaces.
     *
     * @param text the text to test
     * @return true if there is nothing meaningful in the text
     */
    public static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Tells whether a text is made only of the digits 0-9.
     *
     * @param text the text to test
     * @return true if the text is not empty and every character is 0-9
     */
    public static boolean isAllDigits(String text) {
        if (text == null || text.isEmpty()) {                       // [Guard] no digits at all
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (character < '0' || character > '9') {               // only plain 0-9 count
                return false;
            }
        }
        return true;
    }

    /**
     * Cleans a text for storage: trims both ends and turns every run of
     * spaces or tabs inside it into a single space.
     *
     * @param text the text to clean, e.g. "  Dr   Alice  Wong "
     * @return the cleaned text, e.g. "Dr Alice Wong"; an empty text for null
     */
    public static String tidy(String text) {
        if (text == null) {                                         // [Guard] treat missing as empty
            return "";
        }
        String trimmed = text.trim();
        String result = "";
        boolean lastWasSpace = false;
        for (int i = 0; i < trimmed.length(); i++) {
            char character = trimmed.charAt(i);
            if (Character.isWhitespace(character)) {
                if (!lastWasSpace) {
                    result += ' ';                                  // keep only the first space of a run
                }
                lastWasSpace = true;
            } else {
                result += character;
                lastWasSpace = false;
            }
        }
        return result;
    }

    /**
     * Removes every space from a text, e.g. "0412 345 678" becomes "0412345678".
     * Used for codes that people often type with spaces for readability.
     *
     * @param text the text to compact
     * @return the text without spaces; an empty text for null
     */
    public static String removeSpaces(String text) {
        if (text == null) {                                         // [Guard] treat missing as empty
            return "";
        }
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (!Character.isWhitespace(character)) {
                result += character;
            }
        }
        return result;
    }

    /**
     * Checks a person's name. It is required, at most 60 characters, must
     * contain a letter, and may otherwise only use spaces, hyphens,
     * apostrophes and full stops (so "Dr Mary-Jane O'Neil" is accepted).
     *
     * @param label what the name belongs to, used in the message, e.g. "Patient name"
     * @param name  the name to check
     * @return an empty text if valid, otherwise the reason it is not
     */
    public static String checkPersonName(String label, String name) {
        if (isBlank(name)) {
            return label + " is required.";
        }
        String text = tidy(name);
        if (text.length() > MAX_NAME_LENGTH) {
            return label + " must be at most " + MAX_NAME_LENGTH + " characters long.";
        }
        boolean hasLetter = false;
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (Character.isLetter(character)) {
                hasLetter = true;
            } else if (character != ' ' && character != '-'
                    && character != '\'' && character != '.') {
                return label + " contains a character that is not allowed: '" + character + "'.";
            }
        }
        if (!hasLetter) {
            return label + " must contain at least one letter.";
        }
        return "";
    }

    /**
     * Checks a required free-text detail such as a specialty or a medicine.
     *
     * @param label what the text describes, used in the message, e.g. "Specialty"
     * @param text  the text to check
     * @return an empty text if valid, otherwise the reason it is not
     */
    public static String checkRequiredText(String label, String text) {
        if (isBlank(text)) {
            return label + " is required.";
        }
        if (tidy(text).length() > MAX_TEXT_LENGTH) {
            return label + " must be at most " + MAX_TEXT_LENGTH + " characters long.";
        }
        return "";
    }

    /**
     * Checks a code that must be an exact number of digits, such as a
     * staff ID or a mobile number.
     *
     * @param label          what the code is, used in the message, e.g. "Professional ID"
     * @param code           the code to check (it must not contain spaces)
     * @param requiredLength how many digits the code must have
     * @return an empty text if valid, otherwise the reason it is not
     */
    public static String checkDigitCode(String label, String code, int requiredLength) {
        if (isBlank(code)) {
            return label + " is required.";
        }
        String text = code.trim();
        if (!isAllDigits(text)) {
            return label + " must contain digits only (got \"" + text + "\").";
        }
        if (text.length() != requiredLength) {
            return label + " must be exactly " + requiredLength
                    + " digits (got " + text.length() + ").";
        }
        return "";
    }

    /**
     * Adds one more problem to a list of problems found so far, so that a
     * caller can report every mistake at once instead of only the first.
     *
     * @param problems   the problems found so far (empty if none)
     * @param newProblem the result of another check (empty if that check passed)
     * @return all problems joined by a space, or an empty text if there are none
     */
    public static String addProblem(String problems, String newProblem) {
        if (newProblem.isEmpty()) {                                 // that check passed
            return problems;
        }
        if (problems.isEmpty()) {                                   // first problem found
            return newProblem;
        }
        return problems + " " + newProblem;
    }
}
