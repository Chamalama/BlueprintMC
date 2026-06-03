package mike.blueprint.schem;

import mike.blueprint.Blueprint;
import mike.blueprint.loader.Component;
import mike.blueprint.util.FastLocation;
import mike.blueprint.util.Region;
import mike.blueprint.util.Text;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Component
public class SchemService {

    private final SchemStorage schemStorage;

    public static final File SCHEM_DIRECTORY = new File(Blueprint.getInst().getDataFolder(), "schem-folder");

    public static final Map<UUID, FastLocation[]> EDIT_MAP = new HashMap<>();

    private final Map<UUID, BukkitTask> activePastingTasks = new HashMap<>();

    public static final Map<Material, Integer> ID_TYPES = new HashMap<>(){{
        final Material[] vals = Material.values();
        for(int i = 0; i < vals.length; ++i) {
            put(vals[i], i);
        }
    }};

    public static final Map<Integer, Material> BLOCK_TYPES = new HashMap<>(){{
        for(Map.Entry<Material, Integer> entries : ID_TYPES.entrySet()) {
            put(entries.getValue(), entries.getKey());
        }
    }};

    public SchemService(SchemStorage schemStorage) {
        this.schemStorage = schemStorage;
    }

    public static Material getTypeFromID(int id) {
        return BLOCK_TYPES.get(id);
    }

    public static int getIDFromType(Material type) {
        return ID_TYPES.get(type);
    }

