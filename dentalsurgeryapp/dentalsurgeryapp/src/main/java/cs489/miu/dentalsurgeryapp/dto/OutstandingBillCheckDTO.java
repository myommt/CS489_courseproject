package cs489.miu.dentalsurgeryapp.dto;

public record OutstandingBillCheckDTO(
    Integer patientId,
    boolean hasOutstandingBills
) {
}
