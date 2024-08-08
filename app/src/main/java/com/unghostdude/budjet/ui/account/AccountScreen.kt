package com.unghostdude.budjet.ui.account

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.viewmodel.AccountScreenViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AccountScreen(
    navigateAway: () -> Unit,
    navigateToAccountCreation: () -> Unit,
    vm: AccountScreenViewModel = hiltViewModel()
) {
    val accounts by vm.accounts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Accounts")
                },
                navigationIcon = {
                    IconButton(onClick = { navigateAway() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = navigateToAccountCreation) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
        ) {
            items(items = accounts){ account ->
                ListItem(
                    headlineContent = {
                        Text(text = account.name)
                    },
                    supportingContent = {
                        Text(text = "${account.balance + account.startAmount}")
                    },
                    modifier = Modifier
                        .combinedClickable(
                            onClick = {

                            }
                        )
                )
            }
        }
    }
}