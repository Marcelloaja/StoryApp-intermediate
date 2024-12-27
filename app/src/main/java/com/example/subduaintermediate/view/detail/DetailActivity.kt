package com.example.subduaintermediate.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.subduaintermediate.data.response.ListStoryItem
import com.example.subduaintermediate.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val detail =
            intent.getParcelableExtra<ListStoryItem>(DetailActivity.DETAIL_STORY) as ListStoryItem
        setupAction(detail)
        supportActionBar?.show()
        supportActionBar?.title = "Detail Notes"
    }

    private fun setupAction(detail: ListStoryItem) {
        Glide.with(applicationContext)
            .load(detail.photoUrl)
            .into(binding.ivDetail)
        binding.nameDetail.text = detail.name
        binding.descDetail.text = detail.description
    }

    companion object {
        const val DETAIL_STORY = "detail_notes"
    }
}