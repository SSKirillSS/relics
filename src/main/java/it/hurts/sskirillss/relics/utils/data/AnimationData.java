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

    public Pair<Integer, Integer> getFrameByTime(long time) {
        int frames = getFrames().size();

        long remainder = (time - 1) % getLength();

        int index = 0;

        while (remainder > 0) {
            Pair<Integer, Integer> pair = getFrames().get(index);

            remainder -= pair.getRight();

            index = (index >= frames - 1) ? frames - 1 : index + 1;
        }

        return getFrames().get(index);
    }

    public static AnimationData builder() {
        return new AnimationData();
    }

    public AnimationData frame(int index, int time) {
        frames.add(Pair.of(index, time));

        return this;
    }
}
