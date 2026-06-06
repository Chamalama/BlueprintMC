package mike.blueprint.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.*;

public class PathfinderContext {

    private final Long2ObjectOpenHashMap<ChunkSnapshot> cachedSnapshots = new Long2ObjectOpenHashMap<>();
    private final PriorityQueue<Node> openNodes = new PriorityQueue<>(Comparator.comparing(Node::f));
    private final Set<Node> visitedNodes = new HashSet<>();

    private final PathfinderState pathfinderState;

    public PathfinderContext(PathfinderState pathfinderState) {
        this.pathfinderState = pathfinderState;
    }

    public PathfinderState buildPath(Location start, Location end) {
        if (start == null || end == null) return null;
        start = start.clone();
        end = end.clone();
        final World world = start.getWorld();

        if (!end.getBlock().isPassable()) {
            end.setY(world.getHighestBlockYAt(end.getBlockX(), end.getBlockZ()));
        }

        final Node startNode = new Node(start.getBlockX(), start.getBlockY(), start.getBlockZ());
        startNode.setH(startNode.getDistance(end));
        openNodes.add(startNode);

        int tries = 0;

        while (!openNodes.isEmpty() && ++tries < 150) {

            Node current = openNodes.poll();

            visitedNodes.add(current);

            if (current.getDistance(end) <= 1.5) {
                while (current != null) {
                    pathfinderState.getPath().addFirst(current);
                    current = current.getParent();
                }
                break;
            }

            final double nextGCost = current.getG() + current.getDistance(end);

            for (Node node : getNeighbors(current)) {
                final ChunkSnapshot snapshot = getNodeSnapshot(node, world);
                if (!canWalkOnNode(node, snapshot)) continue;
                if (visitedNodes.contains(node)) continue;
                if (!canAccessFromCurrentNode(current, node, snapshot)) continue;
                node.setG(nextGCost);
                node.setH(node.getDistance(end));
                node.setParent(current);
                openNodes.remove(node);
                openNodes.add(node);
            }

        }

        return pathfinderState;

    }

    public boolean canWalkOnNode(Node node, ChunkSnapshot chunkSnapshot) {
        final int nodeX = node.getX();
        final int nodeY = node.getY();
        final int nodeZ = node.getZ();

        final int chunkBlockX = nodeX & 15;
        final int chunkBlockZ = nodeZ & 15;

        if (nodeY < -64 || nodeY > 319) return false;

        final Material block = chunkSnapshot.getBlockType(chunkBlockX, nodeY - 1, chunkBlockZ);
        final Material feetBlock = chunkSnapshot.getBlockType(chunkBlockX, nodeY, chunkBlockZ);
        final Material headBlock = chunkSnapshot.getBlockType(chunkBlockX, nodeY + 1, chunkBlockZ);
        //final Material aboveHeadBlock = chunkSnapshot.getBlockType(chunkBlockX, nodeY + 2, chunkBlockZ);

        return block.isSolid() && !feetBlock.isCollidable() && !headBlock.isCollidable();
    }

    public boolean canAccessFromCurrentNode(Node current, Node next, ChunkSnapshot snapshot) {
        final int currX = current.getX();
        final int currY = current.getY();
        final int currZ = current.getZ();

        final int nextX = next.getX();
        final int nextY = next.getY();
        final int nextZ = next.getZ();

        final int currChunkBlockX = currX & 15;
        final int currChunkBlockZ = currZ & 15;

        final int nextChunkBlockX = nextX & 15;
        final int nextChunkBlockZ = nextZ & 15;

        boolean higherY = nextY > currY;

        if (currY == nextY) return true;

        if (higherY) {
            final Material headMaterial = snapshot.getBlockType(currChunkBlockX, currY + 2, currChunkBlockZ);
            if (headMaterial.isCollidable()) return false;
        }

        final Material currentHead = snapshot.getBlockType(nextChunkBlockX, nextY + 2, nextChunkBlockZ);
        return !currentHead.isCollidable();
    }

    public ChunkSnapshot getNodeSnapshot(Node node, World world) {
        final int chunkX = node.getX() >> 4;
        final int chunkZ = node.getZ() >> 4;
        final long chunkKey = (long) chunkX << 32 | chunkZ & 0xffffffffL;
        if (cachedSnapshots.containsKey(chunkKey)) {
            return cachedSnapshots.get(chunkKey);
        }
        final ChunkSnapshot snapshot = world.getChunkAt(chunkX, chunkZ).getChunkSnapshot();
        cachedSnapshots.put(chunkKey, snapshot);
        return snapshot;
    }

    public static Location convertNodeToBukkit(Node node, World world) {
        return new Location(world, node.getX(), node.getY(), node.getZ());
    }

    private final List<int[]> directions = List.of(
            new int[]{1, 0, 0},
            new int[]{-1, 0, 0},
            new int[]{0, 0, 1},
            new int[]{0, 0, -1},
            new int[]{0, 1, 0},
            new int[]{0, -1, 0},
            new int[]{1, 1, 0},
            new int[]{-1, 1, 0},
            new int[]{1, -1, 0},
            new int[]{-1, -1, 0},
            new int[]{0, 1, 1},
            new int[]{0, 1, -1},
            new int[]{0, -1, 1},
            new int[]{0, -1, -1}
    );

    private List<Node> getNeighbors(Node node) {
        final List<Node> neighbors = new ArrayList<>();
        for (int[] dir : directions) {
            int x = node.getX() + dir[0];
            int y = node.getY() + dir[1];
            int z = node.getZ() + dir[2];
            long key = (((long) x & 0x3FFFFFF) << 38)
                    | (((long) z & 0x3FFFFFF) << 12)
                    | (y & 0xFFF);
            Node storedNode = pathfinderState.getNode(key);
            if (storedNode == null) {
                storedNode = new Node(x, y, z);
                pathfinderState.getStoredNodes().putIfAbsent(key, storedNode);
            }
            neighbors.add(storedNode);
        }
        return neighbors;
    }

}