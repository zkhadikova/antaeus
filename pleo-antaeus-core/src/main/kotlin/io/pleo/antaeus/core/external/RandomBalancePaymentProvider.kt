package io.pleo.antaeus.core.external

import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import java.math.BigDecimal
import kotlin.random.Random

class RandomBalancePaymentProvider(val customerLookupService: CustomerLookupService) : PaymentProvider {
    
    override fun charge(invoice: Invoice): Boolean {
        val customer = customerLookupService.getCustomer(invoice.customerId)
        if(customer?.currency != invoice.amount.currency)
            throw CurrencyMismatchException(invoice.id, customer!!.id)
        if(getCustomerBalance(customer).compareTo(invoice.amount.value) >= 0)
            return true
        return false
    }
    
    //stub method to return random balance
    private fun getCustomerBalance(customer: Customer): BigDecimal {
        return BigDecimal(Random.nextDouble(0.0, 500.0))
    }
}
