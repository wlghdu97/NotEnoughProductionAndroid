package com.xhlab.nep.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.TypedValue
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.xhlab.nep.R
import java.io.File

inline fun <reified T : ViewModel> Fragment.viewModelProvider(
    viewModelFactory: ViewModelProvider.Factory
): T {
    return ViewModelProvider(this, viewModelFactory).get(T::class.java)
}

inline fun <reified T : ViewModel> FragmentActivity.viewModelProvider(
    viewModelFactory: ViewModelProvider.Factory
): T {
    return ViewModelProvider(this, viewModelFactory).get(T::class.java)
}

fun updateGlobalTheme(theme: Theme) {
    val previousTheme = AppCompatDelegate.getDefaultNightMode()
    val targetTheme = when (theme) {
        Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        Theme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        Theme.BATTERY_SAVE -> AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
    }
    if (previousTheme != targetTheme) {
        AppCompatDelegate.setDefaultNightMode(targetTheme)
    }
}

fun Context.formatString(@StringRes formatStringId: Int, vararg args: Any?): String {
    return String.format(getString(formatStringId), *args)
}

fun Context.getIconsFile(fileName: String): File {
    return File(getExternalFilesDir("icons"), fileName)
}

fun Context.getIcon(unlocalizedName: String): Drawable? {
    val encodedName = Base64.encodeToString(unlocalizedName.toByteArray(), Base64.NO_WRAP)
    return Drawable.createFromPath(getIconsFile("${encodedName}.png").path)
}

fun Context.getCardBackgroundColor(): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(R.attr.colorSurface, typedValue, true)
    return ColorUtils.compositeColors(Color.parseColor("#09FFFFFF"), typedValue.data)
}

fun ImageView.setIcon(unlocalizedName: String) {
    val requestOptions = RequestOptions.bitmapTransform(RoundedCorners(16))
    Glide.with(context)
        .load(context.getIcon(unlocalizedName))
        .apply(requestOptions)
        .into(this)
}

inline fun <reified T> LiveData<T?>.observeNotNull(
    lifecycleOwner: LifecycleOwner,
    crossinline block: (T) -> Unit
) = observe(lifecycleOwner) {
    if (it != null) { block(it) }
}

fun Fragment.longToast(@StringRes stringId: Int) {
    Toast.makeText(context, stringId, Toast.LENGTH_LONG).show()
}

fun Context.dip(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).toInt()
}
