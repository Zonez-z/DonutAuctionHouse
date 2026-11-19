package com.zonez.donutah;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Local-only auction-house mockup. It does not connect to DonutSMP or any server economy. */
public final class AuctionScreen extends Screen {
    private enum Filter { ALL, BLOCKS, BUILDING, DECORATION, MISC }
    private enum Sort { RECENT, LOWEST, HIGHEST }
    private record Listing(ItemStack stack, int price, String seller, long listedAt) {}

    private final List<Listing> all = new ArrayList<>();
    private String search;
    private Filter filter = Filter.ALL;
    private Sort sort = Sort.RECENT;
    private int page = 0;
    private Listing selected;
    private boolean confirm;

    public AuctionScreen(String initialSearch) {
        super(Component.literal("Auction House"));
        this.search = initialSearch == null || initialSearch.equals("regear") ? "" : initialSearch;
        seed();
    }

    private void seed() {
        add(Items.DIAMOND_BLOCK, 12500, "SkyBuilder");
        add(Items.EMERALD_BLOCK, 8500, "CraftKing");
        add(Items.IRON_BLOCK, 2200, "Miner_42");
        add(Items.GOLD_BLOCK, 3100, "GoldRush");
        add(Items.COPPER_BLOCK, 900, "CopperCat");
        add(Items.OBSIDIAN, 180, "NetherFox");
        add(Items.CRYING_OBSIDIAN, 260, "NetherFox");
        add(Items.WHITE_TERRACOTTA, 45, "BuildMaster");
        add(Items.ORANGE_TERRACOTTA, 45, "BuildMaster");
        add(Items.RED_TERRACOTTA, 50, "BuildMaster");
        add(Items.WHITE_CONCRETE, 55, "PixelArt");
        add(Items.ORANGE_CONCRETE, 55, "PixelArt");
        add(Items.RED_CONCRETE, 60, "PixelArt");
        add(Items.SHULKER_BOX, 2500, "BoxTrader");
        add(Items.WHITE_SHULKER_BOX, 2800, "BoxTrader");
        add(Items.BARREL, 150, "StoragePro");
        add(Items.CHEST, 100, "StoragePro");
        add(Items.HOPPER, 450, "RedstoneLab");
        add(Items.ANVIL, 700, "Smithy");
        add(Items.CRAFTING_TABLE, 35, "Crafter");
        add(Items.BOOKSHELF, 120, "Librarian");
        add(Items.GLASS, 25, "Builder");
        add(Items.SEA_LANTERN, 400, "Glowstone");
        add(Items.GLOWSTONE, 160, "Glowstone");
        add(Items.AMETHYST_BLOCK, 220, "CrystalShop");
        add(Items.PRISMARINE, 90, "Oceanic");
        add(Items.DARK_PRISMARINE, 130, "Oceanic");
        add(Items.QUARTZ_BLOCK, 100, "NetherFox");
        add(Items.SMOOTH_STONE, 35, "Builder");
        add(Items.MOSS_BLOCK, 80, "GreenThumb");
        add(Items.BONE_BLOCK, 65, "FossilFan");
        add(Items.HONEY_BLOCK, 140, "BeeHappy");
        add(Items.SLIME_BLOCK, 180, "RedstoneLab");
        add(Items.CAKE, 75, "Baker");
        add(Items.GOLDEN_CARROT, 18, "Farmer");
        add(Items.BREAD, 8, "Farmer");
        add(Items.COOKED_BEEF, 14, "Farmer");
        add(Items.ENDER_CHEST, 900, "StoragePro");
        add(Items.LANTERN, 75, "Builder");
        add(Items.CHAIN, 45, "Smithy");
        add(Items.FLOWER_POT, 30, "Decorator");
        add(Items.PINK_TULIP, 12, "Decorator");
        add(Items.SCAFFOLDING, 22, "Builder");
        add(Items.TARGET, 110, "RedstoneLab");
    }

    private void add(Item item, int price, String seller) {
        all.add(new Listing(new ItemStack(item), price, seller, System.nanoTime() + all.size()));
    }

