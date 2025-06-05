package net.iraxon.phalanx.registration;

import java.util.function.Function;

public interface ConstructedElement<P, T> extends ModElement<T> {

    @Override
    public default T supply() {
        return constructor().apply(properties());
    }

    public P properties();
    public Function<P, ? extends T> constructor();
}
