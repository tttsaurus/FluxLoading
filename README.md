[![Versions](https://img.shields.io/curseforge/game-versions/1207328?logo=curseforge&label=Game%20Version)](https://www.curseforge.com/minecraft/mc-mods/fluxloading)
[![Downloads](https://img.shields.io/curseforge/dt/1207328?logo=curseforge&label=Downloads)](https://www.curseforge.com/minecraft/mc-mods/fluxloading)
[![Downloads](https://img.shields.io/modrinth/dt/fluxloading?logo=modrinth&label=Downloads)](https://modrinth.com/mod/fluxloading)

## About the Mod
FluxLoading is a mod that enhances immersion by capturing a screenshot of the logout screen and displaying it when you reload your world. When you rejoin, you'll see the exact scene from where you left off, smoothly fading out into real-time gameplay.

![ezgif-1359521c68cdd9](https://github.com/user-attachments/assets/1e45e221-90c3-4090-af9c-b2daaa46632c)

## Guide on Making Addons
- Whether the loading screen is handled by FluxLoading
  ```java
  FluxLoadingAPI.isActive();
  ```
- FluxLoading Timeline
  ```
  // FadingInPhase is guaranteed to finish 
  // before DefaultWorldLoadingPhase finishes
  +-------------------+
  |  FadingInPhase    |
  |  (parallel start) |
  +-------------------+
   ||
   || (parallel)
   ||
  +----------------------------+
  |  DefaultWorldLoadingPhase  |
  |  (parallel start)          |
  +----------------------------+
   |
   | (The player is now present in the world, but remains locked)
   |
   v
  // ExtraChunkLoadingPhase may not be triggered
  // if chunks loaded fast enough
  +--------------------------+
  |  ExtraChunkLoadingPhase  |
  +--------------------------+
   |
   v
  +------------------+
  |  ExtraWaitPhase  |
  +------------------+
   |
   v
  +------------------+
  |  FadingOutPhase  |
  +------------------+
   |
   v
  +-----------------+
  |  FinishLoading  |
  +-----------------+
  ```
  ```java
  FluxLoadingAPI.isDuringFadingInPhase();
  FluxLoadingAPI.isDuringDefaultWorldLoadingPhase();
  FluxLoadingAPI.isDuringExtraChunkLoadingPhase();
  FluxLoadingAPI.isDuringExtraWaitPhase();
  FluxLoadingAPI.isDuringFadingOutPhase();
  FluxLoadingAPI.isFinishLoading();
  ```
- Register Listeners
  ```java
  FluxLoadingAPI.addFluxLoadingTickListener(Runnable listener);
  FluxLoadingAPI.addFluxLoadingStartListener(Runnable listener);
  FluxLoadingAPI.addFluxLoadingEndListener(Runnable listener);
  ```

## Dependency
- mixinbooter 10.0+

## Compatibility
- [Nothirium](https://www.curseforge.com/minecraft/mc-mods/nothirium)
- [Loading Progress Bar](https://www.curseforge.com/minecraft/mc-mods/loading-progress-bar)
- [AnotherTips](https://www.curseforge.com/minecraft/mc-mods/anothertips)
- [Open Terrain Generator](https://www.curseforge.com/minecraft/mc-mods/open-terrain-generator)
- [Celeritas](https://git.taumc.org/embeddedt/celeritas)
- [Hwyla](https://www.curseforge.com/minecraft/mc-mods/hwyla)
