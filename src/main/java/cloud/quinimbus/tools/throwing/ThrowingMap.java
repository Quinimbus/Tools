package cloud.quinimbus.tools.throwing;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import name.falgout.jeffrey.throwing.ThrowingBiFunction;
import name.falgout.jeffrey.throwing.ThrowingFunction;

public class ThrowingMap<K, V, T extends Throwable> {

    private final Map<K, V> delegate;

    public ThrowingMap(Map<K, V> delegate, Class<T> throwable) {
        this.delegate = delegate;
    }

    public static <K, V, T extends Throwable> ThrowingMap<K, V, T> of(Map<K, V> delegate, Class<T> throwable) {
        return new ThrowingMap<>(delegate, throwable);
    }

    public static <K, V, T extends Throwable> ThrowingMap<K, V, T> empty(Class<T> throwable) {
        return new ThrowingMap<>(new LinkedHashMap<>(), throwable);
    }

    public V get(K key) {
        return this.delegate.get(key);
    }

    public V put(K key, V value) {
        return this.delegate.put(key, value);
    }

    public V compute(K key, ThrowingBiFunction<? super K, ? super V, ? extends V, T> remappingFunction) throws T {
        Objects.requireNonNull(remappingFunction);
        V oldValue = get(key);

        V newValue = remappingFunction.apply(key, oldValue);
        if (newValue == null) {
            if (oldValue != null || this.delegate.containsKey(key)) {
                this.delegate.remove(key);
                return null;
            } else {
                return null;
            }
        } else {
            this.delegate.put(key, newValue);
            return newValue;
        }
    }

    public V computeIfAbsent(K key, ThrowingFunction<? super K, ? extends V, T> mappingFunction) throws T {
        Objects.requireNonNull(mappingFunction);
        V v;
        if ((v = get(key)) == null) {
            V newValue;
            if ((newValue = mappingFunction.apply(key)) != null) {
                this.delegate.put(key, newValue);
                return newValue;
            }
        }

        return v;
    }

    public V computeIfPresent(K key, ThrowingBiFunction<? super K, ? super V, ? extends V, T> remappingFunction)
            throws T {
        Objects.requireNonNull(remappingFunction);
        V oldValue;
        if ((oldValue = get(key)) != null) {
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null) {
                this.delegate.put(key, newValue);
                return newValue;
            } else {
                this.delegate.remove(key);
                return null;
            }
        } else {
            return null;
        }
    }
}
