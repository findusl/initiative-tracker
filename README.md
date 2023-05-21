This file demonstrates a problem with Jetbrains compose on desktop (or maybe on other platforms).

Use the included "Run Desktop Frontend" run configuration for Intellij to run the frontend and you will be presented
with a simple view that has two text strings and a button. The two texts should be kept in Sync, which is easily
verifiable when looking into the Class `ContentScreen`. However when pressing the "Generate new id" button just a few 
times, they can become out of sync.
