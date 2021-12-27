package it.hurts.sskirillss.relics.items.relics.base;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.api.durability.IRepairableItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicSlotModifier;
import it.hurts.sskirillss.relics.client.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.client.tooltip.base.RelicTooltip;
import it.hurts.sskirillss.relics.configs.data.relics.RelicConfigData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttributeModifier;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.utils.DurabilityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class RelicItem<T extends RelicStats> extends Item implements ICurioItem, IRepairableItem {
    @Getter
    @Setter
    protected RelicData data;
    @Getter
    @Setter
    protected T stats;
    @Getter
    @Setter
    protected RelicConfigData<T> configData;

    @SneakyThrows
    public RelicItem(RelicData data) {
        super(data.getRarity() == null ? data.getProperties()
                : data.getProperties().rarity(data.getRarity()));

        setData(data);
        setConfig(getConfigData());
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> modifiers = LinkedHashMultimap.create();

        if (DurabilityUtils.isBroken(stack))
            return modifiers;

        RelicAttributeModifier attributes = getAttributeModifiers(stack);
        RelicSlotModifier slots = getSlotModifiers(stack);

        if (attributes != null)
            attributes.getAttributes().forEach(attribute ->
                    modifiers.put(attribute.getAttribute(), new AttributeModifier(uuid,
                            stack.getItem().getRegistryName().getPath() + "_" + attribute.getAttribute().getRegistryName().getPath(),
                            attribute.getMultiplier(), attribute.getOperation())));

        if (slots != null)
            slots.getModifiers().forEach(slot ->
                    CuriosApi.getCuriosHelper().addSlotModifier(modifiers, slot.getLeft(), uuid, slot.getRight(), AttributeModifier.Operation.ADDITION));

        return modifiers;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!DurabilityUtils.isBroken(stack)) {
            Vector3d pos = entity.position();

            entity.getCommandSenderWorld().addParticle(new CircleTintData(stack.getRarity().color.getColor() != null
                            ? new Color(stack.getRarity().color.getColor(), false) : new Color(255, 255, 255),
                            random.nextFloat() * 0.025F + 0.04F, 25, 0.95F, true),
                    pos.x() + MathUtils.randomFloat(random) * 0.25F, pos.y() + 0.1F,
                    pos.z() + MathUtils.randomFloat(random) * 0.25F, 0, random.nextFloat() * 0.05D, 0);
        }

        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        BipedModel<LivingEntity> model = getModel();

        if (model == null)
            return;

        matrixStack.pushPose();

        model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTicks);
        ICurio.RenderHelper.followBodyRotations(livingEntity, model);
        model.renderToBuffer(matrixStack, renderTypeBuffer.getBuffer(RenderType.entityTranslucent(new ResourceLocation(
                        Reference.MODID, "textures/items/models/" + this.getRegistryName().getPath() + ".png"))),
                light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.popPose();
    }

    @Override
    public List<ITextComponent> getTagsTooltip(List<ITextComponent> tagTooltips, ItemStack stack) {
        return new ArrayList<>();
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.UNBREAKING;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean showAttributesTooltip(String identifier, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canEquip(String identifier, LivingEntity livingEntity, ItemStack stack) {
        return !DurabilityUtils.isBroken(stack);
    }

    @Override
    public boolean canRender(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        return !DurabilityUtils.isBroken(stack);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return !DurabilityUtils.isBroken(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        switch (stack.getRarity()) {
            case COMMON:
                return 100;
            case UNCOMMON:
                return 150;
            case RARE:
                return 200;
            case EPIC:
                return 250;
            default:
                return 0;
        }
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return getMaxDamage(stack) > 0;
    }

    @OnlyIn(Dist.CLIENT)
    public BipedModel<LivingEntity> getModel() {
        return null;
    }

    public RelicTooltip getTooltip(ItemStack stack) {
        return null;
    }

    public RelicAttributeModifier getAttributeModifiers(ItemStack stack) {
        return null;
    }

    public RelicSlotModifier getSlotModifiers(ItemStack stack) {
        return null;
    }

    public void castAbility(PlayerEntity player, ItemStack stack) {

    }

    public void setConfig(RelicConfigData<?> data) {
        this.configData = (RelicConfigData<T>) data;
        this.stats = (T) data.getStats();
    }
}