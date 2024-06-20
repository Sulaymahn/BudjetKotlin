package com.unghostdude.budjet.ui.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.viewmodel.SettingScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navigateAway: () -> Unit,
    vm: SettingScreenViewModel = hiltViewModel<SettingScreenViewModel>()
) {
    val settings by vm.settings.collectAsState()
    val focusManager = LocalFocusManager.current
    var showUsernameModal by remember {
        mutableStateOf(false)
    }

    val usernameFocusRequester = remember {
        FocusRequester()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Settings")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateAway()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            ListItem(
                headlineContent = {
                    Text(text = settings.username)
                },
                overlineContent = {
                    Text(text = "Username")
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null)
                },
                trailingContent = {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                },
                modifier = Modifier
                    .clickable {
                        showUsernameModal = true
                    }
            )


            if (showUsernameModal) {
                var username by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = settings.username,
                            selection = TextRange(
                                settings.username.length,
                                settings.username.length
                            )
                        )
                    )
                }

                ModalBottomSheet(
                    dragHandle = null,
                    onDismissRequest = { showUsernameModal = false }
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(20.dp)
                    ) {
                        Text(text = "Username")
                        TextField(
                            value = username,
                            onValueChange = { newValue ->
                                username = newValue
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            colors = TextFieldDefaults.colors().copy(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(usernameFocusRequester)
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            OutlinedButton(onClick = { showUsernameModal = false }) {
                                Text(text = "Cancel")
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Button(onClick = {
                                vm.updateUsername(
                                    username = username.text.trim(),
                                    callback = { showUsernameModal = false }
                                )
                            }) {
                                Text(text = "Okay")
                            }
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    usernameFocusRequester.requestFocus()
                }
            }
        }
    }
}