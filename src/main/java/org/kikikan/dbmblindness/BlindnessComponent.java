package org.kikikan.dbmblindness;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.kikikan.deadbymoonlight.GameComponent;
import org.kikikan.deadbymoonlight.events.player.survivor.HealthStateChangedEvent;
import org.kikikan.deadbymoonlight.events.player.survivor.SurvivorLeftEvent;
import org.kikikan.deadbymoonlight.events.world.GameFinishedEvent;
import org.kikikan.deadbymoonlight.events.world.GameStartedEvent;
import org.kikikan.deadbymoonlight.game.Game;
import org.kikikan.deadbymoonlight.game.PerkUser;
import org.kikikan.deadbymoonlight.util.Health;

import java.util.ArrayList;

public class BlindnessComponent extends GameComponent {

    private BlindRunnable runnable;
    static int blindLevel;

    public BlindnessComponent(JavaPlugin plugin, Game game) {
        super(plugin, game);
        BlindnessComponent.blindLevel = (int)getValueFromConfig("level", 1) - 1;
    }

    private ArrayList<PerkUser> players = new ArrayList<>();

    @Override
    public String getName() {
        return "Blindness";
    }

    @Override
    public String getDescription() {
        return "Gives the Players Blindness Potion Effect until they die.";
    }

    public void onInit(GameStartedEvent event){
        players.addAll(event.getGame().getPlayerManager().getSurvivors());
        if (event.getGame().getPlayerManager().getKiller().isPresent())
            players.add(event.getGame().getPlayerManager().getKiller().get());
        event.getGame().announce(ChatColor.DARK_PURPLE + "The Entity has shrouded the surrounding area with never-ending darkness!");
        runnable = new BlindRunnable(getPlugin(), players);
    }

    public void onEnd(GameFinishedEvent event){
        runnable.end();
        runnable = null;
        players.clear();
    }

    public void onDeathOrEscape(HealthStateChangedEvent event){
        if (event.getTo() == Health.DEAD || event.getTo() == Health.ESCAPED)
            players.remove(event.getPerkUser());
    }

    public void leave(SurvivorLeftEvent event){
        players.remove(event.getPerkUser());
    }
}

class BlindRunnable extends BukkitRunnable {

    private final ArrayList<PerkUser> players;

    BlindRunnable(JavaPlugin plugin, ArrayList<PerkUser> p){
        players = p;
        this.runTaskTimer(plugin, 0,5 );
    }

    @Override
    public void run() {
        players.forEach((s) -> {
            Player p = s.getPlayer();
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, BlindnessComponent.blindLevel));
        });
    }

    public void end(){
        players.clear();
        this.cancel();
    }
}