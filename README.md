# How Do I Look? Rate My Look

**Package:** `com.bqsqwad.howdoilook`  
**Publisher:** BQ Sqwad Technologies  
**Contact:** bqsqwadtechnologies@bqsqwadai.com

A fun, feel-good Android app where users take or pick a selfie or outfit photo and receive an entertaining style and presentation rating with kind, actionable tips. All photo processing happens on-device. Nothing is uploaded unless the user explicitly shares a result card.

## Features (v1.0)

- Home with big “Rate My Look” entry, daily streak, recent ratings, daily style tip
- Rating modes: Selfie, Outfit, Hair, Makeup, Casual, Formal, Date Night, Interview
- On-device analysis (ML Kit face detection, image labeling, selfie segmentation + lighting / framing / color / contrast)
- Overall score out of 10 + sub-scores (grooming, outfit harmony, color match, confidence & pose, photo quality)
- Kind, body-positive tips
- Side-by-side Compare mode
- Virtual Wardrobe (save, tag by occasion, favorites)
- Style quiz → personal style profile
- Share cards (photo optional)
- Challenge a friend
- Local history + progress charts
- Color palette finder, idea boards, daily confidence challenge
- Age gate (13+), privacy-first design

## Tech stack

- Kotlin, Jetpack Compose, Material 3
- MVVM + Clean Architecture
- Hilt, Room, DataStore, WorkManager, Navigation Compose
- Gradle Version Catalog, modular structure (`app`, `core:*`, `feature:*`)
- ML Kit (on-device)
- AdMob (banner, native, interstitial, rewarded, app-open) with UMP consent
- Baseline Profiles + Macrobenchmark module
- ktlint + detekt

**SDK:** compileSdk 36 · targetSdk 36 · minSdk 26

## Getting started

1. Clone the repo.
2. Open in Android Studio (Otter / latest stable recommended).
3. Let Gradle sync. The project uses a version catalog (`gradle/libs.versions.toml`).
4. Run the `app` configuration on a device or emulator (API 26+).

### Adding your real AdMob IDs

All test IDs live in one place:

```
core/ads/src/main/java/.../ads_config.kt  (or resources)
```

Replace the test unit IDs with your production ones. Never hard-code real IDs elsewhere.

### Logo

Place your 1024×1024 transparent `logo.png` (and optional mono version) in:

```
branding/logo/
```

Then run:

```bash
./gradlew generateAppIcons
```

(or let the GitHub Action do it). Every launcher, notification, splash and in-app logo updates automatically.

### Signing

Release builds read keystore details from environment variables / `keystore.properties`:

- `KEYSTORE_PATH`
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

A release keystore is generated during setup (see chat / secrets). **Back it up securely.** Enroll in Play App Signing.

## Legal pages (GitHub Pages)

Enabled from the `/docs` folder:

- https://bqsqwadtechnologies.github.io/how-do-I-look-/
- https://bqsqwadtechnologies.github.io/how-do-I-look-/privacy.html
- https://bqsqwadtechnologies.github.io/how-do-I-look-/terms.html

Linked from in-app Settings.

## CI

- `debug.yml` – builds debug APK on every push, uploads artifact
- `release.yml` – signed APK + AAB on tag or manual dispatch, runs lint / ktlint / detekt / unit tests first

## Architecture notes

- Photos never leave the device.
- Every score screen shows the disclaimer: “for fun and style inspiration only, not a measure of your worth or attractiveness”.
- Camera permission requested only when needed; gallery uses the system Photo Picker.
- Ads follow the strict placement map in the project brief (no ads on camera, analysis, onboarding, legal).

## License

Proprietary – BQ Sqwad Technologies. All rights reserved.
