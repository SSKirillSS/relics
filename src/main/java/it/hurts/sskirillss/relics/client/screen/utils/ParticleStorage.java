package it.hurts.sskirillss.relics.client.screen.utils;

import com.google.common.collect.Lists;
import it.hurts.sskirillss.relics.client.screen.description.data.base.ParticleData;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleStorage {
    private static Map<Class<? extends Screen>, List<ParticleData>> SCREENS = new HashMap<>();

    public static Map<Class<? extends Screen>, List<ParticleData>> getParticlesData() {
        return SCREENS;
    }

    public static List<ParticleData> getParticles(Screen screen) {
        return getParticles(screen.getClass());
    }

    public static List<ParticleData> getParticles(Class<? extends Screen> clazz) {
        return getParticlesData().getOrDefault(clazz, new ArrayList<>());
    }

    public static void addParticle(Screen screen, ParticleData... data) {
        addParticle(screen.getClass(), data);
    }

    public static void addParticle(Class<? extends Screen> clazz, ParticleData... data) {
        List<ParticleData> particles = getParticles(clazz);

        particles.addAll(Lists.newArrayList(data));

        getParticlesData().put(clazz, particles);
    }
}