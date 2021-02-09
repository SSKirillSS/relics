package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketPlayerMotion;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class ReflectionNecklaceItem extends Item implements ICurioItem, IHasTooltip {
    public static final String TAG_CHARGE_AMOUNT = "charges";
    public static final String TAG_UPDATE_TIME = "time";

    public ReflectionNecklaceItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.reflection_necklace.shift_1"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity.ticksExisted % 20 == 0) {
            int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);
            int charges = NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0);
            if (charges < RelicsConfig.ReflectionNecklace.MAX_CHARGES.get()) {
                if (time < (charges > 0 ? RelicsConfig.ReflectionNecklace.MIN_TIME_PER_CHARGE.get()
                        * charges : RelicsConfig.ReflectionNecklace.MIN_TIME_PER_CHARGE.get())) {
                    NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
                } else {
                    NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                    NBTUtils.setInt(stack, TAG_CHARGE_AMOUNT, charges + 1);
                }
            }
        }
    }

    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/animations/rn_shield.png");

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        IVertexBuilder builder = renderTypeBuffer.getBuffer(RenderType.getEntityCutoutNoCull(TEXTURE));
        int charges = NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0);
        if (charges > 0) {
            for (int i = 0; i < charges; i++) {
                matrixStack.push();
                matrixStack.translate(0.0, 1.0, 0);
                matrixStack.scale(1.75F, -1.75F, 1.75F);
                matrixStack.rotate(Vector3f.ZP.rotationDegrees((MathHelper.cos(livingEntity.ticksExisted / 10.0F) / 7.0F) * (180F / (float) Math.PI)));
                matrixStack.rotate(Vector3f.YP.rotationDegrees((livingEntity.ticksExisted / 10.0F) * (180F / (float) Math.PI) + (i * (360F / charges))));
                matrixStack.rotate(Vector3f.XP.rotationDegrees((MathHelper.sin(livingEntity.ticksExisted / 10.0F) / 7.0F) * (180F / (float) Math.PI)));
                matrixStack.translate(0F, 0F, -0.5F);
                VertexUtils.addVertexPoint(builder, matrixStack.getLast().getMatrix(), matrixStack.getLast().getNormal(), light, 0.0F, 0, 0, 1);
                VertexUtils.addVertexPoint(builder, matrixStack.getLast().getMatrix(), matrixStack.getLast().getNormal(), light, 1.0F, 0, 1, 1);
                VertexUtils.addVertexPoint(builder, matrixStack.getLast().getMatrix(), matrixStack.getLast().getNormal(), light, 1.0F, 1, 1, 0);
                VertexUtils.addVertexPoint(builder, matrixStack.getLast().getMatrix(), matrixStack.getLast().getNormal(), light, 0.0F, 1, 0, 0);
                matrixStack.pop();
            }
        }

        ICurio.RenderHelper.translateIfSneaking(matrixStack, livingEntity);
        ICurio.RenderHelper.rotateIfSneaking(matrixStack, livingEntity);
        matrixStack.scale(0.35F, 0.35F, 0.35F);
        matrixStack.translate(0.0F, 0.3F, -0.4F);
        matrixStack.rotate(Direction.DOWN.getRotation());
        Minecraft.getInstance().getItemRenderer()
                .renderItem(new ItemStack(ItemRegistry.REFLECTION_NECKLACE.get()), ItemCameraTransforms.TransformType.NONE, light, OverlayTexture.NO_OVERLAY,
                        matrixStack, renderTypeBuffer);
    }

    @Override
    public boolean canRender(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        return true;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ReflectionNecklaceServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity
                    && (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.REFLECTION_NECKLACE.get(), event.getEntityLiving()).isPresent())) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.REFLECTION_NECKLACE.get(), event.getEntityLiving()).get().getRight();
                if (NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0) > 0
                        && event.getSource().getTrueSource() instanceof LivingEntity) {
                    LivingEntity attacker = (LivingEntity) event.getSource().getTrueSource();
                    if (player.getPositionVec().distanceTo(attacker.getPositionVec()) < RelicsConfig.ReflectionNecklace.MAX_THROW_DISTANCE.get()) {
                        Vector3d motion = attacker.getPositionVec().subtract(player.getPositionVec()).normalize().mul(2F, 1.5F, 2F);
                        if (attacker instanceof PlayerEntity) {
                            NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x, motion.y, motion.z), (ServerPlayerEntity) attacker);
                        } else {
                            attacker.setMotion(motion);
                        }
                        event.setCanceled(true);
                    }
                    attacker.attackEntityFrom(DamageSource.causePlayerDamage(player), event.getAmount() * RelicsConfig.ReflectionNecklace.REFLECTION_DAMAGE_MULTIPLIER.get().floatValue());
                    NBTUtils.setInt(stack, TAG_CHARGE_AMOUNT, NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0) - 1);
                }
            }
        }

        @SubscribeEvent
        public static void onProjectileImpact(ProjectileImpactEvent event) {
            if (!(event.getRayTraceResult() instanceof EntityRayTraceResult)) return;
            Entity undefinedProjectile = event.getEntity();
            Entity target = ((EntityRayTraceResult) event.getRayTraceResult()).getEntity();
            if (!(target instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) target;
            if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.REFLECTION_NECKLACE.get(), player).isPresent()) {
                ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.REFLECTION_NECKLACE.get(), player).get().getRight();
                if (NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0) > 0) {
                    undefinedProjectile.setMotion(undefinedProjectile.getMotion().inverse());
                    if (undefinedProjectile instanceof DamagingProjectileEntity) {
                        DamagingProjectileEntity projectile = (DamagingProjectileEntity) undefinedProjectile;
                        projectile.setShooter(player);
                        projectile.accelerationX *= -1;
                        projectile.accelerationY *= -1;
                        projectile.accelerationZ *= -1;
                    }
                    event.setCanceled(true);
                    undefinedProjectile.velocityChanged = true;
                    NBTUtils.setInt(stack, TAG_CHARGE_AMOUNT, NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0) - 1);
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class ReflectionNecklaceClientEvents {
        public static final ResourceLocation HUD_TEXTURE = new ResourceLocation(Reference.MODID, "textures/hud/rn_heart.png");

        @SubscribeEvent
        public static void onOverlayRender(RenderGameOverlayEvent.Pre event) {
            PlayerEntity player = (PlayerEntity) Minecraft.getInstance().getRenderViewEntity();
            if (player != null && !player.isCreative() && !player.isSpectator()
                    && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.REFLECTION_NECKLACE.get(), player).isPresent()) {
                ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.REFLECTION_NECKLACE.get(), player).get().getRight();
                Minecraft.getInstance().getTextureManager().bindTexture(HUD_TEXTURE);
                int x = event.getWindow().getScaledWidth() / 2 - 91;
                int y = event.getWindow().getScaledHeight() - 39;
                for (int i = 0; i < NBTUtils.getInt(stack, TAG_CHARGE_AMOUNT, 0); i++) {
                    AbstractGui.blit(event.getMatrixStack(), x, y, 9, 9, 0F, 0F, 1, 1, 1, 1);
                    x += 8;
                }
                Minecraft.getInstance().textureManager.bindTexture(AbstractGui.GUI_ICONS_LOCATION);
            }
        }
    }
}