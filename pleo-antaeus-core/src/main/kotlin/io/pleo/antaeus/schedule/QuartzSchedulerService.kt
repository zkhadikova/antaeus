package io.pleo.antaeus.schedule

import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.InvoiceService
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory

class QuartzSchedulerService(
	val cronExpression: String,
	val invoiceService: InvoiceService,
	val billingService: BillingService
) {
	val trigger = TriggerBuilder
		.newTrigger()
		.withIdentity("antaeus-payment-trg", "antaeus-group")
		.withSchedule(
			CronScheduleBuilder.cronSchedule(cronExpression)
		)
		.build()

	val job = JobBuilder.newJob(PaymentJob::class.java)
		.withIdentity("antaeus-payment-job", "antaeus-group").build()

	val scheduler = StdSchedulerFactory().getScheduler()

	fun startScheduler(): Unit {
		scheduler.getContext().put("invoiceService", invoiceService)
		scheduler.getContext().put("billingService", billingService)

		scheduler.start()
		scheduler.scheduleJob(job, trigger)
	}

	fun stop(): Unit {
		scheduler.shutdown()
	}

}