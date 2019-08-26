package io.pleo.antaeus.core.external

import java.net.URL

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import khttp.get
import org.json.JSONObject

import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import mu.KotlinLogging
import java.io.BufferedReader

interface CustomerLookupService {
	fun getCustomer(id: Int): Customer?
}

object LocalCustomerLookupService : CustomerLookupService {

	private val logger = KotlinLogging.logger {}

	val baseUrl = "http://localhost:7000/rest/v1"

	val mapper = jacksonObjectMapper()

	override fun getCustomer(id: Int): Customer? {
		val response = try {
			get(baseUrl + "/customers/" + id)
		} catch (e: Exception) {
			logger.warn(e) { "Exception while getting customer by id" }
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
