package com.halilmasali.smsredirector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage

class SmsReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
                val bundle = intent.extras
                if (bundle != null) {

                    val sharedPreferences = context?.getSharedPreferences("numbers", Context.MODE_PRIVATE)
                    val receiveNum = sharedPreferences?.getString("receiveNumber", "")
                    val sendNum = sharedPreferences?.getString("sendNumber", "")
                    val isStarted = sharedPreferences?.getBoolean("isStarted", false)
                    val mainActivityClass = MainActivity::class.java
                    val mainActivity = mainActivityClass.newInstance()

                    val pdus = bundle["pdus"] as Array<Any>
                    for (i in pdus.indices) {
                        val smsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        val sender = smsMessage.displayOriginatingAddress
                        val messageBody = smsMessage.messageBody
                        if (receiveNum == sender) {
                            if (sendNum != null && isStarted != null) {
                                mainActivity.sendSms(messageBody, sendNum, isStarted)
                            }
                        }
                    }
                }
            }
        }
    }
}