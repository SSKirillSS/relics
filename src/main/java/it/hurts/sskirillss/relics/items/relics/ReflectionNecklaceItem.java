package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketPlayerMotion;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
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
import net.minecraft.util.*;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.ArrayUtils;
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

    public static final ModelResourceLocation RL = new ModelResourceLocation(new ResourceLocation(Reference.MODID, "rn_shield"), "inventory");
    private static final Direction[] DIR = ArrayUtils.add(Direction.values(), null);

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing,
                       float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
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
                        player.getEntityWorld().playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                                SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
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
                    player.getEntityWorld().playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                            SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
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

        @SubscribeEvent
        public static void onPlayerRender(RenderPlayerEvent event) {
            if (!event.getPlayer().isSpectator() && !event.getPlayer().isInvisible()
                    && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.REFLECTION_NECKLACE.get(), event.getPlayer()).isPresent()) {
                int charges = NBTUtils.getInt(CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.REFLECTION_NECKLACE.get(),
                        event.getPlayer()).get().getRight(), TAG_CHARGE_AMOUNT, 0);
                PlayerEntity player = event.getPlayer();
                MatrixStack matrixStack = event.getMatrixStack();
                IBakedModel model = Minecraft.getInstance().getModelManager().getModel(RL);
                if (charges > 0) {
                    for (int i = 0; i < charges; i++) {
                        matrixStack.push();
                        matrixStack.scale(2F, 2F, 2F);
                        float f = player.getSwimAnimation(player.ticksExisted);
                        if (player.isElytraFlying()) {
                            matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F - player.rotationYaw));
                            float f1 = (float)player.getTicksElytraFlying() + player.ticksExisted;
                            float f2 = MathHelper.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
                            if (!player.isSpinAttacking()) {
                                matrixStack.rotate(Vector3f.XP.rotationDegrees(f2 * (-90.0F - player.rotationPitch)));
                            }

                            Vector3d vector3d = player.getLook(player.ticksExisted);
                            Vector3d vector3d1 = player.getMotion();
                            double d0 = Entity.horizontalMag(vector3d1);
                            double d1 = Entity.horizontalMag(vector3d);
                            if (d0 > 0.0D && d1 > 0.0D) {
                                double d2 = (vector3d1.x * vector3d.x + vector3d1.z * vector3d.z) / Math.sqrt(d0 * d1);
                                double d3 = vector3d1.x * vector3d.z - vector3d1.z * vector3d.x;
                                matrixStack.rotate(Vector3f.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
                            }
                        } else if (f > 0.0F) {
                            float f3 = player.isInWater() ? -90.0F - player.rotationPitch : -90.0F;
                            float f4 = MathHelper.lerp(f, 0.0F, f3);
                            matrixStack.rotate(Vector3f.XP.rotationDegrees(f4));
                            if (player.isActualySwimming()) {
                                matrixStack.translate(0.0D, -1.0D, (double)0.3F);
                            }
                        }
                        matrixStack.translate(0, 0.75, 0);
                        matrixStack.rotate(Vector3f.ZP.rotationDegrees((MathHelper.cos(player.ticksExisted / 10.0F) / 7.0F) * (180F / (float) Math.PI)));
                        matrixStack.rotate(Vector3f.YP.rotationDegrees((player.ticksExisted / 10.0F) * (180F / (float) Math.PI) + (i * (360F / charges))));
                        matrixStack.rotate(Vector3f.XP.rotationDegrees((MathHelper.sin(player.ticksExisted / 10.0F) / 7.0F) * (180F / (float) Math.PI)));
                        matrixStack.translate(-0.5, -0.75, -1);
                        for (Direction dir : DIR) {
                            Minecraft.getInstance().getItemRenderer().renderQuads(
                                    matrixStack, event.getBuffers().getBuffer(Atlases.getCutoutBlockType()),
                                    model.getQuads(null, dir, player.getEntityWorld().getRandom(), EmptyModelData.INSTANCE),
                                    ItemStack.EMPTY, event.getLight(), OverlayTexture.NO_OVERLAY);
                        }
                        matrixStack.pop();
                    }
                }
            }
        }
    }
}