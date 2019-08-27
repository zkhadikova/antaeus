/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.ProcessingStatus

class InvoiceService(private val dal: AntaeusDal) {
    fun fetchAll(): List<Invoice> {
       return dal.fetchInvoices()
    }

    fun fetch(id: Int): Invoice {
        return dal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id)
    }

    fun fetchPendingInvoices(): List<Invoice> {
        return dal.fetchInvoicesByStatus(InvoiceStatus.PENDING)
    }

    fun updatePaidInvoice(id: Int): Unit {
        dal.updateInvoiceStatus(id, InvoiceStatus.PAID)
    }

    fun recordInvoiceTransaction(invoiceId: Int, status: ProcessingStatus): Unit {
        dal.createInvoiceTransaction(invoiceId, status)
    }
}
