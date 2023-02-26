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
package com.rekast.momoapi.sample.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.rekast.momoapi.BuildConfig
import com.rekast.momoapi.MomoAPI
import com.rekast.momoapi.callback.APIResult
import com.rekast.momoapi.model.api.AccountHolder
import com.rekast.momoapi.model.api.AccountHolderStatus
import com.rekast.momoapi.model.api.Notification
import com.rekast.momoapi.model.api.Transaction
import com.rekast.momoapi.sample.R
import com.rekast.momoapi.sample.databinding.ActivityMainBinding
import com.rekast.momoapi.sample.utils.Utils
import com.rekast.momoapi.utils.AccountHolderType
import com.rekast.momoapi.utils.Constants
import com.rekast.momoapi.utils.ProductType
import com.rekast.momoapi.utils.Settings
import org.apache.commons.lang3.StringUtils
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var momoRemittanceApi: MomoAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        momoRemittanceApi = momoRemittanceApi()
        checkUser()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) ||
            super.onSupportNavigateUp()
    }

    private fun momoRemittanceApi(): MomoAPI {
        return MomoAPI.builder(BuildConfig.MOMO_API_USER_ID)
            .setEnvironment(BuildConfig.MOMO_ENVIRONMENT)
            .getBaseURL(BuildConfig.MOMO_BASE_URL)
            .build()
    }

    private fun checkUser() {
        momoRemittanceApi.checkApiUser(
            Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
            BuildConfig.MOMO_API_VERSION_V1,
        ) { momoAPIResult ->
            when (momoAPIResult) {
                is APIResult.Success -> {
                    getApiKey()
                }
                is APIResult.Failure -> {
                    val momoAPIException = momoAPIResult.APIException
                    toast(momoAPIException?.message ?: "An error occurred!")
                }
            }
        }
    }

    private fun getApiKey() {
        momoRemittanceApi.getUserApiKey(
            Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
            BuildConfig.MOMO_API_VERSION_V1,
        ) { momoAPIResult ->
            when (momoAPIResult) {
                is APIResult.Success -> {
                    val apiUserKey = momoAPIResult.value
                    Utils.saveApiKey(this, apiUserKey.apiKey)
                    getAccessToken()
                }
                is APIResult.Failure -> {
                    val momoAPIException = momoAPIResult.APIException
                    toast(momoAPIException?.message ?: "An error occurred!")
                }
            }
        }
    }

    private fun getAccessToken() {
        val apiUserKey = Utils.getApiKey(this)
        if (StringUtils.isNotBlank(apiUserKey)) {
            momoRemittanceApi.getAccessToken(
                Settings.getProductSubscriptionKeys(ProductType.REMITTANCE),
                apiUserKey,
                Constants.ProductTypes.REMITTANCE,
            ) { momoAPIResult ->
                when (momoAPIResult) {
                    is APIResult.Success -> {
                        val accessToken = momoAPIResult.value
                        Utils.saveAccessToken(this, accessToken)
                        // getAccountBalance()
                        // getBasicUserInfo()
                        // transferRemittance()
                        // validateAccountHolderStatus()
                        // requestToPay()
                        requestToWithdraw()
                    }
                    is APIResult.Failure -> {
                        val momoAPIException = momoAPIResult.APIException
                        toast(momoAPIException?.message ?: "An error occurred!")
                    }
                }
            }
        }
    }

    private fun getAccountBalance() {
        val accessToken = Utils.getAccessToken(this)
        if (StringUtils.isNotBlank(accessToken)) {
            momoRemittanceApi.getBalance(
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
            momoRemittanceApi.getBasicUserInfo(
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
            momoRemittanceApi.getUserInfoWithConsent(
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
        val transactionUuid = Utils.generateUUID()
        if (StringUtils.isNotBlank(accessToken)) {
            momoRemittanceApi.transfer(
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
            Utils.generateUUID(),
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
            momoRemittanceApi.getTransferStatus(
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
            momoRemittanceApi.requestToPayDeliveryNotification(
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
            momoRemittanceApi.validateAccountHolderStatus(
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
        val transactionUuid = Utils.generateUUID()
        if (StringUtils.isNotBlank(accessToken)) {
            momoRemittanceApi.requestToPay(
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
            Utils.generateUUID(),
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
            momoRemittanceApi.requestToPayTransactionStatus(
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
        val transactionUuid = Utils.generateUUID()
        if (StringUtils.isNotBlank(accessToken)) {
            momoRemittanceApi.requestToWithdraw(
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
            momoRemittanceApi.requestToWithdrawTransactionStatus(
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

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}
