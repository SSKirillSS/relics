package it.hurts.sskirillss.relics.mixin;

import com.google.common.collect.ImmutableList;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.PiglinMobsSensor;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;

@Mixin(PiglinMobsSensor.class)
public class MixinPiglinMobsSensor {
    @Inject(at = @At("RETURN"), method = "doTick", remap = false)
    protected void calmPiglins(ServerWorld worldIn, LivingEntity entityIn, CallbackInfo info) {
        Brain<?> brain = entityIn.getBrain();
        PlayerEntity player = brain.getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD).orElse(null);
        if (player == null) return;
        if (!CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.BASTION_RING.get(), player).isPresent()) return;
        brain.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, Optional.empty());
        for (LivingEntity entity : brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).orElse(ImmutableList.of())) {
            if (!(entity instanceof PlayerEntity)) continue;
            player = (PlayerEntity) entity;
            if (EntityPredicates.ATTACK_ALLOWED.test(player) && !PiglinTasks.isWearingGold(player)
                    && !CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.BASTION_RING.get(), player).isPresent())
                brain.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, Optional.of(player));
        }
    }
}