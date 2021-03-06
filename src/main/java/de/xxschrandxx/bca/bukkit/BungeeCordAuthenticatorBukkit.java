package de.xxschrandxx.bca.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import de.xxschrandxx.api.minecraft.ServerVersion;
import de.xxschrandxx.api.minecraft.otherapi.Version;
import de.xxschrandxx.bca.bukkit.api.BungeeCordAuthenticatorBukkitAPI;
import de.xxschrandxx.bca.bukkit.listeners.*;

public class BungeeCordAuthenticatorBukkit extends JavaPlugin {

  private BungeeCordAuthenticatorBukkitAPI api;

  public BungeeCordAuthenticatorBukkitAPI getAPI() {
    return api;
  }

  private static BungeeCordAuthenticatorBukkit instance;

  public static BungeeCordAuthenticatorBukkit getInstance() {
    return instance;
  }

  public void loadAPI() {
    api = new BungeeCordAuthenticatorBukkitAPI(this);
  }

  public void onEnable() {

    instance = this;

    loadAPI();

    if (getAPI().getConfigHandler().isDebugging)
      getLogger().info("onEnable | loaded BungeeCordAuthenticatorBukkitAPI.");
    if (api == null) {
      getLogger().warning("onEnable | BungeeCordAuthenticatorBukkitAPI is null. disabeling plugin.");
      this.onDisable();
      return;
    }
    if (api.hasSQLError()) {
      getLogger().warning("onEnable | SQL connection failed. Please enter valid SQL connection datas in 'hikariconfig.properties' file.");
      this.onDisable();
      return;
    }

    if (api.getConfigHandler().isDebugging)
      getLogger().info("onEnable | loading listener...");
    getServer().getPluginManager().registerEvents(new AuthenticationListener(), this);
    if (api.getConfigHandler().isDebugging)
      getLogger().info("onEnable | loaded AuthenticationListener");
    getServer().getPluginManager().registerEvents(new BlockListener(), this);
    if (api.getConfigHandler().isDebugging)
      getLogger().info("onEnable | loaded BlockListener");
    getServer().getPluginManager().registerEvents(new EntityListener(), this);
    if (api.getConfigHandler().isDebugging)
      getLogger().info("onEnable | loaded EntityListener");
    getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    if (api.getConfigHandler().isDebugging)
      getLogger().info("onEnable | loaded PlayerListener");
    if (ServerVersion.getVersion().i() >= Version.v1_9.i()) {
      getServer().getPluginManager().registerEvents(new PlayerListener19(), this);
      if (api.getConfigHandler().isDebugging)
        getLogger().info("onEnable | loaded PlayerListener19");
    }
    if (ServerVersion.getVersion().i() >= Version.v1_11.i()) {
      getServer().getPluginManager().registerEvents(new PlayerListener111(), this);
      if (api.getConfigHandler().isDebugging)
        getLogger().info("onEnable | loaded PlayerListener111");
    }

  }

}
