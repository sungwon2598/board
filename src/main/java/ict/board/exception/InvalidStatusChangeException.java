package ict.board.exception;

public class InvalidStatusChangeException extends RuntimeException {
    public InvalidStatusChangeException(String message) {
        super(message);
    }
}