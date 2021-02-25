package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class StomperBootsItem extends Item implements ICurioItem, IHasTooltip {
    public StomperBootsItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.stomper_boots.shift_1"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity.fallDistance >= RelicsConfig.StomperBoots.MIN_FALL_DISTANCE.get() && livingEntity.isSneaking()) {
            Vector3d motion = livingEntity.getMotion();
            livingEntity.setMotion(motion.getX(), motion.getY() * RelicsConfig.StomperBoots.FALL_MOTION_MULTIPLIER.get(), motion.getZ());
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class StomperServerEvents {
        @SubscribeEvent
        public static void onEntityFall(LivingFallEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.STOMPER_BOOTS.get(), player).isPresent()
                        && !player.getCooldownTracker().hasCooldown(ItemRegistry.STOMPER_BOOTS.get())) {
                    if (event.getDistance() >= RelicsConfig.StomperBoots.MIN_FALL_DISTANCE.get() && player.isSneaking()) {
                        player.getEntityWorld().playSound(null, player.getPosition(), SoundEvents.ENTITY_WITHER_BREAK_BLOCK,
                                SoundCategory.PLAYERS, 0.75F, 1.0F);
                        player.getCooldownTracker().setCooldown(ItemRegistry.STOMPER_BOOTS.get(), Math.round(event.getDistance()
                                * RelicsConfig.StomperBoots.STOMP_COOLDOWN_MULTIPLIER.get().floatValue() * 20));
                        for (LivingEntity entity : player.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class,
                                player.getBoundingBox().grow(event.getDistance() * RelicsConfig.StomperBoots.STOMP_RADIUS_MULTIPLIER.get()))) {
                            if (entity != player) {
                                entity.attackEntityFrom(DamageSource.causePlayerDamage(player), Math.min(RelicsConfig.StomperBoots.MAX_DEALT_DAMAGE.get().floatValue(),
                                        event.getDistance() * RelicsConfig.StomperBoots.DEALT_DAMAGE_MULTIPLIER.get().floatValue()));
                                entity.setMotion(entity.getPositionVec().subtract(player.getPositionVec()).add(0, 1.005F, 0).mul(
                                        RelicsConfig.StomperBoots.STOMP_MOTION_MULTIPLIER.get(),
                                        RelicsConfig.StomperBoots.STOMP_MOTION_MULTIPLIER.get(),
                                        RelicsConfig.StomperBoots.STOMP_MOTION_MULTIPLIER.get()));
                            }
                        }
                        event.setDamageMultiplier(RelicsConfig.StomperBoots.INCOMING_FALL_DAMAGE_MULTIPLIER.get().floatValue());
                    }
                }
            }
        }
    }
}