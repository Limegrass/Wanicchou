package com.waifusims.wanicchou

import androidx.core.app.NavUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.waifusims.wanicchou.R
import com.waifusims.wanicchou.ui.fragments.SettingsFragment

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(supportFragmentManager.findFragmentById(R.id.action_settings) == null){
            supportFragmentManager.beginTransaction()
                    .replace(R.id.activity_settings, SettingsFragment())
                    .commit()
        }

        setContentView(R.layout.activity_settings)
        val actionBar = this.actionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemID = item.itemId
        when (itemID){
            android.R.id.home ->
                NavUtils.navigateUpFromSameTask(this)
        }
        return super.onOptionsItemSelected(item)
    }
}
