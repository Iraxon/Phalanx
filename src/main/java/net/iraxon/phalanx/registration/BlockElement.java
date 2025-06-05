package net.iraxon.phalanx.registration;

import java.util.function.Function;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public record BlockElement(RegisterManager registerManager, String name,
        Function<BlockBehaviour.Properties, ? extends Block> constructor,
        BlockBehaviour.Properties properties, boolean blockItem)
        implements ConstructedElement<BlockBehaviour.Properties, Block> {

    @Override
    public Block supply() {
        return constructor.apply(properties);
    }

    @Override
    public Class<Block> type() {
        return Block.class;
    }

    @Override
    public RegisterManager register() {
        var r = ConstructedElement.super.register();
        if (blockItem) {
            registerManager.newElementFromSupplier(name, BlockItem.class, () -> new BlockItem(
                    registerManager.get(Block.class, name).get(), new Item.Properties()));
        }
        return r;
    }

    public BlockElement constructor(Function<BlockBehaviour.Properties, ? extends Block> blockConstructor) {
        return new BlockElement(registerManager, name, blockConstructor, properties, blockItem);
    }

    public BlockElement properties(BlockBehaviour.Properties properties) {
        return new BlockElement(registerManager, name, constructor, properties, blockItem);
    }

    public BlockElement withoutBlockItem() {
        return new BlockElement(registerManager, name, constructor, properties, false);
    }
}
