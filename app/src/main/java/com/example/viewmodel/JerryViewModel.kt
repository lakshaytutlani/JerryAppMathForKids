package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.ChatMessage
import com.example.data.db.UserProfile
import com.example.data.repository.MathCoachRepository
import com.example.ui.components.JerryExpression
import com.example.ui.components.MathChallenge
import com.example.ui.components.MathChallengeGenerator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class JerryViewModel(private val repository: MathCoachRepository) : ViewModel() {

    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .map { it ?: UserProfile() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile()
        )

    private val _expression = MutableStateFlow(JerryExpression.IDLE)
    val expression: StateFlow<JerryExpression> = _expression.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _activeChallenge = MutableStateFlow<MathChallenge?>(null)
    val activeChallenge: StateFlow<MathChallenge?> = _activeChallenge.asStateFlow()

    private val _challengeFeedback = MutableStateFlow<String?>(null)
    val challengeFeedback: StateFlow<String?> = _challengeFeedback.asStateFlow()

    init {
        viewModelScope.launch {
            chatMessages.first { true } // Observe initial
            if (chatMessages.value.isEmpty()) {
                repository.addMessage(
                    ChatMessage(
                        sender = "jerry",
                        text = "Hello Kiddo! 🐭 I'm Jerry, your personal Math Coach! What math adventure are we taking today? Tell me what problem you are solving, or click the Cheese Box below to solve one of my special riddles!",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val userMsg = ChatMessage(sender = "user", text = text)
            repository.addMessage(userMsg)

            _expression.value = JerryExpression.THINKING
            _isLoading.value = true

            val reply = repository.askJerry(
                history = chatMessages.value,
                userMessage = text,
                gradeLevel = userProfile.value.gradeLevel,
                userName = userProfile.value.kidsName
            )

            repository.addMessage(ChatMessage(sender = "jerry", text = reply))
            _expression.value = JerryExpression.HAPPY
            _isLoading.value = false
        }
    }

    fun generateNewChallenge() {
        viewModelScope.launch {
            val grade = userProfile.value.gradeLevel
            val challenge = MathChallengeGenerator.generateQuestion(grade)
            _activeChallenge.value = challenge
            _challengeFeedback.value = null
            _expression.value = JerryExpression.THINKING

            repository.addMessage(
                ChatMessage(
                    sender = "jerry",
                    text = "🎯 *NEW MATH CHALLENGE!* (Grade ${grade})\n\n${challenge.problem}\n\nCan you enter your number answer below? I've got cheese crumbs and stars waiting! 🧀⭐"
                )
            )
        }
    }

    fun checkChallengeAnswer(answerStr: String) {
        val currentChallenge = _activeChallenge.value ?: return
        val parsed = answerStr.trim().toIntOrNull()

        viewModelScope.launch {
            if (parsed == currentChallenge.correctValue) {
                val current = userProfile.value
                val newProfile = current.copy(
                    stars = current.stars + currentChallenge.rewardStars,
                    cheeseBlocks = current.cheeseBlocks + currentChallenge.rewardCheese
                )
                repository.updateProfile(newProfile)

                _challengeFeedback.value = "🎉 Spot on! The answer is ${currentChallenge.correctValue}! Jerry says: Amazing Job! You earned ${currentChallenge.rewardStars} ⭐ and ${currentChallenge.rewardCheese} 🧀!"
                _expression.value = JerryExpression.HAPPY

                repository.addMessage(
                    ChatMessage(
                        sender = "jerry",
                        text = "🎉 Hooray! That is completely right! The mystery value is indeed **${currentChallenge.correctValue}**! Adding stars and cheese to your dashboard. Let's keep exploring!"
                    )
                )
                _activeChallenge.value = null
            } else {
                _expression.value = JerryExpression.ENCOURAGING
                _challengeFeedback.value = "So close! Try again, Kiddo! 🐭 Jerry's Hint: ${currentChallenge.solutionHint}"

                repository.addMessage(
                    ChatMessage(
                        sender = "jerry",
                        text = "Aww, almost! That was a really clever guess! Let's sketch it on our cheese scratchpad together. 🧠 *Jerry's Hint:* ${currentChallenge.solutionHint}"
                    )
                )
            }
        }
    }

    fun skipCurrentChallenge() {
        _activeChallenge.value = null
        _challengeFeedback.value = null
        _expression.value = JerryExpression.IDLE
    }

    fun updateProfile(name: String, grade: Int) {
        viewModelScope.launch {
            val current = userProfile.value
            repository.updateProfile(
                current.copy(
                    kidsName = name.ifBlank { "Kiddo" },
                    gradeLevel = grade
                )
            )
            repository.addMessage(
                ChatMessage(
                    sender = "jerry",
                    text = "Aww yeah! Profile modified! Nice to meet you, **$name**! Grade $grade is a fantastic grade for learning some amazing math tricks. What's on your mind today?"
                )
            )
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearHistory()
            _activeChallenge.value = null
            _challengeFeedback.value = null
            _expression.value = JerryExpression.IDLE
            repository.addMessage(
                ChatMessage(
                    sender = "jerry",
                    text = "Hello Kiddo! 🐭 Fresh slate! Ask me any math question or tap 'Grab Cheese Challenge' below!",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
