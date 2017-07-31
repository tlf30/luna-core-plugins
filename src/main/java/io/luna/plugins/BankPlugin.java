package io.luna.plugins;

import com.google.gson.JsonElement;
import io.luna.LunaContext;
import io.luna.game.event.Event;
import io.luna.game.event.impl.ButtonClickEvent;
import io.luna.game.event.impl.ObjectClickEvent.ObjectFirstClickEvent;
import io.luna.game.plugin.Plugin;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Trevor Flynn {@literal <trevorflynn@liquidcrystalstudios.com>}
 */
public class BankPlugin implements Plugin {

    private LunaContext context;
    private File config;
    private JsonElement reader;

    @Override
    public String getName() {
        return "Banking";
    }

    @Override
    public int getVersionID() {
        return 0;
    }

    @Override
    public void init(LunaContext context, File config, JsonElement reader) {
        this.config = config;
        this.context = context;
        this.reader = reader;
    }

    @Override
    public void start() {

    }

    @Override
    public void event(Event event) {
        if (event instanceof ObjectFirstClickEvent) {
            ObjectFirstClickEvent ofce = (ObjectFirstClickEvent) event;
            if (Arrays.asList(readConfig()).contains(new Integer(ofce.id()))) {
                ofce.plr().getBank().open();
            }
        } else if (event instanceof ButtonClickEvent) {
            ButtonClickEvent click = (ButtonClickEvent) event;
            if (click.id() == 5387) {
                click.plr().getAttributes().get("withdraw_as_note").set(false);
            } else if (click.id() == 5386) {
                click.plr().getAttributes().get("withdraw_as_note").set(true);
            }
        }
    }
    
    private Integer[] readConfig() {
        if (reader.isJsonArray()) {
            ArrayList<Integer> lines = new ArrayList<>();
            for (JsonElement entry : reader.getAsJsonArray()) {
                lines.add(entry.getAsInt());
            }
            return lines.toArray(new Integer[lines.size()]);
        }
        return new Integer[] {3193, 2213, 3095};
    }

}
