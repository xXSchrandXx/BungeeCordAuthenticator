package de.xxschrandxx.bca.bungee.api;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import de.xxschrandxx.bca.bungee.BungeeCordAuthenticatorBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigHandler {

  private BungeeCordAuthenticatorBungee bcab;

  public File configyml, messageyml;

  public Configuration config, message;

  private File hikariconfigfile;
  public File getHikariConfigFile() {
    return hikariconfigfile;
  } 

  public ConfigHandler(BungeeCordAuthenticatorBungee bcab) {
    //Setting bcab
    this.bcab = bcab;

    //Loading config.yml
    loadConfig();

    //Loading HikariConfig-Path
    loadHikaryCP();

    //Loading message.yml
    loadMessage();
  }

  //Config Values
  //debug
  public Boolean isDebugging;

  //Sessions
  public Boolean SessionEnabled;
  public Integer SessionLength;

  //Registration
  public Integer MaxAccountsPerIP;
  public Integer MinCharacters;

  //Login
  public Integer MaxAttempts;

  //Protection
  public Boolean AllowServerSwitch;
  public Boolean AllowMessageSend;
  public List<String> AllowedCommands;
  public Boolean AllowMessageReceive;

  //UnauthedTask
  public Boolean UnauthenticatedKickEnabled;
  public Integer UnauthenticatedKickLength;
  public Boolean UnauthenticatedReminderEnabled;
  public Integer UnauthenticatedReminderInterval;

  public void loadConfig() {
    boolean error = false;
    configyml = new File(bcab.getDataFolder(), "config.yml");
    try {
      if (!configyml.exists()) {
        bcab.getDataFolder().mkdirs();
        configyml.createNewFile();
      }
      config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configyml);
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    String path = null;
    //isDebugging
    path = "debug";
    if (config.contains(path)) {
      isDebugging = config.getBoolean(path);
    }
    else {
      bcab.getLogger().warning("loadConfig() | " + path + " is not given. Setting it...");
      config.set(path, false);
      error = true;
    }
    //Sesions
    //SessionEnabled
    path = "session.enabled";
    if (config.contains(path)) {
      SessionEnabled = config.getBoolean(path);
    }
    else {
      bcab.getLogger().warning("loadConfig() | " + path + " is not given. Setting it...");
      config.set(path, false);
      error = true;
    }
    //SessionLength
    path = "session.length";
    if (config.contains(path)) {
      SessionLength = config.getInt(path);
    }
    else {
      bcab.getLogger().warning("loadConfig() | " + path + " is not given. Setting it...");
      config.set(path, 5);
      error = true;
    }
    //Registration
    //MaxAccountsPerIP
    path = "registration.maxaccountsperip";
    if (config.contains(path)) {
      MaxAccountsPerIP = config.getInt(path);
    }
    else {
      bcab.getLogger().warning("loadConfig() | " + path + " is not given. Setting it...");
      config.set(path, 5);
      error = true;
    }
    //MinCharacters
    path = "registration.mincharacters";
    if (config.contains(path)) {
      MinCharacters = config.getInt(path);
    }
    else {
      bcab.getLogger().warning("loadConfig() | " + path + " is not given. Setting it...");
      config.set(path, 8);
      error = true;
    }
    //Login
    //MaxAttempts
    path = "login.maxattempts";
    if (config.contains(path)) {
      MaxAttempts = config.getInt(path);
    }
    else {
      bcab.getLogger().warning("loadConfig() | " + path + " is not given. Setting it...");
      config.set(path, 3);
      error = true;
    }
    //Protection
    //AllowServerSwitch
    path = "protection.allowserverswitch";
    if (config.contains(path)) {
      AllowServerSwitch = config.getBoolean(path);
    }
    else {
      bcab.getLogger().warning("loadConfig() | " + path + " is not given. Setting it...");
      config.set(path, false);
      error = true;
    }
    //AllowMessageSend
    path = "protection.allowmessagesend";
    if (config.contains(path)) {
      AllowMessageSend = config.getBoolean(path);
    }
    else {
      bcab.getLogger().warning("loadConfig() | " + path  + " is not given. Setting it...");
      config.set(path, false);
      error = true;
    }
    //AllowMessageReceive
    path = "protection.allowmessagereceive";
    if (config.contains(path)) {
      AllowMessageReceive = config.getBoolean(path);
    }
    else {
      bcab.getLogger().warning("loadConfig() | " + path + " is not given. Setting it...");
      config.set(path, false);
      error = true;
    }
    //AllowedCommands
    path = "protection.allowedcommands";
    if (config.contains(path)) {
      AllowedCommands = config.getStringList(path);
    }
    else {
      bcab.getLogger().warning("loadConfig() | " + path + " is not given. Setting it...");
      ArrayList<String> allowedcmds = new ArrayList<String>();
      allowedcmds.add("command1");
      allowedcmds.add("command2");
      config.set(path, allowedcmds);
      error = true;
    }
    //Unauthenticated
    //UnauthenticatedKickEnabled
    path = "unauthenticated.kick.enabled";
    if (config.contains(path)) {
      UnauthenticatedKickEnabled = config.getBoolean(path);
    }
    else {
      bcab.getLogger().warning("loadConfig() | " + path + " is not given. Setting it...");
      config.set(path, false);
      error = true;
    }
    //UnauthenticatedKickLength
    path = "unauthenticated.kick.length";
    if (config.contains(path)) {
      UnauthenticatedKickLength = config.getInt(path);
    }
    else {
      bcab.getLogger().warning("loadConfig() | " + path + " is not given. Setting it...");
      config.set(path, 2);
      error = true;
    }
    //UnauthenticatedReminderEnabled
    path = "unauthenticated.reminder.enabled";
    if (config.contains(path)) {
      UnauthenticatedReminderEnabled = config.getBoolean(path);
    }
    else {
      bcab.getLogger().warning("loadConfig() | " + path + " is not given. Setting it...");
      config.set(path, true);
      error = true;
    }
    //UnauthenticatedReminderInterval
    path = "unauthenticated.reminder.interval";
    if (config.contains(path)) {
      UnauthenticatedReminderInterval = config.getInt(path);
    }
    else {
      bcab.getLogger().warning("loadConfig() | " + path + " is not given. Setting it...");
      config.set(path, 10);
      error = true;
    }

    if (isDebugging != null) {
      if (isDebugging && !error)
        bcab.getLogger().info("DEBUG | " +
          "isDebuggin=" + isDebugging +
          ", SessionEnabled=" + SessionEnabled +
          ", SessionLength=" + SessionLength +
          ", MaxAccountsPerIP=" + MaxAccountsPerIP +
          ", MinCharacters=" + MinCharacters +
          ", MaxAttempts=" + MaxAttempts +
          ", AllowServerSwitch=" + AllowServerSwitch +
          ", AllowMessageSend=" + AllowMessageSend + 
          ", AllowedCommands=" + AllowedCommands +
          ", AllowMessageReceive=" + AllowMessageReceive +
          ", UnauthenticatedKickEnabled=" + UnauthenticatedKickEnabled +
          ", UnauthenticatedKickLength=" + UnauthenticatedKickLength +
          ", UnauthenticatedReminderEnabled=" + UnauthenticatedReminderEnabled +
          ", UnauthenticatedReminderInterval=" + UnauthenticatedReminderInterval
          );
    }

    if (error) {
      saveConfig();
      loadConfig();
    }
  }

  public void saveConfig() {
    if (config != null) {
      if (!bcab.getDataFolder().exists()) {
        bcab.getDataFolder().mkdirs();
      }
      try {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configyml);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void loadHikaryCP() {
    hikariconfigfile = new File(bcab.getDataFolder(), "hikariconfig.properties");
    if (!hikariconfigfile.exists()) {
      if (!bcab.getDataFolder().exists()) {
        bcab.getDataFolder().mkdirs();
      }
      try {
        hikariconfigfile.createNewFile();
        PrintStream writer = new PrintStream(hikariconfigfile);
        writer.println("#Default file, infos configuration infos under:");
        writer.println("#https://github.com/brettwooldridge/HikariCP/wiki/Configuration");
        writer.println("jdbcUrl=jdbc:mysql://localhost:3306/");
        writer.println("username=test");
        writer.println("password=test");
        writer.println("dataSource.databaseName=test");
        writer.println("dataSource.cachePrepStmts=true");
        writer.println("dataSource.prepStmtCacheSize=250");
        writer.println("dataSource.useServerPrepStmts=true");
        writer.println("dataSource.useLocalSessionState=true");
        writer.println("dataSource.rewriteBatchedStatements=true");
        writer.println("dataSource.cacheResultSetMetadata=true");
        writer.println("dataSource.cacheServerConfiguration=true");
        writer.println("dataSource.elideSetAutoCommits=true");
        writer.println("dataSource.maintainTimeStats=false");
        writer.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  //Message Values
  public String Prefix;
  public String PlayerOnly;
  public String SQLError;

  //Register
  public String RegisterUsage;
  public String RegisterSamePassword;
  public String RegisterNotEnoughCharacters;
  public String RegisterAlreadyRegistered;
  public String RegisterMaxAccountsPerIP;
  public String RegisterError;
  public String RegisterSuccessful;
  
  //Login
  public String LoginUsage;
  public String LoginAlreadyAuthenticated;
  public String LoginNotRegistered;
  public String LoginWrongPassword;
  public String LoginMaxAttempts;
  public String LoginSuccessful;

  //Logout
  public String LogoutNotAuthenticated;
  public String LogoutSuccessful;

  //ChangePassword
  public String ChangePasswordUsage;
  public String ChangePasswordNotEnoughCharacters;
  public String ChangePasswordNotAuthenticated;
  public String ChangePasswordNotRegistered;
  public String ChangePasswordWrongPassword;
  public String ChangePasswordSuccessful;

  //Reset
  public String ResetUsage;
  public String ResetNotRegistered;
  public String ResetWrongPassword;
  public String ResetSuccessful;

  //Protection
  public String DenyServerSwitch;
  public String DenyMessageSend;
  public String DenyCommandSend;

  //Unauthenticated
  public String UnauthenticatedKickMessage;
  public String UnauthenticatedReminderMessageRegister;
  public String UnauthenticatedReminderMessageLogin;

  //BCAB
  public String BCABUsage;
  public String BCABPermission;
  public String BCABSQLshutdown;
  public String BCABReload;
  public String BCABReloadLogin;
  public String BCABUserUuidEmpty;
  public String BCABPasswordEmpty;
  public String BCABUserUuidNull;
  public String BCABAlreadyAuthenticated;
  public String BCABForceLoginSuccess;
  public String BCABNotAuthenticated;
  public String BCABNotConnected;
  public String BCABForceResetSuccess;
  public String BCABForceRegisterSuccess;
  public String BCABForcePasswordSuccess;
  public String BCABInfoSuccess;
  public String BCABVersion;

  public void loadMessage() {
    boolean error = false;
    messageyml = new File(bcab.getDataFolder(), "message.yml");
    try {
      if (!messageyml.exists()) {
        bcab.getDataFolder().mkdirs();
        messageyml.createNewFile();
      }
      message = ConfigurationProvider.getProvider(YamlConfiguration.class).load(messageyml);
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    String path;
    //Prefix
    path = "prefix";
    if (message.contains(path)) {
      Prefix = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "&8[&6BCA&8]&7 ");
      error = true;
    }
    //PlayerOnly
    path = "playeronly";
    if (message.contains(path)) {
      PlayerOnly = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "This command can only be executed by Players.");
      error = true;
    }
    //SQLError
    path = "sqlerror";
    if (message.contains(path)) {
      SQLError = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "An error has occurred in the database, contact an administrator.");
      error = true;
    }
    //Register
    //RegisterUsage
    path = "register.usage";
    if (message.contains(path)) {
      RegisterUsage = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Usage: /register [password] [password]");
      error = true;
    }
    //RegisterSamePassword
    path = "register.samepassword";
    if (message.contains(path)) {
      RegisterSamePassword = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "The passwords must be identical.");
      error = true;
    }
    //RegisterNotEnoughCharacters
    path = "register.notenoughcharacters";
    if (message.contains(path)) {
      RegisterNotEnoughCharacters = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "The password must be at least %minchars% characters long.");
      error = true;
    }
    //RegisterAlreadyRegistered
    path = "register.alreadyregistered";
    if (message.contains(path)) {
      RegisterAlreadyRegistered = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "You are already registered.");
      error = true;
    }
    //RegisterMaxAccountsPerIP
    path = "register.maxaccountsperip";
    if (message.contains(path)) {
      RegisterMaxAccountsPerIP = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "The maximum number of accounts allowed for your IP has been reached.");
      error = true;
    }
    //RegisterError
    path = "register.error";
    if (message.contains(path)) {
      RegisterError = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "An error has occurred during registration, contact an administrator.");
      error = true;
    }
    //RegisterSuccessful
    path = "register.successful";
    if (message.contains(path)) {
      RegisterSuccessful = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "You have registered successfully.");
      error = true;
    }
    //Login
    //LoginUsage
    path = "login.usage";
    if (message.contains(path)) {
      LoginUsage = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Usage: /login [password]");
      error = true;
    }
    //LoginAlreadyAuthenticated
    path = "login.alreadyauthenticated";
    if (message.contains(path)) {
      LoginAlreadyAuthenticated = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "You are already authenticated.");
      error = true;
    }
    //LoginNotRegistered
    path = "login.notregistered";
    if (message.contains(path)) {
      LoginNotRegistered = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "You are not registered on this server. Use /register [password] [password]");
      error = true;
    }
    //LoginWrongPassword
    path = "login.wrongpassword";
    if (message.contains(path)) {
      LoginWrongPassword = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Wrong password.");
      error = true;
    }
    //LoginMaxAttempts
    path = "login.maxattempts";
    if (message.contains(path)) {
      LoginMaxAttempts = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "You have given the wrong password too often.");
      error = true;
    }
    //LoginSuccessful
    path = "login.successful";
    if (message.contains(path)) {
      LoginSuccessful = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Login successful.");
      error = true;
    }
    //Logout
    //LogoutNotAuthenticated
    path = "logout.notauthenticated";
    if (message.contains(path)) {
      LogoutNotAuthenticated = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "You are not authenticated");
      error = true;
    }
    //LogoutSuccessful
    path = "logout.successful";
    if (message.contains(path)) {
      LogoutSuccessful = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Logout successful.");
      error = true;
    }
    //ChangePassword
    //ChangePasswordUsage
    path = "changepassword.usage";
    if (message.contains(path)) {
      ChangePasswordUsage = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Usage: /changepassword [oldpassword] [newpassword]");
      error = true;
    }
    //ChangePasswordNotEnoughCharacters
    path = "changepassword.notenoughcharacters";
    if (message.contains(path)) {
      ChangePasswordNotEnoughCharacters = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "The password must be at least %minchars% characters long.");
      error = true;
    }
    //ChangePasswordNotAuthenticated
    path = "changepassword.notauthenticated";
    if (message.contains(path)) {
      ChangePasswordNotAuthenticated = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "You have to be authenticated for this command.");
      error = true;
    }
    //ChangePasswordNotRegistered
    path = "changepassword.notregistered";
    if (message.contains(path)) {
      ChangePasswordNotRegistered = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "You are not registered.");
      error = true;
    }
    //ChangePasswordWrongPassword
    path = "changepassword.wrongpassword";
    if (message.contains(path)) {
      ChangePasswordWrongPassword = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Wrong password.");
      error = true;
    }
    //ChangePasswordSuccessful
    path = "changepassword.successful";
    if (message.contains(path)) {
      ChangePasswordSuccessful = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Successfully changed password.");
      error = true;
    }
    //Reset
    //ResetUsage
    path = "reset.usage";
    if (message.contains(path)) {
      ResetUsage = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Usage: /reset [password]");
      error = true;
    }
    //ResetNotRegistered
    path = "reset.notregistered";
    if (message.contains(path)) {
      ResetNotRegistered = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "You are not registered.");
      error = true;
    }
    //ResetWrongPassword
    path = "reset.wrongpassword";
    if (message.contains(path)) {
      ResetWrongPassword = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Wrrong password.");
      error = true;
    }
    //ResetSuccessful
    path = "reset.successful";
    if (message.contains(path)) {
      ResetSuccessful = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Reset successful.");
      error = true;
    }
    //Protection
    //DenyServerSwitch
    path = "protection.serverswitch";
    if (message.contains(path)) {
      DenyServerSwitch = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "You are not allowed to switch servers.");
      error = true;
    }
    //DenyMessageSend
    path = "protection.messagesend";
    if (message.contains(path)) {
      DenyMessageSend = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "You are not allowed to send messages.");
      error = true;
    }
    //DenyCommandSend
    path = "protection.commandsend";
    if (message.contains(path)) {
      DenyCommandSend = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "You are not allowed to send commands.");
      error = true;
    }
    //UnauthenticatedKickMessage
    path = "unauthenticated.kick.message";
    if (message.contains(path)) {
      UnauthenticatedKickMessage = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "It took you too long to log in.");
      error = true;
    }
    //UnauthenticatedReminderMessageRegister
    path = "unauthenticated.reminder.message.register";
    if (message.contains(path)) {
      UnauthenticatedReminderMessageRegister = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Please register with: &c/register [password] [password]");
      error = true;
    }
    //UnauthenticatedReminderMessageLogin
    path = "unauthenticated.reminder.message.login";
    if (message.contains(path)) {
      UnauthenticatedReminderMessageLogin = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Please login with: &c/login [password]");
      error = true;
    }
    //BCAB
    //BCABUsage
    path = "bcab.usage";
    if (message.contains(path)) {
      BCABUsage = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Usage: /bcab <reload/forcelogin/forcereset/forceregister/forcepassword/info/version> []");
      error = true;
    }
    //BCABPermission
    path = "bcab.nopermission";
    if (message.contains(path)) {
      BCABPermission = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "You don't have the permission to do that. [%permission%]");
      error = true;
    }
    //BCABSQLshutdown
    path = "bcab.reload.sqlshutdown";
    if (message.contains(path)) {
      BCABSQLshutdown = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Closing SQL-Connection...");
      error = true;
    }
    //BCABReload
    path = "bcab.reload.success";
    if (message.contains(path)) {
      BCABReload = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "SQL-connection, config.yml and message.yml reloaded successfully");
      error = true;
    }
    //BCABReloadLogin
    path = "bcab.reload.authenticate";
    if (message.contains(path)) {
      BCABReloadLogin = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Please authenticate again.");
      error = true;
    }
    //BCABUserUuidEmpty
    path = "bcab.useroruuidempty";
    if (message.contains(path)) {
      BCABUserUuidEmpty = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Name or UUID is empty or blank.");
      error = true;
    }
    //BCABPasswordEmpty
    path = "bcab.passwordempty";
    if (message.contains(path)) {
      BCABPasswordEmpty = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Password is empty or blank.");
      error = true;
    }
    //BCABUserUuidNull
    path = "bcab.useroruuidnull";
    if (message.contains(path)) {
      BCABUserUuidNull = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Player with given Name or UUID is null.");
      error = true;
    }
    //BCABNotConnected
    path = "bcab.playernotconnected";
    if (message.contains(path)) {
      BCABNotConnected = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Player with given Name or UUID is not connected.");
      error = true;
    }
    //BCABAlreadyAuthenticated
    path = "bcab.alreadyauthenticated";
    if (message.contains(path)) {
      BCABAlreadyAuthenticated = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Player with given Name or UUID is already authenticated.");
      error = true;
    }
    //BCABForceLoginSuccess
    path = "bcab.forceloginsuccess";
    if (message.contains(path)) {
      BCABForceLoginSuccess = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Successfully authenticated %player%.");
      error = true;
    }
    //BCABNotAuthenticated
    path = "bcab.notauthenticated";
    if (message.contains(path)) {
      BCABNotAuthenticated = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Player with given Name or UUID is not authenticated.");
      error = true;
    }
    //BCABForceResetSuccess
    path = "bcab.forceresetsuccess";
    if (message.contains(path)) {
      BCABForceResetSuccess = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Successfully authenticated %player%.");
      error = true;
    }
    //BCABForceRegisterSuccess
    path = "bcab.forceregistersuccess";
    if (message.contains(path)) {
      BCABForceRegisterSuccess = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Successfully registered %player%.");
      error = true;
    }
    //BCABForcePasswordSuccess
    path = "bcab.forcepasswordsuccess";
    if (message.contains(path)) {
      BCABForcePasswordSuccess = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Successfully set %player%'s password.");
      error = true;
    }
    //BCABInfoSuccess
    path = "bcab.infosuccess";
    if (message.contains(path)) {
      BCABInfoSuccess = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Info about %player%:" + '\n' +
        "  &7Status: &r%status%" + '\n' +
        "  &7Registerdate: &r%registerdate%" + '\n' +
        "  &7RegisterIP: &r%registerip%" + '\n' +
        "  &7RegisteredIPCount: &r%registeripcount%" + '\n' +
        "  &7Lastseen: &r%lastseen%" + '\n' +
        "  &7LastIP: &r%lastseen%" + '\n' +
        "  &7Version: &r%version%");
      error = true;
    }
    //BCABVersion
    path = "bcab.version";
    if (message.contains(path)) {
      BCABVersion = color(message.getString(path));
    }
    else {
      bcab.getLogger().warning("loadMessage() | " + path + " is not given. Setting it...");
      message.set(path, "Info about %pluginname%:" + '\n' +
        "  &7Using version: %version%" + '\n' +
        "  &7Running on: &r%server%");
      error = true;
    }

    if (isDebugging && !error)
    bcab.getLogger().info("DEBUG | " +
      "Prefix=" + Prefix + "&r" + '\n' +
      ", PlayerOnly=" + PlayerOnly + "&r" + '\n' +
      ", SQLError=" + SQLError + "&r" + '\n' +
      ", RegisterUsage=" + RegisterUsage + "&r" + '\n' +
      ", RegisterSamePassword=" + RegisterSamePassword + "&r" + '\n' +
      ", RegisterNotEnoughCharacters=" + RegisterNotEnoughCharacters + "&r" + '\n' +
      ", RegisterAlreadyRegistered=" + RegisterAlreadyRegistered + "&r" + '\n' +
      ", RegisterMaxAccountsPerIP=" + RegisterMaxAccountsPerIP + "&r" + '\n' +
      ", RegisterError=" + RegisterError + "&r" + '\n' +
      ", RegisterSuccessful=" + RegisterSuccessful + "&r" + '\n' +
      ", LoginUsage=" + LoginUsage + "&r" + '\n' +
      ", LoginAlreadyAuthenticated=" + LoginAlreadyAuthenticated + "&r" + '\n' +
      ", LoginNotRegistered=" + LoginNotRegistered + "&r" + '\n' +
      ", LoginWrongPassword=" + LoginWrongPassword + "&r" + '\n' +
      ", LoginMaxAttempts=" + LoginMaxAttempts + "&r" + '\n' +
      ", LoginSuccessful=" + LoginSuccessful + "&r" + '\n' +
      ", LogoutNotAuthenticated=" + LogoutNotAuthenticated + "&r" + '\n' +
      ", LogoutSuccessful=" + LogoutSuccessful + "&r" + '\n' +
      ", ChangePasswordUsage=" + ChangePasswordUsage + "&r" + '\n' +
      ", ChangePasswordNotEnoughCharacters=" + ChangePasswordNotEnoughCharacters + "&r" + '\n' +
      ", ChangePasswordNotAuthenticated=" + ChangePasswordNotAuthenticated + "&r" + '\n' +
      ", ChangePasswordNotRegistered=" + ChangePasswordNotRegistered + "&r" + '\n' +
      ", ChangePasswordWrongPassword=" + ChangePasswordWrongPassword + "&r" + '\n' +
      ", ChangePasswordSuccessful=" + ChangePasswordSuccessful + "&r" + '\n' +
      ", ResetUsage=" + ResetUsage + "&r" + '\n' +
      ", ResetNotRegistered=" + ResetNotRegistered + "&r" + '\n' +
      ", ResetWrongPassword=" + ResetWrongPassword + "&r" + '\n' +
      ", ResetSuccessful=" + ResetSuccessful + "&r" + '\n' +
      ", DenyServerSwitch=" + DenyServerSwitch + "&r" + '\n' +
      ", DenyMessageSend=" + DenyMessageSend + "&r" + '\n' +
      ", DenyCommandSend=" + DenyCommandSend + "&r" + '\n' +
      ", UnauthenticatedKickMessage=" + UnauthenticatedKickMessage + "&r" + '\n' +
      ", UnauthenticatedReminderMessageRegister=" + UnauthenticatedReminderMessageRegister + "&r" + '\n' +
      ", UnauthenticatedReminderMessageLogin=" + UnauthenticatedReminderMessageLogin + "&r" + '\n' +
      ", BCABUsage=" + BCABUsage + "&r" + '\n' +
      ", BCABPermission=" + BCABPermission + "&r" + '\n' +
      ", BCABSQLshutdown=" + BCABSQLshutdown + "&r" + '\n' +
      ", BCABReload=" + BCABReload + "&r" + '\n' +
      ", BCABReloadLogin=" + BCABReloadLogin + "&r" + '\n' +
      ", BCABUserUuidEmpty=" + BCABUserUuidEmpty + "&r" + '\n' +
      ", BCABPasswordEmpty=" + BCABPasswordEmpty + "&r" + '\n' +
      ", BCABUserUuidNull=" + BCABUserUuidNull + "&r" + '\n' +
      ", BCABAlreadyAuthenticated=" + BCABAlreadyAuthenticated + "&r" + '\n' +
      ", BCABForceLoginSuccess=" + BCABForceLoginSuccess + "&r" + '\n' +
      ", BCABNotAuthenticated=" + BCABNotAuthenticated + "&r" + '\n' +
      ", BCABNotConnected=" + BCABNotConnected + "&r" + '\n' +
      ", BCABForceResetSuccess=" + BCABForceResetSuccess + "&r" + '\n' +
      ", BCABForceRegisterSuccess=" + BCABForceRegisterSuccess + "&r" + '\n' +
      ", BCABForcePasswordSuccess=" + BCABForcePasswordSuccess + "&r" + '\n' +
      ", BCABInfoSuccess=" + BCABInfoSuccess + "&r" + '\n' +
      ", BCABVersion=" + BCABVersion
    );

    if (error) {
      saveMessage();
      loadMessage();
    }
  }

  public void saveMessage() {
    if (message != null) {
      if (!bcab.getDataFolder().exists()) {
        bcab.getDataFolder().mkdirs();
      }
      try {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(message, messageyml);
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public String color(String message) {
    return ChatColor.translateAlternateColorCodes('&', message);
  }

}
