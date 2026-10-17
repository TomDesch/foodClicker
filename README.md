# FoodClicker

Inventory-based idle game for Spigot and Paper 26.3. No Paper-only APIs or NMS are used.

## Build and install

Build with JDK 26 and Maven: `mvn clean verify`. The plugin emits Java 21 bytecode; this does not lower the server's own Java requirement. Run current servers with Java 25 or 26.

Copy `target\FoodClicker-3.0.0.jar` into the server's `plugins` directory and restart. No runtime libraries or companion plugins are required. Do not use server reload.

## Commands

- `/foodclicker`: open the game.
- `/fcstats [true|false]`: broadcast statistics with a hover tooltip. The prestige view requires Prestige Brag.
- `/fctop`: show the richest players by lifetime earnings. Requires the Leaderboard prestige upgrade.
- `/fcfirework <player> <amount> <size>`: requires `foodclicker.firework` (operators by default).

## Player data

Player YAML files live in `plugins\FoodClicker\foodclicker`, keyed by UUID. Settings and progression are loaded and saved through an ordered background queue. Statistics and leaderboard commands never start a game or collect AFK income.

Closing the game saves a snapshot; disconnect and server stop also save. A disconnect during initial loading preserves the stored progression and records only the connection's time boundaries. Online idle time and offline time are counted separately, excluding active play. Temporary boosts and buy-max tasks stop when the game closes or prestige resets progression.

A failed load does not create an empty replacement file. Repair the affected YAML and retry `/foodclicker`. A failed save keeps the session in memory and prevents reopening or replacing it until a retry succeeds. Use `/foodclicker` to retry and check the server log before restarting after any storage failure. Unsupported atomic file replacement fails safely rather than risking the previous file.

Shutdown waits up to 30 seconds for the data queue. On timeout, unfinished operations report failure and cancellation is requested. A blocked filesystem write may still finish; re-enabling the plugin is refused until the old worker actually exits, so it cannot overwrite a newer session's save.

Back up player data before upgrading. Stop the server before editing files.

## Validation

`mvn test` runs focused persistence, lifecycle, command, GUI ownership and gameplay regressions. Real-server startup/shutdown checks are separate from authenticated-client gameplay checks.
