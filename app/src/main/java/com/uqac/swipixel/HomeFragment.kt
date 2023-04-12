package com.uqac.swipixel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var cardDeck: Swiper

    // variable pour afficher une photo de la galerie
    var selectedImage: List<SwiperData> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        cardDeck = root.findViewById<Swiper>(R.id.cardDeck);
        val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            // Callback is invoked after the user selects media items or closes the
            // photo picker.
            if (uris.isNotEmpty()) {
                selectedImage = uris.map {
                    SwiperData(it)
                }
                cardDeck.addData(selectedImage)
            }
        }

        // Ecouter le bouton pour charger l'image
        val pickButton = root.findViewById<Button>(R.id.button)
        pickButton.setOnClickListener { pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }

        val revertButton = root.findViewById<Button>(R.id.revert)
        revertButton.setOnClickListener { cardDeck.revertSwipedCard() }

        // Use this to retrieve the current top Card Data (Uri)
        // getCurrentData()

        return root;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

}