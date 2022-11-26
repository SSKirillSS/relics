package it.hurts.sskirillss.relics.client.handlers;

import it.hurts.sskirillss.relics.tiles.base.IHasHUDInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class TileHUDInfoHandler {
    @SubscribeEvent
    public static void onRenderHUD(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.CHAT)
            return;

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

        infoTile.renderHUDInfo(event.getMatrixStack(), event.getWindow());
    }
}