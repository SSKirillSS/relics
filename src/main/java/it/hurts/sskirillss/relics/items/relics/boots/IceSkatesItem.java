package it.hurts.sskirillss.relics.items.relics.boots;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.items.relics.renderer.IceSkatesModel;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.MutablePair;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class IceSkatesItem extends RelicItem<IceSkatesItem.Stats> implements ICurioItem {
    private final MutablePair<String, UUID> SPEED_INFO = new MutablePair<>(Reference.MODID
            + ":" + "ice_skates_movement_speed", UUID.fromString("c0f5890f-a878-49bb-b24c-bbbf60d8539b"));

    public static final String TAG_SPEEDUP_TIME = "time";

    public static IceSkatesItem INSTANCE;

    public IceSkatesItem() {
        super(Rarity.UNCOMMON);

        INSTANCE = this;
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.ice_skates.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.ice_skates.shift_2"));
        return tooltip;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        int time = NBTUtils.getInt(stack, TAG_SPEEDUP_TIME, 0);
        BlockPos pos = WorldUtils.getSolidBlockUnderFeet(livingEntity.getCommandSenderWorld(), livingEntity.blockPosition());
        if (pos != null) {
            if (livingEntity.getCommandSenderWorld().getBlockState(pos).is(BlockTags.ICE)) {
                if (livingEntity.isSprinting()) {
                    EntityUtils.applyAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(),
                            SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
                    if (livingEntity.tickCount % 20 == 0) {
                        if (time < config.maxSpeedupTime) {
                            NBTUtils.setInt(stack, TAG_SPEEDUP_TIME, time + 1);
                        }
                    }
                    if (time > config.speedupsPerRam) {
                        livingEntity.getCommandSenderWorld().addParticle(ParticleTypes.CLOUD, livingEntity.getX(), livingEntity.getY() + 0.1F, livingEntity.getZ(), 0, 0, 0);
                        for (LivingEntity entity : livingEntity.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(config.ramRadius))) {
                            if (entity != livingEntity) {
                                entity.setDeltaMovement(entity.position().subtract(livingEntity.position()).normalize().multiply(time * 0.25F, time * 0.1F, time * 0.25F));
                                entity.hurt(DamageSource.FLY_INTO_WALL, config.baseRamDamage + time);
                                NBTUtils.setInt(stack, TAG_SPEEDUP_TIME, Math.max(time - config.speedupsPerRam, 0));
                            }
                        }
                    }
                }
            } else {
                if (livingEntity.getCommandSenderWorld().getBlockState(livingEntity.blockPosition().below()) == Fluids.WATER.getSource()
                        .defaultFluidState().createLegacyBlock() && time > 0) {
                    livingEntity.getCommandSenderWorld().setBlockAndUpdate(livingEntity.blockPosition().below(), Blocks.FROSTED_ICE.defaultBlockState());
                    NBTUtils.setInt(stack, TAG_SPEEDUP_TIME, time - 1);
                } else {
                    NBTUtils.setInt(stack, TAG_SPEEDUP_TIME, 0);
                    EntityUtils.removeAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(),
                            config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
            }
        }
    }

    @Override
    public void onUnequip(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        EntityUtils.removeAttributeModifier(livingEntity.getAttribute(Attributes.MOVEMENT_SPEED),
                new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.COLD;
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    private final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/items/models/ice_skates.png");

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        IceSkatesModel model = new IceSkatesModel();
        matrixStack.pushPose();
        matrixStack.scale(1.025F, 1.025F, 1.025F);
        model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTicks);
        ICurio.RenderHelper.followBodyRotations(livingEntity, model);
        model.renderToBuffer(matrixStack, renderTypeBuffer.getBuffer(RenderType.entityCutout(TEXTURE)),
                light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
    }

    @Override
    public boolean canRender(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        return true;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class IceSkatesEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SCARAB_TALISMAN.get(), player).isPresent()
                        && event.getSource() == DamageSource.FALL
                        && player.getCommandSenderWorld().getBlockState(player.blockPosition().below()).is(BlockTags.ICE)) {
                    event.setAmount(event.getAmount() * config.fallingDamageMultiplier);
                }
            }
        }
    }

    public static class Stats extends RelicStats {
        public float speedModifier = 1.1F;
        public float maxSpeedupTime = 10;
        public int speedupsPerRam = 2;
        public int ramRadius = 1;
        public float baseRamDamage = 2.0F;
        public float fallingDamageMultiplier = 0.25F;
    }
}