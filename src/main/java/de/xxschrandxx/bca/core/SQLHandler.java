package de.xxschrandxx.bca.core;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class SQLHandler {

  private Integer version = 2;

  private HikariDataSource hikari;

  private String database, table = "BungeeCordAuthenticator";

  protected Logger logger;

  private Boolean isdebug;

  public SimpleDateFormat lastseenformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  public SimpleDateFormat regdateformat = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * Creates a {@link HikariDataSource}.
   * @param SQLProperties The {@link Path} to the {@link HikariConfig}. 
   * @param Logger The {@link Logger}.
   * @param isDebug Weather debug messages should be shown.
   * @throws SQLException {@link SQLException}
   */
  public SQLHandler(Path SQLProperties, Logger Logger, Boolean isDebug) throws SQLException {
    this.logger = Logger;
    this.isdebug = isDebug;
    HikariConfig config = new HikariConfig(SQLProperties.toString());
    if (config.getJdbcUrl() == null)
      logger.warning("Error with " + SQLProperties.toString() + " jdbcUrl not given");
    if (config.getUsername() == null)
      logger.warning("Error with " + SQLProperties.toString() + " username not given");
    if (config.getPassword() == null)
      logger.warning("Error with " + SQLProperties.toString() + " password not given");
    database = config.getDataSourceProperties().getProperty("databaseName");
    if (database == null)
      logger.warning("Error with " + SQLProperties.toString() + " dataSource.databaseName not given");
    hikari = new HikariDataSource(config);
    if (!existsTable()) {
      logger.warning("Table does not exists. Creating it.");
      createTable();
    }
  }

  private final Connection getConnection() throws SQLException {
    if (hikari == null) {
      throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
  }

  final Connection connection = hikari.getConnection();
  if (connection == null) {
      throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
  }
  return connection;
  }

  /**
   * {@linkplain HikariDataSource}
   */
  public void shutdown() {
    hikari.close();
  } 

  public void update(String qry) throws SQLException {
    if (isdebug)
      logger.info("DEBUG | performing -> " + qry);
    Connection con = getConnection();
    Statement st = null;
    try {
      st = con.createStatement();
      st.executeUpdate(qry);
    }
    finally {
      if (st != null) try { st.close(); } catch (SQLException ignore) {}
      if (con != null) try { con.close(); } catch (SQLException ignore) {}
    }
  }

  public List<Map<String, Object>> query(String qry) throws SQLException {
    if (isdebug)
      logger.info("DEBUG | performing -> " + qry);
    List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
    Connection con = getConnection();
    Statement st = null;
    ResultSet rs = null;
    try {
      st = con.createStatement();
      rs = st.executeQuery(qry);
      ResultSetMetaData metaData = rs.getMetaData();
      Integer columnCount = metaData.getColumnCount();
      Map<String, Object> row = null;
      while (rs.next()) {
        row = new HashMap<String, Object>();
        for (int i = 1; i <= columnCount; i++) {
            row.put(metaData.getColumnName(i), rs.getObject(i));
        }
        resultList.add(row);
      }
    }
    finally {
      if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
      if (st != null) try { st.close(); } catch (SQLException ignore) {}
      if (con != null)
        try {
          con.close();
        }
        catch (SQLException ignore) {}
    }
    if (isdebug)
      logger.info("DEBUG | got -> " + resultList.toString());
    return resultList;
  }

  /**
   * Checks weather the table exisits.
   * @return Weatehr the table exists.
   * @throws SQLException {@link SQLException}
   */
  public Boolean existsTable() throws SQLException {
    List<Map<String, Object>> qry = query("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + database + "' AND TABLE_NAME = '" + table + "'");
    if (qry.isEmpty())
      return false;
    else
      return true;
  }

  /**
   * Creates the SQL table.
   * @throws SQLException {@link SQLException}
   */
  public void createTable() throws SQLException {
    update(
      "CREATE TABLE IF NOT EXISTS `" + database + "`.`" + table + "` " +
      "(" +
      "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT," +
      "`uuid` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci," +
      "`playername` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
      "`password` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
      "`status` VARCHAR(15) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
      "`pwtype` TINYINT(2) UNSIGNED NOT NULL," +
      "`registeredip` VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
      "`registerdate` DATE NOT NULL DEFAULT '0001-01-01'," +
      "`lastip` VARCHAR(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL," +
      "`lastseen` DATETIME NOT NULL DEFAULT '0001-01-01 01:01:01'," +
      "`version` TINYINT(2) NOT NULL," +
      "PRIMARY KEY (`id`)" +
      ")"
      );
  }

  //modifying entrys 

  /**
   * Creates a new entry in the databse.
   * @param uuid The {@link} UUID}.
   * @param playername The Playername.
   * @param phash The hashed password.
   * @param ptype The password type.
   * @param regip The IP.
   * @param regdate The {@link Date} (regestrationdate).
   * @param lastip The last IP.
   * @param lastseen The {@link Date} (seen last time).
   * @throws SQLException {@link SQLException}
   */
  public void createPlayerEntry(UUID uuid, String playername, String phash, Integer ptype, Date regdate, String regip, String lastip, Date lastseen) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.createPlayerEntry | UUID is null, skipping");
      return;
    }
    if (playername == null) {
      logger.warning("SQLHandler.createPlayerEntry | Playername is null, skipping");
      return;
    }
    if (playername.isEmpty() || playername.isBlank()) {
      logger.warning("SQLHandler.createPlayerEntry | Playername is empty or blank, skipping");
      return;
    }
    if (phash == null) {
      logger.warning("SQLHandler.createPlayerEntry | Hashed password is null, skipping");
      return;
    }
    if (playername.isEmpty() || playername.isBlank()) {
      logger.warning("SQLHandler.createPlayerEntry | Hashed password is empty or blank, skipping");
      return;
    }
    if (ptype == null) {
      logger.warning("SQLHandler.createPlayerEntry | Password type is null, skipping");
      return;
    }
    if (regdate == null) {
      logger.warning("SQLHandler.createPlayerEntry | Registrationdate is null, skipping");
      return;
    }
    if (regip == null) {
      logger.warning("SQLHandler.createPlayerEntry | RegistrationIP is null, skipping");
      return;
    }
    if (playername.isEmpty() || playername.isBlank()) {
      logger.warning("SQLHandler.createPlayerEntry | RegistrationIP is empty or blank, skipping");
      return;
    }
    if (lastip == null) {
      logger.warning("SQLHandler.createPlayerEntry | LastIP is null, skipping");
      return;
    }
    if (playername.isEmpty() || playername.isBlank()) {
      logger.warning("SQLHandler.createPlayerEntry | LastIP is empty or blank, skipping");
      return;
    }
    if (lastseen == null) {
      logger.warning("SQLHandler.createPlayerEntry | LastSeen is null, skipping");
      return;
    }
    String lastseenString = lastseenformat.format(lastseen);
    String regdateString = regdateformat.format(regdate);
    update(
      "INSERT INTO `" + database + "`.`" + table + "` " +
      "(" +
      "`uuid`," +
      "`playername`," +
      "`password`," +
      "`pwtype`," +
      "`registeredip`," +
      "`registerdate`," +
      "`lastip`," +
      "`lastseen`," +
      "`version`," +
      "`status`" +
      ")" +
      " VALUES " +
      "(" +
      "'" + uuid.toString() + "'," +
      "'" + playername.toLowerCase() + "'," +
      "'" + phash + "'," +
      "'" + ptype.toString() + "'," +
      "'" + regip + "'," +
      "'" + regdateString + "'," +
      "'" + lastip + "'," +
      "'" + lastseenString + "'," +
      "'" + version + "'," +
      "'unauthenticated'" +
      ")"
    );
  }

  /**
   * Sets the password for the given {@link UUID}.
   * @param uuid The {@link UUID} to change the password for.
   * @param newPw The new password.
   * @param pwType The passwordtype.
   * @throws SQLException {@link SQLException}
   */
  public void setPassword(UUID uuid, String newPw, Integer pwType) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.setPassword | UUID is null, skipping");
      return;
    }
    if (newPw == null) {
      logger.warning("SQLHandler.setPassword | Password is null, skipping");
      return;
    }
    if (pwType == null) {
      logger.warning("SQLHandler.setPassword | PasswordType is null, skipping");
      return;
    }
    update("UPDATE `" + database + "`.`" + table + "` SET `password` = '" + newPw + "', pwtype = '" + pwType.toString() + "' WHERE `uuid` = '" + uuid.toString() + "'");
  }

  /**
   * Sets the {@link UUID} for the given playername.
   * @param playername The playername.
   * @param uuid The {@link UUID}
   * @throws SQLException {@link SQLException}
   */
  public void setUUID(String playername, UUID uuid) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.setUUID | UUID is null, skipping");
      return;
    }
    if (playername == null) {
      logger.warning("SQLHandler.setUUID | playername is null, skipping");
      return;
    }
    if (playername.isEmpty() || playername.isBlank()) {
      logger.warning("SQLHandler.setUUID | playername is empty or blank, skipping");
      return;
    }
    update("UPDATE `" + database + "`.`" + table + "` SET `uuid` = '" + uuid + "' WHERE `playername` = '" + playername.toLowerCase() + "'");
  }

  /**
   * Sets the version for the given {@link UUID}
   * @param uuid The {@link UUID}.
   * @throws SQLException {@link SQLException}
   */
  public void setVersion(UUID uuid) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.setUUID | UUID is null, skipping");
      return;
    }
    update("UPDATE `" + database + "`.`" + table + "` SET `version` = " + version + " WHERE `uuid` = '" + uuid.toString() + "'");
  }

  /**
   * Removes the entry for the given {@link UUID}.
   * @param uuid The {@link UUID} to remvoe.
   * @throws SQLException {@link SQLException}
   */
  public void removePlayerEntry(UUID uuid) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.removePlayerEntry | UUID is null, skipping");
      return;
    }
    update("DELETE FROM `" + database + "`.`" + table + "` WHERE `uuid`='" + uuid.toString() + "'");
  }

  /**
   * Sets the {@link} {@link OnlineStatus} for the given {@link UUID}.
   * @param uuid The {@link UUID}.
   * @param status The {@link OnlineStatus}
   * @throws SQLException {@link SQLException}
   */
  public void setStatus(UUID uuid, OnlineStatus status) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.setStatus | UUID is null, skipping");
      return;
    }
    if (status == null) {
      logger.warning("SQLHandler.setStatus | UUID is null, skipping");
      return;
    }
    update("UPDATE `" + database + "`.`" + table + "` SET status='" + status.toString() + "' WHERE uuid='" + uuid.toString() + "'");
  }

  /**
   * Check weather a entry for the given {@link UUID} exists.
   * @param uuid The {@link UUID} to check.
   * @return Weather a entry for the given {@link UUID} exists or null if given {@link UUID} is null.
   * @throws SQLException {@link SQLException}
   */
  public Boolean checkPlayerEntry(UUID uuid) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.checkPlayerEntry | UUID is null, skipping");
      return null;
    }
    List<Map<String, Object>> result = query("SELECT `uuid` FROM `" + database + "`.`" + table + "` WHERE `uuid` = '" + uuid.toString() + "'");
    if (result.isEmpty())
      return false;
    else
      return true;
  }

  /**
   * Check weather a entry for the given playername exists.
   * @param playername The playername to check.
   * @return Weather a entry for the given playername exists or null if given playername is null.
   * @throws SQLException {@link SQLException}
   */
  @Deprecated
  public Boolean checkPlayerEntry(String playername) throws SQLException {
    if (playername == null) {
      logger.warning("SQLHandler.checkPlayerEntry | playername is null, skipping");
      return null;
    }
    if (playername.isEmpty() || playername.isBlank()) {
      logger.warning("SQLHandler.checkPlayerEntry | playername is emty or blank, skipping");
      return null;
    }
    List<Map<String, Object>> result = query("SELECT `playername` FROM `" + database + "`.`" + table + "` WHERE `playername` = '" + playername.toLowerCase() + "'");
    if (result.isEmpty())
      return false;
    else
      return true;
  }

  /**
   * Sets the IP and {@link Timestamp} for the given {@link UUID}.
   * @param uuid The {@link UUID}.
   * @param ip The IP for the given {@link UUID}.
   * @param date The {@link Timestamp} for the given {@link UUID}.
   * @throws SQLException {@link SQLException}
   */
  public void setLastSeen(UUID uuid, String ip, Timestamp date) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.setLastSeen | UUID is null, skipping");
      return;
    }
    if (ip == null) {
      logger.warning("SQLHandler.setLastSeen | IP is null, skipping");
      return;
    }
    if (date == null) {
      logger.warning("SQLHandler.setLastSeen | Timestamp is null, skipping");
      return;
    }
    update("UPDATE `" + database + "`.`" + table + "` SET `lastip` = '" + ip + "', `lastseen`= '" + date + "' WHERE uuid = '" + uuid.toString() + "'");
  }

  //checking entrys

  /**
   * Gets the hashed passwortd for the given {@link UUID}.
   * @param uuid The {@link UUID} for the password.
   * @return The password for the given {@link UUID} or null if {@link UUID} is null.
   * @throws SQLException {@link SQLException}
   */
  public String getPassword(UUID uuid) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.getPassword | UUID is null, skipping");
      return null;
    }
    List<Map<String, Object>> result = query("SELECT `uuid`, `password` FROM `" + database + "`.`" + table + "` WHERE `uuid` = '" + uuid.toString() + "'");
    ConcurrentHashMap<UUID, String> uuids = new ConcurrentHashMap<UUID, String>();
    for (Map<String, Object> tmpresult : result) {
      UUID tmpuuid = UUID.fromString((String) tmpresult.get("uuid"));
      String tmppassword = (String) tmpresult.get("password");
      uuids.put(tmpuuid, tmppassword);
    }
    if (uuids.containsKey(uuid))
      return uuids.get(uuid);
    logger.warning("SQLHandler.getPassword | returns null.");
    return null;
  }

  /**
   * Gets the passwordtype for the given {@link UUID}.
   * @param uuid The {@link UUID} for the passwordtype.
   * @return The passwordtype for the given {@link UUID} or null if {@link UUID} is null.
   * @throws SQLException {@link SQLException}
   */
  public Integer getType(UUID uuid) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.getType | UUID is null, skipping");
      return null;
    }
    List<Map<String, Object>> result = query("SELECT `uuid`, `pwtype` FROM `" + database + "`.`" + table + "` WHERE `uuid` = '" + uuid.toString() + "'");
    ConcurrentHashMap<UUID, Integer> uuids = new ConcurrentHashMap<UUID, Integer>();
    for (Map<String, Object> tmpresult : result) {
      UUID tmpuuid = UUID.fromString((String) tmpresult.get("uuid"));
      Integer tmppwtype = (Integer) tmpresult.get("pwtype");
      uuids.put(tmpuuid, tmppwtype);
    }
    if (uuids.containsKey(uuid))
      return uuids.get(uuid);
    logger.warning("SQLHandler.getType | returns null.");
    return null;
  }

  /**
   * Gets the {@link Timestamp} (last seen) for the for the given {@link UUID}.
   * @param uuid The {@link UUID} for the {@link Date}.
   * @return The {@link Timestamp} for the {@link UUID} or null if uuid is null.
   * @throws SQLException {@link SQLException}
   * @throws ParseException {@link ParseException}
   */
  public Timestamp getLastSeen(UUID uuid) throws SQLException, ParseException {
    if (uuid == null) {
      logger.warning("SQLHandler.getLastSeen | UUID is null, skipping");
      return null;
    }
    List<Map<String, Object>> result = query("SELECT `uuid`, `lastseen` FROM `" + database + "`.`" + table + "` WHERE `uuid` = '" + uuid.toString() + "'");
    ConcurrentHashMap<UUID, Timestamp> uuids = new ConcurrentHashMap<UUID, Timestamp>();
    for (Map<String, Object> tmpresult : result) {
      UUID tmpuuid = UUID.fromString((String) tmpresult.get("uuid"));
      Timestamp tmpdate = (Timestamp) tmpresult.get("lastseen");
      uuids.put(tmpuuid, tmpdate);
    }
    if (uuids.containsKey(uuid))
      return uuids.get(uuid);
    logger.warning("SQLHandler.getLastSeen | returns null.");
    return null;
  }

  /**
   * Gets the {@link Date} (registered) for the given {@link UUID}.
   * @param uuid The {@link UUID} for the {@link Date}
   * @return The {@link Date} for the {@link UUID} or null if {@link UUID} is null.
   * @throws SQLException {@link SQLException}
   * @throws ParseException {@link ParseException}
   */
  public Date getRegisterDate(UUID uuid) throws SQLException, ParseException {
    if (uuid == null) {
      logger.warning("SQLHandler.getRegisterDate | UUID is null, skipping");
      return null;
    }
    List<Map<String, Object>> result = query("SELECT `uuid`, `registerdate` FROM `" + database + "`.`" + table + "` WHERE `uuid` = '" + uuid.toString() + "'");
    ConcurrentHashMap<UUID, Date> uuids = new ConcurrentHashMap<UUID, Date>();
    for (Map<String, Object> tmpresult : result) {
      UUID tmpuuid = UUID.fromString((String) tmpresult.get("uuid"));
      Date tmpdate = (Date) tmpresult.get("registerdate");
      uuids.put(tmpuuid, tmpdate);
    }
    if (uuids.containsKey(uuid))
      return uuids.get(uuid);
    logger.warning("SQLHandler.getRegisterDate | returns null.");
    return null;
  }

  /**
   * Gets the last IP for the given {@link UUID}.
   * @param uuid The {@link UUID} for the IP.
   * @return The IP for the {@link UUID} or null if {@link UUID} is null.
   * @throws SQLException {@link SQLException}
   */
  public String getLastIP(UUID uuid) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.getLastIP | UUID is null, skipping");
      return null;
    }
    List<Map<String, Object>> result = query("SELECT `uuid`, `lastip` FROM `" + database + "`.`" + table + "` WHERE `uuid` = '" + uuid.toString() + "'");
    ConcurrentHashMap<UUID, String> uuids = new ConcurrentHashMap<UUID, String>();
    for (Map<String, Object> tmpresult : result) {
      UUID tmpuuid = UUID.fromString((String) tmpresult.get("uuid"));
      String tmplastip = (String) tmpresult.get("lastip");
      uuids.put(tmpuuid, tmplastip);
    }
    if (uuids.containsKey(uuid))
      return uuids.get(uuid);
    logger.warning("SQLHandler.getLastIP | returns null.");
    return null;
  }

  /**
   * Gets the register IP for the given {@link UUID}.
   * @param uuid The {@link UUID} for the IP.
   * @return The IP for the {@link UUID} or null if {@link UUID} is null.
   * @throws SQLException {@link SQLException}
   */
  public String getRegisteredIP(UUID uuid) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.getRegisteredIP | UUID is null, skipping");
      return null;
    }
    List<Map<String, Object>> result = query("SELECT `uuid`, `registeredip` FROM `" + database + "`.`" + table + "` WHERE `uuid` = '" + uuid.toString() + "'");
    ConcurrentHashMap<UUID, String> uuids = new ConcurrentHashMap<UUID, String>();
    for (Map<String, Object> tmpresult : result) {
      UUID tmpuuid = UUID.fromString((String) tmpresult.get("uuid"));
      String tmpregtip = (String) tmpresult.get("registeredip");
      uuids.put(tmpuuid, tmpregtip);
    }
    if (uuids.containsKey(uuid))
      return uuids.get(uuid);
    logger.warning("SQLHandler.getRegisteredIP | returns null.");
    return null;
  }

  /**
   * Gets the {@link OnlineStatus} for the given {@link UUID}.
   * @param uuid The {@link UUID}.
   * @return The {@link OnlineStatus} for the {@link UUID} or null if {@link UUID} is null.
   * @throws SQLException {@link SQLException}
   */
  public OnlineStatus getStatus(UUID uuid) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.getStatus | UUID is null, skipping");
      return null;
    }
    List<Map<String, Object>> result = query("SELECT `uuid`, `status` FROM `" + database + "`.`" + table + "` WHERE `uuid` = '" + uuid.toString() + "'");
    ConcurrentHashMap<UUID, OnlineStatus> uuids = new ConcurrentHashMap<UUID, OnlineStatus>();
    for (Map<String, Object> tmpresult : result) {
      UUID tmpuuid = UUID.fromString((String) tmpresult.get("uuid"));
      OnlineStatus tmpstatus = OnlineStatus.valueOf((String) tmpresult.get("status"));
      uuids.put(tmpuuid, tmpstatus);
    }
    if (uuids.containsKey(uuid))
      return uuids.get(uuid);
    logger.warning("SQLHandler.getStatus | returns null.");
    return null;
  }

  /**
   * Gets the version of the entry from the given {@link UUID}.
   * @param uuid The {@link UUID} for the version.
   * @return The version of the entry or null if {@link UUID} is null.
   * @throws SQLException {@link SQLException}
   */
  public Integer getVersion(UUID uuid) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.getVersion | UUID is null, skipping");
      return null;
    }
    List<Map<String, Object>> result = query("SELECT `uuid`, `version` FROM `" + database + "`.`" + table + "` WHERE `uuid` = '" + uuid.toString() + "'");
    ConcurrentHashMap<UUID, Integer> uuids = new ConcurrentHashMap<UUID, Integer>();
    for (Map<String, Object> tmpresult : result) {
      UUID tmpuuid = UUID.fromString((String) tmpresult.get("uuid"));
      Integer tmpversion = (Integer) tmpresult.get("version");
      uuids.put(tmpuuid, tmpversion);
    }
    if (uuids.containsKey(uuid))
      return uuids.get(uuid);
    logger.warning("SQLHandler.getVersion | returns null.");
    return null;
  }

    /**
   * Gets the version of the entry from the given playername}.
   * @param playername The playername for the version.
   * @return The version of the entry or null if {@link UUID} is null.
   * @throws SQLException {@link SQLException}
   */
  public Integer getVersion(String playername) throws SQLException {
    if (playername == null) {
      logger.warning("SQLHandler.getVersion | playername is null, skipping");
      return null;
    }
    if (playername.isEmpty() || playername.isBlank()) {
      logger.warning("SQLHandler.getVersion | playername is empty or blank, skipping");
      return null;
    }
    playername = playername.toLowerCase();
    List<Map<String, Object>> result = query("SELECT `playername`, `version` FROM `" + database + "`.`" + table + "` WHERE `playername` = '" + playername + "'");
    ConcurrentHashMap<String, Integer> names = new ConcurrentHashMap<String, Integer>();
    for (Map<String, Object> tmpresult : result) {
      String tmpname = (String) tmpresult.get("playername");
      Integer tmpversion = (Integer) tmpresult.get("version");
      names.put(tmpname, tmpversion);
    }
    if (names.containsKey(playername))
      return names.get(playername);
    logger.warning("SQLHandler.getVersion | returns null.");
    return null;
  }

  /**
   * Gets the name for the given {@link UUID}.
   * @param uuid The {@link UUID} for the name.
   * @return The name for the given {@link UUID} or null if {@link UUID} is null.
   * @throws SQLException {@link SQLException}
   */
  public String getName(UUID uuid) throws SQLException {
    if (uuid == null) {
      logger.warning("SQLHandler.getName | UUID is null, skipping");
      return null;
    }
    List<Map<String, Object>> result = query("SELECT `uuid`, `playername` FROM `" + database + "`.`" + table + "` WHERE `uuid` = '" + uuid.toString() + "'");
    ConcurrentHashMap<UUID, String> uuids = new ConcurrentHashMap<UUID, String>();
    for (Map<String, Object> tmpresult : result) {
      UUID tmpuuid = UUID.fromString((String) tmpresult.get("uuid"));
      String tmpname = (String) tmpresult.get("playername");
      uuids.put(tmpuuid, tmpname);
    }
    if (uuids.get(uuid) != null)
      return uuids.get(uuid);
      logger.warning("SQLHandler.getName | returns null.");
      return null;
  }

  /**
   * Gets the count of the registered IPs for the given IP.
   * @param ip The IP.
   * @return The amount of the registered IPs or null if IP is null, empty or blank.
   * @throws SQLException {@link SQLException}
   */
  public Integer getRegisteredIPCount(String ip) throws SQLException {
    if (ip == null) {
      logger.warning("SQLHandler.getRegisteredIPCount | IP is null, skipping");
      return null;
    }
    if (ip.isEmpty()) {
      logger.warning("SQLHandler.getRegisteredIPCount | IP is empty, skipping");
      return null;
    }
    if (ip.isBlank()) {
      logger.warning("SQLHandler.getRegisteredIPCount | IP is blank, skipping");
      return null;
    }
    List<Map<String, Object>> result = query("SELECT `registeredip` FROM `" + database + "`.`" + table + "` WHERE `registeredip` = '" + ip + "'");
    ArrayList<String> ips = new ArrayList<String>();
    for (Map<String, Object> tmpresult : result) {
      String tmpip = (String) tmpresult.get("registeredip");
      ips.add(tmpip);
    }
    return ips.size();
  }

  /**
   * Gets the {@link UUID} for the given name.
   * @param pName The name.
   * @return The {@link UUID} for the given name or null if name is null, empty or blank.
   * @throws SQLException {@link SQLException}
   */
  public UUID getUUID(String pName) throws SQLException {
    if (pName == null) {
      logger.warning("SQLHandler.getUUID | Name is null, skipping");
      return null;
    }
    if (pName.isEmpty()) {
      logger.warning("SQLHandler.getUUID | Name is empty, skipping");
      return null;
    }
    if (pName.isBlank()) {
      logger.warning("SQLHandler.getUUID | Name is blank, skipping");
      return null;
    }
    List<Map<String, Object>> result = query("SELECT `playername`, `uuid` FROM `" + database + "`.`" + table + "` WHERE `playername` = " + pName.toLowerCase() + "'");
    ConcurrentHashMap<String, UUID> names = new ConcurrentHashMap<String, UUID>();
    for (Map<String, Object> tmpresult : result) {
      String tmpname = (String) tmpresult.get("playername");
      UUID tmpuuid = UUID.fromString((String) tmpresult.get("uuid"));
      names.put(tmpname, tmpuuid);
    }
    if (names.containsKey(pName))
      return names.get(pName);
    logger.warning("SQLHandler.getUUID | returns null.");
    return null;
  }

  /**
   * Gets every {@link UUID} in database.
   * @return A {@link List} of every {@link UUID} in database.
   * @throws SQLException {@link SQLException}
   */
  public List<UUID> getAllUUIDs() throws SQLException {
    List<Map<String, Object>> result = query("SELECT `uuid` FROM `" + database + "`.`" + table + "`");
    List<UUID> uuids = new ArrayList<UUID>();
    for (Map<String, Object> tmpresult : result) {
      uuids.add(UUID.fromString((String)tmpresult.get("uuid")));
    }
    return uuids;
  }

}
