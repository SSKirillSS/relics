package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.IHasTooltip;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.TooltipUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Collections;
import java.util.List;

public class HunterBeltItem extends RelicItem<HunterBeltItem.Stats> implements ICurioItem, IHasTooltip {
    public static HunterBeltItem INSTANCE;

    public HunterBeltItem() {
        super(Rarity.UNCOMMON);

        INSTANCE = this;
    }

    @Override
    public List<ITextComponent> getShiftTooltip() {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.hunter_belt.shift_1"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.addAll(TooltipUtils.applyTooltip(stack));
    }

    @Override
    public int getLootingBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
        return config.additionalLooting;
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return Collections.singletonList(LootTables.VILLAGE_BUTCHER);
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class HunterBeltEvents {
        @SubscribeEvent
        public static void onLivingDamage(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            if (event.getSource().getEntity() instanceof PlayerEntity
                    && event.getEntityLiving() instanceof AnimalEntity) {
                PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
                if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.HUNTER_BELT.get(), player).isPresent()) {
                    event.setAmount(event.getAmount() * config.playerDamageMultiplier);
                }
            }
            if (event.getSource().getEntity() instanceof TameableEntity) {
                TameableEntity pet = (TameableEntity) event.getSource().getEntity();
                if (pet.getOwner() != null && pet.getOwner() instanceof PlayerEntity
                        && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.HUNTER_BELT.get(), pet.getOwner()).isPresent()) {
                    event.setAmount(event.getAmount() * config.petDamageMultiplier);
                }
            }
        }
    }

    public static class Stats extends RelicStats {
        public int additionalLooting = 1;
        public float playerDamageMultiplier = 2.0F;
        public float petDamageMultiplier = 3.0F;
    }
}