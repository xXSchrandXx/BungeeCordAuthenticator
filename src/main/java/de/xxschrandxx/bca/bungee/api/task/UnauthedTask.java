package de.xxschrandxx.bca.bungee.api.task;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import de.xxschrandxx.bca.bungee.BungeeCordAuthenticatorBungee;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class UnauthedTask {

  private final ScheduledTask kickTask;

  private final ScheduledTask messageTask;

  public UnauthedTask(BungeeCordAuthenticatorBungee bcab, ProxiedPlayer player) {
    if (bcab.getAPI().getConfigHandler().UnauthenticatedKickEnabled) {
      kickTask = bcab.getProxy().getScheduler().schedule(bcab, new Runnable() {
        @Override
        public void run() {
          player.disconnect(new TextComponent(bcab.getAPI().getConfigHandler().UnauthenticatedKickMessage));
        }
      }, bcab.getAPI().getConfigHandler().UnauthenticatedKickLength, TimeUnit.MINUTES);
    }
    else
      kickTask = null;
    if (bcab.getAPI().getConfigHandler().UnauthenticatedReminderEnabled) {
      messageTask = bcab.getProxy().getScheduler().schedule(bcab, new Runnable(){
        @Override
        public void run() {
          try {
            if (bcab.getAPI().getSQL().checkPlayerEntry(player)) {
              player.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().UnauthenticatedReminderMessageLogin));
            }
            else {
              player.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().UnauthenticatedReminderMessageRegister));
            }
          }
          catch (SQLException e) {
            e.printStackTrace();
          }
        }
      }, 0, bcab.getAPI().getConfigHandler().UnauthenticatedReminderInterval, TimeUnit.SECONDS);
    }
    else
      messageTask = null;
  }

  public void cancel() {
    if (kickTask != null)
      kickTask.cancel();
    if (messageTask != null)
      messageTask.cancel();
  }

}
