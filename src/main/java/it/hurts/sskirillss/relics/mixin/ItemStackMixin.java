package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(at = @At(value = "TAIL"), method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/nbt/CompoundTag;)V")
    protected void init(ItemLike slug, int count, CompoundTag tag, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        Item item = stack.getItem();

        if (!(item instanceof IRelicItem relic))
            return;

        RelicData data = relic.getRelicData();

        if (data == null)
            return;

        for (Map.Entry<String, AbilityData> entry : data.getAbilities().getAbilities().entrySet()) {
            String id = entry.getKey();

            relic.randomizeStats(stack, id);
            relic.setAbilityPoints(stack, id, 0);

            if (entry.getValue().getCastData().getKey() == CastType.TOGGLEABLE)
                relic.setAbilityTicking(stack, id, true);
        }
    }
}