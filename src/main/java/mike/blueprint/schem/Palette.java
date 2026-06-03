package mike.blueprint.schem;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Palette {

    private Map<Integer, List<short[]>> idToOffsets;

    public Palette() {
        this.idToOffsets = new HashMap<>();
    }

    public List<short[]> getOffsets(int id) {
        return idToOffsets.get(id);
    }

}
