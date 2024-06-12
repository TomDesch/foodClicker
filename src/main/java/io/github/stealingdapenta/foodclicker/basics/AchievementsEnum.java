package io.github.stealingdapenta.foodclicker.basics;

import io.github.stealingdapenta.foodclicker.clickingplayers.ClickingPlayer;
import io.github.stealingdapenta.foodclicker.utils.InventoryManager;
import io.github.stealingdapenta.foodclicker.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.stealingdapenta.foodclicker.basics.Buildings.CAFETERIA;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.CHAIN;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.CHEF;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.COMPANY;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.DELIVERY;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.DRIVETHROUGH;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.FOODTRUCK;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.HOTEL;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.HOUSEHOLDNAME;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.MOM;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.POPUP;
import static io.github.stealingdapenta.foodclicker.basics.Buildings.RESTAURANT;

public enum AchievementsEnum {

    // achievements based on actions or stuff
    OUTSIDER("Outsider", Material.COD, "How could you mis click so badly..?", "&6► &e Earned by clicking outside of the FoodClicker window."), //todo fix?
    INSIDER("Insider", Material.SALMON, "Not even close.", "&6► &e Earned by clicking inside of your own inventory."),
    RIGHT("Right", Material.SHEEP_SPAWN_EGG, "You don't follow the sheep!", "&6► &e Earned by right-clicking the cookie."),
    WHEELIE("Wheelie", Material.SNOWBALL, "They see me rollin...", "&6► &e Earned by clicking with a mousewheel."), //
    NUMERAL("Numberphile", Material.STICK, "Cheater...", "&6► &e Earned by using num keys to click."),
    DROP("Drop", Material.STICK, "... it like it's hot", "Sno00ooOooOO00op!", "&6► &e Earned by using the drop key to click."),
    DARK("Dark side", Material.COAL, "Join the Dark side!", "We have cookies!", "&6► &e Earned by trying out full dark mode."),
    ORTEIL("Orteil", Material.COOKIE, "A true CookieClicker fan.", "&6► &e Earned by clicking on an actual cookie."), // requires the player to have a cookie in their inv so hard achievement
    LEPRECHAUN("Leprechaun&a♣", Material.EMERALD, "You're just lucky.", "You had 0.0001% chance every second to get this.", "&6► &e Earned by being lucky."),
    HEADCASE("Headcase", Material.SKELETON_SKULL, "Nope, completely empty!", "&6► &e Earned by clicking your own head."),
    WELCOME("No problem!", Material.DIAMOND, "All you had to do was ask. ☺", "&6► &e Earned by clicking this achievement slot."),

    // achievements based on statistics
    BONUSCOLLECTOR1("Bonus Collector 1", Material.IRON_NUGGET, "Do it for the vine!", "&6► &e Earned by collecting 10 bonuses."),
    BONUSCOLLECTOR2("Bonus Collector 2", Material.IRON_NUGGET, "I ain't gonna do it.", "&6► &e Earned by collecting 100 bonuses."),
    BONUSCOLLECTOR3("Bonus Collector 3", Material.IRON_INGOT, "I did it for the vine.", "&6► &e Earned by collecting 500 bonuses."),
    BONUSCOLLECTOR4("Bonus Collector 4", Material.IRON_INGOT, "But have you left a review on Spigot?", "&6► &e Earned by collecting 1000 bonuses."),
    BONUSCOLLECTOR5("Bonus Collector 5", Material.IRON_BLOCK, "You can stop now.", "&6► &e Earned by collecting 5.000 bonuses."),
    BONUSCOLLECTOR6("Bonus Collector 6", Material.IRON_SWORD, "You understand the true power of bonuses!", "&6► &e Earned by collecting 10.000 bonuses."),

