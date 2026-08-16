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

# The compact GitHub transport pack keeps the supplied splash base at 941x2048
# but stores the alpha atlas at half resolution. Destination geometry stays exact;
# only source rectangles are halved before compilation.
sprite_replacements = {
    "Sprite0299(4, 4, 571, 713, 41, 550, 571, 713)": "Sprite0299(2, 2, 286, 357, 41, 550, 571, 713)",
    "Sprite0299(579, 4, 565, 650, 19, 535, 565, 650)": "Sprite0299(290, 2, 283, 325, 19, 535, 565, 650)",
    "Sprite0299(4, 721, 925, 489, 16, 769, 925, 489)": "Sprite0299(2, 360, 463, 245, 16, 769, 925, 489)",
    "Sprite0299(933, 721, 937, 386, 4, 809, 937, 386)": "Sprite0299(466, 360, 469, 193, 4, 809, 937, 386)",
    "Sprite0299(4, 1214, 885, 318, 46, 858, 885, 318)": "Sprite0299(2, 607, 443, 159, 46, 858, 885, 318)",
    "Sprite0299(893, 1214, 915, 281, 24, 854, 915, 281)": "Sprite0299(446, 607, 458, 141, 24, 854, 915, 281)",
    "Sprite0299(4, 1536, 908, 269, 33, 826, 908, 269)": "Sprite0299(2, 768, 454, 135, 33, 826, 908, 269)",
    "Sprite0299(916, 1536, 243, 237, 235, 732, 243, 237)": "Sprite0299(458, 768, 122, 119, 235, 732, 243, 237)",
    "Sprite0299(1163, 1536, 638, 42, 159, 1002, 638, 42)": "Sprite0299(582, 768, 319, 21, 159, 1002, 638, 42)",
}
for old, new in sprite_replacements.items():
    if splash.count(old) != 1:
        raise SystemExit(f"0.2.9.9 sprite source marker missing: {old}")
    splash = splash.replace(old, new, 1)
SPLASH_KT.write_text(splash, encoding="utf-8")

# User has installed 0.2.9.8/code20. This test must be strictly higher.
g = BUILD_GRADLE.read_text(encoding="utf-8")
if g.count("versionCode = 17") != 1 or g.count('versionName = "0.2.9.5"') != 1:
    raise SystemExit("0.2.9.9 version bump: expected accepted build-time 0.2.9.5 state not found")
g = g.replace("versionCode = 17", "versionCode = 21", 1)
g = g.replace('versionName = "0.2.9.5"', 'versionName = "0.2.9.9"', 1)
BUILD_GRADLE.write_text(g, encoding="utf-8")

print("Lunari 0.2.9.9 layered mobile splash applied")
