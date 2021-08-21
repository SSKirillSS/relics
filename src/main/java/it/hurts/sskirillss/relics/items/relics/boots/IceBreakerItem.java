package it.hurts.sskirillss.relics.items.relics.boots;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.items.relics.renderer.IceBreakerModel;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class IceBreakerItem extends RelicItem<IceBreakerItem.Stats> implements ICurioItem {

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
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> result = super.getAttributeModifiers(slotContext, uuid, stack);
        result.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, Reference.MODID + ":" + "ice_breaker_movement_speed",
                config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
        result.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, Reference.MODID + ":" + "ice_breaker_knockback_resistance",
                config.knockbackResistanceModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
        return result;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) livingEntity;
        Vector3d motion = player.getDeltaMovement();
        if (player.isOnGround() || player.isInWater()) return;
        player.setDeltaMovement(motion.x(), motion.y() * config.fallMotionMultiplier, motion.z());
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
        matrixStack.scale(1.025F, 1.025F, 1.025F);
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
            if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.ICE_BREAKER.get(), player).ifPresent(triple -> {
                ItemStack stack = triple.getRight();
                if (player.getCooldowns().isOnCooldown(stack.getItem())) return;
                float distance = event.getDistance();
                World world = player.getCommandSenderWorld();
                if (distance < config.minFallDistance || !player.isShiftKeyDown()) return;
                world.playSound(null, player.blockPosition(), SoundEvents.WITHER_BREAK_BLOCK,
                        SoundCategory.PLAYERS, 0.75F, 1.0F);
                player.getCooldowns().addCooldown(ItemRegistry.ICE_BREAKER.get(), Math.round(distance * config.stompCooldownMultiplier * 20));
                for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class,
                        player.getBoundingBox().inflate(distance * config.stompRadiusMultiplier))) {
                    if (entity == player) continue;
                    entity.hurt(DamageSource.playerAttack(player), Math.min(config.maxDealtDamage,
                            distance * config.dealtDamageMultiplier));
                    entity.setDeltaMovement(entity.position().subtract(player.position()).add(0, 1.005F, 0).multiply(
                            config.stompMotionMultiplier, config.stompMotionMultiplier, config.stompMotionMultiplier));
                }
                event.setDamageMultiplier(config.incomingFallDamageMultiplier);
            });
        }
    }

    public static class Stats extends RelicStats {
        public float speedModifier = -0.1F;
        public float knockbackResistanceModifier = 0.5F;
        public int minFallDistance = 3;
        public float fallMotionMultiplier = 1.075F;
        public float stompCooldownMultiplier = 1.5F;
        public float stompRadiusMultiplier = 0.5F;
        public float stompMotionMultiplier = 1.005F;
        public float dealtDamageMultiplier = 1.0F;
        public int maxDealtDamage = 100;
        public float incomingFallDamageMultiplier = 0.0F;
    }
}