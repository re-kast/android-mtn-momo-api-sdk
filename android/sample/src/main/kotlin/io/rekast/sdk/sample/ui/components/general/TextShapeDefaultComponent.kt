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
package io.rekast.sdk.sample.ui.components.general

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Returns the [Shape] applied to text fields throughout the sample application.
 *
 * Uses [androidx.compose.foundation.shape.RoundedCornerShape] with a 3.dp corner radius,
 * giving fields a subtly rounded appearance. Border thickness (stroke width) is separate
 * from the shape and must be configured at the call site — e.g. via
 * `Modifier.border(width, color, shape)`.
 */
fun textShapeDefaultComponent(): Shape = RoundedCornerShape(3.dp)
