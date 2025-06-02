package net.iraxon.phalanx.common;

import java.lang.reflect.InvocationTargetException;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

interface ModElement<T> {

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
     * A supplier for the object to be registered; it must return distinct instances
     * every call
     *
     * @return A unique insance of the object to be registered
     */
    public T supply();

    /**
     * Sends this ModElement to the ModBuilder for registration
     *
     * @return The Forge RegistryObject created
     */
    public default void register() {
        registerManager().newElementFromSupplier(name(), this::supply);
    }
}

public record BlockElement(RegisterManager registerManager, String name,
        Class<? extends Block> blockClass, BlockBehaviour.Properties properties) implements ModElement<Block> {

    @Override
    public Block supply() {
        try {
            return blockClass().getConstructor(BlockBehaviour.Properties.class).newInstance(properties);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void register() {
        registerManager.newElementFromSupplier(name, this::supply);
    }

    public BlockElement blockClass(Class<? extends Block> blockClass) {
        return new BlockElement(registerManager, name, blockClass, properties);
    }

    public BlockElement properties(BlockBehaviour.Properties properties) {
        return new BlockElement(registerManager, name, blockClass, properties);
    }
}
