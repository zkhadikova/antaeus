package io.pleo.antaeus.core.services

import io.mockk.*
import io.pleo.antaeus.core.exceptions.*
import io.pleo.antaeus.core.external.CustomerLookupService
import io.pleo.antaeus.core.external.RandomBalancePaymentProvider
import io.pleo.antaeus.models.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.random.Random

class RandomBalancePaymentTest {
	private val customerLookup = mockk<CustomerLookupService> {
		every { getCustomer(1) } returns Customer(1, Currency.USD)
		every { getCustomer(404) } throws (CustomerNotFoundException(404))
		every { getCustomer(5) } throws (NetworkException())
	}

	private val paymentProvider = RandomBalancePaymentProvider(customerLookup)

	init {
		mockkObject(Random)
		every { Random.nextDouble(0.0, 500.0) } returns 100.0
	}

	@Test
	fun `will throw currency mismatch`() {
		assertThrows<CurrencyMismatchException> {
			val invoice = Invoice(1, 1, Money(BigDecimal(10), Currency.EUR), InvoiceStatus.PENDING)
			paymentProvider.charge(invoice)
		}
	}

	@Test
	fun `will throw customer not found`() {
		assertThrows<CustomerNotFoundException> {
			val invoice = Invoice(1, 404, Money(BigDecimal(10), Currency.EUR), InvoiceStatus.PENDING)
			paymentProvider.charge(invoice)
		}
	}

	@Test
	fun `will throw network exception`() {
		assertThrows<NetworkException> {
			val invoice = Invoice(1, 5, Money(BigDecimal(10), Currency.EUR), InvoiceStatus.PENDING)
			paymentProvider.charge(invoice)
		}
	}

	@Test
	fun `will return true when amount is equal to balance`() {
		val invoice = Invoice(1, 1, Money(BigDecimal(100), Currency.USD), InvoiceStatus.PENDING)
		assert(paymentProvider.charge(invoice))
	}

	@Test
	fun `will return true when amount is less than balance`() {
		val invoice = Invoice(1, 1, Money(BigDecimal(10), Currency.USD), InvoiceStatus.PENDING)
		assert(paymentProvider.charge(invoice))
	}

	@Test
	fun `will return false when amount exceeds balance`() {
		val invoice = Invoice(1, 1, Money(BigDecimal(1000), Currency.USD), InvoiceStatus.PENDING)
		assert(!paymentProvider.charge(invoice))
	}
}
