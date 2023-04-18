package com.uqac.swipixel

import android.app.AlertDialog
import android.content.Context
import android.location.Geocoder
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import java.lang.Math.abs
import java.lang.Math.floor
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var cardDeck: Swiper

    // variable pour afficher une photo de la galerie
    var selectedImages: List<SwiperData> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        cardDeck = root.findViewById<Swiper>(R.id.cardDeck);

        val  pickMultipleMedia: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            // Do something with the selected URIs
            if (uris.isNotEmpty()) {
                selectedImages = uris.map {
                    SwiperData(it)
                }
                cardDeck.addData(selectedImages)
            }
        }

        // Ecouter le bouton pour charger l'image
        val pickButton = root.findViewById<Button>(R.id.button)
        pickButton.setOnClickListener {  pickMultipleMedia.launch("image/*") }

        val revertButton = root.findViewById<Button>(R.id.revert)
        revertButton.setOnClickListener { cardDeck.revertSwipedCard() }


        // Ecouter bouton info
        val infoButton: ImageButton = root.findViewById(R.id.info_button)
        infoButton.setOnClickListener {
            // Récupérer les infos de l'image actuellement devant
            if(!cardDeck.isEmpty()){
                showImageInfo(cardDeck.getCurrentData())
            }
            else{
                Toast.makeText(context, "No photo", Toast.LENGTH_SHORT).show()
            }
        }

        val deleteButton = root.findViewById<ImageView>(R.id.bin_button)
        deleteButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToBinFragment(cardDeck.deletedImages.toTypedArray())
            findNavController().navigate(action)
        }

        return root;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    private fun showImageInfo(imageItem: SwiperData) {
        val filePath = getFilePathFromUri(requireContext(), imageItem.image)
        // Créez une instance de ExifInterface pour lire les exifs de l'image
        val exif = ExifInterface(filePath!!)

        // Obtenez la date et l'heure de prise de vue
        val dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME)

        // Obtenez la latitude et la longitude
        val latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
        val longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
        val latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
        val longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)

        // Convertissez la latitude et la longitude en une ville
        val location = getLocationFromExif(latitude, longitude, latitudeRef, longitudeRef)

        // Créez une boîte de dialogue pour afficher les informations de l'image
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Image information")
            .setMessage("Date and hours : $dateTime\nLieu : $location")
            .setPositiveButton("OK", null)
            .create()
        dialog.show()
    }

    private fun getLocationFromExif(
        latitude: String?,
        longitude: String?,
        latitudeRef: String?,
        longitudeRef: String?
    ): String {
        if (latitude != null && longitude != null && latitudeRef != null && longitudeRef != null) {
            val lat = convertToDegree(latitude.toDouble())
            val lon = convertToDegree(longitude.toDouble())

            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lon, 1)

            if (addresses!!.isNotEmpty()) {
                return addresses[0].locality ?: addresses[0].adminArea ?: ""
            }
        }
        return ""
    }

    private fun convertToDegree(coordinate: Double): Double {
        val absoluteCoordinate = abs(coordinate)
        val degrees = floor(absoluteCoordinate)
        val minutes = floor((absoluteCoordinate - degrees) * 60)
        val seconds = (absoluteCoordinate - degrees - minutes / 60) * 3600
        return degrees + minutes / 60 + seconds / 3600
    }

    fun getFilePathFromUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            it.moveToFirst()
            val columnIndex = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            filePath = it.getString(columnIndex)
            cursor.close()
        }
        return filePath
    }
}