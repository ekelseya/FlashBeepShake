package com.example.flashbeepshake

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.View.INVISIBLE
import android.widget.Button
import android.widget.Toast
import android.widget.ToggleButton


@TargetApi(Build.VERSION_CODES.M)
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MainActivity : AppCompatActivity() {

    private var flashCheck = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val mCameraId = mCameraManager.cameraIdList[0]

        //Checks if Flash is available
        val isFlashAvailable = applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

/*          //Below code automatically pops an error and shuts down the app if there is no flash  */

/*        if (!isFlashAvailable) {

            val alert = AlertDialog.Builder(this@MainActivity)
                .create()
            alert.setTitle("Error!")
            alert.setMessage("Your device doesn't support flash!")
            alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { _, _ ->
                // closing the application
                finish()
                System.exit(0)
            }
            alert.show()
            return
        }*/

        val buttonFlash = findViewById<ToggleButton>(R.id.buttonFlash)
        if (!isFlashAvailable) buttonFlash.visibility = INVISIBLE
        buttonFlash.setOnCheckedChangeListener { _, checked ->
            this.flashCheck = if (checked) {
                mCameraManager.setTorchMode(mCameraId, true)
                1

            } else {

                mCameraManager.setTorchMode(mCameraId, false)
                0
            }
        }

        val buttonBeep = findViewById<Button>(R.id.buttonBeep)
        var mp = MediaPlayer.create(this, R.raw.beep02)
        buttonBeep.setOnClickListener {
            if (mp.isPlaying) {
                mp.stop()
                mp.release()
                mp = MediaPlayer.create(this, R.raw.beep02)
            }
            mp.start()

        }

        val buttonShake = findViewById<Button>(R.id.buttonShake)
        //buttonShake is only visible if the phone has vibrate
        if (!hasVibrator){
            buttonShake.visibility = INVISIBLE
        }
        buttonShake.setOnClickListener {
            vibrate()
        }
    }

    private fun Context.vibrate(milliseconds:Long = 500){
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Check whether device/hardware has a vibrator
        val canVibrate:Boolean = vibrator.hasVibrator()

        if(canVibrate){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                // void vibrate (VibrationEffect vibe)
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        milliseconds,
                        // The default vibration strength of the device.
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            }else{
                Toast.makeText(this, "Sorry, no shakey-shake on your phone!", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Extension property to check whether device has Vibrator
    private val Context.hasVibrator:Boolean
        get() {
            val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            return vibrator.hasVibrator()
        }

//Disables flash onStop -- not very useful
/*    override fun onStop() {
        super.onStop()
        val mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val mCameraId = mCameraManager.cameraIdList[0]
        if (flashCheck == 1){
            mCameraManager.setTorchMode(mCameraId, false)
        }

    }*/
// Checks status of flash on Start -- only useful if above functions in use
    override fun onStart() {
        super.onStart()
        val mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val mCameraId = mCameraManager.cameraIdList[0]
        if (flashCheck == 1){
            mCameraManager.setTorchMode(mCameraId, true)
        }
    }
// Disables flash onPause -- not very useful
/*    override fun onPause() {
        super.onPause()
        val mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val mCameraId = mCameraManager.cameraIdList[0]
        if (flashCheck == 1){
            mCameraManager.setTorchMode(mCameraId, false)
        }
    }*/
//Checks status of flash on Resume (only useful if above functions in use)
    override fun onResume() {
        super.onResume()
        val mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val mCameraId = mCameraManager.cameraIdList[0]
        if (flashCheck == 1){
            mCameraManager.setTorchMode(mCameraId, true)
        }
    }
// Deisables flash onDestroy -- turns flash off if user closes app
    override fun onDestroy() {
        super.onDestroy()
        val mCameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val mCameraId = mCameraManager.cameraIdList[0]
        if (flashCheck == 1){
            mCameraManager.setTorchMode(mCameraId, false)
        }
    }
}

