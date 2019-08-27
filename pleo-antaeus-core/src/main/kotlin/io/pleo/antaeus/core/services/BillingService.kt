package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.ProcessingStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging


class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService
) {
    private val logger = KotlinLogging.logger {}

    fun processInvoice(invoice: Invoice): Pair<Int, ProcessingStatus> {
        val status = try {
            logger.info { "Processing invoice $invoice" }
            val paid = paymentProvider.charge(invoice)
            if (paid) {
                ProcessingStatus.SUCCESS
            } else ProcessingStatus.OVERDRAFT
        } catch (e: CurrencyMismatchException) {
            ProcessingStatus.CURRENCY_MISMATCH
        } catch (e: CustomerNotFoundException) {
            ProcessingStatus.CUSTOMER_NOT_FOUND
        } catch (e: NetworkException) {
            ProcessingStatus.NETWORK_ERROR
        } catch (e: Exception) {
            logger.error(e) { "Unexpected exception" }
            ProcessingStatus.UNKNOWN_ERROR
        }
        return Pair(invoice.id, status)
    }

    fun processPayments(): Unit {
        val context = newFixedThreadPoolContext(5, "antaeus-payment-system")

        val pendingInvoices = invoiceService.fetchPendingInvoices()
        val deferred = pendingInvoices.map { invoice ->
            CoroutineScope(context).async {
                processInvoice(invoice)
            }
        }
        runBlocking {
            deferred.forEach {
                it.await()
                val (id, status) = it.getCompleted()
                invoiceService.recordInvoiceTransaction(id, status)
                if (status == ProcessingStatus.SUCCESS) {
                    invoiceService.updatePaidInvoice(id)
                }
            }
        }
    }
}