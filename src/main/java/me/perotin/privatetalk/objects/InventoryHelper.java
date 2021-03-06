package me.perotin.privatetalk.objects;

/* Created by Perotin on 8/27/19 */

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.perotin.privatetalk.PrivateTalk;
import me.perotin.privatetalk.objects.inventory.actions.CreateChatroomAction;
import me.perotin.privatetalk.storage.Pair;
import me.perotin.privatetalk.storage.files.FileType;
import me.perotin.privatetalk.storage.files.PrivateFile;
import me.perotin.privatetalk.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Class for bringing up static parts of inventories like the nav-bar etc.
 */
public class InventoryHelper {

    private PrivateFile file;
    private StaticPane navBar;
    private StaticPane pagingNavigationBar;
    private StaticPane creationMenu;
    private StaticPane rightSideDecoSlots;
    private StaticPane leftSideDecoSlots;
    /**
     * Consumer for making decoration items cancel the click event and nothing else
     **/
    private Consumer<InventoryClickEvent> doNothing;

    public InventoryHelper() {
        this.file = new PrivateFile(FileType.MENUS);
        this.navBar = new StaticPane(0, 0, 9, 1);
        this.pagingNavigationBar = new StaticPane(0, 5, 9, 1);
        this.doNothing = (event) -> event.setCancelled(true);
        setNavBar();
        setPagingNavigationBar();
        setSideDecoSlots();
    }

    //----------------------- Navigation Bar Methods ----------------------------------------------------


    /**
     * @param inventory to set
     * @return sets the nav bar for any given inventory
     */
    public Gui setNavigationBar(Gui inventory, OfflinePlayer owner) {

        navBar.addItem(new GuiItem(getItemFrom(Material.PLAYER_HEAD, "nav-bar.player-profile-head", owner).getFirst()), getItemFrom(Material.PLAYER_HEAD, "nav-bar.player-profile-head", owner).getSecond(), 0);
        inventory.addPane(navBar);
        return inventory;
    }


    /**
     * Sets the navigation bar that appears in most menus at the very top
     */
    private void setNavBar() {
        PrivateFile file = new PrivateFile(FileType.MENUS);
        Pair head = getItemFrom(Material.PLAYER_HEAD, "nav-bar.player-profile-head", null);
        Pair invites = getItemFrom(Material.WRITABLE_BOOK, "nav-bar.manage-invites", null);
        Pair createChatroom = getItemFrom(Material.ANVIL, "nav-bar.create-chatroom", null);

        navBar.addItem(new GuiItem((ItemStack) head.getFirst()), (int) head.getSecond(), 0);
        navBar.addItem(new GuiItem((ItemStack) createChatroom.getFirst(), CreateChatroomAction.createChatroomConsumer()), (int) createChatroom.getSecond(), 0);
        navBar.addItem(new GuiItem((ItemStack) invites.getFirst()), (int) invites.getSecond(), 0);
        GuiItem deco = new GuiItem(DECO_ITEM(), doNothing);
        List<Integer> slots = getAsInts(file.getConfiguration().getStringList("nav-bar.deco-item.slots"));
        for (int x : slots) {
            navBar.addItem(deco, x, 0);
        }

    }


    //----------------------- Creation Menu Methods ----------------------------------------------------

    /**
     * Sets the creation menu on an inventory
     */
    private Gui setCreationMenu(Gui toSet, PreChatroom chatroom) {
        setCreationMenu(chatroom);
        toSet.addPane(creationMenu);
        return toSet;
    }

