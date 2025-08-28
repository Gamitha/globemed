package com.globemed.core.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

public class BillItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final String description;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final String category;
    private final String serviceCode;  // Added service code field

    private BillItem(Builder builder) {
        this.id = builder.id;
        this.description = builder.description;
        this.quantity = builder.quantity;
        this.unitPrice = builder.unitPrice;
        this.category = builder.category;
        this.serviceCode = builder.serviceCode;
    }

    public static class Builder implements Serializable {
        private static final long serialVersionUID = 1L;

        private UUID id;
        private String description;
        private int quantity;
        private BigDecimal unitPrice;
        private String category;
        private String serviceCode;  // Added service code field

        public Builder() {
            this.id = UUID.randomUUID();
        }

        public Builder withId(UUID id) {
            this.id = id;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder withUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }

        public Builder withCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder withServiceCode(String serviceCode) {  // Added service code builder method
            this.serviceCode = serviceCode;
            return this;
        }

        public BillItem build() {
            if (description == null || description.trim().isEmpty()) {
                throw new IllegalStateException("Description is required");
            }
            if (quantity <= 0) {
                throw new IllegalStateException("Quantity must be positive");
            }
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("Unit price must be positive");
            }
            if (serviceCode == null || serviceCode.trim().isEmpty()) {
                this.serviceCode = "N/A";  // Default value if not provided
            }
            return new BillItem(this);
        }
    }

    // Getters
    public UUID getId() { return id; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public String getCategory() { return category; }
    public String getServiceCode() { return serviceCode; }  // Added service code getter

    public BigDecimal getTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
