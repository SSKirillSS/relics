package it.hurts.sskirillss.relics.mixin;

import com.google.common.collect.ImmutableList;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.PiglinSpecificSensor;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PiglinSpecificSensor.class)
public class MixinPiglinMobsSensor {
    @Inject(at = @At(value = "RETURN"), method = "doTick")
    protected void calmPiglins(ServerLevel worldIn, LivingEntity entityIn, CallbackInfo info) {
        Brain<?> brain = entityIn.getBrain();
        Player player = brain.getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD).orElse(null);

        if (player == null)
            return;

        if (EntityUtils.findEquippedCurio(player, ItemRegistry.BASTION_RING.get()).isEmpty())
            return;

        brain.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, Optional.empty());

        for (LivingEntity entity : brain.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse(ImmutableList.of())) {
            if (!(entity instanceof Player))
                continue;

            player = (Player) entity;

            if (EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(player) && !PiglinAi.isWearingGold(player)
                    && EntityUtils.findEquippedCurio(player, ItemRegistry.BASTION_RING.get()).isEmpty())
                brain.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, Optional.of(player));
        }
    }
}