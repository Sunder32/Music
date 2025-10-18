package com.example.musicai.data.model

import com.google.gson.annotations.SerializedName

data class MusicRequest(
    @SerializedName("model")
    val model: String = "gpt-3.5-turbo",
    @SerializedName("messages")
    val messages: List<Message>,
    @SerializedName("max_tokens")
    val maxTokens: Int = 500
)

data class Message(
    @SerializedName("role")
    val role: String,
    @SerializedName("content")
    val content: String
)

data class MusicResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("choices")
    val choices: List<Choice>
)

data class Choice(
    @SerializedName("message")
    val message: Message
)

data class MusicTrack(
    val id: String,
    val title: String,
    val description: String,
    val genre: String,
    val mood: String,
    val duration: String = "3:45",
    val audioUrl: String? = null,
    val audioData: ByteArray? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val coverColor: Int,
    val isGeneratingAudio: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as MusicTrack
        
        if (id != other.id) return false
        if (audioData != null) {
            if (other.audioData == null) return false
            if (!audioData.contentEquals(other.audioData)) return false
        } else if (other.audioData != null) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (audioData?.contentHashCode() ?: 0)
        return result
    }
}
