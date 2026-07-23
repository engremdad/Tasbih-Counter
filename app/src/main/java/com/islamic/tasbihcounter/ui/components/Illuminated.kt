package com.islamic.tasbihcounter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.tasbihcounter.ui.theme.Mushaf

/**
 * Full-screen illuminated ground: midnight emerald with soft radial glows and a
 * faint gold star lattice. Place other content on top of it.
 */
@Composable
fun StarLatticeBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Mushaf.Ink)
            .drawBehind {
                // Emerald glows top and bottom.
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(Mushaf.Emerald2.copy(alpha = 0.35f), Color.Transparent),
                        center = Offset(size.width * 0.5f, -size.height * 0.05f),
                        radius = size.width * 0.9f
                    )
                )
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(Mushaf.Emerald.copy(alpha = 0.30f), Color.Transparent),
                        center = Offset(size.width * 0.5f, size.height * 1.05f),
                        radius = size.width
                    )
                )
                // Gold star lattice — small dots on a 26dp grid.
                val step = 26.dp.toPx()
                val r = 1.2.dp.toPx()
                val dot = Mushaf.Gold.copy(alpha = 0.10f)
                var y = step / 2
                while (y < size.height) {
                    var x = step / 2
                    while (x < size.width) {
                        drawCircle(color = dot, radius = r, center = Offset(x, y))
                        x += step
                    }
                    y += step
                }
            }
    ) {
        content()
    }
}

/** A spaced, uppercase gold label used as a gilded eyebrow above content. */
@Composable
fun Eyebrow(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Mushaf.Gold
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.5.sp,
        letterSpacing = 3.2.sp
    )
}

/** A gilded rule with a rotated lozenge in the middle (optionally showing ۹۹). */
@Composable
fun LozengeDivider(
    modifier: Modifier = Modifier,
    glyph: String = "۹۹"
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GildedRule(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(24.dp)
                .rotate(45f)
                .border(1.dp, Mushaf.Gold),
            contentAlignment = Alignment.Center
        ) {
            Text(glyph, color = Mushaf.Gold, fontSize = 10.sp, modifier = Modifier.rotate(-45f))
        }
        GildedRule(Modifier.weight(1f))
    }
}

@Composable
private fun GildedRule(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(1.dp)
            .drawBehind {
                drawLine(
                    brush = Brush.horizontalGradient(
                        listOf(Color.Transparent, Mushaf.Line, Color.Transparent)
                    ),
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round
                )
            }
    )
}

/**
 * A gilded card: a gradient emerald panel wrapped in the signature double gold rule
 * (gold line · page gap · gold line), with optional corner brackets framing content.
 */
@Composable
fun IlluminatedCard(
    modifier: Modifier = Modifier,
    corner: Dp = 20.dp,
    gradient: List<Color> = listOf(Mushaf.CardTop, Mushaf.CardBottom),
    cornerBrackets: Boolean = false,
    contentPadding: Dp = 18.dp,
    content: @Composable () -> Unit
) {
    val outerShape = RoundedCornerShape(corner + 4.dp)
    val innerShape = RoundedCornerShape(corner)
    Box(
        modifier = modifier
            .clip(outerShape)
            .border(1.dp, Mushaf.Line, outerShape)
            .background(Mushaf.Page)
            .padding(3.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(innerShape)
                .background(Brush.verticalGradient(gradient))
                .border(1.dp, Mushaf.Line, innerShape)
                .then(if (cornerBrackets) Modifier.cornerBrackets() else Modifier)
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

/** Draws four gold L-shaped corner brackets just inside the element bounds. */
private fun Modifier.cornerBrackets(): Modifier = this.drawWithContent {
    drawContent()
    val gold = Mushaf.Gold.copy(alpha = 0.7f)
    val len = 14.dp.toPx()
    val inset = 10.dp.toPx()
    val sw = 1.5.dp.toPx()
    val w = size.width
    val h = size.height
    // top-left
    drawLine(gold, Offset(inset, inset), Offset(inset + len, inset), sw)
    drawLine(gold, Offset(inset, inset), Offset(inset, inset + len), sw)
    // top-right
    drawLine(gold, Offset(w - inset, inset), Offset(w - inset - len, inset), sw)
    drawLine(gold, Offset(w - inset, inset), Offset(w - inset, inset + len), sw)
    // bottom-left
    drawLine(gold, Offset(inset, h - inset), Offset(inset + len, h - inset), sw)
    drawLine(gold, Offset(inset, h - inset), Offset(inset, h - inset - len), sw)
    // bottom-right
    drawLine(gold, Offset(w - inset, h - inset), Offset(w - inset - len, h - inset), sw)
    drawLine(gold, Offset(w - inset, h - inset), Offset(w - inset, h - inset - len), sw)
}

/** A section panel with a gold left border — used for meaning / virtue / practice. */
@Composable
fun SectionCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Mushaf.SectionBg)
            .border(1.dp, Mushaf.SectionBorder, shape)
            .drawBehind {
                drawRect(
                    color = Mushaf.Gold,
                    topLeft = Offset(0f, 0f),
                    size = androidx.compose.ui.geometry.Size(3.dp.toPx(), size.height)
                )
            }
            .padding(start = 17.dp, top = 14.dp, end = 16.dp, bottom = 15.dp)
    ) {
        Column {
            Eyebrow(title)
            Spacer(Modifier.size(7.dp))
            Text(
                body,
                color = Mushaf.Cream,
                fontSize = 14.sp,
                lineHeight = 24.sp
            )
        }
    }
}
