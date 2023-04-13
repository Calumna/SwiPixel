package com.uqac.swipixel

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BinFragment : Fragment(R.layout.fragment_bin){

    private lateinit var bin : RecycleBinAdaptater

    private val args: BinFragmentArgs by navArgs()

    // Liste d'images dans la corbeille
    var deleteImages: List<SwiperData> = ArrayList<SwiperData>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bin, container, false)
        deleteImages = getDeletedImages()
        // Define the RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // Set the RecyclerView's LayoutManager
        recyclerView.layoutManager = GridLayoutManager(activity, 2)

        // Set the RecyclerView's Adapter
        bin = RecycleBinAdaptater(deleteImages)

        // Set button to clearBin
        val deleteButton: Button = view.findViewById(R.id.clear_bin)

        // Add OnClickListener to delete button
        deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Vider la corbeille")
            builder.setMessage("Êtes-vous sûr de vouloir vider la corbeille ?")

            // Ajouter le bouton "Oui"
            // Supprimer les images de la corbeille et de l'appareil
            builder.setPositiveButton("Oui") { dialog, which ->
                clearBin()
            }
            // Ajouter le bouton "Non"
            builder.setNegativeButton("Non") { dialog, which ->
                // Ne rien faire
            }

            // Afficher la boîte de dialogue
            val dialog = builder.create()
            dialog.show()
        }

        return view
    }

    private fun getDeletedImages(): List<SwiperData> {
        // Ajoutez chaque image à la liste imageItemList
        return args.deletedImages.toMutableList()
    }

    private fun clearBin() {
        // Supprimez les images de la corbeille
//        deleteImages = getDeletedImages()
        //deleteImages.clear()

        // Supprimez les images de l'appareil de l'utilisateur
//        for (image in args.deletedImages) {
//            val file = File(image.imagePath)
//            if (file.exists()) {
//                file.delete()
//            }
//        }
        // Mettez à jour le RecyclerView
        bin.notifyDataSetChanged()

        //toast et retour au fragment home
        Toast.makeText(context, "La corbeille a été vidé", Toast.LENGTH_SHORT).show()
    }

}