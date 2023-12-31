package com.khor.smartpay.feature_cards.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.khor.smartpay.R
import com.khor.smartpay.core.presentation.components.StandardToolbar
import com.khor.smartpay.feature_auth.presentation.verification.VerificationViewModel
import com.khor.smartpay.feature_cards.presentation.components.QrCodeCard
import kotlinx.coroutines.flow.filterNotNull

@OptIn(ExperimentalPagerApi::class)
@SuppressLint(
    "ResourceType", "CoroutineCreationDuringComposition",
    "FlowOperatorInvokedInComposition"
)
@Composable
fun CardsScreen() {
    val viewModel: CardsViewModel = hiltViewModel()
    val cards = viewModel.state.value.cards
    val pageCount = cards.size
    val pagerState = rememberPagerState(pageCount = pageCount)

    StandardToolbar(title = { Text("Cards") }, navActions = {
        IconButton(
            onClick = {
                viewModel.startScan()
            }, modifier = Modifier
        ) {
            Icon(
                modifier = Modifier
                    .size(28.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface, CircleShape).alpha(0.8f),
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add Card"
            )
        }
    })

    if (pageCount > 0) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration)
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(20.dp)
                    )
                }
            }

            HorizontalPager(
                state = pagerState
            ) { page ->
                // Our page content
                QrCodeCard(
                    qrCode = cards[page].qrCode,
                    isFrozen = cards[page].isFrozen,
                    limit = cards[page].limit,
                    viewModel = viewModel
                )
            }
        }
    } else {
        // Handle the empty state (show a placeholder, a message, or another content)
        // For example:
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No cards available.", modifier = Modifier.offset(y = (-50).dp))
        }
    }

}