package com.unghostdude.budjet.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unghostdude.budjet.R
import com.unghostdude.budjet.viewmodel.OnboardingScreenViewModel

data class Onboarding(
    val header: String,
    val body: String,
    val image: String
)


@Composable
fun OnboardingScreen(
    vm: OnboardingScreenViewModel = hiltViewModel<OnboardingScreenViewModel>(),
    navigateToUsername: () -> Unit
) {
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

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val r = when (currentPageIndex) {
                0 -> R.drawable.tutorial_1
                1 -> R.drawable.tutorial_2
                else -> R.drawable.tutorial_3
            }

            Image(
                painter = painterResource(id = r),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .heightIn(max = 300.dp)
            )


            Text(
                text = currentOnboarding.header,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = currentOnboarding.body,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
            )
            Button(
                onClick = {
                    if (currentPageIndex == onboardings.size - 1) {
                        vm.completeSetup(navigateToUsername)
                    } else {
                        currentPageIndex++
                    }
                },
                modifier = Modifier
            ) {
                Text(
                    text = if (currentPageIndex == onboardings.size - 1) context.getString(R.string.done) else context.getString(
                        R.string.next
                    )
                )
            }
        }
    }
}