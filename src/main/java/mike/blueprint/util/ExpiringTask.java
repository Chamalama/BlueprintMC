package mike.blueprint.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

@Getter
public class ExpiringTask implements Consumer<BukkitTask> {

    private final Plugin plugin;
    private boolean async;
    private int runCount, currentRuns;
    private Runnable action, completeAction;

    public ExpiringTask(Plugin plugin) {
        this.plugin = plugin;
        this.currentRuns = 0;
    }

    /**
     * Sets whether this task will be async or not
     * @param async if true - will not use main thread
     * @return this task for chaining
     */
    public ExpiringTask setAsync(boolean async) {
        this.async = async;
        return this;
    }

    /**
     * Sets the amount of times this task will run
     * @param runCount count
     * @return this task for chaining
     */
    public ExpiringTask runCount(int runCount) {
        this.runCount = runCount;
        return this;
    }

    /**
     * Sets the action that will be fired during the task
     * @param action to run
     * @return this task instance for chaining
     */
    public ExpiringTask setAction(Runnable action) {
        this.action = action;
        return this;
    }

    public ExpiringTask onComplete(Runnable action) {
        this.completeAction = action;
        return this;
    }

    /**
     * Runs the action on this task
     * @param delay ticks before first run
     * @param period ticks between each run
     */
    public void run(long delay, long period) {
        if(async) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, delay, period);
        }else{
            Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period);
        }
    }

    @Override
    public void accept(BukkitTask bukkitTask) {
        if(action == null) {
            bukkitTask.cancel();
            return;
        }
        action.run();
        if(runCount > 0 && ++currentRuns >= runCount) {
            if(this.completeAction != null) {
                this.completeAction.run();
            }
            bukkitTask.cancel();
        }
    }
}
