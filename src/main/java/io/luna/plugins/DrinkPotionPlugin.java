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
 * A plugin that adds functionality for drinking potions.
 *
 * SUPPORTS:
 * -> Drinking a variety of potions.
 * -> Potion status and level effects.
 *
 * TODO:
 * -> Support for more potions.
 *
 *
 * @author Trevor Flynn {@literal <trevorflynn@liquidcrystalstudios.com>}
 */
public class DrinkPotionPlugin implements Plugin {

    private LunaContext context;
    private File config;

    private final Item VIAL = new Item(229);
    private final Animation ANIMATION = new Animation(829);
    private final int CONSUME_DELAY = 1200; // TODO confirm if delay is really 1200ms

    private Map<String, Potion> POTIONS = new HashMap<>();
    private Map<Integer, Potion> IDS = new HashMap<>();

    @Override
    public String getName() {
        return "DrinkPotion";
    }

    @Override
    public int getVersionID() {
        return 0;
    }

    @Override
    public void init(LunaContext context, File config, JsonElement reader) {
        this.config = config;
        this.context = context;
        POTIONS.put("zamorak_brew", new Potion(193, 191, 189, 2450) {
            @Override
            public void onEffect(Player p) {
                onZamorakBrew(p);
            }
        });
        POTIONS.put("saradomin_brew", new Potion(6691, 6689, 6687, 6685) {
            @Override
            public void onEffect(Player p) {
                onSaradominBrew(p);
            }
        });
        POTIONS.put("agility_potion", new Potion(3038, 3036, 3034, 3032) {
            @Override
            public void onEffect(Player p) {
                onSkillPotion(p, Skill.AGILITY);
            }
        });
        POTIONS.put("fishing_potion", new Potion(155, 153, 151, 2438) {
            @Override
            public void onEffect(Player p) {
                onSkillPotion(p, Skill.FISHING);
            }
        });
        POTIONS.put("ranging_potion", new Potion(173, 171, 169, 2444) {
            @Override
            public void onEffect(Player p) {
                onCombatPotion(p, Skill.RANGED, false);
            }
        });
        POTIONS.put("magic_potion", new Potion(3046, 3044, 3042, 3040) {
            @Override
            public void onEffect(Player p) {
                onCombatPotion(p, Skill.MAGIC, false);
            }
        });
        POTIONS.put("defence_potion", new Potion(137, 135, 133, 2432) {
            @Override
            public void onEffect(Player p) {
                onCombatPotion(p, Skill.DEFENCE, false);
            }
        });
        POTIONS.put("strength_potion", new Potion(119, 117, 115, 113) {
            @Override
            public void onEffect(Player p) {
                onCombatPotion(p, Skill.STRENGTH, false);
            }
        });
        POTIONS.put("attack_potion", new Potion(125, 123, 121, 2428) {
            @Override
            public void onEffect(Player p) {
                onCombatPotion(p, Skill.ATTACK, false);
            }
        });
        POTIONS.put("super_defence", new Potion(167, 165, 163, 2442) {
            @Override
            public void onEffect(Player p) {
                onCombatPotion(p, Skill.DEFENCE, true);
            }
        });
        POTIONS.put("super_attack", new Potion(149, 147, 145, 2436) {
            @Override
            public void onEffect(Player p) {
                onCombatPotion(p, Skill.ATTACK, true);
            }
        });
        POTIONS.put("super_strength", new Potion(161, 159, 157, 2400) {
            @Override
            public void onEffect(Player p) {
                onCombatPotion(p, Skill.STRENGTH, true);
            }
        });
        POTIONS.put("energy_potion", new Potion(3014, 3012, 3010, 3008) {
            @Override
            public void onEffect(Player p) {
                onEnergyPotion(p, false);
            }
        });
        POTIONS.put("super_energy", new Potion(3022, 3020, 3018, 3016) {
            @Override
            public void onEffect(Player p) {
                onEnergyPotion(p, true);
            }
        });
        POTIONS.put("antipoison_potion", new Potion(179, 177, 175, 24436) {
            @Override
            public void onEffect(Player p) {
                onAntipoison(p, 0);
            }
        });
        POTIONS.put("super_antipoison", new Potion(185, 183, 181, 2448) {
            @Override
            public void onEffect(Player p) {
                onAntipoison(p, 500);
            }
        });
        POTIONS.put("antidote_+", new Potion(5949, 5947, 5945, 5943) {
            @Override
            public void onEffect(Player p) {
                onAntipoison(p, 1000);
            }
        });
        POTIONS.put("antidote_++", new Potion(5958, 5956, 5954, 5952) {
            @Override
            public void onEffect(Player p) {
                onAntipoison(p, 1200);
            }
        });
        POTIONS.put("prayer_potion", new Potion(143, 141, 139, 2434) {
            @Override
            public void onEffect(Player p) {
                onPrayerPotion(p);
            }
        });
        POTIONS.put("anti_fire_potion", new Potion(2458, 2456, 2454, 2452) {
            @Override
            public void onEffect(Player p) {
                onAntifirePotion(p);
            }
        });
        POTIONS.put("super_restore", new Potion(3030, 3028, 3026, 3024) {
            @Override
            public void onEffect(Player p) {
                onRestorePotion(p, true);
            }
        });
        for (String key : POTIONS.keySet()) {
            Potion f = POTIONS.get(key);
            for (int id : f.ids) {
                IDS.put(id, f);
            }
        }
    }
    

    @Override
    public void start() {

    }

    @Override
    public void event(Event event) {
        if (event instanceof ItemFirstClickEvent) {
            if (IDS.containsKey(((ItemFirstClickEvent) event).id())) {
                Potion potion = IDS.get(((ItemFirstClickEvent) event).id());
                consume(((ItemFirstClickEvent) event).plr(), potion, ((ItemFirstClickEvent) event).index());
                event.terminate();
            }
        }
    }

