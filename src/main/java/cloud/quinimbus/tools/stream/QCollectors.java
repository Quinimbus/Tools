package cloud.quinimbus.tools.stream;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collector;

public class QCollectors {

    public static <E> Collector<E, ?, Optional<E>> oneOrNone() {
        return Collector.of(
                AtomicReference<E>::new,
                (ref, e) -> {
                    if (!ref.compareAndSet(null, e)) {
                        throw new IllegalStateException("The stream contains more than one value");
                    }
                },
                (ref1, ref2) -> {
                    if (ref1.get() == null) {
                        return ref2;
                    } else if (ref2.get() != null) {
                        throw new IllegalStateException("The stream contains more than one value");
                    } else {
                        return ref1;
                    }
                },
                ref -> Optional.ofNullable(ref.get()),
                Collector.Characteristics.UNORDERED);
    }
}
