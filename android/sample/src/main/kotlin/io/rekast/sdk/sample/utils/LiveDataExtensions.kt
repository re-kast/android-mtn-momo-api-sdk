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

import androidx.lifecycle.LiveData

/**
 * Returns this [LiveData]'s current value, or [Constants.EMPTY_STRING] when it has not been set.
 *
 * Centralises the nullable [LiveData.value] read so that view-model form-building code can work
 * with non-null strings without repeating a null check at every call site.
 */
fun LiveData<String>.valueOrEmpty(): String = value ?: Constants.EMPTY_STRING

/**
 * Returns this [LiveData]'s current value, or `null` when it is unset or blank.
 *
 * Used for optional payload fields that should be omitted (sent as `null`) when the user has not
 * entered anything.
 */
fun LiveData<String>.valueOrNullIfBlank(): String? = value?.ifBlank { null }
