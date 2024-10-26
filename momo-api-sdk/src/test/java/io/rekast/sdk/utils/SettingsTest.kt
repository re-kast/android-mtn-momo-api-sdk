/*
 *              Apache License
 *        Version 2.0, January 2004
 *     http://www.apache.org/licenses/
 *
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
package io.rekast.sdk.utils

import io.io.rekast.momoapi.utils.Settings
import io.rekast.sdk.BuildConfig
import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SettingsTest {
    @Test
    fun addition_isCorrect() {
        Assert.assertEquals(4, 2 + 2)
    }

    @Test
    fun getProductSubscriptionKeysForRemittance() {
        val productType = Settings.getProductSubscriptionKeys(ProductType.REMITTANCE)
        Assert.assertEquals(productType, BuildConfig.MOMO_REMITTANCE_PRIMARY_KEY)
    }

    @Test
    fun getProductSubscriptionKeysForDisbursements() {
        val productType = Settings.getProductSubscriptionKeys(ProductType.DISBURSEMENTS)
        Assert.assertEquals(productType, BuildConfig.MOMO_DISBURSEMENTS_PRIMARY_KEY)
    }

    @Test
    fun getProductSubscriptionKeysForCollection() {
        val productType = Settings.getProductSubscriptionKeys(ProductType.COLLECTION)
        Assert.assertEquals(productType, BuildConfig.MOMO_COLLECTION_PRIMARY_KEY)
    }
}
