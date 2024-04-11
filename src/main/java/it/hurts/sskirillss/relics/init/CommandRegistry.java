package it.hurts.sskirillss.relics.init;

import it.hurts.sskirillss.relics.commands.RelicsCommand;
import it.hurts.sskirillss.relics.commands.arguments.RelicAbilityArgument;
import it.hurts.sskirillss.relics.commands.arguments.RelicAbilityStatArgument;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.core.Registry;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandRegistry {
//    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENTS = DeferredRegister.create(Registry.COMMAND_ARGUMENT_TYPE_REGISTRY, Reference.MODID);
//
//    public static final RegistryObject<ArgumentTypeInfo<?, ?>> RELIC_ABILITY = COMMAND_ARGUMENTS.register("relic_ability", () -> ArgumentTypeInfos.registerByClass(RelicAbilityArgument.class, SingletonArgumentInfo.contextFree(RelicAbilityArgument::ability)));
//    public static final RegistryObject<ArgumentTypeInfo<?, ?>> RELIC_ABILITY_STAT = COMMAND_ARGUMENTS.register("relic_ability_stat", () -> ArgumentTypeInfos.registerByClass(RelicAbilityStatArgument.class, SingletonArgumentInfo.contextFree(RelicAbilityStatArgument::abilityStat)));

    public static void register() {
        //COMMAND_ARGUMENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        RelicsCommand.register(event.getDispatcher());
    }
}