    AUTOCLICKER1("Clicker 1", Material.APPLE, "And so it begins.", "&6► &e Earned by clicking the apple 1 time."),
    AUTOCLICKER2("Clicker 2", Material.APPLE, "And so it continues.", "&6► &e Earned by clicking the apple 50 times."),
    AUTOCLICKER3("Clicker 3", Material.APPLE, "And the tale lives on.", "&6► &e Earned by clicking the apple 100 times."),
    AUTOCLICKER4("Clicker 4", Material.APPLE, "But then, a cramp kicked in!", "&6► &e Earned by clicking the apple 500 times."),
    AUTOCLICKER5("Clicker 5", Material.GOLDEN_APPLE, "This did not stop our hero!", "&6► &e Earned by clicking the apple 1.000 times."),
    AUTOCLICKER6("Clicker 6", Material.GOLDEN_APPLE, "He continued his journey to the top!", "&6► &e Earned by clicking the apple 5.000 times."),
    AUTOCLICKER7("Clicker 7", Material.GOLDEN_APPLE, "'Third party software', some whispered.", "&6► &e Earned by clicking the apple 10.000 times."),
    AUTOCLICKER8("Clicker 8", Material.GOLDEN_APPLE, "'An autoclicker abuser!', others shouted.", "&6► &e Earned by clicking the apple 25.000 times."),
    AUTOCLICKER9("Clicker 9", Material.ENCHANTED_GOLDEN_APPLE, "But the hero didn't take it to heart.", "&6► &e Earned by clicking the apple 50.000 times."),
    AUTOCLICKER10("Clicker 10", Material.ENCHANTED_GOLDEN_APPLE, "And he kept using an autoclicker, for sure.", "&6► &e Earned by clicking the apple 100.000 times."),
    AUTOCLICKER11("AutoClicker", Material.GOLD_BLOCK, "The end.", "&6► &e Earned by clicking the apple 1.000.000 times."),

    IDLE1("Idle 1", Material.EMERALD, "Short for Integrated Development and Learning Environment.", "&6► &e Earned by getting a passive income of £1 per second."),
    IDLE2("Idle 2", Material.EMERALD, "Not in use or operation; not kept busy.", "&6► &e Earned by getting a passive income of £10 per second."),
    IDLE3("Idle 3", Material.EMERALD, "You made it!", "&6► &e Earned by getting a passive income of £100 per second."),
    IDLE4("Idle 4", Material.EMERALD, "Some would call this being rich.", "&6► &e Earned by getting a passive income of £1.000 per second."),
    IDLE5("Idle 5", Material.EMERALD, "You now earn 50x as much as Jeff Bezos.", "&6► &e Earned by getting a passive income of £100.000 per second."),
    IDLE6("Idle 6", Material.EMERALD_ORE, "You're by far the richest in the world.", "&6► &e Earned by getting a passive income of £100.000.000 per second."),
    IDLE7("Idle 7", Material.EMERALD_ORE, "You could stop hunger in the world.", "&6► &e Earned by getting a passive income of £100.000.000.000 per second."),
    IDLE8("Idle 8", Material.EMERALD_ORE, "But you don't.", "&6► &e Earned by getting a passive income of £100.000.000.000.000 per second."),
    IDLE9("Idle 9", Material.EMERALD_ORE, "You could buy world peace.", "&6► &e Earned by getting a passive income of £100.000.000.000.000.000 per second."),
    IDLE10("Idle 10", Material.EMERALD_BLOCK, "But you didn't do that either.", "&6► &e Earned by getting a passive income of £100.000.000.000.000.000.000 per second."),

    AFK1("AFK 1", Material.FEATHER, "The start of something beautiful.", "&6► &e Earned by playing for 1 minute."),
    AFK2("AFK 2", Material.FEATHER, "10 minutes well spent!", "&6► &e Earned by playing for 10 minuted."),
    AFK3("AFK 3", Material.FEATHER, "Had to kill some time.", "&6► &e Earned by playing for 30 minutes."),
    AFK4("AFK 4", Material.FEATHER, "Every hour, in Europe 60 minutes pass.", "&6► &e Earned by playing for 1 hour."),
    AFK5("AFK 5", Material.FEATHER, "AFK stands for Away From Keyboard.", "&6► &e Earned by playing for 2 hours."),
    AFK6("AFK 6", Material.FEATHER, "This apple wont click itself!", "&6► &e Earned by playing for 5 hours."),
    AFK7("AFK 7", Material.FEATHER, "On the grind.", "&6► &e Earned by playing for 10 hours."),
    AFK8("AFK 8", Material.FEATHER, "What's a day in a lifetime anyway.", "&6► &e Earned by playing for 24 hours."),
    AFK9("AFK 9", Material.FEATHER, "Alright, you're actually AFK then.", "&6► &e Earned by playing for 48 hours."),
    AFK10("AFK 10", Material.CHICKEN, "You could've mastered a skill, but you clicked instead.", "&6► &e Earned by playing for 7 days."),

