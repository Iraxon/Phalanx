package net.iraxon.phalanx.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

/**
 * A class that accepts mod elements and handles registering
 * them; add elements and then call build() when done
 */

public class RegisterManager {
    private final String id;
    private final IEventBus modEventBus;

    private final Map<Class<?>, DeferredRegister<?>> REGISTERS = new HashMap<>();
    private final Map<Class<?>, Map<String, RegistryObject<?>>> REGISTRY_OBJECTS = new HashMap<>();
    private final Map<CreativeModeTab, List<Item>> tabAssignments = new HashMap<>();

    public RegisterManager(String id, IEventBus modEventBus) {
        this.id = id;
        this.modEventBus = modEventBus;

        modEventBus.addListener(this::addCreative);
    }

    @SuppressWarnings("unchecked")
    public <T> void newElementFromSupplier(String name, Supplier<T> element) {
        newElementFromSupplier(name, (Class<T>) element.get().getClass(), element);
    }

    /**
     * Adds an element to the RegisterManager
     * @param <T> The type of the element
     * @param name The name, not including the mod id and colon, of the element
     * @param key The Class object of T
     * @param supplier A supplier that provides distinct instances of the element
     */
    public <T> void newElementFromSupplier(String name, Class<T> key, Supplier<T> supplier) {
        guaranteeRegister(key);
        getRegister(key).register(name, supplier);
    }

    public void build() {
        for (var r : REGISTERS.values())
            r.register(this.modEventBus);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        var t = event.getTab();
        if (tabAssignments.containsKey(t)) {
            for (var i : tabAssignments.get(t))
                event.accept(i);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> IForgeRegistry<T> findRegister(Class<T> cls) {
        final Predicate<Class<?>> test = x -> x.isAssignableFrom(cls);
        IForgeRegistry<?> r;

        if (test.test(Block.class)) {
            r = ForgeRegistries.BLOCKS;
        } else if (test.test(EntityType.class)) {
            r = ForgeRegistries.ENTITY_TYPES;
        } else if (test.test(Fluid.class)) {
            r = ForgeRegistries.FLUIDS;
        } else if (test.test(Item.class)) {
            r = ForgeRegistries.ITEMS;
        } else {
            throw new IllegalArgumentException("No known registry available for type " + cls);
        }

        return (IForgeRegistry<T>) r;
    }

    private <T> void addRegister(Class<T> key) {
        REGISTERS.put(key, DeferredRegister.create(findRegister(key), id));
        REGISTRY_OBJECTS.put(key, new HashMap<>());
    }

    private <T> boolean isRegister(Class<T> key) {
        return REGISTERS.containsKey(key);
    }

    private <T> void guaranteeRegister(Class<T> key) {
        if (!isRegister(key))
            addRegister(key);
    }

    @SuppressWarnings("unchecked")
    public <T> DeferredRegister<T> getRegister(Class<T> key) {
        if (!isRegister(key)) {
            throw new UnknownRegistryException("Unknown key: " + key);
        }
        return (DeferredRegister<T>) REGISTERS.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> RegistryObject<T> get(Class<T> cls, String name) {
        final var r = REGISTRY_OBJECTS.get(cls).get(name);
        assert cls.isInstance(r.get());
        return (RegistryObject<T>) r;
    }

    public BlockElement newBlock(String string) {
        return new BlockElement(this, string, Block::new, BlockBehaviour.Properties.of());
    }
}

class UnknownRegistryException extends IllegalArgumentException {
    public UnknownRegistryException(String s) {
        super(s);
    }
}
