package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Optional;

@Mixin(PiglinAi.class)
public abstract class MixinPiglinAi {
    @Shadow
    private static void throwItems(Piglin piglin, List<ItemStack> items) {
    }

    @Shadow
    private static List<ItemStack> getBarterResponseItems(Piglin piglin) {
        return null;
    }

    @Redirect(method = "stopHoldingOffHandItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;throwItems(Lnet/minecraft/world/entity/monster/piglin/Piglin;Ljava/util/List;)V", ordinal = 0))
    private static void tweakBartering(Piglin piglin, List<ItemStack> items) {
        Optional<Player> optional = piglin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);

        if (optional.isEmpty()) {
            throwItems(piglin, getBarterResponseItems(piglin));

            return;
        }

        ItemStack stack = EntityUtils.findEquippedCurio(optional.get(), ItemRegistry.BASTION_RING.get());

        if (!stack.isEmpty()) {
            for (int i = 0; i < Math.round(RelicItem.getAbilityValue(stack, "trade", "rolls")); i++) {
                if (piglin.getRandom().nextBoolean()) {
                    throwItems(piglin, getBarterResponseItems(piglin));

                    RelicItem.addExperience(optional.get(), stack, 5);
                }
            }
        } else
            throwItems(piglin, getBarterResponseItems(piglin));
    }
}