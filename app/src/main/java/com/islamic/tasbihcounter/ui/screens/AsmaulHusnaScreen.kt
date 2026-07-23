package com.islamic.tasbihcounter.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.islamic.tasbihcounter.data.AsmaulHusnaData
import com.islamic.tasbihcounter.data.TasbihState
import com.islamic.tasbihcounter.data.model.AsmaName
import com.islamic.tasbihcounter.ui.Str
import com.islamic.tasbihcounter.ui.TasbihViewModel
import com.islamic.tasbihcounter.ui.components.Eyebrow
import com.islamic.tasbihcounter.ui.components.IlluminatedCard
import com.islamic.tasbihcounter.ui.components.SectionCard
import com.islamic.tasbihcounter.ui.theme.Mushaf
import com.islamic.tasbihcounter.util.AsmaPlayer
import com.islamic.tasbihcounter.util.Haptics
import com.islamic.tasbihcounter.util.bn

@Composable
fun AsmaulHusnaScreen(
    state: TasbihState,
    vm: TasbihViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val names = AsmaulHusnaData.list

    val player = remember { AsmaPlayer(context) }
    DisposableEffect(Unit) { onDispose { player.release() } }

    // Which name the detail view shows; -1 means the grid is showing.
    var openNumber by remember { mutableIntStateOf(-1) }

    if (openNumber in 1..99) {
        val name = names[openNumber - 1]
        NameDetail(
            name = name,
            onBack = { openNumber = -1 },
            onListen = { player.play(name.number) },
            onCount = {
                vm.setAsmaProgress(name.number)
                Haptics.tick(context)
            },
            modifier = modifier
        )
    } else {
        NameGrid(
            names = names,
            progress = state.asmaProgress,
            onOpen = { openNumber = it },
            modifier = modifier
        )
    }
}

@Composable
private fun NameGrid(
    names: List<AsmaName>,
    progress: Int,
    onOpen: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val todayIndex = progress.coerceIn(0, names.size - 1)
    val today = names[todayIndex]

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        // Header
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 6.dp)) {
                Text(Str.namesArabicTitle, color = Mushaf.Gold, fontSize = 26.sp)
                Spacer(Modifier.height(3.dp))
                Eyebrow("Asmaul Husna · 99 Names")
            }
        }
        // Today's name feature card
        item(span = { GridItemSpan(maxLineSpan) }) {
            IlluminatedCard(
                modifier = Modifier.fillMaxWidth().clickable { onOpen(today.number) },
                contentPadding = 20.dp
            ) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Eyebrow("${Str.todaysName} · Today's Name")
                    Spacer(Modifier.height(8.dp))
                    Text(today.arabic, color = Mushaf.Cream, fontSize = 40.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(6.dp))
                    Text(today.transliteration, color = Mushaf.GoldSoft, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text(today.meaning, color = Mushaf.CreamDim, fontSize = 13.sp, textAlign = TextAlign.Center)
                }
            }
        }
        // Search affordance (visual)
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Mushaf.SearchBg)
                    .border(1.dp, Mushaf.SearchBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 15.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = Mushaf.CreamDim, modifier = Modifier.size(18.dp))
                Text(Str.searchNames, color = Mushaf.CreamDim, fontSize = 13.sp)
            }
        }
        // The 99 cells
        itemsIndexed(names, key = { _, n -> n.number }) { index, name ->
            NameCell(name = name, highlighted = index == todayIndex, onClick = { onOpen(name.number) })
        }
    }
}

@Composable
private fun NameCell(name: AsmaName, highlighted: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    val gradient = if (highlighted) listOf(Mushaf.CellHiTop, Mushaf.CellHiBottom)
    else listOf(Mushaf.CellTop, Mushaf.CellBottom)
    Box(
        modifier = Modifier
            .aspectRatio(0.82f)
            .clip(shape)
            .background(Brush.verticalGradient(gradient))
            .border(1.dp, if (highlighted) Mushaf.Gold else Mushaf.CellBorder, shape)
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Text(
            name.number.bn(),
            color = Mushaf.GoldDim,
            fontSize = 9.sp,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                name.arabic,
                color = if (highlighted) Mushaf.Cream else Mushaf.GoldSoft,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(Modifier.height(5.dp))
            Text(
                name.transliteration,
                color = Mushaf.CreamDim,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun NameDetail(
    name: AsmaName,
    onBack: () -> Unit,
    onListen: () -> Unit,
    onCount: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Back row
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Mushaf.Gold,
                modifier = Modifier.size(24.dp).clickable { onBack() }
            )
            Spacer(Modifier.size(12.dp))
            Eyebrow("${name.number.bn()} / ৯৯")
        }
        Spacer(Modifier.height(12.dp))

        // Hero with corner brackets
        IlluminatedCard(
            modifier = Modifier.fillMaxWidth(),
            corner = 24.dp,
            gradient = listOf(Mushaf.CellHiTop, Mushaf.CellHiBottom),
            cornerBrackets = true,
            contentPadding = 28.dp
        ) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(name.arabic, color = Mushaf.Cream, fontSize = 56.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(10.dp))
                Text(name.transliteration, color = Mushaf.GoldSoft, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(name.meaning, color = Mushaf.CreamDim, fontSize = 14.sp, textAlign = TextAlign.Center)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Listen + count pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            GoldPill(text = Str.listen, icon = Icons.Filled.PlayArrow, filled = true, onClick = onListen)
            GoldPill(text = Str.markCounted, icon = Icons.Filled.Add, filled = false, onClick = onCount)
        }

        Spacer(Modifier.height(18.dp))

        SectionCard(Str.sectionMeaning, name.meaning)
        if (name.fazilat.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            SectionCard(Str.sectionVirtue, name.fazilat)
        }
        if (name.amal.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            SectionCard(Str.sectionPractice, name.amal)
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun GoldPill(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    filled: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = Modifier
            .clip(shape)
            .then(
                if (filled) Modifier.background(Mushaf.Gold)
                else Modifier.border(1.dp, Mushaf.Gold, shape)
            )
            .clickable { onClick() }
            .padding(horizontal = 22.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (filled) Mushaf.Ink else Mushaf.Gold,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text,
            color = if (filled) Mushaf.Ink else Mushaf.Gold,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}