    /**
     * Sets the creation menu items
     * TODO Functions attached to each item
     */
    private void setCreationMenu(PreChatroom chatroom) {
        PrivateFile menus = new PrivateFile(FileType.MENUS);
        PrivateFile messages = new PrivateFile(FileType.MESSAGES);

        this.creationMenu = new StaticPane(1, 1, 7, 4);
        Pair<ItemStack, Integer> name = getItemFrom(Material.valueOf(menus.getString("creation-menu.name.material")), "creation-menu.name", null);
        Pair<ItemStack, Integer> description = getItemFrom(Material.valueOf(menus.getString("creation-menu.description.material")), "creation-menu.description", null);
        Pair<ItemStack, Integer> status = getItemFrom(Material.valueOf(menus.getString("creation-menu.status.material")), "creation-menu.status", null);
        Pair<ItemStack, Integer> saved = getItemFrom(Material.valueOf(menus.getString("creation-menu.saved.material")), "creation-menu.saved", null);
        Pair<ItemStack, Integer> createButton = getItemFrom(Material.valueOf(menus.getString("creation-menu.create-button.material")), "creation-menu.create-button", null);
        // setting all the pair values to use the values from the PreChatroom
        ItemStack nameItem = name.getFirst();
        if (chatroom.getName() != null) {
            ItemMeta meta = name.getFirst().getItemMeta();
            meta.setDisplayName(name.getFirst().getItemMeta().getDisplayName() + " " + chatroom.getName());
            nameItem.setItemMeta(meta);

        }
        name = new Pair<>(nameItem, name.getSecond());

        ItemStack descItem = description.getFirst();
        if (chatroom.getDescription() != null) {
            ItemMeta meta = description.getFirst().getItemMeta();
            meta.setDisplayName(description.getFirst().getItemMeta().getDisplayName() + " " + chatroom.getDescription());
            descItem.setItemMeta(meta);

        }
        description = new Pair<>(descItem, description.getSecond());

        ItemStack statusItem = status.getFirst();
        ItemMeta sMeta = statusItem.getItemMeta();
        if(chatroom.isPublic()) {
            sMeta.setDisplayName(sMeta.getDisplayName() + " " +messages.getString("public") );
        } else {
            sMeta.setDisplayName(sMeta.getDisplayName() + " " + messages.getString("private"));
        }
        statusItem.setItemMeta(sMeta);

        status = new Pair<>(statusItem, status.getSecond());

        ItemStack savedItem = saved.getFirst();
        ItemMeta savedMeta = savedItem.getItemMeta();
        if(chatroom.isSaved()) {
            sMeta.setDisplayName(sMeta.getDisplayName() + " " +messages.getString("true") );
        } else {
            sMeta.setDisplayName(sMeta.getDisplayName() + " " + messages.getString("false"));
        }
        savedItem.setItemMeta(savedMeta);

        status = new Pair<>(statusItem, status.getSecond());
        saved = new Pair<>(savedItem, saved.getSecond());


        creationMenu.addItem(new GuiItem(name.getFirst(), CreateChatroomAction.setNameConsumer()), name.getSecond(), 1);
        creationMenu.addItem(new GuiItem(description.getFirst(), CreateChatroomAction.setDescriptionConsumer()), description.getSecond(), 1);
        creationMenu.addItem(new GuiItem(status.getFirst(), CreateChatroomAction.toggleStatusConsumer()), status.getSecond(), 2);
        creationMenu.addItem(new GuiItem(saved.getFirst(), CreateChatroomAction.toggleSavedConsumer()), saved.getSecond(), 2);
        creationMenu.addItem(new GuiItem(createButton.getFirst(), CreateChatroomAction.clickCreateButtonConsumer()), createButton.getSecond(), 3);

    }

    /**
     * Gets the GUI for any designed PreChatroom object
     */
    public Gui getCreationMenu(PreChatroom chatroom) {
        PrivateFile file = new PrivateFile(FileType.MENUS);
        Gui gui = new Gui(PrivateTalk.getInstance(), 6, file.getString("creation-menu.display-name"));
        setSideDecorationSlots(gui);
        gui = setCreationMenu(gui, chatroom);
      //  gui = setPagingNavBar(gui, false, true);
        return gui;
    }
    //----------------------- Decoration Methods ----------------------------------------------------

    /**
     * @param inv to set
     * @return inventory set with side decoration slotsss
     */
    public Gui setSideDecorationSlots(Gui inv) {
        inv.addPane(leftSideDecoSlots);
        inv.addPane(rightSideDecoSlots);
        return inv;

    }

