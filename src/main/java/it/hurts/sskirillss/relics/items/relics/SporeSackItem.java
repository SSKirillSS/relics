package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.items.relics.renderer.SporeSackModel;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Collections;
import java.util.List;

public class SporeSackItem extends RelicItem<SporeSackItem.Stats> implements ICurioItem {
    public static SporeSackItem INSTANCE;

    public SporeSackItem() {
        super(Rarity.UNCOMMON);

        INSTANCE = this;
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.spore_sack.shift_1"));
        return tooltip;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class SporeSackEvents {
        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            Stats config = INSTANCE.config;
            if (!(event.getEntity() instanceof ProjectileEntity)) return;
            ProjectileEntity projectile = (ProjectileEntity) event.getEntity();
            if (projectile.getOwner() == null || !(projectile.getOwner() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) projectile.getOwner();
            World world = projectile.getCommandSenderWorld();
            if (world.isClientSide()) return;
            if (!CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SPORE_SACK.get(), player).isPresent()
                    || player.getCooldowns().isOnCooldown(ItemRegistry.SPORE_SACK.get())
                    || world.getRandom().nextFloat() > config.chance) return;
            ((ServerWorld) world).sendParticles(new RedstoneParticleData(0, 255, 0, 1),
                    projectile.getX(), projectile.getY(), projectile.getZ(), 100, 1, 1, 1, 0.5);
            world.playSound(null, projectile.blockPosition(), SoundEvents.FIRE_EXTINGUISH,
                    SoundCategory.PLAYERS, 1.0F, 0.5F);
            player.getCooldowns().addCooldown(ItemRegistry.SPORE_SACK.get(), config.cooldown * 20);
            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, projectile.getBoundingBox().inflate(config.radius))) {
                if (entity == player) continue;
                entity.addEffect(new EffectInstance(Effects.POISON, config.poisonDuration * 20, config.poisonAmplifier));
                entity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, config.slownessDuration * 20, config.slownessAmplifier));
            }
        }
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return Collections.singletonList(LootTables.JUNGLE_TEMPLE);
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    private final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/items/models/spore_sack.png");

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        SporeSackModel model = new SporeSackModel();
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

    public static class Stats extends RelicStats {
        public float chance = 0.3F;
        public int radius = 3;
        public int cooldown = 5;
        public int poisonAmplifier = 2;
        public int poisonDuration = 5;
        public int slownessAmplifier = 0;
        public int slownessDuration = 5;
    }
}