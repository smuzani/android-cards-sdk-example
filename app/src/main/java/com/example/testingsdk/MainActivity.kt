package com.example.testingsdk

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.cards.session.cards.sdk.CardSessions
import com.cards.session.cards.sdk.create
import com.example.testingsdk.ui.theme.TestingSDKTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var responseMessage by mutableStateOf("Response will appear here") // State for response message
    private var paymentSessionIdInput by mutableStateOf("") // State for input field

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestingSDKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        paymentSessionId = paymentSessionIdInput,
                        onPaymentSessionIdChange = { paymentSessionIdInput = it },
                        responseMessage = responseMessage,
                        onButtonClick = { test(paymentSessionIdInput) }, // Pass current input value
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun test(paymentSessionId: String) {
        responseMessage = "Running test..."
        Log.d("MainActivity", "Using Payment Session ID: $paymentSessionId")
        val publicKey = "xnd_public_development_1TFSZ1eyExAAhfR48LRlbTIH2WFT6*********uNghjCMJakP0hq0ZCV"

        val cardSessions = CardSessions.create(
            context = this,
            apiKey = publicKey
        )
        lifecycleScope.launch {
            try {
                val response = cardSessions.collectCardData(
                    cardNumber = "4000000000001091",
                    expiryMonth = "12",
                    expiryYear = "2040",
                    cvn = "123",
                    cardholderFirstName = "John",
                    cardholderLastName = "Doe",
                    cardholderEmail = "1234@qq.com",
                    cardholderPhoneNumber = "+621234567890",
                    paymentSessionId = paymentSessionId,
                )
                Log.d("MainActivity", "Xendit Response: $response")
                // Update state with the message from the response
                responseMessage = response.message ?: "Request successful, but no message received."
            } catch (e: Exception) {
                 Log.e("MainActivity", "Error during card session: ${e.message}", e)
                 responseMessage = "Error: ${e.message ?: "Unknown error occurred"}"
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    paymentSessionId: String, // State for TextField value
    onPaymentSessionIdChange: (String) -> Unit, // Callback to update state
    responseMessage: String, // State for response display
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello $name!"
        )
        Spacer(modifier = Modifier.height(16.dp)) // Add some space

        TextField(
            value = paymentSessionId,
            onValueChange = onPaymentSessionIdChange,
            label = { Text("Payment Session ID") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onButtonClick) {
            Text("Run Test Function")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = responseMessage // Display the response message
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestingSDKTheme {
        // Need to handle state within the preview or pass static values
        var previewPaymentSessionId by remember { mutableStateOf("ps-preview-id") }
        var previewResponseMessage by remember { mutableStateOf("Preview Response") }
        Greeting(
            name = "Android",
            paymentSessionId = previewPaymentSessionId,
            onPaymentSessionIdChange = { previewPaymentSessionId = it },
            responseMessage = previewResponseMessage,
            onButtonClick = { previewResponseMessage = "Preview Button Clicked" } // Dummy action for preview
        )
    }
}