package com.uqac.swipixel

import android.app.AlertDialog
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BinFragment : Fragment(R.layout.fragment_bin){

    private lateinit var bin : RecycleBinAdaptater

    private val args: BinFragmentArgs by navArgs()

    // Liste d'images dans la corbeille
    var deletedImages: MutableList<SwiperData> = ArrayList()

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intentSenderLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {}
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bin, container, false)
        deletedImages = retreiveDeletedImages()
        Log.d("Bin","deletedImages : " + deletedImages.size.toString())
        // Define the RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // Set the RecyclerView's LayoutManager
        recyclerView.layoutManager = GridLayoutManager(activity, 2)

        // Set the RecyclerView's Adapter
        bin = RecycleBinAdaptater(deletedImages)
        recyclerView.adapter = bin

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

    private fun retreiveDeletedImages(): MutableList<SwiperData> {
        return args.deletedImages.toMutableList()
    }

    private fun clearBin() {
        // Supprimer les images de l'appareil de l'utilisateur
        val uris = deletedImages.map {
            val mediaUri: Uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            val idString: String = it.image.lastPathSegment ?: return
            val id: Long = idString.substringAfter("image:").toLongOrNull() ?: return
            ContentUris.withAppendedId(mediaUri, id)
        }
        for (uri in uris) {
            deletePhoto(uri)
        }
        val size = deletedImages.size
        deletedImages.clear()
        bin.notifyItemRangeRemoved(0,size)
    }

    private fun deletePhoto (uri: Uri) {
        val contentResolver = context?.contentResolver
        try {
            contentResolver?.delete(uri, null, null)
        } catch (e: SecurityException) {
            val intentSender = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                    if (contentResolver != null) {
                        MediaStore.createDeleteRequest(
                            contentResolver,
                            listOf(uri)
                        ).intentSender
                    } else {
                        null
                    }
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    val recoverableSecurityException = e as? RecoverableSecurityException
                    recoverableSecurityException?.userAction?.actionIntent?.intentSender
                }
                else -> null
            }
            intentSender?.let { sender ->
                intentSenderLauncher.launch(
                    IntentSenderRequest.Builder(sender).build()
                )
            }
        }
    }

}