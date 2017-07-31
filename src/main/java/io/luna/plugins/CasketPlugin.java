package io.luna.plugins;

import com.google.common.collect.Range;
import com.google.gson.JsonElement;
import io.luna.LunaContext;
import io.luna.game.event.Event;
import io.luna.game.event.impl.ItemClickEvent.ItemFirstClickEvent;
import io.luna.game.model.Chance;
import io.luna.game.model.item.Inventory;
import io.luna.game.model.item.Item;
import io.luna.game.model.item.RationalItem;
import io.luna.game.model.item.RationalItemTable;
import io.luna.game.plugin.Plugin;
import java.io.File;

/**
 *
 * @author Trevor Flynn {@literal <trevorflynn@liquidcrystalstudios.com>}
 */
public class CasketPlugin implements Plugin {

    private LunaContext context;
    private File config;

    private final Item CASKET = new Item(405);
    private final Item DEFAULT = new Item(995, 500);
    private final RationalItemTable CASKET_TABLE = new RationalItemTable(
            new RationalItem(995, Range.closed(1, 3000), Chance.COMMON), // Coins
            new RationalItem(1623, 1, Chance.COMMON), // Uncut sapphire
            new RationalItem(1621, 1, Chance.UNCOMMON), // Uncut emerald
            new RationalItem(1619, 1, Chance.UNCOMMON), // Uncut ruby
            new RationalItem(1617, 1, Chance.RARE), // Uncut diamond
            new RationalItem(987, 1, Chance.RARE), // Loop-half of key
            new RationalItem(985, 1, Chance.RARE), // Tooth-half of key
            new RationalItem(1454, 1, Chance.COMMON), // Cosmic talisman
            new RationalItem(1452, 1, Chance.UNCOMMON), // Chaos talisman
            new RationalItem(1462, 1, Chance.RARE) // Nature talisman
    );

    @Override
    public String getName() {
        return "Caskets";
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
        if (event instanceof ItemFirstClickEvent) {
            if (((ItemFirstClickEvent) event).id() == CASKET.getId()) {
                Inventory inv = ((ItemFirstClickEvent) event).plr().getInventory();
                inv.remove(CASKET);
                Item i = CASKET_TABLE.selectIndexed().orElse(DEFAULT);
                inv.add(i);
            }
        }
    }

}
