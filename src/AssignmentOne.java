/**
 * Entry point of Assessment 1 - Health appointment system.
 *
 * @author ZefeiNiu
 * @version 1.0
 */
public class AssignmentOne {
    
    /**         
     * Program starts here. 
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
                String[] days = {"Monday", "Wednesday", "Friday"};
        GeneralPractitioner drA =
                new GeneralPractitioner(1001, "Dr Alice Wong", days, "Gold Coast Family Clinic");
        GeneralPractitioner drB =
                new GeneralPractitioner(1002, "Dr Ben Carter", "Lismore Medical Centre");
        GeneralPractitioner drC = new GeneralPractitioner(-5, "   ", "  "); // invalid input

        System.out.println(drA);
        System.out.println(drB);
        System.out.println(drC);

        days[0] = "Sunday"; // try to change the original array from outside
        System.out.println(drA); // still shows Monday, proving the copy works

        System.out.println(drA.performDuty());
        System.out.println(drA.equals(new GeneralPractitioner(1001, "Someone Else", "Other")));
    }
}       