    public void writeSchemFile(String fileName, Palette palette) {
        Bukkit.getScheduler().runTaskAsynchronously(Blueprint.getInst(), () -> {
            final File mSchem = new File(SCHEM_DIRECTORY, fileName + ".mschem");
            mSchem.getParentFile().mkdirs();
            try(FileOutputStream fileOutputStream = new FileOutputStream(mSchem);
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream)) {
                byte[] data = serialize(palette);
                gzipOutputStream.write(data);
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void addPoint(Player player, Location location, int point) {
        final FastLocation[] points = EDIT_MAP.getOrDefault(player.getUniqueId(), new FastLocation[2]);
        int pointIndex = point == 1 ? 0 : 1;
        points[pointIndex] = new FastLocation(location);
        EDIT_MAP.put(player.getUniqueId(), points);
        player.sendMessage(Text.translate("<yellow><b>(!)</b> Point " + point + " set to " + points[pointIndex].serialize()));
    }

    public void deleteSchemFile(String fileName) {
        Bukkit.getScheduler().runTaskAsynchronously(Blueprint.getInst(), () -> {
            final File file = new File(SCHEM_DIRECTORY, fileName + ".mschem");
            file.delete();
        });
    }

    public FastLocation[] getEditPoints(Player sender) {
        return EDIT_MAP.get(sender.getUniqueId());
    }

    public Set<Block> getBlocksInPoints(FastLocation[] points) {
        final World world = points[0].toBukkit().getWorld();
        final Region region = new Region(points[0].toBukkit(), points[1].toBukkit());
        return region.getRegionBlocks(world);
    }

    public byte[] serialize(Palette palette) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(baos);
        final Map<Integer, List<short[]>> paletteMap = palette.getIdToOffsets();
        dataOutputStream.writeInt(palette.getIdToOffsets().size());

        for(Map.Entry<Integer, List<short[]>> entry : paletteMap.entrySet()) {
            final int blockID = entry.getKey();
            final List<short[]> offsets = entry.getValue();
            dataOutputStream.writeInt(blockID);
            dataOutputStream.writeInt(offsets.size());
            for(short[] offset : offsets) {
                dataOutputStream.writeShort(offset[0]);
                dataOutputStream.writeShort(offset[1]);
                dataOutputStream.writeShort(offset[2]);
            }
        }
        return baos.toByteArray();
    }

    public Palette deserialize(File file) {
        try(GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(file));
            DataInputStream dataInputStream = new DataInputStream(gzip)) {

            final Palette palette = new Palette();
            final int blockTypeCount = dataInputStream.readInt();

            for(int i = 0; i < blockTypeCount; i++) {
                int blockID = dataInputStream.readInt();
                int offsetCount = dataInputStream.readInt();
                List<short[]> offsets = new ArrayList<>(offsetCount);

                for(int k = 0; k < offsetCount; k++) {
                    offsets.add(new short[]{
                        dataInputStream.readShort(),
                        dataInputStream.readShort(),
                        dataInputStream.readShort()
                    });
                }
                palette.getIdToOffsets().put(blockID, offsets);
            }

            return palette;

        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void pasteSchem(Location center, String file) {
        Bukkit.getScheduler().runTaskAsynchronously(Blueprint.getInst(), () -> {
            final File schemFile = new File(SCHEM_DIRECTORY, file + ".mschem");
            if (!schemFile.exists()) return;
            final Palette palette = deserialize(schemFile);

            final int centerX = center.getBlockX();
            final int centerY = center.getBlockY();
            final int centerZ = center.getBlockZ();
            final World world = center.getWorld();

            for (Map.Entry<Integer, List<short[]>> entry : palette.getIdToOffsets().entrySet()) {
                final int id = entry.getKey();
                final List<short[]> offsets = entry.getValue();
                final Material material = getTypeFromID(id);
                for (short[] offset : offsets) {
                    final int pasteX = centerX + offset[0];
                    final int pasteY = centerY + offset[1];
                    final int pasteZ = centerZ + offset[2];
                    world.getBlockAt(pasteX, pasteY, pasteZ).setType(material, false);
                }
            }
        });
    }

    public void pasteSchemDelayed(UUID pasteID, Location center, String file, long delay, long period) {
        Bukkit.getScheduler().runTaskAsynchronously(Blueprint.getInst(), () -> {

            final File schemFile = new File(SCHEM_DIRECTORY, file + ".mschem");
            if (!schemFile.exists()) return;
            final Palette palette = deserialize(schemFile);

            final int centerX = center.getBlockX();
            final int centerY = center.getBlockY();
            final int centerZ = center.getBlockZ();
            final World world = center.getWorld();

            final Queue<Map.Entry<Integer, List<short[]>>> pasteQueue = new LinkedList<>(palette.getIdToOffsets().entrySet());

            final BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Blueprint.getInst(), () -> {
                Map.Entry<Integer, List<short[]>> toPaste = pasteQueue.poll();
                if (toPaste == null) {
                    activePastingTasks.remove(pasteID).cancel();
                    return;
                }
                final int id = toPaste.getKey();
                final List<short[]> offsets = toPaste.getValue();
                final Material material = getTypeFromID(id);
                for (short[] offset : offsets) {
                    final int pasteX = centerX + offset[0];
                    final int pasteY = centerY + offset[1];
                    final int pasteZ = centerZ + offset[2];
                    world.getBlockAt(pasteX, pasteY, pasteZ).setType(material, false);
                }
            }, delay, period);

            activePastingTasks.put(pasteID, task);

        });
    }

    public void pasteSchemDelayed(UUID pasteID, Location center, String file, long delay, long period, Consumer<Location> locationConsumer) {
        Bukkit.getScheduler().runTaskAsynchronously(Blueprint.getInst(), () -> {

            final File schemFile = new File(SCHEM_DIRECTORY, file + ".mschem");
            if (!schemFile.exists()) return;
            final Palette palette = deserialize(schemFile);

            final int centerX = center.getBlockX();
            final int centerY = center.getBlockY();
            final int centerZ = center.getBlockZ();
            final World world = center.getWorld();

            final Queue<Map.Entry<Integer, List<short[]>>> pasteQueue = new LinkedList<>(palette.getIdToOffsets().entrySet());

            final BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(Blueprint.getInst(), () -> {
                Map.Entry<Integer, List<short[]>> toPaste = pasteQueue.poll();
                if (toPaste == null) {
                    activePastingTasks.remove(pasteID).cancel();
                    return;
                }
                final int id = toPaste.getKey();
                final List<short[]> offsets = toPaste.getValue();
                final Material material = getTypeFromID(id);
                for (short[] offset : offsets) {
                    final int pasteX = centerX + offset[0];
                    final int pasteY = centerY + offset[1];
                    final int pasteZ = centerZ + offset[2];
                    final Block block = world.getBlockAt(pasteX, pasteY, pasteZ);
                    block.setType(material, false);
                    locationConsumer.accept(block.getLocation());
                }
            }, delay, period);

            activePastingTasks.put(pasteID, task);

        });
    }


}
