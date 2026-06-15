package mike.blueprint.menu;

import com.google.common.collect.Lists;

import java.util.List;

public abstract class BaseMenu {

    private String title;

    private final List<MenuComponent> menuComponents = Lists.newArrayList();

    public BaseMenu(String title) {
        this.title = title;
    }

}
