package com.islamic.tasbihcounter.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.islamic.tasbihcounter.data.model.Dhikr
import com.islamic.tasbihcounter.ui.Str

/**
 * Create or edit a custom dhikr. When [existing] is non-null the dialog is in edit
 * mode and [onDelete] is shown.
 */
@Composable
fun CustomDhikrDialog(
    existing: Dhikr?,
    onDismiss: () -> Unit,
    onSave: (arabic: String, transliteration: String, translation: String, target: Int) -> Unit,
    onDelete: (() -> Unit)?
) {
    var arabic by remember { mutableStateOf(existing?.arabic ?: "") }
    var translit by remember { mutableStateOf(existing?.transliteration ?: "") }
    var translation by remember { mutableStateOf(existing?.translation ?: "") }
    var targetText by remember { mutableStateOf((existing?.target ?: 33).toString()) }

    val canSave = translit.isNotBlank() && (targetText.toIntOrNull() ?: 0) > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) Str.newDhikr else Str.editDhikr) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = arabic,
                    onValueChange = { arabic = it },
                    label = { Text(Str.dhikrArabic) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = translit,
                    onValueChange = { translit = it },
                    label = { Text(Str.dhikrTransliteration) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = translation,
                    onValueChange = { translation = it },
                    label = { Text(Str.dhikrTranslation) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = targetText,
                    onValueChange = { new -> targetText = new.filter { it.isDigit() }.take(5) },
                    label = { Text(Str.dhikrTarget) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = canSave,
                onClick = {
                    onSave(
                        arabic.trim(),
                        translit.trim(),
                        translation.trim(),
                        targetText.toIntOrNull() ?: 33
                    )
                }
            ) { Text(Str.save) }
        },
        dismissButton = {
            if (onDelete != null) {
                TextButton(onClick = onDelete) { Text(Str.delete) }
            } else {
                TextButton(onClick = onDismiss) { Text(Str.cancel) }
            }
        }
    )
}
