package it.hurts.sskirillss.relics.items.relics.boots;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.items.relics.renderer.IceBreakerModel;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.MutablePair;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class IceBreakerItem extends RelicItem<IceBreakerItem.Stats> implements ICurioItem {
    private final MutablePair<String, UUID> SPEED_INFO = new MutablePair<>(Reference.MODID
            + ":" + "ice_breaker_movement_speed", UUID.fromString("90af8e8a-93aa-4b0f-8ddc-8986dd2a8461"));
    private final MutablePair<String, UUID> KNOCKBACK_INFO = new MutablePair<>(Reference.MODID
            + ":" + "ice_breaker_knockback resistance", UUID.fromString("70c6b1a0-e025-44bf-8dcd-c165c59b7eb4"));

    public static IceBreakerItem INSTANCE;

    public IceBreakerItem() {
        super(Rarity.RARE);
        INSTANCE = this;
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.ice_breaker.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.ice_breaker.shift_2"));
        return tooltip;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            Vector3d motion = player.getDeltaMovement();
            EntityUtils.applyAttributeModifier(player.getAttribute(Attributes.MOVEMENT_SPEED), new AttributeModifier(SPEED_INFO.getRight(),
                    SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
            EntityUtils.applyAttributeModifier(player.getAttribute(Attributes.KNOCKBACK_RESISTANCE), new AttributeModifier(KNOCKBACK_INFO.getRight(),
                    KNOCKBACK_INFO.getLeft(), config.knockbackResistanceModifier, AttributeModifier.Operation.ADDITION));
            if (player.fallDistance >= config.minFallDistance && player.isShiftKeyDown())
                player.setDeltaMovement(motion.x(), motion.y() * config.fallMotionMultiplier, motion.z());
            if (player.horizontalCollision && player.isShiftKeyDown()) {
                player.setDeltaMovement(0, -config.wallSlippingSpeed, 0);
                player.fallDistance = 0;
                player.getCommandSenderWorld().addParticle(ParticleTypes.CRIT, player.getX(), player.getY() - 0.15D, player.getZ(), 0, 0, 0);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        EntityUtils.removeAttributeModifier(slotContext.getWearer().getAttribute(Attributes.MOVEMENT_SPEED),
                new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
        EntityUtils.removeAttributeModifier(slotContext.getWearer().getAttribute(Attributes.KNOCKBACK_RESISTANCE),
                new AttributeModifier(KNOCKBACK_INFO.getRight(), KNOCKBACK_INFO.getLeft(), config.knockbackResistanceModifier, AttributeModifier.Operation.ADDITION));
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.COLD;
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    private final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/items/models/ice_breaker.png");

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        IceBreakerModel model = new IceBreakerModel();
        matrixStack.pushPose();
        model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTicks);
        ICurio.RenderHelper.followBodyRotations(livingEntity, model);
        model.renderToBuffer(matrixStack, renderTypeBuffer.getBuffer(RenderType.entityTranslucent(TEXTURE)),
                light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
    }

    @Override
    public boolean canRender(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        return true;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class IceBreakerServerEvents {
        @SubscribeEvent
        public static void onEntityFall(LivingFallEvent event) {
            Stats config = INSTANCE.config;
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.ICE_BREAKER.get(), player).isPresent()
                        && !player.getCooldowns().isOnCooldown(ItemRegistry.ICE_BREAKER.get())) {
                    if (event.getDistance() >= config.minFallDistance && player.isShiftKeyDown()) {
                        player.getCommandSenderWorld().playSound(null, player.blockPosition(), SoundEvents.WITHER_BREAK_BLOCK,
                                SoundCategory.PLAYERS, 0.75F, 1.0F);
                        player.getCooldowns().addCooldown(ItemRegistry.ICE_BREAKER.get(), Math.round(event.getDistance() * config.stompCooldownMultiplier * 20));
                        for (LivingEntity entity : player.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class,
                                player.getBoundingBox().inflate(event.getDistance() * config.stompRadiusMultiplier))) {
                            if (entity != player) {
                                entity.hurt(DamageSource.playerAttack(player), Math.min(config.maxDealtDamage,
                                        event.getDistance() * config.dealtDamageMultiplier));
                                entity.setDeltaMovement(entity.position().subtract(player.position()).add(0, 1.005F, 0).multiply(
                                        config.stompMotionMultiplier, config.stompMotionMultiplier, config.stompMotionMultiplier));
                            }
                        }
                        if (player.getCommandSenderWorld().getBlockState(player.blockPosition().below()).is(BlockTags.ICE))
                            player.getCommandSenderWorld().destroyBlock(player.blockPosition().below(), false, player);
                        event.setDamageMultiplier(config.incomingFallDamageMultiplier);
                    }
                }
            }
        }
    }

    public static class Stats extends RelicStats {
        public float speedModifier = -0.1F;
        public float knockbackResistanceModifier = 0.5F;
        public int minFallDistance = 3;
        public float fallMotionMultiplier = 1.075F;
        public float wallSlippingSpeed = 0.1F;
        public float stompCooldownMultiplier = 3.0F;
        public float stompRadiusMultiplier = 0.5F;
        public float stompMotionMultiplier = 1.01F;
        public float dealtDamageMultiplier = 1.0F;
        public int maxDealtDamage = 100;
        public float incomingFallDamageMultiplier = 0.0F;
    }
}