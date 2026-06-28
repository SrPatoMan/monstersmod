# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

MonstersMod is a Minecraft Fabric mod targeting Minecraft 1.20.1, built with Fabric Loom. Code is written primarily in **Kotlin** (`src/main/kotlin`), with Java reserved for Mixins (`src/main/java`). Mod ID is `monstersmod`; base package is `manuel.monstersmod`.

## Build / run commands

This is a Gradle project using the Fabric Loom plugin (`net.fabricmc.fabric-loom-remap`, currently a 1.16 snapshot). **The Loom plugin requires Gradle itself to run on a Java 21 JVM**, even though the mod's source/target bytecode level is Java 17 (see `build.gradle.kts`). The system default `java` on PATH here is 17, which will fail Gradle configuration with `Dependency requires at least JVM runtime version 21`. A Temurin 21 JDK is installed at `C:\Users\manul\.jdks\temurin-21.0.11` — point `JAVA_HOME` at it (or pass `-Dorg.gradle.java.home=...`) before invoking the wrapper:

```bash
JAVA_HOME=/c/Users/manul/.jdks/temurin-21.0.11 ./gradlew <task>
```

Common tasks (standard Fabric Loom):
- `./gradlew build` — compile and produce the mod jar (output in `build/libs/`)
- `./gradlew runClient` — launch a dev Minecraft client with the mod loaded
- `./gradlew runServer` — launch a dev Minecraft server with the mod loaded
- `./gradlew runDatagen` — run the Fabric data generator (entry point `MonstersModDataGenerator`); output goes to `src/main/generated`
- There is no dedicated automated test suite in this project — verification is done by running the client/server (`runClient`/`runServer`) and exercising features in-game.

Matching IntelliJ run configurations exist under `.idea/runConfigurations/` (`Minecraft Client`, `Minecraft Server`, `Data Generation`).

Mod version / dependency versions (Minecraft, Yarn mappings, Fabric API, Fabric Loader, Fabric Language Kotlin) are centralized in `gradle.properties`.

## Architecture

### Entry points
Defined in `src/main/resources/fabric.mod.json`, each backed by a Kotlin `object` (singletons), using the `kotlin` adapter:
- `MonstersMod` (`main`) — common/server logic: registers items, NPC entity types, NPC default attributes, and the server-side dialogue network receiver.
- `MonstersModClient` (`client`) — client-only logic: registers entity renderers and the client-side dialogue network receiver that opens the dialogue GUI.
- `MonstersModDataGenerator` (`fabric-datagen`) — data generation entry point (currently empty, ready for generators).

Registration of game objects (items, entities, item groups) happens inside these `object`s' `companion object` blocks using `Registry.register(Registries.X, Identifier(MOD_ID, name), instance)`, following vanilla/Fabric conventions — see `items/MisItems.kt`, `tabsCreativo/MisTabs.kt`, `npcs/ModEntities.kt`.

### NPC / dialogue system
This is the most involved subsystem and spans multiple packages:

- `npcs/NpcEntity.kt` — base class for custom NPCs (extends `PathAwareEntity`). Defines shared attributes/AI goals (wander, look-at-player) and makes NPCs immortal and non-despawning. Concrete NPCs (`npcs/XokasNpc.kt`, `npcs/AlexElCapo.kt`) extend it.
- Each concrete NPC class has its own `companion object Dialogue` holding a `Map<String, DialogueNode>` (its dialogue tree) and a `START_NODE` constant. `DialogueNode`/`DialogueOption`/`DialogueAction` are defined in `dialogos/NodoDialogo.kt`. **To add a new NPC**, create a class extending `NpcEntity`, give it its own `Dialogue` map, register its `EntityType` in `npcs/ModEntities.kt`, register its render in `MonstersModClient`, and add its dialogue-tree lookup branch in the `when (npcId)` blocks in `MonstersMod.onInitialize` (server receiver) — dialogue trees are looked up by a hardcoded NPC id string, not polymorphically.
- `npcs/NpcRender.kt` — generic renderer (`BipedEntityRenderer` using the vanilla player model) parameterized by a texture `Identifier`, reused for all NPCs.
- Dialogue is server-authoritative: `NpcEntity.interactMob` (in each NPC subclass) only acts on the server, fetches the NPC's start node, and sends it to the client.
- `network/CustomPayload.kt` (`DialogueNetworking`) defines the two custom packet channels and manual `PacketByteBuf` read/write logic (no codec/payload-type abstraction yet):
  - `OPEN_DIALOGUE` (server → client): npc id, node id, node text, option texts.
  - `DIALOGUE_CHOICE` (client → server): npc id, current node id, chosen option index.
- `gui/DialogueScreen.kt` — client-side `Screen` that renders the NPC text and one button per option; clicking a button sends a `DIALOGUE_CHOICE` packet and closes the screen.
- On the server, `MonstersMod.onInitialize` registers the `DIALOGUE_CHOICE` receiver, which re-resolves the dialogue node/option from the NPC's own `Dialogue.nodes` map, optionally pushes the next node back to the client, and executes the option's `DialogueAction` (e.g. `ACCEPT_QUEST`/`DECLINE_QUEST` just send a chat message today — this is where quest logic would hook in).

### Items
`items/MisItems.kt` registers all items via the `registrarItem` helper and adds them to creative inventory tabs via `agregarAlInventario` (wraps `ItemGroupEvents.modifyEntriesEvent`). Custom item behavior (e.g. `items/CuboKfcItem.kt`, which returns an empty container item after eating) lives in its own `Item` subclass alongside `MisItems`.

### Creative tabs
`tabsCreativo/MisTabs.kt` defines custom `ItemGroup`s via `FabricItemGroup.builder()` and registers them with `Registry.register(Registries.ITEM_GROUP, ...)`.

### Mixins
Java mixins live in `src/main/java/manuel/monstersmod/mixin` and must be listed in `src/main/resources/monstersmod.mixins.json` (`mixins` array) to take effect.

### Localization
Translation strings live in `src/main/resources/assets/monstersmod/lang/` (`en_us.json`, `es_es.json`). Source code comments throughout the codebase are written in Spanish.
