// package net.iraxon.phalanx.common;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.function.Supplier;

// import net.iraxon.phalanx.PhalanxMod;
// import net.minecraft.world.item.BlockItem;
// import net.minecraft.world.item.Item;
// import net.minecraft.world.level.block.Block;
// import net.minecraft.world.level.block.Blocks;
// import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
// import net.minecraftforge.eventbus.api.IEventBus;
// import net.minecraftforge.registries.DeferredRegister;
// import net.minecraftforge.registries.ForgeRegistries;
// import net.minecraftforge.registries.RegistryObject;

// public class ModElements {

//     private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
//             PhalanxMod.MOD_ID);

//     private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
//             PhalanxMod.MOD_ID);

//     public static class ModBlocks {
//         public static final RegistryObject<Block> CLAIM_TOTEM = registerSimpleBlock(
//                 "claim_totem",
//                 Properties.copy(Blocks.IRON_BLOCK).explosionResistance(100000));
//     }

//     public static void registerRegisters(IEventBus eventBus) {
//         List.of(ITEMS, BLOCKS).stream().forEach(r -> r.register(eventBus));
//     }

//     private static RegistryObject<Block> registerSimpleBlock(String name, Properties properties, boolean blockItem) {
//         var r = BLOCKS.register(name, () -> (new Block(properties)));
//         if (blockItem) {
//             registerBlockItem(name, r);
//         }
//         return r;
//     }

//     private static RegistryObject<Block> registerSimpleBlock(String name, Properties properties) {
//         return registerSimpleBlock(name, properties, true);
//     }

//     private static RegistryObject<Item> registerBlockItem(String name, RegistryObject<? extends Block> block) {
//         return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
//     }
// }
