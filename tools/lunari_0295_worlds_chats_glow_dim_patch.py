from pathlib import Path
import runpy

# Start from the exact accepted 0.2.9.4 behavior and geometry.
# Change ONLY the opacity of the user-supplied glow PNG layer for the
# "Миры" and "Чаты" cards: reduce brightness by 45% => alpha 0.55.
runpy.run_path("tools/lunari_0294_glow_contour_patch.py", run_name="__main__")

HOME_KT = Path("app/src/main/java/com/vega/yakor/LunariHomeV2.kt")
s = HOME_KT.read_text(encoding="utf-8")

old = '''        Image(
            painter = painterResource(glowRes),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer(
                    scaleX = glowScaleX,
                    scaleY = glowScaleY
                ),
            contentScale = ContentScale.FillBounds
        )
'''

new = '''        val glowAlpha = if (title == "Миры" || title == "Чаты") 0.55f else 1f

        Image(
            painter = painterResource(glowRes),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer(
                    scaleX = glowScaleX,
                    scaleY = glowScaleY,
                    alpha = glowAlpha
                ),
            contentScale = ContentScale.FillBounds
        )
'''

count = s.count(old)
if count != 1:
    raise SystemExit(f"0.2.9.5 glow dim: expected exactly 1 image block, got {count}")
s = s.replace(old, new, 1)

# Scope guards: geometry and PNG-only implementation must remain intact.
for forbidden in (
    "LunariCardCornerGlow0291",
    "LunariCornerHalo0291",
    "LunariCardEdgeGlints0291",
    "LunariCardCornerGlow0292",
):
    if forbidden in s:
        raise SystemExit(f"forbidden previous glow survived: {forbidden}")

required = (
    'R.drawable.lunari_card_glow_custom_01, "Миры"',
    'R.drawable.lunari_card_glow_custom_06, "Чаты"',
    'val glowAlpha = if (title == "Миры" || title == "Чаты") 0.55f else 1f',
    'alpha = glowAlpha',
)
for marker in required:
    if marker not in s:
        raise SystemExit(f"required 0.2.9.5 marker missing: {marker}")

HOME_KT.write_text(s, encoding="utf-8")
print("Lunari 0.2.9.5 Worlds/Chats glow brightness -45% patch applied")
