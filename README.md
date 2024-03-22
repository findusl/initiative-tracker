# Initiative Tracker

## Summary

### Technical Overview

This application consists of a backend and a frontend application. Both frontend and backend have multiple targets.

- Frontend can currently target Android and Desktop JVM. Plans to add iOS and Desktop Native once they are more stable.
- Backend can target JVM and native Linux. The Linux target allows for smaller docker containers.

### Functionality Overview

This app is used during D&D 5e Combat, to reduce the workload on the DM, increase the player immersion and speed up the combat.
More in Detail on this below. The app can probably be applied to most initiative based combat systems.

## Functional Details

### Features
The initiative tracker provides multiple features that improve combat. The current combat situation is hosted by the DM
and in real time shared with the players using Websockets and the backend.

- Never again forget whose turn it is. **The app tracks the initiative order** during the combat and removes
  that task from the DM.
- **No more HP tracking for the DM**. The app can do that by itself. Even better, the players can input the damage and
  the DM only has to approve or apply resistance/vulnerability. Significantly reduces DM overhead during turns
  and improves immersion as the DM is not busy calculating numbers but can describe what the hit looked like.
- Have you had your players ask "How damaged does this enemy look?" "Has that enemy been hurt?" "Which one looks the weakest?"
  No more! **The tracker shows monster health in approximated steps** to the player, representing "hurt", "bloodied" and "injured".
  The players can already plan to attack the weakest enemy without waiting for a chance to ask about the health states!
- Ever asked everyone for initiative and got it all in the wrong order? **The players can join a combat in the tracker
  by themselves** and are automatically sorted.
- The dm can **skip rolling the monster initiatives**. The app can roll those
- If you use D&D 5e, you don't need to look up some basic monster stats, like the HP points. The tracker uses a backend
  with all official monster blocks and can automatically get stats for them. (currently limited to D&D 5e and no homebrew)
- Hide monsters from the players. All they see is an initiative entry with `hidden` as the name. Increases the tension
  while you don't forget the creature in the order.
- Exclude creatures from the initiative until you need them.
- Name the Monsters in a way that makes it more immersive for the players, like "brain with legs" instead of "intellect devourer"
  while you still have the actual monster type available to yourself. (WIP #95)
- Remind players they are next in order (ISSUE #97)

### User guide

I hope it's quite intuitive to use. Maybe worth mentioning, for the dungeon master, once you finished setting up
your monsters, you can "start" a combat with the play button on top. That adds the button for advancing initiative, and
it changes the swipe actions.

## Technical Details Frontend

Most frontend code is shared in the module `frontendshared`. The platform modules are required for correct
gradle tasks specific to the platform.

### Locally hosted backend vs remote hosted backend

The frontend can connect to a local backend (default localhost) and a remote backend. This mainly affects whether
ssl is used or not. This is configured by setting the property `buildkonfig.flavor` in
[gradle.properties](gradle.properties) to `lan` or `remote`.

For default values see the section `buildkonfig` in
[frontendshared/build.gradle.kts](frontendshared/build.gradle.kts). Custom values can be set in
local.properties see [local.properties.template](local.properties.template). The associated code is in
[HTTPClient code](frontendshared/src/commonMain/kotlin/de/lehrbaum/initiativetracker/networking/HttpClientExtensions.kt).

For more details on how to run the backend, see below.

### Running the desktop frontend

You can run the desktop application by running Main.kt in the [desktop](frontenddesktop) module. Either with your favorite IDE
or with the gradle task `:frontenddesktop:run`.

### Running the android app

Use Android studio and run the `frontendandroid` target.

### Build the android app (on my computer)

Android Studio -> generate signed apk `Development/my_release.keystore`

## Technical Details Backend

The backend supports linux native and JVM target. Mostly because I want to, you can just use JVM.

### JVM

#### Run directly

You can run the jvm backend using the gradle task `:backendjvm:run` or by running the
[Main method](backendjvm/src/main/kotlin/de/lehrbaum/initiativetracker/backend/Main.kt) in the `backendjvm` module.

#### Run in docker

There are also a few docker tasks from the ktor docker plugin to build or run it as a docker container.

### Native Linux

#### Run directly

The task `:backendshared:linuxX64Binaries` will build the binaries into
[backendshared/build/bin/linuxX64](backendshared/build/bin/linuxX64), and you can run them.

#### Run in docker

Use the dockerfile in [backendshared/Dockerfile](backendshared/Dockerfile)
