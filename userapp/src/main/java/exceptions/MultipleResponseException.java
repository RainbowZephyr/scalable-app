package exceptions;

public class MultipleResponseException extends Exception {
    public MultipleResponseException() {
        super("Thy second message shalt not passeth");
    }
}
