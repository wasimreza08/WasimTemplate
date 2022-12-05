package com.example.core.ext

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.DrawableRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.core.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun RecyclerView.doNotLeak(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.lifecycle.addObserver(RecyclerviewEventObserver(this))
}

internal class RecyclerviewEventObserver(private val recyclerView: RecyclerView) :
    LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            recyclerView.adapter = null
        }
    }
}

fun RecyclerView.asVerticalLayout(reverse: Boolean = false) {
    this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, reverse)
}

fun View.showNetworkError() {
    Snackbar.make(this, R.string.network_error, BaseTransientBottomBar.LENGTH_LONG).show()
}

fun View.showUnknownError() {
    Snackbar.make(this, R.string.unknown_error, BaseTransientBottomBar.LENGTH_LONG).show()
}

fun View.uiVisibility(isVisible: Boolean = true) {
    if (isVisible) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun ImageView.loadImage(
    url: String,
    progress: ProgressBar? = null,
    @DrawableRes errorImage: Int = R.drawable.ic_broken_image,
    cornerRadius: Int = 0,
    isCircular: Boolean = false,
) {
    val requestOption: RequestOptions = if (isCircular) {
        RequestOptions.circleCropTransform()
    } else if (cornerRadius > 0) {
        RequestOptions().transform(CenterCrop(), RoundedCorners(cornerRadius))
    } else {
        RequestOptions()
    }

    Glide
        .with(this.context)
        .load(url)
        .centerCrop()
        .listener(
            object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    progress?.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    progress?.visibility = View.GONE
                    return false
                }
            }
        ).apply(requestOption)
        .error(errorImage)
        .into(this)
}
