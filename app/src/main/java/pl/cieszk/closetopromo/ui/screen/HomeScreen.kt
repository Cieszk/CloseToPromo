package pl.cieszk.closetopromo.ui.screen

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import pl.cieszk.closetopromo.data.model.Discount
import pl.cieszk.closetopromo.viewModel.HomeScreenViewModel
import java.util.Calendar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    val homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
    val discounts = homeScreenViewModel.discounts.observeAsState(initial = emptyList()).value

    var showForm by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showForm = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Discount")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        LazyColumn {
            items(discounts) { discount ->
                DiscountCard(discount= discount, navController= navController)
            }
        }

        if (showForm) {
            DiscountForm(
                showFormState = showForm, homeScreenViewModel = homeScreenViewModel
            ) { showForm = false }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscountForm(
    showFormState: Boolean, homeScreenViewModel: HomeScreenViewModel, onDismissRequest: () -> Unit
) {
    var discountTitle by remember { mutableStateOf("") }
    var discountShop by remember { mutableStateOf("") }
    var discountDescription by remember { mutableStateOf("") }
    var discountAmount by remember { mutableStateOf<Int>(0) }
    var discountGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var discountPicture by remember { mutableStateOf("") }
    var discountDateFrom by remember { mutableStateOf<Timestamp?>(null) }
    var discountDateTo by remember { mutableStateOf<Timestamp?>(null) }

    val scrollState = rememberScrollState()

    if (showFormState) {
        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card(elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Add a new Discount", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(value = discountTitle,
                        onValueChange = { discountTitle = it },
                        label = { Text("Title") })

                    OutlinedTextField(value = discountShop,
                        onValueChange = { discountShop = it },
                        label = { Text("Shop") })

                    OutlinedTextField(value = discountDescription,
                        onValueChange = { discountDescription = it },
                        label = { Text("Description") })
                    IntInputField(
                        label = "Enter discount amount",
                        value = discountAmount,
                        onValueChange = { newValue ->
                            if (newValue != null) {
                                discountAmount = newValue
                            }
                        })
                    GeoPointField(discountGeoPoint) { newGeoPoint ->
                        discountGeoPoint = newGeoPoint
                    }
                    PictureInputField(
                        label = "Picture URL",
                        pictureUrl = discountPicture,
                        onPictureUrlChange = { newUrl -> discountPicture = newUrl }
                    )
                    TimestampPicker(label = "Select date from",
                        onTimestampSelected = { selectedTimestamp ->
                            discountDateFrom = selectedTimestamp
                        })
                    TimestampPicker(label = "Select date to",
                        onTimestampSelected = { selectedTimestamp ->
                            discountDateTo = selectedTimestamp
                        })

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = {

                        val newDiscount = Discount(
                            title = discountTitle,
                            description = discountDescription,
                            shop = discountShop,
                            discountAmount = discountAmount,
                            dateFrom = discountDateFrom ?: Timestamp.now(),
                            dateTo = discountDateTo ?: Timestamp.now(),
                            geolocation = discountGeoPoint ?: GeoPoint(0.0, 0.0),
                            picture = discountPicture
                        )
                        homeScreenViewModel.addDiscount(newDiscount)
                        onDismissRequest()
                    }) {
                        Text("Add Discount")
                    }

                }
            }
        }
    }
}

@Composable
fun DiscountCard(discount: Discount, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                       navController.navigate("detail/${discount.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = rememberAsyncImagePainter(discount.picture),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(discount.shop, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(discount.title, style = MaterialTheme.typography.bodyMedium)
            }
            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Text("-${discount.discountAmount}%", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntInputField(
    label: String,
    value: Int?,
    onValueChange: (Int?) -> Unit
) {
    val text = remember { mutableStateOf(value?.toString() ?: "") }

    OutlinedTextField(
        value = text.value,
        onValueChange = { newValue ->
            text.value = newValue
            val filteredValue = newValue.filter { it.isDigit() }
            if (newValue != filteredValue) {
                text.value = filteredValue
            }
            onValueChange(filteredValue.toIntOrNull())
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PictureInputField(
    label: String,
    pictureUrl: String,
    onPictureUrlChange: (String) -> Unit
) {
    var imageUrl by remember { mutableStateOf(pictureUrl) }

    Column {
        OutlinedTextField(
            value = imageUrl,
            onValueChange = { newValue ->
                imageUrl = newValue
                onPictureUrlChange(newValue)
            },
            label = { Text(label) }
        )
    }
}

@Composable
fun TimestampPicker(label: String, onTimestampSelected: (Timestamp) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val calendar = remember { Calendar.getInstance() }
    Row {
        OutlinedButton(onClick = { showDatePicker = true }) {
            Text(label)
        }
    }


    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                val timestamp = Timestamp(calendar.time)
                onTimestampSelected(timestamp)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeoPointField(
    geoPoint: GeoPoint?, onGeoPointChange: (GeoPoint?) -> Unit
) {
    var latitudeText by remember { mutableStateOf(geoPoint?.latitude?.toString() ?: "") }
    var longitudeText by remember { mutableStateOf(geoPoint?.longitude?.toString() ?: "") }
    var latitudeError by remember { mutableStateOf(false) }
    var longitudeError by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(value = latitudeText, onValueChange = { newValue ->
            latitudeText = newValue
            latitudeError = !isValidCoordinate(newValue, -90.0, 90.0)
            updateGeoPoint(
                latitudeText, longitudeText, latitudeError, longitudeError, onGeoPointChange
            )
        }, label = { Text("Latitude") }, isError = latitudeError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        if (latitudeError) {
            Text(
                "Enter a valid latitude (-90 to 90)", color = androidx.compose.ui.graphics.Color.Red
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = longitudeText, onValueChange = { newValue ->
            longitudeText = newValue
            longitudeError = !isValidCoordinate(newValue, -180.0, 180.0)
            updateGeoPoint(
                latitudeText, longitudeText, latitudeError, longitudeError, onGeoPointChange
            )
        }, label = { Text("Longitude") }, isError = longitudeError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        if (longitudeError) {
            Text(
                "Enter a valid longitude (-180 to 180)",
                color = androidx.compose.ui.graphics.Color.Red
            )
        }
    }
}

fun updateGeoPoint(
    latitude: String,
    longitude: String,
    latError: Boolean,
    longError: Boolean,
    onGeoPointChange: (GeoPoint?) -> Unit
) {
    if (!latError && !longError && latitude.isNotBlank() && longitude.isNotBlank()) {
        onGeoPointChange(GeoPoint(latitude.toDouble(), longitude.toDouble()))
    } else {
        onGeoPointChange(null)
    }
}

fun isValidCoordinate(input: String, min: Double, max: Double): Boolean {
    return input.toDoubleOrNull()?.let { it in min..max } ?: false
}
