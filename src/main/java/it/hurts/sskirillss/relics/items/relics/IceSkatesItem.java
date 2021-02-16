package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.utils.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
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

public class IceSkatesItem extends Item implements ICurioItem, IHasTooltip {
    private static final AttributeModifier ICE_SKATES_SPEED_BOOST = new AttributeModifier(UUID.fromString("c0f5890f-a878-49bb-b24c-bbbf60d8539b"),
            Reference.MODID + ":" + "ice_skates_movement_speed", RelicsConfig.IceSkates.MOVEMENT_SPEED_MULTIPLIER.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);

    public static final String TAG_SPEEDUP_TIME = "time";

    public IceSkatesItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.UNCOMMON));
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.ice_skates.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.ice_skates.shift_2"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        int time = NBTUtils.getInt(stack, TAG_SPEEDUP_TIME, 0);
        BlockPos pos = WorldUtils.getSolidBlockUnderFeet(livingEntity.getEntityWorld(), livingEntity.getPosition());
        if (pos != null) {
            if (livingEntity.getEntityWorld().getBlockState(pos).isIn(BlockTags.ICE)) {
                if (livingEntity.isSprinting()) {
                    if (!movementSpeed.hasModifier(ICE_SKATES_SPEED_BOOST)) {
                        movementSpeed.applyNonPersistentModifier(ICE_SKATES_SPEED_BOOST);
                    }
                    if (livingEntity.ticksExisted % 20 == 0) {
                        if (time < RelicsConfig.IceSkates.MAX_SPEEDUP_TIME.get()) {
                            NBTUtils.setInt(stack, TAG_SPEEDUP_TIME, time + 1);
                        }
                    }
                    if (time > RelicsConfig.IceSkates.SPEEDUP_TIME_PER_RAM.get()) {
                        livingEntity.getEntityWorld().addParticle(ParticleTypes.CLOUD, livingEntity.getPosX(), livingEntity.getPosY() + 0.1F, livingEntity.getPosZ(), 0, 0, 0);
                        for (LivingEntity entity : livingEntity.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, livingEntity.getBoundingBox().grow(RelicsConfig.IceSkates.RAM_RADIUS.get()))) {
                            if (entity != livingEntity) {
                                entity.setMotion(entity.getPositionVec().subtract(livingEntity.getPositionVec()).normalize().mul(time * 0.25F, time * 0.1F, time * 0.25F));
                                entity.attackEntityFrom(DamageSource.FLY_INTO_WALL, RelicsConfig.IceSkates.BASE_RAM_DAMAGE_AMOUNT.get().floatValue() + time);
                                NBTUtils.setInt(stack, TAG_SPEEDUP_TIME, Math.max(time - RelicsConfig.IceSkates.SPEEDUP_TIME_PER_RAM.get(), 0));
                            }
                        }
                    }
                }
            } else {
                if (livingEntity.getEntityWorld().getBlockState(livingEntity.getPosition().down()) == Fluids.WATER.getStillFluid()
                        .getDefaultState().getBlockState() && time > 0) {
                    livingEntity.getEntityWorld().setBlockState(livingEntity.getPosition().down(), Blocks.FROSTED_ICE.getDefaultState());
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
                && !livingEntity.getEntityWorld().getBlockState(livingEntity.getPosition().down()).isIn(BlockTags.ICE)
                && !livingEntity.isSprinting()) {
            movementSpeed.removeModifier(ICE_SKATES_SPEED_BOOST);
        }
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class IceSkatesEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SCARAB_TALISMAN.get(), player).isPresent()
                        && event.getSource() == DamageSource.FALL
                        && player.getEntityWorld().getBlockState(player.getPosition().down()).isIn(BlockTags.ICE)) {
                    event.setAmount(event.getAmount() * RelicsConfig.IceSkates.FALLING_DAMAGE_MULTIPLIER.get().floatValue());
                }
            }
        }
    }
}