package mike.blueprint.util;

import lombok.Getter;

@Getter
public class LootItem {

    private byte[] serializedStack;
    private String internalName, itemCommand;
    private double weight;
    private int amount, itemCost;
    private boolean isJackpot, isGuaranteed;

    public static class Builder {

        private final LootItem item = new LootItem();

        public Builder stack(byte[] stack) {
            item.serializedStack = stack;
            return this;
        }

        public Builder internalName(String name) {
            item.internalName = name;
            return this;
        }

        public Builder weight(double weight) {
            item.weight = weight;
            return this;
        }

        public Builder amount(int amount) {
            item.amount = amount;
            return this;
        }

        public Builder itemCommand(String cmd) {
            item.itemCommand = cmd;
            return this;
        }

        public Builder itemCost(int cost) {
            item.itemCost = cost;
            return this;
        }

        public Builder jackpot(boolean jackpot) {
            item.isJackpot = jackpot;
            return this;
        }

        public Builder guaranteed(boolean guaranteed) {
            item.isGuaranteed = guaranteed;
            return this;
        }

        public LootItem build() {
            return item;
        }
    }

}
