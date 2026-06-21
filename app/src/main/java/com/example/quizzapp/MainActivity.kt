package com.example.quizzapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Datenklasse für eine Quizfrage
data class QuizQuestion(
    val question: String,
    val answers: List<String>,
    val correctAnswerIndex: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QuizApp()
        }
    }
}

@Composable
fun QuizApp() {
    // Liste mit allen Fragen
    val questions = listOf(
        QuizQuestion(
            "Welche Programmiersprache wird oft für Android-Apps genutzt?",
            listOf("Kotlin", "Python", "HTML"),
            0
        ),
        QuizQuestion(
            "Wie viele Antwortmöglichkeiten hat jede Frage in dieser App?",
            listOf("2", "3", "5"),
            1
        ),
        QuizQuestion(
            "Was bedeutet UI?",
            listOf("User Interface", "Universal Input", "Update Internet"),
            0
        ),
        QuizQuestion(
            "Was ergibt 1 + 1?",
            listOf("2", "1,6", "15"),
            0
        ),
        QuizQuestion(
            "Wofür steht APK?",
            listOf("Android Package Kit", "App Programming Kotlin", "Automatic Phone Key"),
            0
        )
    )

    // Speichert, ob Start-, Quiz- oder Endbildschirm angezeigt wird
    var screen by remember { mutableStateOf("start") }

    // Index der aktuellen Frage
    var currentQuestionIndex by remember { mutableStateOf(0) }

    // Punktestand
    var score by remember { mutableStateOf(0) }

    // Speichert ausgewählte Antwort
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }

    // Feedbacktext
    var feedback by remember { mutableStateOf("") }

    MaterialTheme {
        when (screen) {
            "start" -> StartScreen {
                screen = "quiz"
                currentQuestionIndex = 0
                score = 0
                selectedAnswerIndex = null
                feedback = ""
            }

            "quiz" -> QuizScreen(
                question = questions[currentQuestionIndex],
                questionNumber = currentQuestionIndex + 1,
                totalQuestions = questions.size,
                score = score,
                selectedAnswerIndex = selectedAnswerIndex,
                feedback = feedback,
                onAnswerSelected = { index ->
                    if (selectedAnswerIndex == null) {
                        selectedAnswerIndex = index

                        if (index == questions[currentQuestionIndex].correctAnswerIndex) {
                            score++
                            feedback = "Richtig!"
                        } else {
                            feedback = "Falsch!"
                        }
                    }
                },
                onNextQuestion = {
                    if (currentQuestionIndex < questions.lastIndex) {
                        currentQuestionIndex++
                        selectedAnswerIndex = null
                        feedback = ""
                    } else {
                        screen = "end"
                    }
                }
            )

            "end" -> EndScreen(
                score = score,
                totalQuestions = questions.size,
                onRestart = {
                    screen = "start"
                }
            )
        }
    }
}

@Composable
fun StartScreen(onStartClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Quiz-App",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Beantworte die Fragen und sammle Punkte.",
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onStartClick) {
                Text("Quiz starten")
            }
        }
    }
}

@Composable
fun QuizScreen(
    question: QuizQuestion,
    questionNumber: Int,
    totalQuestions: Int,
    score: Int,
    selectedAnswerIndex: Int?,
    feedback: String,
    onAnswerSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Punkte: $score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Frage $questionNumber von $totalQuestions",
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    question.question,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                question.answers.forEachIndexed { index, answer ->
                    val buttonColor =
                        if (selectedAnswerIndex == null) {
                            MaterialTheme.colorScheme.primary
                        } else if (index == question.correctAnswerIndex) {
                            Color(0xFF2E7D32)
                        } else if (index == selectedAnswerIndex) {
                            Color(0xFFC62828)
                        } else {
                            MaterialTheme.colorScheme.primary
                        }

                    Button(
                        onClick = { onAnswerSelected(index) },
                        enabled = selectedAnswerIndex == null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(answer, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    feedback,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onNextQuestion,
                enabled = selectedAnswerIndex != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (questionNumber == totalQuestions) "Ergebnis anzeigen" else "Nächste Frage"
                )
            }
        }
    }
}

@Composable
fun EndScreen(
    score: Int,
    totalQuestions: Int,
    onRestart: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Quiz beendet!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Du hast $score von $totalQuestions Fragen richtig beantwortet.",
                textAlign = TextAlign.Center,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onRestart) {
                Text("Nochmal spielen")
            }
        }
    }
}