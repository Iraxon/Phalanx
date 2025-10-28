package net.iraxon.phalanx.registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
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

    /**
     * A map from types to the DeferredRegister for those types
     */
    private final HashMap<IForgeRegistry<?>, DeferredRegister<?>> deferred_registers = new HashMap<>();
    /**
     * A map from types to the RegisterManager's stored RegistryObject instances of
     * that type
     */
    private final HashMap<IForgeRegistry<?>, HashMap<String, RegistryObject<?>>> registry_objects = new HashMap<>();
    /**
     * A map from CreativeModeTab ResourceKeys to lists of items to be put into that
     * tab
     */
    private final HashMap<ResourceKey<CreativeModeTab>, ArrayList<RegistryObject<Item>>> tabAssignments = new HashMap<>();

    private static final Logger LOGGER = LogUtils.getLogger();

    public RegisterManager(String id, IEventBus modEventBus) {
        this.id = id;
        this.modEventBus = modEventBus;

        modEventBus.addListener(this::executeTabAssignments);
    }

    // @SuppressWarnings("unchecked")
    // public <T> void newElementFromSupplier(String name, Supplier<T> element) {
    // newElementFromSupplier(name, (Class<T>) element.get().getClass(), element);
    // }

    /**
     * Adds an element to the RegisterManager
     *
     * @param <T>      The type of the element
     * @param name     The name, not including the mod id and colon, of the element
     * @param registry      The register for T
     * @param supplier A supplier that provides distinct instances of the element
     */
    public <T> void newElementFromSupplier(String name, IForgeRegistry<T> registry, Supplier<? extends T> supplier) {
        guaranteeRegister(registry);
        LOGGER.info("Registering element " + name + " of class " + registry + " with supplier " + supplier);
        registry_objects.get(registry).put(name, getRegister(registry).register(name, supplier));
    }

    /**
     * Registers the elements stored in the RegisterManager
     * to Forge.
     *
     * No more elements should be registered after this is called. This method
     * should never be called more than once.
     *
     * @return null
     */
    public void build() {
        LOGGER.info("Building RegisterManager...");
        for (var r : deferred_registers.values())
            r.register(this.modEventBus);
        LOGGER.info("RegisterManager built");
    }

    public void putTabAssignmentOrder(RegistryObject<Item> i, ResourceKey<CreativeModeTab> t) {
        if (!tabAssignments.containsKey(t))
            tabAssignments.put(t, new ArrayList<>());
        tabAssignments.get(t).add(i);
    }

    private void executeTabAssignments(BuildCreativeModeTabContentsEvent event) {
        var t = event.getTabKey();
        if (tabAssignments.containsKey(t)) {
            for (var i : tabAssignments.get(t))
                event.accept(i);
        }
    }

    private <T> void guaranteeRegister(IForgeRegistry<T> forgeRegistry) {
        if (deferred_registers.containsKey(forgeRegistry))
            deferred_registers.put(forgeRegistry, DeferredRegister.create(forgeRegistry, id));
        registry_objects.put(forgeRegistry, new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    public <T> DeferredRegister<T> getRegister(IForgeRegistry<T> forgeRegistry) {
        guaranteeRegister(forgeRegistry);
        return (DeferredRegister<T>) deferred_registers.get(forgeRegistry);
    }

    @SuppressWarnings("unchecked")
    public <T> RegistryObject<T> get(IForgeRegistry<T> registry, String name) {
        final RegistryObject<?> r;
        try {
            r = registry_objects.get(registry).get(name);
        } catch (NullPointerException e) {
            throw new NoSuchElementException(e);
        }
        // assert cls.isInstance(r.get());
        return (RegistryObject<T>) r;
    }

    /**
     * Compaction of RegistryManager.get(__, __).get()
     *
     * @param <T> Type of the requested item
     * @return The result of applying .get() to the RegistryObject item (i.e. an
     *         instance of the item itself)
     */
    public <T> T getInstance(IForgeRegistry<T> registry, String name) {
        return get(registry, name).get();
    }

    public BlockElement newBlock(String name) {
        return new BlockElement(this, name, Block::new, BlockBehaviour.Properties.of()/* , true */);
    }

    public ItemElement newItem(String name) {
        return new ItemElement(this, name, Item::new, new Item.Properties(), List.of());
    }

    /**
     * Generates an ItemElement that will register a BlockItem
     * for the Block that shares the given name
     *
     * The block must have been registered first!
     *
     * @param name The name of the Block (which the BlockItem will share)
     * @return An ItemElement to configure the BlockItem
     */
    public ItemElement newBlockItem(String name) {
        return new ItemElement(this, name, (p) -> new BlockItem(this.getInstance(ForgeRegistries.BLOCKS, name), p),
                new Item.Properties(), List.of());
    }

    /**
     * An interface for Element objects that allow easy use of
     * a RegisterManager
     *
     * Call RegisterManager.new[block/item/etc.] to get a ModElement instance.
     * Use chained calls of that instance's methods and then .register() to add an
     * element to your mod.
     */

    public interface ModElement<T> {

        /**
         * @return The RegisterManager used to create this ModElement, which will
         *         register it when it is done
         */
        public RegisterManager registerManager();

        /**
         * @return The registry name (not included the mod id or colon) of the object to
         *         be registered
         */
        public String name();

        /**
         * @return The Forge register that this element needs to be registered to eventually
         */
        public IForgeRegistry<T> targetRegister();

        /**
         * A supplier for the object to be registered; it must return distinct instances
         * every call
         *
         * @return A unique insance of the object to be registered
         */
        public T supply();

        /**
         * Sends this ModElement to the RegisterManager for registration
         *
         * @return The RegisterManager, for conveneint chaining
         */
        public default RegisterManager register() {
            registerManager().newElementFromSupplier(name(), targetRegister(), this::supply);
            return registerManager();
        }
    }

    public interface ConstructedElement<P, T> extends ModElement<T> {

        @Override
        public default T supply() {
            return constructor().apply(properties());
        }

        public P properties();

        public Function<P, ? extends T> constructor();
    }

    public record ItemElement(RegisterManager registerManager, String name,
            Function<Item.Properties, ? extends Item> constructor, Item.Properties properties,
            List<ResourceKey<CreativeModeTab>> tabs)
            implements ConstructedElement<Item.Properties, Item> {

        @Override
        public IForgeRegistry<Item> targetRegister() {
            return ForgeRegistries.ITEMS;
        }

        @Override
        public RegisterManager register() {
            var r = ConstructedElement.super.register();
            if (!tabs.isEmpty()) {
                for (var t : tabs) {
                    registerManager.putTabAssignmentOrder(registerManager.get(ForgeRegistries.ITEMS, name()), t);
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

        public ItemElement tabs(List<ResourceKey<CreativeModeTab>> tabs) {
            return new ItemElement(registerManager, name, constructor, properties, tabs);
        }
    }

    public record BlockElement(RegisterManager registerManager, String name,
            Function<BlockBehaviour.Properties, ? extends Block> constructor,
            BlockBehaviour.Properties properties/* , boolean blockItem */)
            implements ConstructedElement<BlockBehaviour.Properties, Block> {

        @Override
        public Block supply() {
            return constructor.apply(properties);
        }

        @Override
        public IForgeRegistry<Block> targetRegister() {
            return ForgeRegistries.BLOCKS;
        }

        @Override
        public RegisterManager register() {
            var r = ConstructedElement.super.register();
            // if (blockItem) {
            // registerManager.newElementFromSupplier(name, BlockItem.class, () -> new
            // BlockItem(
            // registerManager.get(Block.class, name).get(), new Item.Properties()));
            // }
            return r;
        }

        public BlockElement constructor(Function<BlockBehaviour.Properties, ? extends Block> blockConstructor) {
            return new BlockElement(registerManager, name, blockConstructor, properties/* , blockItem */);
        }

        public BlockElement properties(BlockBehaviour.Properties properties) {
            return new BlockElement(registerManager, name, constructor, properties/* , blockItem */);
        }

        public BlockElement withoutBlockItem() {
            return new BlockElement(registerManager, name, constructor, properties/* , false */);
        }
    }
}

class UnknownRegistryException extends IllegalArgumentException {
    public UnknownRegistryException(String s) {
        super(s);
    }
}
