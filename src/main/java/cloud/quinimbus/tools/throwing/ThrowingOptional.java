package cloud.quinimbus.tools.throwing;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import name.falgout.jeffrey.throwing.ThrowingConsumer;
import name.falgout.jeffrey.throwing.ThrowingFunction;
import name.falgout.jeffrey.throwing.ThrowingPredicate;
import name.falgout.jeffrey.throwing.ThrowingRunnable;
import name.falgout.jeffrey.throwing.ThrowingSupplier;
import name.falgout.jeffrey.throwing.stream.ThrowingStream;

public final class ThrowingOptional<T, X extends Throwable> {

    private final T value;

    private final X throwable;

    private final Class<T> expectedValueClass;

    private final Class<X> expectedThrowableClass;

    private static final ThrowingOptional<?, ?> EMPTY = new ThrowingOptional<>();

    public static <T, X extends Throwable> ThrowingOptional<T, X> empty() {
        @SuppressWarnings("unchecked")
        ThrowingOptional<T, X> t = (ThrowingOptional<T, X>) EMPTY;
        return t;
    }

    private ThrowingOptional() {
        this.value = null;
        this.throwable = null;
        this.expectedValueClass = null;
        this.expectedThrowableClass = null;
    }

    private ThrowingOptional(T value, Class<X> expectedThrowableClass) {
        this.value = value;
        this.throwable = null;
        this.expectedValueClass = (Class<T>) value.getClass();
        this.expectedThrowableClass = expectedThrowableClass;
    }

    private ThrowingOptional(X throwable, Class<T> expectedValueClass) {
        this.value = null;
        this.throwable = throwable;
        this.expectedValueClass = expectedValueClass;
        this.expectedThrowableClass = (Class<X>) throwable.getClass();
    }

    public static <T, X extends Throwable> ThrowingOptional<T, X> of(T value, Class<X> expectedThrowableClass) {
        return new ThrowingOptional<>(Objects.requireNonNull(value), expectedThrowableClass);
    }

    public static <T, X extends Throwable> ThrowingOptional<T, X> ofNullable(T value, Class<X> expectedThrowableClass) {
        return value == null ? (ThrowingOptional<T, X>) EMPTY : new ThrowingOptional<>(value, expectedThrowableClass);
    }

    public static <T, X extends Throwable> ThrowingOptional<T, X> ofOptional(Optional<T> value, Class<X> expectedThrowableClass) {
        return value.isPresent() ? new ThrowingOptional<>(value.get(), expectedThrowableClass) : (ThrowingOptional<T, X>) EMPTY;
    }

    public static <T, X extends Throwable> ThrowingOptional<T, X> ofThrowable(Class<T> expectedValueClass, X throwable) {
        return new ThrowingOptional<>(Objects.requireNonNull(throwable), expectedValueClass);
    }

    public T get() throws X {
        if (this.throwable != null) {
            throw this.throwable;
        }
        if (this.value == null) {
            throw new NoSuchElementException("No value present");
        }
        return this.value;
    }

    public boolean isPresent() {
        return this.value != null;
    }

    public boolean isEmpty() {
        return this.value == null;
    }

    public void ifPresent(ThrowingConsumer<? super T, ? extends X> action) throws X {
        if (this.value != null) {
            action.accept(this.value);
        }
    }

    public void ifPresentOrElse(ThrowingConsumer<? super T, ? extends X> action, ThrowingRunnable<? extends X> emptyAction) throws X {
        if (this.value != null) {
            action.accept(this.value);
        } else {
            emptyAction.run();
        }
    }

    public ThrowingOptional<T, X> filter(ThrowingPredicate<? super T, ? extends X> predicate) throws X {
        Objects.requireNonNull(predicate);
        if (!this.isPresent()) {
            return this;
        } else {
            return predicate.test(this.value) ? this : empty();
        }
    }

    public <U> ThrowingOptional<U, X> map(ThrowingFunction<? super T, ? extends U, ? extends X> mapper) throws X {
        Objects.requireNonNull(mapper);
        if (!this.isPresent()) {
            return empty();
        } else {
            return ThrowingOptional.ofNullable(mapper.apply(this.value), this.expectedThrowableClass);
        }
    }

    public <U> ThrowingOptional<U, X> flatMap(ThrowingFunction<? super T, ? extends ThrowingOptional<? extends U, ? extends X>, ? extends X> mapper) throws X {
        Objects.requireNonNull(mapper);
        if (!this.isPresent()) {
            return empty();
        } else {
            @SuppressWarnings("unchecked")
            ThrowingOptional<U, X> r = (ThrowingOptional<U, X>) mapper.apply(this.value);
            return Objects.requireNonNull(r);
        }
    }

    public <U> ThrowingOptional<U, X> flatMapOptional(ThrowingFunction<? super T, ? extends Optional<? extends U>, ? extends X> mapper) throws X {
        Objects.requireNonNull(mapper);
        if (!this.isPresent()) {
            return empty();
        } else {
            @SuppressWarnings("unchecked")
            Optional<U> r = (Optional<U>) mapper.apply(this.value);
            return ofOptional(Objects.requireNonNull(r), this.expectedThrowableClass);
        }
    }

    public ThrowingOptional<T, X> or(ThrowingSupplier<? extends ThrowingOptional<? extends T, ? extends X>, ? extends X> supplier) {
        Objects.requireNonNull(supplier);
        if (this.isPresent()) {
            return this;
        } else {
            try {
                @SuppressWarnings("unchecked")
                ThrowingOptional<T, X> r = (ThrowingOptional<T, X>) supplier.get();
                return Objects.requireNonNull(r);
            } catch (Throwable t) {
                if (this.expectedThrowableClass.isAssignableFrom(t.getClass())) {
                    return ThrowingOptional.ofThrowable(expectedValueClass, (X) t);
                } else {
                    throw new IllegalStateException("Unexpected exception thrown in or supplier", t);
                }
            }
        }
    }

    public ThrowingStream<T, X> stream() {
        if (!this.isPresent()) {
            return ThrowingStream.of(Stream.empty(), this.expectedThrowableClass);
        } else {
            return ThrowingStream.of(Stream.of(this.value), this.expectedThrowableClass);
        }
    }

    public T orElse(T other) throws X {
        if (this.throwable != null) {
            throw this.throwable;
        }
        return this.value != null ? this.value : other;
    }

    public T orElseGet(ThrowingSupplier<? extends T, ? extends X> supplier) throws X {
        if (this.throwable != null) {
            throw this.throwable;
        }
        return this.value != null ? this.value : supplier.get();
    }

    public T orElseThrow() throws X {
        if (this.throwable != null) {
            throw this.throwable;
        }
        if (this.value == null) {
            throw new NoSuchElementException("No value present");
        }
        return this.value;
    }

    public <X2 extends Throwable> T orElseThrow(Supplier<? extends X2> exceptionSupplier) throws X, X2 {
        if (this.throwable != null) {
            throw this.throwable;
        }
        if (this.value != null) {
            return this.value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public String toString() {
        return this.throwable != null
                ? String.format("ThrowingOptional[%s]", this.throwable.getClass().getName())
                : this.value != null
                        ? String.format("ThrowingOptional[%s]", this.value)
                        : "ThrowingOptional.empty";
    }
    
    public Optional<T> toOptional() throws X {
        if (this.throwable != null) {
            throw this.throwable;
        }
        return Optional.ofNullable(this.value);
    }
}
