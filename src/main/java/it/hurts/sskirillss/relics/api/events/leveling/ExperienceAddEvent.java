package it.hurts.sskirillss.relics.api.events.leveling;

import it.hurts.sskirillss.relics.api.events.base.RelicEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;

@Cancelable
public class ExperienceAddEvent extends RelicEvent {
    @Getter
    @Setter
    private int amount;

    public ExperienceAddEvent(@Nullable Player player, ItemStack stack, int amount) {
        super(player, stack);

        this.amount = amount;
    }
}