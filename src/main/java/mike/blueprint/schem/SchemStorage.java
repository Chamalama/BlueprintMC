package mike.blueprint.schem;

import lombok.Getter;
import mike.blueprint.Blueprint;
import mike.blueprint.config.Config;
import mike.blueprint.loader.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class SchemStorage extends Config {

    private List<String> VALID_SCHEMS = new ArrayList<>();

    public SchemStorage() {
        super(Blueprint.getInst(), "schems", "schem-names");
    }

    public void addSchem(String name) {
        VALID_SCHEMS.add(name);
        this.update();
    }

    public void removeSchem(String name) {
        VALID_SCHEMS.remove(name);
        this.update();
    }

}
