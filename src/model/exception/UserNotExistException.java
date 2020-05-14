package model.exception;

public class UserNotExistException extends AlertException{
    public UserNotExistException(String message) {
        super(message);
    }
}
