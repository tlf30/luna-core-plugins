
package io.luna.plugins;

import com.google.gson.JsonElement;
import io.luna.LunaContext;
import io.luna.game.event.Event;
import io.luna.game.event.impl.ButtonClickEvent;
import io.luna.game.plugin.Plugin;
import java.io.File;

/**
 *
 * @author Trevor Flynn {@literal <trevorflynn@liquidcrystalstudios.com>}
 */
public class RunPlugin implements Plugin {

    private LunaContext context;
    private File config;
    
    @Override
    public String getName() {
        return "Run";
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
        if (event instanceof ButtonClickEvent) {
            ButtonClickEvent click = (ButtonClickEvent) event;
            if (click.id() == 152) {
                ((ButtonClickEvent) event).plr().getWalkingQueue().setRunning(false);
            } else if (click.id() == 153) {
                ((ButtonClickEvent) event).plr().getWalkingQueue().setRunning(true);
            }
        }
    }
    
}
