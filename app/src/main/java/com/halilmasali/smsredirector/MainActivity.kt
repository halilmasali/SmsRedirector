package com.halilmasali.smsredirector

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_READ_SMS = 1001
    private val PERMISSIONS_REQUEST_RECEIVE_SMS = 1002
    private val PERMISSIONS_REQUEST_SEND_SMS = 1002


    lateinit var startButton: Button
    lateinit var receiveNumber: EditText
    lateinit var sendNumber: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("numbers", Context.MODE_PRIVATE)
        startButton = findViewById(R.id.startButton)
        receiveNumber = findViewById(R.id.receiveNumber)
        sendNumber = findViewById(R.id.sendNumber)

        (sendNumber as TextView).text = sharedPreferences.getString("sendNumber", "")
        (receiveNumber as TextView).text = sharedPreferences.getString("receiveNumber", "")
        val started = sharedPreferences.getBoolean("isStarted", false)
        if (started)
            startButton.text = "Yönlendirmeyi Bitir"

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_SMS),
                PERMISSIONS_REQUEST_READ_SMS
            )
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SEND_SMS),
                PERMISSIONS_REQUEST_SEND_SMS
            )
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                PERMISSIONS_REQUEST_RECEIVE_SMS
            )
        }

        startButton.setOnClickListener {
            val receiveNum = receiveNumber.text.toString()
            val sendNum = sendNumber.text.toString()
            if (receiveNum.isNotEmpty() && sendNum.isNotEmpty() && startButton.text == "Yönlendirmeyi Başlat") {
                val editor = sharedPreferences.edit()
                editor.putString("receiveNumber", receiveNum)
                editor.putString("sendNumber", sendNum)
                editor.putBoolean("isStarted", true)
                editor.apply()
                startButton.text = "Yönlendirmeyi Bitir"
            }
            else if (startButton.text == "Yönlendirmeyi Bitir"){
                val editor = sharedPreferences.edit()
                editor.putString("receiveNumber", "")
                editor.putString("sendNumber", "")
                editor.putBoolean("isStarted", false)
                editor.apply()
                startButton.text = "Yönlendirmeyi Başlat"
            }

        }
    }

    fun sendSms(message: String,sendNum: String,isStarted:Boolean) {
        if (isStarted) {
            val smsManager = SmsManager.getDefault() as SmsManager
            try {
                smsManager.sendTextMessage(sendNum, null, message, null, null)
                Toast.makeText(this, "SMS gönderildi!", Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                Toast.makeText(this, "SMS gönderim izni reddedildi!", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, "Geçersiz telefon numarası veya mesaj!", Toast.LENGTH_SHORT)
                    .show()
                e.printStackTrace()
            }
        }
    }

}