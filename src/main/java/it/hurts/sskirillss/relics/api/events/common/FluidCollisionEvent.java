package it.hurts.sskirillss.relics.api.events.common;

import lombok.Getter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class FluidCollisionEvent extends LivingEvent {
    @Getter
    private final FluidState fluid;

    public FluidCollisionEvent(LivingEntity entity, FluidState fluid) {
        super(entity);

        this.fluid = fluid;
    }
}