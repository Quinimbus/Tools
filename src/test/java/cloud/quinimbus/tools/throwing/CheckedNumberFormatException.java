package cloud.quinimbus.tools.throwing;

public class CheckedNumberFormatException extends Exception {
    
    public CheckedNumberFormatException() {
    }

    public CheckedNumberFormatException(String msg) {
        super(msg);
    }

    public CheckedNumberFormatException(NumberFormatException cause) {
        super(cause.getMessage(), cause);
    }
}
