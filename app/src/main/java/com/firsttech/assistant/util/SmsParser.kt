package com.firsttech.assistant.util
data class ParsedSms(val type: String, val amount: Int? = null, val store: String? = null, val cardName: String? = null, val category: String? = null)
object SmsParser {
    fun parse(body: String): ParsedSms? {
        if (!body.contains("결제") && !body.contains("승인")) return null
        val amount = Regex("([\\d,]+)원").find(body)?.groupValues?.get(1)?.replace(",","")?.toIntOrNull()
        return ParsedSms(type="payment", amount=amount, store="", category="기타")
    }
}
