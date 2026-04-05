package mike.blueprint.config;

import lombok.Getter;
import mike.blueprint.util.ByteUtil;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public abstract class SQLiteStorage {

    private final Plugin plugin;
    private final String dbName;
    private final Connection connection;

    private final Map<String, Object> dataMap = new HashMap<>();

    public SQLiteStorage(Plugin plugin, String dbName) {
        this.plugin = plugin;
        this.dbName = dbName;
        plugin.getDataFolder().mkdirs();
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/" + dbName + ".db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {

    }

    public void createUUIDTable(String tableName) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (uuid TEXT PRIMARY KEY, data BLOB NOT NULL)")) {
            preparedStatement.execute();
        }
    }

    public void createIDTable(String tableName) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (id TEXT PRIMARY KEY, data BLOB NOT NULL)")) {
            preparedStatement.execute();
        }
    }

    public <V> void writeData(String table, String key, V data) {
        try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO " + table + " (id, data)" + " VALUES (?, ?)")) {
            preparedStatement.setString(1, key);
            preparedStatement.setBlob(2, new ByteArrayInputStream(ByteUtil.serialize(dataMap.getOrDefault(key, data))));
            preparedStatement.executeUpdate();
            dataMap.put(key, data);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <V> void writePlayerData(String table, UUID uuid, V data) {
        try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT OR REPLACE INTO " + table + " (uuid, data)" + " VALUES (?, ?)")) {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setBlob(2, new ByteArrayInputStream(ByteUtil.serialize(data)));
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

    public <V> V getCachedData(String key) {
        return (V) dataMap.get(key);
    }

}
