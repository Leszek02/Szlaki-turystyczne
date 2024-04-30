package edu.put.szlaki.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.put.szlaki.R
import edu.put.szlaki.fragment.TrialList

class FabTrialListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fab_trial_list)

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.TrialList, TrialList())
                .commit()
        }
    }
}