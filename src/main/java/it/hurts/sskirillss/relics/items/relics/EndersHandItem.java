package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.items.relics.renderer.EndersHandModel;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.function.Predicate;

public class EndersHandItem extends RelicItem<EndersHandItem.Stats> implements ICurioItem {
    public static final String TAG_UPDATE_TIME = "time";

    public EndersHandItem() {
        super(Rarity.RARE);
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.enders_hand.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.enders_hand.shift_2"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.enders_hand.shift_3"));
        return tooltip;
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) livingEntity;
        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);
        if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
            if (player.isShiftKeyDown()) {
                Predicate<Entity> predicate = (entity) -> !entity.isSpectator() && entity.isPickable();
                EntityRayTraceResult result = EntityUtils.rayTraceEntity(player, predicate, config.maxDistance);
                if (result != null && result.getEntity() instanceof EndermanEntity) {
                    if (time >= config.preparationTime * 20) {
                        Vector3d swapVec = player.position();
                        EndermanEntity enderman = (EndermanEntity) result.getEntity();
                        player.teleportTo(enderman.getX(), enderman.getY(), enderman.getZ());
                        player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        enderman.teleportTo(swapVec.x(), swapVec.y(), swapVec.z());
                        player.getCommandSenderWorld().playSound(null, swapVec.x(), swapVec.y(), swapVec.z(),
                                SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                        player.getCooldowns().addCooldown(stack.getItem(), config.cooldown * 20);
                    } else NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
                } else if (time > 0) NBTUtils.setInt(stack, TAG_UPDATE_TIME, time - 1);
            } else if (time > 0) NBTUtils.setInt(stack, TAG_UPDATE_TIME, time - 1);
        }
    }

    @Override
    public void castAbility(PlayerEntity player, ItemStack stack) {
        if (player.getCommandSenderWorld().isClientSide) return;
        player.openMenu(new SimpleNamedContainerProvider((windowId, playerInv, playerEntity) ->
                ChestContainer.threeRows(windowId, playerInv, playerEntity.getEnderChestInventory()), stack.getDisplayName()));
        player.playSound(SoundEvents.ENDER_CHEST_OPEN, 1F, 1F);
    }

    @Override
    public boolean hasAbility() {
        return true;
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.CAVE;
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    private final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/items/models/enders_hand.png");

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        EndersHandModel model = new EndersHandModel();
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

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class EndersHandClientEvents {
        @SubscribeEvent
        public static void onFOVUpdate(FOVUpdateEvent event) {
            if (!CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.ENDERS_HAND.get(), event.getEntity()).isPresent()) return;
            int time = NBTUtils.getInt(CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.ENDERS_HAND.get(),
                    event.getEntity()).get().getRight(), TAG_UPDATE_TIME, 0);
            if (time > 0) event.setNewfov(event.getNewfov() - time / 32.0F);
        }
    }

    public static class Stats extends RelicStats {
        public int preparationTime = 1;
        public int maxDistance = 64;
        public int cooldown = 0;
    }
}