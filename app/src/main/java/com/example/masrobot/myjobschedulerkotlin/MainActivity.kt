package com.example.masrobot.myjobschedulerkotlin

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    val jobId: Int = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_start.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_start -> startJob()
            R.id.btn_cancel -> cancelJob()
        }
    }

    private fun startJob() {
        val mServiceCOmponent = ComponentName(this@MainActivity, GetCurrentWeatherJobService::class.java)
        val builder = JobInfo.Builder(jobId, mServiceCOmponent)
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        builder.setRequiresDeviceIdle(false)
        builder.setRequiresCharging(false)
        builder.setPeriodic(18000)

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())

        Toast.makeText(this@MainActivity, "Job Service started", Toast.LENGTH_SHORT).show()
    }

    private fun cancelJob() {
        val tm = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        tm.cancel(jobId)
        Toast.makeText(this@MainActivity, "Job Service canceled", Toast.LENGTH_SHORT).show()
        finish()
    }
}
