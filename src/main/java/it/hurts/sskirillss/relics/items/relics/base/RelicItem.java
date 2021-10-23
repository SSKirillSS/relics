package it.hurts.sskirillss.relics.items.relics.base;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicAttribute;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.items.relics.base.handlers.TooltipHandler;
import it.hurts.sskirillss.relics.particles.circle.CircleTintData;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

public abstract class RelicItem<T extends RelicStats> extends Item implements ICurioItem {
    @Getter
    @Setter
    protected RelicData data;
    @Getter
    @Setter
    protected T config;

    @SneakyThrows
    public RelicItem(RelicData data) {
        super(data.getRarity() == null ? data.getProperties()
                : data.getProperties().rarity(data.getRarity()));

        setData(data);
        setConfig((T) data.getConfig().newInstance());
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (isBroken(stack))
            return;

        LivingEntity entity = slotContext.getWearer();
        RelicAttribute modifiers = getAttributes(stack);

        if (modifiers == null)
            return;

        modifiers.getAttributes().forEach(attribute ->
                EntityUtils.applyAttribute(entity, stack, attribute.getAttribute(), attribute.getMultiplier(), attribute.getOperation()));

        modifiers.getSlots().forEach(slot -> {
            int amount = slot.getRight();

            if (amount == 0)
                return;

            if (amount > 0)
                CuriosApi.getSlotHelper().growSlotType(slot.getLeft(), amount, entity);
            else
                CuriosApi.getSlotHelper().shrinkSlotType(slot.getLeft(), Math.abs(amount), entity);
        });
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        LivingEntity entity = slotContext.getWearer();
        RelicAttribute modifiers = getAttributes(stack);

        if (modifiers == null || (stack.getItem() == newStack.getItem() && !isBroken(newStack)))
            return;

        modifiers.getAttributes().forEach(attribute ->
                EntityUtils.removeAttribute(entity, stack, attribute.getAttribute(), attribute.getMultiplier(), attribute.getOperation()));

        modifiers.getSlots().forEach(slot -> {
            int amount = slot.getRight();

            if (amount == 0)
                return;

            if (amount > 0)
                CuriosApi.getSlotHelper().shrinkSlotType(slot.getLeft(), amount, entity);
            else
                CuriosApi.getSlotHelper().growSlotType(slot.getLeft(), Math.abs(amount), entity);
        });
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!isBroken(stack)) {
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
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn == null)
            return;

        TooltipHandler.setupTooltip(stack, worldIn, tooltip);

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean canEquip(String identifier, LivingEntity livingEntity, ItemStack stack) {
        return !isBroken(stack);
    }

    @Override
    public boolean canRender(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        return !isBroken(stack);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return !isBroken(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return data.getDurability().getMaxDurability();
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

    public RelicAttribute getAttributes(ItemStack stack) {
        return null;
    }

    public static boolean isBroken(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue() <= 0;
    }

    public void castAbility(PlayerEntity player, ItemStack stack) {

    }
}