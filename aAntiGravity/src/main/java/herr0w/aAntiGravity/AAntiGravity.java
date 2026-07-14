package herr0w.aAntiGravity;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class AAntiGravity extends JavaPlugin implements Listener {

    private static final int GUI_SIZE = 54;
    private static final int[] CONTENT_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };
    private static final int ITEMS_PER_PAGE = CONTENT_SLOTS.length;
    private static final int PAGE_INFO_SLOT = 4;
    private static final int PREVIOUS_SLOT = 45;
    private static final int ENABLE_ALL_SLOT = 47;
    private static final int CLOSE_SLOT = 49;
    private static final int DISABLE_ALL_SLOT = 51;
    private static final int NEXT_SLOT = 53;
    private static final String RELOAD_PERMISSION = "aantigravity.reload";
    private static final String GUI_PERMISSION = "aantigravity.gui";

    private final Set<Material> blockedMaterials = EnumSet.noneOf(Material.class);
    private boolean enabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings();

        getServer().getPluginManager().registerEvents(this, this);
        registerCommand();

        getLogger().info("aAntiGravity aktif edildi.");
    }

    @Override
    public void onDisable() {
        blockedMaterials.clear();
        getLogger().info("aAntiGravity devre dışı bırakıldı.");
    }

    private void registerCommand() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> event.registrar().register(
                "aantigravity",
                "aAntiGravity komutları",
                List.of("ag"),
                new AntiGravityCommand()
        ));
    }

    private void loadSettings() {
        reloadConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        enabled = getConfig().getBoolean("enabled", true);
        blockedMaterials.clear();

        if (!getConfig().isConfigurationSection("blocks")) {
            getLogger().warning("Config içinde 'blocks' bölümü bulunamadı.");
            return;
        }

        for (String key : getConfig().getConfigurationSection("blocks").getKeys(false)) {
            Material material = getValidBlockMaterial(key);

            if (material == null) {
                getLogger().warning("Config içinde geçersiz blok ismi var: " + key);
                continue;
            }

            if (getConfig().getBoolean("blocks." + key, false)) {
                blockedMaterials.add(material);
            }
        }
    }

    private Material getValidBlockMaterial(String name) {
        Material material = Material.matchMaterial(name.toUpperCase(Locale.ROOT));

        if (material == null || !material.isBlock() || material.isAir()) {
            return null;
        }

        return material;
    }

    private boolean shouldBlock(Material material) {
        return enabled && blockedMaterials.contains(material);
    }

    private List<BlockEntry> getConfigBlocks() {
        if (!getConfig().isConfigurationSection("blocks")) {
            return Collections.emptyList();
        }

        List<BlockEntry> blocks = new ArrayList<>();

        for (String key : getConfig().getConfigurationSection("blocks").getKeys(false)) {
            Material material = getValidBlockMaterial(key);

            if (material != null) {
                blocks.add(new BlockEntry(key, material));
            }
        }

        return blocks;
    }

    private void openGui(Player player, int page) {
        List<BlockEntry> blocks = getConfigBlocks();
        int maxPage = Math.max(0, (blocks.size() - 1) / ITEMS_PER_PAGE);
        int safePage = Math.max(0, Math.min(page, maxPage));
        Inventory inventory = Bukkit.createInventory(new AntiGravityHolder(safePage), GUI_SIZE, color("&b&laAntiGravity &8- &fBlok Ayarları"));

        fillGuiFrame(inventory, safePage, maxPage, blocks.size());

        int start = safePage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, blocks.size());

        for (int index = start; index < end; index++) {
            BlockEntry entry = blocks.get(index);
            inventory.setItem(CONTENT_SLOTS[index - start], createBlockItem(entry));
        }

        player.openInventory(inventory);
    }

    private void fillGuiFrame(Inventory inventory, int page, int maxPage, int totalBlocks) {
        ItemStack filler = createFiller(Material.GRAY_STAINED_GLASS_PANE);

        for (int slot = 0; slot < GUI_SIZE; slot++) {
            inventory.setItem(slot, filler);
        }

        for (int slot : CONTENT_SLOTS) {
            inventory.clear(slot);
        }

        inventory.setItem(PAGE_INFO_SLOT, createInfoItem(page, maxPage, totalBlocks));
        inventory.setItem(PREVIOUS_SLOT, page > 0
                ? createButton(Material.ARROW, "&bÖnceki Sayfa", List.of("&7Bir önceki sayfaya geç."))
                : createButton(Material.GRAY_DYE, "&8Önceki Sayfa", List.of("&7Zaten ilk sayfadasın.")));
        inventory.setItem(ENABLE_ALL_SLOT, createButton(Material.LIME_DYE, "&aTümünü Aç", List.of("&7Listedeki tüm blokları aktif yap.", "&7Aktif bloklar artık düşmez.")));
        inventory.setItem(CLOSE_SLOT, createButton(Material.BARRIER, "&cKapat", List.of("&7Menüyü kapat.")));
        inventory.setItem(DISABLE_ALL_SLOT, createButton(Material.RED_DYE, "&cTümünü Kapat", List.of("&7Listedeki tüm blokları pasif yap.", "&7Pasif bloklar normal düşer.")));
        inventory.setItem(NEXT_SLOT, page < maxPage
                ? createButton(Material.ARROW, "&bSonraki Sayfa", List.of("&7Bir sonraki sayfaya geç."))
                : createButton(Material.GRAY_DYE, "&8Sonraki Sayfa", List.of("&7Zaten son sayfadasın.")));
    }

    private ItemStack createBlockItem(BlockEntry entry) {
        boolean active = getConfig().getBoolean("blocks." + entry.configKey(), false);
        ItemStack item = new ItemStack(entry.material());
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(color((active ? "&a" : "&c") + formatMaterialName(entry.material())));
            meta.setLore(List.of(
                    color(active ? "&aDurum: Açık" : "&cDurum: Kapalı"),
                    color(active ? "&7Bu blok artık düşmez." : "&7Bu blok normal şekilde düşer."),
                    "",
                    color("&8Tıklayarak değiştir.")
            ));
            meta.setEnchantmentGlintOverride(active);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createInfoItem(int page, int maxPage, int totalBlocks) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(color("&bSayfa Bilgisi"));
            meta.setLore(List.of(
                    color("&7Sayfa: &f" + (page + 1) + "&8/&f" + (maxPage + 1)),
                    color("&7Toplam blok: &f" + totalBlocks),
                    color("&7Aktif blok: &a" + blockedMaterials.size())
            ));
            meta.setEnchantmentGlintOverride(true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createButton(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(color(name));
            meta.setLore(lore.stream().map(this::color).toList());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createFiller(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(" ");
            meta.setHideTooltip(true);
            item.setItemMeta(meta);
        }

        return item;
    }

    private String formatMaterialName(Material material) {
        String[] words = material.name().toLowerCase(Locale.ROOT).split("_");
        StringBuilder builder = new StringBuilder();

        for (String word : words) {
            if (!builder.isEmpty()) {
                builder.append(' ');
            }

            builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }

        return builder.toString();
    }

    private void setAllBlocks(boolean value) {
        for (BlockEntry entry : getConfigBlocks()) {
            getConfig().set("blocks." + entry.configKey(), value);
        }

        saveConfig();
        loadSettings();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (shouldBlock(event.getBlock().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock fallingBlock && shouldBlock(fallingBlock.getMaterial())) {
            event.setCancelled(true);
            fallingBlock.remove();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof FallingBlock fallingBlock && shouldBlock(fallingBlock.getMaterial())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof AntiGravityHolder holder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player) || event.getClickedInventory() == null) {
            return;
        }

        int slot = event.getRawSlot();

        if (slot < 0 || slot >= GUI_SIZE) {
            return;
        }

        if (slot == CLOSE_SLOT) {
            player.closeInventory();
            return;
        }

        if (slot == PREVIOUS_SLOT) {
            openGui(player, holder.page() - 1);
            return;
        }

        if (slot == NEXT_SLOT) {
            openGui(player, holder.page() + 1);
            return;
        }

        if (slot == ENABLE_ALL_SLOT) {
            setAllBlocks(true);
            player.sendMessage(message("block-status").replace("%block%", "Tüm bloklar").replace("%status%", "Açık"));
            openGui(player, holder.page());
            return;
        }

        if (slot == DISABLE_ALL_SLOT) {
            setAllBlocks(false);
            player.sendMessage(message("block-status").replace("%block%", "Tüm bloklar").replace("%status%", "Kapalı"));
            openGui(player, holder.page());
            return;
        }

        int contentIndex = getContentIndex(slot);

        if (contentIndex == -1) {
            return;
        }

        List<BlockEntry> blocks = getConfigBlocks();
        int blockIndex = holder.page() * ITEMS_PER_PAGE + contentIndex;

        if (blockIndex >= blocks.size()) {
            return;
        }

        BlockEntry entry = blocks.get(blockIndex);
        boolean newValue = !getConfig().getBoolean("blocks." + entry.configKey(), false);

        getConfig().set("blocks." + entry.configKey(), newValue);
        saveConfig();
        loadSettings();

        player.sendMessage(message("block-status")
                .replace("%block%", entry.material().name())
                .replace("%status%", newValue ? "Açık" : "Kapalı"));
        openGui(player, holder.page());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof AntiGravityHolder) {
            event.setCancelled(true);
        }
    }

    private int getContentIndex(int slot) {
        for (int index = 0; index < CONTENT_SLOTS.length; index++) {
            if (CONTENT_SLOTS[index] == slot) {
                return index;
            }
        }

        return -1;
    }

    private String message(String path) {
        String value = getConfig().getString("messages." + path, "");
        return color(value);
    }

    private String color(String value) {
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    private record BlockEntry(String configKey, Material material) {
    }

    private record AntiGravityHolder(int page) implements InventoryHolder {

        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private final class AntiGravityCommand implements BasicCommand {

        @Override
        public void execute(CommandSourceStack source, String[] args) {
            CommandSender sender = source.getSender();

            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission(RELOAD_PERMISSION)) {
                    sender.sendMessage(message("no-permission"));
                    return;
                }

                loadSettings();
                sender.sendMessage(message("reload"));
                return;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("gui")) {
                if (!sender.hasPermission(GUI_PERMISSION)) {
                    sender.sendMessage(message("no-permission"));
                    return;
                }

                if (!(sender instanceof Player player)) {
                    sender.sendMessage(color("&b&laAntiGravity &8» &cBu komut sadece oyuncular tarafından kullanılabilir."));
                    return;
                }

                openGui(player, 0);
                player.sendMessage(message("gui-open"));
                return;
            }

            sender.sendMessage(color("&b&laAntiGravity &8» &fKullanım: &b/aantigravity reload &7| &b/aantigravity gui"));
        }

        @Override
        public Collection<String> suggest(CommandSourceStack source, String[] args) {
            if (args.length > 1) {
                return Collections.emptyList();
            }

            String current = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            List<String> suggestions = new ArrayList<>();

            if (source.getSender().hasPermission(RELOAD_PERMISSION) && "reload".startsWith(current)) {
                suggestions.add("reload");
            }

            if (source.getSender().hasPermission(GUI_PERMISSION) && "gui".startsWith(current)) {
                suggestions.add("gui");
            }

            return suggestions;
        }
    }
}
