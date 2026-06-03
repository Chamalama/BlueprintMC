package mike.blueprint.store;

import mike.blueprint.loader.Component;
import mike.blueprint.storage.BlockStorage;
import mike.blueprint.util.FastLocation;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class BlockStore {

    private final BlockStorage blockStorage;

    private final Executor executor = Executors.newSingleThreadExecutor();

    public BlockStore(BlockStorage blockStorage) {
        this.blockStorage = blockStorage;
    }

    public <T> void setBlockData(Block block, T data) {
        final Location location = block.getLocation();
        final FastLocation fastLocation = new FastLocation(location);
        executor.execute(() -> {
            blockStorage.writeData(BlockStorage.TABLE, fastLocation.serialize(), data);
        });
    }

    public <T> T getBlockData(Block block) {
        final Location location = block.getLocation();
        final FastLocation fastLocation = new FastLocation(location);
        final T data = blockStorage.getCachedData(fastLocation.serialize());
        return data != null ? data : blockStorage.getData(BlockStorage.TABLE, "id", fastLocation.serialize());
    }

    public <T> T getBlockData(Location location) {
        final FastLocation fastLocation = new FastLocation(location);
        final T data = blockStorage.getCachedData(fastLocation.serialize());
        return data != null ? data : blockStorage.getData(BlockStorage.TABLE, "id", fastLocation.serialize());
    }

    public void clearBlockData(Block block) {
        final Location location = block.getLocation();
        final FastLocation fastLocation = new FastLocation(location);
        executor.execute(() -> {
            blockStorage.deleteColumn(BlockStorage.TABLE, "id", fastLocation.serialize());
        });
    }

    public void clearBlockData(Location location) {
        final FastLocation fastLocation = new FastLocation(location);
        executor.execute(() -> {
            blockStorage.deleteColumn(BlockStorage.TABLE, "id", fastLocation.serialize());
        });
    }

}
