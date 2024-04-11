package it.hurts.sskirillss.relics.items;

import it.hurts.sskirillss.relics.entities.SolidSnowballEntity;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class SolidSnowballItem extends Item {
    public static final String TAG_SNOW = "snow";

    public SolidSnowballItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON)
        );
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (EntityUtils.findEquippedCurio(entity, ItemRegistry.WOOL_MITTEN.get()).isEmpty())
            entity.setTicksFrozen(entity.getTicksFrozen() + 3);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        SolidSnowballEntity entity = new SolidSnowballEntity(level);

        entity.setOwner(player);
        entity.setPos(player.getX(), player.getEyeY(), player.getZ());
        entity.setSize(NBTUtils.getInt(stack, TAG_SNOW, 0));
        entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);

        level.addFreshEntity(entity);

        level.playSound(null, player.blockPosition(), SoundEvents.SNOWBALL_THROW, SoundSource.MASTER, 0.5F, 0.5F);

        if (!player.isCreative())
            stack.shrink(1);

        return InteractionResultHolder.pass(stack);
    }
}