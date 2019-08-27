package io.pleo.antaeus.models

enum class ProcessingStatus {
    SUCCESS,
    NETWORK_ERROR,
	CUSTOMER_NOT_FOUND,
	CURRENCY_MISMATCH,
	UNKNOWN_ERROR,
	OVERDRAFT
}
