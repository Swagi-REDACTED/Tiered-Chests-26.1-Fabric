package me.pajic.tiered_chests.config;

import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.pajic.tiered_chests.TieredChests;

@Version(version = 1)
public class ModConfig extends me.fzzyhmstrs.fzzy_config.config.Config {

    public ModConfig() {
        super(TieredChests.id("main"));
    }

    public static class TierConfig extends ConfigSection {
        public ValidatedInt extraRows = new ValidatedInt(0, 64, -64);
        public ValidatedInt extraCols = new ValidatedInt(0, 64, -64);

        public TierConfig() {}
        public TierConfig(int rows, int cols) {
            this.extraRows = new ValidatedInt(rows, 64, -64);
            this.extraCols = new ValidatedInt(cols, 64, -64);
        }
    }

    public static class ChestConfigs extends ConfigSection {
        public TierConfig wood = new TierConfig(0, 0);
        public TierConfig copper = new TierConfig(0, 0);
        public TierConfig iron = new TierConfig(0, 0);
        public TierConfig golden = new TierConfig(0, 0);
        public TierConfig diamond = new TierConfig(0, 0);
        public TierConfig netherite = new TierConfig(0, 0);
    }

    public ChestConfigs chestConfigs = new ChestConfigs();
    public ChestConfigs barrelConfigs = new ChestConfigs();
    
    public ValidatedBoolean fancyChests = new ValidatedBoolean(true);
    public ValidatedBoolean fancyBarrels = new ValidatedBoolean(true);
    public ValidatedBoolean fancyLocks = new ValidatedBoolean(true);
    public ValidatedBoolean matchGuiColors = new ValidatedBoolean(false);
    public ValidatedBoolean texturePackOverride = new ValidatedBoolean(false);

    // Removed manual instance logic, now handled in TieredChests.java

    // Fzzy Config permission handling: Always require OP level 2 for server settings
    @Override
    public int defaultPermLevel() {
        return 2;
    }

    public int getRows(String tier, boolean isBarrel, int base) {
        TierConfig cfg = getTierConfig(tier, isBarrel);
        int extra = cfg != null ? cfg.extraRows.get() : 0;
        return Math.max(3, Math.min(base + 64, base + extra));
    }

    public int getCols(String tier, boolean isBarrel, int base) {
        TierConfig cfg = getTierConfig(tier, isBarrel);
        int extra = cfg != null ? cfg.extraCols.get() : 0;
        return Math.max(9, Math.min(base + 64, base + extra));
    }
    
    private TierConfig getTierConfig(String tier, boolean isBarrel) {
        ChestConfigs group = isBarrel ? barrelConfigs : chestConfigs;
        return switch (tier) {
            case "wood" -> group.wood;
            case "copper" -> group.copper;
            case "iron" -> group.iron;
            case "golden" -> group.golden;
            case "diamond" -> group.diamond;
            case "netherite" -> group.netherite;
            default -> null;
        };
    }
}
