package it.hurts.sskirillss.relics.items.relics.back;

import it.hurts.sskirillss.relics.api.events.ContainerSlotClickEvent;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.indev.*;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

public class ElytraBoosterItem extends RelicItem {
    public static final String TAG_FUEL = "fuel";
    public static final String TAG_SPEED = "speed";

    public ElytraBoosterItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());
    }

    @Override
    public RelicDataNew getNewData() {
        return RelicDataNew.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("boost", RelicAbilityEntry.builder()
                                .stat("capacity", RelicAbilityStat.builder()
                                        .initialValue(50, 100)
                                        .upgradeModifier("add", 10)
                                        .build())
                                .stat("speed", RelicAbilityStat.builder()
                                        .initialValue(1.1D, 1.5D)
                                        .upgradeModifier("add", 0.1D)
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 20, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#b8b8d6", "#6e6e8f")
                        .build())
                .build();
    }

    public int getBreathCapacity(ItemStack stack) {
        return (int) Math.round(getAbilityValue(stack, "boost", "capacity"));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof Player player) || DurabilityUtils.isBroken(stack))
            return;

        int fuel = NBTUtils.getInt(stack, TAG_FUEL, 0);
        double speed = NBTUtils.getDouble(stack, TAG_SPEED, 1D);

        if (!player.isShiftKeyDown() || fuel <= 0 || !player.isFallFlying()) {
            NBTUtils.setDouble(stack, TAG_SPEED, 1D);

            return;
        }

        if (player.tickCount % 3 == 0) {
            double maxSpeed = getAbilityValue(stack, "boost", "speed");

            if (speed < maxSpeed) {
                speed = Math.min(maxSpeed, speed + ((maxSpeed - 1D) / 100D));

                NBTUtils.setDouble(stack, TAG_SPEED, speed);
            } else {
                player.startAutoSpinAttack(5);
            }
        }

        Vec3 look = player.getLookAngle();
        Vec3 motion = player.getDeltaMovement();
        Level world = player.getCommandSenderWorld();
        Random random = world.getRandom();

        player.setDeltaMovement(motion.add(look.x * 0.1D + (look.x * speed - motion.x) * 0.5D,
                look.y * 0.1D + (look.y * speed - motion.y) * 0.5D,
                look.z * 0.1D + (look.z * speed - motion.z) * 0.5D));

        for (int i = 0; i < speed * 3; i++)
            world.addParticle(ParticleTypes.SMOKE,
                    player.getX() + (MathUtils.randomFloat(random) * 0.4F),
                    player.getY() + (MathUtils.randomFloat(random) * 0.4F),
                    player.getZ() + (MathUtils.randomFloat(random) * 0.4F),
                    0, 0, 0);

        if (player.tickCount % Math.max(1, (int) Math.round((10 - speed * 2) / (player.isInWaterOrRain() ? 2 : 1))) == 0)
            NBTUtils.setInt(stack, TAG_FUEL, fuel - 1);
    }

    @Mod.EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onSlotClick(ContainerSlotClickEvent event) {
            if (event.getAction() != ClickAction.PRIMARY)
                return;

            Player player = event.getPlayer();

            ItemStack heldStack = event.getHeldStack();
            ItemStack slotStack = event.getSlotStack();

            if (!(slotStack.getItem() instanceof ElytraBoosterItem booster))
                return;

            int time = ForgeHooks.getBurnTime(heldStack, RecipeType.SMELTING) / 20;
            int amount = NBTUtils.getInt(slotStack, TAG_FUEL, 0);

            if (time <= 0 || amount >= booster.getBreathCapacity(slotStack))
                return;

            NBTUtils.setInt(slotStack, TAG_FUEL, Math.min(booster.getBreathCapacity(slotStack), amount + time));

            ItemStack result = heldStack.getContainerItem();

            heldStack.shrink(1);

            if (!result.isEmpty()) {
                if (heldStack.isEmpty())
                    player.containerMenu.setCarried(result);
                else
                    player.getInventory().add(result);
            }

            player.playSound(SoundEvents.BLAZE_SHOOT, 0.75F, 2F / (time * 0.025F));

            event.setCanceled(true);
        }
    }
}