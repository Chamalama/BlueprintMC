package mike.blueprint.data;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.persistence.PersistentDataType;

public class DataTypes {

    private static final Object2ObjectMap<Class<?>, PersistentDataType<byte[], ?>> DATA_TYPES = new Object2ObjectOpenHashMap<>();

    public static <V> PersistentDataType<byte[], V> get(Class<V> clazz) {
        return (PersistentDataType<byte[], V>) DATA_TYPES.computeIfAbsent(clazz, type -> new DataType<>(clazz));
    }

}
