/*
 * Copyright 2023-2026, Benjamin Mwalimu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rekast.sdk.sample.ui.components.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.rekast.sdk.sample.ui.components.general.CardTitle
import io.rekast.sdk.sample.ui.components.general.MomoCard
import io.rekast.sdk.sample.ui.theme.subtleTextColor

/*
 * Reusable building blocks for the "operation" screens (Invoice, Pre-approval, Cash Transfer) that
 * showcase create -> status -> cancel flows against the SDK. All are theme-aware (light and dark).
 */

/**
 * A labeled outlined text field styled with the app's field defaults.
 *
 * @param label The field label / placeholder.
 * @param value The current text value.
 * @param onValueChange Callback invoked as the user types.
 * @param modifier Modifier applied to the field.
 * @param keyboardType The soft-keyboard type; defaults to [KeyboardType.Text].
 * @param singleLine Whether the field is single-line; defaults to true.
 * @param imeAction The IME action shown on the soft keyboard; defaults to [ImeAction.Next]. When
 *   [ImeAction.Next], the keyboard's next button advances focus to the following field; when
 *   [ImeAction.Done], it dismisses the keyboard. Use [ImeAction.Default] for multi-line fields so
 *   Enter inserts a newline instead of navigating.
 */
@Composable
fun LabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    imeAction: ImeAction = ImeAction.Next
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = singleLine,
        colors = io.rekast.sdk.sample.ui.components.general.textFieldDefaultsComponent(),
        shape = io.rekast.sdk.sample.ui.components.general.textShapeDefaultComponent(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = { focusManager.clearFocus() }
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    )
}

/**
 * A full-width primary action button using the MTN accent color.
 *
 * @param text The button label.
 * @param onClick Click callback.
 * @param modifier Modifier applied to the button.
 * @param enabled Whether the button is enabled; defaults to true.
 */
@Composable
fun OperationActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.secondary,
            contentColor = MaterialTheme.colors.onSecondary
        ),
        elevation = null,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = text, modifier = Modifier.padding(8.dp))
    }
}

/**
 * A console-style result card that shows the outcome of the last operation (a reference ID,
 * a status payload, or an error), or a hint when nothing has run yet.
 *
 * @param result The text to display; null shows the placeholder hint.
 * @param hint The placeholder text shown when [result] is null.
 * @param modifier Modifier applied to the card.
 */
@Composable
fun OperationConsole(result: String?, hint: String, modifier: Modifier = Modifier) {
    MomoCard(modifier = modifier) {
        CardTitle(title = "Result")
        Spacer(modifier = Modifier.height(10.dp))
        if (result.isNullOrBlank()) {
            Text(text = hint, style = MaterialTheme.typography.body2, color = subtleTextColor)
        } else {
            Text(
                text = result,
                style = MaterialTheme.typography.body2.copy(fontFamily = FontFamily.Monospace),
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}
