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
package io.rekast.sdk.sample.views.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.rekast.sdk.model.AccountBalance
import io.rekast.sdk.model.AccountHolder
import io.rekast.sdk.model.AccountHolderStatus
import io.rekast.sdk.model.BasicUserInfo
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.DispatcherProvider
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import io.rekast.sdk.sample.utils.Utils
import io.rekast.sdk.utils.AccountHolderType
import io.rekast.sdk.utils.ProductType
import io.rekast.sdk.utils.Settings
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber

/**
 * ViewModel for the Home screen, responsible for fetching and exposing basic user info,
 * account holder status, and account balance from the MTN MOMO API.
 *
 * Each data-fetching method collects from a [kotlinx.coroutines.flow.Flow] returned by
 * [io.rekast.sdk.repository.DefaultRepository]. The flow always emits
 * [io.rekast.sdk.repository.data.NetworkResult.Loading] first, followed by a terminal
 * [io.rekast.sdk.repository.data.NetworkResult.Success] or
 * [io.rekast.sdk.repository.data.NetworkResult.Error].
 *
 * Because all four calls on the Home screen launch concurrently, [showProgressBar] is driven by
 * [activeRequestCount] — an `AtomicInteger` that is incremented on each `Loading` emission and
 * decremented on each terminal emission. The progress bar stays visible until the count reaches
 * zero, preventing any single call from hiding the spinner while the others are still in-flight.
 *
 * One-off UI events (snackbar messages) are emitted via [snackBarStateFlow], a [SharedFlow]
 * that the composable collects inside a `LaunchedEffect`.
 *
 * All API calls are guarded by a check on [CredentialStorage.getAccessToken]: if no valid
 * token is present the request is skipped and a snackbar is shown instead. In normal operation
 * the access token is provisioned by [io.rekast.sdk.sample.views.MainViewModel] on first
 * launch and refreshed automatically by `TokenAuthenticator` on 401.
 */
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val defaultRepository: DefaultRepository,
    private val credentialStorage: CredentialStorage,
    private val settings: Settings,
    private val dispatchers: DispatcherProvider,
    private val sampleConfig: SampleConfig
) : ViewModel() {
    /** Controls whether the circular progress indicator is shown on the Home screen. */
    val showProgressBar = MutableLiveData(false)

    /**
     * Counts how many API requests are currently in-flight.
     * The progress bar is shown while this is > 0 and hidden when it reaches 0,
     * preventing concurrent calls from toggling the bar off while others are still loading.
     */
    private val activeRequestCount = AtomicInteger(0)

    private val _snackBarStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>()

    /** Flow of [SnackBarComponentConfiguration] events to be displayed as snackbars. */
    val snackBarStateFlow: SharedFlow<SnackBarComponentConfiguration> = _snackBarStateFlow.asSharedFlow()

    /** Holds the fetched [BasicUserInfo] for the authenticated user; null until the API responds. */
    var basicUserInfo: MutableLiveData<BasicUserInfo?> = MutableLiveData(null)

    /** Holds the fetched [AccountHolderStatus] for the account; null until the API responds. */
    var accountHolderStatus: MutableLiveData<AccountHolderStatus?> = MutableLiveData(null)

    /** Holds the fetched [AccountBalance] for the account; null until the API responds. */
    var accountBalance: MutableLiveData<AccountBalance?> = MutableLiveData(null)

    /**
     * Fetches basic user info from the Remittance API and posts the result to [basicUserInfo].
     */
    fun getBasicUserInfo() {
        val productType = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig)

        viewModelScope.launch(dispatchers.io()) {
            if (credentialStorage.getAccessToken().isNotBlank()) {
                defaultRepository.getBasicUserInfo(
                    productType = ProductType.REMITTANCE.productType,
                    apiVersion = sampleConfig.apiVersionV1,
                    accountHolder = "99733123459",
                    productSubscriptionKey = productType,
                    environment = sampleConfig.environment
                ).collect { foundBasicUserInfo ->
                    when (foundBasicUserInfo) {
                        is NetworkResult.Loading -> {
                            activeRequestCount.incrementAndGet()
                            showProgressBar.postValue(true)
                        }

                        is NetworkResult.Success -> {
                            val userInfo = foundBasicUserInfo.response
                            val date = Utils.convertToDate(userInfo?.updatedAt!!.toLong())
                            userInfo.displayUpdatedAt = date
                            basicUserInfo.postValue(userInfo)

                            Timber.d("Basic user info was fetched successfully")
                            showProgressBar.postValue(activeRequestCount.decrementAndGet() > 0)
                            emitSnackBarState(
                                SnackBarComponentConfiguration(message = "Basic user info was fetched successfully")
                            )
                        }

                        is NetworkResult.Error -> {
                            Timber.e("Basic user info was not fetched %s", foundBasicUserInfo.message)
                            showProgressBar.postValue(activeRequestCount.decrementAndGet() > 0)

                            val message = foundBasicUserInfo.message
                            emitSnackBarState(
                                SnackBarComponentConfiguration(message = "Basic user info was not fetched $message")
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Fetches user info with OAuth2 consent from the Remittance API and logs the result.
     */
    fun getUserInfoWithConsent() {
        val productType = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig)

        viewModelScope.launch(dispatchers.io()) {
            if (credentialStorage.getAccessToken().isNotBlank()) {
                defaultRepository.getUserInfoWithConsent(
                    productType = ProductType.REMITTANCE.productType,
                    apiVersion = sampleConfig.apiVersionV1,
                    productSubscriptionKey = productType,
                    environment = sampleConfig.environment
                ).collect { userInfoWithConsent ->
                    when (userInfoWithConsent) {
                        is NetworkResult.Loading -> {
                            activeRequestCount.incrementAndGet()
                            showProgressBar.postValue(true)
                        }

                        is NetworkResult.Success -> {
                            Timber.d(userInfoWithConsent.response.toString())

                            Timber.d("Basic user info with consent was fetched successfully")
                            showProgressBar.postValue(activeRequestCount.decrementAndGet() > 0)
                            emitSnackBarState(
                                SnackBarComponentConfiguration(message = "Basic user info with consent was fetched successfully")
                            )
                        }

                        is NetworkResult.Error -> {
                            Timber.e("Basic user info with consent was not fetched %s", userInfoWithConsent.message)
                            showProgressBar.postValue(activeRequestCount.decrementAndGet() > 0)

                            val message = userInfoWithConsent.message
                            emitSnackBarState(
                                SnackBarComponentConfiguration(message = "Basic user info with consent was not fetched $message")
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Validates the account holder status via the Remittance API and posts the result to [accountHolderStatus].
     */
    fun validateAccountHolderStatus() {
        viewModelScope.launch(dispatchers.io()) {
            val accountHolder = AccountHolder(
                partyId = "99733123459",
                partyIdType = AccountHolderType.MSISDN.accountHolderType
            )
            if (credentialStorage.getAccessToken().isNotBlank()) {
                defaultRepository.validateAccountHolderStatus(
                    productType = ProductType.REMITTANCE.productType,
                    apiVersion = sampleConfig.apiVersionV1,
                    accountHolder = accountHolder,
                    productSubscriptionKey = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig),
                    environment = sampleConfig.environment
                ).collect { foundStatus ->
                    when (foundStatus) {
                        is NetworkResult.Loading -> {
                            activeRequestCount.incrementAndGet()
                            showProgressBar.postValue(true)
                        }

                        is NetworkResult.Success -> {
                            val status = Json.decodeFromString<AccountHolderStatus>(foundStatus.response!!.source().readUtf8())
                            accountHolderStatus.postValue(status)

                            Timber.d("Account Holder status was fetched successfully")
                            showProgressBar.postValue(activeRequestCount.decrementAndGet() > 0)
                            emitSnackBarState(
                                SnackBarComponentConfiguration(message = "Account Holder status was fetched successfully")
                            )
                        }

                        is NetworkResult.Error -> {
                            Timber.e("Account Holder status was not fetched %s", foundStatus.message)
                            showProgressBar.postValue(activeRequestCount.decrementAndGet() > 0)

                            val message = foundStatus.message
                            emitSnackBarState(
                                SnackBarComponentConfiguration(message = "Account Holder status was not fetched $message")
                            )
                        }
                    }
                }
            } else {
                emitSnackBarState(
                    SnackBarComponentConfiguration(
                        message = "Expired access token! Please refresh the token"
                    )
                )
            }
        }
    }

    /**
     * Fetches the account balance via the Collection API and posts the result to [accountBalance].
     *
     * Note: This function only works with the Collection API product type.
     */
    fun getAccountBalance() {
        viewModelScope.launch(dispatchers.io()) {
            if (credentialStorage.getAccessToken().isNotBlank()) {
                defaultRepository.getAccountBalance(
                    productType = ProductType.COLLECTION.productType,
                    apiVersion = sampleConfig.apiVersionV1,
                    currency = "",
                    productSubscriptionKey = Utils.getProductSubscriptionKeys(ProductType.COLLECTION, sampleConfig),
                    environment = sampleConfig.environment
                ).collect { balance ->
                    when (balance) {
                        is NetworkResult.Loading -> {
                            activeRequestCount.incrementAndGet()
                            showProgressBar.postValue(true)
                        }

                        is NetworkResult.Success -> {
                            accountBalance.postValue(balance.response)

                            Timber.d("Account balance fetched successfully")
                            showProgressBar.postValue(activeRequestCount.decrementAndGet() > 0)
                            emitSnackBarState(
                                SnackBarComponentConfiguration(
                                    message = "Account balance fetched successfully"
                                )
                            )
                        }

                        is NetworkResult.Error -> {
                            showProgressBar.postValue(activeRequestCount.decrementAndGet() > 0)

                            val message = balance.message
                            Timber.e("Account balance not fetched! %s", message)
                            emitSnackBarState(
                                SnackBarComponentConfiguration(
                                    message = "Account balance not fetched! $message"
                                )
                            )
                        }
                    }
                }
            } else {
                emitSnackBarState(
                    SnackBarComponentConfiguration(
                        message = "Expired access token! Please refresh the token"
                    )
                )
            }
        }
    }
    private fun emitSnackBarState(snackBarComponentConfiguration: SnackBarComponentConfiguration) {
        viewModelScope.launch { _snackBarStateFlow.emit(snackBarComponentConfiguration) }
    }
}
