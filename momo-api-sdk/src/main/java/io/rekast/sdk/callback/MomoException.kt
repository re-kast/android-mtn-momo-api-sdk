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

import io.rekast.sdk.model.api.ErrorResponse

/**
 * Handles exceptions and messages for the exceptions.
 */
class MomoException : Exception {
    lateinit var errorResponse: ErrorResponse
    constructor(message: String?) : super(message)
    constructor(errorResponse: ErrorResponse) : super("${errorResponse.code} : ${errorResponse.message}") {
        this.errorResponse = errorResponse
    }
}
