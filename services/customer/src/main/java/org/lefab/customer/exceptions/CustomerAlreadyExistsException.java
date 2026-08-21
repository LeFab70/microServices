package org.lefab.customer.exceptions;

public class CustomerAlreadyExistsException extends RuntimeException {
  public CustomerAlreadyExistsException(String message) {
    super(message);
  }
}
