package com.project.photoedit

import android.Manifest
import android.app.Activity
import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.slider.Slider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.gun0912.tedpermission.provider.TedPermissionProvider
import com.magicgoop.tagsphere.OnTagTapListener
import com.magicgoop.tagsphere.item.TagItem
import com.magicgoop.tagsphere.item.TextTagItem
import com.project.photoedit.aboutus.CreatorNames
import com.project.photoedit.aboutus.aboutus
import com.project.photoedit.databinding.FragmentFirstBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class FirstFragment : Fragment() {

    lateinit var imageView: ImageView
    lateinit var slider_bright: Slider
    lateinit var slider_contrast: Slider
    lateinit var slider_saturation: Slider
    lateinit var imageuri: Uri

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
//                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(
                    context,
                    "Permission Denied\n$deniedPermissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        activity?.let {
            ActivityCompat.requestPermissions(
                it, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ),
                1
            )
        }

        //check all needed permissions together

        //check all needed permissions together

        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
            .check()



        binding.btnSavefile.isEnabled = false
        binding.btnSetwallpaper.isEnabled=false
//        binding.btnSavefile.isVisible=false
        binding.imageView.isVisible=false
        binding.tvBrightness.isVisible = false
        binding.tvContrast.isVisible = false
        binding.tvSaturation.isVisible = false
        binding.sliderBrightness.isVisible = false
        binding.sliderContrast.isVisible = false
        binding.sliderSaturation.isVisible = false



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }

        imageView = binding.imageView
        slider_bright = binding.sliderBrightness
        slider_contrast = binding.sliderContrast
        slider_saturation = binding.sliderSaturation
        val btn_addfile: ExtendedFloatingActionButton = binding.btnAddfile
        val btn_savefile: ExtendedFloatingActionButton = binding.btnSavefile
        val btn_aboutus:ExtendedFloatingActionButton = binding.btnAboutus
        val btn_setwall:ExtendedFloatingActionButton = binding.btnSetwallpaper


        btn_addfile.setOnClickListener(View.OnClickListener {
            openGalleryForImage()
        })
        btn_savefile.setOnClickListener(View.OnClickListener {
            val save_bitmap: Bitmap = getbitmap(imageuri)
            saveMediaToStorage(save_bitmap)
        })
        btn_aboutus.setOnClickListener(View.OnClickListener {
            val intent=Intent(this@FirstFragment.context,aboutus::class.java)
            startActivity(intent)
        })
        btn_setwall.setOnClickListener(View.OnClickListener {
            val save_bitmap: Bitmap = getbitmap(imageuri)
            setWallpaper(save_bitmap)
        })



        slider_bright.addOnChangeListener(onchangeebrightnessListener)
        slider_contrast.addOnChangeListener(onchangeecontrastListener)
        slider_saturation.addOnChangeListener(onchangeesaturatuinListener)


        initTagView()
        when (TedPermissionProvider.context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                val paint = Paint()
                paint.setColor(Color.WHITE)
                val textPaint = TextPaint(paint)
                textPaint.textSize= 50F
                binding.tagSphereViewInfrag1.setTextPaint(TextPaint(textPaint))
                binding.tagSphereViewInfrag1.setRadius(3f)


            }
            Configuration.UI_MODE_NIGHT_NO -> {


            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val REQUEST_CODE = 100

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            imageView.setImageURI(data?.data) // handle chosen image
            imageuri = data?.data!!

            binding.btnSavefile.isEnabled = true

            binding.tagSphereViewInfrag1.isVisible=false
            binding.tagSphereViewInfrag1.isEnabled=false
            binding.btnSetwallpaper.isEnabled=true
            binding.imageView.isVisible=true
            binding.tvBrightness.isVisible = true
            binding.tvContrast.isVisible = true
            binding.tvSaturation.isVisible = true
            binding.sliderBrightness.isVisible = true
            binding.sliderContrast.isVisible = true
            binding.sliderSaturation.isVisible = true
            slider_saturation.value = 0f
            slider_bright.value = 0f
            slider_contrast.value = 0f
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun getbitmap(uri: Uri): Bitmap {

        val orginal_bitmap: Bitmap =
            MediaStore.Images.Media.getBitmap(context!!.contentResolver, uri)
        val bitmap = Bitmap.createBitmap(
            orginal_bitmap.width,
            orginal_bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        val paint = Paint()
        paint.colorFilter = ColorFilterGenerator.adjustColor(
            slider_bright.value.toInt(),
            slider_contrast.value.toInt(),
            slider_saturation.value.toInt(),
            0
        )
        canvas.drawBitmap(orginal_bitmap, 0f, 0f, paint)

        return bitmap
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        //Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            context?.contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(context, "Saved to Photos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setWallpaper(bitmap: Bitmap) {
        //Generating a file name
        val wallpaperManager = WallpaperManager.getInstance(context)
        wallpaperManager.setBitmap(bitmap)
        Toast.makeText(context,"Wallpaper Set Successfully",Toast.LENGTH_SHORT)
    }


    private val onchangeebrightnessListener: Slider.OnChangeListener =
        object : Slider.OnChangeListener {

            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {

                imageView.colorFilter = ColorFilterGenerator.adjustColor(
                    slider_bright.value.toInt(),
                    slider_contrast.value.toInt(),
                    slider_saturation.value.toInt(),
                    0
                )
            }
        }

    private val onchangeecontrastListener: Slider.OnChangeListener =
        object : Slider.OnChangeListener {

            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {

                imageView.colorFilter = ColorFilterGenerator.adjustColor(
                    slider_bright.value.toInt(),
                    slider_contrast.value.toInt(),
                    slider_saturation.value.toInt(),
                    0
                )
            }
        }

    private val onchangeesaturatuinListener: Slider.OnChangeListener =
        object : Slider.OnChangeListener {

            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {

                imageView.colorFilter = ColorFilterGenerator.adjustColor(
                    slider_bright.value.toInt(),
                    slider_contrast.value.toInt(),
                    slider_saturation.value.toInt(),
                    0
                )
            }
        }
    private fun initTagView() {
        binding.tagSphereViewInfrag1.setTextPaint(
            TextPaint().apply {
                isAntiAlias = true
                textSize = resources.getDimension(R.dimen.tag_text_size)
                color = Color.DKGRAY
            }
        )
        var i =0
        val loremSize = StringPickaFile.list.size
        (0..loremSize-1).map {
            TextTagItem(text = StringPickaFile.list[i++])

        }.toList().let {
            binding.tagSphereViewInfrag1.addTagList(it)
        }
        binding.tagSphereViewInfrag1.setRadius(3f)
        binding.tagSphereViewInfrag1.setOnTagTapListener(object : OnTagTapListener {
            override fun onTap(tagItem: TagItem) {
                Toast.makeText(
                    TedPermissionProvider.context,
                    "On tap: ${(tagItem as TextTagItem).text}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        binding.tagSphereViewInfrag1.setOnTagTapListener(onTagTapListener = null)
    }

    object StringPickaFile {
        val list =
            ". . . . . . . . . . . . . . . . . . . . . . . . PickAnImage . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . "

                .replace("\\s+".toRegex(), " ")
                .split(" ")
    }

    override fun onResume() {
        super.onResume()
        binding.tagSphereViewInfrag1.startAutoRotation(-1f, 1f)
    }

    override fun onPause() {
        super.onPause()
        binding.tagSphereViewInfrag1.stopAutoRotation()
    }


}


