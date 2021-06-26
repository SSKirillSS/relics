package it.hurts.sskirillss.relics.utils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Queues;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class Scheduler {

    private static final Queue<Task<?>> tasks = Queues.newArrayDeque();

    private static Scheduler instance;

    public Scheduler() {
        instance = this;
    }

    public static Scheduler getInstance() {
        return instance;
    }

    public void update() {
        synchronized (tasks) {
            Iterables.removeIf(tasks, task -> {
                try {
                    return task.update();
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
            });
        }
    }

    public <V> Task<V> addTask(@NotNull Callable<V> callable, long delay, long repeats) {
        Task<V> task = new Task<>(callable, delay, repeats);
        synchronized (tasks) {
            tasks.add(task);
            return task;
        }
    }

    public <V> Task<V> addTask(Callable<V> callable, long delay) {
        return addTask(callable, delay, 0);
    }

    public <V> Task<V> addTask(Callable<V> callable) {
        return addTask(callable, 1);
    }

    public Task<Object> addTask(Runnable runnable, long delay, long repeats) {
        return addTask(Executors.callable(runnable), delay, repeats);
    }

    public Task<Object> addTask(Runnable runnable, long delay) {
        return addTask(runnable, delay, 0);
    }

    public Task<Object> addTask(Runnable runnable) {
        return addTask(runnable, 1);
    }

    public static class Task<T> {

        private final Callable<T> callable;
        private final Queue<T> results;
        private final long delay;
        private long time;
        private long repeats;
        private boolean finished, canceled;

        private Task(@Nonnull Callable<T> task, long taskDelay, long taskRepeats) {
            callable = task;
            results = Queues.newArrayDeque();
            delay = time = taskDelay;
            repeats = taskRepeats;
        }

        public boolean update() throws Exception {
            if (canceled) {
                return true;
            }

            time--;
            if (time <= 0) {
                execute();
                if (--repeats > 0) {
                    time = delay;
                } else {
                    finished = true;
                }
            }
            return finished;
        }

        private void execute() throws Exception {
            T value = callable.call();
            if (value != null) {
                results.add(value);
            }
        }

        public void finish() throws Exception {
            if (!finished) {
                execute();
            }
            time = 0;
            finished = true;
        }

        public void cancel() {
            canceled = true;
        }

        public T getResult() {
            return results.poll();
        }
    }

    @Mod.EventBusSubscriber
    public static class SchedulerHandler {
        @SubscribeEvent
        public static void onServerUpdate(TickEvent.ServerTickEvent event) {
            if (tasks.isEmpty() || event.phase != TickEvent.Phase.END) return;
            getInstance().update();
        }
    }
}