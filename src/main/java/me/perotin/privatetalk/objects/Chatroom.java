package me.perotin.privatetalk.objects;

/* Created by Perotin on 8/14/19 */

import me.perotin.privatetalk.PrivateTalk;
import me.perotin.privatetalk.storage.Pair;
import me.perotin.privatetalk.storage.files.FileType;
import me.perotin.privatetalk.storage.files.PrivateFile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.stream.Collectors;

import static me.perotin.privatetalk.storage.files.FileType.MESSAGES;

/**
 * Captures a chatroom object.
 */
public class Chatroom {

    /** @apiNote contains all members with their respective chat roles **/
    private Map<UUID, ChatRole> members;
    // owner of chatroom
    private UUID owner;
    private String name;
    private String description;
    // true if chatroom is public, false if private
    private boolean isPublic;
    // true if saved, false if not
    private boolean isSaved;
    private List<UUID> bannedMembers;
    private HashMap<UUID, String> nickNames;
    private PrivateFile messages;
    private ItemStack display;

    /**
     * Initial chatroom constructor
     */
    public Chatroom(UUID owner, String name, String description, boolean isPublic, boolean isSaved) {
        this.members = new HashMap<>();
        this.members.put(owner, ChatRole.OWNER);
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.messages = new PrivateFile(MESSAGES);
        this.isSaved = isSaved;
        this.display = generateItem();
    }

    /**
     * Used for loading a chatroom from file with members
     * @return chatroom
     */
    public Chatroom(UUID owner, String name, String description, boolean isPublic, boolean isSaved, Map<UUID, ChatRole> members) {
        this(owner, name, description, isPublic, isSaved);
        this.messages = new PrivateFile(MESSAGES);
        this.members = members;
    }

    /**
     * @return Set of all Moderators
     */
    public List<UUID> getModerators(){
        List<UUID> moderators = new ArrayList<>();
        for(UUID uuid : members.keySet()){
            if(members.get(uuid) == ChatRole.MODERATOR){
                moderators.add(uuid);
            }
        }
        return moderators;
    }

    public Map<UUID, ChatRole> getMemberMap(){
        return members;
    }
    public Set<UUID> getMembers() {
        return members.keySet();
    }

    /**
     * @return List of all online players
     */
    public List<Player> getOnlinePlayers(){
       return getMembers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }


    /**
     * Called only once to generate the itemstack to represent the chatroom in player-profiles and the main menu
     * @return Itemstack representation of the chatroom
     */
    private ItemStack generateItem(){
        PrivateFile messages = new PrivateFile(FileType.MENUS);
        ItemStack item;
        if(isSaved){
           String savedString = PrivateTalk.getInstance().getConfig().getString("saved-material");

           Material saved;
           try {
                saved = Material.valueOf(savedString);
           } catch (IllegalArgumentException ex){
               // exception handling
               Bukkit.getLogger().severe("The material id for the saved-material key in /PrivateTalk/config.yml is invalid! Make sure to use a correct value.");
               return null;
           }
             item = new ItemStack(saved);

        } else {
            // player skin head
            item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta skull = (SkullMeta) item.getItemMeta();
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(getOwner()));
            item.setItemMeta(skull);
        }
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(messages.getString("chatroom-items.name").replace("$name$", getName()));
        itemMeta.setLore(Arrays.asList(messages.getString("chatroom-items.description").replace("$description$", getDescription()),
                messages.getString("chatroom-items.status").replace("$status$", getStringStatus()),
                messages.getString("chatroom-items.owner").replace("$owner$", Bukkit.getOfflinePlayer(getOwner()).getName()),
                messages.getString("chatroom-items.members").replace("$member_count$", getMembers().size()+"" )));

        item.setItemMeta(itemMeta);



        return item;
    }

    /**
     * @return String form of the status
     */
    private String getStringStatus(){
        PrivateFile messages = new PrivateFile(MESSAGES);
        if(isPublic){
            return messages.getString("public");
        } else {
            return messages.getString("private");
        }
    }

    /**
     * @return true if chatroom gets saved, false if not
     */

    public boolean isSaved() {
        return isSaved;
    }

    /**
     * @return Item representation of the chatroom
     */
    public ItemStack getItem(){
        return display;
    }

    public void addMember(Pair<UUID, ChatRole> value) {
        this.members.put(value.getFirst(), value.getSecond());
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<UUID> getBannedMembers() {
        return bannedMembers;
    }

    public void setBannedMembers(List<UUID> bannedMembers) {
        this.bannedMembers = bannedMembers;
    }

    public HashMap<UUID, String> getNickNames() {
        return nickNames;
    }

    /**
     *Sets the nickname map
     */
    public void setNickNames(HashMap<UUID, String> nickNames) {
        this.nickNames = nickNames;
    }


    /**
     * Checks if a memmber is in this chatroom object
     */
    public boolean isInChatroom(UUID uuid){
        return getMembers().contains(uuid);
    }

    /**
     *
     * @param member of chatroom
     * @return string version of their role, being either member, moderator, or owner
     */
    public String getRole(UUID member){
        if(isInChatroom(member)){
            ChatRole value = getMemberMap().get(member);
            switch(value){
                case OWNER: messages.getString("owner");
                case MODERATOR: messages.getString("moderator");
                case MEMBER: messages.getString("member");
            }
        }
        return "";
    }

    /**
     * @apiNote Used to save a chatroom to chatrooms.yml
     */
    public void saveToFile(){
        PrivateFile chatrooms = new PrivateFile(FileType.CHATROOM);
        chatrooms.set(name+".status", isPublic);
        chatrooms.set(name+".saved", isSaved);
        chatrooms.set(name+".owner", getOwner().toString());
        chatrooms.set(name+".description", description);
        chatrooms.getConfiguration().createSection(name+".members", getMemberMap());
        chatrooms.save();
    }


    /**
     * @param name of chatroom to load
     * @return chatroom object
     *
     */
    public static Chatroom loadChatroom(String name){
        PrivateFile chatrooms = new PrivateFile(FileType.CHATROOM);
        if(chatrooms.getConfiguration().contains(name)) {
            String description = chatrooms.getString(name + ".description");
            UUID owner = UUID.fromString(chatrooms.getString(name + ".owner"));
            boolean saved = chatrooms.getBool(name + ".saved");
            boolean isPublic = chatrooms.getBool(name + ".status");
            ConfigurationSection sec = chatrooms.getConfiguration().getConfigurationSection(name + ".members");
            Map<UUID, ChatRole> loadedRoles = new HashMap<>();
            for (String key : sec.getKeys(false)) {
                String role = sec.get(key).toString();
                ChatRole roleO = ChatRole.valueOf(role);
                UUID uuid = UUID.fromString(key);
                loadedRoles.put(uuid, roleO);
            }
            return new Chatroom(owner, name, description, isPublic, saved, loadedRoles);
        } else return null;

    }


}
