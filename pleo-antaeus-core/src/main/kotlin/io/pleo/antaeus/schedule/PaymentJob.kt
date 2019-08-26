package io.pleo.antaeus.schedule

import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.models.InvoiceStatus
import mu.KotlinLogging
import org.quartz.Job
import org.quartz.JobExecutionContext

class PaymentJob : Job {
	private val logger = KotlinLogging.logger {}
	override fun execute(context: JobExecutionContext): Unit {
		logger.info { "Triggred payment processing" }
		
		val schedulerContext = context.getScheduler().getContext()
		val invoiceServiceParam = schedulerContext.get("invoiceService")
		val invoiceService = when (invoiceServiceParam) {
			is InvoiceService -> invoiceServiceParam
			else -> throw IllegalArgumentException("Invalid job param invoiceService")
		}
		
		val billingServiceParam = schedulerContext.get("billingService")
		val billingService = when (billingServiceParam) {
			is BillingService -> billingServiceParam
			else -> throw IllegalArgumentException("Invalid job param billingService")
		}
		
		val pendingInvoices = invoiceService.fetchPendingInvoices()
		pendingInvoices.forEach { invoice ->
			val paid = billingService.processInvoice(invoice)
			if (paid) {
				invoiceService.updateStatus(invoice.id, InvoiceStatus.PAID)
			}
		}
	}
}