    MONEYMAN1("Big shot 1", Material.GOLD_NUGGET, "First 100 in the bank!", "&6► &e Earned by having over £100 total earned."),
    MONEYMAN2("Big shot 2", Material.GOLD_NUGGET, "Fiveee thouuuwieee", "&6► &e Earned by having over £5.000 total earned."),
    MONEYMAN3("Big shot 3", Material.GOLD_NUGGET, "Buying gf 10k plx", "&6► &e Earned by having over £10.000 total earned."),
    MONEYMAN4("Big shot 4", Material.GOLD_NUGGET, "Getting there.", "&6► &e Earned by having over £100.000 total earned."),
    MONEYMAN5("Big shot 5", Material.GOLD_INGOT, "Multi-millionaire.", "&6► &e Earned by having over £100.000.000 total earned."),
    MONEYMAN6("Big shot 6", Material.GOLD_INGOT, "Multi-billionaire.", "&6► &e Earned by having over £100.000.000.000 total earned."),
    MONEYMAN7("Big shot 7", Material.GOLD_INGOT, "Multi-whatever.", "&6► &e Earned by having over £100.000.000.000.000 total earned."),
    MONEYMAN8("Big shot 8", Material.GOLD_INGOT, "Yeah, yeah, we get it, you're rich.", "&6► &e Earned by having over £100.000.000.000.000.000 total earned."),
    MONEYMAN9("Big shot 9", Material.GOLD_BLOCK, "Money no longer fills the void.", "&6► &e Earned by having over £100.000.000.000.000.000.000 total earned."),
    MONEYMAN10("Big shot 10", Material.GOLD_BLOCK, "Money is pointless anyway, isn't it.", "&6► &e Earned by having over £100.000.000.000.000.000.000.000 total earned."),


    // have x in the bank at once
    // have x income per click
    // have event x happen y times
    // in the future: prestige x times
    // prestige with x money in the bank
    // prestige with x money made during this session

    // achievements based on buildings
    MOMSCOLLECTOR("Baking Committee 1", MOM, "You can hear them discussing from afar.", "&6► &e Earned by owning 100 XXXXX in one session."),
    MOMSCOLLECTOR2("Baking Committee 2", MOM, "You can hear them discussing from afar.", "&6► &e Earned by owning 500 XXXXX in one session."),
    MOMSCOLLECTOR3("Baking Committee 3", MOM, "I hear rumours about striking...", "&6► &e Earned by owning 1000 XXXXX in one session."),

    CHEFSCOLLECTOR("Food Inspection 1", CHEF, "What does an upset chef make food with?", "Angrydients.", "&6► &e Earned by owning 100 XXXXX in one session."),
    CHEFSCOLLECTOR2("Food Inspection 2", CHEF, "Why was the French chef contemplating suicide?", "Because he'd lost the *huile d'olive.*", "&6► &e Earned by owning 500 XXXXX in one session."),
    CHEFSCOLLECTOR3("Food Inspection 3", CHEF, "Why did the Hipster Chef burn his tongue?", "He ate his food before it was cool.", "&6► &e Earned by owning 1000 XXXXX in one session."),

    CAFETERIASCOLLECTOR("Take a seat 1", CAFETERIA, "The chemistry department cafeteria has good food, but finding a place to sit can be a challenge.", "They only have periodic tables.", "&6► &e " +
            "Earned by owning 100 XXXXX in one session."),
    CAFETERIASCOLLECTOR2("Take a seat 2", CAFETERIA, "Why is Cafeteria called a Cafeteria?", "Because it's a coffee-tea-area.", "&6► &e Earned by owning 500 XXXXX in one session."),
    CAFETERIASCOLLECTOR3("Take a seat 3", CAFETERIA, "Good morning Sir what would you like in your sandwich?", "Buddhist: 'Make me one with everything.'", "&6► &e Earned by owning 1000 XXXXX in one" +
            " session."),

