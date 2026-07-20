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
import io.rekast.sdk.model.UserInfoWithConsent
import io.rekast.sdk.repository.DefaultRepository
import io.rekast.sdk.repository.data.NetworkResult
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.utils.CredentialStorage
import io.rekast.sdk.sample.utils.DispatcherProvider
import io.rekast.sdk.sample.utils.SampleConfig
import io.rekast.sdk.sample.utils.SnackBarComponentConfiguration
import io.rekast.sdk.sample.utils.SnackBarType
import io.rekast.sdk.sample.utils.Utils
import io.rekast.sdk.sample.utils.messageOrEmpty
import io.rekast.sdk.utils.AccountHolderType
import io.rekast.sdk.utils.ProductType
import io.rekast.sdk.utils.Settings
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber

/**
 * ViewModel for the Home screen, responsible for fetching and exposing the signed-in subscriber's
 * verified profile, basic user info, account holder status, and account balance from the MTN MOMO API.
 *
 * All four calls run through a single ordered pipeline in [loadHomeData] rather than concurrently:
 *
 * 1. **Verified profile** (`userinfo`, consent) — fetched first because it identifies the subscriber;
 *    its phone number becomes the account holder used by the next two calls.
 * 2. **Basic user info** — for the account holder derived from step 1.
 * 3. **Account holder status** — for the same account holder.
 * 4. **Account balance** — independent of the others.
 *
 * Because the pipeline is a single coroutine, [showProgressBar] is set to `true` once at the start
 * and back to `false` in a `finally` block only after every step has completed — the spinner never
 * hides while any request is still in flight, and later steps can wait on earlier steps' data.
 *
 * One-off UI events (snackbar messages) are emitted via [snackBarStateFlow], a [SharedFlow]
 * that the composable collects inside a `LaunchedEffect`.
 *
 * The whole pipeline is guarded by a single check on [CredentialStorage.getAccessToken]: if no valid
 * token is present the load is skipped and an error snackbar is shown. In normal operation the access
 * token is provisioned by [io.rekast.sdk.sample.views.MainViewModel] on first launch and refreshed
 * automatically by `TokenAuthenticator` on 401.
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

    private val _snackBarStateFlow = MutableSharedFlow<SnackBarComponentConfiguration>()

    /** Flow of [SnackBarComponentConfiguration] events to be displayed as snackbars. */
    val snackBarStateFlow: SharedFlow<SnackBarComponentConfiguration> = _snackBarStateFlow.asSharedFlow()

    /** Holds the fetched [BasicUserInfo] for the authenticated user; null until the API responds. */
    var basicUserInfo: MutableLiveData<BasicUserInfo?> = MutableLiveData(null)

    /** Holds the fetched consent-granted [UserInfoWithConsent] profile; null until the API responds. */
    var userInfoWithConsent: MutableLiveData<UserInfoWithConsent?> = MutableLiveData(null)

    /** Holds the fetched [AccountHolderStatus] for the account; null until the API responds. */
    var accountHolderStatus: MutableLiveData<AccountHolderStatus?> = MutableLiveData(null)

    /** Holds the fetched [AccountBalance] for the account; null until the API responds. */
    var accountBalance: MutableLiveData<AccountBalance?> = MutableLiveData(null)

    /**
     * Loads all Home screen data in a single ordered pipeline. Shows the progress bar for the whole
     * batch, fetches each resource in sequence — so a step can wait on the previous step's data — and
     * hides the progress bar only once every step has finished (success or failure).
     */
    fun loadHomeData() {
        viewModelScope.launch(dispatchers.io()) {
            if (credentialStorage.getAccessToken().isBlank()) {
                Timber.w("Home data load skipped: access token is blank")
                emitSnackBarState(
                    SnackBarComponentConfiguration(
                        messageResId = R.string.snackbar_token_expired,
                        type = SnackBarType.ERROR
                    )
                )
                return@launch
            }

            showProgressBar.postValue(true)
            try {
                // 1. Verified profile first — its phone number identifies the account holder that the
                //    account-scoped calls below depend on.
                val consent = fetchUserInfoWithConsent()
                val accountHolder = consent?.phonenumber?.takeIf { it.isNotBlank() } ?: DEFAULT_ACCOUNT_HOLDER

                // 2 & 3. Account-scoped calls, using the account holder resolved from step 1.
                fetchBasicUserInfo(accountHolder)
                fetchAccountHolderStatus(accountHolder)

                // 4. Account balance — independent of the account holder.
                fetchAccountBalance()
            } finally {
                showProgressBar.postValue(false)
            }
        }
    }

    /**
     * Fetches the consent-granted verified profile from the Remittance API, posts it to
     * [userInfoWithConsent], and returns it so the caller can derive the account holder from it.
     */
    private suspend fun fetchUserInfoWithConsent(): UserInfoWithConsent? {
        val result = defaultRepository.getUserInfoWithConsent(
            productType = ProductType.REMITTANCE.productType,
            apiVersion = sampleConfig.apiVersionV1,
            productSubscriptionKey = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig),
            environment = sampleConfig.environment
        ).awaitTerminal()

        return when (result) {
            is NetworkResult.Success -> {
                userInfoWithConsent.postValue(result.response)
                Timber.d("User info with consent was fetched successfully")
                emitSnackBarState(
                    SnackBarComponentConfiguration(
                        messageResId = R.string.snackbar_verified_profile_fetched,
                        type = SnackBarType.SUCCESS
                    )
                )
                result.response
            }

            else -> {
                Timber.e("User info with consent was not fetched: %s", result.message)
                emitSnackBarState(
                    SnackBarComponentConfiguration(
                        messageResId = R.string.snackbar_verified_profile_failed,
                        messageArgs = listOf(result.message),
                        type = SnackBarType.ERROR
                    )
                )
                null
            }
        }
    }

    /**
     * Fetches basic user info for [accountHolder] and posts the result to [basicUserInfo].
     */
    private suspend fun fetchBasicUserInfo(accountHolder: String) {
        val result = defaultRepository.getBasicUserInfo(
            productType = ProductType.REMITTANCE.productType,
            apiVersion = sampleConfig.apiVersionV1,
            accountHolder = accountHolder,
            productSubscriptionKey = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig),
            environment = sampleConfig.environment
        ).awaitTerminal()

        when (result) {
            is NetworkResult.Success -> {
                val info = result.response
                info?.updatedAt?.let { info.displayUpdatedAt = Utils.convertToDate(it.toLong()) }
                basicUserInfo.postValue(info)
                Timber.d("Basic user info was fetched successfully")
                emitSnackBarState(
                    SnackBarComponentConfiguration(
                        messageResId = R.string.snackbar_basic_user_info_fetched,
                        type = SnackBarType.SUCCESS
                    )
                )
            }

            else -> {
                Timber.e("Basic user info was not fetched: %s", result.message)
                emitSnackBarState(
                    SnackBarComponentConfiguration(
                        messageResId = R.string.snackbar_basic_user_info_failed,
                        messageArgs = listOf(result.message),
                        type = SnackBarType.ERROR
                    )
                )
            }
        }
    }

    /**
     * Validates the account holder status for [accountHolder] and posts the result to [accountHolderStatus].
     */
    private suspend fun fetchAccountHolderStatus(accountHolder: String) {
        val holder = AccountHolder(
            partyId = accountHolder,
            partyIdType = AccountHolderType.MSISDN.accountHolderType
        )
        val result = defaultRepository.validateAccountHolderStatus(
            productType = ProductType.REMITTANCE.productType,
            apiVersion = sampleConfig.apiVersionV1,
            accountHolder = holder,
            productSubscriptionKey = Utils.getProductSubscriptionKeys(ProductType.REMITTANCE, sampleConfig),
            environment = sampleConfig.environment
        ).awaitTerminal()

        when (result) {
            is NetworkResult.Success -> {
                runCatching {
                    Json.decodeFromString<AccountHolderStatus>(result.response!!.source().readUtf8())
                }.onSuccess { status ->
                    accountHolderStatus.postValue(status)
                    Timber.d("Account Holder status was fetched successfully")
                    emitSnackBarState(
                        SnackBarComponentConfiguration(
                            messageResId = R.string.snackbar_account_status_fetched,
                            type = SnackBarType.SUCCESS
                        )
                    )
                }.onFailure { throwable ->
                    Timber.e(throwable, "Account Holder status could not be parsed")
                    emitSnackBarState(
                        SnackBarComponentConfiguration(
                            messageResId = R.string.snackbar_account_status_unreadable,
                            messageArgs = listOf(throwable.messageOrEmpty()),
                            type = SnackBarType.ERROR
                        )
                    )
                }
            }

            else -> {
                Timber.e("Account Holder status was not fetched: %s", result.message)
                emitSnackBarState(
                    SnackBarComponentConfiguration(
                        messageResId = R.string.snackbar_account_status_failed,
                        messageArgs = listOf(result.message),
                        type = SnackBarType.ERROR
                    )
                )
            }
        }
    }

    /**
     * Fetches the account balance and posts the result to [accountBalance].
     *
     * Uses the Collection product type and subscription key, not Remittance: the MTN MoMo balance
     * endpoint only works reliably with [ProductType.COLLECTION] (see
     * [io.rekast.sdk.repository.DefaultRepository.getAccountBalance]), and calling it against
     * Remittance commonly returns 401/404. This is safe because the Bearer token provisioned during
     * bootstrap is api-user-scoped — it is accepted across products — so pairing it with the
     * Collection subscription key targets the Collection balance endpoint the same way the Collection
     * screens do. The account holder / user-info calls above remain on Remittance because those
     * endpoints are product-agnostic.
     */
    private suspend fun fetchAccountBalance() {
        val result = defaultRepository.getAccountBalance(
            productType = ProductType.COLLECTION.productType,
            apiVersion = sampleConfig.apiVersionV1,
            currency = "",
            productSubscriptionKey = Utils.getProductSubscriptionKeys(ProductType.COLLECTION, sampleConfig),
            environment = sampleConfig.environment
        ).awaitTerminal()

        when (result) {
            is NetworkResult.Success -> {
                accountBalance.postValue(result.response)
                Timber.d("Account balance fetched successfully")
                emitSnackBarState(
                    SnackBarComponentConfiguration(
                        messageResId = R.string.snackbar_account_balance_fetched,
                        type = SnackBarType.SUCCESS
                    )
                )
            }

            else -> {
                Timber.e("Account balance was not fetched: %s", result.message)
                emitSnackBarState(
                    SnackBarComponentConfiguration(
                        messageResId = R.string.snackbar_account_balance_failed,
                        messageArgs = listOf(result.message),
                        type = SnackBarType.ERROR
                    )
                )
            }
        }
    }

    /**
     * Collects this result [Flow] to completion and returns the terminal (non-[NetworkResult.Loading])
     * emission, so a caller can `await` the flow's final [NetworkResult.Success] or [NetworkResult.Error].
     * Returns a synthetic [NetworkResult.Error] if the flow completes without a terminal emission.
     */
    private suspend fun <T> Flow<NetworkResult<T>>.awaitTerminal(): NetworkResult<T> {
        var terminal: NetworkResult<T> = NetworkResult.Error("No response received")
        collect { emission -> if (emission !is NetworkResult.Loading) terminal = emission }
        return terminal
    }

    private fun emitSnackBarState(snackBarComponentConfiguration: SnackBarComponentConfiguration) {
        viewModelScope.launch { _snackBarStateFlow.emit(snackBarComponentConfiguration) }
    }

    private companion object {
        /** Fallback account-holder MSISDN used when the consent profile carries no phone number. */
        const val DEFAULT_ACCOUNT_HOLDER = "99733123459"
    }
}
