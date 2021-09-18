package it.hurts.sskirillss.relics.items.relics;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.renderer.ScarabTalismanModel;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.MutablePair;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ScarabTalismanItem extends RelicItem<ScarabTalismanItem.Stats> implements ICurioItem {
    private final MutablePair<String, UUID> SPEED_INFO = new MutablePair<>(Reference.MODID
            + ":" + "scarab_talisman_movement_speed", UUID.fromString("09bc5b60-3277-45ee-8bf0-aae7acba4385"));

    public static ScarabTalismanItem INSTANCE;

    public ScarabTalismanItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.DESERT)
                        .chance(0.15F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .varArg("+" + (int) (config.speedModifier * 100 - 100) + "%")
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .varArg("+" + (int) (config.digModifier * 100 - 100) + "%")
                        .build())
                .ability(new AbilityTooltip.Builder()
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        World world = livingEntity.getCommandSenderWorld();

        if (config.allowedBiomes.stream().map(Biome.Category::byName).collect(Collectors.toList())
                .contains(world.getBiome(livingEntity.blockPosition()).getBiomeCategory()))
            EntityUtils.applyAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(),
                    SPEED_INFO.getLeft(), config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
        else
            EntityUtils.removeAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(),
                    config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        ModifiableAttributeInstance movementSpeed = slotContext.getWearer().getAttribute(Attributes.MOVEMENT_SPEED);

        EntityUtils.removeAttributeModifier(movementSpeed, new AttributeModifier(SPEED_INFO.getRight(), SPEED_INFO.getLeft(),
                config.speedModifier, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    private final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/items/models/scarab_talisman.png");

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        ScarabTalismanModel model = new ScarabTalismanModel();

        matrixStack.pushPose();

        model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTicks);
        ICurio.RenderHelper.followBodyRotations(livingEntity, model);
        model.renderToBuffer(matrixStack, renderTypeBuffer.getBuffer(RenderType.entityTranslucent(TEXTURE)),
                light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.popPose();
    }

    @Override
    public boolean canRender(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        return true;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ScarabTalismanServerEvents {
        @SubscribeEvent
        public static void onEntityHurt(LivingHurtEvent event) {
            LivingEntity entity = event.getEntityLiving();

            if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SCARAB_TALISMAN.get(),
                    entity).isPresent() && event.getSource() == DamageSource.IN_WALL) {
                entity.heal(event.getAmount());

                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onEntityAttack(LivingAttackEvent event) {
            if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SCARAB_TALISMAN.get(),
                    event.getEntityLiving()).isPresent() && event.getSource() == DamageSource.IN_WALL)
                event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onBlockBreakCalculate(PlayerEvent.BreakSpeed event) {
            Stats config = INSTANCE.config;

            if (CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.SCARAB_TALISMAN.get(),
                    event.getEntityLiving()).isPresent())
                event.setNewSpeed(event.getNewSpeed() * config.digModifier);
        }
    }

    public static class Stats extends RelicStats {
        public float speedModifier = 1.5F;
        public float digModifier = 1.15F;
        public List<String> allowedBiomes = Arrays.asList("mesa", "desert");
    }
}