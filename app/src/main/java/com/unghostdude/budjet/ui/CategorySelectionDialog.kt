package com.unghostdude.budjet.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.viewmodel.CategorySelectionDialogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionDialog(
    initialSelected: List<Int>,
    onSelected: (Int) -> Unit,
    onDeselected: (Int) -> Unit,
    onDismiss: () -> Unit,
    vm: CategorySelectionDialogViewModel = hiltViewModel()
) {
    val categories by vm.categories.collectAsState()
    val state = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = onDismiss
    ) {
        Text(
            text = "Choose Categories",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(items = categories) { category ->
                ListItem(
                    headlineContent = {
                        Text(text = category.name)
                    },
                    trailingContent = {
                        Checkbox(
                            checked = initialSelected.contains(category.id),
                            onCheckedChange = { isCheck ->
                                if (isCheck) {
                                    onSelected(category.id)
                                } else {
                                    onDeselected(category.id)
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (initialSelected.contains(category.id)) {
                                onDeselected(category.id)
                            } else {
                                onSelected(category.id)

                            }
                        }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

    }
}