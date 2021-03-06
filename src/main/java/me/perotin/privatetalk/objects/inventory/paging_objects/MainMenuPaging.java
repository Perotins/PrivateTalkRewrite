package me.perotin.privatetalk.objects.inventory.paging_objects;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import me.perotin.privatetalk.PrivateTalk;
import me.perotin.privatetalk.objects.Chatroom;
import me.perotin.privatetalk.objects.inventory.PagingMenu;
import me.perotin.privatetalk.objects.inventory.actions.ChatroomItemStackAction;
import me.perotin.privatetalk.storage.files.FileType;
import me.perotin.privatetalk.storage.files.PrivateFile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

/* Created by Perotin on 11/29/19 */

/**
 * Class for all Main Menu instances that are shown to players
 */
public class MainMenuPaging extends PagingMenu {


    private PrivateFile messages;
    private PrivateTalk plugin;
    public MainMenuPaging(Player viewer, PrivateTalk plugin){
        super(viewer.getName()+"-main", 6, viewer);
        this.messages = new PrivateFile(FileType.MESSAGES);
        this.pane = new PaginatedPane(2, 1, 7, 4);
        this.plugin = plugin;
        getPaginatedPane().populateWithGuiItems(generatePages());

    }



    @Override
    protected List<GuiItem> generatePages() {
        List<ItemStack> toDisplay = plugin.getChatrooms().stream().map(Chatroom::getItem).collect(Collectors.toList());
        toDisplay = toDisplay.stream().sorted(this::compare).collect(Collectors.toList());
        List<GuiItem> guiItems = toDisplay.stream().map(item -> new GuiItem(item, ChatroomItemStackAction.clickOnChatroom())).collect(Collectors.toList());
        return guiItems;
    }


    /**
     * Sorts the itemstacks by whether or not they have the saved material type, sorting them from saved -> everything else
     * @param o
     * @param o1
     * @return
     */
    private int compare(ItemStack o, ItemStack o1){
        Material saved = Material.valueOf(plugin.getConfig().getString("saved-material"));
        if(o.getType() == o1.getType()) return 0;
        else if(o.getType() == saved) return 1;
        else if(o1.getType() == saved) return -1;
        return 0;
    }
}
