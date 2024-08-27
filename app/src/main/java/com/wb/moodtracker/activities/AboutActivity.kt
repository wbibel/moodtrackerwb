package com.wb.moodtracker.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import com.wb.moodtracker.R

class AboutActivity : AppCompatActivity() {
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var textView4: TextView
    private lateinit var textView5: TextView
    private lateinit var textView6: TextView
    private lateinit var textView7: TextView
    private lateinit var textView8: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)

        textView1 = findViewById(R.id.textView1)
        textView2 = findViewById(R.id.textView2)
        textView3 = findViewById(R.id.textView3)
        textView4 = findViewById(R.id.textView4)
        textView5 = findViewById(R.id.textView5)
        textView6 = findViewById(R.id.textView6)
        textView7 = findViewById(R.id.textView7)
        textView8 = findViewById(R.id.textView8)
        textView1.text=getString(R.string.about1)
        textView2.text=getString(R.string.about2)
        textView3.text=getString(R.string.about3)
        textView4.text=getString(R.string.about4)
        textView5.text=getString(R.string.about5)
        textView6.text=getString(R.string.about6)
        textView7.text=getString(R.string.about7)
        textView8.text= buildString {
        append(getString(R.string.about8))
        append("\n")
        append(getString(R.string.about9))
        append("\n")
        append(getString(R.string.about10))
        append("\n")
    }

    }
}
