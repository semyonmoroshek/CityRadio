package com.smproject.cityradio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.smproject.cityradio.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        supportActionBar?.title = "Your City Radio";

        binding.imgPlayPauseBtn.setOnClickListener {
            viewModel.clickBtnPlayer()
        }

        viewModel.viewState.observe(this) {
            Log.d("TTTT", "ViewState observe: $it ")
            renderUI(it)
        }
    }

    private fun renderUI(mainViewState: MainViewState) {
        Log.d("TTTT", "ViewState renderUI: ${mainViewState.btnStatus}")
        when (mainViewState.btnStatus) {
            PlayerStatus.PLAYING ->
                binding
                    .imgPlayPauseBtn
                    .setImageResource(R.drawable.ic_pause_button)
            PlayerStatus.PAUSE ->
                binding
                    .imgPlayPauseBtn
                    .setImageResource(R.drawable.ic_play_button_dark)
        }
    }
}