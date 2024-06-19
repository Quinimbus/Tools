package cloud.quinimbus.tools.throwing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import name.falgout.jeffrey.throwing.ThrowingBiFunction;
import name.falgout.jeffrey.throwing.ThrowingFunction;
import org.junit.jupiter.api.Test;

public class ThrowingMapTest {

    public static ThrowingFunction<String, Integer, CheckedNumberFormatException> PARSE = k -> {
        try {
            return Integer.parseInt(k);
        } catch (NumberFormatException ex) {
            throw new CheckedNumberFormatException(ex);
        }
    };

    public static ThrowingBiFunction<String, Integer, Integer, CheckedNumberFormatException> BIPARSE = (k, v) -> {
        try {
            return Integer.parseInt(k);
        } catch (NumberFormatException ex) {
            throw new CheckedNumberFormatException(ex);
        }
    };

    @Test
    public void testComputeSuccess() throws CheckedNumberFormatException {
        var map = ThrowingMap.of(new LinkedHashMap<String, Integer>(), CheckedNumberFormatException.class);
        map.compute("1", BIPARSE);
        assertEquals(1, map.get("1"));
    }

    @Test
    public void testComputeException() {
        var map = ThrowingMap.of(new LinkedHashMap<String, Integer>(), CheckedNumberFormatException.class);
        var throwable = assertThrows(CheckedNumberFormatException.class, () -> map.compute("A", BIPARSE));
        assertEquals("For input string: \"A\"", throwable.getMessage());
        assertNull(map.get("A"));
    }

    @Test
    public void testComputeIfAbsentSuccess() throws CheckedNumberFormatException {
        var map = ThrowingMap.of(new LinkedHashMap<String, Integer>(), CheckedNumberFormatException.class);
        map.computeIfAbsent("1", PARSE);
        map.computeIfAbsent("1", k -> {
            throw new CheckedNumberFormatException();
        });
        assertEquals(1, map.get("1"));
    }

    @Test
    public void testComputeIfAbsentException() {
        var map = ThrowingMap.of(new LinkedHashMap<String, Integer>(), CheckedNumberFormatException.class);
        var throwable = assertThrows(CheckedNumberFormatException.class, () -> map.computeIfAbsent("A", PARSE));
        assertEquals("For input string: \"A\"", throwable.getMessage());
        assertNull(map.get("A"));
    }

    @Test
    public void testComputeIfPresentSuccess() throws CheckedNumberFormatException {
        var map = ThrowingMap.of(new LinkedHashMap<String, Integer>(), CheckedNumberFormatException.class);
        map.put("1", 13);
        map.computeIfPresent("1", BIPARSE);
        assertEquals(1, map.get("1"));
    }

    @Test
    public void testComputeIfPresentException() throws CheckedNumberFormatException {
        var map = ThrowingMap.of(new LinkedHashMap<String, Integer>(), CheckedNumberFormatException.class);
        map.computeIfPresent("A", BIPARSE);
        map.put("1", 13);
        var throwable = assertThrows(
                CheckedNumberFormatException.class,
                () -> map.computeIfPresent("1", (k, v) -> {
                    throw new CheckedNumberFormatException("TEST");
                }));
        assertEquals("TEST", throwable.getMessage());
        assertNull(map.get("A"));
    }
}
