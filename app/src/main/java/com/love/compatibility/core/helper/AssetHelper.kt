package com.love.compatibility.core.helper

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import com.love.compatibility.core.utils.key.AssetsKey
import com.love.compatibility.core.utils.key.ValueKey

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream


object AssetHelper {
    // Read sub folder
    fun getSubfoldersAsset(context: Context, path: String, withOutDomain: Boolean = false): ArrayList<String> {
        val allData = context.assets.list(path)
        val sortedData = if (!withOutDomain){
            MediaHelper.sortAsset(allData)?.map { "${AssetsKey.ASSET_MANAGER}/$path/$it" }?.toCollection(ArrayList())
        }else{
            MediaHelper.sortAsset(allData)?.map { it }?.toCollection(ArrayList())
        }
        return sortedData ?: arrayListOf()
    }



    // Read sub folder
    fun getSubfoldersNotDomainAsset(context: Context, path: String): ArrayList<String> {
        val allData = context.assets.list(path)
        val sortedData = MediaHelper.sortAsset(allData)?.map { "${AssetsKey.DATA}/$it" }?.toCollection(ArrayList())
        return sortedData ?: arrayListOf()
    }

    // Read file txt -> json -> T
    inline fun <reified T> readJsonAsset(context: Context, path: String): T? {
        return try {
            val json = context.assets.open(path).bufferedReader().use { it.readText() }
            Gson().fromJson(json, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Read file txt -> json -> list
    inline fun <reified T> readTextToJsonAssets(context: Context, path: String): ArrayList<T> {
        return try {
            val json = context.assets.open(path).bufferedReader().use { it.readText() }
            val type = object : TypeToken<ArrayList<T>>() {}.type
            Gson().fromJson(json, type) ?: arrayListOf()
        } catch (e: Exception) {
            e.printStackTrace()
            arrayListOf()
        }
    }

    // Read file -> bitmap
    fun getBitmapFromAsset(context: Context, fileName: String): Bitmap? {
        return try {
            context.assets.open(fileName).use { input ->
                android.graphics.BitmapFactory.decodeStream(input)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // file asset -> internal
    fun copyAssetToInternal(context: Context, fileName: String): File? {
        return try {
            val outFile = File(context.filesDir, fileName)
            outFile.parentFile?.mkdirs()

            if (!outFile.exists()) {
                context.assets.open(fileName).use { input ->
                    FileOutputStream(outFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }

            outFile
        } catch (e: Exception) {
            Log.e("nbhieu", "Copy asset failed: ${e.message}")
            null
        }
    }


    // ---------------------------------------------------------------------------------------------

}