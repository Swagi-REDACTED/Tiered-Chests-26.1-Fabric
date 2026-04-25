package me.pajic.tiered_chests.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;

public class UpgradeRecipe extends ShapedRecipe {
    public static final MapCodec<UpgradeRecipe> MAP_CODEC = ShapedRecipe.MAP_CODEC.xmap(
        r -> new UpgradeRecipe(((ShapedRecipeAccessor)r).getCommonInfo(), ((ShapedRecipeAccessor)r).getBookInfo(), ((ShapedRecipeAccessor)r).getPattern(), ((ShapedRecipeAccessor)r).getResult()),
        r -> r
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeRecipe> STREAM_CODEC = ShapedRecipe.STREAM_CODEC.map(
        r -> new UpgradeRecipe(((ShapedRecipeAccessor)r).getCommonInfo(), ((ShapedRecipeAccessor)r).getBookInfo(), ((ShapedRecipeAccessor)r).getPattern(), ((ShapedRecipeAccessor)r).getResult()),
        r -> r
    );

    public UpgradeRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ShapedRecipePattern pattern, ItemStackTemplate result) {
        super(commonInfo, bookInfo, pattern, result);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack result = super.assemble(input);
        
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                // Only copy container contents if we are not duplicating the item
                if (result.getCount() == 1 && stack.has(DataComponents.CONTAINER)) {
                    result.set(DataComponents.CONTAINER, stack.get(DataComponents.CONTAINER));
                }
                if (stack.has(DataComponents.CUSTOM_NAME)) {
                    result.set(DataComponents.CUSTOM_NAME, stack.get(DataComponents.CUSTOM_NAME));
                }
            }
        }
        
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public RecipeSerializer<ShapedRecipe> getSerializer() {
        return (RecipeSerializer) ModRecipes.UPGRADE_SERIALIZER;
    }
}
