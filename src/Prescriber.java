/*
 * Arcana Health Centre - Appointment System
 * PROG2004 Object Oriented Programming, Assessment 1
 * Author: Zefei Niu (zniu16)
 */

/**
 * Anyone who can write prescriptions.
 *
 * Why an interface and not a method in a Doctor parent class: in a real
 * clinic, GPs and specialists can prescribe, and so can a nurse practitioner,
 * who belongs to the nursing branch. A class can only extend one parent, so
 * a nurse practitioner can't be both a Nurse and a Doctor. An interface fixes
 * this: a class in any branch can say "I can prescribe" by implementing it.
 *
 * It also means code that only needs a prescriber can depend on Prescriber
 * alone, like the ServiceStation in Activity 3.8. The clinic's repeat
 * prescription does exactly that, and never needs to know the real class.
 *
 * @author Zefei Niu
 * @version 1.0
 */
public interface Prescriber { // [Abstraction] a capability, not a place in the class tree

    /**
     * Says what this prescriber is allowed to prescribe.
     *
     * @return e.g. "General medicines for everyday conditions"
     */
    String getPrescribingScope();

    /**
     * Writes a prescription for a patient.
     *
     * @param patientName the patient
     * @param medication  the medicine and dose, e.g. "Amoxicillin 500 mg"
     * @return the prescription, or a warning saying why it wasn't written
     */
    String prescribe(String patientName, String medication);
}
