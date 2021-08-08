package it.hurts.sskirillss.relics.items;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.PacketPlayerMotion;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

public abstract class RelicItem<T extends RelicStats> extends Item implements ICurioItem {
    protected T config;

    public RelicItem(Rarity rarity) {
        super(new Item.Properties()
                .tab(RelicsTab.RELICS_TAB)
                .stacksTo(1)
                .rarity(rarity));
    }

    public RelicItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!(entityIn instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) entityIn;
        handleOwner(player, worldIn, stack);
        if (RelicUtils.Durability.getDurability(stack) == -1) RelicUtils.Durability.setDurability(stack, RelicUtils.Durability.getMaxDurability(stack.getItem()));
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    private static void handleOwner(PlayerEntity player, World world, ItemStack stack) {
        PlayerEntity owner = RelicUtils.Owner.getOwner(stack, world);
        if (owner == null || world.isClientSide()) return;
        int contract = NBTUtils.getInt(stack, RelicContractItem.TAG_DATE, 0);
        if (contract == 0) NBTUtils.setInt(stack, RelicContractItem.TAG_DATE, (int) world.getGameTime());
        else if (world.getGameTime() - contract >= (3600 * 20)) {
            NBTUtils.setInt(stack, RelicContractItem.TAG_DATE, -1);
            RelicUtils.Owner.setOwnerUUID(stack, "");
            return;
        }
        if (!owner.getStringUUID().equals(player.getStringUUID()) && !player.isCreative() && !player.isSpectator()) {
            player.drop(stack.copy(), false, true);
            stack.shrink(1);
            player.setDeltaMovement(player.getViewVector(0F).multiply(-1F, -1F, -1F).normalize());
            world.addParticle(ParticleTypes.EXPLOSION, player.getX(), player.getY() + 1, player.getZ(), 0, 0, 0);
            world.playSound(player, player.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundCategory.PLAYERS, 1F, 1F);
        }
    }

    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        return new ArrayList<>();
    }

    public List<ITextComponent> getAltTooltip(ItemStack stack) {
        return new ArrayList<>();
    }

    public List<ITextComponent> getControlTooltip(ItemStack stack) {
        return new ArrayList<>();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn == null) return;
        PlayerEntity owner = RelicUtils.Owner.getOwner(stack, worldIn);
        long time = (NBTUtils.getLong(stack, RelicContractItem.TAG_DATE, 0) + (3600 * 20) - worldIn.getGameTime()) / 20;
        if (time > 0 && owner != null) {
            long hours = time / 3600;
            long minutes = (time % 3600) / 60;
            long seconds = (time % 3600) % 60;
            tooltip.add(new TranslationTextComponent("tooltip.relics.contract", owner.getDisplayName(), hours, minutes, seconds));
        }
        int durability = RelicUtils.Durability.getDurability(stack);
        tooltip.add(new TranslationTextComponent("tooltip.relics.durability",
                durability == -1 ? 0 : durability, RelicUtils.Durability.getMaxDurability(stack.getItem())));
