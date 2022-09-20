package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.commands.RelicsCommand;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandRegistry {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        RelicsCommand.register(event.getDispatcher());
    }
}