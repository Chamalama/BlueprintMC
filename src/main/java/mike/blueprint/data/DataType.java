package mike.blueprint.data;

import mike.blueprint.util.ByteUtil;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.io.IOException;

public class DataType<V> implements PersistentDataType<byte[], V> {

    private final Class<V> type;

    public DataType(Class<V> type) {
        this.type = type;
    }

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<V> getComplexType() {
        return type;
    }

    @Override
    public byte @NonNull [] toPrimitive(@NonNull V v, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        try {
            return ByteUtil.serialize(v);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NonNull V fromPrimitive(byte @NonNull [] bytes, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        try {
            return ByteUtil.deserialize(bytes);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