    /**
     * @apiNote sets the side decoration slots
     */
    private void setSideDecoSlots() {
        this.rightSideDecoSlots = new StaticPane(8, 1, 1, 4);
        this.leftSideDecoSlots = new StaticPane(0, 1, 1, 4);
        rightSideDecoSlots.fillWith(DECO_ITEM());
        leftSideDecoSlots.fillWith(DECO_ITEM());

    }
    //----------------------- Paging Buttons Methods ----------------------------------------------------


    /**
     * @param inventory to set paging-nav-bar, next is whether a next button should appear and back is whether a back button should appear
     * @return inventory with paging-navar bar
     */
    public Gui setPagingNavBar(Gui inventory, boolean next, boolean back) {

        inventory.addPane(pagingNavigationBar);
        return inventory;
//        if(next && back) {
//            inventory.addPane(pagingNavigationBar);
//            return inventory;
//        } else {
//            if(!back){
//                pagingNavigationBar.removeItem(new GuiItem(BACK_ITEM()));
//            }
//            if(!next){
//                pagingNavigationBar.removeItem(new GuiItem(NEXT_ITEM()));
//            }
//            inventory.addPane(pagingNavigationBar);
//            return inventory;
//        }
    }


    /**
     * @apiNote Sets the next / back buttons at the bottom along with deco slots
     */
    private void setPagingNavigationBar() {
        PrivateFile file = new PrivateFile(FileType.MENUS);
        List<Integer> decoSlots = getAsInts(file.getConfiguration().getStringList("paging-nav-bar.deco-item.slots"));
        int nextSlot = file.getConfiguration().getInt("paging-nav-bar.next-item.slot");
        int backSlot = file.getConfiguration().getInt("paging-nav-bar.back-item.slot");
        for (int x : decoSlots) {
            pagingNavigationBar.addItem(new GuiItem(DECO_ITEM(), doNothing), x, 5);
        }
        pagingNavigationBar.addItem(new GuiItem(BACK_ITEM()), backSlot, 5);
        pagingNavigationBar.addItem(new GuiItem(NEXT_ITEM()), nextSlot, 5);

    }


    //----------------------- Utility Methods ----------------------------------------------------


    /**
     * @param material path to the item in menus.yml, for example, "nav-bar.player-profile-head" will retrieve said path
     * @return itemstack with integer slot
     */
    private Pair<ItemStack, Integer> getItemFrom(Material material, String path, OfflinePlayer owner) {
        PrivateFile file = new PrivateFile(FileType.MENUS);
        ItemStackUtils builder = new ItemStackUtils(material, owner);
        builder.setName(file.getString(path + ".display"));
        builder.setLore(file.getConfiguration().getStringList(path + ".lore"));
        return new Pair<>(builder.build(), file.getConfiguration().getInt(path + ".slot"));

    }


    /**
     * @param stringList to convert
     * @return converted int list
     * @apiNote will break if stringList doesn't meet parsing conditions
     */
    private List<Integer> getAsInts(List<String> stringList) {
        return stringList.stream().map(Integer::parseInt).collect(Collectors.toList());
    }


    // STATIC ITEMS ------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * @return blank decoration item used to fill white-space
     */
    public static ItemStack DECO_ITEM() {
        PrivateFile items = new PrivateFile(FileType.MENUS);
        ItemStackUtils item = new ItemStackUtils(Material.getMaterial(items.getString("global-items.deco-item.material")));
        item.setName(items.getString("global-items.deco-item.material"));
        return item.build();
    }


    /**
     * @return item used to navigate backwards in a menu
     */
    public static ItemStack BACK_ITEM() {
        PrivateFile items = new PrivateFile(FileType.MENUS);
        ItemStackUtils item = new ItemStackUtils(Material.getMaterial(items.getString("global-items.back-item.material")));
        item.setName(items.getString("global-items.back-item.material"));
        return item.build();
    }

    /**
     * @return item used to navigate forwards in a menu
     */
    public static ItemStack NEXT_ITEM() {
        PrivateFile items = new PrivateFile(FileType.MENUS);
        ItemStackUtils item = new ItemStackUtils(Material.getMaterial(items.getString("global-items.next-item.material")));
        item.setName(items.getString("global-items.next-item.material"));
        return item.build();
    }


}
