package pro.fessional.mirana.pain;

/**
 * @author trydofor
 * @since 2019-05-29
 */
public class TimeoutRuntimeException extends RuntimeException {

    public TimeoutRuntimeException() {
        super();
    }

    public TimeoutRuntimeException(String message) {
        super(message);
    }

    public TimeoutRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
