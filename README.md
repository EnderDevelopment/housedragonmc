# Minecraft Dragon Plugin

HouseDragonMC is a Paper Minecraft dragon plugin that adds medieval dragon powers, dragon fire, and a custom dragon egg. Players with the dragon.use permission can use commands and item interactions to launch fireballs, with visual flame particles, dragon sounds, and a five second fire cooldown.

## Install

1. Build the plugin with Java 21 and Maven:
   `mvn clean package`
2. Copy the generated JAR from the `target` directory to your Paper server's `plugins` directory.
3. Start or restart the server.
4. Confirm that the plugin loads successfully.
5. Grant the `dragon.use` permission if your permissions setup does not provide it by default.

Requirements:

- Paper Minecraft server 1.21 or newer
- Java 21
- Maven for building from source

## Use

Use the following commands in game:

- `/dragon help` displays the available dragon commands.
- `/dragon egg` gives the player a custom Dragon's Egg.
- `/dragon fire` launches a small fireball in the direction the player is looking.
- `/drake` can be used as an alias for `/dragon`.

Players can also right-click the custom Dragon's Egg to use dragon fire. The egg is identified with plugin data, so regular dragon eggs do not activate the ability.

Dragon fire launches an incendiary small fireball, creates flame particles, plays a fire sound, and displays a dragon fire action bar message. Each player has a five second cooldown between uses. The `dragon.use` permission controls access to the commands and dragon egg ability.

---

More Minecraft plugin projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/cb039978-2fdc-4082-b20c-a3ed6ceb980e?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=cb039978-2fdc-4082-b20c-a3ed6ceb980e
