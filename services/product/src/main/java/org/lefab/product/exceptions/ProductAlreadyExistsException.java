package org.lefab.product.exceptions;

public class ProductAlreadyExistsException extends RuntimeException {
  public ProductAlreadyExistsException(String message) {
    super(message);
  }
}
