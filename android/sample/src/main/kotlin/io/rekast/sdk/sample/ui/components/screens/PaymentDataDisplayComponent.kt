/*
 * Copyright 2023-2024, Benjamin Mwalimu
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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import io.rekast.sdk.model.MomoTransaction
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.utils.annotation.PreviewWithBackgroundExcludeGenerated

@Composable
fun PaymentDataDisplayComponent(
    modifier: Modifier = Modifier,
    title: String,
    momoTransaction: MutableLiveData<MomoTransaction?>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
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
            Divider(modifier = modifier.padding(top = 10.dp, bottom = 10.dp))
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(end = 20.dp)) {
                Text(
                    text = stringResource(id = R.string.display_amount),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(end = 10.dp)) {
                momoTransaction.value?.amount?.let {
                    Text(
                        text = it,
                        color = colorResource(
                            id = R.color.black
                        )
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(end = 20.dp)) {
                Text(
                    text = stringResource(id = R.string.currency),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(end = 10.dp)) {
                momoTransaction.value?.currency?.let {
                    Text(
                        text = it,
                        color = colorResource(id = R.color.black)
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(end = 20.dp)) {
                Text(
                    text = stringResource(id = R.string.financial_transaction_id),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(end = 10.dp)) {
                momoTransaction.value?.financialTransactionId?.let {
                    Text(
                        text = it,
                        color = colorResource(id = R.color.black)
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(end = 20.dp)) {
                Text(
                    text = stringResource(id = R.string.external_id),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(end = 10.dp)) {
                momoTransaction.value?.externalId?.let {
                    Text(
                        text = it,
                        color = colorResource(id = R.color.black)
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(end = 20.dp)) {
                val title = if (momoTransaction.value?.payee == null) {
                    stringResource(id = R.string.payer)
                } else {
                    stringResource(id = R.string.payee)
                }
                Text(
                    text = title,
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(end = 10.dp)) {
                if (momoTransaction.value?.payee == null) {
                    momoTransaction.value?.payer?.partyId?.let {
                        Text(
                            text = it + " -- " + momoTransaction.value?.payer?.partyIdType,
                            color = colorResource(
                                id = R.color.black
                            )
                        )
                    }
                } else {
                    momoTransaction.value?.payee?.partyId?.let {
                        Text(
                            text = it + " -- " + momoTransaction.value?.payee?.partyIdType,
                            color = colorResource(
                                id = R.color.black
                            )
                        )
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(end = 20.dp)) {
                Text(
                    text = stringResource(id = R.string.payment_message_display),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(end = 10.dp)) {
                momoTransaction.value?.payerMessage?.let {
                    Text(
                        text = it,
                        color = colorResource(
                            id = R.color.black
                        )
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(end = 20.dp)) {
                Text(
                    text = stringResource(id = R.string.payment_note_display),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(end = 10.dp)) {
                momoTransaction.value?.payeeNote?.let {
                    Text(
                        text = it,
                        color = colorResource(
                            id = R.color.black
                        )
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = modifier.padding(end = 20.dp)) {
                Text(
                    text = stringResource(id = R.string.status),
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
            Column(modifier = modifier.padding(end = 10.dp)) {
                momoTransaction.value?.status?.let {
                    Text(
                        text = it,
                        color = colorResource(
                            id = R.color.black
                        )
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            if (momoTransaction.value?.reason != null) {
                Column(modifier = modifier.padding(end = 20.dp)) {
                    Text(
                        text = stringResource(id = R.string.reason),
                        color = colorResource(id = R.color.black),
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = modifier.padding(end = 10.dp)) {
                    momoTransaction.value?.reason?.let {
                        Text(
                            text = it,
                            color = colorResource(
                                id = R.color.black
                            )
                        )
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            if (momoTransaction.value?.referenceIdToRefund != null) {
                Column(modifier = modifier.padding(end = 20.dp)) {
                    Text(
                        text = stringResource(id = R.string.reference_id_to_refund),
                        color = colorResource(id = R.color.black),
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(modifier = modifier.padding(end = 10.dp)) {
                    momoTransaction.value?.referenceIdToRefund?.let {
                        Text(
                            text = it,
                            color = colorResource(
                                id = R.color.black
                            )
                        )
                    }
                }
            }
        }
    }
}

@PreviewWithBackgroundExcludeGenerated
@Composable
fun PaymentDataDisplayComponentPreview() {
    PaymentDataDisplayComponent(
        title = stringResource(id = R.string.request_to_pay_title),
        momoTransaction = MutableLiveData(null)
    )
}
