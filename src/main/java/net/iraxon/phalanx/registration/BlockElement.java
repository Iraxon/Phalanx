package net.iraxon.phalanx.registration;

import java.util.function.Function;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public record BlockElement(RegisterManager registerManager, String name,
        Function<BlockBehaviour.Properties, ? extends Block> supplier,
        BlockBehaviour.Properties properties) implements ModElement<Block> {

    @Override
    public Block supply() {
        return supplier.apply(properties);
    }

    @Override
    public Class<Block> type() {
        return Block.class;
    }

    public BlockElement supplier(Function<BlockBehaviour.Properties, ? extends Block> blockConstructor) {
        return new BlockElement(registerManager, name, blockConstructor, properties);
    }

    public BlockElement properties(BlockBehaviour.Properties properties) {
        return new BlockElement(registerManager, name, supplier, properties);
    }
}
