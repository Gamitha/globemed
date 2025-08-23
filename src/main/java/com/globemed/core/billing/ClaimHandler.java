package com.globemed.core.billing;

public abstract class ClaimHandler {
    protected ClaimHandler nextHandler;

    public void setNext(ClaimHandler handler) {
        this.nextHandler = handler;
    }

    public abstract void processRequest(InsuranceClaim claim);
}
