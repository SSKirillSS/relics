package it.hurts.sskirillss.relics.mixin;

import it.hurts.sskirillss.relics.indev.RelicAbilityData;
import it.hurts.sskirillss.relics.indev.RelicAbilityEntry;
import it.hurts.sskirillss.relics.indev.RelicDataNew;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
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

        if (!(item instanceof RelicItem<?> relic))
            return;

        RelicDataNew data = relic.getNewData();

        if (data == null)
            return;

        RelicAbilityData abilities = data.getAbilityData();

        if (abilities == null)
            return;

        for (Map.Entry<String, RelicAbilityEntry> entries : abilities.getAbilities().entrySet()) {
            RelicItem.randomizeStats(stack, entries.getKey());

            RelicItem.setAbilityPoints(stack, entries.getKey(), 0);
        }
    }
}