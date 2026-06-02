package mike.blueprint.storage;

import mike.blueprint.Blueprint;
import mike.blueprint.config.SQLiteStorage;
import mike.blueprint.loader.Component;

@Component
public class BlockStorage extends SQLiteStorage {

    public static final String TABLE = "blocks";

    public BlockStorage() {
        super(Blueprint.getInst(), "block-data");
    }

    @Override
    public void load() {
        try {
            this.createIDTable(TABLE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
