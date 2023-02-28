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
package com.rekast.momoapi.sample.ui.main

import androidx.lifecycle.ViewModel
import com.rekast.momoapi.BuildConfig
import com.rekast.momoapi.MomoAPI
import com.rekast.momoapi.callback.APIResult
import com.rekast.momoapi.utils.Constants
import com.rekast.momoapi.utils.ProductType
import com.rekast.momoapi.utils.Settings
import org.apache.commons.lang3.StringUtils

class AppMainViewModel : ViewModel() {
    fun momoAPI(): MomoAPI {
        return MomoAPI.builder(BuildConfig.MOMO_API_USER_ID)
            .setEnvironment(BuildConfig.MOMO_ENVIRONMENT)
            .getBaseURL(BuildConfig.MOMO_BASE_URL)
            .build()
    }

    fun checkUser(momoAPi: MomoAPI) {
        momoAPi.checkApiUser(
            Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
            BuildConfig.MOMO_API_VERSION_V1,
        ) { momoAPIResult ->
            when (momoAPIResult) {
                is APIResult.Success -> {
                    getApiKey(momoAPi)
                }
                is APIResult.Failure -> {
                    val momoAPIException = momoAPIResult.APIException
                    // showToast(momoAPIException?.message ?: "An error occurred!")
                }
            }
        }
    }

    private fun getApiKey(momoAPi: MomoAPI) {
        momoAPi.getUserApiKey(
            Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
            BuildConfig.MOMO_API_VERSION_V1,
        ) { momoAPIResult ->
            when (momoAPIResult) {
                is APIResult.Success -> {
                    val apiUserKey = momoAPIResult.value
                    // Utils.saveApiKey(, apiUserKey.apiKey)
                    getAccessToken(momoAPi)
                }
                is APIResult.Failure -> {
                    val momoAPIException = momoAPIResult.APIException
                    // showToast(momoAPIException?.message ?: "An error occurred!")
                }
            }
        }
    }

