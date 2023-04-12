package com.uqac.swipixel

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deletedImages = getDeletedImages()

        bin = RecycleBinAdaptater(deletedImages)
//        recyclerView.adapter = imageAdapter
//        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getDeletedImages(): List<SwiperData> {
        // Récupérez la liste des images supprimées et créez une liste d'objets ImageItem
        //val imageItemList = mutableListOf<SwiperData>()
        // Ajoutez chaque image à la liste imageItemList
        return args.deletedImages.toMutableList()
    }
}