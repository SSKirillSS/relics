package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.AbilityCastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.utils.AbilityUtils;
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
public abstract class MixinItemStack {
    @Inject(at = @At(value = "TAIL"), method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/nbt/CompoundTag;)V")
    protected void init(ItemLike slug, int count, CompoundTag tag, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        Item item = stack.getItem();

        if (!(item instanceof RelicItem relic))
            return;

        RelicData data = relic.getRelicData();

        if (data == null)
            return;

        RelicAbilityData abilities = data.getAbilityData();

        if (abilities == null)
            return;

        for (Map.Entry<String, RelicAbilityEntry> entry : abilities.getAbilities().entrySet()) {
            String id = entry.getKey();

            AbilityUtils.randomizeStats(stack, id);
            AbilityUtils.setAbilityPoints(stack, id, 0);

            if (entry.getValue().getCastData().getKey() == AbilityCastType.TOGGLEABLE)
                AbilityUtils.setAbilityTicking(stack, id, true);
        }
    }
}