from pathlib import Path
import runpy

runpy.run_path("tools/lunari_0295_worlds_chats_glow_dim_patch.py", run_name="__main__")

BUILD_GRADLE = Path("app/build.gradle.kts")

SCREENS_KT = Path("app/src/main/java/com/vega/yakor/Screens.kt")
screens = SCREENS_KT.read_text(encoding="utf-8")
old_route = "Route.Home -> HomeScreen(store, navigate)"
new_route = "Route.Home -> LunariHomeV2(store, navigate)"
if old_route in screens:
    screens = screens.replace(old_route, new_route, 1)
elif new_route not in screens:
    raise SystemExit("0.2.9.9 Home route marker not found")
SCREENS_KT.write_text(screens, encoding="utf-8")

MAIN_KT = Path("app/src/main/java/com/vega/yakor/MainActivity.kt")
SPLASH_KT = Path("app/src/main/java/com/vega/yakor/LunariSplash0299.kt")

main = MAIN_KT.read_text(encoding="utf-8")
splash = SPLASH_KT.read_text(encoding="utf-8")

for marker in (
    "var showSplash by remember { mutableStateOf(true) }",
    "LunariSplash0299(",
    "onFinished = { showSplash = false }",
    "fadeOut(animationSpec = tween(durationMillis = 450))",
):
    if marker not in main:
        raise SystemExit(f"0.2.9.9 MainActivity marker missing: {marker}")

for marker in (
    "R.drawable.lunari_splash_base_0299",
    "R.drawable.lunari_splash_atlas_0299",
    "ContentScale.Fit",
    "makeSettledDust0299()",
    "makeTravellingDust0299()",
    "activeDot = (activeDot + 1) % 4",
    'text = "Загрузка... $progressPercent%"',
    "durationMillis = 5200",
    "delay(5650)",
):
    if marker not in splash:
        raise SystemExit(f"0.2.9.9 splash marker missing: {marker}")

# User has installed 0.2.9.8/code20. This test must be strictly higher.
g = BUILD_GRADLE.read_text(encoding="utf-8")
if g.count("versionCode = 17") != 1 or g.count('versionName = "0.2.9.5"') != 1:
    raise SystemExit("0.2.9.9 version bump: expected accepted build-time 0.2.9.5 state not found")
g = g.replace("versionCode = 17", "versionCode = 21", 1)
g = g.replace('versionName = "0.2.9.5"', 'versionName = "0.2.9.9"', 1)
BUILD_GRADLE.write_text(g, encoding="utf-8")

print("Lunari 0.2.9.9 layered mobile splash applied")
