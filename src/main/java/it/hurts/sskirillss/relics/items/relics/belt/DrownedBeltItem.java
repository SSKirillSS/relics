package it.hurts.sskirillss.relics.items.relics.belt;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicStyleData;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.IRenderableCurio;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.base.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityEntry;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicAbilityStat;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.RelicLevelingData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;
import top.theillusivec4.curios.api.SlotContext;

import java.util.List;

public class DrownedBeltItem extends RelicItem implements IRenderableCurio {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilityData(RelicAbilityData.builder()
                        .ability("slots", RelicAbilityEntry.builder()
                                .requiredPoints(2)
                                .stat("talisman", RelicAbilityStat.builder()
                                        .initialValue(0D, 2D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, 1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 0)))
                                        .build())
                                .build())
                        .ability("anchor", RelicAbilityEntry.builder()
                                .stat("slowness", RelicAbilityStat.builder()
                                        .initialValue(0.5D, 0.25D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, -0.05D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 2) * 100))
                                        .build())
                                .stat("sinking", RelicAbilityStat.builder()
                                        .initialValue(5D, 3D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, -0.1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 2) * 100))
                                        .build())
                                .build())
                        .ability("pressure", RelicAbilityEntry.builder()
                                .stat("damage", RelicAbilityStat.builder()
                                        .initialValue(1.25D, 2D)
                                        .upgradeModifier(RelicAbilityStat.Operation.MULTIPLY_BASE, 0.1D)
                                        .formatValue(value -> (int) (MathUtils.round(value, 2) * 100))
                                        .build())
                                .build())
                        .ability("riptide", RelicAbilityEntry.builder()
                                .stat("cooldown", RelicAbilityStat.builder()
                                        .initialValue(10D, 5D)
                                        .upgradeModifier(RelicAbilityStat.Operation.ADD, -0.5D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .build())
                        .build())
                .levelingData(new RelicLevelingData(100, 10, 100))
                .styleData(RelicStyleData.builder()
                        .borders("#7889b8", "#25374e")
                        .build())
                .build();
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        if (player.isEyeInFluid(FluidTags.WATER) && !player.onGround())
            EntityUtils.applyAttribute(player, stack, ForgeMod.ENTITY_GRAVITY.get(), (float) getAbilityValue(stack, "anchor", "sinking"), AttributeModifier.Operation.MULTIPLY_TOTAL);
        else
            EntityUtils.removeAttribute(player, stack, ForgeMod.ENTITY_GRAVITY.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        EntityUtils.removeAttribute(slotContext.entity(), stack, ForgeMod.ENTITY_GRAVITY.get(), AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public RelicSlotModifier getSlotModifiers(ItemStack stack) {
        return RelicSlotModifier.builder()
                .entry(Pair.of("talisman", (int) Math.round(getAbilityValue(stack, "slots", "talisman"))))
                .build();
    }

    @Override
    public RelicAttributeModifier getAttributeModifiers(ItemStack stack) {
        return RelicAttributeModifier.builder()
                .attribute(new RelicAttributeModifier.Modifier(ForgeMod.SWIM_SPEED.get(), (float) -getAbilityValue(stack, "anchor", "slowness")))
                .build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public LayerDefinition constructLayerDefinition() {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0.4F), 0.0F);

        PartDefinition bone = mesh.getRoot().addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, 9.0F, -2.5F, 9.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 7).addBox(-2.05F, -1.5F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.0F, -2.5F, -0.1295F, -0.0378F, 0.0894F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public List<String> headParts() {
        return Lists.newArrayList("body");
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class Events {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            if (!(event.getSource().getEntity() instanceof Player player)
                    || !player.isUnderWater() || !event.getEntity().isUnderWater())
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.DROWNED_BELT.get());

            if (!(stack.getItem() instanceof IRelicItem relic))
                return;

            event.setAmount((float) (event.getAmount() * relic.getAbilityValue(stack, "pressure", "damage")));
        }

        @SubscribeEvent
        public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
            ItemStack stack = event.getItem();

            if (!(event.getEntity() instanceof Player player) || stack.getItem() != Items.TRIDENT || !player.getCooldowns().isOnCooldown(stack.getItem()))
                return;

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onItemUseFinish(LivingEntityUseItemEvent.Stop event) {
            ItemStack trident = event.getItem();

            if (!(event.getEntity() instanceof Player player) || trident.getItem() != Items.TRIDENT)
                return;

            ItemStack stack = EntityUtils.findEquippedCurio(player, ItemRegistry.DROWNED_BELT.get());

            if (!(stack.getItem() instanceof IRelicItem relic))
                return;


            int duration = trident.getItem().getUseDuration(trident) - event.getDuration();
            int enchantment = EnchantmentHelper.getRiptide(trident);

            if (duration < 10 || enchantment <= 0)
                return;

            relic.addExperience(player, stack, enchantment);

            player.getCooldowns().addCooldown(trident.getItem(), (int) Math.round(relic.getAbilityValue(stack, "riptide", "cooldown") * enchantment * 20));
        }
    }
}