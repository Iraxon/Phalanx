package net.iraxon.phalanx.warhorn;

import javax.annotation.Nonnull;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WarhornItem extends Item {
    public static final Item.Properties PROPERTIES = new Item.Properties().fireResistant();

    public WarhornItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level pLevel, @Nonnull Player pPlayer,
            @Nonnull InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if (pUsedHand.equals(InteractionHand.MAIN_HAND)) {
            pLevel.playSound(pPlayer, pPlayer.blockPosition(), SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(6).get(),
                    SoundSource.MASTER);
            return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide);
        }
        return InteractionResultHolder.pass(itemstack);
    }
}
