package com.islamic.tasbihcounter.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.islamic.tasbihcounter.data.TasbihState
import com.islamic.tasbihcounter.data.model.DailyRecord
import com.islamic.tasbihcounter.ui.Stats
import com.islamic.tasbihcounter.ui.Str
import com.islamic.tasbihcounter.ui.TasbihViewModel
import com.islamic.tasbihcounter.util.BanglaNumber
import com.islamic.tasbihcounter.util.bn

@Composable
fun HistoryScreen(
    state: TasbihState,
    vm: TasbihViewModel,
    modifier: Modifier = Modifier
) {
    val stats = vm.stats(state)
    val badges = vm.allBadges()
    val history = state.history.sortedByDescending { it.date }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                Str.historyTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        // Stat tiles
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatTile(Str.streak, "${stats.currentStreak.bn()} ${Str.days}", Icons.Filled.LocalFireDepartment, Modifier.weight(1f))
                StatTile(Str.lifetimeTotal, stats.lifetimeCount.bn(), Icons.Filled.EmojiEvents, Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatTile(Str.longestStreak, "${stats.longestStreak.bn()} ${Str.days}", Icons.Filled.LocalFireDepartment, Modifier.weight(1f))
                StatTile(Str.todayTotal, stats.todayCount.bn(), Icons.Filled.EmojiEvents, Modifier.weight(1f))
            }
        }

        // Badges
        item {
            Text(Str.badges, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        items(badges, key = { it.id }) { badge ->
            val earned = badge.id in stats.earnedBadgeIds
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (earned) MaterialTheme.colorScheme.tertiaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp).alpha(if (earned) 1f else 0.6f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (earned) Icons.Filled.EmojiEvents else Icons.Filled.Lock,
                        contentDescription = if (earned) badge.title else Str.locked,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.size(12.dp))
                    Column {
                        Text(badge.title, fontWeight = FontWeight.SemiBold)
                        Text(badge.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Daily log
        item {
            Text(Str.historyTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        if (history.isEmpty()) {
            item {
                Text(Str.noHistory, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(history, key = { it.date }) { record ->
                DayRow(record)
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun StatTile(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
private fun DayRow(record: DailyRecord) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(BanglaNumber.format(record.date), style = MaterialTheme.typography.bodyLarge)
            Text(record.count.bn(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
