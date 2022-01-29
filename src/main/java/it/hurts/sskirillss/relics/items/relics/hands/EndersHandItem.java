package it.hurts.sskirillss.relics.items.relics.hands;

import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.FOVModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Predicate;

public class EndersHandItem extends RelicItem<EndersHandItem.Stats> {
    public static final String TAG_UPDATE_TIME = "time";

    public EndersHandItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .hasAbility()
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .borders("#00c98f", "#027f44")
                .ability(AbilityTooltip.builder()
                        .build())
                .ability(AbilityTooltip.builder()
                        .active(Minecraft.getInstance().options.keyShift)
                        .build())
                .ability(AbilityTooltip.builder()
                        .active()
                        .build())
                .build();
    }

    @Override
    public RelicConfigData<Stats> getConfigData() {
        return RelicConfigData.<Stats>builder()
                .stats(new Stats())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof Player player))
            return;

        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);

        if (player.getCooldowns().isOnCooldown(stack.getItem()) || DurabilityUtils.isBroken(stack))
            return;

        if (player.isShiftKeyDown()) {
            Predicate<Entity> predicate = (entity) -> !entity.isSpectator() && entity.isPickable();
            EntityHitResult result = EntityUtils.rayTraceEntity(player, predicate, stats.maxDistance);

            if (result != null && result.getEntity() instanceof EnderMan) {
                if (time >= stats.preparationTime * 20) {
                    Vec3 swapVec = player.position();
                    EnderMan enderman = (EnderMan) result.getEntity();
                    Level world = player.getCommandSenderWorld();

                    player.teleportTo(enderman.getX(), enderman.getY(), enderman.getZ());
                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

                    enderman.teleportTo(swapVec.x(), swapVec.y(), swapVec.z());
                    world.playSound(null, swapVec.x(), swapVec.y(), swapVec.z(),
                            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

                    NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);

                    player.getCooldowns().addCooldown(stack.getItem(), stats.cooldown * 20);
                } else
                    NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
            } else if (time > 0)
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, time - 1);
        } else if (time > 0)
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, time - 1);
    }

    @Override
    public void castAbility(Player player, ItemStack stack) {
        if (player.getCommandSenderWorld().isClientSide)
            return;

        player.openMenu(new SimpleMenuProvider((windowId, playerInv, playerEntity) ->
                ChestMenu.threeRows(windowId, playerInv, playerEntity.getEnderChestInventory()), stack.getDisplayName()));
        player.level.playSound(player, player.blockPosition(), SoundEvents.ENDER_CHEST_OPEN, SoundSource.PLAYERS, 1F, 1F);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class EndersHandClientEvents {
        @SubscribeEvent
        public static void onFOVUpdate(FOVModifierEvent event) {
            ItemStack stack = EntityUtils.findEquippedCurio(event.getEntity(), ItemRegistry.ENDERS_HAND.get());

            if (stack.isEmpty())
                return;

            int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);

            if (time > 0)
                event.setNewfov(event.getNewfov() - time / 32.0F);
        }
    }

    public static class Stats extends RelicStats {
        public int preparationTime = 1;
        public int maxDistance = 64;
        public int cooldown = 0;
    }
}