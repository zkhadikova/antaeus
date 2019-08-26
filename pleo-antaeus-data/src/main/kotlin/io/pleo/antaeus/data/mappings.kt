/*
    Defines mappings between database rows and Kotlin objects.
    To be used by `AntaeusDal`.
 */

package io.pleo.antaeus.data

import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.InvoiceTransaction
import io.pleo.antaeus.models.Money
import io.pleo.antaeus.models.ProcessingStatus
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toInvoice(): Invoice = Invoice(
    id = this[InvoiceTable.id],
    amount = Money(
        value = this[InvoiceTable.value],
        currency = Currency.valueOf(this[InvoiceTable.currency])
    ),
    status = InvoiceStatus.valueOf(this[InvoiceTable.status]),
    customerId = this[InvoiceTable.customerId]
)

fun ResultRow.toCustomer(): Customer = Customer(
    id = this[CustomerTable.id],
    currency = Currency.valueOf(this[CustomerTable.currency])
)

fun ResultRow.toInvoiceTransaction(): InvoiceTransaction = InvoiceTransaction(
    id = this[InvoiceTransactionTable.id],
	invoiceId = this[InvoiceTransactionTable.invoiceId],
    status = ProcessingStatus.valueOf(this[InvoiceTransactionTable.status]),
    transactionDate = this[InvoiceTransactionTable.transactionDate]
)
