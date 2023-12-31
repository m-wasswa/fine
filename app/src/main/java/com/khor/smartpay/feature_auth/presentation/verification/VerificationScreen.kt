package com.khor.smartpay.feature_auth.presentation.verification

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.PhoneAuthProvider
import com.khor.smartpay.core.data.prefdatastore.UserStore
import com.khor.smartpay.core.util.Screen
import com.khor.smartpay.feature_auth.presentation.verification.components.CodeInputField
import com.khor.smartpay.feature_auth.presentation.verification.components.DescriptionMessage
import com.khor.smartpay.feature_auth.presentation.verification.components.ResendTextButton
import com.khor.smartpay.feature_auth.presentation.verification.components.VerificationTitle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun VerificationScreen(
    navController: NavController,
    viewModel: VerificationViewModel = hiltViewModel(),
    verificationId: String,
    phoneNumber: String
) {
    val localContext = LocalContext.current
    val store = UserStore(localContext)

    var openDialog by remember { mutableStateOf(false) }
    var showProgressIndicator by remember { mutableStateOf(false) }
    var openDialogMessage by remember { mutableStateOf("") }
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is VerificationViewModel.UiEvent.ShowAlertDialog -> {
                    openDialog = event.showDialog
                    openDialogMessage = event.message
                    showProgressIndicator = false
                }

                is VerificationViewModel.UiEvent.ShowProgressIndicator -> {
                    showProgressIndicator = true
                }

                is VerificationViewModel.UiEvent.NavigateToMainScreen -> {
                    navController.navigate(Screen.InternalScreen.route)
                    store.saveToken(true)
                }
            }
        }
    }

    if (showProgressIndicator) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .height(60.dp)
                    .width(60.dp)
            )
        }

    }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            title = { Text(text = "Error") },
            text = { Text(openDialogMessage, fontSize = 18.sp) },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                    }) {
                    Text("Close")
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        VerificationTitle(modifier = Modifier.padding(top = 60.dp))
        DescriptionMessage(
            modifier = Modifier.padding(24.dp),
            phoneNumber = phoneNumber
        )
        CodeInputField(modifier = Modifier.padding(28.dp), viewModel) {
            viewModel.onEvent(VerificationEvent.OnCodeResultChange(it))
            if (it.length == 6) {
                viewModel.signInWithCredentials(
                    phoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationId, it
                    ),
                    activity = localContext as Activity
                )
            }
        }
        ResendTextButton(modifier = Modifier.padding(32.dp)) {
            viewModel.resendVerificationCode(
                number = phoneNumber,
                activity = localContext as Activity
            )
        }
    }
}