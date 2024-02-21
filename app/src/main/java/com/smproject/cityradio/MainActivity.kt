package com.smproject.cityradio

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smproject.cityradio.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.red)))

        supportActionBar?.title = "Your City Radio";

        binding.imgPlayPauseBtn.setOnClickListener {
            viewModel.clickBtnPlayer()
        }

        binding.txtHomeLink.setOnClickListener {
            openYourCityRadioLink()
        }

        viewModel.viewState.observe(this) {
            Log.d("TTTT", "viewModel.viewState.observe: $it")
            renderUI(it)
        }



        viewModel.songTitle.observe(this) { songTitle ->
            binding.title.text = songTitle

        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.startUpdateSongTitle()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_items, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.spotifyPlaylist -> {
                openSpotify()
                true
            }
            R.id.backgroundMusicService -> {
                openBackgroundMusicService()
                true
            }

            R.id.djJainaroBooking -> {
                openDjJainaroBooking()
                true
            }
            R.id.radioOnFacebook -> {
                openRadioOnFacebook()
                true
            }
            R.id.programShedule -> {
                openProgramShedule()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openProgramShedule() {
        val url = "https://jainaro.wixsite.com/jainaro/yourcityradio-1"

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)

        val chooser = Intent.createChooser(intent, "Open link with")

        if (chooser.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        } else {
            Toast.makeText(this, "No app can handle this action", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openRadioOnFacebook() {
        val url = "https://www.facebook.com/yourcityradio"

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)

        val chooser = Intent.createChooser(intent, "Open link with")

        if (chooser.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        } else {
            Toast.makeText(this, "No app can handle this action", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDjJainaroBooking() {
        val url = "https://jainaro.wixsite.com/jainaro/en/contact"

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)

        val chooser = Intent.createChooser(intent, "Open link with")

        if (chooser.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        } else {
            Toast.makeText(this, "No app can handle this action", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openBackgroundMusicService() {
        val url = "https://jainaro.wixsite.com/jainaro/en/bar-and-restaurant-music-service"

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)

        val chooser = Intent.createChooser(intent, "Open link with")

        if (chooser.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        } else {
            Toast.makeText(this, "No app can handle this action", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openYourCityRadioLink() {
        val url = "https://jainaro.wixsite.com/jainaro"

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)

        val chooser = Intent.createChooser(intent, "Open link with")

        if (chooser.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        } else {
            Toast.makeText(this, "No app can handle this action", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openSpotify() {
        val url = "https://jainaro.wixsite.com/jainaro/dj-sets"

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)

        val chooser = Intent.createChooser(intent, "Open link with")

        if (chooser.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        } else {
            Toast.makeText(this, "No app can handle this action", Toast.LENGTH_SHORT).show()
        }

    }

    private fun renderUI(mainViewState: MainViewState) {
        when (mainViewState.btnStatus) {
            PlayerStatus.PLAYING -> binding.imgPlayPauseBtn.setImageResource(R.drawable.ic_pause_button)

            PlayerStatus.PAUSE -> binding.imgPlayPauseBtn.setImageResource(R.drawable.ic_play_button_dark)
        }
    }
}