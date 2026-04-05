package mike.blueprint.util;

import mike.blueprint.Blueprint;
import mike.blueprint.world.EmptyWorld;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import javax.security.auth.callback.Callback;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;

public class WorldUtil {

    private static Location spawnLocation;

    public static void createEmptyWorld(String name, String environment, Consumer<World> callback) {
        Bukkit.getGlobalRegionScheduler().execute(Blueprint.getInst(), () -> {
            final WorldCreator worldCreator = new WorldCreator(name);
            worldCreator.generator(new EmptyWorld());
            worldCreator.environment(World.Environment.valueOf(environment));
            final World world = Bukkit.createWorld(worldCreator);
            callback.accept(world);
        });
    }

    public static void loadWorld(String name) {
        final WorldCreator worldCreator = new WorldCreator(name);
        worldCreator.generator(new EmptyWorld());
        worldCreator.createWorld();
    }

    public static void createCopy(String initialWorld, String worldName, Consumer<World> callback) {
        Bukkit.getGlobalRegionScheduler().execute(Blueprint.getInst(), () -> {
            final World world = Bukkit.getWorld(initialWorld);
            final WorldCreator worldCreator = new WorldCreator(worldName);
            if (world != null) {
                worldCreator.copy(world);
                final World newWorld = worldCreator.createWorld();
                callback.accept(newWorld);
            }
        });
    }

    public static void fastCopy(String initialWorld, String newWorld, Consumer<World> callback) {
        final File worldDir = Bukkit.getWorldContainer();
        Bukkit.getScheduler().runTaskAsynchronously(Blueprint.getInst(), () -> {
            final File[] files = worldDir.listFiles();
            if(files == null) return;
            final File copyFile = (File) Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase(initialWorld)).toArray()[0];
            if(copyFile == null || !copyFile.isDirectory()) return;
            final File copyWorld = new File(worldDir, newWorld);
            if(!copyWorld.exists()) {
                copyWorld.mkdirs();
            }
            final File[] worldFiles = copyFile.listFiles();
            if(worldFiles == null) return;
            for(File file : worldFiles) {
                if(file.isDirectory()) {
                    try {
                        final File copyDir = new File(copyWorld, file.getName());
                        copyDir.mkdirs();
                        FileUtils.copyDirectory(file, copyDir, false);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }
                final String name = file.getName();
                if(name.equalsIgnoreCase("session.lock") || name.equalsIgnoreCase("uid.dat")) continue;
                try {
                    FileUtils.copyFileToDirectory(file, copyWorld, false);
                }catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            Bukkit.getScheduler().runTask(Blueprint.getInst(), () -> {
                final WorldCreator worldCreator = new WorldCreator(newWorld);
                worldCreator.generator(new EmptyWorld());
                callback.accept(Bukkit.createWorld(worldCreator));
            });
        });
    }

    public static void unloadWorld(World world) {
        if(spawnLocation == null) {
            spawnLocation = new Location(Bukkit.getWorld("world"), 0, 100, 0);
        }
        List<CompletableFuture<Boolean>> teleports = world.getPlayers().stream().map(player -> player.teleportAsync(spawnLocation)).toList();
        CompletableFuture.allOf(teleports.toArray(new CompletableFuture[0])).thenRun(() -> {
            Bukkit.getGlobalRegionScheduler().execute(Blueprint.getInst(), () -> {
                boolean unloaded = Bukkit.unloadWorld(world, false);
                if(unloaded) {
                    CompletableFuture.runAsync(() -> {
                        final File worldDir = Bukkit.getWorldContainer();
                        final File[] worlds = worldDir.listFiles();
                        if(worlds == null) return;
                        final Iterator<File> dirIterator = Arrays.stream(worlds).iterator();
                        while(dirIterator.hasNext()) {
                            final File file = dirIterator.next();
                            if(!file.getName().contains(world.getName())) continue;
                            deleteDir(file);
                            dirIterator.remove();
                            break;
                        }
                    });
                }
            });
        });

    }

    private static void deleteDir(File dir) {
        final File[] listFiles = dir.listFiles();
        if(listFiles != null) {
            for (File file : listFiles) {
                deleteDir(file);
            }
        }
        dir.delete();
    }

}
