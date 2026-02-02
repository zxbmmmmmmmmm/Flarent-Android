package com.bettafish.flarent.ui.widgets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints

//@Composable
//@UiComposable
//fun TabRow(
//    selectedTabIndex: Int,
//    modifier: Modifier = Modifier,
//    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
//    contentColor: Color = contentColorFor(backgroundColor),
//    indicator: @Composable @UiComposable
//        (tabPositions: List<TabPosition>) -> Unit = @Composable { tabPositions ->
//        TabRowDefaults.Indicator(
//            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
//        )
//    },
//    tabs: @Composable @UiComposable () -> Unit,
//) {
//    Surface(
//        modifier = modifier.selectableGroup(),
//        color = backgroundColor,
//        contentColor = contentColor
//    ) {
//        SubcomposeLayout(Modifier.fillMaxWidth()) { constraints ->
//            val tabRowWidth = constraints.maxWidth
//            val tabMeasurables = subcompose(TabSlots.Tabs, tabs)
//            val tabCount = tabMeasurables.size
//            val tabWidth = (tabRowWidth / tabCount)
//            val tabPlaceables = tabMeasurables.map {
//                it.measure(constraints.copy(minWidth = tabWidth, maxWidth = tabWidth))
//            }
//
//            val tabRowHeight = tabPlaceables.maxByOrNull { it.height }?.height ?: 0
//
//            val tabPositions = List(tabCount) { index ->
//                TabPosition(tabWidth.toDp() * index, tabWidth.toDp(), tabWidth.toDp())
//            }
//
//            layout(tabRowWidth, tabRowHeight) {
//                tabPlaceables.forEachIndexed { index, placeable ->
//                    placeable.placeRelative(index * tabWidth, 0)
//                }
//
//                subcompose(TabSlots.Divider, { } ).forEach {
//                    val placeable = it.measure(constraints.copy(minHeight = 0))
//                    placeable.placeRelative(0, tabRowHeight - placeable.height)
//                }
//
//                subcompose(TabSlots.Indicator) {
//                    indicator(tabPositions)
//                }.forEach {
//                    it.measure(Constraints.fixed(tabRowWidth, tabRowHeight)).placeRelative(0, 0)
//                }
//            }
//        }
//    }
//}
//
//private enum class TabSlots {
//    Tabs,
//    Divider,
//    Indicator
//}
