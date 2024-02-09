package it.hurts.sskirillss.relics.tiles;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.tiles.base.IHasHUDInfo;
import it.hurts.sskirillss.relics.tiles.base.TileBase;
import it.hurts.sskirillss.relics.utils.Reference;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ResearchingTableTile extends TileBase implements IHasHUDInfo {
    @Getter
    @Setter
    private ItemStack stack = ItemStack.EMPTY;

    public int ticksExisted;

    public ResearchingTableTile(BlockPos pos, BlockState state) {
        super(TileRegistry.RESEARCHING_TABLE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ResearchingTableTile tile) {
        if (level == null)
            return;

        tile.ticksExisted++;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);

        stack = ItemStack.of((CompoundTag) compound.get("stack"));
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);

        if (stack != null) {
            CompoundTag itemStack = new CompoundTag();

            stack.save(itemStack);

            compound.put("stack", itemStack);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();

        this.saveAdditional(tag);

        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);

        this.load(pkt.getTag());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHUDInfo(PoseStack poseStack, Window window) {
        Minecraft MC = Minecraft.getInstance();
        LocalPlayer player = MC.player;

        if (player == null)
            return;

        TextureManager manager = MC.getTextureManager();

        int scale = 2;

        if (!stack.isEmpty()) {
            ResourceLocation texture = new ResourceLocation(Reference.MODID, "textures/hud/info/crouch_rmb.png");

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, texture);

            RenderSystem.enableBlend();

            poseStack.pushPose();

            int width = 99;
            int height = 20;

            int x = window.getGuiScaledWidth() / 2 - width / 2 / scale;
            int y = window.getGuiScaledHeight() / 2 + 10;

            manager.bindForSetup(texture);
            Gui.blit(poseStack, x, y, width / scale, height / scale, 0, 0, width, height, width, height);

            poseStack.popPose();

            RenderSystem.disableBlend();
        } else if (player.getMainHandItem().getItem() instanceof IRelicItem) {
            ResourceLocation texture = new ResourceLocation(Reference.MODID, "textures/hud/info/rmb.png");

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, texture);

            RenderSystem.enableBlend();

            poseStack.pushPose();

            int width = 17;
            int height = 20;

            int x = window.getGuiScaledWidth() / 2 - width / 2 / scale;
            int y = window.getGuiScaledHeight() / 2 + 10;

            manager.bindForSetup(texture);
            Gui.blit(poseStack, x, y, width / scale, height / scale, 0, 0, width, height, width, height);

            poseStack.popPose();

            RenderSystem.disableBlend();
        }
    }
}