package com.example.calllogs

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.calllogs.R
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.widget.TextView
import android.widget.Toast
import android.provider.CallLog
import android.view.View
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    Manifest.permission.READ_CALL_LOG
                )
            ) {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_CALL_LOG), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_CALL_LOG), 1)
            }
        } else {
            //do stuff
            val textView = findViewById<View>(R.id.textView) as TextView
            textView.text = callDetails
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.READ_CALL_LOG
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show()
                        val textView = findViewById<View>(R.id.textView) as TextView
                        textView.text = callDetails
                    }
                } else {
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private val callDetails: String
        private get() {
            val sb = StringBuffer()
            val managedCursor = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null)
            val number = managedCursor!!.getColumnIndex(CallLog.Calls.NUMBER)
            val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
            val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
            val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
            sb.append("Call Details:\n\n")
            while (managedCursor.moveToNext()) {
                val phNumber = managedCursor.getString(number)
                val callType = managedCursor.getString(type)
                val callDate = managedCursor.getString(date)
                val callDayTime = Date(java.lang.Long.valueOf(callDate))
                val formatter = SimpleDateFormat("dd-MM-yy HH:mm")
                val dateString = formatter.format(callDayTime)
                val callDuration = managedCursor.getString(duration)
                var dir: String? = null
                val dircode = callType.toInt()
                when (dircode) {
                    CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
                    CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
                    CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
                }
                sb.append(
                    """
Phone Number: $phNumber
CallType: $dir
Call Date: $dateString 
Call Duration: $callDuration"""
                )
                sb.append("\n--------------------------------------------")
            }
            managedCursor.close()
            return sb.toString()
        }
}