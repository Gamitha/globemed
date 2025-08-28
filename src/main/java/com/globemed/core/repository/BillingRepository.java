package com.globemed.core.repository;

import com.globemed.core.billing.*;
import com.globemed.core.model.Bill;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class BillingRepository {
    private static final Logger logger = LoggerFactory.getLogger(BillingRepository.class);
    private final DB db;
    private final BTreeMap<String, byte[]> billMap;
    private final BTreeMap<String, byte[]> claimMap;
    private final ClaimHandler claimProcessingChain;

    public BillingRepository() {
        this.db = DatabaseConfig.getDatabase();
        this.billMap = db.treeMap("bills")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.BYTE_ARRAY)
                .createOrOpen();
        this.claimMap = db.treeMap("claims")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.BYTE_ARRAY)
                .createOrOpen();

        // Initialize the claim processing chain
        this.claimProcessingChain = new ValidationHandler();
        ClaimHandler coverageHandler = new CoverageVerificationHandler();
        ClaimHandler amountHandler = new AmountApprovalHandler();
        ClaimHandler settlementHandler = new SettlementHandler();

        this.claimProcessingChain.setNext(coverageHandler);
        coverageHandler.setNext(amountHandler);
        amountHandler.setNext(settlementHandler);
    }

    public void saveBill(Bill bill) {
        try {
            byte[] serializedBill = serializeObject(bill);
            billMap.put(bill.getId().toString(), serializedBill);
            db.commit();
            logger.info("Bill saved successfully: {}", bill.getId());
        } catch (Exception e) {
            db.rollback();
            logger.error("Failed to save bill: {}", bill.getId(), e);
            throw new RuntimeException("Failed to save bill", e);
        }
    }

    public void saveInsuranceClaim(InsuranceClaim claim) {
        try {
            byte[] serializedClaim = serializeObject(claim);
            claimMap.put(claim.getId().toString(), serializedClaim);
            db.commit();
            logger.info("Insurance claim saved successfully: {}", claim.getId());
        } catch (Exception e) {
            db.rollback();
            logger.error("Failed to save insurance claim: {}", claim.getId(), e);
            throw new RuntimeException("Failed to save insurance claim", e);
        }
    }

    public Optional<Bill> findBillById(String id) {
        try {
            byte[] serializedBill = billMap.get(id);
            if (serializedBill == null) {
                return Optional.empty();
            }
            Bill bill = (Bill) deserializeObject(serializedBill);
            return Optional.of(bill);
        } catch (Exception e) {
            logger.error("Failed to retrieve bill: {}", id, e);
            throw new RuntimeException("Failed to retrieve bill", e);
        }
    }

    public Optional<InsuranceClaim> findClaimById(String id) {
        byte[] data = claimMap.get(id);
        if (data == null) {
            return Optional.empty();
        }
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return Optional.of((InsuranceClaim) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error retrieving claim {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to retrieve claim", e);
        }
    }

    public List<Bill> findAllBills() {
        List<Bill> bills = new ArrayList<>();
        try {
            for (byte[] serializedBill : billMap.values()) {
                bills.add((Bill) deserializeObject(serializedBill));
            }
            return bills;
        } catch (Exception e) {
            logger.error("Failed to retrieve all bills", e);
            throw new RuntimeException("Failed to retrieve all bills", e);
        }
    }

    public List<InsuranceClaim> findAllClaims() {
        List<InsuranceClaim> claims = new ArrayList<>();
        for (String id : claimMap.keySet()) {
            findClaimById(id).ifPresent(claims::add);
        }
        return claims;
    }

    public void deleteBill(String id) {
        try {
            billMap.remove(id);
            db.commit();
            logger.info("Bill deleted successfully: {}", id);
        } catch (Exception e) {
            db.rollback();
            logger.error("Failed to delete bill: {}", id, e);
            throw new RuntimeException("Failed to delete bill", e);
        }
    }

    public void deleteClaim(String id) {
        claimMap.remove(id);
        db.commit();
        logger.info("Deleted claim: {}", id);
    }

    public List<Bill> getBillsByPatient(UUID patientId) {
        return findAllBills().stream()
                .filter(bill -> bill.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    private byte[] serializeObject(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return bos.toByteArray();
        }
    }

    private Object deserializeObject(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }
}
