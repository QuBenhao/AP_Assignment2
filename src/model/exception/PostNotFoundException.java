package model.exception;
import java.lang.Exception;
public class PostNotFoundException extends Exception {
    public PostNotFoundException(String message){
        super(message);
    }
}
