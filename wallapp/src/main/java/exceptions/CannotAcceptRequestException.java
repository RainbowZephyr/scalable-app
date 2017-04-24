package exceptions;

import java.util.concurrent.RejectedExecutionException;

public class CannotAcceptRequestException extends RejectedExecutionException{
    public CannotAcceptRequestException() {
        super("Thy request shalt not passeth");
    }
}
