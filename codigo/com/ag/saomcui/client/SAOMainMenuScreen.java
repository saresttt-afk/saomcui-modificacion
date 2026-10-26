/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.GenericDirtMessageScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.client.gui.screens.inventory.InventoryScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.Container
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.TransientCraftingContainer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.Level
 *  org.joml.Matrix4f
 */
package com.ag.saomcui.client;

import com.ag.saomcui.client.SAOChatManager;
import com.ag.saomcui.client.SAOClientPreferences;
import com.ag.saomcui.client.SAODrawingUtils;
import com.ag.saomcui.client.SAOFloatMotion;
import com.ag.saomcui.client.SAOText;
import com.ag.saomcui.client.SAOUISettingsScreen;
import com.ag.saomcui.client.SAOUISounds;
import com.ag.saomcui.client.SkillEditBridge;
import com.ag.saomcui.client.WebTextureHelper;
import com.ag.saomcui.client.map.SAOMapScreen;
import com.ag.saomcui.config.SAOConfig;
import com.ag.saomcui.inventory.SAOInventory;
import com.ag.saomcui.network.ChatPacket;
import com.ag.saomcui.network.CraftActionPacket;
import com.ag.saomcui.network.InviteRequestPacket;
import com.ag.saomcui.network.ItemActionPacket;
import com.ag.saomcui.network.MenuActivityPacket;
import com.ag.saomcui.network.PublicMenuView;
import com.ag.saomcui.network.SAOPacketHandler;
import com.ag.saomcui.network.SocialPacket;
import com.ag.saomcui.social.SocialManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

