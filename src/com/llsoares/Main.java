package com.llsoares;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private BowShotListener bowShotListener = new BowShotListener();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(bowShotListener, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
