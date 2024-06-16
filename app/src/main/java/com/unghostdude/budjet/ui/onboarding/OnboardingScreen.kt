package com.unghostdude.budjet.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unghostdude.budjet.R

data class Onboarding(
    val header: String,
    val body: String,
    val image: String
)


@Composable
fun OnboardingScreen(navigateToHome: () -> Unit) {
    var currentPageIndex by remember {
        mutableIntStateOf(0)
    }

    val context = LocalContext.current

    val onboardings: List<Onboarding> = listOf(
        Onboarding(
            context.getString(R.string.tutorial_one_title),
            context.getString(R.string.tutorial_one_body),
            ""
        ),
        Onboarding(
            context.getString(R.string.tutorial_two_title),
            context.getString(R.string.tutorial_two_body),
            ""
        ),
        Onboarding(
            context.getString(R.string.tutorial_three_title),
            context.getString(R.string.tutorial_three_body),
            ""
        )
    )

    val currentOnboarding = onboardings[currentPageIndex]

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = currentOnboarding.header,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = currentOnboarding.body,
                style = MaterialTheme.typography.bodyLarge
            )
            Button(
                onClick = {
                    if (currentPageIndex == onboardings.size - 1) {
                        navigateToHome()
                    } else {
                        currentPageIndex++
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = if (currentPageIndex == onboardings.size - 1) context.getString(R.string.done) else context.getString(R.string.next))
            }
        }
    }
}