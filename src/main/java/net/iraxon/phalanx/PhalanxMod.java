package net.iraxon.phalanx;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.iraxon.phalanx.claim.ClaimBlock;
import net.iraxon.phalanx.registration.RegisterManager;
import net.iraxon.phalanx.warhorn.WarhornItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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

        regm.newBlock("claim_block").properties(ClaimBlock.PROPERTIES).constructor(ClaimBlock::new).register();
        regm.newBlockItem("claim_block").tab(CreativeModeTabs.FUNCTIONAL_BLOCKS).register();

        regm.newItem("warhorn").properties(WarhornItem.PROPERTIES).constructor(WarhornItem::new).tab(CreativeModeTabs.COMBAT)
                .register();

        regm.build();

        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    public void onServerStarting(ServerStartingEvent event) {
    }
}
