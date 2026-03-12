package mike.blueprint.util;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class NMSUtil {

    public static ServerPlayer getServerPlayer(Player player) {
        return ((CraftPlayer)player).getHandle();
    }

    public static Connection getConnection(Player player) {
        final ServerPlayer serverPlayer = getServerPlayer(player);
        return serverPlayer.connection.connection;
    }

    public static ServerGamePacketListenerImpl getImpl(Player player) {
        final ServerPlayer serverPlayer = getServerPlayer(player);
        return serverPlayer.connection;
    }

    public static ServerLevel getLevel(Player player) {
        return getServerPlayer(player).level();
    }

    public static ServerLevel getLevel(World world) {
        return ((CraftWorld)world).getHandle();
    }

    public static ServerLevel getLevel(Entity entity) {
        return getLevel(entity.getWorld());
    }

    public static void sendPacket(Player player, Packet<?> packet) {
        getImpl(player).send(packet);
    }

}
