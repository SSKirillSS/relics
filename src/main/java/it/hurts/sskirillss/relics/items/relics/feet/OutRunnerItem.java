package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.api.durability.IRepairableItem;
import it.hurts.sskirillss.relics.client.renderer.items.models.OutRunnerModel;
import it.hurts.sskirillss.relics.client.tooltip.base.AbilityTooltip;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.ConfigData;
import it.hurts.sskirillss.relics.configs.data.LootData;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.MutablePair;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Objects;
import java.util.UUID;

public class OutRunnerItem extends RelicItem<OutRunnerItem.Stats> {
    private final MutablePair<String, UUID> SPEED_INFO = new MutablePair<>(Reference.MODID
            + ":" + "out_runner_movement_speed", UUID.fromString("9bf3eeb5-8587-4fb7-ad81-fd76e01f4acf"));

    private static final String TAG_RUN_DURATION = "duration";

    public OutRunnerItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .build());
    }

    @Override
    public RelicTooltip getTooltip(ItemStack stack) {
        return RelicTooltip.builder()
                .ability(AbilityTooltip.builder()
                        .arg("+" + (int) (stats.speedModifier * 100 * 5) + "%")
                        .build())
                .build();
    }

    @Override
    public ConfigData<Stats> getConfigData() {
        return ConfigData.<Stats>builder()
                .stats(new Stats())
                .loot(LootData.builder()
                        .table(RelicUtils.Worldgen.CAVE)
                        .chance(0.05F)
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity) || IRepairableItem.isBroken(stack))
            return;

        PlayerEntity player = (PlayerEntity) livingEntity;
        World world = player.getCommandSenderWorld();
        ModifiableAttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        int duration = NBTUtils.getInt(stack, TAG_RUN_DURATION, 0);

        if (player.isSprinting() && !player.isShiftKeyDown() && !player.isInWater() && !player.isInLava()) {
            if (duration < stats.maxModifiers && player.tickCount % 4 == 0)
                NBTUtils.setInt(stack, TAG_RUN_DURATION, duration + 1);

            if (world.getRandom().nextInt(stats.maxModifiers) < duration)
                world.addParticle(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.15F,
                        player.getZ(), 0, 0.25F, 0);

        } else if (duration > 0)
            NBTUtils.setInt(stack, TAG_RUN_DURATION, duration - 1);

        if (duration > 0) {
            EntityUtils.removeAttributeModifier(Objects.requireNonNull(movementSpeed), new AttributeModifier(SPEED_INFO.getRight(),
                    SPEED_INFO.getLeft(), movementSpeed.getValue(), AttributeModifier.Operation.MULTIPLY_TOTAL));
            EntityUtils.applyAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(),
                    SPEED_INFO.getLeft(), duration * stats.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));

            player.maxUpStep = 1.1F;
        } else {
            EntityUtils.removeAttributeModifier(Objects.requireNonNull(movementSpeed), new AttributeModifier(SPEED_INFO.getRight(),
                    SPEED_INFO.getLeft(),  stats.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));

            player.maxUpStep = 0.6F;
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (stack.getItem() == newStack.getItem())
            return;

        LivingEntity entity = slotContext.getWearer();

        EntityUtils.removeAttributeModifier(Objects.requireNonNull(entity.getAttribute(Attributes.MOVEMENT_SPEED)), new AttributeModifier(SPEED_INFO.getRight(),
                SPEED_INFO.getLeft(), stats.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));

        entity.maxUpStep = 0.6F;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return new OutRunnerModel();
    }

    public static class Stats extends RelicStats {
        public float speedModifier = 0.01F;
        public int maxModifiers = 125;
    }
}