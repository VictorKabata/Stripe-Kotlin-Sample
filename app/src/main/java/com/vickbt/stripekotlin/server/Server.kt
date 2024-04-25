package com.vickbt.stripekotlin.server

import com.stripe.Stripe
import com.stripe.model.Customer.create
import com.stripe.model.EphemeralKey
import com.stripe.model.PaymentIntent
import com.stripe.param.CustomerCreateParams
import com.stripe.param.EphemeralKeyCreateParams
import com.stripe.param.PaymentIntentCreateParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

object Server {

    /**Set up server configs that returns stripe payment info
     *
     * Do not run this code in the main thread. You can use runBlocking but pass the IO dispatcher to
     * avoid blocking the UI thread which throws an error
     *
     * You can return a custom data class of [StripePaymentInfo]
     * */
    fun serverConfig(): Map<String, String> = runBlocking(Dispatchers.IO) {

        // Pass your api key from: https://dashboard.stripe.com/apikeys
        Stripe.apiKey =
            "sk_test_51P9RJAEWsxWYTkMNTzm7C0lwcqdamjwbPhoU7tDENNv0ovHEVlHE1iW4wRGcbvY44peEygoPTGue6y5AwVaqr3J20085Nrr6LP"

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
            "publishableKey" to "pk_test_51P9RJAEWsxWYTkMNGGS6ne0uW9NAAST6oynLPv61RPA3qpohYYaPkY8gLDehMTIDVAVQzGk4rG3FlWutbjNePIiX005OZlY4en" // You can get his value from stripe tutorial
        )

        stripePaymentInfo
    }

}
