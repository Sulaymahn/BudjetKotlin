package com.unghostdude.budjet.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.viewmodel.UsernameSetupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsernameSetupScreen(
    navigateToHome: () -> Unit,
    vm: UsernameSetupViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = "Username")
                },
                actions = {
                    IconButton(
                        enabled = vm.canSetUsername(),
                        onClick = {
                            vm.setUsername(
                                onComplete = navigateToHome
                            )
                        }) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = null)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            OutlinedTextField(
                value = vm.username.currentValue,
                onValueChange = { newValue ->
                    vm.username.setValue(newValue)
                },
                label = {
                    Text(text = "Username")
                },
                isError = !vm.username.isValid,
                supportingText = {
                    if (vm.username.errors.isNotEmpty()) {
                        Text(text = vm.username.errors.first())
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
            )
        }
    }

}