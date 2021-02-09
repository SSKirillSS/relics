package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.RelicsTab;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class ArrowQuiverItem extends Item implements ICurioItem, IHasTooltip {
    public ArrowQuiverItem() {
        super(new Item.Properties()
                .group(RelicsTab.RELICS_TAB)
                .maxStackSize(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.arrow_quiver.shift_1"));
        return tooltip;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ArrowQuiverEvents {
        @SubscribeEvent
        public static void onArrowShoot(ArrowLooseEvent event) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.ARROW_QUIVER.get(), player).isPresent()
                        && random.nextFloat() <= RelicsConfig.ArrowQuiver.MULTISHOT_CHANCE.get()) {
                    for (int i = 0; i < RelicsConfig.ArrowQuiver.ADDITIONAL_ARROW_AMOUNT.get(); i++) {
                        ItemStack bow = player.getHeldItemMainhand();
                        ItemStack ammo = player.findAmmo(bow);
                        AbstractArrowEntity projectile = ((ArrowItem) (ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW))
                                .createArrow(player.getEntityWorld(), ammo, player);
                        projectile.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                        projectile.func_234612_a_(player, player.rotationPitch, player.rotationYaw, 0.0F,
                                BowItem.getArrowVelocity(event.getCharge()) * 3.0F, player.isSneaking() ? 2.0F : 5.0F);

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bow) > 0) {
                            projectile.setDamage(projectile.getDamage() + (double) EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bow) * 0.5D + 0.5D);
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bow) > 0) {
                            projectile.setKnockbackStrength(EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bow));
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bow) > 0) {
                            projectile.setFire(100);
                        }

                        player.getEntityWorld().addEntity(projectile);
                    }
                }
            }
        }
    }
}