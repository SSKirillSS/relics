package it.hurts.sskirillss.relics.effects;

import it.hurts.sskirillss.relics.init.EffectRegistry;
import net.minecraft.client.player.Input;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ParalysisEffect extends MobEffect {
    public ParalysisEffect() {
        super(MobEffectCategory.HARMFUL, 0X6836AA);
    }

    @Mod.EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onMovementInput(MovementInputUpdateEvent event) {
            Player player = event.getPlayer();

            if (player.hasEffect(EffectRegistry.PARALYSIS.get())) {
                Input input = event.getInput();

                input.shiftKeyDown = false;
                input.jumping = false;

                input.forwardImpulse = 0;
                input.leftImpulse = 0;
            }
        }
    }
}