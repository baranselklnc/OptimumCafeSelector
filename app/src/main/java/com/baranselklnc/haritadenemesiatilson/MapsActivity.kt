package com.baranselklnc.haritadenemesiatilson

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.baranselklnc.haritadenemesiatilson.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager // konum yöneticisi
    private lateinit var locationListener: LocationListener// konum dinleyicisi
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener (dinleyici)
        println("deneme")
        // Add a marker in Sydney and move the camera
        //Latitude-> Enlem
        // Longitude -> Boylam
        /*
        val yahsihan = LatLng(39.851479, 33.449879)
        mMap.addMarker(MarkerOptions().position(yahsihan).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yahsihan,15f))
        */
        //casting-> as LOCATİON_SERVİCE değer olarak any döndürecek ama ben onun bir LocationManager olduğunu biliyorum "as" in kullanımı böyledir.
        locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager //Context. dedikten sonra erişilebilecek servisler görünecektir
        locationListener= LocationListener { p0 ->

            mMap.clear() //markerları vs.siler
            val guncelKonum=LatLng(p0.latitude,p0.longitude)
            mMap.addMarker(MarkerOptions().position(guncelKonum).title("Güncel Konumunuz"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum,19f ))
            val geocoder=Geocoder(this@MapsActivity,Locale.getDefault())
            try {
             val adresListesi=geocoder.getFromLocation(p0.latitude,p0.longitude,1)
                println(adresListesi!!.get(0).toString())

            }
            catch (e:Exception){
                e.printStackTrace()
            }

        }
        if (ContextCompat.checkSelfPermission(  this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            //izin verilmemiş
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
        else{
            //izin verildi
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
            val sonBilinenKonum=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
           if (sonBilinenKonum!=null){
               val sonBilinenLatLng=LatLng(sonBilinenKonum.latitude,sonBilinenKonum.longitude)
               mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonBilinenLatLng,19f))
               mMap.addMarker(MarkerOptions().position(sonBilinenLatLng).title("Son Bilinen Konum"))
           }
        }




    }


    override fun onRequestPermissionsResult( //izin istediğimiz zaman iznin sonuçlarını bildiren metod
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode==1){
            if(grantResults.size>0){
                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    val dinleyici = object :GoogleMap.OnMapLongClickListener {
        override fun onMapLongClick(p0: LatLng) {
           mMap.clear()
            val geocoder=Geocoder(this@MapsActivity,Locale.getDefault())
            if (p0!=null){
                var adres =""
                try {
                     val adresListesi=geocoder.getFromLocation(p0.latitude,p0.longitude,1, )
                    if (adresListesi!!.size>0){
                        adres+=adresListesi.get(0).subThoroughfare
                    }
                }
                catch (e:Exception){
                    e.printStackTrace()
                }

                mMap.addMarker(MarkerOptions().position(p0).title(adres))
            }
        }
    }
}