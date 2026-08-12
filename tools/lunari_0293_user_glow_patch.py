from pathlib import Path
import re
import runpy

# Reuse ONLY the device-approved 0.2.9.1 changes for HUD visibility,
# equal bottom-nav icon tint, and the position-only Snapshots camera lift.
# Do NOT reuse the rejected 0.2.9.1 / 0.2.9.2 programmatic card glow.
runpy.run_path("tools/lunari_0291_fix_patch.py", run_name="__main__")

HOME_KT = Path("app/src/main/java/com/vega/yakor/LunariHomeV2.kt")
s = HOME_KT.read_text(encoding="utf-8")


def replace_once(old: str, new: str, label: str) -> None:
    global s
    count = s.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly 1 match, got {count}")
    s = s.replace(old, new, 1)


# 0.2.9.3 uses the six user-supplied glow PNGs as image overlays.
# Remove imports that were needed only by the rejected Canvas glow.
s = s.replace("import androidx.compose.foundation.Canvas\n", "")
s = s.replace("import androidx.compose.ui.geometry.Offset\n", "")

# Fixed chaotic assignment approved for this patch. Every glow PNG is used once.
card_glows = [
    (
        'LunariCard026(R.drawable.lunari_character, R.drawable.lunari_card_overlay_01, "Персонажи"',
        'LunariCard026(R.drawable.lunari_character, R.drawable.lunari_card_overlay_01, R.drawable.lunari_card_glow_custom_04, "Персонажи"',
        "characters glow 04",
    ),
    (
        'LunariCard026(R.drawable.lunari_world, R.drawable.lunari_card_overlay_02, "Миры"',
        'LunariCard026(R.drawable.lunari_world, R.drawable.lunari_card_overlay_02, R.drawable.lunari_card_glow_custom_01, "Миры"',
        "worlds glow 01",
    ),
    (
        'LunariCard026(R.drawable.lunari_chat, R.drawable.lunari_card_overlay_03, "Чаты"',
        'LunariCard026(R.drawable.lunari_chat, R.drawable.lunari_card_overlay_03, R.drawable.lunari_card_glow_custom_06, "Чаты"',
        "chats glow 06",
    ),
    (
        'LunariCard026(R.drawable.lunari_memory, R.drawable.lunari_card_overlay_04, "Память"',
        'LunariCard026(R.drawable.lunari_memory, R.drawable.lunari_card_overlay_04, R.drawable.lunari_card_glow_custom_03, "Память"',
        "memory glow 03",
    ),
    (
        'LunariCard026(R.drawable.lunari_profiles, R.drawable.lunari_card_overlay_05, "Профили"',
        'LunariCard026(R.drawable.lunari_profiles, R.drawable.lunari_card_overlay_05, R.drawable.lunari_card_glow_custom_05, "Профили"',
        "profiles glow 05",
    ),
    (
        'LunariCard026(R.drawable.lunari_snapshots, R.drawable.lunari_card_overlay_06, "Снимки"',
        'LunariCard026(R.drawable.lunari_snapshots, R.drawable.lunari_card_overlay_06, R.drawable.lunari_card_glow_custom_02, "Снимки"',
        "snapshots glow 02",
    ),
]
for old, new, label in card_glows:
    replace_once(old, new, label)

# Replace the entire 0.2.9.1 wrapper + Canvas glow helpers with a PNG-only wrapper.
# The user-supplied glow is drawn as the LAST decorative layer, stretched exactly to
# the card bounds with ContentScale.FillBounds. The current card body remains unchanged.
new_wrapper = r'''@Composable
private fun LunariCard026(
    imageRes: Int,
    overlayRes: Int,
    glowRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(107.dp)
    ) {
        LunariCardBody0291(
            imageRes = imageRes,
            overlayRes = overlayRes,
            title = title,
            subtitle = subtitle,
            onClick = onClick
        )

        Image(
            painter = painterResource(glowRes),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

'''
pattern = re.compile(
    r'@Composable\nprivate fun LunariCard026\(.*?\n@Composable\nprivate fun LunariCardBody0291\(',
    re.S,
)
s2, count = pattern.subn(new_wrapper + '@Composable\nprivate fun LunariCardBody0291(', s, count=1)
if count != 1:
    raise SystemExit(f"PNG glow wrapper replacement: expected 1 match, got {count}")
s = s2

# Hard guards: no previous procedural glow may survive in the generated source.
for forbidden in (
    "LunariCardCornerGlow0291",
    "LunariCornerHalo0291",
    "LunariCardEdgeGlints0291",
    "LunariCardCornerGlow0292",
):
    if forbidden in s:
        raise SystemExit(f"forbidden previous glow survived: {forbidden}")

HOME_KT.write_text(s, encoding="utf-8")
print("Lunari 0.2.9.3 user-supplied PNG card glow patch applied")
