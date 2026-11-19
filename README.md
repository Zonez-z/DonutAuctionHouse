# Donut Auction House — Fabric 26.3

A client-side, singleplayer auction-house GUI prototype. It does **not** connect to DonutSMP, reproduce its private server data, or interact with a server economy. Purchases are local inventory actions only.

## Current prototype features

- `/ah`, `/ah <search>` and `/ah regear` open the local GUI.
- 54-slot-style, six-row menu with 45 sample listings and a control row.
- Search text matching with a basic edit-distance fallback.
- Category and price-sort cycling, page navigation, listing hover details.
- Click a listing to open a confirmation view; confirm adds a copy to your singleplayer inventory, cancel closes the confirmation.
- Uses ordinary building, storage, decoration and food listings. This first build intentionally omits combat-oriented listings and a pre-filled combat kit.

## Build automatically on GitHub

1. Create a new empty GitHub repository.
2. Upload all files and folders from this project, preserving the `.github/workflows/build.yml` path.
3. Open the repository's **Actions** tab.
4. Select **Build Fabric Mod** and click **Run workflow** (or push to `main`).
5. Open the completed run and download the `donut-auction-house-mc-26.3` artifact.
6. Extract the downloaded ZIP. Put the resulting mod JAR in your Minecraft 26.3 Fabric profile's `mods` folder.

Requirements: Minecraft 26.3, Fabric Loader 0.20.1 or newer, Fabric API 0.166.2+26.3, Java 25.

## Important status

This project was generated in an environment without Java 25, Gradle, or access to dependency repositories, so it has **not been compiled or runtime-tested here**. The GitHub workflow performs the real build and will report any compiler errors. Do not treat this prototype as a verified final JAR until that workflow succeeds.
