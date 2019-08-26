package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.ProcessingStatus
import io.pleo.antaeus.core.exceptions.*
import mu.KotlinLogging

class BillingService(
	private val paymentProvider: PaymentProvider,
	private val invoiceService: InvoiceService
) {
	private val logger = KotlinLogging.logger {}

	fun processInvoice(invoice: Invoice): Boolean {
		try {
			logger.trace { "Processing invoice $invoice" }
			return paymentProvider.charge(invoice)
		} catch (e: CurrencyMismatchException) {
			invoiceService.recordInvoiceTransaction(invoice, ProcessingStatus.CURRENCY_MISMATCH)
			return false
		} catch (e: CustomerNotFoundException) {
			invoiceService.recordInvoiceTransaction(invoice, ProcessingStatus.CUSTOMER_NOT_FOUND)
			return false
		} catch (e: NetworkException) {
			invoiceService.recordInvoiceTransaction(invoice, ProcessingStatus.NETWORK_ERROR)
			return false
		} catch (e: Exception) {
			logger.error(e) { "Unexpected exception" }
			return false
		}
	}

	fun processPayments(): Unit {
		val pendingInvoices = invoiceService.fetchPendingInvoices()
		pendingInvoices.forEach { invoice ->
			val paid = processInvoice(invoice)
			if (paid) {
				invoiceService.updateStatus(invoice.id, InvoiceStatus.PAID)
				invoiceService.recordInvoiceTransaction(invoice, ProcessingStatus.SUCCESS)
			}
		}
	}
}