package org.rescript.symbol;

import org.rescript.value.Value;

public interface Symbol {

  Value get();

  /**
   * increment number and return new Value or throw exception if operation is not supported or value is not a number
   */
  Value inc();

  /**
   * decrement number and return new Value or throw exception if operation is not supported or value is not a number
   */
  Value dec();
}
