package me.pajic.tiered_chests.mixin;

import me.pajic.tiered_chests.recipe.ShapedRecipeAccessor;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.NormalCraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NormalCraftingRecipe.class)
public abstract class NormalCraftingRecipeMixin implements ShapedRecipeAccessor {
    @Shadow @Final protected Recipe.CommonInfo commonInfo;
    @Shadow @Final protected CraftingRecipe.CraftingBookInfo bookInfo;

    @Override
    public Recipe.CommonInfo getCommonInfo() {
        return commonInfo;
    }

    @Override
    public CraftingRecipe.CraftingBookInfo getBookInfo() {
        return bookInfo;
    }
}
