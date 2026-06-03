package mike.blueprint.gui;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GUI {

    private String title = "Title";
    private int pageSize = 54;
    private Map<Integer, List<GUIItem>> pageItems = new HashMap<>(){{
        put(0, new ArrayList<>(){{
            add(new GUIItem());
        }});
    }};

}
