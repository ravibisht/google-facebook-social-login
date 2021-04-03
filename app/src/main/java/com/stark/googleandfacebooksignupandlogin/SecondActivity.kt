package com.stark.googleandfacebooksignupandlogin

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.stark.googleandfacebooksignupandlogin.MainActivity.Companion.INTENT_ORIGIN_FACEBOOK
import com.stark.googleandfacebooksignupandlogin.MainActivity.Companion.INTENT_ORIGIN_GOOGLE

class SecondActivity : AppCompatActivity() {
    private  val TAG = "SecondActivity"
    private lateinit var userInfo : TextView
    private lateinit var userImage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        userImage=findViewById(R.id.userImage)
        userInfo=findViewById(R.id.userData)

        if(intent!=null && intent.getStringExtra(INTENT_ORIGIN_GOOGLE).equals("Google")){
            val parcelableExtra = intent.getParcelableExtra<GoogleSignInAccount>("GoogleData")
    

            Glide.with(this)
                .load(parcelableExtra?.photoUrl)
                .into(userImage)

            var userData = """Name : ${parcelableExtra?.displayName}
                |
                |Person Given Name  : ${parcelableExtra?.givenName}
                |
                |PersonFamilyName   : ${parcelableExtra?.familyName}
                |
                |PersonEmail        : ${parcelableExtra?.email}
                |
                |PersonId           : ${parcelableExtra?.id}
                |
                |Id Token           : ${parcelableExtra?.idToken} 
            """.trimMargin()

            Log.d(TAG, "onCreate: ${userData}")
            Log.d(TAG, "onCreate: ${parcelableExtra?.email}")

            userInfo.text = userData
        }

        else if(intent!=null && intent.getStringExtra(INTENT_ORIGIN_FACEBOOK).equals("Facebook")){
            var userId=intent.getStringExtra("userId")
            var userImageUrl = "https://graph.facebook.com/${userId}/picture?return_ssl_resources=1"
            Glide.with(this).load(userImageUrl).into(userImage)

            var userData = """First Name : ${intent.getStringExtra("firstname")}
                |
                |Last Name  :${intent.getStringExtra("lastname")}
                |
                |User Id   : ${userId}
                |
                |PersonEmail        : ${intent.getStringExtra("email")}
            """.trimMargin()

            userInfo.text=userData

        }
    }
}