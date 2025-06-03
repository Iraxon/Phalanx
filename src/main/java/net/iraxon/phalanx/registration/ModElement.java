package net.iraxon.phalanx.registration;

public interface ModElement<T> {

    /**
     * @return The RegisterManager used to create this ModElement, which will
     *         register it when it is done
     */
    public RegisterManager registerManager();

    /**
     * @return The registry name (not included the mod id or colon) of the object to
     *         be registered
     */
    public String name();

    /**
     * @return The Class object for the object being represented (Block.class for a
     *         block, for example)
     */
    public Class<T> type();

    /**
     * A supplier for the object to be registered; it must return distinct instances
     * every call
     *
     * @return A unique insance of the object to be registered
     */
    public T supply();

    /**
     * Sends this ModElement to the RegisterManager for registration
     *
     * @return The RegisterManager, for conveneint chaining
     */
    public default RegisterManager register() {
        registerManager().newElementFromSupplier(name(), type(), this::supply);
        return registerManager();
    }
}
