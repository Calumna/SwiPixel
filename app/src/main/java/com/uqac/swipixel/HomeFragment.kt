package com.uqac.swipixel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI

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
}