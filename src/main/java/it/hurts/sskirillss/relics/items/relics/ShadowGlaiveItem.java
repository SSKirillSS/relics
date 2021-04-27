package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.entities.ShadowGlaiveEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShadowGlaiveItem extends Item implements IHasTooltip {
    public ShadowGlaiveItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.shadow_glaive.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.shadow_glaive.shift_2"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ShadowGlaiveServerEvents {
        @SubscribeEvent
        public static void onEntityDamage(LivingDamageEvent event) {
            if (!(event.getSource().getTrueSource() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
            if (player.getHeldItemOffhand().getItem() != ItemRegistry.SHADOW_GLAIVE.get()
                    || player.getCooldownTracker().hasCooldown(ItemRegistry.SHADOW_GLAIVE.get())) return;
            World world = player.getEntityWorld();
            if (world.getRandom().nextFloat() > RelicsConfig.ShadowGlaive.SUMMON_CHANCE.get()
                    || event.getAmount() < RelicsConfig.ShadowGlaive.MIN_DAMAGE_FOR_SUMMON.get()) return;
            LivingEntity entity = event.getEntityLiving();
            if (entity == null || !entity.isAlive() || player.getPositionVec().distanceTo(entity.getPositionVec())
                    > RelicsConfig.ShadowGlaive.MAX_DISTANCE_FOR_SUMMON.get()) return;
            ShadowGlaiveEntity glaive = new ShadowGlaiveEntity(world, player);
            glaive.setDamage(event.getAmount() * RelicsConfig.ShadowGlaive.INITIAL_DAMAGE_MULTIPLIER.get().floatValue());
            glaive.setOwner(player);
            glaive.setTarget(entity);
            glaive.setPositionAndUpdate(player.getPosX(), player.getPosY() + player.getHeight() * 0.5F, player.getPosZ());
            player.getCooldownTracker().setCooldown(ItemRegistry.SHADOW_GLAIVE.get(), RelicsConfig.ShadowGlaive.SUMMON_COOLDOWN.get() * 20);
            world.addEntity(glaive);
        }
    }
}