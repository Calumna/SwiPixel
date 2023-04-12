package com.uqac.swipixel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BinFragment : Fragment(R.layout.fragment_bin){

    private lateinit var bin : RecycleBinAdaptater

    val args: BinFragmentArgs by navArgs()

    // Liste d'images dans la corbeille
    var deleteImages: List<SwiperData> = ArrayList<SwiperData>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bin, container, false)
        deleteImages = getDeletedImages()
        // Define the RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // Set the RecyclerView's LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // Set the RecyclerView's Adapter
        recyclerView.adapter = RecycleBinAdaptater(deleteImages)

        return view
    }

    private fun getDeletedImages(): List<SwiperData> {
        // Récupérez la liste des images supprimées et créez une liste d'objets ImageItem
        //val imageItemList = mutableListOf<SwiperData>()
        // Ajoutez chaque image à la liste imageItemList
        return args.deletedImages.toMutableList()
    }
}