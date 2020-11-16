package org.kikikan.dbmblindness;

import org.bukkit.plugin.java.JavaPlugin;
import org.kikikan.deadbymoonlight.DeadByMoonlightAPI;

public class DbMBlindness extends JavaPlugin {

    @Override
    public void onEnable() {
        DeadByMoonlightAPI.addGameComponent(new BlindnessComponent(this, null));
    }
}

