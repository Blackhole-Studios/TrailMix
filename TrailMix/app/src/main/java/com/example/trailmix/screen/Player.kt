package com.example.trailmix.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PlayerScreen(
    expanded: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit
) {
    if (expanded) {
        Text("FULL PLAYER")
    } else {
        Text("COMPACT PLAYER")
    }
}