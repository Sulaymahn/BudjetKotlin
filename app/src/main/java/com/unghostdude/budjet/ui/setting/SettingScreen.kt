package com.unghostdude.budjet.ui.setting

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.BuildConfig
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.AppTheme
import com.unghostdude.budjet.viewmodel.SettingScreenViewModel

enum class SettingsScreenDialog {
    None,
    Username,
    Theme
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navigateAway: () -> Unit,
    vm: SettingScreenViewModel = hiltViewModel<SettingScreenViewModel>()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val settings by vm.settings.collectAsState()

    var currentDialog by remember {
        mutableStateOf(SettingsScreenDialog.None)
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
                    Text(text = "Username")
                },
                supportingContent = {
                    Text(text = settings.username)
                },
                leadingContent = {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null)
                },
                trailingContent = {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                },
                modifier = Modifier
                    .clickable {
                        currentDialog = SettingsScreenDialog.Username
                    }
            )

            ListItem(
                headlineContent = {
                    Text(text = "Theme")
                },
                supportingContent = {
                    Text(text = settings.theme.name)
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_format_paint_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .clickable {
                        currentDialog = SettingsScreenDialog.Theme
                    }
            )

            ListItem(
                headlineContent = {
                    Text(text = "Export to CSV")
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_export_notes_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .clickable {
                        vm.exportToCsv(
                            baseDirectory = context.filesDir.absolutePath,
                            callback = { path, file ->
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    BuildConfig.APPLICATION_ID + ".fileprovider",
                                    file
                                )
                                val intent = Intent(Intent.ACTION_SEND)
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                intent.setType("text/csv")
                                intent.putExtra(Intent.EXTRA_STREAM, uri)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(
                                    Intent.createChooser(
                                        intent,
                                        "Share file"
                                    )
                                )
                            }
                        )
                    }
            )

            HorizontalDivider()

            ListItem(
                headlineContent = {
                    Text(text = "Send feedback")
                },
                supportingContent = {
                    Text(text = "sulaymahn28@gmail.com")
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_send_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .clickable {
                        val intent = Intent(Intent.ACTION_SENDTO)
                        intent.data = Uri.parse("mailto:sulaymahn28@gmail.com")
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Budjet App - Feedback")
                        try {
                            context.startActivity(
                                Intent.createChooser(
                                    intent,
                                    "Send feedback"
                                )
                            )
                        } catch (_: Exception) {

                        }
                    }
            )

            ListItem(
                headlineContent = {
                    Text(text = "About")
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_info_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .clickable {
                    }
            )

            if (currentDialog == SettingsScreenDialog.Theme) {
                BasicAlertDialog(
                    onDismissRequest = {
                        currentDialog = SettingsScreenDialog.None
                    }
                ) {
                    Card {
                        Column {
                            ListItem(
                                headlineContent = {
                                    Text(text = "Theme")
                                }
                            )

                            repeat(AppTheme.entries.size) { index ->
                                val theme = AppTheme.entries[index]
                                ListItem(
                                    headlineContent = {
                                        Text(text = theme.name)
                                    },
                                    trailingContent = {
                                        RadioButton(
                                            selected = settings.theme == theme,
                                            onClick = {
                                                vm.changeTheme(theme)
                                                currentDialog = SettingsScreenDialog.None
                                            }
                                        )
                                    },
                                    modifier = Modifier
                                        .clickable {
                                            vm.changeTheme(theme)
                                            currentDialog = SettingsScreenDialog.None
                                        }
                                )
                            }
                        }
                    }
                }
            } else if (currentDialog == SettingsScreenDialog.Username) {
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
                    onDismissRequest = { currentDialog = SettingsScreenDialog.None }
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(20.dp)
                    ) {

                        OutlinedTextField(
                            value = username,
                            onValueChange = { newValue ->
                                username = newValue
                            },
                            label = {
                                Text(text = "Username")
                            },
                            isError = username.text.isBlank(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
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
                            OutlinedButton(onClick = {
                                currentDialog = SettingsScreenDialog.None
                            }) {
                                Text(text = "Cancel")
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Button(
                                enabled = username.text.isNotBlank(),
                                onClick = {
                                    vm.updateUsername(
                                        username = username.text.trim(),
                                        callback = { currentDialog = SettingsScreenDialog.None }
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