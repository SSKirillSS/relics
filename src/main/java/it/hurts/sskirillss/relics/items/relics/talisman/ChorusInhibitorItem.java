package it.hurts.sskirillss.relics.items.relics.talisman;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ChorusInhibitorItem extends RelicItem<ChorusInhibitorItem.Stats> {
    public static ChorusInhibitorItem INSTANCE;

    public ChorusInhibitorItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#6e0096", "#201425")
                .ability(AbilityTooltip.builder()
                        .arg(stats.maxDistance)
                        .active(Minecraft.getInstance().options.keyUse)
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .build();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ChorusInhibitorEvents {
        @SubscribeEvent
        public static void onChorusTeleport(EntityTeleportEvent.ChorusFruit event) {
            Stats stats = INSTANCE.stats;

            if (!(event.getEntityLiving() instanceof Player player))
                return;

            if (EntityUtils.findEquippedCurio(player, ItemRegistry.CHORUS_INHIBITOR.get()).isEmpty())
                return;

            Level world = player.getCommandSenderWorld();
            Vec3 view = player.getViewVector(0);
            Vec3 eyeVec = player.getEyePosition(0);
            BlockHitResult ray = world.clip(new ClipContext(eyeVec, eyeVec.add(view.x * stats.maxDistance, view.y * stats.maxDistance,
                    view.z * stats.maxDistance), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            BlockPos pos = ray.getBlockPos();

            if (!world.getBlockState(pos).getMaterial().isSolid())
                return;

            pos = pos.above();

            for (int i = 0; i < stats.safeChecks; i++) {
                if (world.getBlockState(pos).getMaterial().blocksMotion() || world.getBlockState(pos.above()).getMaterial().blocksMotion()) {
                    pos = pos.above();

                    continue;
                }

                event.setCanceled(true);

                player.teleportTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                world.playSound(null, pos, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

                break;
            }
        }
    }

    public static class Stats extends RelicStats {
        public int maxDistance = 50;
        public int safeChecks = 5;
    }
}