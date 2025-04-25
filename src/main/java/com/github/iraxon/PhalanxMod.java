package com.github.iraxon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(PhalanxMod.MODID)
public class PhalanxMod {
    public static final String MODID = "phalanx";

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(
            ForgeRegistries.ENTITY_TYPES, MODID);

    public static final RegistryObject<EntityType<?>> CUSTOM_ZOMBIE = ENTITY_TYPES.register("custom_zombie",
            () -> (EntityType.Builder.of(Soldier::new, MobCategory.MISC).build(
                    MODID + ":soldier")));

    public PhalanxMod() {
    }
}

class Soldier extends Mob {
    public Soldier(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }
}
