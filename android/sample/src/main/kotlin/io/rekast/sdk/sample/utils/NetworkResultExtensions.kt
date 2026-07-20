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
package io.rekast.sdk.sample.utils

import io.rekast.sdk.repository.data.NetworkResult
import okhttp3.ResponseBody

/**
 * Reads this result's [ResponseBody] as a UTF-8 string, or returns `null` when there is no body.
 *
 * Centralises the single nullable check ([NetworkResult.response]); `source()` and `readUtf8()`
 * are non-null OkHttp calls, so keeping them here avoids repeating unreachable null-safe operators
 * at every call site.
 */
fun NetworkResult<ResponseBody>.bodyText(): String? = response?.let { it.source().readUtf8() }
