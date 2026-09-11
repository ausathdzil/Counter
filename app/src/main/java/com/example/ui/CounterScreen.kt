package com.example.ui

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(
    viewModel: CounterViewModel,
    uiState: CounterUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun triggerHaptic() {
        if (!uiState.hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(25)
                }
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    // Haptic toggle icon button
                    IconButton(
                        onClick = {
                            viewModel.toggleHaptics()
                            triggerHaptic()
                        },
                        modifier = Modifier.testTag("haptics_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.hapticsEnabled) Icons.Filled.Vibration else Icons.Outlined.Vibration,
                            contentDescription = stringResource(R.string.haptics_toggle),
                            tint = if (uiState.hapticsEnabled) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            }
                        )
                    }

                    // Reset icon button (instant reset, no dialog/undo)
                    IconButton(
                        onClick = {
                            triggerHaptic()
                            viewModel.reset()
                        },
                        enabled = uiState.count > 0,
                        modifier = Modifier.testTag("reset_button"),
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.reset),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        // We apply equal padding to top and bottom to ensure the content is perfectly 
        // centered on the screen, compensating for the height of the TopAppBar.
        val verticalPadding = innerPadding.calculateTopPadding()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = verticalPadding, bottom = verticalPadding)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    triggerHaptic()
                    viewModel.increment()
                }
                .testTag("counter_tap_area"),
            contentAlignment = Alignment.Center
        ) {
            val countText = uiState.count.toString()
            Text(
                text = countText,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = when {
                        countText.length > 7 -> 72.sp
                        countText.length > 4 -> 96.sp
                        else -> 128.sp
                    },
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-2).sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("counter_display")
            )
        }
    }
}
