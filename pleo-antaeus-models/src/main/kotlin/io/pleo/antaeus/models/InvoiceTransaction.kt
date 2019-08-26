package io.pleo.antaeus.models
import org.joda.time.DateTime

data class InvoiceTransaction (
    val id: Int,
    val invoiceId: Int,
    val status: ProcessingStatus,
    val transactionDate: DateTime
)
