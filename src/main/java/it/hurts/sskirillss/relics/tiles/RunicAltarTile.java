package it.hurts.sskirillss.relics.tiles;

import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.crafting.RunicAltarContext;
import it.hurts.sskirillss.relics.crafting.RunicAltarRecipe;
import it.hurts.sskirillss.relics.crafting.SingletonInventory;
import it.hurts.sskirillss.relics.init.TileRegistry;
import it.hurts.sskirillss.relics.items.RuneItem;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RunicAltarTile extends TileBase implements ITickableTileEntity {
    private ItemStack relicStack = ItemStack.EMPTY;
    private ItemStack eastStack = ItemStack.EMPTY;
    private ItemStack westStack = ItemStack.EMPTY;
    private ItemStack southStack = ItemStack.EMPTY;
    private ItemStack northStack = ItemStack.EMPTY;
    private ItemStack ingredient = ItemStack.EMPTY;

    public int ticksExisted;
    private int progress;

    public static final Direction[] runeDirections = {Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};

    public RunicAltarTile() {
        super(TileRegistry.RUNIC_ALTAR_TILE.get());
    }

    public void setStack(ItemStack stack, Direction direction) {
        switch (direction) {
            case NORTH:
                this.northStack = stack;
                break;
            case WEST:
                this.westStack = stack;
                break;
            case EAST:
                this.eastStack = stack;
                break;
            case SOUTH:
                this.southStack = stack;
                break;
            case UP:
                this.relicStack = stack;
                break;
        }
    }

    public ItemStack getStack(Direction direction) {
        switch (direction) {
            case NORTH:
                return this.northStack;
            case WEST:
                return this.westStack;
            case EAST:
                return this.eastStack;
            case SOUTH:
                return this.southStack;
            case UP:
                return this.relicStack;
            default:
                return ItemStack.EMPTY;
        }
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

    @Override
    public void tick() {
        if (level == null)
            return;

        ticksExisted++;

        Random random = level.getRandom();

        if (relicStack.isEmpty() || getCraftingProgress() == 0)
            return;

        spawnParticles(level, random);

        if (level.isClientSide())
            return;

        handleRunes(level);
        handleRecipe(level, random);
    }

    protected void spawnParticles(World world, Random random) {
        if (!world.isClientSide())
            return;

        BlockPos pos = getBlockPos();

        world.addParticle(new CircleTintData(relicStack.getRarity().color.getColor() != null ? new Color(relicStack.getRarity().color.getColor(), false)
                        : new Color(255, 255, 255), random.nextFloat() * 0.025F + 0.04F, 20, 0.94F, true),
                pos.getX() + 0.5D + MathUtils.randomFloat(random) * 0.2F, pos.getY() + 0.85F,
                pos.getZ() + 0.5D + MathUtils.randomFloat(random) * 0.2F, 0, random.nextFloat() * 0.05D, 0);
    }

    protected void handleRecipe(World world, Random random) {
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
                world.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundCategory.BLOCKS, 1.0F, 1.0F);

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
                        addCraftingProgress(random.nextInt(20) + 1);

                    return;
                }

                RuneItem rune = (RuneItem) runes.get(random.nextInt(runes.size())).getItem();
                List<Item> ingredients = rune.getIngredients().stream()
                        .map(name -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)))
                        .collect(Collectors.toList());

                setIngredient(new ItemStack(ingredients.get(random.nextInt(ingredients.size()))));

                world.playSound(null, pos, SoundEvents.CHICKEN_EGG, SoundCategory.BLOCKS, 1F, 1F);
                world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
            } else
                world.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(),
                        pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1)).stream().map(ItemEntity::getItem)
                        .filter(stack -> ingredient.getItem().equals(stack.getItem())).findFirst().ifPresent(item -> {
                    if (!world.isClientSide())
                        addCraftingProgress(random.nextInt(20) + 1);

                    setIngredient(ItemStack.EMPTY);
                    item.shrink(1);

                    world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
                });
        }
    }

    protected void handleRunes(World world) {
        if (getRunes().isEmpty() || ticksExisted % 20 == 0)
            return;

        BlockPos pos = getBlockPos().offset(0.5D, 0.5D, 0.5D);

        for (ItemStack stack : getRunes()) {
            if (!(stack.getItem() instanceof RuneItem))
                continue;

            RuneItem rune = (RuneItem) stack.getItem();

            rune.applyAbility(world, pos);
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        relicStack = ItemStack.of((CompoundNBT) compound.get("relicStack"));
        eastStack = ItemStack.of((CompoundNBT) compound.get("eastStack"));
        westStack = ItemStack.of((CompoundNBT) compound.get("westStack"));
        southStack = ItemStack.of((CompoundNBT) compound.get("southStack"));
        northStack = ItemStack.of((CompoundNBT) compound.get("northStack"));
        ingredient = ItemStack.of((CompoundNBT) compound.get("ingredient"));
        progress = compound.getInt("progress");

        super.load(state, compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        if (relicStack != null) {
            CompoundNBT compoundNBT = new CompoundNBT();

            relicStack.save(compoundNBT);
            compound.put("relicStack", compoundNBT);
        }

        if (eastStack != null) {
            CompoundNBT compoundNBT = new CompoundNBT();

            eastStack.save(compoundNBT);
            compound.put("eastStack", compoundNBT);
        }

        if (westStack != null) {
            CompoundNBT compoundNBT = new CompoundNBT();

            westStack.save(compoundNBT);
            compound.put("westStack", compoundNBT);
        }

        if (southStack != null) {
            CompoundNBT compoundNBT = new CompoundNBT();

            southStack.save(compoundNBT);
            compound.put("southStack", compoundNBT);
        }

        if (northStack != null) {
            CompoundNBT compoundNBT = new CompoundNBT();

            northStack.save(compoundNBT);
            compound.put("northStack", compoundNBT);
        }

        if (ingredient != null) {
            CompoundNBT compoundNBT = new CompoundNBT();

            ingredient.save(compoundNBT);
            compound.put("ingredient", compoundNBT);
        }

        compound.putInt("progress", getCraftingProgress());

        return super.save(compound);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getBlockPos(), -1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        this.load(getBlockState(), packet.getTag());
    }
}