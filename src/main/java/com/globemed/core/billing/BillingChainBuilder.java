package com.globemed.core.billing;

/**
 * Builder pattern implementation for constructing the billing chain of responsibility
 */
public class BillingChainBuilder {
    private ClaimHandler firstHandler;
    private ClaimHandler lastHandler;

    public BillingChainBuilder() {
        this.firstHandler = null;
        this.lastHandler = null;
    }

    public BillingChainBuilder addHandler(ClaimHandler handler) {
        if (firstHandler == null) {
            firstHandler = handler;
            lastHandler = handler;
        } else {
            lastHandler.setNext(handler);
            lastHandler = handler;
        }
        return this;
    }

    public ClaimHandler build() {
        return firstHandler;
    }

    public static BillingChainBuilder getDefaultChain() {
        return new BillingChainBuilder()
                .addHandler(new ValidationHandler())
                .addHandler(new CoverageVerificationHandler())
                .addHandler(new AmountApprovalHandler())
                .addHandler(new SettlementHandler());
    }
}
