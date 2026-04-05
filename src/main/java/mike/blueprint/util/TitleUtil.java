package mike.blueprint.util;

import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class TitleUtil {

    public static void sendTitle(Player player, String text, String subtext, int fadeIn, int stay, int fadeOut) {
        player.sendTitlePart(TitlePart.TITLE, Text.translate(text));
        player.sendTitlePart(TitlePart.SUBTITLE, Text.translate(subtext));
        player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.of(fadeIn, ChronoUnit.SECONDS), Duration.of(stay, ChronoUnit.SECONDS), Duration.of(fadeOut, ChronoUnit.SECONDS)));
    }

    public static void sendMultiMessageTitle(Player player) {

    }

}
