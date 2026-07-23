package com.islamic.tasbihcounter.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.islamic.tasbihcounter.data.TasbihState
import com.islamic.tasbihcounter.ui.Str
import com.islamic.tasbihcounter.ui.TasbihViewModel
import com.islamic.tasbihcounter.ui.components.ProgressRing
import com.islamic.tasbihcounter.ui.components.Eyebrow
import com.islamic.tasbihcounter.ui.components.LozengeDivider
import com.islamic.tasbihcounter.util.Haptics
import com.islamic.tasbihcounter.util.DhikrVoiceRecognizer
import com.islamic.tasbihcounter.util.bn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(
    state: TasbihState,
    vm: TasbihViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dhikr = state.selectedDhikr
    val count = state.countFor(dhikr.id)
    val target = dhikr.target.coerceAtLeast(1)
    val progress = count.toFloat() / target

    var showReset by remember { mutableStateOf(false) }
    var showAdd by remember { mutableStateOf(false) }
    var voiceActive by remember { mutableStateOf(false) }

    // Microphone permission launcher — enables voice counting once granted.
    val micPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) voiceActive = true
        else Toast.makeText(context, Str.permissionNeededMic, Toast.LENGTH_SHORT).show()
    }

    // Voice recognizer lifecycle: active only while enabled in settings AND toggled on here.
    val recognizer = remember {
        DhikrVoiceRecognizer(
            context = context,
            onMatch = {
                vm.increment()
                if (state.hapticsEnabled) Haptics.tick(context)
            },
            onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
        )
    }
    DisposableEffect(voiceActive, dhikr.id, state.voiceEnabled) {
        if (voiceActive && state.voiceEnabled) {
            recognizer.updateKeywords(dhikr.transliteration)
            recognizer.start()
        } else {
            recognizer.stop()
        }
        onDispose { recognizer.stop() }
    }

    fun onCountTap() {
        vm.increment()
        if (state.hapticsEnabled) {
            if (vm.lastReachedGoal) Haptics.goalReached(context) else Haptics.tick(context)
        }
        if (vm.lastReachedGoal) {
            Toast.makeText(context, Str.goalReached, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(10.dp))
        Eyebrow("তাসবিহ · Dhikr")

        // Dhikr selector chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(state.allDhikr, key = { it.id }) { d ->
                FilterChip(
                    selected = d.id == dhikr.id,
                    onClick = { vm.selectDhikr(d.id) },
                    label = { Text(d.transliteration) }
                )
            }
            item {
                AssistChip(
                    onClick = { showAdd = true },
                    label = { Text(Str.addCustomDhikr) },
                    leadingIcon = {
                        Icon(Icons.Filled.Add, contentDescription = Str.addCustomDhikr, Modifier.size(18.dp))
                    },
                    colors = AssistChipDefaults.assistChipColors()
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Arabic + translation
        Text(
            text = dhikr.arabic,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = dhikr.translation,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LozengeDivider(modifier = Modifier.padding(horizontal = 32.dp))

        Spacer(Modifier.height(16.dp))

        // Big tap ring
        Box(
            modifier = Modifier
                .clickable { onCountTap() },
            contentAlignment = Alignment.Center
        ) {
            ProgressRing(progress = progress) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = count.bn(),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${Str.target} ${target.bn()}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = if (voiceActive && state.voiceEnabled) Str.voiceListening else Str.tapToCount,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        // Action row: reset + voice toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleAction(
                icon = Icons.Filled.RestartAlt,
                label = Str.reset,
                onClick = { showReset = true }
            )
            if (state.voiceEnabled) {
                Spacer(Modifier.width(28.dp))
                CircleAction(
                    icon = if (voiceActive) Icons.Filled.Mic else Icons.Filled.MicOff,
                    label = Str.voiceTap,
                    highlighted = voiceActive,
                    onClick = {
                        if (voiceActive) {
                            voiceActive = false
                        } else {
                            val granted = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                            if (granted) voiceActive = true
                            else micPermission.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                )
            }
        }
    }

    if (showReset) {
        AlertDialog(
            onDismissRequest = { showReset = false },
            title = { Text(Str.resetConfirmTitle) },
            text = { Text(Str.resetConfirmMsg) },
            confirmButton = {
                TextButton(onClick = {
                    vm.resetCurrent()
                    showReset = false
                }) { Text(Str.confirm) }
            },
            dismissButton = {
                TextButton(onClick = { showReset = false }) { Text(Str.cancel) }
            }
        )
    }

    if (showAdd) {
        CustomDhikrDialog(
            existing = null,
            onDismiss = { showAdd = false },
            onSave = { arabic, translit, translation, tgt ->
                vm.addCustomDhikr(arabic, translit, translation, tgt)
                showAdd = false
            },
            onDelete = null
        )
    }
}

@Composable
private fun CircleAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    highlighted: Boolean = false,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = if (highlighted) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(56.dp).clickable { onClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = if (highlighted) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
