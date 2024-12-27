package com.example.subduaintermediate.view.maps

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.subduaintermediate.R
import com.example.subduaintermediate.data.api.ApiConfig
import com.example.subduaintermediate.data.preference.UserPreference
import com.example.subduaintermediate.data.preference.dataStore
import com.example.subduaintermediate.data.response.ListStoryItem
import com.example.subduaintermediate.databinding.ActivityMapsBinding
import com.example.subduaintermediate.repository.StoryRepository
import kotlinx.coroutines.launch
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.first

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        lifecycleScope.launch {
            val userPreference = UserPreference.getInstance(applicationContext.dataStore)
            val userSession = userPreference.getSession().first()
            val token = userSession.token

            val factory = MapsViewModelFactory(StoryRepository(ApiConfig.getApiService(token)))
            val viewModel = ViewModelProvider(this@MapsActivity, factory).get(MapsViewModel::class.java)

            observeStories(viewModel)
        }
    }

    private fun observeStories(viewModel: MapsViewModel) {
        viewModel.getStoriesWithLocation().observe(this) { response ->
            if (response.error) {
                Toast.makeText(this, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                Log.e("MapsActivity", "Failed to fetch data: ${response.message}")
            } else {
                addMarkersToMap(response.listStory)
            }
        }
    }

    private fun addMarkersToMap(stories: List<ListStoryItem>) {
        val boundsBuilder = LatLngBounds.builder()

        stories.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.name)
                    .snippet(story.description)
            )
            boundsBuilder.include(latLng)
        }

        val bounds = boundsBuilder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
        mMap.moveCamera(cameraUpdate)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}
