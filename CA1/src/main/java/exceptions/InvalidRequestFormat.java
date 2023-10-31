package exceptions;

import static defines.Errors.INVALID_REQUEST_FORMAT;

public class InvalidRequestFormat extends Exception {
    public InvalidRequestFormat() {super(INVALID_REQUEST_FORMAT);}
}
