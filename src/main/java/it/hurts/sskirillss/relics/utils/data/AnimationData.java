package it.hurts.sskirillss.relics.utils.data;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@Data
public class AnimationData {
    private final List<Pair<Integer, Integer>> frames = new ArrayList<>();

    public int getLength() {
        int time = 0;

        for (Pair<Integer, Integer> pair : frames)
            time += pair.getRight();

        return time;
    }

    public static AnimationData builder() {
        return new AnimationData();
    }

    public AnimationData frame(int index, int time) {
        frames.add(Pair.of(index, time));

        return this;
    }
}
