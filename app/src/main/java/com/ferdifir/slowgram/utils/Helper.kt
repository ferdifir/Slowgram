package com.ferdifir.slowgram.utils

import android.animation.ObjectAnimator
import android.app.Application
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ferdifir.slowgram.BuildConfig
import com.ferdifir.slowgram.R
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Helper {

    fun isPermissionGranted(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    private fun getCurrentDate(): Date { return Date() }
    private const val timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    private fun parseUTCDate(timestamp: String): Date {
        return try {
            val formatter = SimpleDateFormat(timestampFormat, Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            formatter.parse(timestamp) as Date
        } catch (e: ParseException) {
            getCurrentDate()
        }
    }

    fun getUploadStoryTime(timestamp: String): String {
        val date: Date = parseUTCDate(timestamp)
        return getSimpleDate(date)
    }

    private const val simpleDateFormat = "dd MMM yyyy HH.mm"
    private val simpleDate = SimpleDateFormat(simpleDateFormat, Locale.getDefault())
    private fun getSimpleDate(date: Date): String = simpleDate.format(date)

    fun getTimelineUpload(context: Context, timestamp: String): String {
        val currentTime = getCurrentDate()
        val uploadTime = parseUTCDate(timestamp)
        val diff: Long = currentTime.time - uploadTime.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val label = when (minutes.toInt()) {
            0 -> "$seconds ${context.getString(R.string.const_text_seconds_ago)}"
            in 1..59 -> "$minutes ${context.getString(R.string.const_text_minutes_ago)}"
            in 60..1440 -> "$hours ${context.getString(R.string.const_text_hours_ago)}"
            else -> "$days ${context.getString(R.string.const_text_days_ago)}"
        }
        return label
    }

    fun parseAddressLocation(
        context: Context,
        lat: Double,
        lon: Double
    ): String {
        var address = ""
        try {
            val geocoder = Geocoder(context)
            val geoLocation =
                geocoder.getFromLocation(lat, lon, 1)
            if (geoLocation != null) {
                address = if (geoLocation.size > 0) {
                    val location = geoLocation[0]
                    val fullAddress = location.getAddressLine(0)
                    StringBuilder("📌 ")
                        .append(fullAddress).toString()
                } else {
                    "📌 Location Unknown"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return address
    }

    fun View.animateVisibility(isVisible: Boolean, duration: Long = 400) {
        ObjectAnimator
            .ofFloat(this, View.ALPHA, if (isVisible) 1f else 0f)
            .setDuration(duration)
            .start()
    }

    fun TextView.setLocalDateFormat(timestamp: String) {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val date = sdf.parse(timestamp) as Date

        val formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date)
        this.text = formattedDate
    }

    fun notifyGivePermission(context: Context, message: String) {
        val dialog = dialogInfoBuilder(context, message)
        val button = dialog.findViewById<Button>(R.id.button_ok)
        button.setOnClickListener {
            dialog.dismiss()
            openSettingPermission(context)
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun openSettingPermission(context: Context) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", context.packageName, null)
        context.startActivity(intent)
    }

    private fun dialogInfoBuilder(
        context: Context,
        message: String,
        alignment: Int = Gravity.CENTER
    ): Dialog {
        val dialog = Dialog(context)
        dialog.setCancelable(false)
        dialog.window!!.apply {
            val params: WindowManager.LayoutParams = this.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            attributes.windowAnimations = android.R.transition.fade
            setGravity(Gravity.CENTER)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.setContentView(R.layout.custom_dialog_info)
        val tvMessage = dialog.findViewById<TextView>(R.id.message)
        when (alignment) {
            Gravity.CENTER -> tvMessage.gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER
            Gravity.START -> tvMessage.gravity = Gravity.CENTER_VERTICAL or Gravity.START
            Gravity.END -> tvMessage.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        }
        tvMessage.text = message
        return dialog
    }

    fun bitmapFromURL(context: Context, urlString: String): Bitmap {
        return try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val url = URL(urlString)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            BitmapFactory.decodeResource(context.resources, R.drawable.logo_dicoding)
        }
    }

    fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
        val matrix = Matrix()
        return if (isBackCamera) {
            matrix.postRotate(90f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        } else {
            matrix.postRotate(-90f)
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
        }
    }

    private const val FILENAME_FORMAT = "dd-MMM-yyyy"
    private val timeStamp: String = SimpleDateFormat(
        FILENAME_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    fun createFile(application: Application): File {
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
            File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        val outputDirectory = if (
            mediaDir != null && mediaDir.exists()
        ) mediaDir else application.filesDir

        return File(outputDirectory, "$timeStamp.jpg")
    }

    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)
        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()
        return myFile
    }

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(currentTimestamp, ".jpg", storageDir)
    }

    private val currentTimestamp: String = SimpleDateFormat(
        "ddMMyyHHmmssSS",
        Locale.getDefault()
    ).format(System.currentTimeMillis())

    fun reduceFileSize(myFile: File): File {
        val MAX_IMAGE_SIZE = 1024000
        if (myFile.length() > MAX_IMAGE_SIZE) {
            var streamLength = MAX_IMAGE_SIZE
            var compressQuality = 105
            val bmpStream = ByteArrayOutputStream()
            while (streamLength >= MAX_IMAGE_SIZE && compressQuality > 5) {
                bmpStream.use {
                    it.flush()
                    it.reset()
                }

                compressQuality -= 5
                val bitmap = BitmapFactory.decodeFile(myFile.absolutePath, BitmapFactory.Options())
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                val bmpPicByteArray = bmpStream.toByteArray()
                streamLength = bmpPicByteArray.size
                if (BuildConfig.DEBUG) {
                    Log.d("test upload", "Quality: $compressQuality")
                    Log.d("test upload", "Size: $streamLength")
                }
            }

            FileOutputStream(myFile).use {
                it.write(bmpStream.toByteArray())
            }
        }
        return myFile
    }
}