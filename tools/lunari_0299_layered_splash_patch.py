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
    raise SystemExit("0.2.9.14 Home route marker not found")
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
        raise SystemExit(f"0.2.9.14 MainActivity marker missing: {marker}")

required = (
    "R.drawable.lunari_splash_base_0299",
    "R.drawable.lunari_splash_atlas_0299",
    "ContentScale.Fit",
    "private const val VISUAL_DIM_02911 = 0.75f",
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
    "Modifier.size(18.dp)",
    "Arrangement.spacedBy(18.dp)",
    "spriteScale: Float,\n                alpha: Float",
    "sprite.sw * spriteScale * baseScale",
)
for marker in required:
    if marker not in splash:
        raise SystemExit(f"0.2.9.14 splash marker missing: {marker}")

replacements = {
    "drawPlaced(MOON_0299, designX = 75f, designY = 395f, spriteScale = 0.735f":
        "drawPlaced(MOON_0299, designX = 18f, designY = 388f, spriteScale = 0.67f",
    "drawPlaced(TRAIL1_0299, designX = 251f, designY = 665f, spriteScale = 0.66f":
        "drawPlaced(TRAIL1_0299, designX = 120f, designY = 840f, spriteScale = 0.46f, spriteScaleX = 0.76f",
    "drawPlaced(TRAIL2_0299, designX = 266f, designY = 650f, spriteScale = 0.66f":
        "drawPlaced(TRAIL2_0299, designX = 120f, designY = 816f, spriteScale = 0.46f, spriteScaleX = 0.77f",
    "drawPlaced(TRAIL3_0299, designX = 232f, designY = 625f, spriteScale = 0.66f":
        "drawPlaced(TRAIL3_0299, designX = 118f, designY = 785f, spriteScale = 0.46f, spriteScaleX = 0.75f",
    "drawPlaced(TRAIL4_0299, designX = 239f, designY = 590f, spriteScale = 0.66f":
        "drawPlaced(TRAIL4_0299, designX = 118f, designY = 748f, spriteScale = 0.46f, spriteScaleX = 0.75f",
    "drawPlaced(LOGO_0299, designX = 190f, designY = 635f, spriteScale = 0.74f":
        "drawPlaced(LOGO_0299, designX = 118f, designY = 639f, spriteScale = 0.77f",
    "drawPlaced(STAR_0299, designX = 331f, designY = 567f, spriteScale = 0.185f":
        "drawPlaced(STAR_0299, designX = 499f, designY = 542f, spriteScale = 0.11f",
    "drawPlaced(TAGLINE_0299, designX = 215f, designY = 925f, spriteScale = 0.95f":
        "drawPlaced(TAGLINE_0299, designX = 241f, designY = 975f, spriteScale = 0.72f",
    "val c = mapDesign(0.76f, 0.445f)":
        "val c = mapDesign(0.87f, 0.545f)",
    "val x = 0.22f + 0.55f * u":
        "val x = 0.13f + 0.74f * u",
    "val y = 0.445f + 0.030f * sin((PI * u).toFloat()) + 0.003f * u":
        "val y = 0.545f + 0.018f * sin((PI * u).toFloat()) + 0.002f * u",
    "val cy = 0.444f - 0.014f * u":
        "val cy = 0.543f - 0.010f * u",
    "Modifier.size(18.dp)": "Modifier.size(14.dp)",
    "Arrangement.spacedBy(18.dp)": "Arrangement.spacedBy(14.dp)",
    "spriteScale: Float,\n                alpha: Float":
        "spriteScale: Float,\n                spriteScaleX: Float = spriteScale,\n                alpha: Float",
    "sprite.sw * spriteScale * baseScale":
        "sprite.sw * spriteScaleX * baseScale",
}
for old, new in replacements.items():
    if splash.count(old) != 1:
        raise SystemExit(f"0.2.9.14 expected exactly one marker: {old}")
    splash = splash.replace(old, new, 1)

splash = splash.replace("VISUAL_DIM_02911", "VISUAL_DIM_02914")
if splash.count("private const val VISUAL_DIM_02914 = 0.75f") != 1:
    raise SystemExit("0.2.9.14 visual dim constant missing")
splash = splash.replace("private const val VISUAL_DIM_02914 = 0.75f", "private const val VISUAL_DIM_02914 = 0.5625f", 1)
SPLASH_KT.write_text(splash, encoding="utf-8")

g = BUILD_GRADLE.read_text(encoding="utf-8")
if g.count("versionCode = 17") != 1 or g.count('versionName = "0.2.9.5"') != 1:
    raise SystemExit("0.2.9.14 version bump: expected accepted build-time 0.2.9.5 state not found")
g = g.replace("versionCode = 17", "versionCode = 26", 1)
g = g.replace('versionName = "0.2.9.5"', 'versionName = "0.2.9.14"', 1)
BUILD_GRADLE.write_text(g, encoding="utf-8")

print("Lunari 0.2.9.14 full-width trail correction applied")
