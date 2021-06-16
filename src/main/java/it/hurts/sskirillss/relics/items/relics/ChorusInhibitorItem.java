package it.hurts.sskirillss.relics.items.relics;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.configs.variables.stats.RelicStats;
import it.hurts.sskirillss.relics.init.ItemRegistry;
import it.hurts.sskirillss.relics.items.RelicItem;
import it.hurts.sskirillss.relics.utils.NBTUtils;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.Collections;
import java.util.List;

public class ChorusInhibitorItem extends RelicItem<ChorusInhibitorItem.Stats> implements ICurioItem {
    public static final String TAG_POSITION = "pos";
    public static final String TAG_TIME = "time";
    public static final String TAG_WORLD = "world";

    public static ChorusInhibitorItem INSTANCE;

    public ChorusInhibitorItem() {
        super(Rarity.RARE);

        INSTANCE = this;
    }

    @Override
    public List<ITextComponent> getShiftTooltip(ItemStack stack) {
        List<ITextComponent> tooltip = Lists.newArrayList();
        tooltip.add(new TranslationTextComponent("tooltip.relics.chorus_inhibitor.shift_1"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.chorus_inhibitor.shift_2"));
        tooltip.add(new TranslationTextComponent("tooltip.relics.chorus_inhibitor.shift_3"));
        return tooltip;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (!NBTUtils.getString(stack, TAG_POSITION, "").equals("")) {
            Vector3d pos = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));
            tooltip.add(new TranslationTextComponent("tooltip.relics.chorus_inhibitor.tooltip_1", pos.x(), pos.y(), pos.z()));
            tooltip.add(new TranslationTextComponent("tooltip.relics.chorus_inhibitor.tooltip_2", NBTUtils.getInt(stack, TAG_TIME, 0)));
        }
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!(livingEntity instanceof PlayerEntity) || livingEntity.getCommandSenderWorld().isClientSide() || livingEntity.tickCount % 20 != 0) return;
        PlayerEntity player = (PlayerEntity) livingEntity;
        int time = NBTUtils.getInt(stack, TAG_TIME, 0);
        if (time > 0) NBTUtils.setInt(stack, TAG_TIME, time - 1);
        else if (!NBTUtils.getString(stack, TAG_POSITION, "").equals("")) {
            Vector3d pos = NBTUtils.parsePosition(NBTUtils.getString(stack, TAG_POSITION, ""));
            ServerWorld world = NBTUtils.parseWorld(player.getCommandSenderWorld(), NBTUtils.getString(stack, TAG_WORLD, ""));
            if (pos == null || world == null) return;
            ((ServerPlayerEntity) player).teleportTo(world, pos.x(), pos.y(), pos.z(), player.yRot, player.xRot);
            player.getCommandSenderWorld().playSound(player, pos.x(), pos.y(), pos.z(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            NBTUtils.setString(stack, TAG_POSITION, "");
            NBTUtils.setString(stack, TAG_WORLD, "");
            NBTUtils.setInt(stack, TAG_TIME, 0);
        }
    }

    @Override
    public List<ResourceLocation> getLootChests() {
        return Collections.singletonList(LootTables.END_CITY_TREASURE);
    }

    @Override
    public Class<Stats> getConfigClass() {
        return Stats.class;
    }

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class ChorusInhibitorEvents {
        @SubscribeEvent
        public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
            Stats config = INSTANCE.config;
            if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            if (player.getMainHandItem().getItem() == Items.CHORUS_FRUIT && !player.isShiftKeyDown()) {
                CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.CHORUS_INHIBITOR.get(), player).ifPresent(triple -> {
                    ItemStack stack = triple.getRight();
                    int time = NBTUtils.getInt(stack, TAG_TIME, 0);
                    NBTUtils.setInt(stack, TAG_TIME, time + config.timePerChorus);
                    if (time <= 0) {
                        NBTUtils.setString(stack, TAG_POSITION, NBTUtils.writePosition(player.position()));
                        NBTUtils.setString(stack, TAG_WORLD, player.getCommandSenderWorld().dimension().location().toString());
                    }
                    player.getMainHandItem().shrink(1);
                    event.setCanceled(true);
                });
            }
        }

    }

    @SubscribeEvent
    public static void onPlayerTeleported(EnderTeleportEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity
                && CuriosApi.getCuriosHelper().findEquippedCurio(ItemRegistry.CHORUS_INHIBITOR.get(), event.getEntityLiving()).isPresent()) {
            event.setAttackDamage(0.0F);
        }
    }

    public static class Stats extends RelicStats {
        public int timePerChorus = 30;
    }
}