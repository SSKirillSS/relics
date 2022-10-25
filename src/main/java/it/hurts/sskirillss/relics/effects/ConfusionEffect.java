package it.hurts.sskirillss.relics.effects;

import it.hurts.sskirillss.relics.init.EffectRegistry;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.player.Input;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ConfusionEffect extends MobEffect {
    public ConfusionEffect() {
        super(MobEffectCategory.HARMFUL, 0X6836AA);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onChatMessage(MovementInputUpdateEvent event) {
            Player player = event.getPlayer();

            if (player.hasEffect(EffectRegistry.CONFUSION.get())) {
                Input input = event.getInput();

                input.leftImpulse *= -1;
                input.forwardImpulse *= -1;
            }
        }
    }
}