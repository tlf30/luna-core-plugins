package io.luna.plugins;

import com.google.gson.JsonElement;
import io.luna.LunaContext;
import io.luna.game.event.Event;
import io.luna.game.event.impl.ItemClickEvent.ItemFirstClickEvent;
import io.luna.game.model.def.ItemDefinition;
import io.luna.game.model.item.Inventory;
import io.luna.game.model.item.Item;
import io.luna.game.model.mobile.Animation;
import io.luna.game.model.mobile.Player;
import io.luna.game.model.mobile.Skill;
import io.luna.game.plugin.Plugin;
import io.luna.net.msg.out.GameChatboxMessageWriter;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trevor Flynn {@literal <trevorflynn@liquidcrystalstudios.com>}
 */
public class EatFoodPlugin implements Plugin {

    private LunaContext context;
    private File config;

    private final Animation ANIMATION = new Animation(829);
    private Map<String, Food> FOOD_TABLE = new HashMap<>();
    private Map<Integer, Food> ID_TO_FOOD = new HashMap<>();

    @Override
    public String getName() {
        return "EatFood";
    }

    @Override
    public int getVersionID() {
        return 0;
    }

    @Override
    public void init(LunaContext context, File config, JsonElement reader) {
        this.config = config;
        this.context = context;
        FOOD_TABLE.put("cooked_meat", new Food(2, 1800, 2142));
        FOOD_TABLE.put("cooked_chicken", new Food(2, 1800, 2140));
        FOOD_TABLE.put("herring", new Food(2, 1800, 347));
        FOOD_TABLE.put("anchovies", new Food(2, 1800, 319));
        FOOD_TABLE.put("redberry_pie", new Food(2, 600, 2325, 2333));
        FOOD_TABLE.put("shrimp", new Food(3, 1800, 315));
        FOOD_TABLE.put("cake", new Food(4, 1800, 1891, 1893, 1895));
        FOOD_TABLE.put("cod", new Food(4, 1800, 339));
        FOOD_TABLE.put("pike", new Food(4, 1800, 351));
        FOOD_TABLE.put("chocolate_cake", new Food(5, 1800, 1897, 1899, 1901));
        FOOD_TABLE.put("mackerel", new Food(6, 1800, 355));
        FOOD_TABLE.put("meat_pie", new Food(6, 600, 2327, 2331));
        FOOD_TABLE.put("plain_pizza", new Food(7, 1800, 2289, 2291));
        FOOD_TABLE.put("apple_pie", new Food(7, 600, 2323, 2335));
        FOOD_TABLE.put("trout", new Food(7, 1800, 333));
        FOOD_TABLE.put("meat_pizza", new Food(8, 1800, 2293, 2295));
        FOOD_TABLE.put("anchovy_pizza", new Food(9, 1800, 2297, 2299));
        FOOD_TABLE.put("salmon", new Food(9, 1800, 329));
        FOOD_TABLE.put("bass", new Food(9, 1800, 365));
        FOOD_TABLE.put("tuna", new Food(10, 1800, 361));
        FOOD_TABLE.put("pineapple_pizza", new Food(11, 1800, 2303));
        FOOD_TABLE.put("lobster", new Food(12, 1800, 379));
        FOOD_TABLE.put("swordfish", new Food(14, 1800, 373));
        FOOD_TABLE.put("monkfish", new Food(16, 1800, 7946));
        FOOD_TABLE.put("karambwan", new Food(18, 600, 3144));
        FOOD_TABLE.put("shark", new Food(20, 1800, 385));
        FOOD_TABLE.put("manta_ray", new Food(22, 1800, 391));
        FOOD_TABLE.put("sea_turtle", new Food(22, 1800, 397));
        FOOD_TABLE.put("tuna_potato", new Food(22, 1800, 7060));
        for (String key : FOOD_TABLE.keySet()) {
            Food f = FOOD_TABLE.get(key);
            for (int id : f.ids) {
                ID_TO_FOOD.put(id, f);
            }
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void event(Event event) {
        if (event instanceof ItemFirstClickEvent) {
            Food food = ID_TO_FOOD.get(((ItemFirstClickEvent) event).id());
            consume(((ItemFirstClickEvent) event).plr(), food, ((ItemFirstClickEvent) event).index());
            event.terminate();
        }
    }

    private void consume(Player plr, Food food, int index) {
        Inventory inv = plr.getInventory();
        Skill hpSkill = plr.skill(Skill.HITPOINTS);
        if (System.currentTimeMillis() - (long) plr.getAttributes().get("last_food_consume").get() <= food.delay) {
            return;
        }
        plr.interruptAction();
        
        Item toConsume = inv.get(index);
        //
        if (inv.remove(toConsume, index)) {
            int nextIndex = 0;
            for (int i = 0; i < food.ids.length; i++) {
                if (food.ids[i] == toConsume.getId()) {
                    nextIndex = i + 1;
                    break;
                }
            }
            plr.queue(new GameChatboxMessageWriter("You eat the " +  ItemDefinition.computeNameForId(toConsume.getId()) + "."));        
            plr.animation(ANIMATION);
            
            if (hpSkill.getLevel() < hpSkill.getStaticLevel()) {
                hpSkill.increaseLevel(food.heal, hpSkill.getStaticLevel());
                 plr.queue(new GameChatboxMessageWriter("It heals some health"));
            }
        }
        //
        plr.getAttributes().get("last_food_consume").set(System.currentTimeMillis());
    }

    final class Food {

        int heal;
        int delay;
        int[] ids;

        Food(int heal, int delay, int... ids) {
            this.heal = heal;
            this.delay = delay;
            this.ids = ids;
        }
    }

}
