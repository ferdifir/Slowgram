package com.ferdifir.slowgram.presentation.ui.explore

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ferdifir.slowgram.R
import com.ferdifir.slowgram.data.local.entity.StoryEntity
import com.ferdifir.slowgram.data.remote.response.ListStoryItem
import com.ferdifir.slowgram.databinding.CustomTooltipMapsExploreBinding
import com.ferdifir.slowgram.databinding.FragmentMapsBinding
import com.ferdifir.slowgram.presentation.ui.detail.DetailActivity
import com.ferdifir.slowgram.presentation.ui.main.MainActivity
import com.ferdifir.slowgram.presentation.viewmodel.ViewModelFactory
import com.ferdifir.slowgram.utils.Const
import com.ferdifir.slowgram.utils.Const.EXTRA_DETAIL
import com.ferdifir.slowgram.utils.Helper
import com.ferdifir.slowgram.utils.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter,
    AdapterView.OnItemSelectedListener {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: MapsViewModel
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = (activity as MainActivity).intent.getStringExtra(Const.EXTRA_TOKEN)
        val factory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        val zoomLevel = arrayOf(
            getString(R.string.const_text_adapter_maps_default),
            getString(R.string.const_text_adapter_maps_province),
            getString(R.string.const_text_adapter_maps_city),
            getString(R.string.const_text_adapter_maps_district),
            getString(R.string.const_text_adapter_maps_around)
        )

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item, zoomLevel
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.zoomType.adapter = adapter
        binding.zoomType.onItemSelectedListener = this

        val mapFragment =
            (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
        mapFragment.getMapAsync(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            } else {
                Helper.notifyGivePermission(
                    requireContext(),
                    "Give the app access to read your location"
                )
            }
        }

    override fun onMapReady(gmap: GoogleMap) {
        mMap = gmap
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true

        mMap.setInfoWindowAdapter(this)
        mMap.setOnInfoWindowClickListener { marker ->
            val data: ListStoryItem = marker.tag as ListStoryItem
            val dto = StoryEntity(
                data.photoUrl,
                data.createdAt,
                data.name,
                data.description,
                data.lon,
                data.id,
                data.lat,
                false
            )
            routeToDetailStory(dto)
        }
        getMyLocation()
        setMapStyle()

        token?.let {
            viewModel.getStoriesWithLocation(it).observe(viewLifecycleOwner) { result ->
                when(result) {
                    is Result.Success -> {
                        for (story in result.data.listStory) {
                            mMap.addMarker(
                                MarkerOptions().position(
                                    LatLng(
                                        story.lat ?: 0.0,
                                        story.lon ?: 0.0
                                    )
                                )
                            )?.tag = story
                        }
                    }
                    else -> {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.coordinateTemp.observe(this) {
            CameraUpdateFactory.newLatLngZoom(it, 4f)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        (activity as MainActivity),
                        R.raw.gmaps_style
                    )
                )
            if (!success) {
                Log.e("MAPS", "Style parsing  failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("MAPS", "Can't find style. Error: ", exception)
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                (activity as MainActivity),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.coordinateTemp.postValue(
                        LatLng(
                            location.latitude,
                            location.longitude
                        )
                    )
                } else {
                    viewModel.coordinateTemp.postValue(LatLng(-2.3932797, 108.8507139))
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun routeToDetailStory(data: StoryEntity) {
        val intent = Intent(activity, DetailActivity::class.java)
        intent.putExtra(EXTRA_DETAIL, data)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        requireActivity().startActivity(intent)
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View {
        val bindingTooltips =
            CustomTooltipMapsExploreBinding.inflate(LayoutInflater.from(requireContext()))
        val data: ListStoryItem = marker.tag as ListStoryItem
        bindingTooltips.labelLocation.text = Helper.parseAddressLocation(
            requireContext(),
            marker.position.latitude, marker.position.longitude
        )
        bindingTooltips.name.text = StringBuilder("Story by ").append(data.name)
        bindingTooltips.image.setImageBitmap(Helper.bitmapFromURL(requireContext(), data.photoUrl))
        bindingTooltips.storyDescription.text = data.description
        bindingTooltips.storyUploadTime.text = Helper.getUploadStoryTime(data.createdAt)
        return bindingTooltips.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val level: Float = when (position) {
            0 -> 4f
            1 -> 8f
            2 -> 11f
            3 -> 14f
            4 -> 17f
            else -> 4f
        }
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                viewModel.coordinateTemp.value!!,
                level
            )
        )
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(-2.3932797, 108.8507139), 4f)
        )
    }

}