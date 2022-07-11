package com.project.photoedit.aboutus



import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.TextPaint
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.ui.AppBarConfiguration
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.magicgoop.tagsphere.OnTagTapListener
import com.magicgoop.tagsphere.item.TagItem
import com.magicgoop.tagsphere.item.TextTagItem
import com.project.photoedit.R
import com.project.photoedit.databinding.ActivityAboutusBinding


class aboutus : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityAboutusBinding

    private fun initTagView() {
        binding.tagSphereView.setTextPaint(
            TextPaint().apply {
                isAntiAlias = true
                textSize = resources.getDimension(R.dimen.tag_text_size)
                color = Color.DKGRAY
            }
        )
        var i =0
        val loremSize = CreatorNames.list.size
        (0..loremSize-1).map {
            TextTagItem(text = CreatorNames.list[i++])

        }.toList().let {
            binding.tagSphereView.addTagList(it)
        }
        binding.tagSphereView.setRadius(3f)
        binding.tagSphereView.setOnTagTapListener(object : OnTagTapListener {
            override fun onTap(tagItem: TagItem) {
                Toast.makeText(
                    context,
                    "On tap: ${(tagItem as TextTagItem).text}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
binding.tagSphereView.setOnTagTapListener(onTagTapListener = null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)


        binding = ActivityAboutusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)




        initTagView()
        when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                val paint = Paint()
                paint.setColor(Color.WHITE)
                val textPaint = TextPaint(paint)
                textPaint.textSize= 50F
                binding.tagSphereView.setTextPaint(TextPaint(textPaint))
                binding.tagSphereView.setRadius(3f)


            }
            Configuration.UI_MODE_NIGHT_NO -> {


            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }



    }



    override fun onResume() {
        super.onResume()
        binding.tagSphereView.startAutoRotation(-1f, 1f)
    }

    override fun onPause() {
        super.onPause()
        binding.tagSphereView.stopAutoRotation()
    }
}


