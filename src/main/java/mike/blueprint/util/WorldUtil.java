package mike.blueprint.util;

import mike.blueprint.Blueprint;
import mike.blueprint.world.EmptyWorld;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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

        final String serverVersion = Bukkit.getBukkitVersion();
        final String version = serverVersion.split("-")[0].split("\\.")[0];
        final int versionNumber = Integer.parseInt(version);
        final boolean is26 = versionNumber >= 26;

        File dimensionDir = is26 ? new File(worldDir, "world/dimensions/minecraft") : worldDir;

        Bukkit.getScheduler().runTaskAsynchronously(Blueprint.getInst(), () -> {

            final File[] files = dimensionDir.listFiles();
            if(files == null) return;

            final File copyFile = (File) Arrays.stream(files).filter(file -> file.getName().equalsIgnoreCase(initialWorld)).toArray()[0];
            if(copyFile == null || !copyFile.isDirectory()) return;

            final File copyWorld = new File(dimensionDir, newWorld);

            if(is26) {
                final File paperDir = new File(copyWorld, "data/paper");
                paperDir.mkdirs();
            }

            copyWorld.mkdirs();

            final File[] worldFiles = copyFile.listFiles();

            if (worldFiles == null) return;

            for (File file : worldFiles) {

                if (is26) {
                    if (file.getName().equalsIgnoreCase("data")) {
                        if (file.listFiles()[1].getName().equalsIgnoreCase("paper")) continue;
                    }
                }

                if (file.isDirectory()) {
                    try {
                        FileUtils.copyDirectoryToDirectory(file, copyWorld);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }

                final String name = file.getName();

                if (!is26 && (name.equalsIgnoreCase("session.lock") || name.equalsIgnoreCase("uid.dat"))) continue;
                if (name.equalsIgnoreCase("paper-world.yml")) continue;

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

    private static void makeDirs(File sourceFile, File copyDir) {
        if(!sourceFile.isDirectory()) return;
        final File newDir = new File(copyDir, sourceFile.getName());
        newDir.mkdirs();
        copyDir = newDir;
        makeDirs(sourceFile, copyDir);
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
                        final File worldDir = new File(Bukkit.getWorldContainer(), "world/dimensions/minecraft");
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
