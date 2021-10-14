package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.renderer.EndersHandModel;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.function.Predicate;

public class EndersHandItem extends RelicItem<EndersHandItem.Stats> implements ICurioItem {
    public static final String TAG_UPDATE_TIME = "time";

    public EndersHandItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .model(new EndersHandModel())
                .loot(RelicLoot.builder()
                        .table(LootTables.END_CITY_TREASURE.toString())
                        .chance(0.1F)
                        .build())
                .loot(RelicLoot.builder()
                        .table(EntityType.ENDERMAN.getDefaultLootTable().toString())
                        .chance(0.01F)
                        .build())
                .build());
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .active(Minecraft.getInstance().options.keyShift)
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .active()
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity))
            return;

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
                        World world = player.getCommandSenderWorld();

                        player.teleportTo(enderman.getX(), enderman.getY(), enderman.getZ());
                        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

                        enderman.teleportTo(swapVec.x(), swapVec.y(), swapVec.z());
                        world.playSound(null, swapVec.x(), swapVec.y(), swapVec.z(),
                                SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

                        NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);

                        player.getCooldowns().addCooldown(stack.getItem(), config.cooldown * 20);
                    } else
                        NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
                } else if (time > 0)
                    NBTUtils.setInt(stack, TAG_UPDATE_TIME, time - 1);
            } else if (time > 0)
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, time - 1);
        }
    }

    @Override
    public void castAbility(PlayerEntity player, ItemStack stack) {
        if (player.getCommandSenderWorld().isClientSide)
            return;

        player.openMenu(new SimpleNamedContainerProvider((windowId, playerInv, playerEntity) ->
                ChestContainer.threeRows(windowId, playerInv, playerEntity.getEnderChestInventory()), stack.getDisplayName()));
        player.playSound(SoundEvents.ENDER_CHEST_OPEN, 1F, 1F);
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID, value = Dist.CLIENT)
    public static class EndersHandClientEvents {
        @SubscribeEvent
        public static void onFOVUpdate(FOVUpdateEvent event) {
            if (!CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.ENDERS_HAND.get(), event.getEntity()).isPresent())
                return;

            int time = NBTUtils.getInt(CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.ENDERS_HAND.get(),
                    event.getEntity()).get().getRight(), TAG_UPDATE_TIME, 0);

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