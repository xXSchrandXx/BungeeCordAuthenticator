## BungeeCordAuthenticator:
[Spigot](https://www.spigotmc.org/resources/bungeecordauthenticator.87669/)

Versions:
  * 0.0.1-SNAPSHOT:
    * [JavaDoc](https://maven.gamestrike.de/docs/BungeeCordAuthenticator/0.0.1-SNAPSHOT/apidocs/)
    * [BungeeCordAuthenticator](https://maven.gamestrike.de/docs/BungeeCordAuthenticator/0.0.1-SNAPSHOT/BungeeCordAuthenticator-0.0.1-SNAPSHOT.jar)
  * 0.0.1:
    * [JavaDoc](https://maven.gamestrike.de/docs/BungeeCordAuthenticator/0.0.1/apidocs/)
    * [BungeeCordAuthenticator](https://maven.gamestrike.de/docs/BungeeCordAuthenticator/0.0.1/BungeeCordAuthenticator-0.0.1.jar)
  * 0.0.2-SNAPSHOT:
    * [JavaDoc](https://maven.gamestrike.de/docs/BungeeCordAuthenticator/0.0.2-SNAPSHOT/apidocs/)
    * [BungeeCordAuthenticator](https://maven.gamestrike.de/docs/BungeeCordAuthenticator/0.0.2-SNAPSHOT/BungeeCordAuthenticator-0.0.2-SNAPSHOT.jar)
  * 0.0.2:
    * [JavaDoc](https://maven.gamestrike.de/docs/BungeeCordAuthenticator/0.0.2/apidocs/)
    * [BungeeCordAuthenticator](https://maven.gamestrike.de/docs/BungeeCordAuthenticator/0.0.2/BungeeCordAuthenticator-0.0.2.jar)
  * 0.0.3-SNAPSHOT:
    * [JavaDoc](https://maven.gamestrike.de/docs/BungeeCordAuthenticator/0.0.3-SNAPSHOT/apidocs/)
    * [BungeeCordAuthenticator](https://maven.gamestrike.de/docs/BungeeCordAuthenticator/0.0.3-SNAPSHOT/BungeeCordAuthenticator-0.0.3-SNAPSHOT.jar)
  * 0.0.3:
    * [JavaDoc](https://maven.gamestrike.de/docs/BungeeCordAuthenticator/0.0.3/apidocs/)
    * [BungeeCordAuthenticator](https://maven.gamestrike.de/docs/BungeeCordAuthenticator/0.0.3/BungeeCordAuthenticator-0.0.3.jar)
  * 0.0.4-SNAPSHOT:
    * [JavaDoc](https://maven.gamestrike.de/docs/BungeeCordAuthenticator/0.0.4-SNAPSHOT/apidocs/)
    * [BungeeCordAuthenticator](https://maven.gamestrike.de/docs/BungeeCordAuthenticator/0.0.4-SNAPSHOT/BungeeCordAuthenticator-0.0.4-SNAPSHOT.jar)

## Installation:
<ol>
<li>Put the BungeeCordAuthenticator JAR into your BungeeCords plugins folder.</li>
<li>Open the `hikariconfig.properties` in the BungeeCordAuthenticatorBungee folder.</li>
<li>Modify `jdbcUrl`, `username`, `password` and `dataSource.databaseName` to your sql login data.</li>
<li>Optionally modify the config.yml and the message.yml in the BungeeCordAuthenticatorBungee folder.</li>
<li>Put the BungeeCordAuthenticator JAR into your Spigots plugins folder.</li>
<li>Open the `hikariconfig.properties` in the BungeeCordAuthenticatorBungee folder.</li>
<li>Modify `jdbcUrl`,  `username`, `password` and `dataSource.databaseName` to your sql login data.</li>
<li>Optionally modify the config.yml and the message.yml in the BungeeCordAuthenticatorBungee folder.</li>
<li>Repead step 5 to 8 for every Spigot server.</li>
</ol>

## Configuration:
<details>
<summary>BungeeCord</summary>

``` YAML
# Weather the plugin should post debug information
debug: false
session:
  # Weather sessions should be enabled.
  enabled: false
  # Amount of minutes a session should last.
  length: 5
registration:
  # Max accounts per IP.
  maxaccountsperip: 5
  # Min characters for the password.
  mincharacters: 8
login:
  # Max attempts for logins until the user gets kicked.
  maxattempts: 3
protection:
  # Weather serverswitching should be allowed.
  allowserverswitch: false
  # Weather sending messages should be allowed.
  allowmessagesend: false
  # Weather getting messages should be allowed.
  allowmessagereceive: false
  # List of allowed Commands.
  allowedcommands:
  - command1
  - command2
unauthenticated:
  kick:
    # Weather unauthenticated users should be kicked after some minutes.
    enabled: false
    # Amount of minutes until a unauthenticated gets kicked.
    length: 2
  reminder:
    # Weather unauthenticated users should get a message to login.
    enabled: true
    # Amount of seconds a message shozld get send.
    interval: 10
```
</details>

<details>
<summary>Spigot</summary>

``` YAML
# Weather the plugin should post debug information
debug: false
protection:
  # Weather serverswitching should be allowed.
  allowserverswitch: false
  # Weather sending messages should be allowed.
  allowmessagesend: false
  # Weather getting messages should be allowed.
  allowmessagereceive: false
  # List of allowed Commands.
  allowedcommands:
  - command1
  - command2
  # Weather movement should be allowed.
  allowmovement: false
teleportation:
  # Location for unauthenticated players.
  unauthed:
    # Weather unauthenticated players should get teleportet.
    enable: false
    location:
      world: world
      x: 0.0
      y: 0.0
      z: 0.0
      yaw: 0.0
      pitch: 0.0
  # Location for authenticated players after authentication.
  authed:
    # Weather authenticated players should get teleportet after authentication.
    enable: false
    location:
      world: world
      x: 0.0
      y: 0.0
      z: 0.0
      yaw: 0.0
      pitch: 0.0
```
</details>

## API Usage:
<details>
<summary>BungeeCord</summary>

``` JAVA
public BungeeCordAuthenticatorBungeeAPI getBungeeCordAuthenticatorAPI() {
  if (getProxy().getPluginManager().getPlugin("BungeeCordAuthenticatorBungee") != null) {
    return BungeeCordAuthenticatorBungee.getInstance().getAPI();
  }
  else {
    return null;
  }
}
```

</details>

<details>
<summary>Bukkit</summary>

``` JAVA
public BungeeCordAuthenticatorBukkitAPI getBungeeCordAuthenticatorAPI() {
  if (getProxy().getPluginManager().getPlugin("BungeeCordAuthenticatorBukkit") != null) {
    return BungeeCordAuthenticatorBukkit.getInstance().getAPI();
  }
  else {
    return null;
  }
}
```

</details>

## Maven:
```
<repositories>
  <repository>
    <id>spigotplugins-repo</id>
    <url>https://maven.gamestrike.de/mvn/</url>
  </repository>
</repositories>
<dependencies>
  <dependency>
    <groupId>de.xxschrandxx.bca</groupId>
    <artifactId>BungeeCordAuthenticator</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```