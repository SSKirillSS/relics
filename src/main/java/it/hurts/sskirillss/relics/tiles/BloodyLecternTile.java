package it.hurts.sskirillss.relics.tiles;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.items.RelicContractItem;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;
import java.util.Random;

public class BloodyLecternTile extends TileBase {
    private ItemStack stack = ItemStack.EMPTY;
    public int ticksExisted;

    public BloodyLecternTile(BlockPos pos, BlockState state) {
        super(TileRegistry.BLOODY_LECTERN_TILE.get(), pos, state);
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BloodyLecternTile tile) {
        if (level == null)
            return;

        tile.ticksExisted++;

        ItemStack stack = tile.getStack();

        if (stack.isEmpty() || stack.getItem() != ItemRegistry.RELIC_CONTRACT.get())
            return;

        int blood = NBTUtils.getInt(stack, RelicContractItem.TAG_BLOOD, 0) + 1;

        if (blood == 0)
            return;

        Random random = level.getRandom();

        for (int i = 0; i < blood; i++)
            if (level.getRandom().nextInt(3) == 0)
                level.addParticle(new CircleTintData(new Color(255, 0, 0),
                                random.nextFloat() * 0.025F + 0.04F, 20, 0.94F, true),
                        pos.getX() + 0.5F + MathUtils.randomFloat(random) * 0.3F, pos.getY() + 0.95F,
                        pos.getZ() + 0.5F + MathUtils.randomFloat(random) * 0.3F, 0, random.nextFloat() * 0.05D, 0);
    }

    @Override
    public void load(CompoundTag compound) {
        stack = ItemStack.of((CompoundTag) compound.get("itemStack"));

        super.load(compound);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        if (stack != null) {
            CompoundTag itemStack = new CompoundTag();

            stack.save(itemStack);

            compound.put("itemStack", itemStack);
        }

        super.saveAdditional(compound);
    }
}