package it.hurts.sskirillss.relics.tiles;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.crafting.RunicAltarContext;
import it.hurts.sskirillss.relics.crafting.RunicAltarRecipe;
import it.hurts.sskirillss.relics.crafting.SingletonInventory;
import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.items.RuneItem;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RunicAltarTile extends TileBase {
    private ItemStack relicStack = ItemStack.EMPTY;
    private ItemStack eastStack = ItemStack.EMPTY;
    private ItemStack westStack = ItemStack.EMPTY;
    private ItemStack southStack = ItemStack.EMPTY;
    private ItemStack northStack = ItemStack.EMPTY;
    private ItemStack ingredient = ItemStack.EMPTY;

    public int ticksExisted;
    private int progress;

    public static final Direction[] runeDirections = {Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};

    public RunicAltarTile(BlockPos pos, BlockState state) {
        super(TileRegistry.RUNIC_ALTAR_TILE.get(), pos, state);
    }

    public void setStack(ItemStack stack, Direction direction) {
        switch (direction) {
            case NORTH -> this.northStack = stack;
            case WEST -> this.westStack = stack;
            case EAST -> this.eastStack = stack;
            case SOUTH -> this.southStack = stack;
            case UP -> this.relicStack = stack;
        }
    }

    public ItemStack getStack(Direction direction) {
        return switch (direction) {
            case NORTH -> this.northStack;
            case WEST -> this.westStack;
            case EAST -> this.eastStack;
            case SOUTH -> this.southStack;
            case UP -> this.relicStack;
            default -> ItemStack.EMPTY;
        };
    }

    public List<ItemStack> getRunes() {
        return Arrays.stream(runeDirections).map(this::getStack).filter(stack -> !stack.isEmpty()).collect(Collectors.toList());
    }

    public void addCraftingProgress(int progress) {
        setCraftingProgress(this.progress + progress);
    }

    public void setCraftingProgress(int progress) {
        this.progress = Math.min(100, progress);
    }

    public int getCraftingProgress() {
        return progress;
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public void setIngredient(ItemStack ingredient) {
        this.ingredient = ingredient;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RunicAltarTile tile) {
        if (level == null)
            return;

        tile.ticksExisted++;

        Random random = level.getRandom();

        if (tile.relicStack.isEmpty() || tile.getCraftingProgress() == 0)
            return;

        tile.spawnParticles(level, random);

        if (level.isClientSide())
            return;

        tile.handleRunes(level);
        tile.handleRecipe(level, random);
    }

    protected void spawnParticles(Level world, Random random) {
        if (!world.isClientSide())
            return;

        BlockPos pos = getBlockPos();

        world.addParticle(new CircleTintData(relicStack.getRarity().color.getColor() != null ? new Color(relicStack.getRarity().color.getColor(), false)
                        : new Color(255, 255, 255), random.nextFloat() * 0.025F + 0.04F, 20, 0.94F, true),
                pos.getX() + 0.5D + MathUtils.randomFloat(random) * 0.2F, pos.getY() + 0.85F,
                pos.getZ() + 0.5D + MathUtils.randomFloat(random) * 0.2F, 0, random.nextFloat() * 0.05D, 0);
    }

    protected void handleRecipe(Level world, Random random) {
        BlockPos pos = getBlockPos();

        if (getCraftingProgress() >= 100)
            world.getRecipeManager().getRecipeFor(RunicAltarRecipe.RECIPE, new RunicAltarContext(
                    new SingletonInventory(getStack(Direction.UP)), world.getNearestPlayer(pos.getX() + 0.5F, pos.getY() + 0.5F,
                    pos.getZ() + 0.5F, 3, false), getRunes(), getStack(Direction.UP)), world).ifPresent(recipe -> {
                Arrays.stream(RunicAltarTile.runeDirections).map(this::getStack).forEach(stack -> stack.shrink(1));

                ItemStack result = recipe.getResultItem();

                result.setDamageValue(Math.min(result.getMaxDamage(), relicStack.getDamageValue()));

                setStack(result, Direction.UP);

                world.addParticle(ParticleTypes.EXPLOSION, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0D, 0D, 0D);
                world.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 1.0F);

                setIngredient(ItemStack.EMPTY);
                setCraftingProgress(0);

                world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
            });
        else if (getCraftingProgress() > 0) {
            if (getIngredient().isEmpty()) {
                List<RuneItem> runes = getRunes().stream().map(rune -> (RuneItem) rune.getItem()).collect(Collectors.toList());

                runes.removeIf(rune -> rune.getIngredients().isEmpty());

                if (runes.isEmpty()) {
                    if (!world.isClientSide() && ticksExisted % 20 == 0)
                        addCraftingProgress(random.nextInt(10) + 1);

                    return;
                }

                RuneItem rune = runes.get(random.nextInt(runes.size()));
                List<Item> ingredients = rune.getIngredients().stream()
                        .map(name -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)))
                        .collect(Collectors.toList());

                setIngredient(new ItemStack(ingredients.get(random.nextInt(ingredients.size()))));

                world.playSound(null, pos, SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 1F, 1F);
                world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
            } else
                world.getEntitiesOfClass(ItemEntity.class, new AABB(pos.getX(), pos.getY(), pos.getZ(),
                        pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1)).stream().map(ItemEntity::getItem)
                        .filter(stack -> ingredient.getItem().equals(stack.getItem())).findFirst().ifPresent(item -> {
                    if (!world.isClientSide())
                        addCraftingProgress(random.nextInt(10) + 1);

                    setIngredient(ItemStack.EMPTY);
                    item.shrink(1);

                    world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
                });
        }
    }

    protected void handleRunes(Level world) {
        if (getRunes().isEmpty() || ticksExisted % 20 == 0)
            return;

        BlockPos pos = getBlockPos().offset(0.5D, 0.5D, 0.5D);

        for (ItemStack stack : getRunes()) {
            if (!(stack.getItem() instanceof RuneItem rune))
                continue;

            rune.applyAbility(world, pos);
        }
    }

    @Override
    public void load(CompoundTag compound) {
        relicStack = ItemStack.of((CompoundTag) compound.get("relicStack"));
        eastStack = ItemStack.of((CompoundTag) compound.get("eastStack"));
        westStack = ItemStack.of((CompoundTag) compound.get("westStack"));
        southStack = ItemStack.of((CompoundTag) compound.get("southStack"));
        northStack = ItemStack.of((CompoundTag) compound.get("northStack"));
        ingredient = ItemStack.of((CompoundTag) compound.get("ingredient"));

        progress = compound.getInt("progress");

        super.load(compound);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        if (relicStack != null) {
            CompoundTag compoundNBT = new CompoundTag();

            relicStack.save(compoundNBT);
            compound.put("relicStack", compoundNBT);
        }

        if (eastStack != null) {
            CompoundTag compoundNBT = new CompoundTag();

            eastStack.save(compoundNBT);
            compound.put("eastStack", compoundNBT);
        }

        if (westStack != null) {
            CompoundTag compoundNBT = new CompoundTag();

            westStack.save(compoundNBT);
            compound.put("westStack", compoundNBT);
        }

        if (southStack != null) {
            CompoundTag compoundNBT = new CompoundTag();

            southStack.save(compoundNBT);
            compound.put("southStack", compoundNBT);
        }

        if (northStack != null) {
            CompoundTag compoundNBT = new CompoundTag();

            northStack.save(compoundNBT);
            compound.put("northStack", compoundNBT);
        }

        if (ingredient != null) {
            CompoundTag compoundNBT = new CompoundTag();

            ingredient.save(compoundNBT);
            compound.put("ingredient", compoundNBT);
        }

        compound.putInt("progress", getCraftingProgress());


        super.saveAdditional(compound);
    }
}