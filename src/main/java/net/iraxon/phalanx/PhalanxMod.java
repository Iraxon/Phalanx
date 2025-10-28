package net.iraxon.phalanx;

import java.util.List;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.iraxon.phalanx.claim.ClaimBlock;
import net.iraxon.phalanx.registration.RegisterManager;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PhalanxMod.MOD_ID)
public class PhalanxMod {
    public static final String MOD_ID = "phalanx";
    public static final Logger LOGGER = LogUtils.getLogger();
    public final RegisterManager regm;

    public PhalanxMod(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);

        regm = new RegisterManager(MOD_ID, modEventBus);

        regm.newBlock("claim_block").constructor(ClaimBlock::new).register();
        regm.newBlockItem("claim_block").tab(CreativeModeTabs.FUNCTIONAL_BLOCKS).register();

        regm.newItem("warhorn").properties(new Item.Properties().fireResistant()).tab(CreativeModeTabs.COMBAT).register();

        regm.build();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    // You can use EventBusSubscriber to automatically register all static methods
    // in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
