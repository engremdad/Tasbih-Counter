package com.islamic.tasbihcounter.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.islamic.tasbihcounter.data.TasbihState
import com.islamic.tasbihcounter.data.model.ThemeMode
import com.islamic.tasbihcounter.data.model.ThemeStyle
import com.islamic.tasbihcounter.ui.Str
import com.islamic.tasbihcounter.ui.TasbihViewModel
import com.islamic.tasbihcounter.util.bn

@Composable
fun SettingsScreen(
    state: TasbihState,
    vm: TasbihViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val notifPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        vm.setReminders(granted)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(Str.settingsTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        }

        // General
        item { SectionCard(Str.general) {
            ToggleRow(Str.haptics, Str.hapticsDesc, state.hapticsEnabled) { vm.setHaptics(it) }
            HorizontalDivider()
            ToggleRow(Str.sound, Str.soundDesc, state.soundEnabled) { vm.setSound(it) }
            HorizontalDivider()
            ToggleRow(Str.keepScreenOn, Str.keepScreenOnDesc, state.keepScreenOn) { vm.setKeepScreenOn(it) }
            HorizontalDivider()
            ToggleRow(Str.voice, Str.voiceDesc, state.voiceEnabled) { vm.setVoice(it) }
        } }

        // Appearance
        item { SectionCard(Str.appearance) {
            Text(Str.themeStyle, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 6.dp))
            ChipFlow(
                options = ThemeStyle.entries,
                selected = state.themeStyle,
                labelOf = { it.displayName },
                onSelect = { vm.setThemeStyle(it) }
            )
            Spacer(Modifier.height(12.dp))
            Text(Str.themeMode, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 6.dp))
            ChipFlow(
                options = ThemeMode.entries,
                selected = state.themeMode,
                labelOf = { it.displayName },
                onSelect = { vm.setThemeMode(it) }
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                ToggleRow(Str.dynamicColor, Str.dynamicColorDesc, state.dynamicColor) { vm.setDynamicColor(it) }
            }
        } }

        // Reminders
        item { SectionCard(Str.reminders) {
            ToggleRow(Str.enableReminders, Str.remindersDesc, state.remindersEnabled) { enabled ->
                if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    vm.setReminders(enabled)
                }
            }
            if (state.remindersEnabled) {
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                HourStepper(Str.morningTime, state.morningHour) {
                    vm.setMorningHour(it); vm.rescheduleReminders()
                }
                Spacer(Modifier.height(8.dp))
                HourStepper(Str.eveningTime, state.eveningHour) {
                    vm.setEveningHour(it); vm.rescheduleReminders()
                }
            }
        } }

        // About
        item { SectionCard(Str.about) {
            Text(Str.aboutDesc, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(10.dp))
            Text(
                Str.audioCredit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } }

        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) { content() }
    }
}

@Composable
private fun ToggleRow(title: String, desc: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun <T> ChipFlow(
    options: List<T>,
    selected: T,
    labelOf: (T) -> String,
    onSelect: (T) -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(labelOf(option)) }
            )
        }
    }
}

@Composable
private fun HourStepper(label: String, hour: Int, onChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onChange(((hour - 1) + 24) % 24) }) {
                Icon(Icons.Filled.Remove, contentDescription = "-")
            }
            Text(
                text = "${hour.bn()}:০০",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { onChange((hour + 1) % 24) }) {
                Icon(Icons.Filled.Add, contentDescription = "+")
            }
        }
    }
}
