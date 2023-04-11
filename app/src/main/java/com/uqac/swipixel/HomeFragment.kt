package com.uqac.swipixel

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var cardDeck: RecyclerView

    // variable pour afficher une photo de la galerie
    var selectedImage: List<SwiperData> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        cardDeck = root.findViewById<RecyclerView>(R.id.cardDeck);

        val adapter = SwiperAdapter()

        // vérifier l'autorisation d'accès aux fichiers
//        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
//            PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
//        } else {
            val pickMultipleMedia =
                registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(100)) { uris ->
                    // Callback is invoked after the user selects media items or closes the
                    // photo picker.
                    if (uris.isNotEmpty()) {
                        selectedImage = uris.map {
                            SwiperData(it)
                        }
                        adapter.addData(selectedImage)
                    }
                }
            // Ecouter le bouton pour charger l'image
            val pickButton = root.findViewById<Button>(R.id.button)
            pickButton.setOnClickListener { pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
//        }

        cardDeck = root.findViewById(R.id.cardDeck);

        cardDeck.adapter = adapter
        cardDeck.layoutManager =  SwiperLayout(3)
        return root;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

}