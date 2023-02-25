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
import com.rekast.momoapi.model.PaymentResult
import com.rekast.momoapi.model.api.MomoErrorResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * PaymentCallback for Mpesa Payments.
 */
class MomoAPIPaymentCallback(
    private val callback: (momoAPIResult: MomoAPIResult<PaymentResult>) -> Unit,
) : Callback<PaymentResult> {

    override fun onResponse(call: Call<PaymentResult>, response: Response<PaymentResult>) {
        if (response.isSuccessful) {
            val result: PaymentResult? = response.body()
            if (result != null) {
                if (result.ResponseCode == "0") {
                    callback.invoke(MomoAPIResult.Success(result))
                } else {
                    val error = "${result.ResponseCode} : ${result.ResponseDescription}"
                    callback.invoke(MomoAPIResult.Failure(false, MomoAPIException(error)))
                }
                return
            }
        } else {
            try {
                val gson = GsonBuilder().create()
                val error = gson.fromJson(response.errorBody()?.string(), MomoErrorResponse::class.java)
                callback.invoke(MomoAPIResult.Failure(false, MomoAPIException(error)))
            } catch (e: IOException) {
                e.printStackTrace()
                callback.invoke(MomoAPIResult.Failure(false, MomoAPIException("${response.code()}")))
            }
        }
    }

    override fun onFailure(call: Call<PaymentResult>, t: Throwable) {
        callback.invoke(MomoAPIResult.Failure(true, MomoAPIException(t.localizedMessage)))
    }
}
