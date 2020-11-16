package org.kikikan.dbmblindness;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.kikikan.deadbymoonlight.GameComponent;
import org.kikikan.deadbymoonlight.events.player.survivor.HealthStateChangedEvent;
import org.kikikan.deadbymoonlight.events.world.GameFinishedEvent;
import org.kikikan.deadbymoonlight.events.world.GameStartedEvent;
import org.kikikan.deadbymoonlight.game.Game;
import org.kikikan.deadbymoonlight.game.PerkUser;
import org.kikikan.deadbymoonlight.util.Health;

import java.util.ArrayList;

public class BlindnessComponent extends GameComponent {

    private BlindRunnable runnable;

    public BlindnessComponent(JavaPlugin plugin, Game game) {
        super(plugin, game);
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
        event.getGame().getPlayerManager().getSurvivors().forEach((s) -> players.add(s));
        if (event.getGame().getPlayerManager().getKiller() != null)
            players.add(event.getGame().getPlayerManager().getKiller());
        event.getGame().announce(ChatColor.DARK_PURPLE + "The Entity has shrouded the surrounding area with never-ending darkness!");
        runnable = new BlindRunnable(getPlugin(), players);
    }

    public void onEnd(GameFinishedEvent event){
        runnable.end();
        runnable = null;
    }

    public void onDeathOrEscape(HealthStateChangedEvent event){
        if (event.to == Health.DEAD || event.to == Health.ESCAPED)
            runnable.remove(event.player);
    }
}

class BlindRunnable extends BukkitRunnable {

    private ArrayList<PerkUser> players;

    BlindRunnable(JavaPlugin plugin, ArrayList<PerkUser> p){
        players = p;
        this.runTaskTimer(plugin, 0,5 );
    }

    @Override
    public void run() {
        players.forEach((s) -> {
            Player p = s.getPlayer();
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
        });
    }

    public void remove(PerkUser p){
        players.remove(p);
    }

    public void end(){
        this.cancel();
    }
}