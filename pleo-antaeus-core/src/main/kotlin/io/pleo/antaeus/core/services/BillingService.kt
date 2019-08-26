package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice
import mu.KotlinLogging

class BillingService(
	private val paymentProvider: PaymentProvider
) {
	private val logger = KotlinLogging.logger {}
	
	fun processInvoice(invoice: Invoice): Boolean {
		try {
			logger.trace { "Processing invoice $invoice" }
			return paymentProvider.charge(invoice)
		} catch (e: Exception) {
			return false
		}
	}
}