package cloud.quinimbus.tools.throwing;

import name.falgout.jeffrey.throwing.ThrowingFunction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ThrowingOptionalTest {
    
    public static ThrowingFunction<String, Integer, CheckedNumberFormatException> PARSE = k -> {
        try {
            return Integer.parseInt(k);
        } catch (NumberFormatException ex) {
            throw new CheckedNumberFormatException(ex);
        }
    };
    
    @Test
    public void testMapSuccess() throws CheckedNumberFormatException {
        var res = ThrowingOptional.of("1", CheckedNumberFormatException.class)
                .map(PARSE)
                .get();
    }
    
    @Test
    public void testMapException() throws CheckedNumberFormatException {
        var res = ThrowingOptional.of("1", CheckedNumberFormatException.class)
                .map(PARSE)
                .get();
    }
}
