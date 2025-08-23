package com.globemed.core.model;

import java.math.BigDecimal;

public class BillItem {
    private final String description;
    private final BigDecimal unitPrice;
    private final int quantity;
    private final String serviceCode;

    private BillItem(Builder builder) {
        this.description = builder.description;
        this.unitPrice = builder.unitPrice;
        this.quantity = builder.quantity;
        this.serviceCode = builder.serviceCode;
    }

    public BigDecimal getTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // Builder Pattern
    public static class Builder {
        private String description;
        private BigDecimal unitPrice;
        private int quantity = 1;
        private String serviceCode;

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withUnitPrice(BigDecimal price) {
            this.unitPrice = price;
            return this;
        }

        public Builder withQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder withServiceCode(String code) {
            this.serviceCode = code;
            return this;
        }

        public BillItem build() {
            if (description == null || description.trim().isEmpty()) {
                throw new IllegalStateException("Description is required");
            }
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("Valid unit price is required");
            }
            if (quantity <= 0) {
                throw new IllegalStateException("Quantity must be positive");
            }
            if (serviceCode == null || serviceCode.trim().isEmpty()) {
                throw new IllegalStateException("Service code is required");
            }
            return new BillItem(this);
        }
    }

    // Getters
    public String getDescription() { return description; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public String getServiceCode() { return serviceCode; }
}
