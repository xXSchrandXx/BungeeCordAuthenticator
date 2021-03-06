package de.xxschrandxx.bca.bungee.command;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.IllegalFormatException;
import java.util.UUID;

import de.xxschrandxx.bca.bungee.BungeeCordAuthenticatorBungee;
import de.xxschrandxx.bca.core.OnlineStatus;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BCAB extends Command {

  BungeeCordAuthenticatorBungee bcab;

  public BCAB(BungeeCordAuthenticatorBungee bcab) {
    super("bcab");
    this.bcab = bcab;
  }

  @Override
  public void execute(CommandSender sender, String[] args) {
    if (!sender.hasPermission("bcab.admin")) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABPermission.replace("%permission%", "bcab.admin")));
      return;
    }
    if (args.length == 1) {
      if (args[0].equalsIgnoreCase("reload")) {
        executereload(sender);
        return;
      }
      if (args[0].equalsIgnoreCase("version")) {
        executeversion(sender);
        return;
      }
    }
    if (args.length == 2) {
      if (args[0].equalsIgnoreCase("forcelogin")) {
        executeforcelogin(sender, args[1]);
        return;
      }
      if (args[0].equalsIgnoreCase("forcereset")) {
        executeforcereset(sender, args[1]);
        return;
      }
      if (args[0].equalsIgnoreCase("info")) {
        executeinfo(sender, args[1]);
        return;
      }
    }
    if (args.length == 3) {
      if (args[0].equalsIgnoreCase("forceregister")) {
        executeforceregister(sender, args[1], args[2]);
        return;
      }
      if (args[0].equalsIgnoreCase("forcepassword")) {
        executeforcepassword(sender, args[1], args[2]);
        return;
      }
    }
    sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABUsage));
  }

  public void executereload(CommandSender sender) {
    if (!sender.hasPermission("bcab.admin.reload")) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABPermission.replace("%permission%", "bcab.admin.forcelogin")));
      return;
    }
    sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABSQLshutdown));
    bcab.getAPI().getSQL().shutdown();
    bcab.loadAPI();
    for (ProxiedPlayer player : bcab.getProxy().getPlayers()) {
      try {
        if (bcab.getAPI().getSQL().getStatus(player) != OnlineStatus.authenticated)
          return;
        bcab.getAPI().setAuthenticated(player);
      }
      catch (SQLException e) {
        player.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().BCABReloadLogin));
      }
    }
    sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABReload));
  }

  public void executeforcelogin(CommandSender sender, String useroruuid) {
    if (!sender.hasPermission("bcab.admin.forcelogin")) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABPermission.replace("%permission%", "bcab.admin.forcelogin")));
      return;
    }
    if (useroruuid.isEmpty() || useroruuid.isBlank()) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABUserUuidEmpty));
      return;
    }
    ProxiedPlayer player = bcab.getProxy().getPlayer(useroruuid);
    if (player == null) {
      try {
        UUID uuid = UUID.fromString(useroruuid);
        player = bcab.getProxy().getPlayer(uuid);
      }
      catch (IllegalFormatException e) {}
    }
    if (player == null) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABUserUuidNull));
      return;
    }
    if (!player.isConnected()) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABNotConnected));
      return;
    }
    if (bcab.getAPI().isAuthenticated(player)) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABAlreadyAuthenticated));
      return;
    }
    try {
      bcab.getAPI().setAuthenticated(player);
    }
    catch (SQLException e) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().SQLError));
      return;
    }
    sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABForceLoginSuccess.replace("%player%", useroruuid)));
  }

  public void executeforcereset(CommandSender sender, String useroruuid) {
    if (!sender.hasPermission("bcab.admin.forcereset")) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABPermission.replace("%permission%", "bcab.admin.forcereset")));
      return;
    }
    if (useroruuid.isEmpty() || useroruuid.isBlank()) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABUserUuidEmpty));
      return;
    }
    ProxiedPlayer player = bcab.getProxy().getPlayer(useroruuid);
    if (player == null) {
      try {
        UUID uuid = UUID.fromString(useroruuid);
        player = bcab.getProxy().getPlayer(uuid);
      }
      catch (IllegalFormatException e) {}
    }
    if (player == null) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABUserUuidNull));
      return;
    }
    if (!bcab.getAPI().isAuthenticated(player)) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABNotAuthenticated));
      return;
    }
    try {
      bcab.getAPI().removePlayerEntry(player.getUniqueId());
    }
    catch (SQLException e) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().SQLError));
      return;
    }
    sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABForceResetSuccess.replace("%player%", useroruuid)));
  }

  public void executeinfo(CommandSender sender, String useroruuid) {
    if (!sender.hasPermission("bcab.admin.info")) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABPermission.replace("%permission%", "bcab.admin.forcereset")));
      return;
    }
    if (useroruuid.isEmpty() || useroruuid.isBlank()) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABUserUuidEmpty));
      return;
    }
    ProxiedPlayer player = bcab.getProxy().getPlayer(useroruuid);
    if (player == null) {
      try {
        UUID uuid = UUID.fromString(useroruuid);
        player = bcab.getProxy().getPlayer(uuid);
      }
      catch (IllegalFormatException e) {}
    }
    if (player == null) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABUserUuidNull));
      return;
    }
    if (!bcab.getAPI().isAuthenticated(player)) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABNotAuthenticated));
      return;
    }
    try {
      String ip = bcab.getAPI().getSQL().getRegisteredIP(player);
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABInfoSuccess
        .replace("%player%", useroruuid)
        .replace("%status%", bcab.getAPI().getSQL().getStatus(player).name())
        .replace("%registerdate%", bcab.getAPI().getSQL().regdateformat.format(bcab.getAPI().getSQL().getRegisterDate(player)))
        .replace("%lastseen%", bcab.getAPI().getSQL().lastseenformat.format(bcab.getAPI().getSQL().getLastSeen(player)))
        .replace("%lastip%", bcab.getAPI().getSQL().getLastIP(player))
        .replace("%registerip%", ip)
        .replace("%version%", bcab.getAPI().getSQL().getVersion(player).toString())
        .replace("%registeripcount%", bcab.getAPI().getSQL().getRegisteredIPCount(ip).toString())
      ));
    }
    catch (SQLException | ParseException e) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().SQLError));
      return;
    }
  }

  public void executeforceregister(CommandSender sender, String useroruuid, String password) {
    if (!sender.hasPermission("bcab.admin.forceregister")) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABPermission.replace("%permission%", "bcab.admin.forceregister")));
      return;
    }
    if (useroruuid.isEmpty() || useroruuid.isBlank()) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABUserUuidEmpty));
      return;
    }
    if (password.isEmpty() || password.isBlank()) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABPasswordEmpty));
      return;
    }
    ProxiedPlayer player = bcab.getProxy().getPlayer(useroruuid);
    if (player == null) {
      try {
        UUID uuid = UUID.fromString(useroruuid);
        player = bcab.getProxy().getPlayer(uuid);
      }
      catch (IllegalFormatException e) {}
    }
    if (player == null) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABUserUuidNull));
      return;
    }
    if (bcab.getAPI().isAuthenticated(player)) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABAlreadyAuthenticated));
      return;
    }
    try {
      bcab.getAPI().createPlayerEntry(player, password);
    }
    catch (SQLException e) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().SQLError));
      return;
    }
    sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABForceRegisterSuccess.replace("%player%", useroruuid)));
  }

  public void executeforcepassword(CommandSender sender, String useroruuid, String password) {
    if (!sender.hasPermission("bcab.admin.forcepassword")) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABPermission.replace("%permission%", "bcab.admin.forcepassword")));
      return;
    }
    if (useroruuid.isEmpty() || useroruuid.isBlank()) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABUserUuidEmpty));
      return;
    }
    if (password.isEmpty() || password.isBlank()) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABPasswordEmpty));
      return;
    }
    ProxiedPlayer player = bcab.getProxy().getPlayer(useroruuid);
    if (player == null) {
      try {
        UUID uuid = UUID.fromString(useroruuid);
        player = bcab.getProxy().getPlayer(uuid);
      }
      catch (IllegalFormatException e) {}
    }
    if (player == null) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABUserUuidNull));
      return;
    }
    if (!bcab.getAPI().isAuthenticated(player)) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABNotAuthenticated));
      return;
    }
    try {
      bcab.getAPI().setPassword(player, password);
    }
    catch (SQLException e) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().SQLError));
      return;
    }
    sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABForcePasswordSuccess.replace("%player%", useroruuid)));
  }

  public void executeversion(CommandSender sender) {
    if (!sender.hasPermission("bcab.admin.version")) {
      sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABPermission.replace("%permission%", "bcab.admin.version")));
      return;
    }
    sender.sendMessage(new TextComponent(bcab.getAPI().getConfigHandler().Prefix + bcab.getAPI().getConfigHandler().BCABVersion.replace("%pluginname%", bcab.getDescription().getName()).replace("%version%", bcab.getDescription().getVersion()).replace("%server%", bcab.getProxy().getVersion())));
  }

}
