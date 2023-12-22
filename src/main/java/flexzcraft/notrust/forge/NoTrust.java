package flexzcraft.notrust.forge;

import com.mojang.logging.LogUtils;
import flexzcraft.notrust.manager.LogManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(NoTrust.MODID)
@Mod.EventBusSubscriber(modid = NoTrust.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class NoTrust {

    public static final String MODID = "notrust";
    private static final Logger LOGGER = LogUtils.getLogger();

    public NoTrust() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.error("FMLCommonSetupEvent: {}", event);
        MinecraftForge.EVENT_BUS.register(PlayerInteractionHandler.class);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.shutdown();
    }

    public void registerCommands(RegisterCommandsEvent event) {
        CommandHandler.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        LogManager.getInstance().disconnect();
    }
}
