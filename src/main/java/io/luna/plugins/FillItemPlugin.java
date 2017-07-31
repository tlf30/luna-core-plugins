
package io.luna.plugins;

import com.google.gson.JsonElement;
import io.luna.LunaContext;
import io.luna.game.action.ProducingAction;
import io.luna.game.event.Event;
import io.luna.game.event.impl.ItemOnObjectEvent;
import io.luna.game.model.item.Item;
import io.luna.game.model.mobile.Animation;
import io.luna.game.model.mobile.Player;
import io.luna.game.plugin.Plugin;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Trevor Flynn {@literal <trevorflynn@liquidcrystalstudios.com>}
 */
public class FillItemPlugin implements Plugin {

    private LunaContext context;
    private File config;
    
    private final Animation ANIMATION = new Animation(832);
    private final Integer[] WATER_SOURCES = {153, 879, 880, 34579, 2864, 6232, 878, 884, 3359, 3485, 4004, 4005,
  5086, 6097, 8747, 8927, 9090, 6827, 3460};
    
    private final Map<Integer, Integer> FILLABLES = new HashMap<>(); 
    
    @Override
    public String getName() {
        return "FillItem";
    }

    @Override
    public int getVersionID() {
        return 0;
    }

    @Override
    public void init(LunaContext context, File config, JsonElement reader) {
        this.config = config;
        this.context = context;
        FILLABLES.put(1923, 1921); //Bowl
        FILLABLES.put(229, 227); //Vial
        FILLABLES.put(1925, 1929); //Bucket
        FILLABLES.put(1980, 4458); //Cup
        FILLABLES.put(1935, 1937); //Jug
    }

    @Override
    public void start() {
        
    }

    @Override
    public void event(Event event) {
        if (event instanceof ItemOnObjectEvent) {
            if (Arrays.asList(WATER_SOURCES).contains(((ItemOnObjectEvent) event).objectId())) {
                int fillable = FILLABLES.get(((ItemOnObjectEvent) event).itemId());
                ((ItemOnObjectEvent) event).plr().submitAction(new FillAction(((ItemOnObjectEvent) event).plr(), ((ItemOnObjectEvent) event).itemId(), fillable));
                event.terminate();
            }
        }
    }
    
    final class FillAction extends ProducingAction {
        final int oldID, newID;
        final Player plr;
        FillAction(Player plr, int oldID, int newID) {
            super(plr, true, 1);
            this.oldID = oldID;
            this.newID = newID;
            this.plr = plr;
        }

        @Override
        protected Item[] remove() {
            return new Item[] {new Item(oldID)};
        }

        @Override
        protected Item[] add() {
            return new Item[] {new Item(newID)};
        }
        
        @Override
        protected void onProduce() {
            plr.animation(ANIMATION);
        }
    }
    
}
