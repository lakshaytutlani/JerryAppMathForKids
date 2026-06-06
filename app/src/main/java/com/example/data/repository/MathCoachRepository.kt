package com.example.data.repository

import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.Part
import com.example.data.api.RetrofitClient
import com.example.data.db.ChatMessage
import com.example.data.db.MathCoachDao
import com.example.data.db.UserProfile
import com.example.BuildConfig
import kotlinx.coroutines.flow.Flow

class MathCoachRepository(private val dao: MathCoachDao) {

    val chatMessages: Flow<List<ChatMessage>> = dao.getAllMessages()
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()

    suspend fun addMessage(message: ChatMessage) {
        dao.insertMessage(message)
    }

    suspend fun clearHistory() {
        dao.clearChatHistory()
    }

    suspend fun updateProfile(profile: UserProfile) {
        dao.saveUserProfile(profile)
    }

    suspend fun askJerry(
        history: List<ChatMessage>,
        userMessage: String,
        gradeLevel: Int,
        userName: String
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return getFallbackResponse(userMessage, gradeLevel)
        }

        val systemInstruction = """
            You are JERRY, the cute, witty brown mouse from 'Tom and Jerry' acting as an expert MATH COACH.
            Your users are kids aged 8 to 12.
            
            Strict Behavior Rules:
            1. Keep the tone EXTREMELY FRIENDLY, PLAYFUL, and supportive. Use kid-friendly analogies (cheese slices, mouse holes, traps, cheese crumbs, cookies, toys).
            2. Never give the final answer right away! ALWAYS provide a clever, helpful cue, hint, or a simple guiding question FIRST, so that the child thinks and solves it.
            3. Never make the user feel bad for not knowing or for getting a wrong answer. Give highly positive rodent-themed encouragement! (e.g. "Nibbling on some cheese takes a few bites, let's try again! That was a super clever try!").
            4. Make math fun! Keep your answers short, highly encouraging, and readable for kids. No overwhelming blocks of text. Use bullet points or playful spacing.
            5. Start your very first response in a chat session with: "Hello Kiddo!" Subsequent responses in the same chat do not have to prepend "Hello Kiddo", but should always refer to the user warmly.
            6. Address kids as 'Kiddo' or their name (provided as: $userName) and relate concepts to cheese, mouse tunnels, mischievous traps, or cozy cartoon fun.
        """.trimIndent()

        val apiContents = mutableListOf<Content>()
        val contextHistory = history.takeLast(10)
        contextHistory.forEach { msg ->
            val prefix = if (msg.sender == "user") "Student: " else "Jerry: "
            apiContents.add(Content(parts = listOf(Part(text = prefix + msg.text))))
        }
        apiContents.add(Content(parts = listOf(Part(text = "Student: $userMessage"))))

        val request = GenerateContentRequest(
            contents = apiContents,
            systemInstruction = Content(parts = listOf(Part(text = systemInstruction)))
        )

        return try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Jerry is busy thinking about cheddar cheese! Let's try asking again."
        } catch (e: Exception) {
            "Golly! My mouse ears can't hear the satellite. Let's try once more! (Offline backup prompt: ${getFallbackResponse(userMessage, gradeLevel)})"
        }
    }

    private fun getFallbackResponse(message: String, grade: Int): String {
        val lower = message.lowercase()
        return when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") -> {
                "Hello Kiddo! 🐭 I'm Jerry, your super friendly math coach! What math mystery shall we nibble on today? Ask me any math question, and let's solve it together!"
            }
            lower.contains("+") || lower.contains("add") || lower.contains("plus") || lower.contains("sum") -> {
                "Oooh, adding! That's like gathering cheese slices in our secret mousehole. 🧀 If you have some slices, and you get more, you combine them! What numbers are we trying to combine today? Tell me, and I'll give you a secret carrot-sized hint!"
            }
            lower.contains("-") || lower.contains("sub") || lower.contains("minus") || lower.contains("less") -> {
                "Ah, subtraction! That's when grumpy Tom chases me and steals my cheese slices! 🧀 If I start with 10 slices of yummy Swiss cheese, and sneaky Tom steals 3, how many do I have left? Try counting backwards and tell me what you think!"
            }
            lower.contains("*") || lower.contains("mult") || lower.contains("times") -> {
                "Multiplication! That's like copying cheese blocks in neat folders! 🧀 If you have 3 rows of cheese and each row has 5 slices, it's like adding 5 + 5 + 5! What do you get if you stack them together?"
            }
            lower.contains("/") || lower.contains("div") || lower.contains("fraction") || lower.contains("share") -> {
                "Division is all about sharing delicious cheese slices with my little cousin Tuffy! 🧀 If we have 12 pieces of cheddar and want to share them equally between 2 mice, how many do we each get? Hint: What double number makes exactly 12?"
            }
            lower.contains("cheese") -> {
                "Cheese?! Mmm, my favorite! 🧀 In math, we can cut a round cheese wheel into pieces. That's what fractions are! If we cut a cheese wheel into 4 equal slices and Tuffy eats 1 slice, how much cheese is left?"
            }
            else -> {
                "Hello Kiddo! That's a super cool math puzzle. 🐭 I'm ready to coach! How about we break it down together? Tell Jerry your thoughts first, and we can take a nibble of it!"
            }
        }
    }
}
