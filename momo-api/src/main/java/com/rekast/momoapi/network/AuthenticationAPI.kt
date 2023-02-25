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
package com.rekast.momoapi.network

import com.rekast.momoapi.model.authentication.ApiUser
import com.rekast.momoapi.model.authentication.ApiUserKey
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * This is the retrofit interface to handle the various calls to the Auth api.
 * This interface defines the method, the request and response from the API.
 */
sealed interface AuthenticationAPI {
    @GET("/{apiVersion}/apiuser/{apiUser}")
    fun getApiUser(@Path("apiVersion") apiVersion: String, @Path("apiUser") apiUser: String): Response<ApiUser>

    @POST("/{apiVersion}/apiuser/{apiUser}/apikey")
    fun getApiUserKey(@Path("apiVersion") apiVersion: String, @Path("apiUser") apiUser: String): Response<ApiUserKey>
}
