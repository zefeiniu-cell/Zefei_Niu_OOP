/*
 * =============================================================================
 *  System  : Arcana Health Centre - Appointment System
 *  Unit    : PROG2004 Object Oriented Programming - Assessment 1
 *  Author  : Zefei Niu (zniu16, 25259175)
 *  File    : Prescriber.java
 *  Purpose : The capability of writing prescriptions, shared across branches.
 * =============================================================================
 */

/**
 * The capability of writing prescriptions.
 * <p>
 * Prescribing does not follow one branch of the class hierarchy. Doctors
 * (GeneralPractitioner, Specialist) can prescribe, and so can a
 * NursePractitioner, which belongs to the nursing branch. Java allows only one
 * parent class, so a NursePractitioner cannot inherit from both Nurse and a
 * doctor class. An interface solves this: any class, wherever it sits in the
 * hierarchy, can promise this capability by implementing Prescriber, and code
 * that needs a prescriber can accept "any Prescriber" without knowing the
 * concrete type (programming to an interface, as in Activity 3.8). The Clinic
 * relies on exactly this when it issues a repeat prescription.
 * </p>
 *
 * @author Zefei Niu
 * @version 2.0
 */
public interface Prescriber {                                      // [Abstraction] a capability with no implementation, shared across branches

    /**
     * Describes what this prescriber is allowed to prescribe.
     *
     * @return the prescribing scope, e.g. "General medicines"
     */
    String getPrescribingScope();

    /**
     * Writes a prescription for a patient.
     *
     * @param patientName the patient the medicine is for
     * @param medication  the medicine and dose, e.g. "Amoxicillin 500 mg"
     * @return the prescription text, or a warning explaining why it was not written
     */
    String prescribe(String patientName, String medication);
}