    DELIVERYSCOLLECTOR("Delivered 1", DELIVERY, "All jokes can be funny if they have the right delivery.", "Except abortion jokes. There is no delivery.", "&6► &e Earned by owning 100 XXXXX in one " +
            "session."),
    DELIVERYSCOLLECTOR2("Delivered 2", DELIVERY, "What do you call a bread delivery service?", "Uber wheats.", "&6► &e Earned by owning 500 XXXXX in one session."),
    DELIVERYSCOLLECTOR3("Delivered 3", DELIVERY, "A delivery man is carrying a box to a house when, suddenly, he drops it.", "Ups!", "&6► &e Earned by owning 1000 XXXXX in one session."),

    DRIVETHROUGHSCOLLECTOR("500 miles", DRIVETHROUGH, "Why do banks have drive thru windows?", "So the cars can meet their real owners.", "&6► &e Earned by owning 100 XXXXX in one session."),
    DRIVETHROUGHSCOLLECTOR2("500 more", DRIVETHROUGH, "Would you like some fries with that?", "&6► &e Earned by owning 500 XXXXX in one session."),
    DRIVETHROUGHSCOLLECTOR3("@your door", DRIVETHROUGH, "Tell Ross Vlog Creations I said hi.", "&6► &e Earned by owning 1000 XXXXX in one session."),

    FOODTRUCKSCOLLECTOR("Petty Patty 1", FOODTRUCK, "What kind of food truck serves hamburgers?", "A patty wagon.", "&6► &e Earned by owning 100 XXXXX in one session."),
    FOODTRUCKSCOLLECTOR2("Petty Patty 2", FOODTRUCK, "I used to own a food truck until it broke down.", "Now I own a restaurant.", "&6► &e Earned by owning 500 XXXXX in one session."),
    FOODTRUCKSCOLLECTOR3("Fatty Patty", FOODTRUCK, "How do you stop a food truck?", "Use the lunch brake.", "&6► &e Earned by owning 1000 XXXXX in one session."),

    POPUPSCOLLECTOR("The king of pop 1", POPUP, "I won 3 million on the lottery so I decided to donate a quarter of it to charity.", "Now I have 2.999.999,75.", "&6► &e Earned by owning 100 XXXXX " +
            "in one session."),
    POPUPSCOLLECTOR2("The king of pop 2", POPUP, "The question isn't at what age I want to retire, it's at what income.", "&6► &e Earned by owning 500 XXXXX in one session."),
    POPUPSCOLLECTOR3("The king of pop 3", POPUP, "Never lend money to a friend. It's dangerous. It could damage his memory.", "&6► &e Earned by owning 1000 XXXXX in one session."),

    RESTAURANTSCOLLECTOR("Delicious 1", RESTAURANT, "If you’re waiting for the waiter at a restaurant, aren’t you the waiter?", "&6► &e Earned by owning 100 XXXXX in one session."),
    RESTAURANTSCOLLECTOR2("Delicious 2", RESTAURANT, "Jesus walks in a restaurant: 'A table for 26, please.'", "&6► &e Earned by owning 500 XXXXX in one session."),
    RESTAURANTSCOLLECTOR3("Delicious 3", RESTAURANT, "Did you hear about the restaurant they're putting on the moon?", "Good food, but no atmosphere...", "&6► &e Earned by owning 1000 XXXXX in one " +
            "session."),

    HOTELSCOLLECTOR("Monopoly 1", HOTEL, "I once stayed over at a hotel.", "The place is nice, but the room I stayed in is just terrible.", "You could say that room was not their strongest suite.",
                    "&6► &e Earned by owning 100 XXXXX in one session."),
    HOTELSCOLLECTOR2("Monopoly 2", HOTEL, "What did the two knights say when they got to the hotel?", "We’d like a room for two nights please.", "&6► &e Earned by owning 500 XXXXX in one session."),
    HOTELSCOLLECTOR3("Monopoly 3", HOTEL, "A photon checks into a hotel...", "The receptionist asks him if he needs help with any baggage.", "Photon: 'No thanks, I'm traveling light.'", "&6► &e " +
            "Earned by owning 1000 XXXXX in one session."),

