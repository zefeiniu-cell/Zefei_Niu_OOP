/**
 * Checks and text formatting reused by the model classes.
 * A check returns an empty string when valid, or a message describing the problem.
 *
 * @author Zefei Niu
 */
public class Validator {
    /** Longest name allowed. */
    public static final int MAX_NAME_LENGTH = 40;

    /** Longest free-text detail allowed, e.g. a specialty. */
    public static final int MAX_TEXT_LENGTH = 60;

    /*
     * Stops anyone creating a Validator object. Everything here is static, so an object would
     * have nothing to do.
     */
    private Validator() {
    }

    /** Checks whether a text is missing, empty or only spaces. */
    public static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    /** Checks whether a text is made only of the digits 0-9. */
    public static boolean isAllDigits(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);

            if (character < '0' || character > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Trims a text and turns every run of spaces inside it into one space.
     *
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
                    result += ' ';
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
     * Checks a person's name. It needs at least one letter and at most 40 characters. Apart
     * from letters, only spaces, - ' and . are allowed, so real names like "Dr Mary-Jane
     * O'Neil" pass.
     *
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
            return label + " must contain at least one letter.";
        }
        return "";
    }

    /**
     * Checks a required text detail such as a specialty or a medicine.
     *
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
     * Checks a code that must be an exact number of digits, like a staff ID or a mobile
     * number.
     *
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
     * Joins non-empty problem messages with one space.
     *
     * @return the combined problems, or an empty string when there are none
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
