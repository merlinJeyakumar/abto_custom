package com.support.utills

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import com.support.utills.MimeTypeUtil.NotificationName.NOTIFICATION_NAME_AUDIO
import com.support.utills.MimeTypeUtil.NotificationName.NOTIFICATION_NAME_DOCUMENT
import com.support.utills.MimeTypeUtil.NotificationName.NOTIFICATION_NAME_IMAGE
import com.support.utills.MimeTypeUtil.NotificationName.NOTIFICATION_NAME_VIDEO
import com.support.utills.MimeTypeUtil.NotificationSymbol.NOTIFICATION_SYMBOL_AUDIO
import com.support.utills.MimeTypeUtil.NotificationSymbol.NOTIFICATION_SYMBOL_CONTACT
import com.support.utills.MimeTypeUtil.NotificationSymbol.NOTIFICATION_SYMBOL_DOCUMENT
import com.support.utills.MimeTypeUtil.NotificationSymbol.NOTIFICATION_SYMBOL_GIFT_CARD
import com.support.utills.MimeTypeUtil.NotificationSymbol.NOTIFICATION_SYMBOL_GREETING_CARD
import com.support.utills.MimeTypeUtil.NotificationSymbol.NOTIFICATION_SYMBOL_IMAGE
import com.support.utills.MimeTypeUtil.NotificationSymbol.NOTIFICATION_SYMBOL_LOCATION
import com.support.utills.MimeTypeUtil.NotificationSymbol.NOTIFICATION_SYMBOL_LOVE_QUOTES
import com.support.utills.MimeTypeUtil.NotificationSymbol.NOTIFICATION_SYMBOL_VIDEO
import java.io.File
import java.util.regex.Pattern


object MimeTypeUtil {


    object NotificationSymbol {
        val NOTIFICATION_SYMBOL_IMAGE = "\uD83D\uDCF7"
        val NOTIFICATION_SYMBOL_VIDEO = "\uD83D\uDCF9 "
        val NOTIFICATION_SYMBOL_LOCATION = "\uD83D\uDCCD"
        val NOTIFICATION_SYMBOL_AUDIO = "\uD83C\uDF99️"
        val NOTIFICATION_SYMBOL_DOCUMENT = "\uD83D\uDCC1"
        val NOTIFICATION_SYMBOL_CONTACT = "\uD83D\uDC64"
        val NOTIFICATION_SYMBOL_LOVE_QUOTES = "\uD83D\uDC8C"
        val NOTIFICATION_SYMBOL_GREETING_CARD = "✉"
        val NOTIFICATION_SYMBOL_GIFT_CARD = "\uD83C\uDF81"
    }

    object NotificationName {
        val NOTIFICATION_NAME_IMAGE = "Image"
        val NOTIFICATION_NAME_VIDEO = "Video"
        val NOTIFICATION_NAME_LOCATION = "Location"
        val NOTIFICATION_NAME_AUDIO = "Audio"
        val NOTIFICATION_NAME_DOCUMENT = "File"
        val NOTIFICATION_NAME_CONTACT = "Contact"
        val NOTIFICATION_NAME_LOVE_QUOTES = "Love Quotes"
        val NOTIFICATION_NAME_GREETING_CARD = "Greeting Card"
        val NOTIFICATION_NAME_GIFT_CARD = "Gift Box"
    }

    fun getFileTypeOrBody(message: String): String {


        if (message.contains(".jpeg") || message.contains(".jpg") || message.contains(".png")||message.equals("image"))
            return "$NOTIFICATION_SYMBOL_IMAGE $NOTIFICATION_NAME_IMAGE"
        else if (message.contains(".mp4") || message.contains(".mkv") || message.contains(".mov") || message.contains(".3gp")||message.equals("video",true) || message.contains(".mov")) {
            return "$NOTIFICATION_SYMBOL_VIDEO $NOTIFICATION_NAME_VIDEO"
        } else if (message.contains(".aac3") || message.contains(".m4a")||message.equals("audio",true)) {
            return "$NOTIFICATION_SYMBOL_AUDIO $NOTIFICATION_NAME_AUDIO"
        } else if (message.contains(".pdf") || message.contains(".doc") || message.contains(".ppt") || message.contains(".xls") || message.contains(".txt")) {
            return "$NOTIFICATION_SYMBOL_DOCUMENT $NOTIFICATION_NAME_DOCUMENT"
        } else if (message.equals("GiftNote", true)) {
            return "$NOTIFICATION_SYMBOL_GIFT_CARD ${NotificationName.NOTIFICATION_NAME_GREETING_CARD}"
        }  else if (message.equals("LoveQuotes", true)) {
            return "$NOTIFICATION_SYMBOL_LOVE_QUOTES ${NotificationName.NOTIFICATION_NAME_LOVE_QUOTES}"
        } else if (message.matches(Pattern.compile("^(\\-?\\d+(\\.\\d+)?),\\s*(\\-?\\d+(\\.\\d+)?)\$").toRegex())) {
            return "$NOTIFICATION_SYMBOL_LOCATION ${NotificationName.NOTIFICATION_NAME_LOCATION}"
        } else {
            return message
        }
    }


    fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }



    fun getBodyMessageType(body: String, subject: String): String {



        when(subject){

            "Text" ->{
                return body
            }

            "GiftBox" ->{
                return "$NOTIFICATION_SYMBOL_GIFT_CARD ${NotificationName.NOTIFICATION_NAME_GIFT_CARD}"
            }

            "LoveQuotes" ->{
                return "$NOTIFICATION_SYMBOL_LOVE_QUOTES ${NotificationName.NOTIFICATION_NAME_LOVE_QUOTES}"
            }

            "Image" ->{
                return "$NOTIFICATION_SYMBOL_IMAGE $NOTIFICATION_NAME_IMAGE"
            }

            "Video" ->{
                return "$NOTIFICATION_SYMBOL_VIDEO $NOTIFICATION_NAME_VIDEO"
            }

            "AnimatedGreetingCard","StaticGreetingCard" ->{
                return "$NOTIFICATION_SYMBOL_GREETING_CARD ${NotificationName.NOTIFICATION_NAME_GREETING_CARD}"
            }

            "Contact" ->{
                return "$NOTIFICATION_SYMBOL_CONTACT ${NotificationName.NOTIFICATION_NAME_CONTACT}"
            }

            "File" ->{
                return "$NOTIFICATION_SYMBOL_DOCUMENT $NOTIFICATION_NAME_DOCUMENT"
            }

            "Location" ->{
                return "$NOTIFICATION_SYMBOL_LOCATION ${NotificationName.NOTIFICATION_NAME_LOCATION}"
            }


        }



        if (body.matches(Pattern.compile("^(\\-?\\d+(\\.\\d+)?),\\s*(\\-?\\d+(\\.\\d+)?)\$").toRegex())) {
            return subject
        } else if (URLUtil.isValidUrl(body)) {
            return subject
        } else {
            return body
        }
    }

    fun getMimeType(context: Context, uri: Uri): String? {
        var mimeType: String?
        //Check uri format to avoid null
        mimeType =
            if (uri.scheme != null && uri.scheme!!.equals(ContentResolver.SCHEME_CONTENT)) { //If scheme is a content
                //val mime = MimeTypeMap.getSingleton()
                //mime.getExtensionFromMimeType(context.getContentResolver().getType(uri))
                context.getContentResolver().getType(uri)
            } else { //If scheme is a File
                //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
                val extension =
                    MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.getPath())).toString())
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }

        if (mimeType.isNullOrEmpty()) {
            mimeType = uri.getQueryParameter("mimeType")
        }
        return mimeType
    }


}