package it.hurts.sskirillss.relics.effects;

import it.hurts.sskirillss.relics.init.EffectRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrunkennessEffect extends MobEffect {
    public DrunkennessEffect() {
        super(MobEffectCategory.HARMFUL, 0X6836AA);
    }

    @Mod.EventBusSubscriber
    public static class Events {
        @SubscribeEvent
        public static void onChatMessage(ServerChatEvent event) {
            Player player = event.getPlayer();
            RandomSource random = player.getRandom();

            if (player.hasEffect(EffectRegistry.DRUNKENNESS.get())) {
                MutableComponent component = Component.literal("");

                ArrayList<Component> components = new ArrayList<>(event.getMessage().getSiblings());

                components.add(0, event.getMessage());

                boolean isNotNickname = false;

                for (Component entry : components) {
                    List<String> result = new ArrayList<>();

                    for (String word : entry.getString().split(" ")) {
                        if (!isNotNickname && !Arrays.asList(event.getMessage().getString().split(" ")).contains(word)) {
                            result.add(word);

                            continue;
                        }

                        isNotNickname = true;

                        if (random.nextFloat() <= 0.1F)
                            result.add("*ик*");

                        int length = word.length();

                        if (length < 3) {
                            result.add(word);

                            continue;
                        }

                        if (random.nextFloat() <= 0.1F) {
                            int step = 1 + random.nextInt(length - 2);
                            int offset = 1 + random.nextInt(length - step - 1);

                            result.add(new StringBuilder(word).replace(offset, offset + step, "*ик*").toString());
                        } else
                            result.add(word);
                    }

                    component.append(Component.literal(String.join(" ", result.toArray(new String[]{}))).withStyle(entry.getStyle()));
                }

                event.setMessage(component);
            }
        }
    }
}