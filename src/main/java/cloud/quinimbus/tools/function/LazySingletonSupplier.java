package cloud.quinimbus.tools.function;

import cloud.quinimbus.tools.lang.TypeRef;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/// A special variant of [java.util.function.Supplier] what only will call its source supplier lazily once and cache
/// that value.
///
/// @param <T> The type of the supplier
/// @since 0.2
public class LazySingletonSupplier<T> implements Supplier<T> {

    private final Supplier<T> source;
    private final Class<? extends T> cls;
    private final Type type;
    private final AtomicReference<T> value;

    /// Create a new instance, the source supplier will not be called in the constructor but lazily on a call to
    /// [#get]. This constructor is for raw classes only, use [#LazySingletonSupplier(Supplier, TypeRef)] if the
    /// supplier is using a generic type.
    ///
    /// @param source The source supplier
    /// @param type The type of the supplier
    public LazySingletonSupplier(Supplier<T> source, Class<? extends T> type) {
        this.source = source;
        this.cls = type;
        this.type = null;
        this.value = new AtomicReference<>();
    }

    /// Create a new instance, the source supplier will not be called in the constructor but lazily on a call to
    /// [#get]
    ///
    /// @param source The source supplier
    /// @param type The type of the supplier
    public LazySingletonSupplier(Supplier<T> source, TypeRef<T> type) {
        this.source = source;
        this.cls = null;
        this.type = type.getType();
        this.value = new AtomicReference<>();
    }

    /// Get the value of the source supplier. This method will call the source supplier just one time if it did not
    /// already happen.
    ///
    /// @return The value supplied by the source supplier
    @Override
    public T get() {
        return value.updateAndGet(c -> c != null ? c : source.get());
    }

    /// Return the type this supplier will return.
    ///
    /// @return The type of this supplier
    public Type getType() {
        if (cls != null) {
            return cls;
        }
        return type;
    }

    /// Return the raw type this supplier will return if available
    ///
    /// @return The raw type of this supplier
    public Class<? extends T> getRawType() {
        if (cls != null) {
            return cls;
        }
        return switch (type) {
            case Class c -> c;
            case ParameterizedType p when p.getRawType() instanceof Class<?> c -> (Class<? extends T>) c;
            default -> null;
        };
    }
}
