package com.vickbt.stripekotlin.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import com.vickbt.stripekotlin.server.ServerConfigs
import com.vickbt.stripekotlin.ui.theme.StripeKotlinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StripeKotlinTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val paymentSheet = rememberPaymentSheet(::onPaymentSheetResult)

    val context = LocalContext.current
    var customerConfig by remember { mutableStateOf<PaymentSheet.CustomerConfiguration?>(null) }
    var paymentIntentClientSecret by remember { mutableStateOf<String?>(null) }

    // Get server configs
    val serverConfig = ServerConfigs.serverConfig()

    LaunchedEffect(context) {
        paymentIntentClientSecret = serverConfig["paymentIntent"]
        customerConfig = PaymentSheet.CustomerConfiguration(
            serverConfig["customer"] ?: "",
            serverConfig["ephemeralKey"] ?: ""
        )

        val publishableKey = serverConfig["publishableKey"]
        PaymentConfiguration.init(context, publishableKey ?: "")
    }

    Box(modifier = modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = {
                val currentConfig = customerConfig
                val currentClientSecret = paymentIntentClientSecret

                if (currentConfig != null && currentClientSecret != null) {
                    presentPaymentSheet(paymentSheet, currentConfig, currentClientSecret)
                }
            }) {
            Text("PAY")
        }
    }
}

// This is from the Stripe documentation
/**This displays the Stripe bottom sheet on button click*/
private fun presentPaymentSheet(
    paymentSheet: PaymentSheet,
    customerConfig: PaymentSheet.CustomerConfiguration,
    paymentIntentClientSecret: String
) {
    paymentSheet.presentWithPaymentIntent(
        paymentIntentClientSecret,
        PaymentSheet.Configuration(
            merchantDisplayName = "My merchant name",
            customer = customerConfig,
            allowsDelayedPaymentMethods = true
        )
    )
}

// This is from the Stripe documentation
/**Fetch the result from payment operation as cancelled, failed and completed*/
private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
    when (paymentSheetResult) {
        is PaymentSheetResult.Canceled -> {
            Log.e("Stripe Log", "Cancelled")
        }

        is PaymentSheetResult.Failed -> {
            Log.e("Stripe Log", "Failed")
        }

        is PaymentSheetResult.Completed -> {
            Log.e("Stripe Log", "Completed")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    StripeKotlinTheme {
        MainScreen()
    }
}