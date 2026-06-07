package me.pajic.tiered_chests.config;

import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;

@Version(version = 1)
public class ClientConfig extends me.fzzyhmstrs.fzzy_config.config.Config {

    public ClientConfig() {
        super(me.pajic.tiered_chests.TieredChests.id("client"));
    }

    public ValidatedBoolean fancyChests = new ValidatedBoolean(true);
    public ValidatedBoolean fancyBarrels = new ValidatedBoolean(true);
    public ValidatedBoolean fancyLocks = new ValidatedBoolean(true);
    public ValidatedBoolean fancyCorners = new ValidatedBoolean(true);
    public ValidatedBoolean matchGuiColors = new ValidatedBoolean(false);
    public ValidatedBoolean texturePackOverride = new ValidatedBoolean(false);
    public ValidatedBoolean autoGuiRescaling = new ValidatedBoolean(true);

    @Override
    public int defaultPermLevel() {
        return 0;
    }
}
