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

    fun createServer(): Map<String, String> = runBlocking(Dispatchers.IO) {

        Stripe.apiKey =
            "sk_test_51P9RJAEWsxWYTkMNTzm7C0lwcqdamjwbPhoU7tDENNv0ovHEVlHE1iW4wRGcbvY44peEygoPTGue6y5AwVaqr3J20085Nrr6LP"

        val customerParams = CustomerCreateParams.builder().build()
        val customer = create(customerParams)

        val ephemeralKeyParams = EphemeralKeyCreateParams.builder()
            .setStripeVersion("2024-04-10")
            .setCustomer(customer.id)
            .build()
        val ephemeralKey: EphemeralKey = EphemeralKey.create(ephemeralKeyParams)

        val paymentIntentParams = PaymentIntentCreateParams.builder()
            .setAmount(1099L)
            .setCurrency("usd") // Set valid currency eg. usd, euro
            .setCustomer(customer.id)
            .build()
        val paymentIntent = PaymentIntent.create(paymentIntentParams)

        val stripePaymentInfo = mapOf(
            "paymentIntent" to paymentIntent.clientSecret,
            "ephemeralKey" to ephemeralKey.secret,
            "customer" to customer.id,
            "publishableKey" to "pk_test_51P9RJAEWsxWYTkMNGGS6ne0uW9NAAST6oynLPv61RPA3qpohYYaPkY8gLDehMTIDVAVQzGk4rG3FlWutbjNePIiX005OZlY4en"
        )

        stripePaymentInfo
    }

}
