package org.lefab.billingservice.exceptions;

public class BillingNotFoundException extends RuntimeException {
  public BillingNotFoundException(String message) {
    super(message);
  }
}