    CHAINSCOLLECTOR("The ol' ball 'n 1", CHAIN, "I need a new bicycle chain.", "Can anyone give me any links?", "&6► &e Earned by owning 100 XXXXX in one session."),
    CHAINSCOLLECTOR2("The ol' ball 'n 2", CHAIN, "Can anyone give me any links?", "It will be for people who love meat tender.", "&6► &e Earned by owning 500 XXXXX in one session."),
    CHAINSCOLLECTOR3("The ol' ball 'n 3", CHAIN, "How do Knights communicate?", "Chain mail.", "&6► &e Earned by owning 1000 XXXXX in one session."),

    COMPANYSCOLLECTOR("Never alone 1", COMPANY, "How can you keep the office clean?", "Stay at home.", "&6► &e Earned by owning 100 XXXXX in one session."),
    COMPANYSCOLLECTOR2("Never alone 2", COMPANY, "HR: 'What's your biggest weakness?'", "Me: 'Interviews.'", "HR: 'And besides that?'", "Me: 'Follow up questions.'", "&6► &e Earned by owning 500 " +
            "XXXXX in one session."),
    COMPANYSCOLLECTOR3("Never alone 3", COMPANY, "Laugh at your problems.", "Everyone else does too.", "&6► &e Earned by owning 1000 XXXXX in one session."),

    HOUSEHOLDNAMESCOLLECTOR("Hold up 1", HOUSEHOLDNAME, "I'm currently boycotting any company that sells items I can't afford.", "&6► &e Earned by owning 100 XXXXX in one session."),
    HOUSEHOLDNAMESCOLLECTOR2("Hold up 2", HOUSEHOLDNAME, "Always borrow money from a pessimist. He won't expect it back.", "&6► &e Earned by owning 500 XXXXX in one session."),
    HOUSEHOLDNAMESCOLLECTOR3("Hold up 3", HOUSEHOLDNAME, "Retirement is the time in your life when time is no longer money.", "&6► &e Earned by owning 1000 XXXXX in one session."),

    ;

    private static final InventoryManager im = InventoryManager.getInstance();
    private final String achievement;
    private final Material material;
    private final String[] lore;
    private final Buildings b;

    AchievementsEnum(String achievement, Material material, String... lore) {
        this.achievement = achievement;
        this.material = material;
        this.lore = lore;
        this.b = null;
    }

    AchievementsEnum(String achievement, Buildings b, String... lore) {
        this.achievement = achievement;
        this.material = b.getMaterial();
        this.b = b;
        this.lore = Arrays.stream(lore).map(s -> (s.replace("XXXXX", "&b" + getBuilding().getName() + "s&e"))).toArray(String[]::new);
    }

    public Buildings getBuilding() {
        return this.b;
    }

    public String getAchievement() {
        return achievement;
    }

    public ItemStack createItem(ClickingPlayer cp) {
        String titleColor = cp.getSettings().getPrimaryLoreColor();

        return new ItemBuilder(getMaterial())
                .setDisplayName(titleColor + getAchievement())
                .addLore(fixLore(cp))
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                .addItemFlags(ItemFlag.HIDE_POTION_EFFECTS).create();
    }

    public Material getMaterial() {
        return material;
    }

    public String[] getLore() {
        return lore;
    }

    private List<String> fixLore(ClickingPlayer cp) {
        String textColor = cp.getSettings().getTextLoreColor();
        List<String> newLore = new ArrayList<>();
        List<String> temporaryLines;
        for (String s : getLore()) {
            if (s.toLowerCase().contains("earned by")) {
                temporaryLines = im.makeLore30CharsPerLine(s, "&e");
            } else {
                temporaryLines = im.makeLore30CharsPerLine(s, textColor);
            }

            newLore.addAll(temporaryLines);
        }
        return newLore;
    }

    public boolean unlocked(ClickingPlayer cp) {
        return (cp.getData().getAchievements().get(getAchievement()));
    }

    public void unlock(ClickingPlayer cp) {
        if (!unlocked(cp)) {
            cp.getData().getAchievements().put(getAchievement(), true);
            cp.doSoundEffect(4);
            cp.doFireWorks(5, 0);
            cp.doFireWorks(5, 1);
            cp.doMessage("Congratulations! You've unlocked the &b" + getAchievement() + " &eachievement!");
        }
    }
}
