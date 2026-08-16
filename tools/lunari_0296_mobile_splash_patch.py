from pathlib import Path
import runpy

# Start from the exact user-accepted 0.2.9.5 visual/functional behavior.
# This re-applies the accepted 0.2.9.4 contour fit plus the 0.2.9.5
# Worlds/Chats glow balance exactly as the current production workflow does.
runpy.run_path("tools/lunari_0295_worlds_chats_glow_dim_patch.py", run_name="__main__")

BUILD_GRADLE = Path("app/build.gradle.kts")
MAIN_KT = Path("app/src/main/java/com/vega/yakor/MainActivity.kt")
SPLASH_KT = Path("app/src/main/java/com/vega/yakor/LunariSplash0296.kt")

main = MAIN_KT.read_text(encoding="utf-8")
splash = SPLASH_KT.read_text(encoding="utf-8")

# Scope guards: the new patch is splash-only. MainActivity may host the overlay,
# but the accepted app navigation remains present underneath it.
required_main = (
    "var showSplash by remember { mutableStateOf(true) }",
    "AnimatedVisibility(",
    "LunariSplash0296(",
    "onFinished = { showSplash = false }",
    "YakorApp(",
    "LunariAccount028(",
)
for marker in required_main:
    if marker not in main:
        raise SystemExit(f"0.2.9.6 MainActivity marker missing: {marker}")

required_splash = (
    "R.drawable.lunari_splash_mobile_0296",
    "ContentScale.FillBounds",
    "PathMeasure",
    "activeDots = if (activeDots >= 4) 1 else activeDots + 1",
    "delay(2200)",
    "onFinished()",
)
for marker in required_splash:
    if marker not in splash:
        raise SystemExit(f"0.2.9.6 splash marker missing: {marker}")

# 0.2.9.5's patch has already moved the build-time version to code17/name0.2.9.5.
g = BUILD_GRADLE.read_text(encoding="utf-8")
if g.count("versionCode = 17") != 1 or g.count('versionName = "0.2.9.5"') != 1:
    raise SystemExit("0.2.9.6 version bump: expected build-time 0.2.9.5 state not found")
g = g.replace("versionCode = 17", "versionCode = 18", 1)
g = g.replace('versionName = "0.2.9.5"', 'versionName = "0.2.9.6"', 1)
BUILD_GRADLE.write_text(g, encoding="utf-8")

print("Lunari 0.2.9.6 mobile portrait splash patch applied")
