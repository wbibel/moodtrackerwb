package com.wb.moodtracker.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.squareup.picasso.Picasso
import com.wb.moodtracker.R
import com.wb.moodtracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navDrawer: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var googleApiClient: GoogleApiClient
    private val RC_SIGN_IN = 9001



    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawerLayout = binding.drawerLayout
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        navDrawer = binding.navDrawer
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
            .build()
        navDrawer.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_Login -> {
                    val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    updateUI(currentUser)
                    return@setNavigationItemSelectedListener true
                }
                R.id.navigation_Logout -> {
                    val firebaseAuth = FirebaseAuth.getInstance()
                    firebaseAuth.signOut()
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    updateUI(currentUser)
                    return@setNavigationItemSelectedListener true
                }
                R.id.navigation_About -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                    return@setNavigationItemSelectedListener false
                }
            }
            false
        }

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_moods, R.id.navigation_add_mood, R.id.navigation_statistics,
                R.id.navigation_notifications, R.id.navigation_profile
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }



    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser!=null){
            navDrawer.menu.findItem(R.id.navigation_Logout).isVisible=true
            navDrawer.menu.findItem(R.id.navigation_Login).isVisible=false
            navDrawer.getHeaderView(0).findViewById<TextView>(R.id.userTextview).apply {
                visibility= View.VISIBLE
                text=currentUser.displayName
            }
            navDrawer.getHeaderView(0).findViewById<TextView>(R.id.emailTextview).apply {
                visibility = View.VISIBLE
                text=currentUser.email
            }
            navDrawer.getHeaderView(0).findViewById<TextView>(R.id.detailedTextview).apply {
                visibility = View.VISIBLE
                text=currentUser.phoneNumber

            }
            navDrawer.getHeaderView(0).findViewById<ImageView>(R.id.imageViewAccountImage).apply {
                visibility = View.VISIBLE
                Picasso.get().load(currentUser.photoUrl).into(this)
            }
        }
        else {
            navDrawer.menu.findItem(R.id.navigation_Logout).isVisible=false
            navDrawer.menu.findItem(R.id.navigation_Login).isVisible=true
            navDrawer.getHeaderView(0).findViewById<TextView>(R.id.userTextview).apply {
                visibility= View.GONE
                text=null
            }
            navDrawer.getHeaderView(0).findViewById<TextView>(R.id.emailTextview).apply {
                visibility = View.GONE
                text=null
            }
            navDrawer.getHeaderView(0).findViewById<TextView>(R.id.detailedTextview).apply {
                visibility = View.GONE
                text=null
            }
            navDrawer.getHeaderView(0).findViewById<ImageView>(R.id.imageViewAccountImage).apply {
                visibility = View.GONE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) true
        else super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.menu.findItem(R.id.navigation_moods).isChecked = true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    override fun onStart() {
        super.onStart()
        googleApiClient.connect()
        val user = FirebaseAuth.getInstance().currentUser
        updateUI(user)
    }
    override fun onStop() {
        super.onStop()
        googleApiClient.disconnect()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            if (result!!.isSuccess) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account)
            } else {
                toastShow(getString(R.string.sign_in_failed))
            }
        }
    }
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val idToken = account?.idToken
        if (idToken != null) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        val email = user?.email
                        val toastString = buildString {
        append(getString(R.string.signed_in_as))
        append(email)
    }
                        updateUI(user)
                        toastShow(toastString)
                    } else {
                        toastShow(getString(R.string.sign_in_failed))
                    }
                }
        } else {
            toastShow(getString(R.string.unable_to_retrieve_idtoken))
        }
    }

    private fun toastShow(text: String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }


}
