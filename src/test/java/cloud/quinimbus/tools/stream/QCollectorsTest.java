package cloud.quinimbus.tools.stream;

import static cloud.quinimbus.tools.stream.QCollectors.*;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QCollectorsTest {

    @Test
    public void testOneOrNone() {
        assertEquals(Optional.empty(), Stream.empty().collect(oneOrNone()));
        assertEquals(Optional.of(1), Stream.of(1).collect(oneOrNone()));
        assertEquals("The stream contains more than one value",
                assertThrows(
                        IllegalStateException.class,
                        () -> Stream.of(1, 2).collect(oneOrNone())).getMessage());
    }
    
    @Test
    public void testExactlyOne() {
        assertEquals("The stream contains no value",
                assertThrows(
                        IllegalStateException.class,
                        () -> Stream.empty().collect(exactlyOne())).getMessage());
        assertEquals(1, Stream.of(1).collect(exactlyOne()));
        assertEquals("The stream contains more than one value",
                assertThrows(
                        IllegalStateException.class,
                        () -> Stream.of(1, 2).collect(exactlyOne())).getMessage());
    }
}
