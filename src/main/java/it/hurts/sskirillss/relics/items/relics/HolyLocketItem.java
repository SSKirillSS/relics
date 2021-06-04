package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.RelicsConfig;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

public class HolyLocketItem extends RelicItem implements IHasTooltip {
    public HolyLocketItem() {
        super(Rarity.UNCOMMON);
    }

    @Override
    public java.util.List<ITextComponent> getShiftTooltip() {
        java.util.List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.holy_locket.shift_1"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return RelicUtils.Worldgen.DESERT;
    }

    @Mod.EventBusSubscriber
    static class HolyLocketServerEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            if (event.getSource().getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
                LivingEntity entity = event.getEntityLiving();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.HOLY_LOCKET.get(), player).isPresent()
                        && entity.isInvertedHealAndHarm()) {
                    if (player.getCommandSenderWorld().random.nextFloat() <= RelicsConfig.HolyLocket.ARSON_CHANCE.get())
                        entity.setSecondsOnFire(RelicsConfig.HolyLocket.BURN_DURATION.get());
                    event.setAmount(event.getAmount() * RelicsConfig.HolyLocket.DAMAGE_MULTIPLIER.get().floatValue());
                }
            }
        }
    }
}