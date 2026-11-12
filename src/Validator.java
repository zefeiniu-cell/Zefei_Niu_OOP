/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * Input checks that more than one class needs.
 *
 * The rule for every check: return "" if the input is fine, otherwise return
 * a sentence that says what is wrong.
 *
 * Why return a message and not throw an exception: exceptions are Module 5,
 * and a message is more useful here anyway. If a receptionist types a staff
 * ID with a letter in it, "must contain digits only" tells them exactly what
 * to fix. A patient's name and a professional's name are checked by the same
 * method, so the two can never follow different rules.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public class Validator {

    /** Longest name allowed. */
    public static final int MAX_NAME_LENGTH = 40;

    /** Longest free-text detail allowed, e.g. a specialty. */
    public static final int MAX_TEXT_LENGTH = 60;

    /**
     * Stops anyone creating a Validator object.
     * Everything here is static, so an object would have nothing to do.
     */
    private Validator() {
    }

    /**
     * Checks whether a text is missing, empty or only spaces.
     *
     * @param text the text to check
     * @return true if there is nothing in it
     */
    public static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Checks whether a text is made only of the digits 0-9.
     *
     * @param text the text to check
     * @return true if it is not empty and every character is 0-9
     */
    public static boolean isAllDigits(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            // not Character.isDigit: it also accepts digits from other scripts (e.g. Arabic-Indic)
            if (character < '0' || character > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Trims a text and turns every run of spaces inside it into one space.
     *
     * @param text the text to clean, e.g. "  Dr   Leona  Hart "
     * @return the cleaned text, e.g. "Dr Leona Hart"; "" for null
     */
    public static String tidy(String text) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        String result = "";
        boolean lastWasSpace = false;
        for (int i = 0; i < trimmed.length(); i++) {
            char character = trimmed.charAt(i);
            if (Character.isWhitespace(character)) {
                if (!lastWasSpace) {
                    result += ' '; // one space survives, so "Romy  Lovell" and "Romy Lovell" match
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
     * Removes spaces and hyphens, e.g. "0412-345 678" becomes "0412345678".
     *
     * @param text the text to compact
     * @return the text without spaces or hyphens; "" for null
     */
    public static String removeSeparators(String text) {
        if (text == null) {
            return "";
        }
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (!Character.isWhitespace(character) && character != '-') {
                result += character;
            }
        }
        return result;
    }

    /**
     * Checks a person's name. It needs at least one letter and at most 40
     * characters. Apart from letters, only spaces, - ' and . are allowed,
     * so real names like "Dr Mary-Jane O'Neil" pass.
     *
     * @param label what the name is for, used in the message, e.g. "Patient name"
     * @param name  the name to check
     * @return "" if valid, otherwise the reason
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
            return label + " must contain at least one letter."; // otherwise "--" would count as a name
        }
        return "";
    }

    /**
     * Checks a required text detail such as a specialty or a medicine.
     *
     * @param label what the text is, used in the message, e.g. "Specialty"
     * @param text  the text to check
     * @return "" if valid, otherwise the reason
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
     * Checks a code that must be an exact number of digits, like a staff ID
     * or a mobile number.
     *
     * @param label          what the code is, used in the message
     * @param code           the code to check, with separators already removed
     * @param requiredLength how many digits it must have
     * @return "" if valid, otherwise the reason
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
     * Adds one more problem to the ones found so far.
     * This is how a check can report every mistake together: a person who
     * typed a bad name and a bad phone number sees both at once, not one
     * after another.
     *
     * @param problems   the problems so far ("" if none)
     * @param newProblem the result of another check ("" if it passed)
     * @return all problems joined by a space, or "" if there are none
     */
    public static String addProblem(String problems, String newProblem) {
        if (newProblem.isEmpty()) {
            return problems;
        }
        if (problems.isEmpty()) {
            return newProblem;
        }
        return problems + " " + newProblem;
    }
}
