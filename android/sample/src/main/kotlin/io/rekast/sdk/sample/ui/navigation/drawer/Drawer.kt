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
package io.rekast.sdk.sample.ui.navigation.drawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.rekast.sdk.sample.R
import io.rekast.sdk.sample.ui.navigation.navigation.NavigationDrawerItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Renders the navigation drawer content with a header, a list of [NavigationDrawerItem] entries,
 * and a copyright footer; navigates to the selected item's route and closes the drawer on click.
 *
 * @param scope [CoroutineScope] used to launch the drawer close animation.
 * @param scaffoldState [ScaffoldState] providing access to the drawer state.
 * @param navController [NavController] used to navigate when a drawer item is selected.
 */
@Composable
fun Drawer(scope: CoroutineScope, scaffoldState: ScaffoldState, navController: NavController) {
    val sections = listOf(
        R.string.section_general to listOf(NavigationDrawerItem.Home, NavigationDrawerItem.Setup, NavigationDrawerItem.Settings),
        R.string.section_collection to listOf(
            NavigationDrawerItem.CollectionRequestToPay,
            NavigationDrawerItem.CollectionRequestToWithdraw,
            NavigationDrawerItem.CollectionInvoice,
            NavigationDrawerItem.CollectionPreApproval,
            NavigationDrawerItem.CollectionApprovedPreApprovals
        ),
        R.string.section_disbursement to listOf(
            NavigationDrawerItem.DisbursementDeposit,
            NavigationDrawerItem.DisbursementRefund
        ),
        R.string.section_remittance to listOf(
            NavigationDrawerItem.Remittance,
            NavigationDrawerItem.RemittanceCashTransfer
        )
    )
    val expandedSections = remember {
        mutableStateMapOf<Int, Boolean>().apply { sections.forEach { put(it.first, true) } }
    }
    Column {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.drawer_header_height))
                .padding(start = dimensionResource(id = R.dimen.spacing_medium), end = dimensionResource(id = R.dimen.spacing_medium))
        ) {
            Column {
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_extra_small)))
                Row {
                    Text(
                        text = stringResource(id = R.string.app_title),
                        fontSize = with(LocalDensity.current) { dimensionResource(id = R.dimen.font_size_medium).toSp() },
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.spacing_medium)))
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacing_extra_small)))
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            sections.forEach { (sectionTitle, sectionItems) ->
                val expanded = expandedSections[sectionTitle] != false
                DrawerSectionHeader(
                    title = stringResource(id = sectionTitle),
                    expanded = expanded,
                    onToggle = { expandedSections[sectionTitle] = !expanded }
                )
                AnimatedVisibility(visible = expanded) {
                    Column {
                        sectionItems.forEach { item ->
                            DrawerItem(item = item, selected = navBackStackEntry?.destination?.id == item.route, onItemClick = {
                                navController.navigate(item.route)
                                scope.launch {
                                    scaffoldState.drawerState.close()
                                }
                            })
                        }
                    }
                }
            }
        }
        Text(
            text = stringResource(id = R.string.copyrights),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.spacing_large))
                .align(Alignment.CenterHorizontally)
        )
    }
}

/**
 * A muted, tappable section header that collapses or expands its group of destinations.
 *
 * @param title The section label, rendered upper-cased.
 * @param expanded Whether the section is currently expanded; drives the chevron rotation.
 * @param onToggle Invoked when the header is tapped to toggle the section.
 */
@Composable
private fun DrawerSectionHeader(title: String, expanded: Boolean, onToggle: () -> Unit) {
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "sectionChevron")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(
                start = dimensionResource(id = R.dimen.spacing_large),
                end = dimensionResource(id = R.dimen.spacing_large),
                top = dimensionResource(id = R.dimen.spacing_medium),
                bottom = dimensionResource(id = R.dimen.spacing_extra_small)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title.uppercase(),
            color = MaterialTheme.colors.secondary,
            fontWeight = FontWeight.Bold,
            fontSize = with(LocalDensity.current) { dimensionResource(id = R.dimen.font_size_section_header).toSp() }
        )
        Image(
            painter = painterResource(id = R.drawable.expand_more),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary),
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.icon_size_default))
                .rotate(rotation)
        )
    }
}

@Preview(showBackground = false)
@Composable
fun DrawerPreview() {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val navController = rememberNavController()
    Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
}
