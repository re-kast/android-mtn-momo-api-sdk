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

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * MomoCallback for MomoAPI API MomoResponse
 * returns [onResponse] for successful calls and
 * [onFailure] for errors.
 */
class MomoCallback<T>(
    private val callback: (momoResponse: MomoResponse<T>) -> Unit
) : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            val data: T? = response.body()
            if (data != null) callback.invoke(MomoResponse.Success(data))
        } else {
            val code = "${response.code()}"
            var error = ""
            runCatching { error = "$code : ${response.errorBody()!!.string()}" }
            callback.invoke(MomoResponse.Failure(false, MomoException(error)))
        }
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) = callback.invoke(
        MomoResponse.Failure(
            true,
            MomoException(throwable.localizedMessage)
        )
    )
}
