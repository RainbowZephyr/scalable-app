package exceptions;

import java.util.concurrent.RejectedExecutionException;

public class CannotAcceptRequestException extends RejectedExecutionException{
    public CannotAcceptRequestException() {
        super("Thy exception shalt not passeth");
    }
}
