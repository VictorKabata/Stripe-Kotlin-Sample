# Stripe Kotlin Sample
 Barebone kotlin app demonstratng integrating Stripe into an android app

## Server Configs

```kotlin
// Add this to build.gradle.kts
implementation(libs.stripe.java)


object ServerConfigs {

    /**Set up server configs that returns stripe payment info
     *
     * Do not run this code in the main thread. You can use runBlocking but pass the IO dispatcher to
     * avoid blocking the UI thread which throws an error
     *
     * You can return a custom data class of [StripePaymentInfo]
     * */
    fun serverConfig(): Map<String, String> = runBlocking(Dispatchers.IO) {

        // Pass your api key from: https://dashboard.stripe.com/apikeys
        Stripe.apiKey = "your API key"

        // Create customer object
        val customerParams = CustomerCreateParams.builder().build()
        val customer = create(customerParams)

        // Create ephemeralKey object
        val ephemeralKeyParams = EphemeralKeyCreateParams.builder()
            .setStripeVersion("2024-04-10")
            .setCustomer(customer.id)
            .build()
        val ephemeralKey: EphemeralKey = EphemeralKey.create(ephemeralKeyParams)

        // Create payment intent object
        val paymentIntentParams = PaymentIntentCreateParams.builder()
            .setAmount(1099L) // You can pass amount as a parameter
            .setCurrency("usd") // Set valid currency eg. usd, euro
            .setCustomer(customer.id)
            .build()
        val paymentIntent = PaymentIntent.create(paymentIntentParams)

        // Stripe payment info. You can pass this as a custom data class
        val stripePaymentInfo = mapOf(
            "paymentIntent" to paymentIntent.clientSecret,
            "ephemeralKey" to ephemeralKey.secret,
            "customer" to customer.id,
            "publishableKey" to "your publish key" // You can get his value from stripe tutorial
        )

        stripePaymentInfo
    }

}
```
