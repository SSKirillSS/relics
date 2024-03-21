package it.hurts.sskirillss.relics.items.relics.base.data.leveling;

import com.mojang.datafixers.util.Function3;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.CastPredicate;
import it.hurts.sskirillss.relics.items.relics.base.data.cast.misc.CastType;
import lombok.Builder;
import lombok.Data;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class AbilityData {
    private final String id;

    public static AbilityDataBuilder builder(String id) {
        AbilityDataBuilder builder = new AbilityDataBuilder();

        builder.id(id);

        return builder;
    }

    @Builder.Default
    private Function3<Player, ItemStack, String, String> icon = (player, stack, ability) -> ability;

    @Builder.Default
    private Map<String, StatData> stats;

    @Builder.Default
    private int maxLevel = 10;

    @Builder.Default
    private int requiredLevel = 0;

    @Builder.Default
    private int requiredPoints = 1;

    @Builder.Default
    private Pair<CastType, CastPredicate> castData;

    public static class AbilityDataBuilder {
        private Map<String, StatData> stats = new HashMap<>();

        public AbilityDataBuilder stat(StatData stat) {
            stats.put(stat.getId(), stat);

            return this;
        }

        private Pair<CastType, CastPredicate> castData = Pair.of(CastType.NONE, CastPredicate.builder().build());

        private AbilityDataBuilder id(String id) {
            this.id = id;

            return this;
        }

        public AbilityDataBuilder active(CastType type) {
            castData = Pair.of(type, CastPredicate.builder().build());

            return this;
        }

        public AbilityDataBuilder active(CastType type, CastPredicate predicates) {
            castData = Pair.of(type, predicates);

            return this;
        }
    }
}