package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.ShiftTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class ChorusInhibitorItem extends RelicItem<ChorusInhibitorItem.Stats> implements ICurioItem {
    public static ChorusInhibitorItem INSTANCE;

    public ChorusInhibitorItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(LootTables.END_CITY_TREASURE.toString())
                        .chance(0.1F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .shift(ShiftTooltip.builder()
                        .arg(config.maxDistance)
                        .active(Minecraft.getInstance().options.keyUse)
                        .build())
                .build();
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ChorusInhibitorEvents {
        @SubscribeEvent
        public static void onChorusTeleport(EntityTeleportEvent.ChorusFruit event) {
            Stats config = INSTANCE.config;

            if (!(event.getEntityLiving() instanceof PlayerEntity))
                return;

            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.CHORUS_INHIBITOR.get(), player).ifPresent(triple -> {

                if (isBroken(triple.getRight()))
                    return;

                World world = player.getCommandSenderWorld();
                Vector3d view = player.getViewVector(0);
                Vector3d eyeVec = player.getEyePosition(0);
                BlockRayTraceResult ray = world.clip(new RayTraceContext(eyeVec, eyeVec.add(view.x * config.maxDistance, view.y * config.maxDistance,
                        view.z * config.maxDistance), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player));
                BlockPos pos = ray.getBlockPos();

                if (!world.getBlockState(pos).getMaterial().isSolid())
                    return;

                pos = pos.above();

                for (int i = 0; i < config.safeChecks; i++) {
                    if (world.getBlockState(pos).getMaterial().blocksMotion() || world.getBlockState(pos.above()).getMaterial().blocksMotion()) {
                        pos = pos.above();

                        continue;
                    }

                    event.setCanceled(true);

                    player.teleportTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                    world.playSound(null, pos, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

                    break;
                }
            });
        }
    }

    public static class Stats extends RelicStats {
        public int maxDistance = 50;
        public int safeChecks = 5;
    }
}