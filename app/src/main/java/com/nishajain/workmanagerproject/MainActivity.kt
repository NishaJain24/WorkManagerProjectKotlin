package com.nishajain.workmanagerproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.work.WorkManager
import android.view.View
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest


class MainActivity : AppCompatActivity(), View.OnClickListener {
    var tvStatus: TextView? = null
    var btnSend: Button? = null
    var btnStorageNotLow: Button? = null
    var btnBatteryNotLow: Button? = null
    var btnNetworkType: Button? = null
    var mRequest: OneTimeWorkRequest? = null
    var mWorkManager: WorkManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        tvStatus = findViewById(R.id.tvStatus)
        btnSend = findViewById(R.id.btnSend)
        btnStorageNotLow = findViewById(R.id.buttonStorageNotLow)
        btnBatteryNotLow = findViewById(R.id.buttonBatteryNotLow)
        btnNetworkType = findViewById(R.id.buttonNetworkType)
        mWorkManager = WorkManager.getInstance()
        btnSend!!.setOnClickListener(this)
        btnStorageNotLow!!.setOnClickListener(this)
        btnBatteryNotLow!!.setOnClickListener(this)
        btnNetworkType!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        tvStatus!!.text = ""
        val mConstraints: Constraints
        when (v.id) {
            R.id.btnSend ->
                mRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java).build()
            R.id.buttonStorageNotLow -> {
                /**
                 * Constraints
                 * If TRUE task execute only when storage's is not low
                 */
                mConstraints = Constraints.Builder().setRequiresStorageNotLow(true).build()
                /**
                 * OneTimeWorkRequest with requiresStorageNotLow Constraints
                 */
                mRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                    .setConstraints(mConstraints).build()
            }
            R.id.buttonBatteryNotLow -> {
                /**
                 * Constraints
                 * If TRUE task execute only when battery isn't low
                 */
                mConstraints = Constraints.Builder().setRequiresBatteryNotLow(true).build()
                /**
                 * OneTimeWorkRequest with requiresBatteryNotLow Constraints
                 */
                mRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                    .setConstraints(mConstraints).build()
            }
            R.id.buttonNetworkType -> {
                /**
                 * Constraints
                 * Network type is conneted
                 */
                mConstraints =
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                /**
                 * OneTimeWorkRequest with requiredNetworkType Connected Constraints
                 */
                mRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
                    .setConstraints(mConstraints).build()
            }
            else -> {
            }
        }
        /**
         * Fetch the particular task status using request ID
         */
        mWorkManager!!.getWorkInfoByIdLiveData(mRequest!!.id).observe(this, { workInfo ->
            if (workInfo != null) {
                val state = workInfo.state
                tvStatus!!.append(
                    """
    $state
    
    """.trimIndent()
                )
            }
        })
        /**
         * Enqueue the WorkRequest
         */
        mWorkManager!!.enqueue(mRequest!!)
    }

    companion object {
        const val MESSAGE_STATUS = "message_status"
    }
}