    private fun getAccessToken(momoAPi: MomoAPI) {
        // val apiUserKey = Utils.getApiKey(this)
        val apiUserKey: String = ""
        if (StringUtils.isNotBlank(apiUserKey)) {
            momoAPi.getAccessToken(
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                apiUserKey,
                Constants.ProductTypes.REMITTANCE,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        val accessToken = momoAPIResult.value
                        // Utils.saveAccessToken(this, accessToken)
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        // showToast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

/*    private fun getAccountBalance() {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.getBalance(
                "EUR",
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                accessToken,
                BuildConfig.MOMO_API_VERSION_V1,
                Constants.ProductTypes.REMITTANCE,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        val balance = momoAPIResult.value
                        Timber.d(balance.toString())
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun getBasicUserInfo() {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.getBasicUserInfo(
                "46733123459",
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                accessToken,
                BuildConfig.MOMO_API_VERSION_V1,
                Constants.ProductTypes.REMITTANCE,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        val basicInfo = momoAPIResult.value
                        Timber.d(basicInfo.toString())
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun getUserInfoWithConsent() {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.getUserInfoWithConsent(
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                accessToken,
                BuildConfig.MOMO_API_VERSION_V1,
                Constants.ProductTypes.REMITTANCE,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        val getUserInfoWithoutConsent = momoAPIResult.value
                        Timber.d(getUserInfoWithoutConsent.toString())
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun transferRemittance() {
        val accessToken = Utils.getAccessToken(this)
        val creditTransaction = createDebitTransaction()
        val transactionUuid = Settings.generateUUID()
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.transfer(
                accessToken,
                creditTransaction,
                BuildConfig.MOMO_API_VERSION_V1,
                Constants.ProductTypes.REMITTANCE,
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                transactionUuid,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        getTransferStatus(transactionUuid)
                        // requestToPayDeliveryNotification(transactionUuid)
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun createDebitTransaction(): Transaction {
        return Transaction(
            "10000",
            "EUR",
            null,
            Settings.generateUUID(),
            AccountHolder(AccountHolderType.MSISDN.name, "346736732646"),
            null,
            "Testing",
            "The Good Company",
            null,
            null,
        )
    }

    private fun getTransferStatus(referenceId: String) {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.getTransferStatus(
                referenceId,
                BuildConfig.MOMO_API_VERSION_V1,
                Constants.ProductTypes.REMITTANCE,
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                accessToken,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        val completeTransfer =
                            Gson().fromJson(momoAPIResult.value!!.source().readUtf8(), Transaction::class.java)
                        Timber.d(completeTransfer.toString())
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun requestToPayDeliveryNotification(referenceId: String, productType: String) {
        val accessToken = Utils.getAccessToken(this)
        val notification = Notification(
            notificationMessage =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur pellentesque mi" +
                " erat, vel placerat erat sollicitudin et. Sed id ex nisi. Quisque luctus metus",
        )
        if (StringUtils.isNotBlank(accessToken) &&
            Settings.checkNotificationMessageLength(notification.notificationMessage)
        ) {
            momoAPI.requestToPayDeliveryNotification(
                notification,
                referenceId,
                BuildConfig.MOMO_API_VERSION_V1,
                productType,
                Settings.getProductSubscriptionKeys(ProductType.COLLECTION),
                accessToken,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        toast("Delivery note sent")
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun validateAccountHolderStatus() {
        val accessToken = Utils.getAccessToken(this)
        val accountHolder =
            AccountHolder(
                partyId = "346736732646",
                partyIdType = Constants.EndpointPaths.AccountHolderTypes.MSISDN,
            )
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.validateAccountHolderStatus(
                accountHolder,
                BuildConfig.MOMO_API_VERSION_V1,
                Constants.ProductTypes.REMITTANCE,
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                accessToken,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        val accountHolderStatus =
                            Gson().fromJson(
                                momoAPIResult.value!!.source().readUtf8(),
                                AccountHolderStatus::class.java,
                            )
                        Timber.d(accountHolderStatus.result.toString())
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun requestToPay() {
        val accessToken = Utils.getAccessToken(this)
        val creditTransaction = createRequestTpPayTransaction()
        val transactionUuid = Settings.generateUUID()
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.requestToPay(
                accessToken,
                creditTransaction,
                BuildConfig.MOMO_API_VERSION_V1,
                Settings.getProductSubscriptionKeys(ProductType.COLLECTION),
                transactionUuid,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        requestToPayDeliveryNotification(transactionUuid, Constants.ProductTypes.COLLECTION)
                        requestToPayTransactionStatus(transactionUuid)
                        refund(transactionUuid)
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun createRequestTpPayTransaction(): Transaction {
        return Transaction(
            "10000",
            "EUR",
            null,
            Settings.generateUUID(),
            null,
            AccountHolder(AccountHolderType.MSISDN.name, "346736732646"),
            "Testing",
            "The Good Company",
            null,
            null,
        )
    }

    private fun requestToPayTransactionStatus(referenceId: String) {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.requestToPayTransactionStatus(
                referenceId,
                BuildConfig.MOMO_API_VERSION_V1,
                Settings.getProductSubscriptionKeys(ProductType.COLLECTION),
                accessToken,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        val completeTransfer =
                            Gson().fromJson(momoAPIResult.value!!.source().readUtf8(), Transaction::class.java)
                        toast("Request to pay is " + completeTransfer.status!!.lowercase())
                        Timber.d(completeTransfer.toString())
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun requestToWithdraw() {
        val accessToken = Utils.getAccessToken(this)
        val creditTransaction = createRequestTpPayTransaction()
        val transactionUuid = Settings.generateUUID()
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.requestToWithdraw(
                accessToken,
                creditTransaction,
                BuildConfig.MOMO_API_VERSION_V2,
                Settings.getProductSubscriptionKeys(ProductType.COLLECTION),
                transactionUuid,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        requestToWithdrawTransactionStatus(transactionUuid)
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun requestToWithdrawTransactionStatus(referenceId: String) {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.requestToWithdrawTransactionStatus(
                referenceId,
                BuildConfig.MOMO_API_VERSION_V1,
                Settings.getProductSubscriptionKeys(ProductType.COLLECTION),
                accessToken,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        val completeTransfer =
                            Gson().fromJson(momoAPIResult.value!!.source().readUtf8(), Transaction::class.java)
                        toast("Request to withdraw is " + completeTransfer.status!!.lowercase())
                        Timber.d(completeTransfer.toString())
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun deposit() {
        val accessToken = Utils.getAccessToken(this)
        val creditTransaction = createDebitTransaction()
        val transactionUuid = Settings.generateUUID()
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.deposit(
                accessToken,
                creditTransaction,
                BuildConfig.MOMO_API_VERSION_V2,
                Settings.getProductSubscriptionKeys(ProductType.DISBURSEMENTS),
                transactionUuid,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        getDepositStatus(transactionUuid)
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun getDepositStatus(referenceId: String) {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.getDepositStatus(
                referenceId,
                BuildConfig.MOMO_API_VERSION_V1,
                Settings.getProductSubscriptionKeys(ProductType.DISBURSEMENTS),
                accessToken,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        val completeTransfer =
                            Gson().fromJson(momoAPIResult.value!!.source().readUtf8(), Transaction::class.java)
                        toast("Request to deposit is " + completeTransfer.status!!.lowercase())
                        Timber.d(completeTransfer.toString())
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun refund(requestToPayUuid: String) {
        val accessToken = Utils.getAccessToken(this)
        val transactionUuid = Settings.generateUUID()
        if (StringUtils.isNotBlank(accessToken) && StringUtils.isNotBlank(requestToPayUuid)) {
            val creditTransaction = createRefundTransaction(requestToPayUuid)
            momoAPI.refund(
                accessToken,
                creditTransaction,
                BuildConfig.MOMO_API_VERSION_V2,
                Settings.getProductSubscriptionKeys(ProductType.DISBURSEMENTS),
                transactionUuid,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        getRefundStatus(transactionUuid)
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun getRefundStatus(referenceId: String) {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoAPI.getRefundStatus(
                referenceId,
                BuildConfig.MOMO_API_VERSION_V1,
                Settings.getProductSubscriptionKeys(ProductType.DISBURSEMENTS),
                accessToken,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        val completeTransfer =
                            Gson().fromJson(momoAPIResult.value!!.source().readUtf8(), Transaction::class.java)
                        toast("Request to refund is " + completeTransfer.status!!.lowercase())
                        Timber.d(completeTransfer.toString())
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun createRefundTransaction(requestToPayUuid: String): Transaction {
        return Transaction(
            "30",
            "EUR",
            null,
            Settings.generateUUID(),
            null,
            null,
            "Testing",
            "The Good Company",
            null,
            null,
            requestToPayUuid,
        )
    }*/
}
