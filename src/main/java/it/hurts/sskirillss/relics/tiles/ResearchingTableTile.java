package it.hurts.sskirillss.relics.tiles;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.tiles.base.IHasHUDInfo;
import it.hurts.sskirillss.relics.utils.Reference;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ResearchingTableTile extends BlockEntity implements IHasHUDInfo {
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
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);

        stack = ItemStack.parseOptional(provider, compound.getCompound("stack"));
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);

        if (stack != null && !stack.isEmpty())
            compound.put("stack", stack.save(provider, new CompoundTag()));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);

        this.saveAdditional(tag, provider);

        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHUDInfo(PoseStack poseStack, Window window) {
        Minecraft MC = Minecraft.getInstance();
        LocalPlayer player = MC.player;

        GuiGraphics gui = new GuiGraphics(MC, MC.renderBuffers().bufferSource());

        if (player == null)
            return;

        TextureManager manager = MC.getTextureManager();

        int scale = 2;

        if (!stack.isEmpty()) {
            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/hud/info/crouch_rmb.png");

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, texture);

            RenderSystem.enableBlend();

            poseStack.pushPose();

            int width = 99;
            int height = 20;

            int x = window.getGuiScaledWidth() / 2 - width / 2 / scale;
            int y = window.getGuiScaledHeight() / 2 + 10;

            manager.bindForSetup(texture);
            gui.blit(texture, x, y, width / scale, height / scale, 0, 0, width, height, width, height);

            poseStack.popPose();

            RenderSystem.disableBlend();
        } else if (player.getMainHandItem().getItem() instanceof IRelicItem) {
            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Reference.MODID, "textures/hud/info/rmb.png");

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, texture);

            RenderSystem.enableBlend();

            poseStack.pushPose();

            int width = 17;
            int height = 20;

            int x = window.getGuiScaledWidth() / 2 - width / 2 / scale;
            int y = window.getGuiScaledHeight() / 2 + 10;

            manager.bindForSetup(texture);
            gui.blit(texture, x, y, width / scale, height / scale, 0, 0, width, height, width, height);

            poseStack.popPose();

            RenderSystem.disableBlend();
        }
    }
}