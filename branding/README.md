# Branding

## Logo

Place the master logo here as `logo_1024.png` (1024×1024, PNG).

### Generate launcher icons (Android Studio)

1. Right-click `app` → **New** → **Image Asset**
2. Icon type: **Launcher Icons (Adaptive and Legacy)**
3. Path: select `branding/logo_1024.png`
4. Background: color `#1E90FF` (or match your brand blue)
5. Finish — Studio writes all `mipmap-*` densities

Adaptive icon XML is already under `app/src/main/res/mipmap-anydpi-v26/`.

### CLI alternative

```bash
# requires ImageMagick or Pillow
python3 scripts/generate_icons.py branding/logo_1024.png
```
