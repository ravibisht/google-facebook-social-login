package com.stark.googleandfacebooksignupandlogin


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val TAG = "MainActivity"
    private lateinit var facebookLoginBtn: LoginButton
    private lateinit var callBackManager: CallbackManager

    companion object {
        private val RC_SIGN_IN = 333
        val INTENT_ORIGIN_FACEBOOK = "INTENT_ORIGIN_FACEBOOK"
        val INTENT_ORIGIN_GOOGLE = "INTENT_ORIGIN_GOOGLE"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val account = GoogleSignIn.getLastSignedInAccount(this)

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)

        signInButton.setOnClickListener {
            signIn()
        }

        facebookLoginBtn = findViewById(R.id.facebook_login_button)
        callBackManager = CallbackManager.Factory.create()

        facebookLoginBtn.setPermissions(listOf("email", "public_profile"))

        facebookLoginBtn.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult?) {

                val intent = Intent(this@MainActivity, SecondActivity::class.java)
                intent.putExtra("userId", result!!.accessToken.userId)
                intent.putExtra(INTENT_ORIGIN_FACEBOOK, "Facebook")



                val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                    object : GraphRequest.GraphJSONObjectCallback {
                        override fun onCompleted(
                            jsonObject: JSONObject?,
                            response: GraphResponse?
                        ) {

                            intent.putExtra("firstname", jsonObject?.getString("first_name"))
                            intent.putExtra("last_name", jsonObject?.getString("last_name"))

                            intent.putExtra("email", jsonObject?.getString("email"))
                            intent.putExtra(
                                "image",
                                "https://graph.facebook.com/" + result!!.accessToken.userId + "/picture?type=normal"
                            )

                        }

                    })

                val parameters = Bundle();
                parameters.putString("fields", "first_name,last_name,email,id")
                request.setParameters(parameters);
                request.executeAsync();

                startActivity(intent)


            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
            }

        })

    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

        callBackManager.onActivityResult(requestCode, resultCode, data)

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if (acct != null) {
                val personName = acct.displayName
                val personGivenName = acct.givenName
                val personFamilyName = acct.familyName
                val personEmail = acct.email
                val personId = acct.id
                val personPhoto: Uri? = acct.photoUrl

                Toast.makeText(this, "${personName}", Toast.LENGTH_LONG).show()
            }

            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("GoogleData", acct)
            intent.putExtra(INTENT_ORIGIN_GOOGLE, "Google")
            startActivity(intent)

        } catch (e: ApiException) {

            Log.w(TAG, "signInResult:failed code=${e.toString()}")
        }
    }

}