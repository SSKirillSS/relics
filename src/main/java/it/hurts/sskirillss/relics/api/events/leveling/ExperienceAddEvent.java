package it.hurts.sskirillss.relics.api.events.leveling;

import it.hurts.sskirillss.relics.api.events.base.RelicEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;

@Cancelable
public class ExperienceAddEvent extends RelicEvent {
    @Getter
    @Setter
    private int amount;

    public ExperienceAddEvent(@Nullable LivingEntity entity, ItemStack stack, int amount) {
        super(entity, stack);

        this.amount = amount;
    }
}