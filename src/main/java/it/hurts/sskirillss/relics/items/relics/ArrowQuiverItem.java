package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.items.relics.renderer.ArrowQuiverModel;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Collections;
import java.util.List;

public class ArrowQuiverItem extends RelicItem<ArrowQuiverItem.Stats> implements ICurioItem {
    public static ArrowQuiverItem INSTANCE;

    public ArrowQuiverItem() {
        super(Rarity.COMMON);

        INSTANCE = this;
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.arrow_quiver.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.arrow_quiver.shift_2"));
        return tooltip;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) livingEntity;
        if (player.getCooldowns().isOnCooldown(stack.getItem())) return;
        if (!player.isShiftKeyDown()) return;
        if (player.tickCount % config.pickupCooldown != 0) return;
        int radius = config.pickupRadius;
        for (AbstractArrowEntity arrow : player.getCommandSenderWorld().getEntitiesOfClass(AbstractArrowEntity.class,
                player.getBoundingBox().inflate(radius, radius, radius))) {
            if (arrow.pickup != AbstractArrowEntity.PickupStatus.ALLOWED) continue;
            arrow.playerTouch(player);
            break;
        }
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return Collections.singletonList(LootTables.VILLAGE_FLETCHER);
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    private final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/items/models/arrow_quiver.png");

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        ArrowQuiverModel model = new ArrowQuiverModel();
        matrixStack.pushPose();
        ICurio.RenderHelper.translateIfSneaking(matrixStack, livingEntity);
        ICurio.RenderHelper.rotateIfSneaking(matrixStack, livingEntity);
        model.renderToBuffer(matrixStack, renderTypeBuffer.getBuffer(RenderType.entityCutout(TEXTURE)),
                light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
    }

    @Override
    public boolean canRender(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        return true;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ArrowQuiverEvents {
        @SubscribeEvent
        public static void onArrowShoot(ArrowLooseEvent event) {
            Stats config = INSTANCE.config;
            if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            if (player.getCooldowns().isOnCooldown(ItemRegistry.ARROW_QUIVER.get())) return;
            if (!CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.ARROW_QUIVER.get(), player).isPresent()) return;
            if (random.nextFloat() > config.multishotChance) return;
            for (int i = 0; i < config.additionalArrows; i++) {
                ItemStack bow = player.getMainHandItem();
                ItemStack ammo = player.getProjectile(bow);
                AbstractArrowEntity projectile = ((ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW))
                        .createArrow(player.getCommandSenderWorld(), ammo, player);
                projectile.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                projectile.shootFromRotation(player, player.xRot, player.yRot, 0.0F, BowItem.getPowerForTime(event.getCharge()) * 3.0F, 5.0F);
                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, bow) > 0)
                    projectile.setBaseDamage(projectile.getBaseDamage() + (double) EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, bow) * 0.5D + 0.5D);
                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, bow) > 0)
                    projectile.setKnockback(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, bow));
                if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, bow) > 0)
                    projectile.setSecondsOnFire(100);
                player.getCommandSenderWorld().addFreshEntity(projectile);
            }
            player.getCooldowns().addCooldown(ItemRegistry.ARROW_QUIVER.get(), config.multishotCooldown * 20);
        }
    }

    public static class Stats extends RelicStats {
        public float multishotChance = 0.2F;
        public int multishotCooldown = 2;
        public int additionalArrows = 4;
        public int pickupRadius = 16;
        public int pickupCooldown = 3;
    }
}