package com.example.a4kotlinlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlin.math.pow

import kotlin.math.sqrt


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainCalculatorFunction()
        }
    }
}

@Composable
fun MainCalculatorFunction() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main_screen") {
        composable("main_screen") {
            MainScreen(navController)
        }
        composable("first_task") {
            FirstTaskScreen() {
                navController.popBackStack()
            }
        }
        composable("second_task") {
            SecondTaskScreen() {
                navController.popBackStack()
            }
        }
        composable("third_task") {
            ThirdTaskScreen() {
                navController.popBackStack()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar (
                title = {
                    Text("Калькулятор розрахунку струму")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { navController.navigate("first_task") },
                shape = RoundedCornerShape(10.dp)
            ) {
                    Text("Вибрати кабелі для живлення двотрансформаторної підстанції системи внутрішнього" +
                            "електропостачання підприємства напругою 10 кВ")
            }
            Button(
                onClick = { navController.navigate("second_task") },
                shape = RoundedCornerShape(10.dp)
            ) {
                    Text("Визначити струми КЗ на шинах 10 кВ ГПП")
            }
            Button(
                onClick = { navController.navigate("third_task") },
                shape = RoundedCornerShape(10.dp)
            ) {
                    Text("Визначити струми КЗ для підстанції Хмельницьких північних електричних мереж" +
                            "(ХПнЕМ), яка може мати три режими: нормальний режим; мінімальний режим; аварійний" +
                            "режим")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstTaskScreen(navigateBack: () -> Unit) {
    var mainsVoltage by remember { mutableStateOf("") }
    var designLoad by remember { mutableStateOf("") }
    var timeOfUse by remember { mutableStateOf("") }
    var Ik by remember { mutableStateOf("") }
    var tf by remember { mutableStateOf("") }
    var result by remember { mutableStateOf <List<Pair<String, Double>>?>(null) }


    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Завдання 1. Вибір кабеля")
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                )
        }
    ) { paddingValues ->
        Box (
            modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = mainsVoltage,
                    onValueChange = { mainsVoltage = it },
                    label = { Text("Напруга мережі (кВ)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = designLoad,
                    onValueChange = { designLoad = it },
                    label = { Text("Розрахункове навантаження (кВ*А)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = timeOfUse,
                    onValueChange = { timeOfUse = it },
                    label = { Text("Час використання (год.)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = Ik,
                    onValueChange = { Ik = it },
                    label = { Text("Струм КЗ (A)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = tf,
                    onValueChange = { tf = it },
                    label = { Text("Фіктивний час вимикання струму КЗ (сек.)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    result = calculateCableParams(
                        designLoad = designLoad.toDouble(),
                        mainsVoltage = mainsVoltage.toDouble(),
                        timeOfUse = timeOfUse.toDouble(),
                        Ik = Ik.toDouble(),
                        tf = tf.toDouble(),
                    )
                }) {
                    Text("Розрахувати")
                }

                result?.let {
                    if (it.isNotEmpty()) {
                        val Im =  String.format("%.2f", it.firstOrNull { pair -> pair.first == "Im" }?.second)
                        val Impa =  String.format("%.2f", it.firstOrNull { pair -> pair.first == "Impa" }?.second)
                        val Sek =  String.format("%.2f", it.firstOrNull { pair -> pair.first == "Sek" }?.second)
                        val Smin =  String.format("%.2f", it.firstOrNull { pair -> pair.first == "Smin" }?.second)

                        Text(text = "Розрахунковий струм у нормальному режимі: $Im А")
                        Text(text = "Розрахунковий струм у післяаварійному режимі: $Impa А")

                        Text("Обираємо броньовані кабелі з паперовою ізоляцією в алюмінієвій оболонці типу ААБ")
                        if (Sek > 0.toString()) Text(text = "Економічний переріз: $Sek мм^2") else Text("Економічний переріз: Некоректне значення часу")
                        Text(text = "Термічна стійкість до дї струмів КЗ: $Smin мм^2")
                    }
                }
            }
        }
    }
}

fun calculateCableParams(designLoad: Double, mainsVoltage: Double, timeOfUse: Double, Ik: Double, tf: Double): List<Pair<String, Double>> {
    // Розрахунковий струм для нормльного режиму
    val Im = (designLoad / 2.0) / (sqrt(3.0) * mainsVoltage)

    // Розрахунковий струм для післяаварійного режиму
    val Impa = 2 * Im

    // Економічний переріз
    var jek = 0.0
    if (timeOfUse > 1000 && timeOfUse <= 3000) jek = 1.6
    else if (timeOfUse > 3000 && timeOfUse <= 5000) jek = 1.4
    else if (timeOfUse > 5000) jek = 1.2

    val Sek = if (jek > 0) Im / jek else jek

    // Термічна стійкість
    val Smin = Ik * sqrt(tf) / 92

    return listOf(
        "Im" to Im,
        "Impa" to Impa,
        "Sek" to Sek,
        "Smin" to Smin,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondTaskScreen(navigateBack: () -> Unit) {
    var Usn by remember { mutableStateOf("") }
    var Sk by remember { mutableStateOf("") }
    var Snom by remember { mutableStateOf("") }
    var Uk by remember { mutableStateOf("") }
    var result by remember { mutableStateOf <List<Pair<String, Double>>?>(null) }


    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = "Завдання 2. Визначення струмів КЗ на шинах")
            },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },)
        }
    ) { paddingValues ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = Usn,
                    onValueChange = { Usn = it },
                    label = { Text("Середня номінальна напруга точки, в якій виникає КЗ (кВ)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = Sk,
                    onValueChange = { Sk = it },
                    label = { Text("Потужність короткого замикання (МВ*А)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = Snom,
                    onValueChange = { Snom = it },
                    label = { Text("Номінальна потужність трансформатора (МВ*А)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = Uk,
                    onValueChange = { Uk = it },
                    label = { Text("Напруга короткого замикання трансформатора (%)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    result = CalculateShortCircuitCurrents(
                        Usn = Usn.toDouble(),
                        Sk = Sk.toDouble(),
                        Snom = Snom.toDouble(),
                        Uk = Uk.toDouble(),
                    )
                }) {
                    Text("Розрахувати")
                }

                result?.let {
                    if (it.isNotEmpty()) {
                        val Xc =  String.format("%.2f", it.firstOrNull { pair -> pair.first == "Xc" }?.second)
                        val Xt =  String.format("%.2f", it.firstOrNull { pair -> pair.first == "Xt" }?.second)
                        val Xsum =  String.format("%.2f", it.firstOrNull { pair -> pair.first == "Xsum" }?.second)
                        val Ip0 =  String.format("%.2f", it.firstOrNull { pair -> pair.first == "Ip0" }?.second)

                        Text(text = "Опір системи: $Xc Ом")
                        Text(text = "Опір трансфомратора: $Xt Ом")
                        Text(text = "Сумарний опір: $Xsum Ом")
                        Text(text = "Початкове діюче значення струму трифазного КЗ: $Ip0 кА")
                    }
                }
            }
        }
    }
}

fun CalculateShortCircuitCurrents(Usn: Double, Sk: Double, Snom: Double, Uk: Double): List<Pair<String, Double>>? {
    // Опір системи
    val Xc = Usn * Usn / Sk

    // Опір трансформатора
    val Xt = (Uk * Usn * Usn) / (100 * Snom)

    // Сумарний опір
    val Xsum = Xc + Xt

    // початковий струм
    val Ip0 = Usn / (sqrt(3.0) * Xsum)

    return listOf(
        "Xc" to Xc,
        "Xt" to Xt,
        "Xsum" to Xsum,
        "Ip0" to Ip0,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdTaskScreen(navigateBack: () -> Unit) {
    var Unn by remember { mutableStateOf("") }
    var Uvn by remember { mutableStateOf("") }
    var Ukmax by remember { mutableStateOf("") }
    var Snomt by remember { mutableStateOf("") }
    var result by remember { mutableStateOf <List<Pair<String, Double>>?>(null) }


    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = "Завдання 3. Визначити струми КЗ для підстанції (ХПнЕМ)")
            },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },)
        }
    ) { paddingValues ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = Unn,
                    onValueChange = { Unn = it },
                    label = { Text("Номінальна напруга системи (кВ)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = Uvn,
                    onValueChange = { Uvn = it },
                    label = { Text("Напруга високої сторони (кВ)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = Ukmax,
                    onValueChange = { Ukmax = it },
                    label = { Text("Напруга короткого замикання (%)") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = Snomt,
                    onValueChange = { Snomt = it },
                    label = { Text("Номінальна потужність трансформатора (МВ)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    result = calculateCurrentsForSubstation(
                        Unn = Unn.toDouble(),
                        Uvn = Uvn.toDouble(),
                        Ukmax = Ukmax.toDouble(),
                        Snomt = Snomt.toDouble(),
                    )
                }) {
                    Text("Розрахувати")
                }

                result?.let {
                    if (it.isNotEmpty()) {
                        val Zsigman =  String.format("%.2f", it.firstOrNull { pair -> pair.first == "Zsigman" }?.second)
                        val Iln =  String.format("%.2f", it.firstOrNull { pair -> pair.first == "Iln" }?.second)
                        val Zsigmamin =  String.format("%.2f", it.firstOrNull { pair -> pair.first == "Zsigmamin" }?.second)
                        val Ilmin =  String.format("%.2f", it.firstOrNull { pair -> pair.first == "Ilmin" }?.second)

                        Text(text = "Опір в нормальному режимі: $Zsigman Ом")
                        Text(text = "Струм трифазного КЗ в нормальному режимі: $Iln A")
                        Text(text = "Опір в мінімальному режимі: $Zsigmamin Ом")
                        Text(text = "Струм трифазного КЗ в мінімальному режимі: $Ilmin A")
                    }
                }
            }
        }
    }
}

fun Double.roundTo(n: Int): Double {
    val factor = 10.0.pow(n)
    return Math.round(this * factor) / factor
}

fun calculateCurrentsForSubstation(Unn: Double, Uvn: Double, Ukmax: Double, Snomt: Double): List<Pair<String, Double>>? {

    val Rl = 7.91
    val Xl = 4.49
    val Rsh = 10.65
    val Xsn = 24.02
    val Xt = ((Ukmax * Math.pow(Uvn, 2.0)) / (100 * Snomt)).roundTo(2)
    val Xsh = (Xsn + Xt).roundTo(2)
    val kpr = (Math.pow(Unn, 2.0) / Math.pow(Uvn, 2.0)).roundTo(3)
    val Rshmin = 34.88
    val Xsmin = 65.68
    val Xshmin = Xsmin + Xt

    //опір в нормальному режимі
    val Rshn = (Rsh * kpr).roundTo(2)
    val Xshn = (Xsh * kpr).roundTo(2)
    val Rsigman = (Rl + Rshn).roundTo(2)
    val Xsigman = (Xl + Xshn).roundTo(2)
    val Zsigman = (sqrt(Math.pow(Rsigman, 2.0) + Math.pow(Xsigman, 2.0))).roundTo(2) //16.014 + 13.684 = 29.698
    //струм трифазного КЗ в нормальному режимі
    val Iln = ((Unn * Math.pow(10.0, 3.0)) / (sqrt(3.0) * Zsigman)).roundTo(2)

    //опір в мінімальному режимі
    val Rshnmin = (Rshmin * kpr).roundTo(2)
    val Xshnmin = (Xshmin * kpr).roundTo(2)
    val Rsigmamin = (Rl + Rshnmin).roundTo(2)
    val Xsigmamin = (Xl + Xshnmin).roundTo(2)
    val Zsigmamin = (sqrt(Math.pow(Rsigmamin, 2.0) + Math.pow(Xsigmamin, 2.0))).roundTo(2)
    //струм трифазного КЗ в мінімальному режимі
    val Ilmin = ((Unn * Math.pow(10.0, 3.0)) / (sqrt(3.0) * Zsigmamin)).roundTo(2)

    return listOf(
        "Zsigman" to Zsigman,
        "Iln" to Iln,
        "Zsigmamin" to Zsigmamin,
        "Ilmin" to Ilmin,
    )
}