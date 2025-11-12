package dev.gagnon.bfpcapi.exception;

public class ResourceNotFoundException extends BFPCBaseException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
