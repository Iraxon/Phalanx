// package net.iraxon.phalanx.registration;

// import java.util.List;

// import net.minecraft.world.item.CreativeModeTab;
// import net.minecraft.world.item.Item;

// public interface TabbedElement<T> extends ModElement<T> {
//     public List<CreativeModeTab> tabs();

//     /**
//      * MUST BE CALLED AFTER THE ITEM HAS BEEN REGISTERED
//      */
//     public default void sendOrders() {
//         for (var t : tabs()) {
//                 registerManager().putTabAssignmentOrder(registerManager().get(Item.class, name()), t);
//             }
//     }
// }
