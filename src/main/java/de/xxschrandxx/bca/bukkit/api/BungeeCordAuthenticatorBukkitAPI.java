package de.xxschrandxx.bca.bukkit.api;

import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import de.xxschrandxx.bca.bukkit.BungeeCordAuthenticatorBukkit;
import de.xxschrandxx.bca.core.OnlineStatus;

public class BungeeCordAuthenticatorBukkitAPI {

  private BungeeCordAuthenticatorBukkit bcab;

  private ConfigHandler ch;

  public ConfigHandler getConfigHandler() {
    return ch;
  }

  private SQLHandlerBukkit sql;

  public SQLHandlerBukkit getSQL() {
    return sql;
  }

  private boolean sqlerror = false;

  public boolean hasSQLError() {
    return sqlerror;
  }

  private Logger lg;

  public Logger getLogger() {
    return lg;
  }

  public BungeeCordAuthenticatorBukkitAPI(BungeeCordAuthenticatorBukkit bcab) {
    this.bcab = bcab;
    lg = bcab.getLogger();
    ch = new ConfigHandler(bcab);
    try {
      sql = new SQLHandlerBukkit(ch.getHikariConfigFile().toPath(), lg, ch.isDebugging);
    }
    catch (SQLException e) {
      sqlerror = true;
      
    }
  }

  public boolean isAuthenticated(Player player) {
    return isAuthenticated(player.getUniqueId());
  }

  public boolean isAuthenticated(UUID uuid) {
    try {
      return getSQL().getStatus(uuid) == OnlineStatus.authenticated;
    }
    catch (SQLException e) {
      getLogger().warning(getConfigHandler().SQLError);
      return false;
    }
  }

  public int scheduleSyncDelayedTask(Runnable r, int i) {
    return bcab.getServer().getScheduler().scheduleSyncDelayedTask(bcab, r, i);
  }

}