    class Potion {

        int d4, d3, d2, d1;
        int[] ids = new int[4];

        void onEffect(Player plr) {

        }

        Potion(int d1, int d2, int d3, int d4) {
            this.d1 = d1;
            this.d2 = d2;
            this.d3 = d3;
            this.d4 = d4;
            ids[0] = d1;
            ids[1] = d2;
            ids[2] = d3;
            ids[3] = d4;
        }
    }

    private void onZamorakBrew(Player plr) {
        Skill attack = plr.skill(Skill.ATTACK);
        Skill strength = plr.skill(Skill.STRENGTH);
        Skill defence = plr.skill(Skill.DEFENCE);
        Skill hp = plr.skill(Skill.HITPOINTS);
        Skill prayer = plr.skill(Skill.PRAYER);
        //
        attack.increaseLevel(2 + (int) (0.20 * attack.getStaticLevel()));
        strength.increaseLevel(2 + (int) (0.12 * strength.getStaticLevel()));
        defence.decreaseLevel(2 + (int) (0.10 * defence.getStaticLevel()));
        hp.decreaseLevel(2 + (int) (0.10 * hp.getStaticLevel()), 0);
        prayer.increaseLevel((int) (0.10 * prayer.getStaticLevel()));
    }

    private void onSaradominBrew(Player plr) {
        Skill attack = plr.skill(Skill.ATTACK);
        Skill strength = plr.skill(Skill.STRENGTH);
        Skill defence = plr.skill(Skill.DEFENCE);
        Skill hp = plr.skill(Skill.HITPOINTS);
        Skill ranged = plr.skill(Skill.RANGED);
        Skill magic = plr.skill(Skill.MAGIC);
        //
        defence.increaseLevel(2 + (int) (0.20 * defence.getStaticLevel()));
        attack.decreaseLevel((int) (0.10 * attack.getStaticLevel()), 0);
        hp.increaseLevel(2 + (int) (0.15 * hp.getStaticLevel()));
        strength.decreaseLevel((int) (0.10 * strength.getStaticLevel()), 0);
        magic.decreaseLevel((int) (0.10 * magic.getStaticLevel()), 0);
        ranged.decreaseLevel((int) (0.10 * ranged.getStaticLevel()), 0);
    }

    private void onAntipoison(Player plr, int durration) {
        //?
    }

    private void onPrayerPotion(Player plr) {
        Skill prayer = plr.skill(Skill.PRAYER);
        prayer.increaseLevel(7 + (prayer.getStaticLevel() / 4), prayer.getStaticLevel());
    }

    private void onSkillPotion(Player plr, int skillID) {
        Skill skill = plr.skill(skillID);
        skill.increaseLevel(3);
    }
    
    private void onEnergyPotion(Player plr, boolean superPotion) {
        double amount = superPotion ? 0.20 : 0.10;
        plr.setRunEnergy(plr.getRunEnergy() + amount);
    }

    private void onRestorePotion(Player plr, boolean superPotion) {
        for (Skill skill : plr.getSkills()) {
            if (skill.getId() != Skill.PRAYER && skill.getId() != Skill.HITPOINTS) {
                skill.increaseLevel((int) boostAmountRestorePotion(skill.getLevel(), superPotion), skill.getStaticLevel());
            }
        }
        if (superPotion) {
            //If super restore is being sipped, restore prayer as well.
            Skill prayer = plr.skill(Skill.PRAYER);
            prayer.increaseLevel(8 + (prayer.getStaticLevel() / 4), prayer.getStaticLevel());
        }
    }
    
    private double boostAmountRestorePotion(int level, boolean superPotion) {
        return superPotion ? 8 + (0.25 * level) : 10 + (0.30 * level);
    }

    private void onAntifirePotion(Player plr) {
        //?
    }
    
    private double boostAmountAntifirePotion(int level, boolean superPotion) {
        return superPotion ? 5 + (0.15 * level) : 3 + (0.10 * level);
    }

    private void onCombatPotion(Player plr, int skillID, boolean superPotion) {
        Skill skill = plr.skill(skillID);
        skill.increaseLevel((int) boostAmountAntifirePotion(skill.getStaticLevel(), superPotion));
    }

    private void consume(Player plr, Potion potion, int index) {
        Inventory inv = plr.getInventory();
        int[] ids = {potion.d1, potion.d2, potion.d3, potion.d4};
        if (System.currentTimeMillis() - (long) plr.getAttributes().get("last_potion_consume").get() <= CONSUME_DELAY) {
            return;
        }

        plr.interruptAction();
        Item toConsume = inv.get(index);
        if (inv.remove(toConsume, index)) {
            int nextIndex = 0;
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] == toConsume.getId()) {
                    nextIndex = i + 1;
                    break;
                }
            }
            if (ids.length <= nextIndex) {
                inv.add(VIAL);
            } else {
                inv.add(new Item(ids[nextIndex]), index);
            }
            plr.queue(new GameChatboxMessageWriter("You drink some of your " + ItemDefinition.computeNameForId(toConsume.getId()) + "."));
            int dosesLeft = ids.length - nextIndex;
            if (dosesLeft > 0) {
                plr.queue(new GameChatboxMessageWriter("You have " + dosesLeft + " doses of potion left."));
            } else {
                plr.queue(new GameChatboxMessageWriter("You have finished your potion."));
            }
            plr.animation(ANIMATION);
            potion.onEffect(plr);
        }

        //Reset stats
        plr.getAttributes().get("last_food_consume").set(System.currentTimeMillis());
        plr.getAttributes().get("last_potion_consume").set(System.currentTimeMillis());
    }

}
