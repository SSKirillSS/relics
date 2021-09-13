package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.renderer.JellyfishNecklaceModel;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class JellyfishNecklaceItem extends RelicItem<JellyfishNecklaceItem.Stats> implements ICurioItem {
    public static JellyfishNecklaceItem INSTANCE;

    public JellyfishNecklaceItem() {
        super(Rarity.RARE);

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .varArg("+" + (int) (config.healMultiplier * 100 - 100) + "%")
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .varArg("+" + (int) (config.magicResistance * 100) + "%")
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .varArg("-" + (int) (Math.abs(config.swimSpeedModifier) * 100 - 100) + "%")
                        .negative()
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .active()
                        .build())
                .build();
    }

    @Override
    public void castAbility(PlayerEntity player, ItemStack stack) {
        if (!player.isUnderWater() || player.getCooldowns().isOnCooldown(stack.getItem()))
            return;

        float rot = ((float) Math.PI / 180F);
        float f0 = MathHelper.cos(player.xRot * rot);
        float f1 = -MathHelper.sin(player.yRot * rot) * f0;
        float f2 = -MathHelper.sin(player.xRot * rot);
        float f3 = MathHelper.cos(player.yRot * rot) * f0;
        float f4 = MathHelper.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
        float f5 = config.riptidePower;
        f1 *= (f5 / f4);
        f2 *= (f5 / f4);
        f3 *= (f5 / f4);

        player.push(f1, f2, f3);
        player.startAutoSpinAttack(20);

        player.getCooldowns().addCooldown(stack.getItem(), config.riptideCooldown * 20);

        player.getCommandSenderWorld().playSound(null, player, SoundEvents.TRIDENT_RIPTIDE_3, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> result = super.getAttributeModifiers(slotContext, uuid, stack);

        result.put(ForgeMod.SWIM_SPEED.get(), new AttributeModifier(uuid, Reference.MODID + ":" + "ice_breaker_movement_speed",
                config.swimSpeedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));

        return result;
    }

    @Override
    public boolean showAttributesTooltip(String identifier, ItemStack stack) {
        return false;
    }

    @Override
    public boolean hasAbility() {
        return true;
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.AQUATIC;
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    private final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/items/models/jellyfish_necklace.png");

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        JellyfishNecklaceModel model = new JellyfishNecklaceModel();

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
    public static class JellyfishNecklaceEvents {
        @SubscribeEvent
        public static void onEntityHeal(LivingHealEvent event) {
            Stats config = INSTANCE.config;
            LivingEntity entity = event.getEntityLiving();

            if (entity.isInWater() && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.JELLYFISH_NECKLACE.get(), entity).isPresent())
                event.setAmount(event.getAmount() * config.healMultiplier);
        }

        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;

            if (event.getSource() == DamageSource.MAGIC && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.JELLYFISH_NECKLACE.get(),
                    event.getEntityLiving()).isPresent())
                event.setAmount(event.getAmount() * config.magicResistance);
        }
    }

    public static class Stats extends RelicStats {
        public float swimSpeedModifier = -0.2F;
        public float magicResistance = 0.25F;
        public float healMultiplier = 3.0F;
        public int riptideCooldown = 5;
        public float riptidePower = 3.0F;
    }
}