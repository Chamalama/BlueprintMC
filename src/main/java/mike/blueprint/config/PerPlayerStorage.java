package mike.blueprint.config;

import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class PerPlayerStorage<V> {

    private final Plugin plugin;
    private final String folderName;
    private final Supplier<V> value;

    private final Map<UUID, V> storedPlayerData = new ConcurrentHashMap<>();

    public PerPlayerStorage(Plugin plugin, String folderName, Supplier<V> value) {
        this.plugin = plugin;
        this.folderName = folderName;
        this.value = value;
    }

    public CompletableFuture<V> preload(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            V data = (V) Json.get(plugin, folderName, uuid.toString(), value.get().getClass());
            if(data == null) {
                data = value.get();
                storedPlayerData.put(uuid, data);
                Json.write(plugin, folderName, uuid.toString(), data, false);
            }else{
                storedPlayerData.put(uuid, data);
            }
            return data;
        });
    }

    public V get(UUID uuid) {
        return storedPlayerData.get(uuid);
    }

    public CompletableFuture<Void> update(UUID uuid) {
        final V storedData = storedPlayerData.get(uuid);
        if(storedData != null) {
            return CompletableFuture.runAsync(() -> Json.write(plugin, folderName, uuid.toString(), storedData, true));
        }
        return CompletableFuture.completedFuture(null);
    }

    public Collection<V> getAll() {
        return storedPlayerData.values();
    }

    public void clear(UUID uuid) {
        storedPlayerData.remove(uuid);
    }

}
