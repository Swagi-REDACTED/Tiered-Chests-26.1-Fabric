package me.pajic.tiered_chests.mixin.client;

import com.mojang.blaze3d.platform.NativeImage;
import me.pajic.tiered_chests.TieredChests;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(SpriteLoader.class)
public class SpriteLoaderMixin {

    @ModifyVariable(method = "stitch", at = @At("HEAD"), argsOnly = true)
    private List<SpriteContents> tiered_chests$injectWashedSprites(List<SpriteContents> sprites) {
        List<SpriteContents> newSprites = new ArrayList<>(sprites);
        List<SpriteContents> washedVariants = new ArrayList<>();

        for (SpriteContents sprite : sprites) {
            if (sprite.name().equals(Identifier.parse("minecraft:block/oak_planks"))) {
                washedVariants.add(tiered_chests$createWashedVariant(sprite));
            }
        }

        newSprites.addAll(washedVariants);
        return newSprites;
    }

    private SpriteContents tiered_chests$createWashedVariant(SpriteContents original) {
        Identifier washedName = original.name().withSuffix("_washed");
        
        // Use accessor to get the private image
        NativeImage originalImage = ((me.pajic.tiered_chests.mixin.client.SpriteContentsAccessor)original).getOriginalImage();
        NativeImage washedImage = originalImage.mappedCopy(color -> {
            int a = ARGB.alpha(color);
            int r = ARGB.red(color);
            int g = ARGB.green(color);
            int b = ARGB.blue(color);
            
            // Standard Luminance Formula
            int gray = (int) (r * 0.299 + g * 0.587 + b * 0.114);
            return ARGB.color(a, gray, gray, gray);
        });

        TieredChests.LOGGER.info("Generated washed sprite variant: {}", washedName);

        return new SpriteContents(
                washedName,
                new FrameSize(original.width(), original.height()),
                washedImage,
                Optional.empty(), // We don't support animated washed sprites yet for simplicity
                List.of(),
                Optional.empty()
        );
    }
}
