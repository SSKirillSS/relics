package it.hurts.sskirillss.relics.items.relics.ring;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.indev.*;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Random;

public class CamouflageRingItem extends RelicItem {
    private static final String TAG_TIME = "time";
    private static final String TAG_POS = "pos";

    public CamouflageRingItem() {
        super(RelicData.builder()
                .rarity(Rarity.UNCOMMON)
                .build());
    }

    @Override
    public RelicDataNew getNewData() {
        return RelicDataNew.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("morph", RelicAbilityEntry.builder()
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 200))
                .styleData(RelicStyleData.builder()
                        .borders("#008cd7", "#0a3484")
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        Level level = player.getLevel();

        if (level.isClientSide())
            return;

        Vec3 oldPos = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POS, ""));
        int time = NBTUtils.getInt(stack, TAG_TIME, 0);

        if (oldPos == null) {
            oldPos = player.position();

            NBTUtils.setString(stack, TAG_POS, NBTUtils.writePosition(oldPos));
        }

        if (oldPos.distanceTo(player.position()) > 0.05F || player.isShiftKeyDown()
                || player.isPassenger() || player.isVisuallySwimming()) {
            if (time > 0) {
                NBTUtils.setInt(stack, TAG_TIME, 0);

                int requiredTime = 100 / 2;

                if (time > requiredTime) {
                    double difference = Math.ceil(player.getY()) - player.getY();

                    BlockPos pos = difference > 0 ? player.blockPosition() : player.blockPosition().below();
                    BlockState state = level.getBlockState(pos);

                    if (state.isAir())
                        state = level.getBlockState(player.getBlockPosBelowThatAffectsMyMovement());

                    if (!state.isAir()) {
                        float scale = (time - requiredTime) / (requiredTime * 1F) * 0.25F;

                        ((ServerLevel) level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                                pos.getX() + 0.5F, pos.getY() + 1.5F, pos.getZ() + 0.5F,
                                Math.round(scale * 100), scale, scale, scale, scale * 0.1F);

                        level.playSound(null, pos, state.getSoundType().getBreakSound(), SoundSource.PLAYERS, 1F, 1F);
                    }
                }
            }

            NBTUtils.setString(stack, TAG_POS, NBTUtils.writePosition(player.position()));

            return;
        }

        if (time < 100)
            NBTUtils.setInt(stack, TAG_TIME, ++time);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class CamouflageRingClientEvents {
        @SubscribeEvent
        public static void onEntityRender(RenderPlayerEvent.Pre event) {
            Player player = event.getPlayer();

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.CAMOUFLAGE_RING.get());

            if (stack.isEmpty())
                return;

            int requiredTime = 100 / 2;
            int time = NBTUtils.getInt(stack, TAG_TIME, 0);

            if (time <= requiredTime)
                return;

            double difference = Math.ceil(player.getY()) - player.getY();

            BlockPos pos = difference > 0 ? player.blockPosition() : player.blockPosition().below();
            Level level = player.getLevel();
            BlockState state = level.getBlockState(pos);

            boolean isGiant = false;

            if (state.isAir()) {
                isGiant = true;

                state = level.getBlockState(player.getBlockPosBelowThatAffectsMyMovement());
            }

            if (state.isAir() || state.getMaterial().isLiquid())
                return;

            Minecraft MC = Minecraft.getInstance();
            PoseStack poseStack = event.getPoseStack();
            BlockRenderDispatcher dispatcher = MC.getBlockRenderer();

            float scale = (time - requiredTime) / (requiredTime * 1F);
            float offset = (1F - scale) / 2;

            poseStack.pushPose();

            double x = player.getX();
            double z = player.getZ();

            poseStack.translate(-(x - Math.ceil(x)) - 1 + offset, isGiant ? -difference + offset : difference + offset, -(z - Math.ceil(z)) - 1 + offset);

            poseStack.scale(scale, scale, scale);

            for (RenderType type : RenderType.chunkBufferLayers()) {
                if (ItemBlockRenderTypes.canRenderInLayer(state, type)) {
                    ForgeHooksClient.setRenderType(type);

                    dispatcher.getModelRenderer().tesselateBlock(player.getLevel(), dispatcher.getBlockModel(state),
                            state, pos.above(), poseStack, event.getMultiBufferSource().getBuffer(type),
                            false, new Random(), state.getSeed(pos), OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
                }
            }

            ForgeHooksClient.setRenderType(null);

            poseStack.popPose();

            if (time < 100) {
                scale = offset * 2;

                poseStack.scale(scale, scale, scale);
            } else {
                event.setCanceled(true);

                poseStack.scale(0F, 0F, 0F);
            }
        }
    }
}