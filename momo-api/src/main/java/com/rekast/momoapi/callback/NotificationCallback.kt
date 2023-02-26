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
import com.rekast.momoapi.model.api.ErrorResponse
import com.rekast.momoapi.model.api.Notification
import com.rekast.momoapi.model.api.Transaction
import com.rekast.momoapi.utils.Settings
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.apache.commons.lang3.StringUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * Transaction Callback for the MTN MOMO API
 * The MTN MOMO API returns the a 200 OK even for transfers that failed.
 * We use [NotificationCallback] to identify the difference between 200 OK and 400
 * This returns the [Transaction] on the [ResponseBody]
 */
class NotificationCallback(
    private val callback: (APIResult: APIResult<ResponseBody?>) -> Unit,
) : Callback<ResponseBody?> {

    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
        if (response.isSuccessful) {
            val notification: Notification? = Settings.generateNotificationFromResponse(response)
            if (StringUtils.isNotBlank(notification!!.notificationMessage)) {
                callback.invoke(
                    APIResult.Success(
                        GsonBuilder().setPrettyPrinting().create().toJson(notification).toResponseBody(),
                    ),
                )
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