    private List<Listing> visible() {
        List<Listing> result = new ArrayList<>();
        String q = search.toLowerCase(Locale.ROOT).trim();
        for (Listing listing : all) {
            Item item = listing.stack().getItem();
            String id = BuiltInRegistries.ITEM.getKey(item).toString().toLowerCase(Locale.ROOT);
            String name = listing.stack().getHoverName().getString().toLowerCase(Locale.ROOT);
            boolean matches = q.isEmpty() || id.contains(q) || name.contains(q) || distance(id, q) <= Math.max(2, q.length() / 3);
            if (!matches) continue;
            if (filter == Filter.BLOCKS && !BuiltInRegistries.ITEM.getKey(item).getPath().contains("block")) continue;
            if (filter == Filter.BUILDING && !(id.contains("terracotta") || id.contains("concrete") || id.contains("stone") || id.contains("wood") || id.contains("brick"))) continue;
            if (filter == Filter.DECORATION && !(id.contains("flower") || id.contains("lantern") || id.contains("glass") || id.contains("pot") || id.contains("bookshelf"))) continue;
            if (filter == Filter.MISC && (id.contains("block") || id.contains("terracotta") || id.contains("concrete"))) continue;
            result.add(listing);
        }
        if (sort == Sort.LOWEST) result.sort(Comparator.comparingInt(Listing::price));
        else if (sort == Sort.HIGHEST) result.sort(Comparator.comparingInt(Listing::price).reversed());
        else result.sort(Comparator.comparingLong(Listing::listedAt).reversed());
        return result;
    }

    private int distance(String a, String b) {
        int[] prev = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) prev[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            int[] cur = new int[b.length() + 1]; cur[0] = i;
            for (int j = 1; j <= b.length(); j++) cur[j] = Math.min(Math.min(cur[j-1] + 1, prev[j] + 1), prev[j-1] + (a.charAt(i-1) == b.charAt(j-1) ? 0 : 1));
            prev = cur;
        }
        return prev[b.length()];
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        int left = (width - 440) / 2, top = (height - 230) / 2;
        g.fill(0, 0, width, height, 0x99000000);
        panel(g, left, top, 440, 230);
        g.drawCenteredString(font, confirm ? "ᴄᴏɴꜰɪʀᴍ ᴘᴜʀᴄʜᴀꜱᴇ" : "ᴀᴜᴄᴛɪᴏɴ ʜᴏᴜꜱᴇ", width / 2, top + 8, 0xFFFF8A32);
        if (confirm) { renderConfirm(g, left, top); super.render(g, mouseX, mouseY, delta); return; }

