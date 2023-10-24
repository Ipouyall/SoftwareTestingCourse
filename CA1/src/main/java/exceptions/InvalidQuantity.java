package exceptions;

import static defines.Errors.INVALID_CREDIT_RANGE;
import static defines.Errors.INVALID_QUANTITY;

public class InvalidQuantity extends Exception {
    public InvalidQuantity() {
        super(INVALID_QUANTITY);
    }
}
