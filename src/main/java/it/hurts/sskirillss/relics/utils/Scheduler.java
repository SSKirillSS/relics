package it.hurts.sskirillss.relics.utils;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class Scheduler {
    private static final List<Task> allTasksQueue = new ArrayList<>();
    private static final List<Task> allTasks = new ArrayList<>();

    public static void schedule(int ticks, Runnable task) {
        allTasksQueue.add(new Task(ticks, task));
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent e) {
        if (e.phase == TickEvent.Phase.START) {
            while (!allTasksQueue.isEmpty())
                allTasks.add(allTasksQueue.remove(0));

            allTasks.removeIf(Task::tick);
        }
    }

    @SubscribeEvent
    public static void reset(ServerStoppedEvent e) {
        allTasks.clear();
    }

    private static class Task {
        int ticks;
        Runnable task;

        public Task(int ticks, Runnable task) {
            this.ticks = ticks;
            this.task = task;
        }

        public boolean tick() {
            if (ticks == 0) task.run();
            return --ticks < 0;
        }
    }
}