package it.hurts.sskirillss.relics.client.gui.layers;

import it.hurts.sskirillss.relics.tiles.base.IHasHUDInfo;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class InfoTileLayer implements LayeredDraw.Layer {
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft MC = Minecraft.getInstance();

        ClientLevel level = MC.level;

        if (level == null)
            return;

        HitResult hit = MC.hitResult;

        if (hit == null || hit.getType() != HitResult.Type.BLOCK)
            return;

        BlockPos pos = ((BlockHitResult) MC.hitResult).getBlockPos();
        BlockEntity tile = level.getBlockEntity(pos);

        if (!(tile instanceof IHasHUDInfo infoTile))
            return;

        infoTile.renderHUDInfo(guiGraphics.pose(), MC.getWindow());
    }
}
