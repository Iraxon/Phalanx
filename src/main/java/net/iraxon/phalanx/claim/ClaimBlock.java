package net.iraxon.phalanx.claim;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public class ClaimBlock extends Block {
    public ClaimBlock(BlockBehaviour.Properties properties) {
        super(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(3.5f, 5f).lightLevel(s -> 3)
                .requiresCorrectToolForDrops().pushReaction(PushReaction.BLOCK));
    }
}
