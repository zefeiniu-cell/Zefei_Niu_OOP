/**
 * The prescribing methods shared by GPs, specialists and nurse practitioners.
 *
 * @author Zefei Niu
 */
public interface Prescriber {
    /** Says what this prescriber is allowed to prescribe. */
    String getPrescribingScope();

    /**
     * Writes a prescription for a patient.
     *
     * @return the prescription, or a warning saying why it wasn't written
     */
    String prescribe(String patientName, String medication);
}
