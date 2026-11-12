# How Do I Look? brand assets

## Upload the exact master logo here

**Upload destination:** `branding/how-do-i-look-logo.png`

Use the square PNG logo supplied in ChatGPT. Keep the original artwork intact; do not replace it with the temporary vector approximation. This is the single source asset for launcher-icon generation.

## Android launcher icon wiring

- Adaptive icon resources: `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` and `ic_launcher_round.xml`
- Foreground resource: `app/src/main/res/drawable/ic_launcher_foreground.xml`
- Generated density icons: `app/src/main/res/mipmap-*/`

After the PNG is uploaded, use Android Studio **app → New → Image Asset → Launcher Icons (Adaptive and Legacy)**. Select `branding/how-do-i-look-logo.png`, keep the blue background, and generate all launcher densities. Verify the icon with round and squircle masks. The currently committed vector foreground is temporary until this PNG is installed.

## Notification icon

Android requires a simple monochrome status-bar glyph for `setSmallIcon`; a full-colour logo is not suitable for the tiny notification slot. The notification helper points to:

`app/src/main/res/drawable/ic_stat_how_do_i_look.xml`

This white glyph is the notification-safe companion mark. Android displays the app's full launcher identity in notification details. Do not set the full-colour launcher bitmap as the small status icon.

## App identity

- Application ID: `com.bqsqwad.howdoilook`
- Launcher label: How Do I Look?
- Brand palette: blue and white
- Notification channel: Style reminders
