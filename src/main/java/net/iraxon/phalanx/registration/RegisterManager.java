package net.iraxon.phalanx.registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

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

    private final HashMap<Class<?>, DeferredRegister<?>> REGISTERS = new HashMap<>();
    private final HashMap<Class<?>, HashMap<String, RegistryObject<?>>> REGISTRY_OBJECTS = new HashMap<>();
    private final HashMap<CreativeModeTab, ArrayList<RegistryObject<Item>>> tabAssignments = new HashMap<>();

    private static final Logger LOGGER = LogUtils.getLogger();

    public RegisterManager(String id, IEventBus modEventBus) {
        this.id = id;
        this.modEventBus = modEventBus;

        modEventBus.addListener(this::executeTabAssignments);
    }

    // @SuppressWarnings("unchecked")
    // public <T> void newElementFromSupplier(String name, Supplier<T> element) {
    //     newElementFromSupplier(name, (Class<T>) element.get().getClass(), element);
    // }

    /**
     * Adds an element to the RegisterManager
     * @param <T> The type of the element
     * @param name The name, not including the mod id and colon, of the element
     * @param key The Class object of T
     * @param supplier A supplier that provides distinct instances of the element
     */
    public <T> void newElementFromSupplier(String name, Class<T> key, Supplier<? extends T> supplier) {
        guaranteeRegister(key);
        LOGGER.info("Registering element " + name + " of class " + key + " with supplier " + supplier);
        REGISTRY_OBJECTS.get(key).put(name, getRegister(key).register(name, supplier));
    }

    public void build() {
        LOGGER.info("Building RegisterManager...");
        for (var r : REGISTERS.values())
            r.register(this.modEventBus);
        LOGGER.info("RegisterManager built");
    }

    public void putTabAssignmentOrder(RegistryObject<Item> i, CreativeModeTab t) {
        if (!tabAssignments.containsKey(t))
            tabAssignments.put(t, new ArrayList<>());
        tabAssignments.get(t).add(i);
    }

    private void executeTabAssignments(BuildCreativeModeTabContentsEvent event) {
        var t = event.getTab();
        if (tabAssignments.containsKey(t)) {
            for (var i : tabAssignments.get(t))
                event.accept(i);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> IForgeRegistry<T> findRegister(Class<T> cls) {
        final Predicate<Class<?>> clsSuperclass = x -> x.isAssignableFrom(cls);
        IForgeRegistry<?> r;

        if (clsSuperclass.test(Block.class)) {
            r = ForgeRegistries.BLOCKS;
        } else if (clsSuperclass.test(EntityType.class)) {
            r = ForgeRegistries.ENTITY_TYPES;
        } else if (clsSuperclass.test(Fluid.class)) {
            r = ForgeRegistries.FLUIDS;
        } else if (clsSuperclass.test(Item.class)) {
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
        final RegistryObject<?> r;
        try {
            r = REGISTRY_OBJECTS.get(cls).get(name);
        } catch (NullPointerException e) {
            throw new NoSuchElementException(e);
        }
        // assert cls.isInstance(r.get());
        return (RegistryObject<T>) r;
    }

    /**
     * Compaction of RegistryObject.get(__, __).get()
     * @param <T> Type of the requested item
     * @return The result of applying .get() to the RegistryObject item (i.e. an instance of the item itself)
     */
    public <T> T get2(Class<T> cls, String name) {
        return get(cls, name).get();
    }

    public BlockElement newBlock(String string) {
        return new BlockElement(this, string, Block::new, BlockBehaviour.Properties.of(), true);
    }

    public ItemElement newItem(String string) {
        return new ItemElement(this, string, Item::new, new Item.Properties(), List.of());
    }
}

class UnknownRegistryException extends IllegalArgumentException {
    public UnknownRegistryException(String s) {
        super(s);
    }
}
