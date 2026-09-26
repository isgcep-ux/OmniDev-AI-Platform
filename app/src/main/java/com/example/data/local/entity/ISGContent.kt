package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Room Database entity for 'ISGContent' to store occupational health and safety
 * (İş Sağlığı ve Güvenliği) educational lessons.
 *
 * @param id Unique identifier for the lesson (auto-generated primary key).
 * @param title Title of the health and safety lesson / topic.
 * @param category Category of the lesson (e.g., "Mevzuat", "Risk Değerlendirmesi", "İlkyardım", "Yangın Güvenliği", "Ergonomi").
 * @param content Full textual content containing the health and safety explanation and key principles.
 * @param timestamp Time of creation or last modification.
 */
@Entity(tableName = "isg_contents")
data class ISGContent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Secondary constructor supporting direct textualContent naming.
     */
    @Ignore
    constructor(
        id: Long = 0,
        title: String,
        category: String,
        textualContent: String
    ) : this(
        id = id,
        title = title,
        category = category,
        content = textualContent,
        timestamp = System.currentTimeMillis()
    )

    /**
     * Accessor alias for the textual content field.
     */
    @get:Ignore
    val textualContent: String
        get() = content
}
