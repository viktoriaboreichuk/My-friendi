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

required_main = (
    "var showSplash by remember { mutableStateOf(true) }",
    "AnimatedVisibility(",
    "LunariSplash0296(",
    "onFinished = { showSplash = false }",
    "fadeOut(animationSpec = tween(durationMillis = 450))",
    "YakorApp(",
    "LunariAccount028(",
)
for marker in required_main:
    if marker not in main:
        raise SystemExit(f"0.2.9.7 MainActivity marker missing: {marker}")

required_splash = (
    "R.drawable.lunari_splash_mobile_0296",
    "ContentScale.FillBounds",
    "PathMeasure",
    "durationMillis = 4400",
    "delay(5000)",
    "activeDots = if (activeDots >= 4) 1 else activeDots + 1",
    "val dotY = h * 0.83050f",
    "0.41369f, 0.47011f, 0.52704f, 0.58453f",
    "onFinished()",
)
for marker in required_splash:
    if marker not in splash:
        raise SystemExit(f"0.2.9.7 splash marker missing: {marker}")

# User installed the first 0.2.9.6/code18 test, so the corrected test must be
# strictly higher for clean in-place Android update installation.
g = BUILD_GRADLE.read_text(encoding="utf-8")
if g.count("versionCode = 17") != 1 or g.count('versionName = "0.2.9.5"') != 1:
    raise SystemExit("0.2.9.7 version bump: expected accepted build-time 0.2.9.5 state not found")
g = g.replace("versionCode = 17", "versionCode = 19", 1)
g = g.replace('versionName = "0.2.9.5"', 'versionName = "0.2.9.7"', 1)
BUILD_GRADLE.write_text(g, encoding="utf-8")

print("Lunari 0.2.9.7 mobile portrait splash revision applied")
