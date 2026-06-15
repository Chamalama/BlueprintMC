package mike.blueprint.util;

import java.util.List;
import java.util.Random;

public class LootUtil {

    public static LootItem selectItem(List<LootItem> items) {
        if (items.isEmpty()) {
            return null;
        } else {

            double weight = 0.0F;

            for(LootItem lootItem : items) {
                weight += lootItem.getWeight();
            }

            if (weight == (double)0.0F) {
                return null;
            } else {
                double rand = (new Random()).nextDouble() * weight;

                for(LootItem lootItem : items) {
                    rand -= lootItem.getWeight();
                    if (rand <= (double)0.0F) {
                        return lootItem;
                    }
                }

                return items.getLast();

            }
        }
    }

}
