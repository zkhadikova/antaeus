package io.pleo.antaeus.schedule

import io.pleo.antaeus.core.services.BillingService
import mu.KotlinLogging
import org.quartz.Job
import org.quartz.JobExecutionContext

class PaymentJob : Job {
    private val logger = KotlinLogging.logger {}
    override fun execute(context: JobExecutionContext): Unit {
        logger.info { "Triggred payment processing" }

        val schedulerContext = context.getScheduler().getContext()

        val billingServiceParam = schedulerContext.get("billingService")

        val billingService = when (billingServiceParam) {
            is BillingService -> billingServiceParam
            else -> throw IllegalArgumentException("Invalid job param billingService")
        }
        billingService.processPayments()
    }
}
