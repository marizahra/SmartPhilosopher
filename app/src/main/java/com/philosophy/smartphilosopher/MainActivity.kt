package com.philosophy.smartphilosopher

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val PurpleDark = Color(0xFF2E1A47)
val PurplePrimary = Color(0xFF4A148C)
val PurpleLight = Color(0xFF7C4DFF)
val GoldPrimary = Color(0xFFFFD700)
val GoldDark = Color(0xFFFFA000)
val BackgroundColor = Color(0xFF120A2A)

data class Question(
    val id: Int,
    val lesson: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

data class LevelCategory(val name: String, val lessons: List<String>)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundColor
                ) {
                    SmartPhilosopherApp()
                }
            }
        }
    }
}

enum class Screen { HOME, LEVELS, QUIZ, RESULT }

@Composable
fun SmartPhilosopherApp() {
    val context = LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("PhilosopherPrefs", Context.MODE_PRIVATE) }
    
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedLevel by remember { mutableStateOf("") }
    var currentScore by remember { mutableStateOf(0) }
    var highScore by remember { mutableStateOf(sharedPref.getInt("HIGH_SCORE", 0)) }

    fun updateHighScore(newScore: Int) {
        if (newScore > highScore) {
            highScore = newScore
            sharedPref.edit().putInt("HIGH_SCORE", newScore).apply()
        }
    }

    when (currentScreen) {
        Screen.HOME -> HomeScreen(highScore) { currentScreen = Screen.LEVELS }
        Screen.LEVELS -> LevelsScreen(
            onLevelSelect = { level ->
                selectedLevel = level
                currentScreen = Screen.QUIZ
            },
            onBack = { currentScreen = Screen.HOME }
        )
        Screen.QUIZ -> QuizScreen(
            questions = getSampleQuestions(),
            onQuizFinished = { finalScore ->
                currentScore = finalScore
                updateHighScore(finalScore)
                currentScreen = Screen.RESULT
            }
        )
        Screen.RESULT -> ResultScreen(
            score = currentScore,
            total = getSampleQuestions().size,
            highScore = highScore,
            onRestart = { currentScreen = Screen.HOME }
        )
    }
}

@Composable
fun HomeScreen(highScore: Int, onStartClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "تحدّي الفيلسوف الذكي",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "تطبيق مخصص لتلاميذ الثانوي في الجزائر",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = PurpleDark),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "أعلى نتيجة مسجلة", fontSize = 18.sp, color = Color.White)
                Text(text = "$highScore نقطة", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
            }
        }

        Button(
            onClick = onStartClicked,
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text(text = "ابدأ اللعب", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PurpleDark)
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun LevelsScreen(onLevelSelect: (String) -> Unit, onBack: () -> Unit) {
    val levels = listOf(
        LevelCategory(
            name = "ثانية ثانوي آداب وفلسفة",
            lessons = listOf(
                "العلاقة بين العلم والفلسفة", "أوجه الاختلاف بينهما", "أوجه التشابه", "المنطق", 
                "التصورات والحدود", "الأحكام والقضايا", "الاستدلالات", "المنطق المادي", 
                "خطوات المنهج التجريبي", "منهجية المقارنة", "منهجية تحليل النص", "منهجية الجدل", 
                "منهجية الاستقصاء", "الفكر الفلسفي اليوناني", "الفلسفة الإسلامية", "الفلسفة الحديثة", 
                "الفلسفة المعاصرة", "المذاهب الفلسفية"
            )
        ),
        LevelCategory(
            name = "ثالثة علوم تجريبية وتقني",
            lessons = listOf(
                "العلاقة بين العلم والفلسفة", "أوجه الاختلاف بينهما", "فلسفة الرياضيات", 
                "خطوات المنهج التجريبي", "البيولوجيا", "الحتمية", "الشعور بالأنا والشعور بالغير", 
                "المسؤولية", "العنف والتسامح", "علم النفس", "علم التاريخ", "منهجية المقارنة", 
                "منهجية تحليل النص", "منهجية الجدل", "منهجية الاستقصاء"
            )
        ),
        LevelCategory(
            name = "ثالثة ثانوي آداب وفلسفة",
            lessons = listOf(
                "الإحساس والإدراك", "الدال بالمدلول", "اللغة والفكر ووظائف اللغة", 
                "الشعور واللاشعور", "الذاكرة", "الإبداع", "النسيان", "العادة والإرادة", 
                "الأخلاق", "العدالة والحقوق والواجبات", "الأنظمة الاقتصادية", "الأنظمة السياسية", 
                "فلسفة الرياضيات", "خطوات المنهج التجريبي", "البيولوجيا", "الحتمية", 
                "علم النفس", "علم التاريخ", "منهجية المقارنة", "منهجية تحليل النص", 
                "منهجية الجدل", "منهجية الاستقصاء"
            )
        )
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "اختر المستوى والقسم",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = GoldPrimary,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(levels) { level ->
                ExpandableLevelCard(level = level, onStartQuiz = { onLevelSelect(level.name) })
            }
        }

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = PurpleLight),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(text = "رجوع", color = Color.White)
        }
    }
}

@Composable
fun ExpandableLevelCard(level: LevelCategory, onStartQuiz: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = PurpleDark),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = level.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                Text(text = if (expanded) "▲" else "▼", color = GoldPrimary)
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(text = "قائمة الدروس والمنهجيات:", fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(6.dp))
                    level.lessons.forEach { lesson ->
                        Text(text = "• $lesson", fontSize = 14.sp, color = Color.LightGray, modifier = Modifier.padding(vertical = 2.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onStartQuiz,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "بدء الاختبار في هذا المستوى", color = PurpleDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun QuizScreen(questions: List<Question>, onQuizFinished: (Int) -> Unit) {
    var currentIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableStateOf(0) }
    var answered by remember { mutableStateOf(false) }

    val currentQuestion = questions[currentIndex]

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "السؤال ${currentIndex + 1}/${questions.size}", color = Color.White, fontSize = 16.sp)
                Text(text = "النقاط: $score", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (currentIndex + 1) / questions.size.toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = GoldPrimary,
                trackColor = PurpleDark,
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = currentQuestion.questionText, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(20.dp))

            currentQuestion.options.forEachIndexed { index, option ->
                val buttonColor = when {
                    !answered -> PurpleDark
                    index == currentQuestion.correctAnswerIndex -> Color(0xFF2E7D32)
                    selectedOption == index -> Color(0xFFC62828)
                    else -> PurpleDark
                }

                Button(
                    onClick = {
                        if (!answered) {
                            selectedOption = index
                            answered = true
                            if (index == currentQuestion.correctAnswerIndex) {
                                score += 10
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(text = option, fontSize = 16.sp, color = Color.White, modifier = Modifier.padding(8.dp))
                }
            }

            if (answered) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = PurpleDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "التفسير:", fontWeight = FontWeight.Bold, color = GoldPrimary)
                        Text(text = currentQuestion.explanation, color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }

        if (answered) {
            Button(
                onClick = {
                    if (currentIndex + 1 < questions.size) {
                        currentIndex++
                        answered = false
                        selectedOption = null
                    } else {
                        onQuizFinished(score)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(
                    text = if (currentIndex + 1 < questions.size) "السؤال التالي" else "إنهاء التحدي",
                    color = PurpleDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun ResultScreen(score: Int, total: Int, highScore: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "اكتمل التحدي!", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
        Spacer(modifier = Modifier.height(20.dp))

