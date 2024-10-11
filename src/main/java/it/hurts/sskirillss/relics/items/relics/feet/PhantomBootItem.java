package it.hurts.sskirillss.relics.items.relics.feet;

import it.hurts.sskirillss.relics.init.BlockRegistry;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.RelicData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastData;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastStage;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilitiesData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.LevelingData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.StyleData;
import it.hurts.sskirillss.relics.items.relics.base.data.style.TooltipData;
import it.hurts.sskirillss.relics.utils.WorldUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;

import static it.hurts.sskirillss.relics.init.DataComponentRegistry.TIME;
import static it.hurts.sskirillss.relics.init.DataComponentRegistry.TOGGLED;

public class PhantomBootItem extends RelicItem {
    @Override
    public RelicData constructDefaultRelicData() {
        return RelicData.builder()
                .abilities(AbilitiesData.builder()
                        .ability(AbilityData.builder("bridge")
                                .active(CastData.builder()
                                        .type(CastType.TOGGLEABLE)
                                        .build())
                                .build())
                        .build())
                .leveling(new LevelingData(100, 10, 200))
                .style(StyleData.builder()
                        .tooltip(TooltipData.builder()
                                .borderTop(0xff1e1e25)
                                .borderBottom(0xff1e1e25)
                                .textured(true)
                                .build())
                        .build())
                .build();
    }

    @Override
    public void castActiveAbility(ItemStack stack, Player player, String ability, CastType type, CastStage stage) {
        var level = player.level();

        if (ability.equals("bridge")) {
            if (stage == CastStage.START) {
                Vec3 motion = player.getDeltaMovement();

                if (motion.y <= -0.5D)
                    player.setDeltaMovement(motion.x, -motion.y, motion.z);
            }

            if (stage == CastStage.TICK && isToggled(stack)) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        var relativePos = player.blockPosition().offset(x, -1, z);

                        if (!level.isEmptyBlock(relativePos))
                            continue;

                        level.setBlockAndUpdate(relativePos, BlockRegistry.PHANTOM_BLOCK.get().defaultBlockState());
                    }
                }
            }
        }
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player))
            return;

        var level = player.getCommandSenderWorld();

        if (level.isClientSide())
            return;

        var block = BlockRegistry.PHANTOM_BLOCK.get();

        var onBridge = level.getBlockState(player.blockPosition().atY((int) Math.floor(WorldUtils.getGroundHeight(player, player.position(), 8)))).getBlock() == block
                || player.isColliding(player.blockPosition(), block.defaultBlockState());

        var time = getTime(stack);

        if (isToggled(stack)) {
            if (onBridge) {
                if (player.getKnownMovement().multiply(1F, 0F, 1F).length() > 0) {
                    if (time > 0)
                        addTime(stack, -1);
                } else {
                    if (time < getMaxTime(stack))
                        addTime(stack, 1);
                    else setToggled(stack, false);
                }
            } else if (time > 0)
                addTime(stack, -1);
        } else {
            if (player.onGround() && !onBridge)
                setToggled(stack, true);
        }
    }

    public int getMaxTime(ItemStack stack) {
        return 30;
    }

    public int getTime(ItemStack stack) {
        return stack.getOrDefault(TIME, 0);
    }

    public void setTime(ItemStack stack, int time) {
        stack.set(TIME, Mth.clamp(time, 0, getMaxTime(stack)));
    }

    public void addTime(ItemStack stack, int time) {
        setTime(stack, getTime(stack) + time);
    }

    public boolean isToggled(ItemStack stack) {
        return stack.getOrDefault(TOGGLED, true);
    }

    public void setToggled(ItemStack stack, boolean toggled) {
        stack.set(TOGGLED, toggled);
    }
}