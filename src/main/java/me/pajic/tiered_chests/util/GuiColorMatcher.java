package me.pajic.tiered_chests.util;

import com.mojang.blaze3d.platform.NativeImage;
import me.pajic.tiered_chests.TieredChests;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class GuiColorMatcher {
    private static Identifier matchedTextureId = null;
    private static DynamicTexture dynamicTexture = null;


    public static Identifier getMatchedTexture() {
        if (!TieredChests.CONFIG.matchGuiColors.get()) {
            return TieredChests.id("textures/gui/generic_54_blank.png");
        }

        if (matchedTextureId != null)
            return matchedTextureId;

        Minecraft mc = Minecraft.getInstance();
        ResourceManager rm = mc.getResourceManager();
        Identifier blankId = TieredChests.id("textures/gui/generic_54_blank.png");
        Identifier targetId = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");

        try {
            Optional<Resource> blankRes = rm.getResource(blankId);
            Optional<Resource> targetRes = rm.getResource(targetId);

            if (blankRes.isPresent() && targetRes.isPresent()) {
                try (InputStream blankStream = blankRes.get().open();
                        InputStream targetStream = targetRes.get().open()) {

                    NativeImage blankImg = NativeImage.read(blankStream);
                    NativeImage targetImg = NativeImage.read(targetStream);

                    int width = blankImg.getWidth();
                    int height = blankImg.getHeight();
                    NativeImage resultImg = new NativeImage(width, height, true);

                    // Sample base color at (5, 5)
                    int targetBaseColor = targetImg.getPixel(5, 5);

                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            int pixelBlank = blankImg.getPixel(x, y);
                            
                            // 1. Preserve transparency outside the GUI
                            if (net.minecraft.util.ARGB.alpha(pixelBlank) < 10) {
                                resultImg.setPixel(x, y, 0);
                                continue;
                            }

                            // 2. Identify if the blank pixel is part of the interior Base Background
                            // Use the mod's default gray (C6C6C6) as the reference for background in the template
                            if (colorDistance(pixelBlank, 0xFFC6C6C6) < 30) {
                                // It is the interior Base -> Fill it with the Target Base color to erase slots from the pack
                                resultImg.setPixel(x, y, targetBaseColor);
                            } else {
                                // 3. It is a structural pixel (Border, Highlight, Shadow) -> DIRECT SMART CLONE from target
                                if (x < targetImg.getWidth() && y < targetImg.getHeight()) {
                                    resultImg.setPixel(x, y, targetImg.getPixel(x, y));
                                } else {
                                    resultImg.setPixel(x, y, pixelBlank);
                                }
                            }
                        }
                    }

                    dynamicTexture = new DynamicTexture(() -> "matched_gui", resultImg);
                    matchedTextureId = Identifier.fromNamespaceAndPath(TieredChests.MOD_ID, "matched_gui");
                    mc.getTextureManager().register(matchedTextureId, dynamicTexture);

                    blankImg.close();
                    targetImg.close();

                    return matchedTextureId;
                }
            }
        } catch (IOException e) {
            TieredChests.LOGGER.error("Failed to generate matched GUI texture", e);
        }

        return blankId;
    }

    public static boolean isMatchingEnabled() {
        return TieredChests.CONFIG.matchGuiColors.get() && matchedTextureId != null;
    }

    private static double colorDistance(int c1, int c2) {
        int r1 = net.minecraft.util.ARGB.red(c1);
        int g1 = net.minecraft.util.ARGB.green(c1);
        int b1 = net.minecraft.util.ARGB.blue(c1);
        int r2 = net.minecraft.util.ARGB.red(c2);
        int g2 = net.minecraft.util.ARGB.green(c2);
        int b2 = net.minecraft.util.ARGB.blue(c2);
        return Math.sqrt(Math.pow(r1 - r2, 2) + Math.pow(g1 - g2, 2) + Math.pow(b1 - b2, 2));
    }

    public static void reset() {
        if (dynamicTexture != null) {
            dynamicTexture.close();
            dynamicTexture = null;
        }
        matchedTextureId = null;
    }
}
