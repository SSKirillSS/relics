package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.commands.RelicsCommand;
import it.hurts.sskirillss.relics.commands.arguments.RelicAbilityArgument;
import it.hurts.sskirillss.relics.commands.arguments.RelicAbilityStatArgument;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Reference.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CommandRegistry {
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENTS = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Reference.MODID);

    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> RELIC_ABILITY = COMMAND_ARGUMENTS.register("relic_ability", () -> ArgumentTypeInfos.registerByClass(RelicAbilityArgument.class, SingletonArgumentInfo.contextFree(RelicAbilityArgument::ability)));
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> RELIC_ABILITY_STAT = COMMAND_ARGUMENTS.register("relic_ability_stat", () -> ArgumentTypeInfos.registerByClass(RelicAbilityStatArgument.class, SingletonArgumentInfo.contextFree(RelicAbilityStatArgument::abilityStat)));

    public static void register(IEventBus bus) {
        COMMAND_ARGUMENTS.register(bus);
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        RelicsCommand.register(event.getDispatcher());
    }
}