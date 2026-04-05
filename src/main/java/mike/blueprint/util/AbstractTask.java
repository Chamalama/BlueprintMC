package mike.blueprint.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

@Getter
@Setter
public abstract class AbstractTask implements Runnable {

    private final Plugin plugin;
    private final long delay, period;
    private boolean async;
    private BukkitTask task;

    public AbstractTask(Plugin plugin, long delay, long period, boolean async) {
        this.plugin = plugin;
        this.delay = delay;
        this.period = period;
        this.async = async;
        if (!async) {
            task = Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period);
        }else{
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, delay, period);
        }
    }

    public void cancel() {
        if(this.task != null) {
            this.task.cancel();
        }
    }

}
