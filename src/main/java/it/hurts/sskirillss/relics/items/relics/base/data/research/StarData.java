package it.hurts.sskirillss.relics.items.relics.base.data.research;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.world.phys.Vec2;

@Data
@AllArgsConstructor
public class StarData {
    private int index;

    private int x;
    private int y;

    public Vec2 getPos() {
        return new Vec2(x, y);
    }
}