package it.hurts.sskirillss.relics.api.events.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@AllArgsConstructor
public class RelicEvent extends Event {
    @Getter
    @Nullable
    Player player;

    @Getter
    ItemStack stack;
}