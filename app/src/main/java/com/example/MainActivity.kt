package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.AppDatabase
import com.example.data.db.ChatMessage
import com.example.data.repository.MathCoachRepository
import com.example.ui.components.CheeseScratchpad
import com.example.ui.components.JerryAvatar
import com.example.ui.components.JerryExpression
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.JerryViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Retrieve databases
        val database = AppDatabase.getDatabase(this)
        val repository = MathCoachRepository(database.mathCoachDao())
        
        setContent {
            MyApplicationTheme {
                val viewModel: JerryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = JerryViewModelFactory(repository)
                )
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    JerryMathCoachApp(
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

class JerryViewModelFactory(private val repository: MathCoachRepository) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JerryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JerryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JerryMathCoachApp(
    viewModel: JerryViewModel,
    modifier: Modifier = Modifier
) {
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val expression by viewModel.expression.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val activeChallenge by viewModel.activeChallenge.collectAsStateWithLifecycle()
    val challengeFeedback by viewModel.challengeFeedback.collectAsStateWithLifecycle()

    var showProfileConfig by remember { mutableStateOf(false) }
    var showScratchpad by remember { mutableStateOf(false) }
    var userText by remember { mutableStateOf("") }
    var challengeAnswerText by remember { mutableStateOf("") }

    // Child edits
    var editedName by remember(userProfile.kidsName) { mutableStateOf(userProfile.kidsName) }
    var selectedGrade by remember(userProfile.gradeLevel) { mutableIntStateOf(userProfile.gradeLevel) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Scroll to bottom when a new chat message arrives
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFDE7), // Top soft yellow cream
                        Color(0xFFFFF9C4)  // Bottom cheese cream
                    )
                )
            )
    ) {
        // --- 1. GAME STATUS HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animated Jerry state avatar
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFFBE6))
                    .clickable {
                        showProfileConfig = !showProfileConfig
                    }
            ) {
                JerryAvatar(
                    expression = expression,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Coach Jerry 🐭",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF5D4037)
                )
                Text(
                    text = "Hi, ${userProfile.kidsName}! Grade ${userProfile.gradeLevel}",
                    fontSize = 13.sp,
                    color = Color(0xFF8D6E63),
                    fontWeight = FontWeight.Medium
                )
            }

            // Earned Cheddar & Star Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⭐", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${userProfile.stars}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFD84315),
                            modifier = Modifier.testTag("stars_badge")
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFECE0)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🧀", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${userProfile.cheeseBlocks}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFEF6C00),
                            modifier = Modifier.testTag("cheese_badge")
                        )
                    }
                }
            }
        }

        // --- 2. COLLAPSIBLE PROFILE EDITOR ---
        AnimatedVisibility(
            visible = showProfileConfig,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Customize Your Math Coach Session!",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037),
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Your Name") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("kids_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Choose Your Grade Level (Age 8 - 12):",
                        fontSize = 13.sp,
                        color = Color(0xFF5D4037),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(3, 4, 5, 6).forEach { g ->
                            FilterChip(
                                selected = selectedGrade == g,
                                onClick = { selectedGrade = g },
                                label = { Text("Grade $g") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFFFA000),
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                viewModel.clearChat()
                                showProfileConfig = false
                            }
                        ) {
                            Text("Reset Chat logs", color = Color(0xFFD84315))
                        }

                        Button(
                            onClick = {
                                viewModel.updateProfile(editedName, selectedGrade)
                                showProfileConfig = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000))
                        ) {
                            Text("Save Cheese Settings")
                        }
                    }
                }
            }
        }

        // --- 3. ACTIVE challenge BOARD ---
        AnimatedVisibility(
            visible = activeChallenge != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            activeChallenge?.let { challenge ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE082)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎯", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Jerry's Math Riddle:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5D4037)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "${challenge.rewardStars}⭐ ${challenge.rewardCheese}🧀",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD84315)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = challenge.problem,
                            fontSize = 14.sp,
                            color = Color(0xFF3E2723),
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = challengeAnswerText,
                                onValueChange = { challengeAnswerText = it },
                                placeholder = { Text("Numbers only") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(56.dp)
                                    .testTag("challenge_answer_input")
                            )

                            Button(
                                onClick = {
                                    if (challengeAnswerText.isNotBlank()) {
                                        viewModel.checkChallengeAnswer(challengeAnswerText)
                                        challengeAnswerText = ""
                                        keyboardController?.hide()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("submit_challenge_button")
                            ) {
                                Text("Pinch answer!")
                            }

                            IconButton(
                                onClick = { viewModel.skipCurrentChallenge() },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Skip Challenge",
                                    tint = Color(0xFF8D6E63)
                                )
                            }
                        }

                        challengeFeedback?.let { feedback ->
                            Text(
                                text = feedback,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (feedback.contains("right") || feedback.contains("Spot")) Color(0xFF2E7D32) else Color(0xFFC62828),
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .testTag("challenge_feedback_text")
                            )
                        }
                    }
                }
            }
        }

        // --- 4. CONVERSATION VIEW OR SCRATCHPAD ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (!showScratchpad) {
                // Regular scrollable discussion chat
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(chatMessages) { msg ->
                        ChatBubble(message = msg)
                    }

                    if (isLoading) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFFBE6))
                                ) {
                                    JerryAvatar(
                                        expression = JerryExpression.THINKING,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "Jerry is writing hints on cheese... 🧀",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Interactive full-height Drawing scratchpad
                CheeseScratchpad(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 4.dp)
                )
            }
        }

        // --- 5. INTERACTIVE BUTTON PANEL ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.generateNewChallenge() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
                modifier = Modifier
                    .weight(1.3f)
                    .testTag("cheese_box_button"),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📦🧀 ")
                    Text("Grab Cheese Challenge", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Button(
                onClick = { showScratchpad = !showScratchpad },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showScratchpad) Color(0xFF8D6E63) else Color(0xFFFFA000)
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("toggle_scratchpad_button"),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (showScratchpad) "💬 Show Chat" else "✏️ Scratchpad", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // --- 6. USER TEXT INPUT BOX ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = userText,
                onValueChange = { userText = it },
                placeholder = { Text("Ask Coach Jerry a math riddle!") },
                maxLines = 3,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                colors = TextFieldDefaults.colors(
                    focusedLabelColor = Color(0xFFFFA000),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(16.dp)
            )

            FloatingActionButton(
                onClick = {
                    if (userText.isNotBlank()) {
                        viewModel.sendMessage(userText)
                        userText = ""
                        keyboardController?.hide()
                    }
                },
                containerColor = Color(0xFFF57C00),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(52.dp)
                    .testTag("send_button")
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send to Jerry")
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "user"
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFFBE6))
                    .padding(2.dp)
            ) {
                // Static icon representation of Jerry on bubble left
                JerryAvatar(expression = JerryExpression.IDLE, modifier = Modifier.fillMaxSize())
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        val bubbleBg = if (isUser) Color(0xFF8D6E63) else Color.White
        val textColor = if (isUser) Color.White else Color(0xFF3E2723)
        val outerCorner = if (isUser) RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp) else RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)

        Card(
            colors = CardDefaults.cardColors(containerColor = bubbleBg),
            shape = outerCorner,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
