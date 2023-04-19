package com.uqac.swipixel

import android.app.AlertDialog
import android.content.Context
import android.location.Geocoder
import android.location.Location
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isEmpty
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import java.lang.Math.abs
import java.lang.Math.floor
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(R.layout.fragment_home), CardDeckChangeListener {

    private lateinit var cardDeck: Swiper
    private lateinit var textRemainingPics : TextView

    // variable pour afficher une photo de la galerie
    var selectedImage: List<SwiperData> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        textRemainingPics = root.findViewById(R.id.nb_rm_pics)

        cardDeck = root.findViewById<Swiper>(R.id.cardDeck)
        val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(100)) { uris ->
            // Callback is invoked after the user selects media items or closes the
            // photo picker.
            if (uris.isNotEmpty()) {
                selectedImage = uris.map {
                    SwiperData(it)
                }
                cardDeck.addData(selectedImage)
                cardDeck.currentIndex = 0
                textRemainingPics.text = cardDeck.size.toString()
            }
        }
        cardDeck.listener = this

        // Ecouter le bouton pour charger l'image
        val pickButton : ImageButton = root.findViewById(R.id.pickPhoto)
        pickButton.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }

        val revertButton = root.findViewById<Button>(R.id.revert)
        revertButton.setOnClickListener { cardDeck.revertSwipedCard() }

        // Use this to retrieve the current top Card Data (Uri)
        // getCurrentData()

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

        val deleteButton = root.findViewById<ImageView>(R.id.delete_button)
        deleteButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToBinFragment(cardDeck.deletedImages.toTypedArray())
            findNavController().navigate(action)
        }

        return root
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

        // Convertissez la latitude et la longitude en une ville
        val location = getLocationFromExif(filePath)
        //val location = exif.getAttribute(ExifInterface.TAG_GPS_AREA_INFORMATION)

        // Créez une boîte de dialogue pour afficher les informations de l'image
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Image information")
            .setMessage("Date and hours : $dateTime\nPlace : $location\n")
            .setPositiveButton("OK", null)
            .create()
        dialog.show()
    }


    fun getLocationFromExif(filePath: String): String? {
        try {
            val exif = ExifInterface(filePath)
            val latLong = FloatArray(2)
            val hasLatLong = exif.getLatLong(latLong)
            if (hasLatLong) {
                val location = Location("")
                location.latitude = latLong[0].toDouble()
                location.longitude = latLong[1].toDouble()


                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses!!.size > 0) {
                    val address = addresses[0]
                    return address.locality
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "No place found"
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

    override fun onCardDeckChanged() {
        // Mettre à jour la vue en conséquence
        textRemainingPics.text = (selectedImage.size - cardDeck.currentIndex).toString()
    }
}