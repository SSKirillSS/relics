package it.hurts.sskirillss.relics.items.relics;

import it.hurts.sskirillss.relics.entities.DissectionEntity;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;

public class SpaceDissectorItem extends RelicItem {
    public static final String TAG_START = "start";
    public static final String TAG_END = "end";

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand handIn) {
        ItemStack stack = player.getItemInHand(handIn);

        NBTUtils.clearTag(stack, TAG_START);
        NBTUtils.clearTag(stack, TAG_END);

        Vec3 view = player.getViewVector(0);
        Vec3 eyeVec = player.getEyePosition(0);

        float distance = 64;

        BlockHitResult ray = world.clip(new ClipContext(eyeVec, eyeVec.add(view.x * distance, view.y * distance,
                view.z * distance), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        NBTUtils.setString(stack, TAG_START, NBTUtils.writePosition(ray.getLocation()));

        player.startUsingItem(handIn);

        return super.use(world, player, handIn);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity pLivingEntity, int pTimeCharged) {
        if (!(pLivingEntity instanceof Player player))
            return;

        Vec3 view = player.getViewVector(0);
        Vec3 eyeVec = player.getEyePosition(0);

        float distance = 64;

        BlockHitResult ray = world.clip(new ClipContext(eyeVec, eyeVec.add(view.x * distance, view.y * distance,
                view.z * distance), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        NBTUtils.setString(stack, TAG_END, NBTUtils.writePosition(ray.getLocation()));

        Vec3 currentVec = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_START, ""));
        Vec3 nextVec = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_END, ""));

        if (currentVec != null && nextVec != null && currentVec.distanceTo(nextVec) <= distance * 1.25F) {
            DissectionEntity start = new DissectionEntity(world);

            start.setPos(currentVec);

            DissectionEntity end = new DissectionEntity(world);

            end.setPos(nextVec);

            world.addFreshEntity(start);
            world.addFreshEntity(end);

            start.setPair(end);
            end.setPair(start);
        }
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}