package me.pajic.tiered_chests.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor("atlasManager")
    AtlasManager getAtlasManager();
}
