package it.hurts.sskirillss.relics.items.relics.boots;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class IceSkatesItem extends RelicItem implements ICurioItem, IHasTooltip {
    private static final AttributeModifier ICE_SKATES_SPEED_BOOST = new AttributeModifier(UUID.fromString("c0f5890f-a878-49bb-b24c-bbbf60d8539b"),
            Reference.MODID + ":" + "ice_skates_movement_speed", RelicsConfig.IceSkates.MOVEMENT_SPEED_MULTIPLIER.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);

    public static final String TAG_SPEEDUP_TIME = "time";

    public IceSkatesItem() {
        super(Rarity.UNCOMMON);
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.ice_skates.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.ice_skates.shift_2"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        int time = NBTUtils.getInt(stack, TAG_SPEEDUP_TIME, 0);
        BlockPos pos = WorldUtils.getSolidBlockUnderFeet(livingEntity.getCommandSenderWorld(), livingEntity.blockPosition());
        if (pos != null) {
            if (livingEntity.getCommandSenderWorld().getBlockState(pos).is(BlockTags.ICE)) {
                if (livingEntity.isSprinting()) {
                    if (!movementSpeed.hasModifier(ICE_SKATES_SPEED_BOOST)) {
                        movementSpeed.addTransientModifier(ICE_SKATES_SPEED_BOOST);
                    }
                    if (livingEntity.tickCount % 20 == 0) {
                        if (time < RelicsConfig.IceSkates.MAX_SPEEDUP_TIME.get()) {
                            NBTUtils.setInt(stack, TAG_SPEEDUP_TIME, time + 1);
                        }
                    }
                    if (time > RelicsConfig.IceSkates.SPEEDUP_TIME_PER_RAM.get()) {
                        livingEntity.getCommandSenderWorld().addParticle(ParticleTypes.CLOUD, livingEntity.getX(), livingEntity.getY() + 0.1F, livingEntity.getZ(), 0, 0, 0);
                        for (LivingEntity entity : livingEntity.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(RelicsConfig.IceSkates.RAM_RADIUS.get()))) {
                            if (entity != livingEntity) {
                                entity.setDeltaMovement(entity.position().subtract(livingEntity.position()).normalize().multiply(time * 0.25F, time * 0.1F, time * 0.25F));
                                entity.hurt(DamageSource.FLY_INTO_WALL, RelicsConfig.IceSkates.BASE_RAM_DAMAGE_AMOUNT.get().floatValue() + time);
                                NBTUtils.setInt(stack, TAG_SPEEDUP_TIME, Math.max(time - RelicsConfig.IceSkates.SPEEDUP_TIME_PER_RAM.get(), 0));
                            }
                        }
                    }
                }
            } else {
                if (livingEntity.getCommandSenderWorld().getBlockState(livingEntity.blockPosition().below()) == Fluids.WATER.getSource()
                        .defaultFluidState().createLegacyBlock() && time > 0) {
                    livingEntity.getCommandSenderWorld().setBlockAndUpdate(livingEntity.blockPosition().below(), Blocks.FROSTED_ICE.defaultBlockState());
                    NBTUtils.setInt(stack, TAG_SPEEDUP_TIME, time - 1);
                } else {
                    NBTUtils.setInt(stack, TAG_SPEEDUP_TIME, 0);
                    if (movementSpeed.hasModifier(ICE_SKATES_SPEED_BOOST)) {
                        movementSpeed.removeModifier(ICE_SKATES_SPEED_BOOST);
                    }
                }
            }
        }
    }

    @Override
    public void onUnequip(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed.hasModifier(ICE_SKATES_SPEED_BOOST)
                && !livingEntity.getCommandSenderWorld().getBlockState(livingEntity.blockPosition().below()).is(BlockTags.ICE)
                && !livingEntity.isSprinting()) {
            movementSpeed.removeModifier(ICE_SKATES_SPEED_BOOST);
        }
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.COLD;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class IceSkatesEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SCARAB_TALISMAN.get(), player).isPresent()
                        && event.getSource() == DamageSource.FALL
                        && player.getCommandSenderWorld().getBlockState(player.blockPosition().below()).is(BlockTags.ICE)) {
                    event.setAmount(event.getAmount() * RelicsConfig.IceSkates.FALLING_DAMAGE_MULTIPLIER.get().floatValue());
                }
            }
        }
    }
}