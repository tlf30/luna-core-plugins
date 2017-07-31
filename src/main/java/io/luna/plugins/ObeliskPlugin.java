package io.luna.plugins;

import com.google.gson.JsonElement;
import io.luna.LunaContext;
import io.luna.game.event.Event;
import io.luna.game.event.impl.ObjectClickEvent.ObjectFirstClickEvent;
import io.luna.game.model.Area;
import io.luna.game.model.Position;
import io.luna.game.model.mobile.Animation;
import io.luna.game.model.mobile.Graphic;
import io.luna.game.model.mobile.Player;
import io.luna.game.plugin.Plugin;
import io.luna.net.msg.out.GameChatboxMessageWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Trevor Flynn {@literal <trevorflynn@liquidcrystalstudios.com>}
 */
public class ObeliskPlugin implements Plugin {
    // TODO replace with activated models
    // TODO only loop through current region players
    // TODO lock movement on teleport
    // TODO destination should be an area as well

    private LunaContext context;
    private File config;

    private final Graphic GRAPHIC = new Graphic(342);
    private final Animation ANIMATION = new Animation(1816);
    private final int ACTIVATED_OBELISK = 14825;
    private final Map<Integer, Obelisk> OBELISKS = new HashMap<>();
    private final List<Integer> obelisksActivated = new ArrayList<>();

    @Override
    public String getName() {
        return "Obelisks";
    }

    @Override
    public int getVersionID() {
        return 0;
    }

    @Override
    public void init(LunaContext context, File config, JsonElement reader) {
        this.config = config;
        this.context = context;
        OBELISKS.put(14829, new Obelisk(14829, new Position(3156, 3620), Area.create(3154, 3618, 3158, 3622))); // level 13
        OBELISKS.put(14830, new Obelisk(14830, new Position(3227, 3667), Area.create(3225, 3665, 3229, 3669))); // level 19
        OBELISKS.put(14827, new Obelisk(14827, new Position(3035, 3732), Area.create(3033, 3730, 3037, 3733))); // level 27
        OBELISKS.put(14828, new Obelisk(14828, new Position(3106, 3794), Area.create(3104, 3792, 3108, 3796))); // level 35
        OBELISKS.put(14826, new Obelisk(14826, new Position(2980, 3866), Area.create(2978, 3864, 2982, 3868))); // level 44
        OBELISKS.put(14831, new Obelisk(14831, new Position(3307, 3916), Area.create(3306, 3914, 3310, 3918))); // level 50

    }

    @Override
    public void start() {

    }

    @Override
    public void event(Event event) {
        if (event instanceof ObjectFirstClickEvent) {
            if (OBELISKS.keySet().contains(((ObjectFirstClickEvent) event).id())) {
                activate(((ObjectFirstClickEvent) event).plr(), OBELISKS.get(((ObjectFirstClickEvent) event).id()));
            }
        }
    }

    private void activate(Player plr, Obelisk obelisk) {
        if (obelisksActivated.contains(obelisk.id)) {
            plr.queue(new GameChatboxMessageWriter("This Obelisk has already been activated!"));
        } else {
            obelisksActivated.add(obelisk.id);
            plr.queue(new GameChatboxMessageWriter("You activate the ancient Obelisk..."));
            Random rand = new Random();
            int nextObelisk = rand.nextInt(OBELISKS.keySet().size());
            //TODO
        }
    }

    final class Obelisk {

        int id;
        Position destination;
        Area teleportArea;

        Obelisk(int id, Position destination, Area teleportArea) {
            this.id = id;
            this.destination = destination;
            this.teleportArea = teleportArea;
        }
    }

}
