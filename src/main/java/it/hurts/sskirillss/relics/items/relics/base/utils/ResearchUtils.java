package it.hurts.sskirillss.relics.items.relics.base.utils;

import it.hurts.sskirillss.relics.capability.utils.CapabilityUtils;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.network.NetworkHandler;
import it.hurts.sskirillss.relics.network.packets.capability.CapabilitySyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ResearchUtils {
    public static CompoundTag getResearchData(Player player) {
        return CapabilityUtils.getRelicsCapability(player).getResearchData();
    }

    public static void setResearchData(Player player, CompoundTag data) {
        CapabilityUtils.getRelicsCapability(player).setResearchData(data);

        if (!player.level.isClientSide())
            NetworkHandler.sendToClient(new CapabilitySyncPacket(CapabilityUtils.getRelicsCapability(player).serializeNBT()), (ServerPlayer) player);
    }

    public static boolean isItemResearched(Player player, ItemStack stack) {
        return isItemResearched(player, stack.getItem());
    }

    public static boolean isItemResearched(Player player, Item item) {
        return item instanceof RelicItem relic && isItemResearched(player, relic);
    }

    public static boolean isItemResearched(Player player, RelicItem relic) {
        return getResearchData(player).getBoolean(ForgeRegistries.ITEMS.getKey(relic).getPath() + "_researched");
    }

    public static void setItemResearched(Player player, ItemStack stack, boolean researched) {
        setItemResearched(player, stack.getItem(), researched);
    }

    public static void setItemResearched(Player player, Item item, boolean researched) {
        if (item instanceof RelicItem relic)
            setItemResearched(player, relic, researched);
    }

    public static void setItemResearched(Player player, RelicItem relic, boolean researched) {
        getResearchData(player).putBoolean(ForgeRegistries.ITEMS.getKey(relic).getPath() + "_researched", researched);

        if (!player.level.isClientSide())
            NetworkHandler.sendToClient(new CapabilitySyncPacket(CapabilityUtils.getRelicsCapability(player).serializeNBT()), (ServerPlayer) player);
    }
}