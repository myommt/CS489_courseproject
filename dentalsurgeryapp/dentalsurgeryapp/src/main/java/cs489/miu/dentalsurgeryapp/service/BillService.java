package cs489.miu.dentalsurgeryapp.service;

import java.util.List;

import cs489.miu.dentalsurgeryapp.model.Bill;
import cs489.miu.dentalsurgeryapp.dto.BillResponseDTO;

public interface BillService {
    
    Bill addNewBill(Bill bill);
    List<BillResponseDTO> getAllBills();
    List<BillResponseDTO> getAllBillsSortedByTotalCost();
    Bill getBillById(Integer id);
    Bill updateBill(Bill bill);
    boolean deleteBillById(Integer id);
    List<BillResponseDTO> getBillsByPatientId(Integer patientId);
    List<BillResponseDTO> getBillsByPaymentStatus(String paymentStatus);
    Bill findOrCreateBill(Bill bill);
    boolean hasOutstandingBills(Integer patientId);
    List<BillResponseDTO> getOutstandingBillsByPatientId(Integer patientId);
}
