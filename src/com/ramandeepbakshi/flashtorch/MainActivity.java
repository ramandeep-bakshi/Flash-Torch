/*******************************************************************************
 * Copyright (c) 2014 Ramandeep Singh Bakshi
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge this software into other softwares and/or 
 * publish derivatives of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
 * 
 * - The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * - Due credit should be given to the 'Ring My Droid' app either in print or
 * a link to the 'Ring My Droid' app on Google Play Store.
 *
 * - Due credit should be given to the developer, Ramandeep Singh Bakshi, in print
 * by mentioning the complete name, as well as a link to the official 
 * website 'http://www.ramandeepbakshi.com' or a link to the facebook 
 * page 'https://www.facebook.com/officialrbxi'
 *
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/



package com.ramandeepbakshi.flashtorch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager; //To verify System Features(in this case Camera Flash)
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
 
public class MainActivity extends Activity {
 
    ImageButton btnSwitch; //Object of ImageButton
 
    private Camera camera; //Object of Camera
    private boolean isFlashOn; //Boolean variable to store Flash state
    private boolean hasFlash; //Boolean variable to check Flash availability
    Parameters params; //Object of Camera.Parameters
    MediaPlayer mp; //Object of MediaPlayer
 
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        // flash switch button
        btnSwitch = (ImageButton) findViewById(R.id.btnSwitch);
 
     
        // First check if device is supporting flashlight or not        
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
 
        if (!hasFlash) 
        {
            // device doesn't support flash
            // Show alert message and close the application
        	
           //This function is not working in new API versions. Try in older if possible.
        	/* AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show(); */
        	
        	
        	
        	//Generating an error message alert, if Camera Flash is not found.
        	AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Error")
            .setMessage("Sorry, your device camera doesn't have flash!")
            .setCancelable(false)
            .setNegativeButton("Close",new DialogInterface.OnClickListener() 
            {
                public void onClick(DialogInterface dialog, int id) 
                {
                    //dialog.cancel();
                	finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            
            return;
        }
 
        // get the camera
        getCamera();
         
        // displaying button image
        toggleButtonImage();
         
         
        // Switch button click event to toggle flash on/off
        btnSwitch.setOnClickListener(new View.OnClickListener()  
        {
 
            @Override
            public void onClick(View v) 
            {
                if (isFlashOn) 
                {
                    // turn off flash
                    turnOffFlash();
                } 
                else 
                {
                    // turn on flash
                    turnOnFlash();
                }
            }
        });
    }
 
     
    // Get the camera
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
            }
        }
    }
 
     
     // Turning On flash
    private void turnOnFlash() 
    {
        if (!isFlashOn) 
        {
            if (camera == null || params == null) 
            {
                return;
            }
            // play sound
            playSound();
             
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            camera.autoFocus(new AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                }
            });
            camera.startSmoothZoom(5);
            isFlashOn = true;
             
            // changing button/switch image
            toggleButtonImage();
        }
 
    }
 
 
    // Turning Off flash
    private void turnOffFlash() 
    {
        if (isFlashOn) 
        {
            if (camera == null || params == null) 
            {
                return;
            }
            // play sound
            playSound();
             
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopSmoothZoom();
            camera.stopPreview();
            isFlashOn = false;
             
            // changing button/switch image
            toggleButtonImage();
        }
    }
     
 
     // Playing sound
     // will play button toggle sound on flash on / off
    private void playSound()
    {
        if(isFlashOn)
        {
            mp = MediaPlayer.create(MainActivity.this, R.raw.torch_off);
        }
        else
        {
            mp = MediaPlayer.create(MainActivity.this, R.raw.torch_on);
        }
        mp.setOnCompletionListener(new OnCompletionListener() 
        {
 
            @Override
            public void onCompletion(MediaPlayer mp) 
            {
                // TODO Auto-generated method stub
                mp.release();
            }
        }); 
        mp.start();
    }
     
    /*
     * Toggle switch button images
     * changing image states to on / off
     * */
    private void toggleButtonImage(){
        if(isFlashOn)
        {
            btnSwitch.setImageResource(R.drawable.torch_on_new);
        }
        else
        {
            btnSwitch.setImageResource(R.drawable.torch_off_new);
        }
    }
 
    @Override
    protected void onDestroy() 
    {
        super.onDestroy();
    }
 
    @Override
    protected void onPause() 
    {
        super.onPause();
         
        // on pause turn off the flash
        turnOffFlash();
    }
 
    @Override
    protected void onRestart() 
    {
        super.onRestart();
    }
 
    @Override
    protected void onResume() 
    {
        super.onResume();
         
        // on resume turn on the flash
        if(hasFlash)
            turnOnFlash();
    }
 
    @Override
    protected void onStart() 
    {
        super.onStart();
         
        // on starting the app get the camera parameters
        getCamera();
    }
 
    @Override
    protected void onStop() 
    {
        super.onStop();
         
        // on stop release the camera
        if (camera != null) 
        {
            camera.release();
            camera = null;
        }
    }
 
}
