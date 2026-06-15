package mike.blueprint.menu;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface MenuComponent {

    Consumer<Player> placeholderAction();

}
