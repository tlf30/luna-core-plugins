
package io.luna.plugins;

import com.google.gson.JsonElement;
import io.luna.LunaContext;
import io.luna.game.event.Event;
import io.luna.game.event.impl.ButtonClickEvent;
import io.luna.game.event.impl.EquipmentChangeEvent;
import static io.luna.game.model.item.Equipment.RING;
import io.luna.game.model.mobile.Player;
import io.luna.game.plugin.Plugin;
import io.luna.net.msg.out.ForceTabMessageWriter;
import io.luna.net.msg.out.GameChatboxMessageWriter;
import io.luna.net.msg.out.TabInterfaceMessageWriter;
import java.io.File;
import java.util.Random;

/**
 *
 * @author Trevor Flynn {@literal <trevorflynn@liquidcrystalstudios.com>}
 */
public class RingPlugin implements Plugin {

    private LunaContext context;
    private File config;
    
    private final int[] EGGS = {3689, 3690, 3691, 3692, 3693, 3694};
    private final int EASTER_RING = 7927;
    private final int RING_OF_STONE = 6583;
    private final int STONE_MORPH = 2626;
    
    @Override
    public String getName() {
        return "RingMorph";
    }

    @Override
    public int getVersionID() {
        return 0;
    }

    @Override
    public void init(LunaContext context, File config, JsonElement reader) {
        this.config = config;
        this.context = context;
    }

    @Override
    public void start() {
        
    }

    @Override
    public void event(Event event) {
        if (event instanceof EquipmentChangeEvent) {
            //EquipmentChangeEvent ece = (EquipmentChangeEvent) event;
            if (((EquipmentChangeEvent) event).index() == RING && ((EquipmentChangeEvent) event).newId().isPresent()) {
                if (((EquipmentChangeEvent) event).newId().getAsInt() == RING_OF_STONE) {
                    morph(((EquipmentChangeEvent) event).plr(), event, STONE_MORPH);
                } else if (((EquipmentChangeEvent) event).newId().getAsInt() == EASTER_RING) {
                    Random r = new Random();
                    morph(((EquipmentChangeEvent) event).plr(), event, EGGS[r.nextInt(EGGS.length)]);
                }
            }
        } else if (event instanceof ButtonClickEvent && ((ButtonClickEvent) event).id() == 6020) {
            unmorph(((ButtonClickEvent) event).plr());
        }
    }
    
    private void morph(Player plr, Event event, int to) {
        for (int i = 0; i < 14; i++) {
            if (i != 3) {
                plr.queue(new TabInterfaceMessageWriter(i, -1));
            }
        }
        plr.queue(new ForceTabMessageWriter(3));
        plr.queue(new TabInterfaceMessageWriter(3, 6014));
        plr.getWalkingQueue().setLocked(true);
        plr.transform(to);
        event.terminate();
    }
    
    private void unmorph(Player plr) {
        if (plr.getInventory().computeRemainingSize() > 1) {
            plr.getEquipment().unequip(RING);
            plr.displayTabInterfaces();
            plr.getWalkingQueue().setLocked(false);
            plr.untransform();
        } else {
            plr.queue(new GameChatboxMessageWriter("You do not have enough space in your inventory."));
        }
    }
    
}
