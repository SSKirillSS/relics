package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class RageGloveItem extends RelicItem implements ICurioItem, IHasTooltip {
    public static final String TAG_UPDATE_TIME = "time";
    public static final String TAG_STACKS_AMOUNT = "stacks";
    public static final String TAG_TARGETED_ENTITY = "target";

    public RageGloveItem() {
        super(Rarity.RARE);
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.rage_glove.shift_1"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (livingEntity.tickCount % 20 == 0) {
            int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);
            int stacks = NBTUtils.getInt(stack, TAG_STACKS_AMOUNT, 0);
            if (stacks > 0) {
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, time + 1);
                if (time >= RelicsConfig.RageGlove.STACK_TIME.get()) {
                    NBTUtils.setInt(stack, TAG_STACKS_AMOUNT, stacks - 1);
                    NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                }
            }
        }
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.NETHER;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class RageGloveEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (event.getSource().getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
                if (event.getAmount() > RelicsConfig.RageGlove.MIN_DAMAGE_AMOUNT.get()
                        && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.RAGE_GLOVE.get(), player).isPresent()) {
                    LivingEntity entity = event.getEntityLiving();
                    ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.RAGE_GLOVE.get(), player).get().getRight();
                    if (!NBTUtils.getString(stack, TAG_TARGETED_ENTITY, "").equals("")
                            && UUID.fromString(NBTUtils.getString(stack, TAG_TARGETED_ENTITY, "")).equals(entity.getUUID())) {
                        NBTUtils.setInt(stack, TAG_STACKS_AMOUNT, NBTUtils.getInt(stack, TAG_STACKS_AMOUNT, 0) + 1);
                        NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                        event.setAmount(event.getAmount() + (event.getAmount() * RelicsConfig.RageGlove.DEALING_DAMAGE_MULTIPLIER_PER_STACK.get().floatValue() * NBTUtils.getInt(stack, TAG_STACKS_AMOUNT, 0)));
                    } else {
                        NBTUtils.setInt(stack, TAG_UPDATE_TIME, 0);
                        NBTUtils.setInt(stack, TAG_STACKS_AMOUNT, 1);
                        NBTUtils.setString(stack, TAG_TARGETED_ENTITY, entity.getUUID().toString());
                    }
                }
            }

            if (event.getSource().getEntity() instanceof LivingEntity
                    && event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.RAGE_GLOVE.get(), player).isPresent()) {
                    ItemStack stack = CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.RAGE_GLOVE.get(), player).get().getRight();
                    if (!NBTUtils.getString(stack, TAG_TARGETED_ENTITY, "").equals("")
                            && event.getSource().getEntity() == ((ServerWorld) player.getCommandSenderWorld()).getEntity(UUID.fromString(NBTUtils.getString(stack, TAG_TARGETED_ENTITY, "")))) {
                        event.setAmount(event.getAmount() + (event.getAmount() * RelicsConfig.RageGlove.INCOMING_DAMAGE_MULTIPLIER_PER_STACK.get().floatValue() * NBTUtils.getInt(stack, TAG_STACKS_AMOUNT, 0)));
                    }
                }
            }
        }
    }
}