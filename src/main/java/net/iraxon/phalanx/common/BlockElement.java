package net.iraxon.phalanx.common;

import java.util.function.Function;

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
        Function<BlockBehaviour.Properties, ? extends Block> blockConstructor, BlockBehaviour.Properties properties) implements ModElement<Block> {

    @Override
    public Block supply() {
        return blockConstructor().apply(properties);
    }

    public void register() {
        registerManager.newElementFromSupplier(name, this::supply);
    }

    public BlockElement blockClass(Function<BlockBehaviour.Properties, ? extends Block> blockConstructor) {
        return new BlockElement(registerManager, name, blockConstructor, properties);
    }

    public BlockElement properties(BlockBehaviour.Properties properties) {
        return new BlockElement(registerManager, name, blockConstructor, properties);
    }
}
