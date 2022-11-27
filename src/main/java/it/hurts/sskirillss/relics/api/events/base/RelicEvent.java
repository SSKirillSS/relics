package it.hurts.sskirillss.relics.api.events.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

@AllArgsConstructor
public class RelicEvent extends Event {
    Player player;

    @Getter
    ItemStack stack;

    @Nullable
    public Player getEntity() {
        return player;
    }
}