package exceptions;

import static defines.Errors.INVALID_DECREASE_AMOUNT;

public class InvalidWithdrawAmount extends Exception {
    public InvalidWithdrawAmount() {
        super(INVALID_DECREASE_AMOUNT);
    }
}
