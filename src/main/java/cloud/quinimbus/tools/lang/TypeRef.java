package cloud.quinimbus.tools.lang;

import java.lang.reflect.*;

/// Represents a typed reference to a {@link java.lang.reflect.Type}, allowing access to generic type information at
/// runtime.
///
/// Instances of this class are usually created by subclassing with an anonymous class to capture the generic type
/// parameter, or by using the [#of(Class)] factory method for non-generic types.
///
/// @param <T> the referenced type
public abstract class TypeRef<T> {
    private final Type type;

    /// Creates a new type reference and captures the generic type parameter.
    ///
    /// @throws IllegalArgumentException if no type parameter is provided
    public TypeRef() {
        Type s = getClass().getGenericSuperclass();
        if (s instanceof ParameterizedType p) {
            this.type = p.getActualTypeArguments()[0];
        } else {
            throw new IllegalArgumentException("Missing type parameter.");
        }
    }

    /// Creates a new type reference for the given {@link Type}.
    ///
    /// @param type the type to be referenced
    public TypeRef(Type type) {
        this.type = type;
    }

    /// Returns a string representation of the referenced type.
    ///
    /// @return the type name
    @Override
    public String toString() {
        return type.getTypeName();
    }

    /// Creates a type reference for the given {@link Class}.
    ///
    /// @param <T> the type of the class
    /// @param cls the class to reference
    /// @return a new type reference for the class
    public static <T> TypeRef<T> of(Class<T> cls) {
        return new TypeRef<T>(cls) {};
    }

    /// Returns the raw {@link Class} of the referenced type.
    ///
    /// @return the raw class of the type
    /// @throws IllegalArgumentException if the raw class cannot be determined
    public Class<? extends T> getRawClass() {
        return switch (type) {
            case Class<?> cls -> (Class<? extends T>) cls;
            case ParameterizedType p when p.getRawType() instanceof Class<?> cls -> (Class<? extends T>) cls;
            default ->
                throw new IllegalArgumentException("Unsupported raw class for type %s".formatted(type.getTypeName()));
        };
    }

    /// Returns the referenced {@link Type}.
    ///
    /// @return the type
    public Type getType() {
        return type;
    }
}
