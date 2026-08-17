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
    raise SystemExit("0.2.9.12 Home route marker not found")
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
        raise SystemExit(f"0.2.9.12 MainActivity marker missing: {marker}")

required = (
    "R.drawable.lunari_splash_base_0299",
    "R.drawable.lunari_splash_atlas_0299",
    "ContentScale.Fit",
    "VISUAL_DIM_02911 = 0.75f",
    "Sprite0299(4, 4, 571, 713)",
    "Sprite0299(933, 721, 937, 386)",
    "Sprite0299(893, 1214, 915, 281)",
    "Sprite0299(1163, 1536, 638, 42)",
    "drawPlaced(MOON_0299, designX = 75f, designY = 395f, spriteScale = 0.735f",
    "drawPlaced(TRAIL1_0299, designX = 251f, designY = 665f, spriteScale = 0.66f",
    "drawPlaced(TRAIL2_0299, designX = 266f, designY = 650f, spriteScale = 0.66f",
    "drawPlaced(TRAIL3_0299, designX = 232f, designY = 625f, spriteScale = 0.66f",
    "drawPlaced(TRAIL4_0299, designX = 239f, designY = 590f, spriteScale = 0.66f",
    "drawPlaced(LOGO_0299, designX = 190f, designY = 635f, spriteScale = 0.74f",
    "drawPlaced(STAR_0299, designX = 331f, designY = 567f, spriteScale = 0.185f",
    "drawPlaced(TAGLINE_0299, designX = 215f, designY = 925f, spriteScale = 0.95f",
    "val c = mapDesign(0.76f, 0.445f)",
    "val x = 0.22f + 0.55f * u",
    "val y = 0.445f + 0.030f * sin((PI * u).toFloat()) + 0.003f * u",
    "val cy = 0.444f - 0.014f * u",
    "makeSettledDust0299()",
    "makeTravellingDust0299()",
    "activeDot = (activeDot + 1) % 4",
    'text = "Загрузка... $progressPercent%"',
    ".padding(bottom = 22.dp)",
    "durationMillis = 5200",
    "delay(5650)",
)
for marker in required:
    if marker not in splash:
        raise SystemExit(f"0.2.9.12 splash marker missing: {marker}")

# The previous build still looked unchanged because the energy ribbons were too large
# and crossed the moon/logo area, visually creating a second crescent and collapsing
# the composition. 0.2.9.12 keeps the same exact source PNGs, but uses the approved
# portrait reference geometry for the static elements and makes the storyboard trail
# substantially smaller/lower/right so it reads as a separate magical flow.
replacements = {
    "drawPlaced(MOON_0299, designX = 75f, designY = 395f, spriteScale = 0.735f":
        "drawPlaced(MOON_0299, designX = 58f, designY = 388f, spriteScale = 0.67f",
    "drawPlaced(TRAIL1_0299, designX = 251f, designY = 665f, spriteScale = 0.66f":
        "drawPlaced(TRAIL1_0299, designX = 310f, designY = 720f, spriteScale = 0.48f",
    "drawPlaced(TRAIL2_0299, designX = 266f, designY = 650f, spriteScale = 0.66f":
        "drawPlaced(TRAIL2_0299, designX = 305f, designY = 700f, spriteScale = 0.48f",
    "drawPlaced(TRAIL3_0299, designX = 232f, designY = 625f, spriteScale = 0.66f":
        "drawPlaced(TRAIL3_0299, designX = 295f, designY = 675f, spriteScale = 0.48f",
    "drawPlaced(TRAIL4_0299, designX = 239f, designY = 590f, spriteScale = 0.66f":
        "drawPlaced(TRAIL4_0299, designX = 285f, designY = 640f, spriteScale = 0.48f",
    "drawPlaced(LOGO_0299, designX = 190f, designY = 635f, spriteScale = 0.74f":
        "drawPlaced(LOGO_0299, designX = 166f, designY = 639f, spriteScale = 0.77f",
    "drawPlaced(STAR_0299, designX = 331f, designY = 567f, spriteScale = 0.185f":
        "drawPlaced(STAR_0299, designX = 499f, designY = 542f, spriteScale = 0.11f",
    "drawPlaced(TAGLINE_0299, designX = 215f, designY = 925f, spriteScale = 0.95f":
        "drawPlaced(TAGLINE_0299, designX = 209f, designY = 971f, spriteScale = 0.72f",
    "val c = mapDesign(0.76f, 0.445f)":
        "val c = mapDesign(0.76f, 0.485f)",
    "val x = 0.22f + 0.55f * u":
        "val x = 0.34f + 0.43f * u",
    "val y = 0.445f + 0.030f * sin((PI * u).toFloat()) + 0.003f * u":
        "val y = 0.487f + 0.018f * sin((PI * u).toFloat()) + 0.002f * u",
    "val cy = 0.444f - 0.014f * u":
        "val cy = 0.485f - 0.010f * u",
}
for old, new in replacements.items():
    if splash.count(old) != 1:
        raise SystemExit(f"0.2.9.12 expected exactly one marker: {old}")
    splash = splash.replace(old, new, 1)

# Keep FULL-resolution atlas source rectangles exact; only destination geometry changes.
SPLASH_KT.write_text(splash, encoding="utf-8")

# User has installed 0.2.9.11/code23. This test must be strictly higher.
g = BUILD_GRADLE.read_text(encoding="utf-8")
if g.count("versionCode = 17") != 1 or g.count('versionName = "0.2.9.5"') != 1:
    raise SystemExit("0.2.9.12 version bump: expected accepted build-time 0.2.9.5 state not found")
g = g.replace("versionCode = 17", "versionCode = 24", 1)
g = g.replace('versionName = "0.2.9.5"', 'versionName = "0.2.9.12"', 1)
BUILD_GRADLE.write_text(g, encoding="utf-8")

print("Lunari 0.2.9.12 portrait composition correction applied")
