package org.lefab.billingservice.exceptions;

public class BillingAlreadyExistsException extends RuntimeException {
  public BillingAlreadyExistsException(String message) {
    super(message);
  }
}
