package dev.gagnon.bfpcapi.exception;

public class UserNotFoundException extends BFPCBaseException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
