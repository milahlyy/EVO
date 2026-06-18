package exception;

public class EventException extends Exception {
    public EventException(String message) {
        //ngirim pesan error ke kelas induk (exception)
        super(message);
    }
}