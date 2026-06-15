package mike.blueprint.config;

import lombok.Getter;
import mike.blueprint.Blueprint;
import mike.blueprint.util.ByteUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class SQLiteStorage {

    private final Plugin plugin;
    private final String dbName, folder;
    private final Connection connection;

    private final Map<String, Object> dataMap = new ConcurrentHashMap<>();

    public SQLiteStorage(Plugin plugin, String folder, String dbName) {
        this.plugin = plugin;
        this.folder = folder;
        this.dbName = dbName;
        final File file = new File(plugin.getDataFolder(), folder);
        file.mkdirs();
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/" + folder + "/" + dbName + ".db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {

    }

    public void createUUIDTable(String tableName) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (id TEXT PRIMARY KEY, data BLOB NOT NULL)")) {
            preparedStatement.execute();
        }
    }

    public void createIDTable(String tableName) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (id TEXT PRIMARY KEY, data BLOB NOT NULL)")) {
            preparedStatement.execute();
        }
    }

    public <V> void writeData(String table, String key, V data) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO " + table + " (id, data)" + " VALUES (?, ?)")) {
            preparedStatement.setString(1, key);
            preparedStatement.setBytes(2, ByteUtil.serialize(dataMap.getOrDefault(key, data)));
            preparedStatement.executeUpdate();
            dataMap.put(key, data);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <V> void writeUncachedData(String table, String key, V data) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO " + table + " (id, data)" + " VALUES (?, ?)")) {
            preparedStatement.setString(1, key);
            preparedStatement.setBytes(2, ByteUtil.serialize(data));
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <V> void writeUncachedData(String table, String key, byte[] data) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO " + table + " (id, data)" + " VALUES (?, ?)")) {
            preparedStatement.setString(1, key);
            preparedStatement.setBytes(2, data);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <V> void writeUncachedDataNoReplace(String table, String key, V data) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + table + " (id, data)" + " VALUES (?, ?)")) {
            preparedStatement.setString(1, key);
            preparedStatement.setBytes(2, ByteUtil.serialize(data));
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <V> void writePlayerData(String table, UUID uuid, V data) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO " + table + " (uuid, data)" + " VALUES (?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setBytes(2, ByteUtil.serialize(data));
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteColumn(String table, String column, String key) {
        try(PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM " + table + " WHERE " + column + " = ?"
        )) {
            statement.setString(1, key);
            statement.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <V> V getData(String table, String column, String key) {
        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT data FROM " + table + " WHERE " + column + " = ?"
        )){
            preparedStatement.setString(1, key);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                final byte[] data = resultSet.getBytes("data");
                final V byteData = ByteUtil.deserialize(data);
                dataMap.put(key, byteData);
                return byteData;
            }
            return null;
        }catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public <V> V getUncachedData(String table, String column, String key) {
        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT data FROM " + table + " WHERE " + column + " = ?"
        )){
            preparedStatement.setString(1, key);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                final byte[] data = resultSet.getBytes("data");
                if(data == null) return null;
                return ByteUtil.deserialize(data);
            }
        }catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public <V> Collection<V> getAllColumnData(String table, String column, Class<V> type) {
        final Collection<V> data = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT " + column + " FROM " + table
        )){
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                final byte[] columnData = resultSet.getBytes("data");
                if(columnData == null) return null;
                boolean useStackDeserialization = ItemStack.class.isAssignableFrom(type);
                final V object = useStackDeserialization ? (V) ItemStack.deserializeBytes(columnData) : ByteUtil.deserialize(columnData);
                data.add(object);
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    public <V> V getCachedData(String key) {
        return (V) dataMap.get(key);
    }

    private void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(Blueprint.getInst(), runnable);
    }

}
