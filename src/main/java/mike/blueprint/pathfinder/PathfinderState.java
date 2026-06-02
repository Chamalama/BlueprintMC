package mike.blueprint.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;

import java.util.LinkedList;

@Getter
public class PathfinderState {

    private final LinkedList<Node> path = new LinkedList<>();
    private final Long2ObjectOpenHashMap<Node> storedNodes = new Long2ObjectOpenHashMap<>();

    public PathfinderState() {

    }

    public Node getNode(long key) {
        return storedNodes.get(key);
    }

}
