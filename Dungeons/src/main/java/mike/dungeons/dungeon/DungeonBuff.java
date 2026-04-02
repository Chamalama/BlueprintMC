package mike.dungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import mike.blueprint.util.Text;
import net.kyori.adventure.bossbar.BossBar;

@Getter
@Setter
public class DungeonBuff {

    private long buffTimer;
    private long maxBuffTime;
    private String buff, id;
    private BossBar displayBar;

    public DungeonBuff(long buffTimer, String id, String buff) {
        this.buffTimer = buffTimer;
        this.maxBuffTime = buffTimer;
        this.id = id;
        this.buff = buff;
        this.displayBar = BossBar.bossBar(Text.translate(buff), 1.0f, BossBar.Color.BLUE, BossBar.Overlay.NOTCHED_6);
    }

    public void decrementTime() {
        --this.buffTimer;
    }

    public void incrementTime() {
        ++this.buffTimer;
    }

    public void updateBar() {
        float currProgress = (float) buffTimer / maxBuffTime;
        this.displayBar.progress(currProgress);
    }

}
