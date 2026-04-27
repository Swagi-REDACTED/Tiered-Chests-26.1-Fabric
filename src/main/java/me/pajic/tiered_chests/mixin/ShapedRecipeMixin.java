package me.pajic.tiered_chests.mixin;

import me.pajic.tiered_chests.recipe.ShapedRecipeAccessor;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShapedRecipe.class)
public abstract class ShapedRecipeMixin implements ShapedRecipeAccessor {
    @Shadow @Final private ShapedRecipePattern pattern;
    @Shadow @Final private ItemStackTemplate result;

    @Override
    public ShapedRecipePattern getPattern() {
        return pattern;
    }

    @Override
    public ItemStackTemplate getResult() {
        return result;
    }
}