public class SAOMainMenuScreen
extends Screen {
    private final SAOFloatMotion floatMotion = new SAOFloatMotion();
    private long motionTime;
    private boolean pointerHeld;
    private float floatX;
    private float floatY;
    private long pageOpenTime;
    private long messagesOpenTime;
    private int animatedChatTab = -1;
    private long publicClickTime;
    private final long[] columnOpened = new long[3];
    private int animatedRoot = -2;
    private int animatedSub = -2;
    private int animatedLeaf = -2;
    private String hoveredFullLabel;
    private Screen mapContent;
    private boolean openMapOnInit;
    private float mapPanelScale = 1.0f;
    private int mapPanelX;
    private int mapPanelY;
    public static boolean openAs3x3 = false;
    private ItemStack[] craftingGridStacks = new ItemStack[9];
    private ItemStack cursorStack = ItemStack.f_41583_;
    private Map<Integer, Integer> craftingReservedCounts = new HashMap<Integer, Integer>();
    private int craftingInvScroll = 0;
    private ItemStack[] recipeSnapshot;
    private ItemStack recipePreview = ItemStack.f_41583_;
    private long recipeTick = Long.MIN_VALUE;
    private int recipeSize;
    private final List<SAOMenuNode> rootNodes = new ArrayList<SAOMenuNode>();
    private int selectedIndex = -1;
    private int itemsCategoryIndex = 0;
    private int subSelectedIndex = -1;
    private int subSubSelectedIndex = -1;
    private int sssSelectedIndex = -1;
    private int chatTabIndex = 0;
    private String privateChatTarget = "";
    private boolean isTypingChat = false;
    private boolean isDraggingCrafting = false;
    private int dragButton = -1;
    private Set<Integer> dragSlots = new HashSet<Integer>();
    private boolean showingConfirm = false;
    private String confirmTitle = "";
    private String confirmContext = "";
    private Runnable confirmAction = null;
    private int confirmSelectedIndex = 0;
    private List<SAOMenuNode> currentSubNodes = null;
    private List<SAOMenuNode> currentSubSubNodes = null;
    private List<SAOMenuNode> currentSSSNodes = null;
    private long openTime;
    private boolean closing = false;
    private long closeTime;
    private long confirmOpenTime;
    private EditBox socialInput;
    private boolean inputForName = true;
    private long lastNodeRefresh = 0L;
    private float cachedSlideY = 0.0f;
    private int friendScrollOffset = 0;

    public PublicMenuView publicView() {
        double mx = this.f_96541_.f_91067_.m_91589_() * (double)this.f_96543_ / (double)this.f_96541_.m_91268_().m_85443_() - (double)this.floatX;
        double my = this.f_96541_.f_91067_.m_91594_() * (double)this.f_96544_ / (double)this.f_96541_.m_91268_().m_85444_() - (double)this.floatY;
        float scale = this.menuScale();
        return new PublicMenuView(this.selectedIndex, this.subSelectedIndex, this.subSubSelectedIndex, this.sssSelectedIndex, (int)(mx / (double)scale - (double)((float)this.f_96543_ / scale / 2.0f - 60.0f)), (int)(my / (double)scale - (double)((float)this.f_96544_ / scale / 2.0f) - (double)this.cachedSlideY), this.pointerHeld || Util.m_137550_() - this.publicClickTime < 220L).publicOnly(this.activitySection());
    }

    private void revealRow(GuiGraphics g, float x, float y, float width, float height, float scale, int depth, int row) {
        float p = SAOFloatMotion.ease((float)(Util.m_137550_() - this.columnOpened[depth] - (long)(Math.min(row, 6) * 12)) / 220.0f);
        g.m_280588_(Math.round(this.floatX + x * scale), Math.round(this.floatY + y * scale), Math.round(this.floatX + (x + width * p) * scale), Math.round(this.floatY + (y + height) * scale));
    }

    public MenuActivityPacket.Section activitySection() {
        MenuActivityPacket.Section section = MenuActivityPacket.Section.MENU;
        if (this.closing) {
            return MenuActivityPacket.Section.CLOSED;
        }
        if (this.mapContent instanceof SAOUISettingsScreen) {
            return MenuActivityPacket.Section.SETTINGS;
        }
        if (this.mapContent != null) {
            return MenuActivityPacket.Section.MAP;
        }
        if (this.isCraftingActive()) {
            return MenuActivityPacket.Section.CRAFTING;
        }
        return switch (this.selectedIndex) {
            case 0 -> MenuActivityPacket.Section.INVENTORY;
            case 1 -> MenuActivityPacket.Section.SOCIAL;
            case 2 -> MenuActivityPacket.Section.MESSAGES;
            case 3 -> MenuActivityPacket.Section.MAP;
            case 4 -> MenuActivityPacket.Section.SETTINGS;
            default -> section;
        };
    }

    private String menuLabel(String label, int availableWidth, boolean hovered) {
        String fitted = SAOText.fit(label, availableWidth, arg_0 -> ((Font)this.f_96547_).m_92895_(arg_0));
        if (hovered && !fitted.equals(label)) {
            this.hoveredFullLabel = label;
        }
        return fitted;
    }

    public static SAOMainMenuScreen forMap() {
        SAOMainMenuScreen menu = new SAOMainMenuScreen();
        menu.openMapOnInit = true;
        return menu;
    }

    public void setMapContent(Screen page) {
        this.pageOpenTime = Util.m_137550_();
        if (this.mapContent != null) {
            this.mapContent.m_7861_();
        }
        this.mapContent = page;
        this.selectedIndex = page == null ? -1 : (page instanceof SAOUISettingsScreen ? 4 : 3);
        this.resetIndices();
        if (page != null) {
            this.layoutMap();
        }
    }

    private void layoutMap() {
        this.positionMap();
        this.mapContent.m_6575_(this.f_96541_, 350, 250);
    }

    private void positionMap() {
        this.mapPanelScale = this.menuScale();
        float rootX = (float)this.f_96543_ / this.mapPanelScale / 2.0f - 60.0f - (this.itemsPanelActive() ? 170.0f : 0.0f);
        this.mapPanelX = Math.round((rootX + 50.0f) * this.mapPanelScale);
        this.mapPanelY = Math.round((float)this.f_96544_ / 2.0f - 125.0f * this.mapPanelScale + this.cachedSlideY * this.mapPanelScale);
    }

    private void renderMapContent(GuiGraphics g, int mx, int my, float dt) {
        this.positionMap();
        Screen screen = this.mapContent;
        if (screen instanceof SAOMapScreen) {
            SAOMapScreen map = (SAOMapScreen)screen;
            map.setPanelTransform(Math.round((float)this.mapPanelX + this.floatX), Math.round((float)this.mapPanelY + this.floatY), this.mapPanelScale);
        }
        float reveal = SAOFloatMotion.ease((float)(Util.m_137550_() - this.pageOpenTime) / 240.0f);
        int midY = Math.round((float)this.mapPanelY + this.floatY + 125.0f * this.mapPanelScale);
        g.m_280588_(Math.round((float)this.mapPanelX + this.floatX - 26.0f * this.mapPanelScale), Math.round((float)midY - 128.0f * this.mapPanelScale * reveal), Math.round((float)this.mapPanelX + this.floatX + 355.0f * this.mapPanelScale), Math.round((float)midY + 128.0f * this.mapPanelScale * reveal));
        g.m_280168_().m_85836_();
        g.m_280168_().m_252880_((float)this.mapPanelX, (float)this.mapPanelY, 0.0f);
        g.m_280168_().m_85841_(this.mapPanelScale, this.mapPanelScale, 1.0f);
        g.m_280509_(4, 5, 354, 255, 0x18000000);
        g.m_280509_(2, 3, 352, 253, 0x18000000);
        SAODrawingUtils.fillTriangle(g.m_280168_().m_85850_().m_252922_(), -25.0f, 125.0f, 0.0f, 115.0f, 0.0f, 135.0f, -855638017);
        this.mapContent.m_88315_(g, (int)((float)(mx - this.mapPanelX) / this.mapPanelScale), (int)((float)(my - this.mapPanelY) / this.mapPanelScale), dt);
        g.m_280168_().m_85849_();
        g.m_280618_();
    }


    private boolean itemsPanelActive() {
        return this.selectedIndex == 0 && this.subSelectedIndex == 1;
    }

    private static final String[] ITEM_CATEGORY_NAMES = {
        "Weapons", "Armor", "Tools", "Food", "Potions", "Relics", "Materials", "Misc."
    };
    private static final String[] ITEM_CATEGORY_ICONS = {
        "\u2694", "\u26e8", "\u26cf", "\ud83c\udf56", "\ud83e\uddea", "\ud83d\udc8e", "\ud83e\udea8", "\ud83d\udce6"
    };
    private static final int[] ITEM_CATEGORY_SIZES = {4, 4, 4, 3, 5, 4, 6, 6};

    private int itemCategoryStart(int category) {
        int start = 0;
        for (int i = 0; i < category; ++i) start += ITEM_CATEGORY_SIZES[i];
        return start;
    }

    private void renderItemsCategoryPanel(GuiGraphics g, int mouseX, int mouseY, float scale, float w, float h, float rootX) {
        if (!this.itemsPanelActive()) return;
        float panelX = rootX + 37.0f + 160.0f + 20.0f + 10.0f;
        float panelY = h / 2.0f - 158.0f + this.cachedSlideY;
        float panelW = 330.0f;
        float panelH = 316.0f;
        g.m_280163_(new ResourceLocation("saomcui:textures/gui/window_blank.png"), (int)panelX, (int)panelY, 0.0f, 0.0f, (int)panelW, (int)panelH, (int)panelW, (int)panelH);
        g.m_280509_((int)panelX + 1, (int)panelY + 1, (int)(panelX + panelW - 1), (int)(panelY + 31), -1512724);
        g.m_280056_(this.f_96547_, "Items", (int)panelX + 12, (int)panelY + 11, -13619152, false);
        float tabsX = panelX + 8.0f;
        float tabsY = panelY + 39.0f;
        for (int i = 0; i < ITEM_CATEGORY_NAMES.length; ++i) {
            float y = tabsY + i * 33.0f;
            boolean active = i == this.itemsCategoryIndex;
            boolean hovered = mouseX / scale >= tabsX && mouseX / scale <= tabsX + 116.0f && mouseY / scale >= y && mouseY / scale <= y + 29.0f;
            g.m_280509_((int)tabsX, (int)y, (int)(tabsX + 116.0f), (int)(y + 29.0f), active ? -1336576 : (hovered ? -855638017 : -1711276033));
            g.m_280056_(this.f_96547_, ITEM_CATEGORY_ICONS[i] + " " + ITEM_CATEGORY_NAMES[i], (int)tabsX + 6, (int)y + 10, active ? -1 : -13619152, false);
        }
        float slotsX = panelX + 137.0f;
        float slotsY = panelY + 47.0f;
        g.m_280056_(this.f_96547_, ITEM_CATEGORY_NAMES[this.itemsCategoryIndex], (int)slotsX, (int)panelY + 35, -13619152, false);
        int categorySize = ITEM_CATEGORY_SIZES[this.itemsCategoryIndex];
        int start = itemCategoryStart(this.itemsCategoryIndex);
        for (int i = 0; i < categorySize; ++i) {
            int col = i % 3;
            int row = i / 3;
            int x = (int)slotsX + col * 58;
            int y = (int)slotsY + row * 58;
            g.m_280509_(x, y, x + 48, y + 48, -3748399);
            g.m_280509_(x + 2, y + 2, x + 46, y + 46, -1446160);
            if (this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
                final int slot = start + i;
                this.f_96541_.f_91074_.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
                    if (slot >= inv.getSlots().size()) return;
                    SAOInventory.SAOStack stack = inv.getStackInSlot(slot);
                    if (stack.count <= 0 || stack.item.m_41619_()) return;
                    ItemStack shown = stack.item.m_41777_();
                    shown.m_41764_(stack.count);
                    g.m_280480_(shown, x + 14, y + 14);
                    g.m_280370_(this.f_96547_, shown, x + 14, y + 14);
                });
            }
            if (mouseX / scale >= x && mouseX / scale <= x + 48 && mouseY / scale >= y && mouseY / scale <= y + 48) {
                g.m_280509_(x, y, x + 48, y + 48, 0x55FFFFFF);
            }
        }
        g.m_280056_(this.f_96547_, "Click: equip  |  Right-click: drop", (int)panelX + 12, (int)(panelY + panelH - 18), -11182749, false);
    }

    private boolean handleItemsCategoryClick(double mouseX, double mouseY, int button, float scale, float w, float h, float rootX) {
        if (!this.itemsPanelActive()) return false;
        float panelX = rootX + 37.0f + 160.0f + 20.0f + 10.0f;
        float panelY = h / 2.0f - 158.0f + this.cachedSlideY;
        if (mouseX / scale < panelX || mouseX / scale > panelX + 330.0f || mouseY / scale < panelY || mouseY / scale > panelY + 316.0f) return false;
        float tabsX = panelX + 8.0f;
        float tabsY = panelY + 39.0f;
        for (int i = 0; i < ITEM_CATEGORY_NAMES.length; ++i) {
            float y = tabsY + i * 33.0f;
            if (mouseX / scale >= tabsX && mouseX / scale <= tabsX + 116.0f && mouseY / scale >= y && mouseY / scale <= y + 29.0f) {
                this.itemsCategoryIndex = i;
                this.playSound("ui.click");
                return true;
            }
        }
        float slotsX = panelX + 137.0f;
        float slotsY = panelY + 47.0f;
        for (int i = 0; i < ITEM_CATEGORY_SIZES[this.itemsCategoryIndex]; ++i) {
            int col = i % 3;
            int row = i / 3;
            float x = slotsX + col * 58.0f;
            float y = slotsY + row * 58.0f;
            if (mouseX / scale < x || mouseX / scale > x + 48.0f || mouseY / scale < y || mouseY / scale > y + 48.0f) continue;
            int slot = itemCategoryStart(this.itemsCategoryIndex) + i;
            if (this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
                final int itemSlot = slot;
                this.f_96541_.f_91074_.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
                    if (itemSlot >= inv.getSlots().size()) return;
                    SAOInventory.SAOStack stack = inv.getStackInSlot(itemSlot);
                    if (stack.count <= 0 || stack.item.m_41619_()) return;
                    if (button == 1) {
                        String name = stack.item.m_41786_().getString() + " x" + stack.count;
                        this.showConfirmation("Drop " + name + "?", "Are you sure you want to drop this item?", () -> {
                            SAOPacketHandler.sendToServer(new ItemActionPacket(itemSlot, ItemActionPacket.ActionType.DROP_SAO));
                            this.refreshNodes();
                        });
                    } else {
                        SAOPacketHandler.sendToServer(new ItemActionPacket(itemSlot, ItemActionPacket.ActionType.EQUIP_SAO));
                        this.refreshNodes();
                    }
                });
            }
            this.playSound("ui.click");
            return true;
        }
        return true;
    }

    private float menuScale() {
        float requested = 1.3f * SAOClientPreferences.scale() / (float)this.f_96541_.m_91268_().m_85449_();
        return Math.min(requested, Math.min((float)this.f_96543_ / 1000.0f, (float)this.f_96544_ / 310.0f));
    }

    public void m_7861_() {
        if (this.mapContent != null) {
            this.mapContent.m_7861_();
        }
    }

    public void m_86600_() {
        super.m_86600_();
        if (this.mapContent != null) {
            this.mapContent.m_86600_();
        }
    }

    public boolean m_5534_(char codePoint, int modifiers) {
        return this.mapContent != null ? this.mapContent.m_5534_(codePoint, modifiers) : super.m_5534_(codePoint, modifiers);
    }

    public SAOMainMenuScreen() {
        super((Component)Component.m_237113_((String)"SAO Menu"));
    }

    public static void showExternalInvite(String inviter, InviteRequestPacket.Type type, String extra) {
        Minecraft mc = Minecraft.m_91087_();
        if (!(mc.f_91080_ instanceof SAOMainMenuScreen)) {
            SAOMainMenuScreen screen = new SAOMainMenuScreen();
            mc.m_91152_((Screen)screen);
            screen.triggerInvitePrompt(inviter, type, extra);
        } else {
            ((SAOMainMenuScreen)mc.f_91080_).triggerInvitePrompt(inviter, type, extra);
        }
    }

    private void triggerInvitePrompt(String inviter, InviteRequestPacket.Type type, String extra) {
        String title = "Invite";
        Object msg = "Accept?";
        if (type == InviteRequestPacket.Type.PARTY) {
            title = "Party Invite";
            msg = "Join " + inviter + "'s party?";
        } else if (type == InviteRequestPacket.Type.GUILD) {
            title = "Guild Invite";
            msg = "Join the guild '" + extra + "'?";
        } else if (type == InviteRequestPacket.Type.FRIEND) {
            title = "Friend Request";
            msg = "Accept friend request from " + inviter + "?";
        }
        Runnable action = () -> SAOPacketHandler.sendToServer(new SocialPacket(type == InviteRequestPacket.Type.PARTY ? SocialPacket.ActionType.PARTY_ACCEPT : (type == InviteRequestPacket.Type.GUILD ? SocialPacket.ActionType.GUILD_ACCEPT : SocialPacket.ActionType.FRIEND_ACCEPT), type == InviteRequestPacket.Type.FRIEND ? inviter : "", ""));
        this.showConfirmation(title, (String)msg, action);
    }

    protected void m_7856_() {
        this.pointerHeld = false;
        this.motionTime = 0L;
        if (this.mapContent != null) {
            this.layoutMap();
        }
        this.openTime = Util.m_137550_();
        this.closing = false;
        this.playSound("ui.open");
        this.refreshNodes();
        this.socialInput = new EditBox(this.f_96547_, this.f_96543_ / 2 - 80, this.f_96544_ / 2, 160, 20, (Component)Component.m_237113_((String)"Input"));
        this.socialInput.m_94194_(false);
        this.socialInput.m_94186_(true);
        this.socialInput.m_94199_(256);
        this.m_7787_((GuiEventListener)this.socialInput);
        if (openAs3x3) {
            this.refreshNodes();
            if (this.rootNodes.size() > 0) {
                this.selectedIndex = 0;
                this.currentSubNodes = this.rootNodes.get(0).getSubNodes();
                if (this.currentSubNodes.size() > 2) {
                    this.subSelectedIndex = 2;
                    this.currentSubSubNodes = this.currentSubNodes.get(2).getSubNodes();
                }
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.craftingGridStacks[i] = ItemStack.f_41583_;
        }
        if (this.openMapOnInit) {
            this.openMapOnInit = false;
            this.setMapContent(new SAOMapScreen(this));
        }
    }

    private boolean isCraftingActive() {
        List<SAOMenuNode> subNodes;
        if (this.selectedIndex >= 0 && this.selectedIndex < this.rootNodes.size() && this.rootNodes.get(this.selectedIndex) != null && (subNodes = this.rootNodes.get(this.selectedIndex).getSubNodes()) != null && this.subSelectedIndex >= 0 && this.subSelectedIndex < subNodes.size()) {
            return "Crafting".equals(subNodes.get(this.subSelectedIndex).getLabel());
        }
        return false;
    }

    private ItemStack simulateCraftingClient() {
        int i;
        boolean same;
        if (this.f_96541_ == null || this.f_96541_.f_91074_ == null || this.f_96541_.f_91073_ == null) {
            return ItemStack.f_41583_;
        }
        int size = openAs3x3 ? 3 : 2;
        long tick = this.f_96541_.f_91073_.m_46467_();
        boolean bl = same = this.recipeSnapshot != null && this.recipeSize == size && this.recipeTick == tick;
        if (same) {
            for (i = 0; i < size * size; ++i) {
                ItemStack stack;
                ItemStack itemStack = stack = this.craftingGridStacks[i] == null ? ItemStack.f_41583_ : this.craftingGridStacks[i];
                if (ItemStack.m_41728_((ItemStack)stack, (ItemStack)this.recipeSnapshot[i])) continue;
                same = false;
                break;
            }
        }
        if (same) {
            return this.recipePreview.m_41777_();
        }
        this.recipeTick = tick;
        this.recipeSize = size;
        this.recipeSnapshot = new ItemStack[size * size];
        for (i = 0; i < size * size; ++i) {
            this.recipeSnapshot[i] = this.craftingGridStacks[i] == null ? ItemStack.f_41583_ : this.craftingGridStacks[i].m_41777_();
        }
        TransientCraftingContainer container = new TransientCraftingContainer(new AbstractContainerMenu(null, -1){

            public ItemStack m_7648_(Player p, int slot) {
                return ItemStack.f_41583_;
            }

            public boolean m_6875_(Player p) {
                return true;
            }
        }, size, size);
        for (int i2 = 0; i2 < size * size; ++i2) {
            container.m_6836_(i2, this.craftingGridStacks[i2] == null ? ItemStack.f_41583_ : this.craftingGridStacks[i2]);
        }
        Optional recipe = this.f_96541_.f_91073_.m_7465_().m_44015_(RecipeType.f_44107_, (Container)container, (Level)this.f_96541_.f_91073_);
        if (recipe.isPresent()) {
            this.recipePreview = ((CraftingRecipe)recipe.get()).m_5874_((Container)container, this.f_96541_.f_91073_.m_9598_());
            return this.recipePreview.m_41777_();
        }
        this.recipePreview = ItemStack.f_41583_;
        return ItemStack.f_41583_;
    }

    private void refreshNodes() {
        this.rootNodes.clear();
        SAOMenuNode profile = new SAOMenuNode("Profile", "saomcui:textures/gui/icons/ringed_man.png", "saomcui:textures/gui/icons/ringed_man_on.png");
        profile.addSub(new SAOMenuNode("Equipment", "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png", () -> {
            ArrayList<SAOMenuNode> items = new ArrayList<SAOMenuNode>();
            if (this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
                for (int i = 0; i < 9; ++i) {
                    ItemStack stack = this.f_96541_.f_91074_.m_150109_().m_8020_(i);
                    if (stack.m_41619_()) continue;
                    int slot = i;
                    String itemName = stack.m_41786_().getString() + " x" + stack.m_41613_();
                    SAOMenuNode itemNode = new SAOMenuNode(itemName, "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png");
                    itemNode.addSub(new SAOMenuNode("Stow in Items", "saomcui:textures/gui/under_icons/logout.png", "saomcui:textures/gui/under_icons/logout_on.png", () -> {
                        SAOPacketHandler.sendToServer(new ItemActionPacket(slot, ItemActionPacket.ActionType.STOW_HOTBAR));
                        this.refreshNodes();
                    }));
                    items.add(itemNode);
                }
            }
            return items;
        }));
        profile.addSub(new SAOMenuNode("Items", "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png", () -> {
            ArrayList items = new ArrayList();
            if (this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
                this.f_96541_.f_91074_.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
                    for (int i = 0; i < inv.getSlots().size(); ++i) {
                        SAOInventory.SAOStack saoStack = inv.getStackInSlot(i);
                        if (saoStack.count <= 0 || saoStack.item.m_41619_()) continue;
                        int slot = i;
                        String itemName = saoStack.item.m_41786_().getString() + " x" + saoStack.count;
                        SAOMenuNode itemNode = new SAOMenuNode(itemName, "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png");
                        itemNode.addSub(new SAOMenuNode("Equip", "saomcui:textures/gui/icons/friends.png", "saomcui:textures/gui/icons/friends_on.png", () -> {
                            SAOPacketHandler.sendToServer(new ItemActionPacket(slot, ItemActionPacket.ActionType.EQUIP_SAO));
                            this.refreshNodes();
                        }));
                        itemNode.addSub(new SAOMenuNode("Drop", "saomcui:textures/gui/under_icons/logout.png", "saomcui:textures/gui/under_icons/logout_on.png", () -> this.showConfirmation("Drop " + itemName + "?", "Are you sure you want to drop this item?", () -> {
                            SAOPacketHandler.sendToServer(new ItemActionPacket(slot, ItemActionPacket.ActionType.DROP_SAO));
                            this.refreshNodes();
                        })));
                        itemNode.addSub(new SAOMenuNode("Destroy", "saomcui:textures/gui/under_icons/logout.png", "saomcui:textures/gui/under_icons/logout_on.png", () -> this.showConfirmation("Destroy " + itemName + "?", "This item will be permanently destroyed.", () -> {
                            SAOPacketHandler.sendToServer(new ItemActionPacket(slot, ItemActionPacket.ActionType.DESTROY_SAO));
                            this.refreshNodes();
                        })));
                        items.add(itemNode);
                    }
                });
            }
            return items;
        }));
        if (SAOConfig.enableCrafting) {
            SAOMenuNode craftingNode = new SAOMenuNode("Crafting", "saomcui:textures/gui/under_icons/equipment.png", "saomcui:textures/gui/under_icons/equipment_on.png");
            craftingNode.keepOpen = true;
            profile.addSub(craftingNode);
        }
        profile.addSub(new SAOMenuNode("Skills", "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png"));
        this.rootNodes.add(profile);
        SAOMenuNode social = new SAOMenuNode("Social", "saomcui:textures/gui/icons/ringed_men.png", "saomcui:textures/gui/icons/ringed_men_on.png");
        social.addSub(new SAOMenuNode("Nearby", "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png", () -> {
            ArrayList<SAOMenuNode> players = new ArrayList<SAOMenuNode>();
            if (this.f_96541_ != null && this.f_96541_.f_91073_ != null) {
                String myRole = SocialManager.getClientGuildRole();
                boolean canInvite = myRole != null && (myRole.equals("LEADER") || myRole.equals("OFFICER"));
                for (Player p : this.f_96541_.f_91073_.m_6907_()) {
                    if (p == this.f_96541_.f_91074_) continue;
                    String pName = p.m_7755_().getString();
                    SAOMenuNode pNode = new SAOMenuNode(pName, "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png");
                    pNode.addSub(new SAOMenuNode("Party Invite", "saomcui:textures/gui/icons/friends.png", "saomcui:textures/gui/icons/friends_on.png", () -> SAOPacketHandler.sendToServer(new SocialPacket(SocialPacket.ActionType.PARTY_INVITE, pName, ""))));
                    if (canInvite) {
                        pNode.addSub(new SAOMenuNode("Guild Invite", "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png", () -> SAOPacketHandler.sendToServer(new SocialPacket(SocialPacket.ActionType.GUILD_INVITE, pName, ""))));
                    }
                    boolean isFriend = false;
                    if (SocialManager.getClientFriends() != null) {
                        for (String f : SocialManager.getClientFriends()) {
                            if (f.split("\\|").length < 2 || !f.split("\\|")[1].equals(pName)) continue;
                            isFriend = true;
                            break;
                        }
                    }
                    if (isFriend) {
                        pNode.addSub(new SAOMenuNode("Remove Friend", "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png", () -> SAOPacketHandler.sendToServer(new SocialPacket(SocialPacket.ActionType.FRIEND_REMOVE, pName, ""))));
                    } else {
                        pNode.addSub(new SAOMenuNode("Add Friend", "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png", () -> SAOPacketHandler.sendToServer(new SocialPacket(SocialPacket.ActionType.FRIEND_ADD, pName, ""))));
                    }
                    players.add(pNode);
                }
            }
            return players;
        }));
        social.addSub(new SAOMenuNode("Friends", "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png", () -> {
            ArrayList<SAOMenuNode> friends = new ArrayList<SAOMenuNode>();
            if (SocialManager.getClientFriends() != null) {
                for (String fData : SocialManager.getClientFriends()) {
                    String[] parts = fData.split("\\|");
                    if (parts.length < 3) continue;
                    String fName = parts[1];
                    boolean isOnline = Boolean.parseBoolean(parts[2]);
                    String label = fName + (isOnline ? " [Online]" : " [Offline]");
                    SAOMenuNode fNode = new SAOMenuNode(label, "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png");
                    fNode.addSub(new SAOMenuNode("Message", "saomcui:textures/gui/icons/ringed_message.png", "saomcui:textures/gui/icons/ringed_message_on.png", () -> {
                        List<ChatPacket.Channel> avail = this.getAvailableChatChannels();
                        this.chatTabIndex = Math.max(0, avail.indexOf((Object)ChatPacket.Channel.PRIVATE));
                        this.privateChatTarget = fName;
                        this.selectedIndex = 2;
                        this.subSelectedIndex = -1;
                        this.subSubSelectedIndex = -1;
                        this.sssSelectedIndex = -1;
                        this.currentSubNodes = null;
                        this.currentSubSubNodes = null;
                        this.currentSSSNodes = null;
                        this.refreshNodes();
                    }));
                    fNode.addSub(new SAOMenuNode("Remove", "saomcui:textures/gui/icons/logout.png", "saomcui:textures/gui/icons/logout_on.png", () -> SAOPacketHandler.sendToServer(new SocialPacket(SocialPacket.ActionType.FRIEND_REMOVE, fName, ""))));
                    friends.add(fNode);
                }
            }
            if (friends.isEmpty()) {
                friends.add(new SAOMenuNode("No Friends", "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png"));
            }
            return friends;
        }));
        SAOMenuNode party = new SAOMenuNode("Party", "saomcui:textures/gui/under_icons/party_and_profile.png", "saomcui:textures/gui/under_icons/party_and_profile_on.png", () -> {
            ArrayList<SAOMenuNode> nodes = new ArrayList<SAOMenuNode>();
            if (!SocialManager.isClientInParty()) {
                nodes.add(new SAOMenuNode("Create", "saomcui:textures/gui/icons/ringed_men.png", "saomcui:textures/gui/icons/ringed_men_on.png", () -> SAOPacketHandler.sendToServer(new SocialPacket(SocialPacket.ActionType.PARTY_CREATE, "", ""))));
                return nodes;
            }
            String myName = this.f_96541_.f_91074_.m_7755_().getString();
            boolean isLeader = false;
            for (String nWithRole : SocialManager.getClientPartyMembers()) {
                if (!nWithRole.equals("[L] " + myName)) continue;
                isLeader = true;
                break;
            }
            nodes.add(new SAOMenuNode("Invite", "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png", () -> {
                this.selectedIndex = 1;
                this.subSelectedIndex = 0;
            }));
            nodes.add(new SAOMenuNode("Leave", "saomcui:textures/gui/under_icons/logout.png", "saomcui:textures/gui/under_icons/logout_on.png", () -> this.showConfirmation("Leave Party?", "Are you sure you want to leave the current party?", () -> SAOPacketHandler.sendToServer(new SocialPacket(SocialPacket.ActionType.PARTY_LEAVE, "", "")))));
            for (String nWithRole : SocialManager.getClientPartyMembers()) {
                String nRaw = nWithRole.startsWith("[L] ") ? nWithRole.substring(4) : nWithRole;
                SAOMenuNode mn = new SAOMenuNode(nWithRole, "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png");
                if (!nRaw.equals(myName) && isLeader) {
                    mn.addSub(new SAOMenuNode("Leader", "saomcui:textures/gui/under_icons/guild.png", "saomcui:textures/gui/under_icons/guild_on.png", () -> this.showConfirmation("Make Leader?", "Transfer party leadership to " + nRaw + "?", () -> SAOPacketHandler.sendToServer(new SocialPacket(SocialPacket.ActionType.PARTY_PROMOTE, nRaw, "")))));
                    mn.addSub(new SAOMenuNode("Kick", "saomcui:textures/gui/under_icons/logout.png", "saomcui:textures/gui/under_icons/logout_on.png", () -> this.showConfirmation("Kick Player?", "Kick " + nRaw + " from the party?", () -> SAOPacketHandler.sendToServer(new SocialPacket(SocialPacket.ActionType.PARTY_KICK, nRaw, "")))));
                }
                nodes.add(mn);
            }
            return nodes;
        });
        SAOMenuNode guild = new SAOMenuNode("Guild", "saomcui:textures/gui/under_icons/guild.png", "saomcui:textures/gui/under_icons/guild_on.png", () -> {
            ArrayList<SAOMenuNode> nodes = new ArrayList<SAOMenuNode>();
            if (SocialManager.getClientGuildName() == null) {
                nodes.add(new SAOMenuNode("Create", "saomcui:textures/gui/icons/ringed_men.png", "saomcui:textures/gui/icons/ringed_men_on.png", () -> {
                    this.inputForName = true;
                    this.socialInput.m_94194_(true);
                    this.socialInput.m_94144_("");
                    this.socialInput.m_93692_(true);
                    this.m_7522_((GuiEventListener)this.socialInput);
                }));
                return nodes;
            }
            String myRole = SocialManager.getClientGuildRole();
            String myName = this.f_96541_.f_91074_.m_7755_().getString();
            boolean isLeader = "LEADER".equals(myRole);
            boolean isOfficer = "OFFICER".equals(myRole);
            if (isLeader || isOfficer) {
                nodes.add(new SAOMenuNode("Invite", "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png", () -> {
                    this.selectedIndex = 1;
                    this.subSelectedIndex = 0;
                }));
            }
            if (isLeader) {
                nodes.add(new SAOMenuNode("Set Emblem", "saomcui:textures/gui/under_icons/field_map_and_position_check.png", "saomcui:textures/gui/under_icons/field_map_and_position_check_on.png", () -> {
                    this.inputForName = false;
                    this.socialInput.m_94194_(true);
                    this.socialInput.m_94144_("");
                    this.socialInput.m_93692_(true);
                    this.m_7522_((GuiEventListener)this.socialInput);
                }));
            }
            nodes.add(new SAOMenuNode("Leave", "saomcui:textures/gui/icons/ringed_men.png", "saomcui:textures/gui/icons/ringed_men_on.png", () -> this.showConfirmation("Leave Guild?", "Are you sure you want to leave the guild?", () -> SAOPacketHandler.sendToServer(new SocialPacket(SocialPacket.ActionType.GUILD_LEAVE, "", "")))));
            for (String nWithRole : SocialManager.getClientGuildMembers()) {
                String nRaw = nWithRole.startsWith("[L] ") || nWithRole.startsWith("[O] ") ? nWithRole.substring(4) : nWithRole;
                SAOMenuNode mn = new SAOMenuNode(nWithRole, "saomcui:textures/gui/under_icons/friend.png", "saomcui:textures/gui/under_icons/friend_on.png");
                if (!nRaw.equals(myName) && myRole != null) {
                    boolean targetIsMember;
                    boolean targetIsOfficer = nWithRole.startsWith("[O] ");
                    boolean targetIsLeader = nWithRole.startsWith("[L] ");
                    boolean bl = targetIsMember = !targetIsOfficer && !targetIsLeader;
                    if (isLeader) {
                        if (targetIsMember) {
                            mn.addSub(new SAOMenuNode("Promote", "saomcui:textures/gui/under_icons/guild.png", "saomcui:textures/gui/under_icons/guild_on.png", () -> this.showConfirmation("Promote to Officer?", "Promote " + nRaw + " to Officer status?", () -> SAOPacketHandler.sendToServer(new SocialPacket(SocialPacket.ActionType.GUILD_SET_ROLE, nRaw, "OFFICER")))));
                        } else if (targetIsOfficer) {
                            mn.addSub(new SAOMenuNode("Demote", "saomcui:textures/gui/under_icons/logout.png", "saomcui:textures/gui/under_icons/logout_on.png", () -> this.showConfirmation("Demote to Member?", "Demote " + nRaw + " back to Member?", () -> SAOPacketHandler.sendToServer(new SocialPacket(SocialPacket.ActionType.GUILD_SET_ROLE, nRaw, "MEMBER")))));
                        }
                        mn.addSub(new SAOMenuNode("Kick", "saomcui:textures/gui/under_icons/logout.png", "saomcui:textures/gui/under_icons/logout_on.png", () -> this.showConfirmation("Kick from Guild?", "Are you sure you want to kick " + nRaw + "?", () -> SAOPacketHandler.sendToServer(new SocialPacket(SocialPacket.ActionType.GUILD_KICK, nRaw, "")))));
                    } else if (isOfficer && targetIsMember) {
                        mn.addSub(new SAOMenuNode("Kick", "saomcui:textures/gui/under_icons/logout.png", "saomcui:textures/gui/under_icons/logout_on.png", () -> this.showConfirmation("Kick from Guild?", "Kick " + nRaw + "?", () -> SAOPacketHandler.sendToServer(new SocialPacket(SocialPacket.ActionType.GUILD_KICK, nRaw, "")))));
                    }
                }
                nodes.add(mn);
            }
            return nodes;
        });
        social.addSub(guild);
        social.addSub(party);
        this.rootNodes.add(social);
        this.rootNodes.add(new SAOMenuNode("Messages", "saomcui:textures/gui/icons/ringed_message.png", "saomcui:textures/gui/icons/ringed_message_on.png"));
        this.rootNodes.add(new SAOMenuNode("Map", "saomcui:textures/gui/icons/ringed_location.png", "saomcui:textures/gui/icons/ringed_location_on.png", () -> this.setMapContent(new SAOMapScreen(this))));
        SAOMenuNode settings = new SAOMenuNode("Settings", "saomcui:textures/gui/icons/ringed_config.png", "saomcui:textures/gui/icons/ringed_config_on.png");
        settings.addSub(new SAOMenuNode("UI Settings", "saomcui:textures/gui/under_icons/option.png", "saomcui:textures/gui/under_icons/option_on.png", () -> this.setMapContent(new SAOUISettingsScreen(this))));
        settings.addSub(new SAOMenuNode("Logout", "saomcui:textures/gui/under_icons/logout.png", "saomcui:textures/gui/under_icons/logout_on.png", () -> {
            if (this.f_96541_.f_91073_ != null) {
                this.f_96541_.f_91073_.m_7462_();
            }
            this.f_96541_.m_91320_((Screen)new GenericDirtMessageScreen((Component)Component.m_237115_((String)"menu.savingLevel")));
            this.f_96541_.m_91152_((Screen)new TitleScreen());
        }));
        this.rootNodes.add(settings);
    }

    public boolean m_7043_() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        long now = Util.m_137550_();
        double elapsed = this.motionTime == 0L ? 0.0 : (double)(now - this.motionTime) / 1000.0;
        this.motionTime = now;
        double amplitude = 5.0f * this.menuScale();
        double tx = ((double)Math.max(0, Math.min(this.f_96543_, mouseX)) / (double)Math.max(1, this.f_96543_) - 0.5) * 2.0 * amplitude;
        double ty = ((double)Math.max(0, Math.min(this.f_96544_, mouseY)) / (double)Math.max(1, this.f_96544_) - 0.5) * amplitude + Math.sin((double)(now - this.openTime) / 1600.0) * (double)this.menuScale();
        this.floatMotion.update(elapsed, tx, ty, this.pointerHeld, SAOClientPreferences.floatingMenu());
        this.floatX = (float)this.floatMotion.x();
        this.floatY = (float)this.floatMotion.y();
        graphics.m_280024_(0, 0, this.f_96543_, this.f_96544_, 0x18081018, 1476923416);
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_252880_(this.floatX, this.floatY, 0.0f);
        try {
            this.renderMenu(graphics, (int)((float)mouseX - this.floatX), (int)((float)mouseY - this.floatY), partialTicks);
        }
        finally {
            graphics.m_280168_().m_85849_();
        }
    }

    private void renderMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        float cardH;
        float progress;
        this.hoveredFullLabel = null;
        long transitionNow = Util.m_137550_();
        if (this.selectedIndex == 2 && (this.animatedRoot != 2 || this.animatedChatTab != this.chatTabIndex)) {
            this.messagesOpenTime = transitionNow;
            this.animatedChatTab = this.chatTabIndex;
        }
        if (this.animatedRoot != this.selectedIndex) {
            Arrays.fill(this.columnOpened, transitionNow);
            this.animatedRoot = this.selectedIndex;
        }
        if (this.animatedSub != this.subSelectedIndex) {
            this.columnOpened[1] = this.columnOpened[2] = transitionNow;
            this.animatedSub = this.subSelectedIndex;
        }
        if (this.animatedLeaf != this.subSubSelectedIndex) {
            this.columnOpened[2] = transitionNow;
            this.animatedLeaf = this.subSubSelectedIndex;
        }
        if (this.showingConfirm) {
            this.renderConfirmation(graphics, mouseX, mouseY);
            return;
        }
        if (Util.m_137550_() - this.lastNodeRefresh > 500L) {
            this.refreshNodes();
            this.lastNodeRefresh = Util.m_137550_();
        }
        long currentTime = Util.m_137550_();
        float scale = this.menuScale();
        float f = progress = this.closing ? Math.max(0.0f, 1.0f - (float)(currentTime - this.closeTime) / 300.0f) : Math.min(1.0f, (float)(currentTime - this.openTime) / 300.0f);
        if (this.closing && progress <= 0.0f) {
            this.f_96541_.m_91152_(null);
            return;
        }
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_85841_(scale, scale, 1.0f);
        float w = (float)this.f_96543_ / scale;
        float h = (float)this.f_96544_ / scale;
        float animEase = SAOFloatMotion.ease(progress);
        this.cachedSlideY = this.closing ? -(h / 2.0f + 190.0f) * (1.0f - animEase) : (1.0f - animEase) * 22.0f;
        float rootX = w / 2.0f - 60.0f - (this.itemsPanelActive() ? 170.0f : 0.0f);
        float rootTotalH = this.rootNodes.size() * 55;
        float rootStartY = h / 2.0f - rootTotalH / 2.0f;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)progress);
        Matrix4f matrix = graphics.m_280168_().m_85850_().m_252922_();
        for (int i = 0; i < this.rootNodes.size(); ++i) {
            float y = rootStartY + (float)(i * 55) + this.cachedSlideY;
            SAOMenuNode node = this.rootNodes.get(i);
            boolean isActive = i == this.selectedIndex;
            boolean hovered = (float)mouseX / scale >= rootX - 22.0f && (float)mouseX / scale <= rootX + 22.0f && (float)mouseY / scale >= y - 22.0f && (float)mouseY / scale <= y + 22.0f;
            boolean isMessages = node.getLabel().equals("Messages");
            graphics.m_280163_(node.getIcon(), (int)(rootX - 22.0f), (int)(y - 22.0f), 0.0f, 0.0f, 44, 44, 44, 44);
            if (isActive || hovered) {
                RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)progress);
                graphics.m_280163_(node.getIconOn(), (int)(rootX - 22.0f), (int)(y - 22.0f), 0.0f, 0.0f, 44, 44, 44, 44);
            } else if (isMessages && SAOChatManager.hasUnreadMessages) {
                float pulse = (float)(Math.sin((double)Util.m_137550_() / 500.0) * 0.5 + 0.5);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)(progress * pulse));
                graphics.m_280163_(node.getIconOn(), (int)(rootX - 22.0f), (int)(y - 22.0f), 0.0f, 0.0f, 44, 44, 44, 44);
                RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)progress);
            }
            if (i < this.rootNodes.size() - 1) {
                graphics.m_280509_((int)rootX - 1, (int)(y + 22.0f), (int)rootX + 1, (int)(y + 33.0f), 852019400);
            }
            if (!isActive || this.mapContent != null) continue;
            if (isMessages) {
                SAOChatManager.hasUnreadMessages = false;
            }
            this.currentSubNodes = node.getSubNodes();
            if (this.currentSubNodes == null || this.currentSubNodes.isEmpty()) continue;
            float subW = 160.0f;
            float subH = 34.0f;
            float subTotalH = (float)this.currentSubNodes.size() * subH + (float)((this.currentSubNodes.size() - 1) * 2);
            float subStartX = rootX + 37.0f;
            float subStartY = y - subTotalH / 2.0f;
            SAODrawingUtils.fillTriangle(matrix, rootX + 24.0f, y, subStartX, y - 8.0f, subStartX, y + 8.0f, -1336576);
            for (int j = 0; j < this.currentSubNodes.size(); ++j) {
                float sY = subStartY + (float)j * (subH + 2.0f);
                SAOMenuNode subNode = this.currentSubNodes.get(j);
                boolean isSubActive = j == this.subSelectedIndex;
                boolean sHovered = (float)mouseX / scale >= subStartX && (float)mouseX / scale <= subStartX + subW && (float)mouseY / scale >= sY && (float)mouseY / scale <= sY + subH;
                this.revealRow(graphics, subStartX, sY, subW, subH, scale, 0, j);
                graphics.m_280509_((int)subStartX, (int)sY, (int)(subStartX + subW), (int)(sY + subH), isSubActive ? -1336576 : (sHovered ? -855638017 : -1711276033));
                graphics.m_280163_(isSubActive || sHovered ? subNode.getIconOn() : subNode.getIcon(), (int)(subStartX + 15.0f), (int)(sY + 5.0f), 0.0f, 0.0f, 24, 24, 24, 24);
                graphics.m_280056_(this.f_96547_, this.menuLabel(subNode.getLabel(), (int)subW - 58, sHovered), (int)(subStartX + 50.0f), (int)(sY + 13.0f), isSubActive ? -1 : -13619152, false);
                graphics.m_280618_();
                if (!isSubActive) continue;
                this.currentSubSubNodes = subNode.getSubNodes();
                if (this.currentSubSubNodes == null || this.currentSubSubNodes.isEmpty()) continue;
                float ssW = 160.0f;
                float ssH = 34.0f;
                float ssTotalH = (float)this.currentSubSubNodes.size() * ssH + (float)((this.currentSubSubNodes.size() - 1) * 2);
                float ssStartX = subStartX + subW + 20.0f;
                float ssStartY = sY + subH / 2.0f - ssTotalH / 2.0f;
                SAODrawingUtils.fillTriangle(matrix, subStartX + subW + 2.0f, sY + subH / 2.0f, ssStartX, sY + subH / 2.0f - 5.0f, ssStartX, sY + subH / 2.0f + 5.0f, -855638017);
                for (int k = 0; k < this.currentSubSubNodes.size(); ++k) {
                    float ssY = ssStartY + (float)k * (ssH + 2.0f);
                    SAOMenuNode ssNode = this.currentSubSubNodes.get(k);
                    boolean isSSActive = k == this.subSubSelectedIndex;
                    boolean ssHovered = (float)mouseX / scale >= ssStartX && (float)mouseX / scale <= ssStartX + ssW && (float)mouseY / scale >= ssY && (float)mouseY / scale <= ssY + ssH;
                    this.revealRow(graphics, ssStartX, ssY, ssW, ssH, scale, 1, k);
                    graphics.m_280509_((int)ssStartX, (int)ssY, (int)(ssStartX + ssW), (int)(ssY + ssH), ssHovered || isSSActive ? -1336576 : -328966);
                    graphics.m_280056_(this.f_96547_, this.menuLabel(ssNode.getLabel(), (int)ssW - 48, ssHovered), (int)(ssStartX + 40.0f), (int)(ssY + 13.0f), -10855073, false);
                    graphics.m_280618_();
                    if (!isSSActive) continue;
                    this.currentSSSNodes = ssNode.getSubNodes();
                    if (this.currentSSSNodes == null || this.currentSSSNodes.isEmpty()) continue;
                    float sssW = 140.0f;
                    float sssH = 30.0f;
                    float sssTotalH = (float)this.currentSSSNodes.size() * sssH + (float)((this.currentSSSNodes.size() - 1) * 2);
                    float sssStartX = ssStartX + ssW + 20.0f;
                    float sssStartY = ssY + ssH / 2.0f - sssTotalH / 2.0f;
                    SAODrawingUtils.fillTriangle(matrix, ssStartX + ssW + 2.0f, ssY + ssH / 2.0f, sssStartX, ssY + ssH / 2.0f - 5.0f, sssStartX, ssY + ssH / 2.0f + 5.0f, -855638017);
                    for (int l = 0; l < this.currentSSSNodes.size(); ++l) {
                        float sssY = sssStartY + (float)l * (sssH + 2.0f);
                        SAOMenuNode sssNode = this.currentSSSNodes.get(l);
                        boolean sssHovered = (float)mouseX / scale >= sssStartX && (float)mouseX / scale <= sssStartX + sssW && (float)mouseY / scale >= sssY && (float)mouseY / scale <= sssY + sssH;
                        this.revealRow(graphics, sssStartX, sssY, sssW, sssH, scale, 2, l);
                        graphics.m_280509_((int)sssStartX, (int)sssY, (int)(sssStartX + sssW), (int)(sssY + sssH), sssHovered ? -1336576 : -328966);
                        graphics.m_280056_(this.f_96547_, this.menuLabel(sssNode.getLabel(), (int)sssW - 18, sssHovered), (int)(sssStartX + 10.0f), (int)(sssY + 11.0f), -10855073, false);
                        graphics.m_280618_();
                    }
                }
            }
        }
        this.renderItemsCategoryPanel(graphics, mouseX, mouseY, scale, w, h, rootX);
        if (this.isCraftingActive()) {
            int gridWidth;
            float cardW = 550.0f;
            cardH = 260.0f;
            float cardX = rootX + 50.0f;
            float cardY = h / 2.0f - cardH / 2.0f + this.cachedSlideY;
            SAODrawingUtils.fillTriangle(matrix, rootX + 25.0f, h / 2.0f + this.cachedSlideY, cardX, h / 2.0f - 10.0f + this.cachedSlideY, cardX, h / 2.0f + 10.0f + this.cachedSlideY, -855638017);
            graphics.m_280163_(new ResourceLocation("saomcui:textures/gui/window_blank.png"), (int)cardX, (int)cardY, 0.0f, 0.0f, (int)cardW, (int)cardH, (int)cardW, (int)cardH);
            graphics.m_280509_((int)cardX + 1, (int)cardY + 1, (int)(cardX + cardW - 1.0f), (int)(cardY + cardH - 1.0f), -168166662);
            graphics.m_280509_((int)cardX + 1, (int)cardY + 1, (int)(cardX + cardW - 1.0f), (int)cardY + 32, -1512724);
            graphics.m_280509_((int)cardX + 20, (int)cardY + 30, (int)cardX + 115, (int)cardY + 32, -22016);
            String title = "Crafting " + (openAs3x3 ? "3x3" : "2x2");
            graphics.m_280056_(this.f_96547_, title, (int)(cardX + 175.0f - (float)(this.f_96547_.m_92895_(title) / 2)), (int)(cardY + 12.0f), -13619152, false);
            graphics.m_280509_((int)cardX + 20, (int)(cardY + 28.0f), (int)(cardX + 350.0f - 20.0f), (int)(cardY + 29.0f), -3618616);
            int gridSize = openAs3x3 ? 3 : 2;
            int slotSize = 40;
            int spacing = 5;
            int gridHeight = gridWidth = gridSize * slotSize + (gridSize - 1) * spacing;
            int gridStartX = (int)cardX + 30;
            int gridStartY = (int)cardY + 70;
            for (int r = 0; r < gridSize; ++r) {
                for (int c = 0; c < gridSize; ++c) {
                    int slotIdx = r * gridSize + c;
                    int x = gridStartX + c * (slotSize + spacing);
                    int y = gridStartY + r * (slotSize + spacing);
                    int color = -3748399;
                    graphics.m_280509_(x, y, x + slotSize, y + slotSize, color);
                    graphics.m_280509_(x + 2, y + 2, x + slotSize - 2, y + slotSize - 2, -1446160);
                    ItemStack slotStack = this.craftingGridStacks[slotIdx];
                    if (slotStack != null && !slotStack.m_41619_()) {
                        graphics.m_280480_(slotStack, x + 12, y + 12);
                        graphics.m_280370_(this.f_96547_, slotStack, x + 12, y + 12);
                    }
                    if (!((float)mouseX / scale >= (float)x) || !((float)mouseX / scale <= (float)(x + slotSize)) || !((float)mouseY / scale >= (float)y) || !((float)mouseY / scale <= (float)(y + slotSize))) continue;
                    graphics.m_280509_(x, y, x + slotSize, y + slotSize, 587180544);
                    graphics.m_280509_(x, y, x + slotSize, y + 2, -22016);
                }
            }
            int outSize = 60;
            int outX = gridStartX + gridWidth + 50;
            int outY = gridStartY + gridHeight / 2 - outSize / 2;
            graphics.m_280509_(outX, outY, outX + outSize, outY + outSize, -22016);
            graphics.m_280509_(outX + 2, outY + 2, outX + outSize - 2, outY + outSize - 2, -2594);
            ItemStack result = this.simulateCraftingClient();
            if (!result.m_41619_()) {
                graphics.m_280168_().m_85836_();
                graphics.m_280168_().m_252880_((float)(outX + outSize / 2) - 12.0f, (float)(outY + outSize / 2) - 12.0f, 0.0f);
                graphics.m_280168_().m_85841_(1.5f, 1.5f, 1.0f);
                graphics.m_280480_(result, 0, 0);
                graphics.m_280370_(this.f_96547_, result, 0, 0);
                graphics.m_280168_().m_85849_();
                if ((float)mouseX / scale >= (float)outX && (float)mouseX / scale <= (float)(outX + outSize) && (float)mouseY / scale >= (float)outY && (float)mouseY / scale <= (float)(outY + outSize)) {
                    graphics.m_280509_(outX, outY, outX + outSize, outY + outSize, 0x55FFFFFF);
                }
            }
            graphics.m_280056_(this.f_96547_, ">", outX - 30, outY + outSize / 2 - 4, -4752640, false);
            String craftText = result.m_41619_() ? "No recipe" : "CRAFT";
            graphics.m_280056_(this.f_96547_, craftText, outX + outSize / 2 - this.f_96547_.m_92895_(craftText) / 2, outY + outSize + 10, result.m_41619_() ? -9143166 : -5279232, false);
            graphics.m_280056_(this.f_96547_, "Left: move  |  Right: split / place one", (int)cardX + 20, (int)(cardY + cardH - 28.0f), -11182749, false);
            graphics.m_280056_(this.f_96547_, "Shift + result: craft all", (int)cardX + 20, (int)(cardY + cardH - 15.0f), -11182749, false);
            int invStartX = (int)cardX + 310;
            int invStartY = (int)cardY + 50;
            graphics.m_280509_(invStartX - 10, (int)cardY + 20, invStartX - 9, (int)(cardY + cardH - 20.0f), 0x55FFFFFF);
            graphics.m_280056_(this.f_96547_, "Inventory", invStartX, (int)cardY + 12, -13619152, false);
            if (this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
                this.f_96541_.f_91074_.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
                    int drawn = 0;
                    int skipped = 0;
                    for (int i = 0; i < inv.getSlots().size(); ++i) {
                        SAOInventory.SAOStack saoStack = inv.getStackInSlot(i);
                        int reserved = this.craftingReservedCounts.getOrDefault(i, 0);
                        int availableCount = saoStack.count - reserved;
                        if (availableCount <= 0 && reserved <= 0 || saoStack.item.m_41619_()) continue;
                        if (skipped < this.craftingInvScroll * 6) {
                            ++skipped;
                            continue;
                        }
                        int r = drawn / 6;
                        int c = drawn % 6;
                        if (r >= 5) break;
                        int ix = invStartX + c * 35;
                        int iy = invStartY + r * 35;
                        graphics.m_280509_(ix, iy, ix + 30, iy + 30, -3748399);
                        graphics.m_280509_(ix + 1, iy + 1, ix + 29, iy + 29, -1446160);
                        ItemStack drawnStack = saoStack.item.m_41777_();
                        drawnStack.m_41764_(availableCount == 0 ? 1 : availableCount);
                        graphics.m_280480_(drawnStack, ix + 7, iy + 7);
                        if (availableCount > 0) {
                            graphics.m_280370_(this.f_96547_, drawnStack, ix + 7, iy + 7);
                        } else {
                            graphics.m_280509_(ix + 1, iy + 1, ix + 29, iy + 29, -1442840576);
                            graphics.m_280168_().m_85836_();
                            graphics.m_280168_().m_252880_(0.0f, 0.0f, 200.0f);
                            graphics.m_280056_(this.f_96547_, "0", ix + 22, iy + 20, -43691, true);
                            graphics.m_280168_().m_85849_();
                        }
                        if ((float)mouseX / scale >= (float)ix && (float)mouseX / scale <= (float)(ix + 30) && (float)mouseY / scale >= (float)iy && (float)mouseY / scale <= (float)(iy + 30)) {
                            graphics.m_280509_(ix, iy, ix + 30, iy + 30, 0x55FFFFFF);
                        }
                        ++drawn;
                    }
                });
            }
            if (this.cursorStack != null && !this.cursorStack.m_41619_()) {
                graphics.m_280168_().m_85836_();
                graphics.m_280168_().m_252880_(0.0f, 0.0f, 300.0f);
                graphics.m_280480_(this.cursorStack, (int)((float)mouseX / scale) - 8, (int)((float)mouseY / scale) - 8);
                graphics.m_280370_(this.f_96547_, this.cursorStack, (int)((float)mouseX / scale) - 8, (int)((float)mouseY / scale) - 8);
                graphics.m_280168_().m_85849_();
            }
        }
        RenderSystem.disableBlend();
        if (this.selectedIndex == 0) {
            float cardW = 200.0f;
            cardH = 300.0f;
            float cardX = rootX - 250.0f;
            float cardY = h / 2.0f - cardH / 2.0f + this.cachedSlideY;
            SAODrawingUtils.fillTriangle(matrix, rootX - 30.0f, h / 2.0f + this.cachedSlideY, cardX + cardW, h / 2.0f - 10.0f + this.cachedSlideY, cardX + cardW, h / 2.0f + 10.0f + this.cachedSlideY, -855638017);
            graphics.m_280163_(new ResourceLocation("saomcui:textures/gui/window_blank.png"), (int)cardX, (int)cardY, 0.0f, 0.0f, (int)cardW, (int)cardH, (int)cardW, (int)cardH);
            String pName = this.f_96541_.f_91074_.m_7755_().getString();
            graphics.m_280056_(this.f_96547_, pName, (int)(cardX + cardW / 2.0f - (float)(this.f_96547_.m_92895_(pName) / 2)), (int)(cardY + 12.0f), -13619152, false);
            graphics.m_280509_((int)cardX + 20, (int)(cardY + 28.0f), (int)(cardX + cardW - 20.0f), (int)(cardY + 29.0f), -3618616);
            graphics.m_280509_((int)cardX + 1, (int)(cardY + 30.0f), (int)(cardX + cardW - 1.0f), (int)(cardY + 249.0f), -1);
            int entX = (int)(cardX + cardW / 2.0f);
            int entY = (int)(cardY + 210.0f);
            int entScale = 75;
            InventoryScreen.m_274545_((GuiGraphics)graphics, (int)entX, (int)entY, (int)entScale, (float)0.0f, (float)0.0f, (LivingEntity)this.f_96541_.f_91074_);
            graphics.m_280509_((int)cardX + 1, (int)(cardY + 250.0f), (int)(cardX + cardW - 1.0f), (int)(cardY + 298.0f), -3092272);
            int slotW = 24;
            int slotY = (int)(cardY + 260.0f);
            int startSlotX = (int)(cardX + (cardW - (float)(4 * slotW + 30)) / 2.0f);
            for (int i = 0; i < 4; ++i) {
                int sX = startSlotX + i * (slotW + 10);
                graphics.m_280509_(sX, slotY, sX + slotW, slotY + slotW, -1);
                graphics.m_280509_(sX + 1, slotY + 1, sX + slotW - 1, slotY + slotW - 1, -7697782);
                ItemStack armor = (ItemStack)this.f_96541_.f_91074_.m_150109_().f_35975_.get(3 - i);
                if (armor.m_41619_()) continue;
                graphics.m_280480_(armor, sX + 4, slotY + 4);
            }
        }
        if (this.selectedIndex == 1 && this.subSelectedIndex == 2) {
            float cardW = 200.0f;
            cardH = 300.0f;
            float cardX = rootX - 250.0f;
            float cardY = h / 2.0f - cardH / 2.0f + this.cachedSlideY;
            SAODrawingUtils.fillTriangle(matrix, rootX - 30.0f, h / 2.0f + this.cachedSlideY, cardX + cardW, h / 2.0f - 10.0f + this.cachedSlideY, cardX + cardW, h / 2.0f + 10.0f + this.cachedSlideY, -855638017);
            graphics.m_280163_(new ResourceLocation("saomcui:textures/gui/window_blank.png"), (int)cardX, (int)cardY, 0.0f, 0.0f, (int)cardW, (int)cardH, (int)cardW, (int)cardH);
            String gName = SocialManager.getClientGuildName() != null ? SocialManager.getClientGuildName() : "NO GUILD";
            graphics.m_280056_(this.f_96547_, gName, (int)(cardX + cardW / 2.0f - (float)(this.f_96547_.m_92895_(gName) / 2)), (int)(cardY + 12.0f), -13619152, false);
            graphics.m_280509_((int)cardX + 20, (int)(cardY + 28.0f), (int)(cardX + cardW - 20.0f), (int)(cardY + 29.0f), -3618616);
            graphics.m_280509_((int)cardX + 1, (int)(cardY + 30.0f), (int)(cardX + cardW - 1.0f), (int)(cardY + 199.0f), -1);
            String url = SocialManager.getClientEmblemUrl();
            ResourceLocation emblem = url != null && !url.isEmpty() ? WebTextureHelper.getOrCreateWebTexture(url) : new ResourceLocation("saomcui:textures/gui/under_icons/guild.png");
            graphics.m_280163_(emblem, (int)(cardX + cardW / 2.0f - 45.0f), (int)(cardY + 55.0f), 0.0f, 0.0f, 90, 90, 90, 90);
            graphics.m_280509_((int)cardX + 1, (int)(cardY + 200.0f), (int)(cardX + cardW - 1.0f), (int)(cardY + 298.0f), -3092272);
            String role = SocialManager.getClientGuildRole();
            if (role != null) {
                graphics.m_280056_(this.f_96547_, "Rank: " + role, (int)(cardX + 25.0f), (int)(cardY + 225.0f), -13619152, false);
            }
            int memCount = SocialManager.getClientGuildMembers() != null ? SocialManager.getClientGuildMembers().size() : 0;
            graphics.m_280056_(this.f_96547_, "Members: " + memCount, (int)(cardX + 25.0f), (int)(cardY + 245.0f), -13619152, false);
            if (this.socialInput.m_94213_()) {
                this.socialInput.m_252865_((int)((cardX + 20.0f) * scale));
                this.socialInput.m_253211_((int)((cardY + 230.0f) * scale));
                graphics.m_280056_(this.f_96547_, this.inputForName ? "Enter Guild Name:" : "Enter Emblem URL:", (int)(cardX + 20.0f), (int)(cardY + 215.0f), -13619152, false);
            }
        }
        if (this.selectedIndex == 2) {
            float cardW = 350.0f;
            cardH = 250.0f;
            float cardX = rootX + 50.0f;
            float cardY = h / 2.0f - cardH / 2.0f + this.cachedSlideY;
            float reveal = SAOFloatMotion.ease((float)(Util.m_137550_() - this.messagesOpenTime) / 240.0f);
            float mid = cardY + cardH / 2.0f;
            graphics.m_280588_(Math.round(this.floatX + (cardX - 26.0f) * scale), Math.round(this.floatY + (mid - 128.0f * reveal) * scale), Math.round(this.floatX + (cardX + cardW + 140.0f) * scale), Math.round(this.floatY + (mid + 128.0f * reveal) * scale));
            SAODrawingUtils.fillTriangle(matrix, rootX + 25.0f, h / 2.0f + this.cachedSlideY, cardX, h / 2.0f - 10.0f + this.cachedSlideY, cardX, h / 2.0f + 10.0f + this.cachedSlideY, -855638017);
            graphics.m_280163_(new ResourceLocation("saomcui:textures/gui/window_blank.png"), (int)cardX, (int)cardY, 0.0f, 0.0f, (int)cardW, (int)cardH, (int)cardW, (int)cardH);
            List<ChatPacket.Channel> availChannels = this.getAvailableChatChannels();
            if (this.chatTabIndex >= availChannels.size()) {
                this.chatTabIndex = availChannels.size() - 1;
            }
            if (this.chatTabIndex < 0) {
                this.chatTabIndex = 0;
            }
            int tabW = 60;
            for (int i = 0; i < availChannels.size(); ++i) {
                int tX = (int)(cardX + 20.0f + (float)(i * tabW));
                int tY = (int)(cardY + 10.0f);
                boolean isActive = this.chatTabIndex == i;
                Object tName = availChannels.get(i).name();
                tName = availChannels.get(i) == ChatPacket.Channel.PRIVATE ? "Friends" : ((String)tName).substring(0, 1).toUpperCase() + ((String)tName).substring(1).toLowerCase();
                graphics.m_280056_(this.f_96547_, (String)tName, tX, tY, isActive ? -1336576 : -13619152, false);
                if (!isActive) continue;
                graphics.m_280509_(tX, tY + 10, tX + this.f_96547_.m_92895_((String)tName), tY + 12, -1336576);
            }
            graphics.m_280509_((int)cardX + 1, (int)(cardY + 30.0f), (int)(cardX + cardW - 1.0f), (int)(cardY + 200.0f), -1);
            ChatPacket.Channel curChannel = availChannels.get(this.chatTabIndex);
            List<SAOChatManager.ChatMessage> allMsgs = SAOChatManager.getMessages(curChannel);
            ArrayList<SAOChatManager.ChatMessage> msgs = new ArrayList<SAOChatManager.ChatMessage>();
            for (SAOChatManager.ChatMessage m : allMsgs) {
                if (curChannel == ChatPacket.Channel.PRIVATE && (this.privateChatTarget.isEmpty() || !m.sender.equals(this.privateChatTarget) && !m.target.equals(this.privateChatTarget))) continue;
                msgs.add(m);
            }
            int maxLines = 15;
            int startIdx = Math.max(0, msgs.size() - maxLines);
            int mY = (int)(cardY + 35.0f);
            for (int i = startIdx; i < msgs.size(); ++i) {
                SAOChatManager.ChatMessage m = (SAOChatManager.ChatMessage)msgs.get(i);
                String pref = m.sender.isEmpty() ? "" : m.sender + ": ";
                graphics.m_280056_(this.f_96547_, pref + m.message, (int)(cardX + 10.0f), mY, -13619152, false);
                mY += 11;
            }
            graphics.m_280509_((int)cardX + 1, (int)(cardY + 200.0f), (int)(cardX + cardW - 1.0f), (int)(cardY + 249.0f), -3092272);
            if (curChannel == ChatPacket.Channel.PRIVATE) {
                String tStr = this.privateChatTarget.isEmpty() ? "None (Select a Friend)" : this.privateChatTarget + " (Press Enter to chat)";
                graphics.m_280056_(this.f_96547_, "To: " + tStr, (int)(cardX + 10.0f), (int)(cardY + 210.0f), -13619152, false);
                float listX = cardX + cardW + 10.0f;
                float listY = cardY;
                float listW = 120.0f;
                float listH = cardH;
                graphics.m_280163_(new ResourceLocation("saomcui:textures/gui/window_blank.png"), (int)listX, (int)listY, 0.0f, 0.0f, (int)listW, (int)listH, (int)listW, (int)listH);
                graphics.m_280056_(this.f_96547_, "Friends", (int)(listX + 10.0f), (int)(listY + 10.0f), -1336576, false);
                graphics.m_280509_((int)listX + 1, (int)(listY + 25.0f), (int)(listX + listW - 1.0f), (int)(listY + listH - 1.0f), -1);
                List<String> fList = SocialManager.getClientFriends();
                if (fList != null && !fList.isEmpty()) {
                    int fY = (int)(listY + 30.0f);
                    int startOff = Math.max(0, this.friendScrollOffset);
                    int drawn = 0;
                    for (int fi = startOff; fi < fList.size() && drawn < 14; ++fi) {
                        String fData = fList.get(fi);
                        String[] fParts = fData.split("\\|");
                        if (fParts.length < 3) continue;
                        String fName = fParts[1];
                        boolean fOnline = Boolean.parseBoolean(fParts[2]);
                        int fColor = this.privateChatTarget.equals(fName) ? -1336576 : (fOnline ? -13619152 : -7829368);
                        graphics.m_280056_(this.f_96547_, fName, (int)(listX + 10.0f), fY, fColor, false);
                        fY += 15;
                        ++drawn;
                    }
                } else {
                    graphics.m_280056_(this.f_96547_, "No Friends", (int)(listX + 10.0f), (int)(listY + 30.0f), -7829368, false);
                }
            } else if (curChannel != ChatPacket.Channel.SYSTEM) {
                graphics.m_280056_(this.f_96547_, "Press Enter to chat", (int)(cardX + 10.0f), (int)(cardY + 210.0f), -13619152, false);
            }
            if (this.socialInput.m_94213_()) {
                this.socialInput.m_252865_((int)((cardX + 10.0f) * scale));
                this.socialInput.m_253211_((int)((cardY + 225.0f) * scale));
            }
        }
        graphics.m_280168_().m_85849_();
        if (this.mapContent != null) {
            this.renderMapContent(graphics, mouseX, mouseY, partialTicks);
        }
        if (this.mapContent == null && this.hoveredFullLabel != null) {
            graphics.m_280557_(this.f_96547_, (Component)Component.m_237113_((String)this.hoveredFullLabel), mouseX, mouseY);
        }
        if (this.socialInput.m_94213_()) {
            this.socialInput.m_88315_(graphics, mouseX, mouseY, partialTicks);
        }
        if (this.selectedIndex == 2) {
            graphics.m_280618_();
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            if (this.showingConfirm) {
                this.showingConfirm = false;
                this.playSound("ui.back");
                return true;
            }
            if (this.socialInput.m_94213_()) {
                this.socialInput.m_94194_(false);
                this.isTypingChat = false;
                this.m_7522_(null);
                return true;
            }
            this.closeMenu();
            this.playSound("ui.back");
            return true;
        }
        if (this.mapContent != null && !this.showingConfirm) {
            return this.mapContent.m_7933_(keyCode, scanCode, modifiers);
        }
        if (keyCode == 71 && !this.socialInput.m_94213_()) {
            this.closeMenu();
            return true;
        }
        if (this.showingConfirm) {
            if (keyCode == 263 || keyCode == 262) {
                this.confirmSelectedIndex = this.confirmSelectedIndex == -1 ? 0 : 1 - this.confirmSelectedIndex;
                this.playSound("ui.move");
            } else if (keyCode == 257) {
                if (this.confirmSelectedIndex == 0 && this.confirmAction != null) {
                    this.confirmAction.run();
                }
                this.showingConfirm = false;
                this.playSound("ui.click");
                this.refreshNodes();
            }
            return true;
        }
        if (this.socialInput.m_94213_()) {
            if (keyCode == 257) {
                String val = this.socialInput.m_94155_();
                if (this.isTypingChat) {
                    if (!val.isEmpty()) {
                        SAOPacketHandler.sendToServer(new ChatPacket(this.getAvailableChatChannels().get(this.chatTabIndex), "", val, this.privateChatTarget));
                    }
                    this.socialInput.m_94194_(false);
                    this.isTypingChat = false;
                    this.m_7522_(null);
                    this.refreshNodes();
                    return true;
                }
                if (!val.isEmpty()) {
                    SAOPacketHandler.sendToServer(new SocialPacket(this.inputForName ? SocialPacket.ActionType.GUILD_CREATE : SocialPacket.ActionType.GUILD_SET_EMBLEM, this.inputForName ? val : "", this.inputForName ? "" : val));
                    this.socialInput.m_94194_(false);
                    this.m_7522_(null);
                    this.refreshNodes();
                }
                return true;
            }
            return this.socialInput.m_7933_(keyCode, scanCode, modifiers);
        }
        if (this.selectedIndex == 2 && !this.socialInput.m_94213_()) {
            ChatPacket.Channel c;
            if (keyCode == 263) {
                this.chatTabIndex = Math.max(0, this.chatTabIndex - 1);
                this.playSound("ui.move");
                return true;
            }
            if (keyCode == 262) {
                this.chatTabIndex = Math.min(this.getAvailableChatChannels().size() - 1, this.chatTabIndex + 1);
                this.playSound("ui.move");
                return true;
            }
            if (!(keyCode != 257 || (c = this.getAvailableChatChannels().get(this.chatTabIndex)) == ChatPacket.Channel.SYSTEM || c == ChatPacket.Channel.PRIVATE && this.privateChatTarget.isEmpty())) {
                this.socialInput.m_94194_(true);
                this.socialInput.m_94144_("");
                this.isTypingChat = true;
                this.socialInput.m_93692_(true);
                this.m_7522_((GuiEventListener)this.socialInput);
                this.playSound("ui.click");
            }
        }
        if (keyCode == 265) {
            if (this.currentSSSNodes != null) {
                this.sssSelectedIndex = (this.sssSelectedIndex - 1 + this.currentSSSNodes.size()) % this.currentSSSNodes.size();
            } else if (this.currentSubSubNodes != null) {
                this.subSubSelectedIndex = (this.subSubSelectedIndex - 1 + this.currentSubSubNodes.size()) % this.currentSubSubNodes.size();
                this.sssSelectedIndex = -1;
            } else if (this.currentSubNodes != null) {
                this.subSelectedIndex = (this.subSelectedIndex - 1 + this.currentSubNodes.size()) % this.currentSubNodes.size();
                this.subSubSelectedIndex = -1;
            } else {
                this.selectedIndex = (this.selectedIndex - 1 + this.rootNodes.size()) % this.rootNodes.size();
                this.subSelectedIndex = -1;
            }
            this.playSound("ui.move");
            return true;
        }
        if (keyCode == 264) {
            if (this.currentSSSNodes != null) {
                this.sssSelectedIndex = (this.sssSelectedIndex + 1) % this.currentSSSNodes.size();
            } else if (this.currentSubSubNodes != null) {
                this.subSubSelectedIndex = (this.subSubSelectedIndex + 1) % this.currentSubSubNodes.size();
                this.sssSelectedIndex = -1;
            } else if (this.currentSubNodes != null) {
                this.subSelectedIndex = (this.subSelectedIndex + 1) % this.currentSubNodes.size();
                this.subSubSelectedIndex = -1;
            } else {
                this.selectedIndex = (this.selectedIndex + 1) % this.rootNodes.size();
                this.subSelectedIndex = -1;
            }
            this.playSound("ui.move");
            return true;
        }
        if (keyCode == 262 || keyCode == 257) {
            this.handleAction();
            return true;
        }
        if (keyCode == 263) {
            if (this.currentSSSNodes != null) {
                this.currentSSSNodes = null;
                this.sssSelectedIndex = -1;
            } else if (this.currentSubSubNodes != null) {
                this.currentSubSubNodes = null;
                this.subSubSelectedIndex = -1;
            } else if (this.currentSubNodes != null) {
                this.currentSubNodes = null;
                this.subSelectedIndex = -1;
            }
            this.playSound("ui.back");
            return true;
        }
        return super.m_7933_(keyCode, scanCode, modifiers);
    }

    private void handleAction() {
        if (this.currentSSSNodes != null) {
            if (this.sssSelectedIndex < 0 || this.sssSelectedIndex >= this.currentSSSNodes.size()) {
                return;
            }
            SAOMenuNode sel = this.currentSSSNodes.get(this.sssSelectedIndex);
            sel.runAction();
            if (!sel.keepOpen) {
                this.sssSelectedIndex = -1;
            }
            this.playSound("ui.click");
            this.refreshNodes();
        } else if (this.currentSubSubNodes != null) {
            if (this.subSubSelectedIndex < 0 || this.subSubSelectedIndex >= this.currentSubSubNodes.size()) {
                return;
            }
            SAOMenuNode sel = this.currentSubSubNodes.get(this.subSubSelectedIndex);
            if (!sel.getSubNodes().isEmpty()) {
                this.currentSSSNodes = sel.getSubNodes();
                this.sssSelectedIndex = 0;
            } else {
                sel.runAction();
                if (!sel.keepOpen) {
                    this.subSubSelectedIndex = -1;
                }
                this.refreshNodes();
            }
            this.playSound("ui.click");
        } else if (this.currentSubNodes != null) {
            if (this.subSelectedIndex < 0 || this.subSelectedIndex >= this.currentSubNodes.size()) {
                return;
            }
            SAOMenuNode sel = this.currentSubNodes.get(this.subSelectedIndex);
            if (!sel.getSubNodes().isEmpty()) {
                this.currentSubSubNodes = sel.getSubNodes();
                this.subSubSelectedIndex = 0;
            } else {
                sel.runAction();
                if (!sel.keepOpen) {
                    this.subSelectedIndex = -1;
                }
                this.refreshNodes();
            }
            this.playSound("ui.click");
        } else {
            if (this.selectedIndex == -1) {
                return;
            }
            SAOMenuNode sel = this.rootNodes.get(this.selectedIndex);
            if (!sel.getSubNodes().isEmpty()) {
                this.currentSubNodes = sel.getSubNodes();
                this.subSelectedIndex = 0;
            } else {
                sel.runAction();
                if (!sel.keepOpen && this.mapContent == null) {
                    this.selectedIndex = -1;
                }
                this.refreshNodes();
            }
            this.playSound("ui.click");
        }
    }

    private void showConfirmation(String title, String context, Runnable action) {
        this.confirmTitle = title;
        this.confirmContext = context;
        this.confirmAction = action;
        this.confirmSelectedIndex = -1;
        this.showingConfirm = true;
        this.confirmOpenTime = Util.m_137550_();
        this.playSound("ui.open");
    }

    private void renderConfirmation(GuiGraphics graphics, int mouseX, int mouseY) {
        float baseScale = this.menuScale();
        float p = Math.min((float)(Util.m_137550_() - this.confirmOpenTime) / 250.0f, 1.0f);
        float animScale = (float)(1.0 - Math.pow(1.0 - (double)p, 3.0));
        float w = (float)this.f_96543_ / baseScale;
        float h = (float)this.f_96544_ / baseScale;
        float winW = 350.0f;
        float winH = 250.0f;
        float winX = w / 2.0f - winW / 2.0f;
        float winY = h / 2.0f - winH / 2.0f;
        float currentH = winH * animScale;
        float drawY = winY + winH / 2.0f - currentH / 2.0f;
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_85841_(baseScale, baseScale, 1.0f);
        graphics.m_280588_(0, Math.round(this.floatY + drawY * baseScale), this.f_96543_, Math.round(this.floatY + (drawY + currentH) * baseScale));
        graphics.m_280163_(new ResourceLocation("saomcui:textures/gui/window_blank.png"), (int)winX, (int)winY, 0.0f, 0.0f, (int)winW, (int)winH, (int)winW, (int)winH);
        graphics.m_280168_().m_85836_();
        float titleScale = 1.6f;
        graphics.m_280168_().m_252880_(winX + winW / 2.0f, winY + 25.0f, 0.0f);
        graphics.m_280168_().m_85841_(titleScale, titleScale, 1.0f);
        graphics.m_280056_(this.f_96547_, this.confirmTitle, -this.f_96547_.m_92895_(this.confirmTitle) / 2, 0, -13619152, false);
        graphics.m_280168_().m_85849_();
        graphics.m_280168_().m_85836_();
        float contextScale = 1.2f;
        graphics.m_280168_().m_252880_(winX + winW / 2.0f, winY + winH / 2.0f - 20.0f, 0.0f);
        graphics.m_280168_().m_85841_(contextScale, contextScale, 1.0f);
        graphics.m_280056_(this.f_96547_, this.confirmContext, -this.f_96547_.m_92895_(this.confirmContext) / 2, 0, -13619152, false);
        graphics.m_280168_().m_85849_();
        float btnS = 50.0f;
        float yesX = winX + winW / 4.0f - btnS / 2.0f;
        float yesY = winY + winH - 65.0f;
        float noX = winX + 3.0f * winW / 4.0f - btnS / 2.0f;
        float noY = yesY;
        boolean yesH = p == 1.0f && (float)mouseX / baseScale >= yesX && (float)mouseX / baseScale <= yesX + btnS && (float)mouseY / baseScale >= yesY && (float)mouseY / baseScale <= yesY + btnS;
        boolean noH = p == 1.0f && (float)mouseX / baseScale >= noX && (float)mouseX / baseScale <= noX + btnS && (float)mouseY / baseScale >= noY && (float)mouseY / baseScale <= noY + btnS;
        ResourceLocation yesRes = this.confirmSelectedIndex == 0 || yesH ? new ResourceLocation("saomcui:textures/gui/buttons/yes_on.png") : new ResourceLocation("saomcui:textures/gui/buttons/yes.png");
        graphics.m_280411_(yesRes, (int)yesX, (int)yesY, (int)btnS, (int)btnS, 0.0f, 0.0f, 256, 256, 256, 256);
        ResourceLocation noRes = this.confirmSelectedIndex == 1 || noH ? new ResourceLocation("saomcui:textures/gui/buttons/no_on.png") : new ResourceLocation("saomcui:textures/gui/buttons/no.png");
        graphics.m_280411_(noRes, (int)noX, (int)noY, (int)btnS, (int)btnS, 0.0f, 0.0f, 256, 256, 256, 256);
        graphics.m_280618_();
        graphics.m_280168_().m_85849_();
    }

    private void returnToVisualInventory(ItemStack stack) {
        if (stack == null || stack.m_41619_()) {
            return;
        }
        if (this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
            this.f_96541_.f_91074_.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
                for (int i = 0; i < inv.getSlots().size(); ++i) {
                    int reserved;
                    SAOInventory.SAOStack sao = inv.getStackInSlot(i);
                    if (!ItemStack.m_150942_((ItemStack)sao.item, (ItemStack)stack) || (reserved = this.craftingReservedCounts.getOrDefault(i, 0).intValue()) <= 0) continue;
                    int amountToReturn = Math.min(reserved, stack.m_41613_());
                    this.craftingReservedCounts.put(i, reserved - amountToReturn);
                    stack.m_41774_(amountToReturn);
                    if (!stack.m_41619_()) continue;
                    return;
                }
            });
        }
    }

    private void closeMenu() {
        if (this.closing) {
            return;
        }
        this.closing = true;
        this.closeTime = Util.m_137550_();
        this.playSound("ui.close");
        openAs3x3 = false;
        for (int i = 0; i < 9; ++i) {
            this.returnToVisualInventory(this.craftingGridStacks[i]);
            this.craftingGridStacks[i] = ItemStack.f_41583_;
        }
        this.returnToVisualInventory(this.cursorStack);
        this.cursorStack = ItemStack.f_41583_;
        this.craftingReservedCounts.clear();
    }

    public boolean m_7979_(double mouseX, double mouseY, int button, double dragX, double dragY) {
        mouseX -= (double)this.floatX;
        mouseY -= (double)this.floatY;
        if (this.mapContent != null && !this.showingConfirm) {
            return this.mapContent.m_7979_((mouseX - (double)this.mapPanelX) / (double)this.mapPanelScale, (mouseY - (double)this.mapPanelY) / (double)this.mapPanelScale, button, dragX / (double)this.mapPanelScale, dragY / (double)this.mapPanelScale);
        }
        if (this.closing || !this.isCraftingActive() || this.cursorStack.m_41619_()) {
            return super.m_7979_(mouseX, mouseY, button, dragX, dragY);
        }
        float actualGuiScale = (float)this.f_96541_.m_91268_().m_85449_();
        float scale = this.menuScale();
        float w = (float)this.f_96543_ / scale;
        float h = (float)this.f_96544_ / scale;
        float rootX = w / 2.0f - 60.0f;
        float cardX = rootX + 50.0f;
        float cardY = h / 2.0f - 130.0f + this.cachedSlideY;
        int gridSize = openAs3x3 ? 3 : 2;
        int slotSize = 40;
        int spacing = 5;
        int gridStartX = (int)cardX + 30;
        int gridStartY = (int)cardY + 70;
        for (int r = 0; r < gridSize; ++r) {
            for (int c = 0; c < gridSize; ++c) {
                int slotIdx = r * gridSize + c;
                int x = gridStartX + c * (slotSize + spacing);
                int y = gridStartY + r * (slotSize + spacing);
                if (!(mouseX / (double)scale >= (double)x) || !(mouseX / (double)scale <= (double)(x + slotSize)) || !(mouseY / (double)scale >= (double)y) || !(mouseY / (double)scale <= (double)(y + slotSize))) continue;
                if (!this.isDraggingCrafting) {
                    this.isDraggingCrafting = true;
                    this.dragButton = button;
                    this.dragSlots.clear();
                }
                if (button == this.dragButton) {
                    this.dragSlots.add(slotIdx);
                }
                return true;
            }
        }
        return super.m_7979_(mouseX, mouseY, button, dragX, dragY);
    }

    public boolean m_6348_(double mouseX, double mouseY, int button) {
        this.pointerHeld = false;
        mouseX -= (double)this.floatX;
        mouseY -= (double)this.floatY;
        if (this.mapContent != null && !this.showingConfirm) {
            return this.mapContent.m_6348_((mouseX - (double)this.mapPanelX) / (double)this.mapPanelScale, (mouseY - (double)this.mapPanelY) / (double)this.mapPanelScale, button);
        }
        if (this.isDraggingCrafting && !this.dragSlots.isEmpty() && !this.cursorStack.m_41619_()) {
            int amountPerSlot = 0;
            if (this.dragButton == 0) {
                amountPerSlot = this.cursorStack.m_41613_() / this.dragSlots.size();
            } else if (this.dragButton == 1) {
                amountPerSlot = 1;
            }
            if (amountPerSlot > 0) {
                for (int slotIdx : this.dragSlots) {
                    int place;
                    ItemStack slotStack;
                    if (this.cursorStack.m_41619_()) break;
                    ItemStack itemStack = slotStack = this.craftingGridStacks[slotIdx] == null ? ItemStack.f_41583_ : this.craftingGridStacks[slotIdx];
                    if (slotStack.m_41619_()) {
                        place = Math.min(amountPerSlot, this.cursorStack.m_41613_());
                        this.craftingGridStacks[slotIdx] = this.cursorStack.m_41620_(place);
                        continue;
                    }
                    if (!ItemStack.m_150942_((ItemStack)this.cursorStack, (ItemStack)slotStack)) continue;
                    place = Math.min(amountPerSlot, slotStack.m_41741_() - slotStack.m_41613_());
                    place = Math.min(place, this.cursorStack.m_41613_());
                    slotStack.m_41769_(place);
                    this.cursorStack.m_41774_(place);
                }
            }
            this.playSound("ui.click");
        }
        this.isDraggingCrafting = false;
        this.dragSlots.clear();
        return super.m_6348_(mouseX, mouseY, button);
    }

    public boolean m_6375_(double screenX, double screenY, int button) {
        float rootX2;
        float h2;
        this.publicClickTime = Util.m_137550_();
        this.pointerHeld = true;
        double mouseX = screenX - (double)this.floatX;
        double mouseY = screenY - (double)this.floatY;
        if (this.mapContent != null && !this.showingConfirm) {
            float rootX = (float)this.f_96543_ / this.mapPanelScale / 2.0f - 60.0f;
            float start = (float)this.f_96544_ / this.mapPanelScale / 2.0f - (float)(this.rootNodes.size() * 55) / 2.0f + this.cachedSlideY;
            boolean onNavigation = false;
            for (int i = 0; i < this.rootNodes.size(); ++i) {
                float y = start + (float)(i * 55);
                if (!(mouseX / (double)this.mapPanelScale >= (double)(rootX - 22.0f)) || !(mouseX / (double)this.mapPanelScale <= (double)(rootX + 22.0f)) || !(mouseY / (double)this.mapPanelScale >= (double)(y - 22.0f)) || !(mouseY / (double)this.mapPanelScale <= (double)(y + 22.0f))) continue;
                onNavigation = true;
            }
            if (onNavigation) {
                this.setMapContent(null);
            } else {
                return this.mapContent.m_6375_((mouseX - (double)this.mapPanelX) / (double)this.mapPanelScale, (mouseY - (double)this.mapPanelY) / (double)this.mapPanelScale, button);
            }
        }
        if (this.closing) {
            return false;
        }
        float actualGuiScale = (float)this.f_96541_.m_91268_().m_85449_();
        float scale = this.menuScale();
        if (this.showingConfirm) {
            boolean noH;
            float w = (float)this.f_96543_ / scale;
            float h = (float)this.f_96544_ / scale;
            float winW = 350.0f;
            float winH = 250.0f;
            float winX = w / 2.0f - winW / 2.0f;
            float winY = h / 2.0f - winH / 2.0f;
            float btnS = 50.0f;
            float yesX = winX + winW / 4.0f - btnS / 2.0f;
            float yesY = winY + winH - 65.0f;
            float noX = winX + 3.0f * winW / 4.0f - btnS / 2.0f;
            float noY = yesY;
            boolean yesH = mouseX / (double)scale >= (double)yesX && mouseX / (double)scale <= (double)(yesX + btnS) && mouseY / (double)scale >= (double)yesY && mouseY / (double)scale <= (double)(yesY + btnS);
            boolean bl = noH = mouseX / (double)scale >= (double)noX && mouseX / (double)scale <= (double)(noX + btnS) && mouseY / (double)scale >= (double)noY && mouseY / (double)scale <= (double)(noY + btnS);
            if (yesH || noH) {
                this.showingConfirm = false;
                this.playSound(yesH ? "ui.click" : "ui.back");
                if (yesH && this.confirmAction != null) {
                    this.confirmAction.run();
                }
                this.refreshNodes();
                return true;
            }
            return true;
        }
        float w = (float)this.f_96543_ / scale;
        float h = (float)this.f_96544_ / scale;
        float rootX = w / 2.0f - 60.0f;
        float rootTotalH = this.rootNodes.size() * 55;
        float rootStartY = h / 2.0f - rootTotalH / 2.0f;
        if (this.isCraftingActive()) {
            int gridWidth;
            float cardW = 550.0f;
            float cardH = 260.0f;
            float cardX = rootX + 50.0f;
            float cardY = h / 2.0f - cardH / 2.0f + this.cachedSlideY;
            int gridSize = openAs3x3 ? 3 : 2;
            int slotSize = 40;
            int spacing = 5;
            int gridHeight = gridWidth = gridSize * slotSize + (gridSize - 1) * spacing;
            int gridStartX = (int)cardX + 30;
            int gridStartY = (int)cardY + 70;
            for (int r = 0; r < gridSize; ++r) {
                for (int c = 0; c < gridSize; ++c) {
                    ItemStack slotStack;
                    int slotIdx = r * gridSize + c;
                    int x = gridStartX + c * (slotSize + spacing);
                    int y = gridStartY + r * (slotSize + spacing);
                    if (!(mouseX / (double)scale >= (double)x) || !(mouseX / (double)scale <= (double)(x + slotSize)) || !(mouseY / (double)scale >= (double)y) || !(mouseY / (double)scale <= (double)(y + slotSize))) continue;
                    ItemStack itemStack = slotStack = this.craftingGridStacks[slotIdx] == null ? ItemStack.f_41583_ : this.craftingGridStacks[slotIdx];
                    if (Screen.m_96638_() && !slotStack.m_41619_() && this.cursorStack.m_41619_()) {
                        this.returnToVisualInventory(slotStack);
                        this.craftingGridStacks[slotIdx] = ItemStack.f_41583_;
                        this.playSound("ui.click");
                        return true;
                    }
                    if (button == 0) {
                        if (this.cursorStack.m_41619_()) {
                            this.cursorStack = slotStack;
                            this.craftingGridStacks[slotIdx] = ItemStack.f_41583_;
                        } else if (slotStack.m_41619_()) {
                            this.craftingGridStacks[slotIdx] = this.cursorStack;
                            this.cursorStack = ItemStack.f_41583_;
                        } else if (ItemStack.m_150942_((ItemStack)this.cursorStack, (ItemStack)slotStack)) {
                            int transfer = Math.min(this.cursorStack.m_41613_(), slotStack.m_41741_() - slotStack.m_41613_());
                            slotStack.m_41769_(transfer);
                            this.cursorStack.m_41774_(transfer);
                        } else {
                            this.craftingGridStacks[slotIdx] = this.cursorStack;
                            this.cursorStack = slotStack;
                        }
                    } else if (button == 1) {
                        if (this.cursorStack.m_41619_()) {
                            if (!slotStack.m_41619_()) {
                                int take = (int)Math.ceil((double)slotStack.m_41613_() / 2.0);
                                this.cursorStack = slotStack.m_41620_(take);
                            }
                        } else if (slotStack.m_41619_()) {
                            this.craftingGridStacks[slotIdx] = this.cursorStack.m_41620_(1);
                        } else if (ItemStack.m_150942_((ItemStack)this.cursorStack, (ItemStack)slotStack)) {
                            if (slotStack.m_41613_() < slotStack.m_41741_()) {
                                slotStack.m_41769_(1);
                                this.cursorStack.m_41774_(1);
                            }
                        } else {
                            this.craftingGridStacks[slotIdx] = this.cursorStack;
                            this.cursorStack = slotStack;
                        }
                    }
                    this.playSound("ui.click");
                    return true;
                }
            }
            int outSize = 60;
            int outX = gridStartX + gridWidth + 50;
            int outY = gridStartY + gridHeight / 2 - outSize / 2;
            if (mouseX / (double)scale >= (double)outX && mouseX / (double)scale <= (double)(outX + outSize) && mouseY / (double)scale >= (double)outY && mouseY / (double)scale <= (double)(outY + outSize) && this.cursorStack.m_41619_()) {
                ItemStack result = this.simulateCraftingClient();
                if (!result.m_41619_()) {
                    SAOPacketHandler.sendToServer(new CraftActionPacket(this.craftingGridStacks, openAs3x3, Screen.m_96638_()));
                    if (Screen.m_96638_()) {
                        for (i = 0; i < 9; ++i) {
                            this.returnToVisualInventory(this.craftingGridStacks[i]);
                            this.craftingGridStacks[i] = ItemStack.f_41583_;
                        }
                    } else {
                        for (i = 0; i < 9; ++i) {
                            if (this.craftingGridStacks[i] == null || this.craftingGridStacks[i].m_41619_()) continue;
                            ItemStack gridStack = this.craftingGridStacks[i];
                            if (this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
                                this.f_96541_.f_91074_.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
                                    for (int invSlot = 0; invSlot < inv.getSlots().size(); ++invSlot) {
                                        if (this.craftingReservedCounts.getOrDefault(invSlot, 0) <= 0) continue;
                                        SAOInventory.SAOStack sao = inv.getStackInSlot(invSlot);
                                        if (!ItemStack.m_150942_((ItemStack)sao.item, (ItemStack)gridStack)) continue;
                                        this.craftingReservedCounts.put(invSlot, this.craftingReservedCounts.get(invSlot) - 1);
                                        break;
                                    }
                                });
                            }
                            gridStack.m_41774_(1);
                        }
                    }
                    this.playSound("ui.click");
                }
                return true;
            }
            int invStartX = (int)cardX + 310;
            int invStartY = (int)cardY + 50;
            if (this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
                AtomicBoolean found = new AtomicBoolean(false);
                this.f_96541_.f_91074_.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
                    int drawn = 0;
                    int skipped = 0;
                    for (int i = 0; i < inv.getSlots().size(); ++i) {
                        SAOInventory.SAOStack saoStack = inv.getStackInSlot(i);
                        int reserved = this.craftingReservedCounts.getOrDefault(i, 0);
                        int availableCount = saoStack.count - reserved;
                        if (availableCount <= 0 && reserved <= 0 || saoStack.item.m_41619_()) continue;
                        if (skipped < this.craftingInvScroll * 6) {
                            ++skipped;
                            continue;
                        }
                        int r = drawn / 6;
                        int c = drawn % 6;
                        if (r >= 5) break;
                        int ix = invStartX + c * 35;
                        int iy = invStartY + r * 35;
                        if (mouseX / (double)scale >= (double)ix && mouseX / (double)scale <= (double)(ix + 30) && mouseY / (double)scale >= (double)iy && mouseY / (double)scale <= (double)(iy + 30)) {
                            if (Screen.m_96638_() && availableCount > 0 && this.cursorStack.m_41619_()) {
                                int gs = openAs3x3 ? 3 : 2;
                                for (int r2 = 0; r2 < gs; ++r2) {
                                    for (int c2 = 0; c2 < gs; ++c2) {
                                        int take;
                                        ItemStack s;
                                        int sIdx = r2 * gs + c2;
                                        ItemStack itemStack = s = this.craftingGridStacks[sIdx] == null ? ItemStack.f_41583_ : this.craftingGridStacks[sIdx];
                                        if (!s.m_41619_() && (!ItemStack.m_150942_((ItemStack)s, (ItemStack)saoStack.item) || s.m_41613_() >= s.m_41741_()) || (take = Math.min(availableCount, saoStack.item.m_41741_() - s.m_41613_())) <= 0) continue;
                                        if (s.m_41619_()) {
                                            ItemStack placed = saoStack.item.m_41777_();
                                            placed.m_41764_(take);
                                            this.craftingGridStacks[sIdx] = placed;
                                        } else {
                                            s.m_41769_(take);
                                        }
                                        this.craftingReservedCounts.put(i, this.craftingReservedCounts.getOrDefault(i, 0) + take);
                                        if ((availableCount -= take) <= 0) break;
                                    }
                                    if (availableCount <= 0) break;
                                }
                                this.playSound("ui.click");
                                found.set(true);
                                return;
                            }
                            if (button == 0) {
                                if (this.cursorStack.m_41619_()) {
                                    take = Math.min(availableCount, saoStack.item.m_41741_());
                                    this.cursorStack = saoStack.item.m_41777_();
                                    this.cursorStack.m_41764_(take);
                                    this.craftingReservedCounts.put(i, this.craftingReservedCounts.getOrDefault(i, 0) + take);
                                } else if (ItemStack.m_150942_((ItemStack)this.cursorStack, (ItemStack)saoStack.item)) {
                                    this.craftingReservedCounts.put(i, this.craftingReservedCounts.getOrDefault(i, 0) - this.cursorStack.m_41613_());
                                    this.cursorStack = ItemStack.f_41583_;
                                } else {
                                    this.returnToVisualInventory(this.cursorStack);
                                    this.cursorStack = ItemStack.f_41583_;
                                }
                            } else if (button == 1) {
                                if (this.cursorStack.m_41619_()) {
                                    take = (int)Math.ceil((double)availableCount / 2.0);
                                    if (take > saoStack.item.m_41741_()) {
                                        take = saoStack.item.m_41741_();
                                    }
                                    this.cursorStack = saoStack.item.m_41777_();
                                    this.cursorStack.m_41764_(take);
                                    this.craftingReservedCounts.put(i, this.craftingReservedCounts.getOrDefault(i, 0) + take);
                                } else if (ItemStack.m_150942_((ItemStack)this.cursorStack, (ItemStack)saoStack.item)) {
                                    this.craftingReservedCounts.put(i, this.craftingReservedCounts.getOrDefault(i, 0) - 1);
                                    this.cursorStack.m_41774_(1);
                                }
                            }
                            this.playSound("ui.click");
                            found.set(true);
                        }
                        ++drawn;
                    }
                });
                if (found.get()) {
                    return true;
                }
            }
            if (mouseX / (double)scale >= (double)cardX && mouseX / (double)scale <= (double)(cardX + cardW) && mouseY / (double)scale >= (double)cardY && mouseY / (double)scale <= (double)(cardY + cardH)) {
                int invPanelX = (int)cardX + 300;
                if (mouseX / (double)scale >= (double)invPanelX && !this.cursorStack.m_41619_()) {
                    this.returnToVisualInventory(this.cursorStack);
                    this.cursorStack = ItemStack.f_41583_;
                    this.playSound("ui.click");
                }
                return true;
            }
        }
        if (this.handleItemsCategoryClick(mouseX, mouseY, button, scale, w, h, rootX)) return true;
        boolean hit = false;
        for (int i = 0; i < this.rootNodes.size(); ++i) {
            float y = rootStartY + (float)(i * 55) + this.cachedSlideY;
            if (!(mouseX / (double)scale >= (double)(rootX - 22.0f)) || !(mouseX / (double)scale <= (double)(rootX + 22.0f)) || !(mouseY / (double)scale >= (double)(y - 22.0f)) || !(mouseY / (double)scale <= (double)(y + 22.0f))) continue;
            if (this.rootNodes.get(i).getLabel().equals("Map")) {
                this.playSound("ui.click");
                this.setMapContent(new SAOMapScreen(this));
                return true;
            }
            if (this.selectedIndex == i) {
                this.selectedIndex = -1;
                this.playSound("ui.back");
            } else {
                this.selectedIndex = i;
                this.playSound("ui.click");
            }
            this.resetIndices();
            hit = true;
            break;
        }
        if (!hit && this.selectedIndex != -1 && this.currentSubNodes != null) {
            float rootY = rootStartY + (float)(this.selectedIndex * 55) + this.cachedSlideY;
            float subW = 160.0f;
            float subH = 34.0f;
            float subTotalH = (float)this.currentSubNodes.size() * subH + (float)((this.currentSubNodes.size() - 1) * 2);
            float subStartX = rootX + 37.0f;
            float subStartY = rootY - subTotalH / 2.0f;
            for (int i = 0; i < this.currentSubNodes.size(); ++i) {
                float sY = subStartY + (float)i * (subH + 2.0f);
                if (!(mouseX / (double)scale >= (double)subStartX) || !(mouseX / (double)scale <= (double)(subStartX + subW)) || !(mouseY / (double)scale >= (double)sY) || !(mouseY / (double)scale <= (double)(sY + subH))) continue;
                if (this.subSelectedIndex == i) {
                    this.subSelectedIndex = -1;
                    this.playSound("ui.back");
                } else {
                    this.subSelectedIndex = i;
                    this.playSound("ui.click");
                    SAOMenuNode sub = this.currentSubNodes.get(i);
                    if (sub.getSubNodes().isEmpty()) {
                        sub.runAction();
                        if (!sub.keepOpen) {
                            this.subSelectedIndex = -1;
                        }
                        this.refreshNodes();
                    } else {
                        this.currentSubSubNodes = sub.getSubNodes();
                    }
                }
                this.subSubSelectedIndex = -1;
                this.sssSelectedIndex = -1;
                this.currentSSSNodes = null;
                hit = true;
                break;
            }
        }
        if (!hit && this.subSelectedIndex != -1 && this.currentSubSubNodes != null) {
            float rootY = rootStartY + (float)(this.selectedIndex * 55) + this.cachedSlideY;
            float subTotalH = this.currentSubNodes.size() * 34 + (this.currentSubNodes.size() - 1) * 2;
            float subStartY = rootY - subTotalH / 2.0f;
            float sY_Base = subStartY + (float)(this.subSelectedIndex * 36);
            float ssW = 160.0f;
            float ssH = 34.0f;
            float ssTotalH = (float)this.currentSubSubNodes.size() * ssH + (float)((this.currentSubSubNodes.size() - 1) * 2);
            float ssStartX = rootX + 37.0f + 160.0f + 20.0f;
            float ssStartY = sY_Base + 17.0f - ssTotalH / 2.0f;
            for (int i = 0; i < this.currentSubSubNodes.size(); ++i) {
                float ssY = ssStartY + (float)(i * 36);
                if (!(mouseX / (double)scale >= (double)ssStartX) || !(mouseX / (double)scale <= (double)(ssStartX + ssW)) || !(mouseY / (double)scale >= (double)ssY) || !(mouseY / (double)scale <= (double)(ssY + ssH))) continue;
                if (this.subSubSelectedIndex == i) {
                    this.subSubSelectedIndex = -1;
                    this.playSound("ui.back");
                } else {
                    this.subSubSelectedIndex = i;
                    this.playSound("ui.click");
                    SAOMenuNode ss = this.currentSubSubNodes.get(i);
                    if (ss.getSubNodes().isEmpty()) {
                        ss.runAction();
                        if (!ss.keepOpen) {
                            this.subSubSelectedIndex = -1;
                        }
                        this.refreshNodes();
                    } else {
                        this.currentSSSNodes = ss.getSubNodes();
                    }
                }
                this.sssSelectedIndex = -1;
                hit = true;
                break;
            }
        }
        if (!hit && this.subSubSelectedIndex != -1 && this.currentSSSNodes != null) {
            float rY = rootStartY + (float)(this.selectedIndex * 55) + this.cachedSlideY;
            float stH = this.currentSubNodes.size() * 34 + (this.currentSubNodes.size() - 1) * 2;
            float stY = rY - stH / 2.0f;
            float sY_B = stY + (float)(this.subSelectedIndex * 36);
            float ssTH = this.currentSubSubNodes.size() * 34 + (this.currentSubSubNodes.size() - 1) * 2;
            float ssSY = sY_B + 17.0f - ssTH / 2.0f;
            float ssY_B = ssSY + (float)(this.subSubSelectedIndex * 36);
            float sssW = 140.0f;
            float sssH = 30.0f;
            float sssTH = (float)this.currentSSSNodes.size() * sssH + (float)((this.currentSSSNodes.size() - 1) * 2);
            float sssSX = rootX + 37.0f + 160.0f + 20.0f + 160.0f + 20.0f;
            float sssSY = ssY_B + 17.0f - sssTH / 2.0f;
            for (int i = 0; i < this.currentSSSNodes.size(); ++i) {
                float sssY = sssSY + (float)(i * 32);
                if (!(mouseX / (double)scale >= (double)sssSX) || !(mouseX / (double)scale <= (double)(sssSX + sssW)) || !(mouseY / (double)scale >= (double)sssY) || !(mouseY / (double)scale <= (double)(sssY + sssH))) continue;
                SAOMenuNode actionNode = this.currentSSSNodes.get(i);
                actionNode.runAction();
                if (!actionNode.keepOpen) {
                    this.sssSelectedIndex = -1;
                }
                this.playSound("ui.click");
                this.refreshNodes();
                hit = true;
                break;
            }
        }
        if (!hit && this.selectedIndex == 0) {
            float w2 = (float)this.f_96543_ / scale;
            h2 = (float)this.f_96544_ / scale;
            rootX2 = w2 / 2.0f - 60.0f - (this.itemsPanelActive() ? 170.0f : 0.0f);
            float cardW = 200.0f;
            float cardH = 300.0f;
            float cardX = rootX2 - 250.0f;
            float cardY = h2 / 2.0f - cardH / 2.0f;
            int slotW = 24;
            int slotY = (int)(cardY + 260.0f);
            int startSlotX = (int)(cardX + (cardW - (float)(4 * slotW + 30)) / 2.0f);
            for (int i = 0; i < 4; ++i) {
                int armorSlot;
                ItemStack armor;
                int sX = startSlotX + i * (slotW + 10);
                if (!(mouseX / (double)scale >= (double)sX) || !(mouseX / (double)scale <= (double)(sX + slotW)) || !(mouseY / (double)scale >= (double)slotY) || !(mouseY / (double)scale <= (double)(slotY + slotW)) || (armor = (ItemStack)this.f_96541_.f_91074_.m_150109_().f_35975_.get(armorSlot = 3 - i)).m_41619_()) continue;
                SAOPacketHandler.sendToServer(new ItemActionPacket(armorSlot, ItemActionPacket.ActionType.UNEQUIP_ARMOR));
                this.playSound("ui.click");
                hit = true;
                break;
            }
        }
        if (!hit && this.selectedIndex == 2) {
            float w2 = (float)this.f_96543_ / scale;
            h2 = (float)this.f_96544_ / scale;
            rootX2 = w2 / 2.0f - 60.0f;
            float cardW = 350.0f;
            float cardH = 250.0f;
            float cardX = rootX2 + 50.0f;
            float cardY = h2 / 2.0f - cardH / 2.0f + this.cachedSlideY;
            List<ChatPacket.Channel> availChannels = this.getAvailableChatChannels();
            if (mouseX / (double)scale >= (double)cardX && mouseX / (double)scale <= (double)(cardX + cardW) && mouseY / (double)scale >= (double)cardY && mouseY / (double)scale <= (double)(cardY + cardH)) {
                hit = true;
                int tabW = 60;
                for (int i = 0; i < availChannels.size(); ++i) {
                    int tX = (int)(cardX + 20.0f + (float)(i * tabW));
                    int tY = (int)(cardY + 10.0f);
                    if (!(mouseX / (double)scale >= (double)tX) || !(mouseX / (double)scale <= (double)(tX + tabW)) || !(mouseY / (double)scale >= (double)tY) || !(mouseY / (double)scale <= (double)(tY + 12))) continue;
                    this.chatTabIndex = i;
                    this.playSound("ui.click");
                    break;
                }
            }
            if (!hit && this.chatTabIndex >= 0 && this.chatTabIndex < availChannels.size() && availChannels.get(this.chatTabIndex) == ChatPacket.Channel.PRIVATE) {
                float listX = cardX + cardW + 10.0f;
                float listY = cardY;
                float listW = 120.0f;
                float listH = cardH;
                if (mouseX / (double)scale >= (double)listX && mouseX / (double)scale <= (double)(listX + listW) && mouseY / (double)scale >= (double)(listY + 25.0f) && mouseY / (double)scale <= (double)(listY + listH)) {
                    String fData;
                    String[] parts;
                    int actualIndex;
                    int index;
                    int relativeY;
                    hit = true;
                    List<String> friends = SocialManager.getClientFriends();
                    if (friends != null && (relativeY = (int)(mouseY / (double)scale - (double)(listY + 30.0f))) >= 0 && (index = relativeY / 15) >= 0 && index < 14 && (actualIndex = this.friendScrollOffset + index) < friends.size() && (parts = (fData = friends.get(actualIndex)).split("\\|")).length >= 3) {
                        this.privateChatTarget = parts[1];
                        this.playSound("ui.click");
                    }
                }
            }
        }
        if (!hit) {
            this.closeMenu();
        }
        return true;
    }

    public boolean m_6050_(double mouseX, double mouseY, double scrollY) {
        mouseX -= (double)this.floatX;
        mouseY -= (double)this.floatY;
        if (this.mapContent != null && !this.showingConfirm) {
            return this.mapContent.m_6050_((mouseX - (double)this.mapPanelX) / (double)this.mapPanelScale, (mouseY - (double)this.mapPanelY) / (double)this.mapPanelScale, scrollY);
        }
        float scale = this.menuScale();
        if (this.isCraftingActive()) {
            float w = (float)this.f_96543_ / scale;
            float h = (float)this.f_96544_ / scale;
            float rootX = w / 2.0f - 60.0f;
            float cardW = 550.0f;
            float cardH = 260.0f;
            float cardX = rootX + 50.0f;
            float cardY = h / 2.0f - cardH / 2.0f + this.cachedSlideY;
            int invStartX = (int)cardX + 310;
            if (mouseX / (double)scale >= (double)(invStartX - 10) && mouseX / (double)scale <= (double)(cardX + cardW) && this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
                AtomicInteger totalSlots = new AtomicInteger(0);
                this.f_96541_.f_91074_.getCapability(SAOInventory.CAPABILITY).ifPresent(inv -> {
                    for (int i = 0; i < inv.getSlots().size(); ++i) {
                        SAOInventory.SAOStack saoStack = inv.getStackInSlot(i);
                        if (saoStack.count <= 0 || saoStack.item.m_41619_()) continue;
                        totalSlots.incrementAndGet();
                    }
                });
                int maxRows = (int)Math.ceil((double)totalSlots.get() / 6.0);
                if (scrollY > 0.0) {
                    this.craftingInvScroll = Math.max(0, this.craftingInvScroll - 1);
                } else if (scrollY < 0.0) {
                    int maxOffset = Math.max(0, maxRows - 5);
                    this.craftingInvScroll = Math.min(maxOffset, this.craftingInvScroll + 1);
                }
                return true;
            }
        }
        if (this.itemsPanelActive()) {
            float w = (float)this.f_96543_ / scale;
            float h = (float)this.f_96544_ / scale;
            float rootX = w / 2.0f - 60.0f - 170.0f;
            float panelX = rootX + 37.0f + 160.0f + 20.0f + 10.0f;
            float panelY = h / 2.0f - 158.0f + this.cachedSlideY;
            double localX = mouseX / (double)scale;
            double localY = mouseY / (double)scale;
            if (localX >= panelX && localX <= panelX + 330.0f
                && localY >= panelY && localY <= panelY + 316.0f) {
                if (scrollY > 0.0) {
                    this.itemsCategoryIndex = Math.max(0, this.itemsCategoryIndex - 1);
                } else if (scrollY < 0.0) {
                    this.itemsCategoryIndex = Math.min(ITEM_CATEGORY_NAMES.length - 1, this.itemsCategoryIndex + 1);
                }
                return true;
            }
        }
        if (this.selectedIndex == 2 && this.currentSubNodes == null) {
            List<String> friends;
            List<ChatPacket.Channel> availChannels = this.getAvailableChatChannels();
            if (this.chatTabIndex >= 0 && this.chatTabIndex < availChannels.size() && availChannels.get(this.chatTabIndex) == ChatPacket.Channel.PRIVATE && (friends = SocialManager.getClientFriends()) != null) {
                if (scrollY > 0.0) {
                    this.friendScrollOffset = Math.max(0, this.friendScrollOffset - 1);
                } else if (scrollY < 0.0) {
                    int maxOffset = Math.max(0, friends.size() - 14);
                    this.friendScrollOffset = Math.min(maxOffset, this.friendScrollOffset + 1);
                }
                return true;
            }
        }
        return super.m_6050_(mouseX, mouseY, scrollY);
    }

    private List<ChatPacket.Channel> getAvailableChatChannels() {
        ArrayList<ChatPacket.Channel> channels = new ArrayList<ChatPacket.Channel>();
        if (SocialManager.getClientPartyMembers() != null && !SocialManager.getClientPartyMembers().isEmpty()) {
            channels.add(ChatPacket.Channel.PARTY);
        }
        if (SocialManager.getClientGuildName() != null && !SocialManager.getClientGuildName().isEmpty()) {
            channels.add(ChatPacket.Channel.GUILD);
        }
        channels.add(ChatPacket.Channel.PRIVATE);
        channels.add(ChatPacket.Channel.SYSTEM);
        return channels;
    }

    private void resetIndices() {
        this.subSelectedIndex = -1;
        this.subSubSelectedIndex = -1;
        this.sssSelectedIndex = -1;
        this.currentSubNodes = null;
        this.currentSubSubNodes = null;
        this.currentSSSNodes = null;
    }

    private void playSound(String path) {
        SAOUISounds.play(path);
    }

    public static class SAOMenuNode {
        public boolean keepOpen = false;
        private final String label;
        private final ResourceLocation icon;
        private final ResourceLocation iconOn;
        private final List<SAOMenuNode> staticSubNodes = new ArrayList<SAOMenuNode>();
        private final Supplier<List<SAOMenuNode>> dynamicSubNodes;
        private final Runnable action;

        public SAOMenuNode(String label, String iconPath, String iconOnPath) {
            this(label, iconPath, iconOnPath, null, null);
        }

        public SAOMenuNode(String label, String iconPath, String iconOnPath, Runnable action) {
            this(label, iconPath, iconOnPath, null, action);
        }

        public SAOMenuNode(String label, String iconPath, String iconOnPath, Supplier<List<SAOMenuNode>> dynamicSubNodes) {
            this(label, iconPath, iconOnPath, dynamicSubNodes, null);
        }

        public SAOMenuNode(String label, String iconPath, String iconOnPath, Supplier<List<SAOMenuNode>> dynamicSubNodes, Runnable action) {
            this.label = label;
            this.icon = ResourceLocation.m_135820_((String)("saomcui:" + iconPath.replace("saomcui:textures/", "textures/")));
            this.iconOn = ResourceLocation.m_135820_((String)("saomcui:" + iconOnPath.replace("saomcui:textures/", "textures/")));
            this.dynamicSubNodes = dynamicSubNodes;
            this.action = action;
        }

        public void addSub(SAOMenuNode node) {
            this.staticSubNodes.add(node);
        }

        public String getLabel() {
            return this.label;
        }

        public ResourceLocation getIcon() {
            return this.icon;
        }

        public ResourceLocation getIconOn() {
            return this.iconOn;
        }

        public List<SAOMenuNode> getSubNodes() {
            return this.dynamicSubNodes != null ? this.dynamicSubNodes.get() : this.staticSubNodes;
        }

        public void runAction() {
            SkillEditBridge.run(this.label, this.action);
        }
    }
}

