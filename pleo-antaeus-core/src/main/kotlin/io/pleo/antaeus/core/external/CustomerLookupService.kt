package io.pleo.antaeus.core.external

import java.net.URL

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import khttp.get
import org.json.JSONObject

import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException

interface CustomerLookupService {
	fun getCustomer(id: Int): Customer?
}

object LocalCustomerLookupService : CustomerLookupService {
	val baseUrl = "http://localhost:7000/rest/v1"

	val mapper = jacksonObjectMapper()

	override fun getCustomer(id: Int): Customer? {
		val response = try {
			get(baseUrl + "/customers/" + id)
		} catch (e: Exception) {
			throw NetworkException()
		}
		if (response.statusCode == 200)
			return jsonToCustomer(response.text)
		if (response.statusCode == 404)
			throw CustomerNotFoundException(id)
		return null
	}

	private fun jsonToCustomer(json: String): Customer? {
		return mapper.readValue(json, Customer::class.java)
	}
}
