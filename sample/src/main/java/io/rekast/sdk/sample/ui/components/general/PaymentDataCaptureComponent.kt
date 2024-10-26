/*
 * Copyright 2023, Benjamin Mwalimu Mulyungi
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
package io.rekast.sdk.sample.ui.components.general

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.rekast.sdk.sample.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PaymentDataScreenComponent(
    modifier: Modifier = Modifier,
    title: String,
    submitButtonText: String,
    phoneNumber: String,
    financialId: String,
    amount: String,
    paymentMessage: String,
    paymentNote: String,
    onRequestPayButtonClicked: () -> Unit,
    onPhoneNumberUpdated: (String) -> Unit,
    onFinancialIdUpdated: (String) -> Unit,
    onAmountUpdated: (String) -> Unit,
    onPayerMessageUpdated: (String) -> Unit,
    onPayerNoteUpdated: (String) -> Unit
) {
    val bringIntoViewRequester = BringIntoViewRequester()

    val phoneNumberFocusRequester = remember { FocusRequester() }
    val financialIdFocusRequester = remember { FocusRequester() }
    val amountFocusRequester = remember { FocusRequester() }
    val payerMessageFocusRequester = remember { FocusRequester() }
    val payerNoteFocusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Column(modifier = modifier.padding(end = 20.dp)) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 18.sp
                ),
                color = colorResource(id = R.color.black),
                fontWeight = FontWeight.Bold
            )
            Divider(
                modifier = modifier.padding(
                    top = 10.dp
                )
            )
        }
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = onPhoneNumberUpdated,
            label = {
                Text(
                    text = stringResource(id = R.string.phone_number)
                )
            },
            maxLines = 1,
            singleLine = true,
            placeholder = {
                Text(
                    color = Color.LightGray,
                    text = stringResource(id = R.string.phone_number)
                )
            },
            colors = textFieldDefaultsComponent(),
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .background(color = Color.Unspecified)
                .focusRequester(phoneNumberFocusRequester),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onDone = { financialIdFocusRequester.requestFocus() })
        )
        OutlinedTextField(
            value = financialId,
            onValueChange = onFinancialIdUpdated,
            label = {
                Text(
                    text = stringResource(id = R.string.financial_id)
                )
            },
            maxLines = 1,
            singleLine = true,
            placeholder = {
                Text(
                    color = Color.LightGray,
                    text = stringResource(id = R.string.financial_id)
                )
            },
            colors = textFieldDefaultsComponent(),
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .background(color = Color.Unspecified)
                .focusRequester(financialIdFocusRequester),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onDone = { amountFocusRequester.requestFocus() })
        )
        OutlinedTextField(
            value = amount,
            onValueChange = onAmountUpdated,
            label = {
                Text(
                    text = stringResource(id = R.string.amount)
                )
            },
            maxLines = 1,
            singleLine = true,
            placeholder = {
                Text(
                    color = Color.LightGray,
                    text = stringResource(id = R.string.amount)
                )
            },
            colors = textFieldDefaultsComponent(),
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .background(color = Color.Unspecified)
                .focusRequester(financialIdFocusRequester),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onDone = { payerMessageFocusRequester.requestFocus() })
        )
        OutlinedTextField(
            value = paymentMessage,
            onValueChange = onPayerMessageUpdated,
            label = {
                Text(
                    text = stringResource(id = R.string.payment_message)
                )
            },
            maxLines = 1,
            singleLine = true,
            placeholder = {
                Text(
                    color = Color.LightGray,
                    text = stringResource(id = R.string.payment_message)
                )
            },
            colors = textFieldDefaultsComponent(),
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .background(color = Color.Unspecified)
                .focusRequester(financialIdFocusRequester),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onDone = { payerNoteFocusRequester.requestFocus() })
        )
        OutlinedTextField(
            value = paymentNote,
            onValueChange = onPayerNoteUpdated,
            label = {
                Text(
                    text = stringResource(id = R.string.payment_note)
                )
            },
            maxLines = 5,
            singleLine = false,
            placeholder = {
                Text(
                    color = Color.LightGray,
                    text = stringResource(id = R.string.payment_note)
                )
            },
            colors = textFieldDefaultsComponent(),
            modifier = modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(vertical = 10.dp)
                .background(color = Color.Unspecified)
                .focusRequester(financialIdFocusRequester),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
        Divider(
            modifier = modifier.padding(
                top = 10.dp,
                bottom = 10.dp
            )
        )
        Button(
            enabled = phoneNumber.isNotEmpty() && financialId.isNotEmpty() && amount.isNotEmpty() && paymentMessage.isNotEmpty() && paymentNote.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.accent_primary),
                contentColor = Color.White
            ),
            onClick = onRequestPayButtonClicked,
            modifier = modifier
                .fillMaxWidth()
                .bringIntoViewRequester(bringIntoViewRequester),
            elevation = null
        ) {
            Text(
                text = submitButtonText,
                modifier = modifier.padding(8.dp)
            )
        }
    }
}
