package com.example

import com.example.data.model.TransactionCategory
import com.example.data.model.TransactionType
import com.example.data.parser.SahalSmsParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testParseSentMoneySMS() {
        val sms = "[SAHAL] Tix: 7265543152, $ 3 ayaad u dirtay MUKHTAAR CABDINUUR MAXAMED(5595018) Tar 12/08/26 17:33:34, Haraagaagu waa $63.8775."
        val result = SahalSmsParser.parse("898", sms)

        assertNotNull(result)
        assertEquals("7265543152", result?.transactionId)
        assertEquals(3.0, result?.amount ?: 0.0, 0.001)
        assertEquals(TransactionType.OUT.code, result?.type)
        assertEquals("MUKHTAAR CABDINUUR MAXAMED(5595018)", result?.recipientOrSource)
        assertEquals(63.8775, result?.balance ?: 0.0, 0.0001)
        assertEquals(TransactionCategory.TRANSFER_SENT.title, result?.category)
    }

    @Test
    fun testParseServicePaymentSMS() {
        val sms = "[SAHAL] $10 Ayaad ku bixisay adeega Waafi Card **** 9060"
        val result = SahalSmsParser.parse("898", sms)

        assertNotNull(result)
        assertEquals(10.0, result?.amount ?: 0.0, 0.001)
        assertEquals(TransactionType.OUT.code, result?.type)
        assertTrue(result?.recipientOrSource?.contains("Waafi Card", ignoreCase = true) == true)
        assertEquals(TransactionCategory.SERVICE_PAYMENT.title, result?.category)
    }

    @Test
    fun testParseBalanceUpdateSMS() {
        val sms = "AMAAN(7103610) Tar 12/08/26 12:51:44, Haraagaagu waa $76.8775."
        val result = SahalSmsParser.parse("898", sms)

        assertNotNull(result)
        assertEquals(TransactionType.BALANCE_UPDATE.code, result?.type)
        assertEquals(76.8775, result?.balance ?: 0.0, 0.0001)
        assertEquals("AMAAN(7103610)", result?.recipientOrSource)
        assertEquals(TransactionCategory.BALANCE_SYNC.title, result?.category)
    }

    @Test
    fun testIsSahalMessage() {
        assertTrue(SahalSmsParser.isSahalMessage("898", "Any body"))
        assertTrue(SahalSmsParser.isSahalMessage("SAHAL", "Any body"))
        assertTrue(SahalSmsParser.isSahalMessage("Unknown", "[SAHAL] Tix: 123..."))
    }

    @Test
    fun testRecipientAndServiceFiltering() {
        val tx1 = SahalSmsParser.parse("898", "[SAHAL] Tix: 7265543152, $ 3 ayaad u dirtay MUKHTAAR CABDINUUR MAXAMED(5595018) Tar 12/08/26 17:33:34, Haraagaagu waa $63.8775.")
        val tx2 = SahalSmsParser.parse("898", "[SAHAL] $10 Ayaad ku bixisay adeega Waafi Card **** 9060")
        val list = listOfNotNull(tx1, tx2)

        // Search by recipient name
        val nameQuery = "MUKHTAAR"
        val nameResults = list.filter { it.recipientOrSource.contains(nameQuery, ignoreCase = true) }
        assertEquals(1, nameResults.size)
        assertEquals("MUKHTAAR CABDINUUR MAXAMED(5595018)", nameResults[0].recipientOrSource)

        // Search by service provider
        val serviceQuery = "Waafi Card"
        val serviceResults = list.filter { it.recipientOrSource.contains(serviceQuery, ignoreCase = true) }
        assertEquals(1, serviceResults.size)
        assertTrue(serviceResults[0].recipientOrSource.contains("Waafi Card"))
    }

    @Test
    fun testMonthlySpendingComparisonCalculation() {
        val thisMonthSpending = 45.0
        val prevMonthSpending = 60.0
        val diff = thisMonthSpending - prevMonthSpending
        val pctChange = ((thisMonthSpending - prevMonthSpending) / prevMonthSpending) * 100.0

        assertEquals(-15.0, diff, 0.001)
        assertEquals(-25.0, pctChange, 0.001)
        assertTrue(diff <= 0) // spending decreased (positive financial trend)
    }
}
