package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.data.db.AppDatabase
import com.example.data.parser.SahalSmsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Background BroadcastReceiver that automatically intercepts incoming SMS
 * messages from Golis Telecom's SAHAL service (Shortcode 898), parses financial
 * transactions using Regex, and writes structured records into the local Room DB.
 */
class SMSReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "SahalSMSReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            return
        }

        val messages = try {
            Telephony.Sms.Intents.getMessagesFromIntent(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read SMS messages from intent", e)
            return
        }

        if (messages.isNullOrEmpty()) {
            return
        }

        // Group message parts by originating address (in case of multi-part SMS)
        val messagesBySender = messages.groupBy { it.originatingAddress ?: "Unknown" }

        for ((sender, smsList) in messagesBySender) {
            val fullBody = smsList.joinToString(separator = "") { it.messageBody ?: "" }
            val timestamp = smsList.firstOrNull()?.timestampMillis ?: System.currentTimeMillis()

            if (SahalSmsParser.isSahalMessage(sender, fullBody)) {
                Log.d(TAG, "Received Sahal SMS from $sender: $fullBody")

                // Handle async persistence safely without blocking the BroadcastReceiver thread
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val parsedTransaction = SahalSmsParser.parse(
                            sender = sender,
                            messageBody = fullBody,
                            receivedTimestamp = timestamp
                        )

                        if (parsedTransaction != null) {
                            val db = AppDatabase.getDatabase(context)
                            val dao = db.transactionDao()

                            // Check duplicate Tix if present
                            if (parsedTransaction.transactionId != null) {
                                val existing = dao.getTransactionByTix(parsedTransaction.transactionId)
                                if (existing == null) {
                                    dao.insertTransaction(parsedTransaction)
                                    Log.i(TAG, "Inserted new Sahal transaction: ${parsedTransaction.transactionId}")
                                } else {
                                    Log.d(TAG, "Transaction ${parsedTransaction.transactionId} already exists.")
                                }
                            } else {
                                dao.insertTransaction(parsedTransaction)
                                Log.i(TAG, "Inserted new Sahal transaction without Tix: ${parsedTransaction.category}")
                            }
                        } else {
                            Log.w(TAG, "Could not parse Sahal SMS structure: $fullBody")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error saving parsed SMS transaction to Room DB", e)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
