package cloud.quinimbus.tools.throwing;

import java.util.LinkedHashMap;
import name.falgout.jeffrey.throwing.ThrowingBiFunction;
import name.falgout.jeffrey.throwing.ThrowingFunction;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        cloud.quinimbus.tools.throwing.ThrowingMap<java.lang.String, java.lang.Integer, cloud.quinimbus.tools.throwing.CheckedNumberFormatException> map = ThrowingMap.of(new LinkedHashMap<String, Integer>(), CheckedNumberFormatException.class);
        map.compute("1", BIPARSE);
        assertEquals(1, map.get("1"));
    }
    
    @Test
    public void testComputeException() {
        cloud.quinimbus.tools.throwing.ThrowingMap<java.lang.String, java.lang.Integer, cloud.quinimbus.tools.throwing.CheckedNumberFormatException> map = ThrowingMap.of(new LinkedHashMap<String, Integer>(), CheckedNumberFormatException.class);
        cloud.quinimbus.tools.throwing.CheckedNumberFormatException throwable = assertThrows(CheckedNumberFormatException.class, () -> map.compute("A", BIPARSE));
        assertEquals("For input string: \"A\"", throwable.getMessage());
        assertNull(map.get("A"));
    }
    
    @Test
    public void testComputeIfAbsentSuccess() throws CheckedNumberFormatException {
        cloud.quinimbus.tools.throwing.ThrowingMap<java.lang.String, java.lang.Integer, cloud.quinimbus.tools.throwing.CheckedNumberFormatException> map = ThrowingMap.of(new LinkedHashMap<String, Integer>(), CheckedNumberFormatException.class);
        map.computeIfAbsent("1", PARSE);
        map.computeIfAbsent("1", k -> {throw new CheckedNumberFormatException();});
        assertEquals(1, map.get("1"));
    }
    
    @Test
    public void testComputeIfAbsentException() {
        cloud.quinimbus.tools.throwing.ThrowingMap<java.lang.String, java.lang.Integer, cloud.quinimbus.tools.throwing.CheckedNumberFormatException> map = ThrowingMap.of(new LinkedHashMap<String, Integer>(), CheckedNumberFormatException.class);
        cloud.quinimbus.tools.throwing.CheckedNumberFormatException throwable = assertThrows(CheckedNumberFormatException.class, () -> map.computeIfAbsent("A", PARSE));
        assertEquals("For input string: \"A\"", throwable.getMessage());
        assertNull(map.get("A"));
    }
    
    @Test
    public void testComputeIfPresentSuccess() throws CheckedNumberFormatException {
        cloud.quinimbus.tools.throwing.ThrowingMap<java.lang.String, java.lang.Integer, cloud.quinimbus.tools.throwing.CheckedNumberFormatException> map = ThrowingMap.of(new LinkedHashMap<String, Integer>(), CheckedNumberFormatException.class);
        map.put("1", 13);
        map.computeIfPresent("1", BIPARSE);
        assertEquals(1, map.get("1"));
    }
    
    @Test
    public void testComputeIfPresentException() throws CheckedNumberFormatException {
        cloud.quinimbus.tools.throwing.ThrowingMap<java.lang.String, java.lang.Integer, cloud.quinimbus.tools.throwing.CheckedNumberFormatException> map = ThrowingMap.of(new LinkedHashMap<String, Integer>(), CheckedNumberFormatException.class);
        map.computeIfPresent("A", BIPARSE);
        map.put("1", 13);
        cloud.quinimbus.tools.throwing.CheckedNumberFormatException throwable = assertThrows(CheckedNumberFormatException.class, () -> map.computeIfPresent("1", (k, v) -> {throw new CheckedNumberFormatException("TEST");}));
        assertEquals("TEST", throwable.getMessage());
        assertNull(map.get("A"));
    }
}
