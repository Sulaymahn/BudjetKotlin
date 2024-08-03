package com.unghostdude.budjet.ui.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.model.AccountEntity
import com.unghostdude.budjet.viewmodel.AccountCreationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountCreationScreen(
    account: AccountEntity,
    navigateAway: () -> Unit,
    vm: AccountCreationViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = "Create Account")
                },
                navigationIcon = {
                    IconButton(onClick = navigateAway) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_done),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
        ) {

        }
    }
}