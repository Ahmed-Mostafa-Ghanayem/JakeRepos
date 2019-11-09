package com.github.jake.repos.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "repository")
data class Repo(
    @PrimaryKey @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("language") val language: String,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("forks_count") val forks: Int,
    @SerializedName("size") val size: Int
)

fun Repo.getSizeAndType(): String {
    val mb = 1024
    val gb = 1024 * 1024

    return when {
        size >= gb -> "${size / gb.toDouble()} GB"
        size >= mb -> "${size / mb.toDouble()} MB"
        else -> "${size.toDouble()} KB"
    }
}