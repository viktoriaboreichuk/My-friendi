from pathlib import Path
import runpy

# Start from the exact 0.2.9.3 behavior: approved HUD, nav tint, Snapshots camera
# position and the six user-supplied PNG glows. Change ONLY how those PNGs are
# fitted so their luminous rim sits on the existing card contour instead of
# remaining visibly inset inside the card.
runpy.run_path("tools/lunari_0293_user_glow_patch.py", run_name="__main__")

HOME_KT = Path("app/src/main/java/com/vega/yakor/LunariHomeV2.kt")
s = HOME_KT.read_text(encoding="utf-8")

old = '''        Image(
            painter = painterResource(glowRes),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds
        )
'''

new = '''        // The supplied PNGs contain their bright rim inset from the image edges.
        // Scale each overlay around the card center so that the rim itself, not the
        // PNG canvas, lands on the existing card contour. Values are per-source PNG
        // because the six approved files have different internal vertical margins.
        val (glowScaleX, glowScaleY) = when (glowRes) {
            R.drawable.lunari_card_glow_custom_01 -> 1.037f to 1.353f
            R.drawable.lunari_card_glow_custom_02 -> 1.033f to 1.279f
            R.drawable.lunari_card_glow_custom_03 -> 1.029f to 1.251f
            R.drawable.lunari_card_glow_custom_04 -> 1.032f to 1.221f
            R.drawable.lunari_card_glow_custom_05 -> 1.038f to 1.249f
            R.drawable.lunari_card_glow_custom_06 -> 1.032f to 1.313f
            else -> 1.032f to 1.260f
        }

        Image(
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

count = s.count(old)
if count != 1:
    raise SystemExit(f"0.2.9.4 glow fit: expected exactly 1 image block, got {count}")
s = s.replace(old, new, 1)

# Guard the scope: still PNG-only, no rejected procedural glow helpers.
for forbidden in (
    "LunariCardCornerGlow0291",
    "LunariCornerHalo0291",
    "LunariCardEdgeGlints0291",
    "LunariCardCornerGlow0292",
):
    if forbidden in s:
        raise SystemExit(f"forbidden previous glow survived: {forbidden}")

HOME_KT.write_text(s, encoding="utf-8")
print("Lunari 0.2.9.4 glow contour alignment patch applied")
