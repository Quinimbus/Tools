package cloud.quinimbus.tools.function;

import cloud.quinimbus.tools.lang.TypeRef;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LazySingletonSupplierTest {

    @Test
    public void testSimple() {
        var supplier = new LazySingletonSupplier<>(LocalDateTime::now, LocalDateTime.class);
        assertTrue(supplier.get().equals(supplier.get()));
    }

    @Test
    public void testGeneric() {
        var supplier = new LazySingletonSupplier<>(
                () -> Optional.of(LocalDateTime.now()), new TypeRef<Optional<LocalDateTime>>() {});
        assertTrue(supplier.get().orElseThrow().equals(supplier.get().orElseThrow()));
    }
}
