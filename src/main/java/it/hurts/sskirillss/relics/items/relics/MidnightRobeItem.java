package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.api.durability.IRepairableItem;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.MidnightRobeModel;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.ShiftTooltip;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.MutablePair;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.UUID;

public class MidnightRobeItem extends RelicItem<MidnightRobeItem.Stats> implements ICurioItem {

    public static MidnightRobeItem INSTANCE;

    private final MutablePair<String, UUID> SPEED_INFO = new MutablePair<>(Reference.MODID
            + ":" + "midnight_robe_movement_speed", UUID.fromString("21a949be-67d9-43bb-96b8-496782d60933"));

    public MidnightRobeItem() {
        super(RelicData.builder()
                .rarity(Rarity.UNCOMMON)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.CAVE)
                        .chance(0.1F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .shift(ShiftTooltip.builder()
                        .arg("+" + (int) (config.speedModifier * 100 - 100) + "%")
                        .build())
                .shift(ShiftTooltip.builder()
                        .arg(config.minLightLevel)
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        World world = livingEntity.getCommandSenderWorld();

        if (world.isClientSide() || IRepairableItem.isBroken(stack) || livingEntity.tickCount % 20 != 0)
            return;

        if (canHide(livingEntity))
            livingEntity.addEffect(new EffectInstance(Effects.INVISIBILITY, 30, 0, false, false));

        ModifiableAttributeInstance attribSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeModifier speedModifier = new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(),
                config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL);

        if (world.isNight())
            EntityUtils.applyAttributeModifier(attribSpeed, speedModifier);
        else
            EntityUtils.removeAttributeModifier(attribSpeed, speedModifier);
    }

    private static boolean canHide(LivingEntity entity) {
        World world = entity.getCommandSenderWorld();
        BlockPos position = entity.blockPosition();

        return !EntityUtils.findEquippedCurio(entity, ItemRegistry.MIDNIGHT_ROBE.get()).isEmpty()
                && world.getBrightness(LightType.BLOCK, position)
                + world.getBrightness(LightType.SKY, position) <= INSTANCE.config.minLightLevel;
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        EntityUtils.removeAttributeModifier(slotContext.getWearer().getAttribute(Attributes.MOVEMENT_SPEED),
                new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(),
                        config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new MidnightRobeModel();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class CamouflageRingClientEvents {
        @SubscribeEvent
        public static void onEntityRender(RenderPlayerEvent.Pre event) {
            if (canHide(event.getPlayer()))
                event.setCanceled(true);
        }
    }

    public static class Stats extends RelicStats {
        public float speedModifier = 1.15F;
        public int minLightLevel = 2;
    }
}