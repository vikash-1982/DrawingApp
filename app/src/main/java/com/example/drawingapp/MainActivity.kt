package com.example.drawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get


class MainActivity : AppCompatActivity() {
    val openGalleryLauncher:ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
            result->
            if(result.resultCode== RESULT_OK && result.data !=null)
            {
                val imageBackground:ImageView=findViewById(R.id.backgrondImage)
                imageBackground.setImageURI(result.data?.data)
            }
        }
    private val readPermission:ActivityResultLauncher <Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
          permission ->
            permission.entries.forEach {
                val perMissionName=it.key
                val isGranted=it.value
                if(isGranted)
                {
                      Toast.makeText(this@MainActivity,
                          "permission granted you can read external memory",Toast.LENGTH_LONG).show()
                    val pickIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(pickIntent)
                }
                else
                {
                    if (perMissionName == Manifest.permission.READ_EXTERNAL_STORAGE)
                        Toast.makeText(
                            this@MainActivity,
                            "Oops you just denied the permission.",
                            Toast.LENGTH_LONG
                        ).show()
                }
            }
        }
    private var drawingView:DrawingView?=null
    private var mImageButtonCurrentPaint:ImageButton?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView=findViewById(R.id.drawing_view)
        val linearlayout=findViewById<LinearLayout>(R.id.color_layout)
        mImageButtonCurrentPaint=linearlayout[3] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
        )
        val ib_brush:ImageButton=findViewById(R.id.brush)
        ib_brush.setOnClickListener {
            showBrushSizeChooserDialog()
        }
        val ib_gallery:ImageButton=findViewById(R.id.galery)
        ib_gallery.setOnClickListener {
           requeststoragePermission()
        }
        val ib_undo:ImageButton=findViewById(R.id.undo)
        ib_undo.setOnClickListener {
           drawingView?.onClickUndo()
        }

    }
    private fun showBrushSizeChooserDialog(){
        var brushDialog= Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("BrushSize")
        val smallBtn=brushDialog.findViewById<ImageButton>(R.id.ib_small_brush)
        val mediumBtn=brushDialog.findViewById<ImageButton>(R.id.ib_medium_brush)
        val largeBtn=brushDialog.findViewById<ImageButton>(R.id.ib_large_brush)

        smallBtn.setOnClickListener {
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }
        mediumBtn.setOnClickListener {
            drawingView?.setSizeForBrush(15.toFloat())
            brushDialog.dismiss()
        }
        largeBtn.setOnClickListener {
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
    }
    fun paintClicked(view: View){
       if(view!==mImageButtonCurrentPaint)
       {
           val imageButton=view as ImageButton
           val colortag=imageButton.tag.toString()
           drawingView?.setcolor(colortag)
           imageButton.setImageDrawable(
               ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
           )
           mImageButtonCurrentPaint?.setImageDrawable(
               ContextCompat.getDrawable(this,R.drawable.pallet_normal)
           )
           mImageButtonCurrentPaint=view
       }
    }
    private fun requeststoragePermission()
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            showRationaleDialog("Kids Drawing App" ,"Kids Drawing App"+"needs to Access Your External Storage")
        }
        else
        {
            readPermission.launch(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            )
        }
    }
    private fun showRationaleDialog(
        title: String,
        message: String,
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

}