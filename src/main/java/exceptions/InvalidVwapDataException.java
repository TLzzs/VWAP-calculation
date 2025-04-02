package exceptions;

public class InvalidVwapDataException extends RuntimeException {
    public InvalidVwapDataException(String message) {
        super(message);
    }
}