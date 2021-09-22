package it.hurts.sskirillss.relics.items.relics;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicLoot;
import it.hurts.sskirillss.relics.items.relics.renderer.RageGloveModel;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import it.hurts.sskirillss.relics.utils.RelicUtils;
import it.hurts.sskirillss.relics.utils.tooltip.AbilityTooltip;
import it.hurts.sskirillss.relics.utils.tooltip.RelicTooltip;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class RageGloveItem extends RelicItem<RageGloveItem.Stats> implements ICurioItem {
    public static final String TAG_STACKS_AMOUNT = "stacks";
    public static final String TAG_UPDATE_TIME = "time";

    public static RageGloveItem INSTANCE;

    public RageGloveItem() {
        super(RelicData.builder()
                .rarity(Rarity.RARE)
                .config(Stats.class)
                .loot(RelicLoot.builder()
                        .table(RelicUtils.Worldgen.NETHER)
                        .chance(0.15F)
                        .build())
                .build());

        INSTANCE = this;
    }

    @Override
    public RelicTooltip getShiftTooltip(ItemStack stack) {
        return new RelicTooltip.Builder(stack)
                .ability(new AbilityTooltip.Builder()
                        .varArg("+" + (int) (config.dealtDamageMultiplier * 100) + "%")
                        .varArg("+" + (int) (config.incomingDamageMultiplier * 100) + "%")
                        .varArg(config.stackDuration)
                        .build())
                .build();
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        int stacks = NBTUtils.getInt(stack, TAG_STACKS_AMOUNT, 0);
        int time = NBTUtils.getInt(stack, TAG_UPDATE_TIME, 0);

        if (livingEntity.tickCount % 20 != 0 || stacks <= 0)
            return;

        if (time > 0)
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, time - 1);
        else {
            NBTUtils.setInt(stack, TAG_STACKS_AMOUNT, stacks - 1);
            NBTUtils.setInt(stack, TAG_UPDATE_TIME, config.stackDuration);
        }
    }

    private final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/items/models/rage_glove.png");

    @Override
    public void render(String identifier, int index, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        RageGloveModel model = new RageGloveModel();

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
    public static class RageGloveEvents {
        @SubscribeEvent
        public static void onLivingHurt(LivingHurtEvent event) {
            Stats config = INSTANCE.config;
            Entity source = event.getSource().getEntity();

            if (!(source instanceof LivingEntity))
                return;

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.RAGE_GLOVE.get(), (LivingEntity) source).ifPresent(triple -> {
                ItemStack stack = triple.getRight();
                int stacks = NBTUtils.getInt(stack, TAG_STACKS_AMOUNT, 0);

                NBTUtils.setInt(stack, TAG_STACKS_AMOUNT, ++stacks);
                NBTUtils.setInt(stack, TAG_UPDATE_TIME, config.stackDuration);

                event.setAmount(event.getAmount() + (event.getAmount() * (stacks * config.dealtDamageMultiplier)));
            });

            CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.RAGE_GLOVE.get(), event.getEntityLiving()).ifPresent(triple -> {
                ItemStack stack = triple.getRight();
                int stacks = NBTUtils.getInt(stack, TAG_STACKS_AMOUNT, 0);

                if (stacks <= 0)
                    return;

                event.setAmount(event.getAmount() + (event.getAmount() * (stacks * config.incomingDamageMultiplier)));
            });
        }
    }

    public static class Stats extends RelicStats {
        public int stackDuration = 3;
        public float dealtDamageMultiplier = 0.1F;
        public float incomingDamageMultiplier = 0.025F;
    }
}