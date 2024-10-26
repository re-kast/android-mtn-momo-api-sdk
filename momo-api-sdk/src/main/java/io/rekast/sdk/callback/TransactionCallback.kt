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
package io.rekast.sdk.callback

import com.google.gson.GsonBuilder
import io.io.rekast.momoapi.utils.Settings
import io.rekast.sdk.model.api.ErrorResponse
import io.rekast.sdk.model.api.MomoTransaction
import io.rekast.sdk.utils.TransactionStatus
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * Transaction Callback for the MTN MOMO API
 * The MTN MOMO API returns the a 200 OK even for transfers that failed.
 * We use [TransactionCallback] to identify the difference between 200 OK and 400
 * This returns the [MomoTransaction] on the [ResponseBody]
 */
class TransactionCallback(
    private val callback: (APIResult: APIResult<ResponseBody?>) -> Unit
) : Callback<ResponseBody?> {

    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
        if (response.isSuccessful) {
            val momoTransaction: MomoTransaction? = Settings.generateTransactionFromResponse(response)
            if (momoTransaction?.status != null) {
                if (momoTransaction.status == TransactionStatus.SUCCESSFUL.name) {
                    callback.invoke(
                        APIResult.Success(
                            GsonBuilder().setPrettyPrinting().create().toJson(momoTransaction).toResponseBody()
                        )
                    )
                } else {
                    val error = "${momoTransaction.reason} : ${momoTransaction.externalId}"
                    callback.invoke(APIResult.Failure(false, APIException(error)))
                }
                return
            }
        } else {
            try {
                val error = GsonBuilder().create().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                callback.invoke(APIResult.Failure(false, APIException(error)))
            } catch (e: IOException) {
                e.printStackTrace()
                callback.invoke(APIResult.Failure(false, APIException("${response.code()}")))
            }
        }
    }

    override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
        callback.invoke(APIResult.Failure(true, APIException(throwable.localizedMessage)))
    }
}
