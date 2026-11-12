# How Do I Look? — repository audit and feature expansion

Audit basis: the current `main` branch source inspected on 12 November 2026. This is a source review; no Android device run or Gradle build has been claimed.

## Confirmed issues

1. Home recent-result cards previously had an empty click callback. They now open a saved-result detail screen.
2. History cards previously had an empty click callback. They now open the same saved-result detail screen.
3. Privacy Policy and Terms of Service were plain text with no click action. They now open the published legal pages.
4. The analysis engine previously used random values for several categories, including fit, grooming, style match, and appeal. It could also invent a result when the image could not be decoded. The analyzer has been changed to deterministic photo-quality measurements and now reports an error when the image cannot be read.
5. The previous scoring categories could be read as judgements of a person's appearance. The replacement measures only photo characteristics such as lighting, contrast, color balance, image detail, and crop readiness.
6. The launcher adaptive icon references `@drawable/ic_launcher_foreground`, but that drawable was absent from the inspected repository tree. The uploaded logo still needs to be added as the actual Android image asset.
7. The README advertises a number of features that are not yet represented by routes or screens in the current project (for example virtual wardrobe, style quiz, compare mode, challenges, color finder, and share cards).
8. No notification scheduling/notification builder implementation was found in the inspected application sources. The Settings switch alone does not deliver notifications.
9. The release workflow uses `|| true` for checks, which means lint, detekt, ktlint, and tests cannot fail the release job. The debug workflow skips the build when the Gradle wrapper script is absent. These workflows were not changed in this pass, per the request to focus on app functionality.
10. The repository's current Gradle wrapper properties target Gradle 8.11.1, but the recursive tree did not contain the wrapper script or wrapper JAR. A reproducible build therefore still needs the wrapper files restored.

## 20 feature additions to solve practical wardrobe and photo problems

These are additions beyond the currently described v1 feature set. They should be implemented as real, testable screens/actions, not decorative buttons.

1. **Outfit calendar** — plan what to wear for upcoming school, work, and social events.
2. **Weather-aware outfit notes** — avoid planning clothes unsuitable for the forecast; weather access must be optional.
3. **Capsule wardrobe builder** — help users make more combinations from clothes they already own.
4. **Wardrobe gap finder** — identify missing versatile clothing categories without pushing unnecessary purchases.
5. **Outfit rotation tracker** — help users remember what they wore recently.
6. **Laundry and care reminders** — track washing, ironing, and garment-care tasks.
7. **Clothing care guide** — explain common fabric symbols and care-label instructions.
8. **Packing checklist** — create trip lists from destination, duration, and planned activities.
9. **Dress-code explainer** — clarify casual, smart-casual, formal, and event-specific dress codes.
10. **Interview outfit checklist** — provide a practical checklist for clean, comfortable, context-appropriate clothing.
11. **Event outfit planner** — save an outfit plan and checklist for a particular event.
12. **Budget-aware wardrobe planner** — help users prioritize needs and avoid impulse buying.
13. **Cost-per-wear tracker** — show how often saved garments are used, without ranking people or bodies.
14. **Color palette notebook** — save colors observed in clothing photos and build reusable combinations.
15. **Contrast and accessibility checker** — flag low-contrast clothing combinations when visibility is important.
16. **Tailoring and repair notes** — record loose buttons, hems, zips, and repair tasks.
17. **Closet inventory search** — find saved garments by type, color, season, and occasion.
18. **Offline style boards** — group saved outfit ideas into local boards.
19. **Private data dashboard** — show what is stored on-device and provide clear export/delete controls.
20. **Camera and photo quality coach** — guide focus, lighting, crop, and photo permissions with recovery steps.

## Integration principles

- Keep photos and wardrobe data on-device by default.
- Ask before sharing a photo or result outside the device.
- Do not score attractiveness, body shape, weight, facial features, skin tone, or other personal physical traits.
- Make each control perform a real action; hide unavailable actions rather than presenting dead buttons.
- Give camera, image decode, storage, and notification permission failures a recovery path.
- Keep notifications opt-in and respect Android notification permission requirements.
- Do not add external network calls for features that can work locally.
- Add unit/UI tests for each feature and verify a debug build before claiming it works.

## Current implementation status

The saved-result detail navigation, deterministic photo-quality analysis, and legal-page links are integrated on `main`. The 20 additions above remain an implementation backlog; they are not represented as completed features.
