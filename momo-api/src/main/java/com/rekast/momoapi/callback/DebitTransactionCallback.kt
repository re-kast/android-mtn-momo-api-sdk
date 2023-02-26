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
package com.rekast.momoapi.callback

import com.google.gson.GsonBuilder
import com.rekast.momoapi.model.api.CreditTransaction
import com.rekast.momoapi.model.api.DebitTransaction
import com.rekast.momoapi.model.api.ErrorResponse
import com.rekast.momoapi.utils.Settings
import com.rekast.momoapi.utils.TransactionStatus
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * Transaction Callback for the MTN MOMO API
 * The MTN MOMO API returns the a 200 OK even for transfers that failed.
 * We use [DebitTransactionCallback] to identify the difference between 200 OK and 400
 * This returns the [DebitTransaction] on the [ResponseBody]
 */
class DebitTransactionCallback(
    private val callback: (APIResult: APIResult<ResponseBody?>) -> Unit,
) : Callback<ResponseBody?> {

    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
        if (response.isSuccessful) {
            val transaction: CreditTransaction? = Settings.generateCreditTransaction(response)
            if (transaction?.status != null) {
                if (transaction.status == TransactionStatus.SUCCESSFUL.name) {
                    callback.invoke(
                        APIResult.Success(
                            GsonBuilder().setPrettyPrinting().create().toJson(transaction).toResponseBody(),
                        ),
                    )
                } else {
                    val error = "${transaction.reason} : ${transaction.reason}"
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

    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
        callback.invoke(APIResult.Failure(true, APIException(t.localizedMessage)))
    }
}
