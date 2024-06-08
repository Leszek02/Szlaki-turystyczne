package edu.put.szlaki.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.TextView
import edu.put.szlaki.R

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val splashText = findViewById<TextView>(R.id.splashText)

        val rotation = ObjectAnimator.ofFloat(splashText, "rotation", 0f, 360f)
        rotation.duration = 2000
        rotation.interpolator = LinearInterpolator()

        rotation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                startActivity(Intent(this@StartActivity, MainActivity::class.java))
                finish()
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        rotation.start()
    }
}
