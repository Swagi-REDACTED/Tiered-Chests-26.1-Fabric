package me.pajic.tiered_chests.util;

import me.pajic.tiered_chests.TieredChests;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class NemosCompatibilityHelper {

    public static void initButtons(AbstractContainerScreen<?> screen, int leftPos, int topPos, int inventoryLabelY, int imageWidth) {
        if (!FabricLoader.getInstance().isModLoaded("nemos_inventory_sorting")) return;

        try {
            // Load necessary classes
            Class<?> configServiceClass = Class.forName("com.nemonotfound.nemos.inventory.sorting.config.service.ConfigService");
            Class<?> sortButtonFactoryClass = Class.forName("com.nemonotfound.nemos.inventory.sorting.factory.SortButtonFactory");
            Class<?> moveSameButtonFactoryClass = Class.forName("com.nemonotfound.nemos.inventory.sorting.factory.MoveSameButtonFactory");
            Class<?> moveAllButtonFactoryClass = Class.forName("com.nemonotfound.nemos.inventory.sorting.factory.MoveAllButtonFactory");
            Class<?> dropAllButtonFactoryClass = Class.forName("com.nemonotfound.nemos.inventory.sorting.factory.DropAllButtonFactory");
            Class<?> offsetClass = Class.forName("com.nemonotfound.nemos.inventory.sorting.model.Offset");
            Class<?> sizeClass = Class.forName("com.nemonotfound.nemos.inventory.sorting.model.Size");
            Class<?> positionClass = Class.forName("com.nemonotfound.nemos.inventory.sorting.model.Position");
            Class<?> slotRangeClass = Class.forName("com.nemonotfound.nemos.inventory.sorting.model.SlotRange");
            Class<?> buttonCreatorClass = Class.forName("com.nemonotfound.nemos.inventory.sorting.factory.ButtonCreator");

            // Get instances and config
            Object configService = configServiceClass.getMethod("getInstance").invoke(null);
            List<?> configs = (List<?>) configServiceClass.getMethod("readOrGetDefaultComponentConfigs").invoke(configService);

            // Button factories
            Object sortFactory = sortButtonFactoryClass.getMethod("getInstance").invoke(null);
            Object moveSameFactory = moveSameButtonFactoryClass.getMethod("getInstance").invoke(null);
            Object moveAllFactory = moveAllButtonFactoryClass.getMethod("getInstance").invoke(null);
            Object dropAllFactory = dropAllButtonFactoryClass.getMethod("getInstance").invoke(null);

            // Container size calculation (Nemo's logic)
            int totalSlots = screen.getMenu().slots.size();
            int inventoryEndIndex = totalSlots - 9;
            int containerSize = inventoryEndIndex - 27;

            // Define button setups
            createNemoButton(screen, configs, configService, "sort_storage_container", sortFactory, 5, false, containerSize, inventoryEndIndex, leftPos, topPos, imageWidth);
            createNemoButton(screen, configs, configService, "move_same_storage_container", moveSameFactory, 5, false, containerSize, inventoryEndIndex, leftPos, topPos, imageWidth);
            createNemoButton(screen, configs, configService, "move_all_storage_container", moveAllFactory, 5, false, containerSize, inventoryEndIndex, leftPos, topPos, imageWidth);
            createNemoButton(screen, configs, configService, "drop_all_storage_container", dropAllFactory, 5, false, containerSize, inventoryEndIndex, leftPos, topPos, imageWidth);

            int inventoryY = inventoryLabelY - 2;
            createNemoButton(screen, configs, configService, "sort_storage_container_inventory", sortFactory, inventoryY, true, containerSize, inventoryEndIndex, leftPos, topPos, imageWidth);
            createNemoButton(screen, configs, configService, "move_same_storage_container_inventory", moveSameFactory, inventoryY, true, containerSize, inventoryEndIndex, leftPos, topPos, imageWidth);
            createNemoButton(screen, configs, configService, "move_all_storage_container_inventory", moveAllFactory, inventoryY, true, containerSize, inventoryEndIndex, leftPos, topPos, imageWidth);
            createNemoButton(screen, configs, configService, "drop_all_storage_container_inventory", dropAllFactory, inventoryY, true, containerSize, inventoryEndIndex, leftPos, topPos, imageWidth);

        } catch (Exception e) {
            TieredChests.LOGGER.error("Failed to integrate with Nemo's Inventory Sorting", e);
        }
    }

    private static void createNemoButton(AbstractContainerScreen<?> screen, List<?> configs, Object configService, String componentName, Object factory, int defaultY, boolean isInventory, int containerSize, int inventoryEndIndex, int leftPos, int topPos, int imageWidth) throws Exception {
        Method getCfgMethod = configService.getClass().getMethod("getOrDefaultComponentConfig", List.class, String.class);
        Object optionalCfg = getCfgMethod.invoke(configService, configs, componentName);
        
        Class<?> optionalClass = Class.forName("java.util.Optional");
        if (!(boolean) optionalClass.getMethod("isPresent").invoke(optionalCfg)) return;
        
        Object config = optionalClass.getMethod("get").invoke(optionalCfg);
        if (!(boolean) config.getClass().getMethod("isEnabled").invoke(config)) return;

        Integer yOffset = (Integer) config.getClass().getMethod("yOffset").invoke(config);
        if (yOffset == null) yOffset = defaultY;
        
        Integer xOffset = (Integer) config.getClass().getMethod("xOffset").invoke(config);
        if (xOffset == null) {
            int rightX = (int) config.getClass().getMethod("rightXOffset").invoke(config);
            xOffset = imageWidth + rightX;
        }

        int width = (int) config.getClass().getMethod("width").invoke(config);
        int height = (int) config.getClass().getMethod("height").invoke(config);

        // Create SlotRange
        int start = isInventory ? containerSize : 0;
        int end = isInventory ? inventoryEndIndex : containerSize;
        Constructor<?> slotRangeCtor = Class.forName("com.nemonotfound.nemos.inventory.sorting.model.SlotRange").getConstructor(int.class, int.class);
        Object slotRange = slotRangeCtor.newInstance(start, end);

        // Create Position
        Constructor<?> positionCtor = Class.forName("com.nemonotfound.nemos.inventory.sorting.model.Position").getConstructor(int.class, int.class);
        Object position = positionCtor.newInstance(leftPos, topPos);

        // Create Offset
        Constructor<?> offsetCtor = Class.forName("com.nemonotfound.nemos.inventory.sorting.model.Offset").getConstructor(int.class, int.class);
        Object offset = offsetCtor.newInstance(xOffset, yOffset);

        // Create Size
        Constructor<?> sizeCtor = Class.forName("com.nemonotfound.nemos.inventory.sorting.model.Size").getConstructor(int.class, int.class, int.class);
        Object size = sizeCtor.newInstance(width, height, 11); // 11 is Nemo's default button size

        // Create Button
        Method createBtnMethod = factory.getClass().getMethod("createButton", slotRange.getClass(), position.getClass(), offset.getClass(), size.getClass(), net.minecraft.world.inventory.AbstractContainerMenu.class);
        AbstractWidget button = (AbstractWidget) createBtnMethod.invoke(factory, slotRange, position, offset, size, screen.getMenu());
        
        // Add to screen (via reflection since it is protected)
        try {
            Method addMethod = net.minecraft.client.gui.screens.Screen.class.getDeclaredMethod("addRenderableWidget", net.minecraft.client.gui.components.events.GuiEventListener.class);
            addMethod.setAccessible(true);
            addMethod.invoke(screen, button);
        } catch (NoSuchMethodException e) {
            // Try alternative name if needed
            Method addMethod = net.minecraft.client.gui.screens.Screen.class.getDeclaredMethod("method_25393", net.minecraft.client.gui.components.events.GuiEventListener.class);
            addMethod.setAccessible(true);
            addMethod.invoke(screen, button);
        }
    }
}
