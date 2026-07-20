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
package io.rekast.sdk.sample.ui.components.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.theme.AppTheme
import io.rekast.sdk.sample.utils.SampleConfig
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI test for [ConfigCard], run on the JVM via Robolectric.
 *
 * Verifies the card resolves its title resource and renders each non-blank [SampleConfig] field
 * (environment, callback host, both API versions, and API user ID) as an info row.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class ConfigCardTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val sampleConfig = SampleConfig(
        apiVersionV1 = "v1_0",
        apiVersionV2 = "v2_0",
        environment = "sandbox",
        providerCallbackHost = "localhost",
        apiUserId = "user-123",
        collectionPrimaryKey = "cpk",
        collectionSecondaryKey = "csk",
        remittancePrimaryKey = "rpk",
        remittanceSecondaryKey = "rsk",
        disbursementsPrimaryKey = "dpk",
        disbursementsSecondaryKey = "dsk"
    )

    /** The config card renders its title and every configuration value. */
    @Test
    fun `renders configuration values`() {
        composeRule.setContent {
            AppTheme {
                ConfigCard(titleRes = R.string.setup_config_title, config = sampleConfig)
            }
        }
        composeRule.onNodeWithText("Configuration").assertIsDisplayed()
        composeRule.onNodeWithText("sandbox").assertIsDisplayed()
        composeRule.onNodeWithText("localhost").assertIsDisplayed()
        composeRule.onNodeWithText("v1_0").assertIsDisplayed()
        composeRule.onNodeWithText("v2_0").assertIsDisplayed()
        composeRule.onNodeWithText("user-123").assertIsDisplayed()
    }
}