        List<Listing> listings = visible();
        int pages = Math.max(1, (listings.size() + 44) / 45);
        page = Math.min(page, pages - 1);
        for (int i = 0; i < 45; i++) {
            int x = left + 10 + (i % 9) * 46, y = top + 28 + (i / 9) * 34;
            slot(g, x, y, mouseX, mouseY);
            int index = page * 45 + i;
            if (index < listings.size()) {
                Listing listing = listings.get(index);
                g.renderItem(listing.stack(), x + 5, y + 4);
                if (mouseX >= x && mouseX <= x + 40 && mouseY >= y && mouseY <= y + 30) {
                    g.renderTooltip(font, List.of(listing.stack().getHoverName(), Component.literal("§6Price: §f" + listing.price() + " coins"), Component.literal("§7Seller: " + listing.seller()), Component.literal("§eClick to view")), java.util.Optional.empty(), mouseX, mouseY);
                }
            }
        }
        int fy = top + 198;
        String[] controls = {"‹", "", filter.name(), sort.name(), "↻", "Search", "Your Items", "", "›"};
        for (int i = 0; i < 9; i++) {
            int x = left + 10 + i * 46;
            slot(g, x, fy, mouseX, mouseY);
            if (!controls[i].isEmpty()) g.drawCenteredString(font, controls[i], x + 20, fy + 11, i == 2 || i == 3 ? 0xFFFFA04A : 0xFFFFFFFF);
        }
        g.drawString(font, "Page " + (page + 1) + "/" + pages + "   Listings: " + listings.size(), left + 10, top + 188, 0xFFB8B8B8);
        super.render(g, mouseX, mouseY, delta);
    }

    private void renderConfirm(GuiGraphics g, int left, int top) {
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++) {
            int x = left + 110 + col * 24, y = top + 65 + row * 24;
            slot(g, x, y, -1, -1);
            if (selected != null && row == 1 && col == 4) g.renderItem(selected.stack(), x + 3, y + 3);
            if (col <= 2) g.fill(x + 2, y + 2, x + 22, y + 22, 0x5533CC55);
            if (col >= 6) g.fill(x + 2, y + 2, x + 22, y + 22, 0x55DD3333);
        }
        g.drawCenteredString(font, selected == null ? "" : selected.stack().getHoverName(), width / 2, top + 45, 0xFFFFFFFF);
        g.drawCenteredString(font, "Confirm", left + 158, top + 151, 0xFF55FF66);
        g.drawCenteredString(font, "Cancel", left + 282, top + 151, 0xFFFF6666);
        g.drawCenteredString(font, "This is a local singleplayer simulation", width / 2, top + 180, 0xFFAAAAAA);
    }

    private void panel(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x - 3, y - 3, x + w + 3, y + h + 3, 0xFF171717);
        g.fill(x, y, x + w, y + h, 0xFFC6C6C6);
        g.fill(x + 3, y + 3, x + w - 3, y + h - 3, 0xFF373737);
    }
    private void slot(GuiGraphics g, int x, int y, int mx, int my) {
        g.fill(x, y, x + 40, y + 30, 0xFF242424);
        g.fill(x + 1, y + 1, x + 39, y + 29, 0xFF8B8B8B);
        g.fill(x + 2, y + 2, x + 38, y + 28, 0xFF555555);
        if (mx >= x && mx <= x + 40 && my >= y && my <= y + 30) g.fill(x + 2, y + 2, x + 38, y + 28, 0x55777777);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int left = (width - 440) / 2, top = (height - 230) / 2;
        if (confirm) {
            if (mouseY >= top + 60 && mouseY <= top + 145) {
                if (mouseX >= left + 110 && mouseX <= left + 182) {
                    if (selected != null && Minecraft.getInstance().player != null) Minecraft.getInstance().player.getInventory().add(selected.stack().copy());
                    selected = null; confirm = false; return true;
                }
                if (mouseX >= left + 254 && mouseX <= left + 326) { selected = null; confirm = false; return true; }
            }
            return true;
        }
        if (mouseY >= top + 28 && mouseY < top + 198) {
            int col = (int)(mouseX - (left + 10)) / 46, row = (int)(mouseY - (top + 28)) / 34;
            if (col >= 0 && col < 9 && row >= 0 && row < 5) {
                int index = page * 45 + row * 9 + col;
                List<Listing> list = visible();
                if (index >= 0 && index < list.size()) { selected = list.get(index); confirm = true; }
                return true;
            }
        }
        if (mouseY >= top + 198 && mouseY <= top + 230) {
            int col = (int)(mouseX - (left + 10)) / 46;
            switch (col) {
                case 0 -> page = Math.max(0, page - 1);
                case 2 -> filter = Filter.values()[(filter.ordinal() + 1) % Filter.values().length];
                case 3 -> sort = Sort.values()[(sort.ordinal() + 1) % Sort.values().length];
                case 4 -> page = 0;
                case 5 -> { search = ""; page = 0; }
                case 8 -> page++;
            }
            return true;
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { onClose(); return true; }
        if (keyCode == 259 && !search.isEmpty()) { search = search.substring(0, search.length() - 1); page = 0; return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override public boolean charTyped(char codePoint, int modifiers) { if (!confirm && !Character.isISOControl(codePoint)) { search += codePoint; page = 0; return true; } return super.charTyped(codePoint, modifiers); }
    @Override public boolean isPauseScreen() { return false; }
}
