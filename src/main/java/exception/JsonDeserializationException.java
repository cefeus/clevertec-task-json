package exception;

public class JsonDeserializationException extends RuntimeException {

    public JsonDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonDeserializationException(String message) {
        super(message);
    }
}