//        int level = RelicUtils.Level.getLevel(stack);
//        int prevExp = RelicUtils.Level.getTotalExperienceForLevel(stack, Math.max(level, level - 1));
//        tooltip.add(new TranslationTextComponent("tooltip.relics.level", level, RelicUtils.Level.getExperience(stack) - prevExp,
//                RelicUtils.Level.getTotalExperienceForLevel(stack, level + 1) - prevExp));
//        float percentage = (RelicUtils.Level.getExperience(stack) - prevExp) * 1.0F / (RelicUtils.Level.getTotalExperienceForLevel(stack,
//                RelicUtils.Level.getLevel(stack) + 1) - prevExp) * 100;
//        tooltip.add(TooltipUtils.drawProgressBar(percentage, RelicsConfig.RelicsGeneral.LEVELING_BAR_STYLE.get(),
//                RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_LOW.get(), RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_MEDIUM.get(),
//                RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_HIGH.get(), RelicsConfig.RelicsGeneral.LEVELING_BAR_COLOR_NEUTRAL.get(), true));
        if (!getShiftTooltip(stack).isEmpty() && Screen.hasShiftDown()) {
            tooltip.add(new StringTextComponent(" "));
            tooltip.add(new TranslationTextComponent("tooltip.relics.shift.divider_up"));
            tooltip.addAll(getShiftTooltip(stack));
            tooltip.add(new TranslationTextComponent("tooltip.relics.shift.divider_down"));
        }
        if (!getAltTooltip(stack).isEmpty() && Screen.hasAltDown()) {
            tooltip.add(new StringTextComponent(" "));
            tooltip.add(new TranslationTextComponent("tooltip.relics.alt.divider_up"));
            tooltip.addAll(getAltTooltip(stack));
            tooltip.add(new TranslationTextComponent("tooltip.relics.alt.divider_sown"));
        }
        if (!getControlTooltip(stack).isEmpty() && Screen.hasControlDown()) {
            tooltip.add(new StringTextComponent(" "));
            tooltip.add(new TranslationTextComponent("tooltip.relics.control.divider_up"));
            tooltip.addAll(getControlTooltip(stack));
            tooltip.add(new TranslationTextComponent("tooltip.relics.control.divider_down"));
        }
        if ((!getShiftTooltip(stack).isEmpty() && !Screen.hasShiftDown()) || (!getAltTooltip(stack).isEmpty() && !Screen.hasAltDown())
                || (!getControlTooltip(stack).isEmpty() && !Screen.hasControlDown())) tooltip.add(new StringTextComponent(" "));
        if (!Screen.hasShiftDown() && !getShiftTooltip(stack).isEmpty()) tooltip.add(new TranslationTextComponent("tooltip.relics.shift.tooltip"));
        if (!Screen.hasAltDown() && !getAltTooltip(stack).isEmpty()) tooltip.add(new TranslationTextComponent("tooltip.relics.alt.tooltip"));
        if (!Screen.hasControlDown() && !getControlTooltip(stack).isEmpty()) tooltip.add(new TranslationTextComponent("tooltip.relics.ctrl.tooltip"));
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    public int getMaxLevel() {
        return 10;
    }

    public int getInitialExp() {
        return 100;
    }

    public int getExpRatio() {
        return 250;
    }

    public float getWorldgenChance() {
        float chance = 0F;
        switch (new ItemStack(this).getRarity()) {
            case COMMON:
                chance = 0.15F;
                break;
            case UNCOMMON:
                chance = 0.125F;
                break;
            case RARE:
                chance = 0.1F;
                break;
            case EPIC:
                chance = 0.075F;
                break;
        }
        return chance;
    }

    public List<ResourceLocation> getLootChests() {
        return Lists.newArrayList();
    }

    public int getDurability() {
        return 100;
    }

    public Class<T> getConfigClass() {
        return (Class<T>) RelicStats.class;
    }

    public T getConfig() {
        return this.config;
    }

    public void setConfig(T config) {
        this.config = config;
    }

    public boolean hasAbility() {
        return false;
    }

    public void castAbility(PlayerEntity player, ItemStack stack) {

    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class RelicEvents {
        @SubscribeEvent
        public static void onItemPickup(EntityItemPickupEvent event) {
            ItemEntity drop = event.getItem();
            ItemStack stack = drop.getItem();
            PlayerEntity player = event.getPlayer();
            ServerWorld world = (ServerWorld) player.getCommandSenderWorld();
            if (!(stack.getItem() instanceof RelicItem) || world.getGameTime() - NBTUtils.getLong(stack, RelicContractItem.TAG_DATE, 0) >= (3600 * 20)) return;
            String uuid = RelicUtils.Owner.getOwnerUUID(stack);
            if (player.isCreative() || uuid.equals("") || uuid.equals(player.getStringUUID())) return;
            drop.setPickUpDelay(40);
            Vector3d motion = player.position().subtract(drop.position()).normalize();
            NetworkHandler.sendToClient(new PacketPlayerMotion(motion.x(), motion.y(), motion.z()), (ServerPlayerEntity) player);
            drop.setDeltaMovement(motion.multiply(-1.25F, -1.25F, -1.25F));
            world.sendParticles(ParticleTypes.EXPLOSION, drop.getX(), drop.getY() + 0.5F, drop.getZ(), 1, 0, 0, 0, 0);
            world.playSound(null, drop.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundCategory.PLAYERS, 1F, 1F);
            event.setCanceled(true);
        }
    }
}