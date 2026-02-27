package com.example.fyp.screens.startup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fyp.R
import kotlinx.coroutines.delay

private val BgColor  = Color(0xFFFAFAFA)
private val NavyBlue = Color(0xFF1565C0)
private val LightBlue = Color(0xFF42A5F5)
private val SkyBlue  = Color(0xFF29B6F6)
private val Gold     = Color(0xFFFFD700)

/**
 * Compose startup / splash screen shown immediately after the OS splash screen.
 * Replicates the full design (text, accent elements, decorations) that the
 * Android SplashScreen API cannot display on its own.
 * Automatically navigates away after [DISPLAY_DURATION_MS] ms.
 */
@Composable
fun StartupScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(DISPLAY_DURATION_MS)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // ── Top navy accent bar ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(NavyBlue)
                .align(Alignment.TopStart)
        )

        // ── Subtle top-left decoration circle ─────────────────────────────
        Box(
            modifier = Modifier
                .padding(start = 40.dp, top = 100.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(SkyBlue.copy(alpha = 0.2f))
                .align(Alignment.TopStart)
        )

        // ── Gold sparkle diamond near the logo ────────────────────────────
        Box(
            modifier = Modifier
                .padding(end = 104.dp, top = 192.dp)
                .size(10.dp)
                .rotate(45f)
                .background(Gold)
                .align(Alignment.TopEnd)
        )

        // ── Large faint decoration circle – bottom-right ──────────────────
        Box(
            modifier = Modifier
                .padding(end = 0.dp, bottom = 80.dp)
                .offset(x = 50.dp)
                .size(100.dp)
                .clip(CircleShape)
                .background(NavyBlue.copy(alpha = 0.05f))
                .align(Alignment.BottomEnd)
        )

        // ── Main content: circle logo + text ──────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Navy circle with speech bubbles (ic_splash_logo vector)
            Icon(
                painter = painterResource(id = R.drawable.ic_splash_logo),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Translator",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = NavyBlue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Translate & Learn",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = LightBlue,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))
        }

        // ── Bottom: three accent dots + short navy bar ────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 56.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(LightBlue))
                Box(Modifier.size(8.dp).clip(CircleShape).background(NavyBlue))
                Box(Modifier.size(8.dp).clip(CircleShape).background(SkyBlue))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(NavyBlue)
            )
        }
    }
}

private const val DISPLAY_DURATION_MS = 1800L
