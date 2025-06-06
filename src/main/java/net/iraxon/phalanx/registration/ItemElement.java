package net.iraxon.phalanx.registration;

import java.util.List;
import java.util.function.Function;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public record ItemElement(RegisterManager registerManager, String name,
        Function<Item.Properties, ? extends Item> constructor, Item.Properties properties, List<CreativeModeTab> tabs)
        implements ConstructedElement<Item.Properties, Item> {

    @Override
    public Class<Item> type() {
        return Item.class;
    }

    @Override
    public RegisterManager register() {
        var r = ConstructedElement.super.register();
        if (!tabs.isEmpty()) {
            for (var t : tabs) {
                registerManager.putTabAssignmentOrder(registerManager.get(Item.class, name()), t);
            }
        }
        return r;
    }

    public ItemElement constructor(Function<Item.Properties, ? extends Item> constructor) {
        return new ItemElement(registerManager, name, constructor, properties, tabs);
    }

    public ItemElement properties(Item.Properties properties) {
        return new ItemElement(registerManager, name, constructor, properties, tabs);
    }

    public ItemElement withoutBlockItem() {
        return new ItemElement(registerManager, name, constructor, properties, tabs);
    }
}
