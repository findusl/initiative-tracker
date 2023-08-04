# Overview

## Technical Overview

This application consists of a backend and a frontend application, all code written using Kotlin Multiplatform.
The backend can be found in the [backend](backend) directory, the frontend has shared code mainly in
[kmpsharedmodule](kmpsharedmodule), with some platform specific configurations in [/app](app) and [desktop](desktop).

- The backend has a separate [README](backend/README.md).
- The frontend is described in this README. There is currently no publicly hosted backend, you need to host it yourself.

## Functionality Overview

This app is used during D&D 5e Combat, to reduce the workload on the DM, increase the player immersion and speed up the combat.
More in Detail on this below. The app can probably be applied to most initiative based combat systems.

# Technical Details Frontend

## Locally hosted backend vs remote hosted backend

The frontend can connect to a local backend (default localhost) and a remote backend. This mainly affects whether
ssl is used or not. This is configured by setting the property `buildkonfig.flavor` in
[gradle.properties](gradle.properties) to `lan` or `remote`.

For more details and default values see the section `buildkonfig` in
[kmpsharedmodule/build.gradle.kts](kmpsharedmodule/build.gradle.kts) and in the
[HTTPClient code](kmpsharedmodule/src/commonMain/kotlin/de/lehrbaum/initiativetracker/networking/HttpClientExtensions.kt).

For more details on how to run the backend, see [backend/README.md](backend/README.md).

# Functional Details

The initiative tracker provides multiple features that improve combat. The current combat situation is hosted by the DM
and in real time shared with the players using Websockets and the backend.

- Never again forget whose turn it just was. **The app tracks the initiative order** during the combat and removes
  that task from the DM.
- **No more HP tracking for the DM**. The app can do that by itself. Even better, the players can input the damage and
  the DM only has to approve and potentially apply resistance/vulnerability. Significantly reduces DM overhead during turns
  and improves immersion as the DM is not busy calculating numbers but can describe what the hit looked like.
- Have you had your players ask "How does this enemy look like?" "Has that enemy been hurt?" "Which one looks the weakest?"
  No more! **The tracker shows monster health in approximated steps** to the player, representing "hurt", "bloodied" and "injured".
  The players can already plan to attack the weakest enemy without waiting for a chance to ask about the health states!
- Ever asked everyone for initiative and got it all in the wrong order? **The players can join a combat in the tracker
  by themselves** and are automatically sorted.
- Also the dm can **skip rolling the monster initiatives**. Or looking up the HP points. The tracker uses a backend
  with all official monster blocks and can automatically roll initiative for them. (currently limited to D&D 5e and no homebrew)
- Hide monsters from the players. All they see is an initiative entry with `hidden` as the name. Increases the tension
  while you don't forget the creature in the order.
- Exclude creatures from the initiative until you need them.
- Name the Monsters in a way that makes it more immersive for the players, like "brain with legs" instead of "intellect devourer"
  while you still have the actual monster type available to yourself. (WIP #95)
- Remind players they are next in order (ISSUE #97)
