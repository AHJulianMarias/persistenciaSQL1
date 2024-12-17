package com.example.persistenciasql1

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private val valladolid = LatLng(41.6523, -4.7245)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(valladolid, 13f))
        googleMap.setOnMapClickListener { latLng ->
            Toast.makeText(
                requireContext(),
                "Latitud: ${latLng.latitude}, Longitud: ${latLng.longitude}",
                Toast.LENGTH_LONG
            ).show()
            Log.d("Coordenadas", "Latitud: ${latLng.latitude}, Longitud: ${latLng.longitude}")
            mMap.addMarker(MarkerOptions().position(latLng).title("Marker puesto manualmente"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13f))
        }
        googleMap.setOnMapLongClickListener {latLng->
            mMap.addMarker((MarkerOptions().position(latLng).title("Marker puesto con on long click").icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))).snippet(("Teléfono: 983989784")).draggable(true))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13f))

        }
    }
}