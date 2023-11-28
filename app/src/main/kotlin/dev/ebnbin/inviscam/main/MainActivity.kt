package dev.ebnbin.inviscam.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.ebnbin.inviscam.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
