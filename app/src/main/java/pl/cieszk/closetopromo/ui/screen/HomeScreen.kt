package pl.cieszk.closetopromo.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import pl.cieszk.closetopromo.data.model.Discount
import pl.cieszk.closetopromo.viewModel.HomeScreenViewModel
import java.util.Calendar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController
) {
    val homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
    val discounts = homeScreenViewModel.discounts.observeAsState(initial = emptyList()).value

    var showForm by remember { mutableStateOf(false) }
    var locationPermissionState by remember { mutableStateOf("") }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                locationPermissionState = "Precise location access granted."
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                locationPermissionState = "Approximate location access granted."
            }

            else -> {
                locationPermissionState = "No location access granted."
            }
        }
    }

    LaunchedEffect(key1 = true) {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

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
                DiscountCard(discount = discount, navController = navController)
            }
        }

        if (showForm) {
            DiscountForm(
                showFormState = showForm, homeScreenViewModel = homeScreenViewModel
            ) { showForm = false }
        }
    }
}

@Composable
fun DiscountForm(
    showFormState: Boolean,
    homeScreenViewModel: HomeScreenViewModel,
    onDismissRequest: () -> Unit
) {
    var discountTitle by remember { mutableStateOf("") }
    var discountShop by remember { mutableStateOf("") }
    var discountDescription by remember { mutableStateOf("") }
    var discountAmount by remember { mutableStateOf("") }
    var discountPicture by remember { mutableStateOf("") }
    var discountDateFrom by remember { mutableStateOf<Timestamp?>(null) }
    var discountDateTo by remember { mutableStateOf<Timestamp?>(null) }
    var discountGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }

    if (showFormState) {
        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(all = 24.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Add a new Discount", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    FormInputField(label = "Title", value = discountTitle) { discountTitle = it }
                    FormInputField(label = "Shop", value = discountShop) { discountShop = it }
                    FormInputField(
                        label = "Description",
                        value = discountDescription
                    ) { discountDescription = it }
                    FormInputField(
                        label = "Discount Amount",
                        value = discountAmount,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    ) { discountAmount = it }
                    PictureInputField(
                        label = "Picture URL",
                        pictureUrl = discountPicture,
                        onPictureUrlChange = { discountPicture = it }
                    )
                    TimestampPicker(label = "Select date from",
                        onTimestampSelected = { discountDateFrom = it }
                    )
                    TimestampPicker(label = "Select date to",
                        onTimestampSelected = { discountDateTo = it }
                    )
                    GeoPointField(discountGeoPoint) { discountGeoPoint = it }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val newDiscount = Discount(
                                title = discountTitle,
                                shop = discountShop,
                                description = discountDescription,
                                discountAmount = discountAmount.toIntOrNull() ?: 0,
                                dateFrom = discountDateFrom ?: Timestamp.now(),
                                dateTo = discountDateTo ?: Timestamp.now(),
                                geolocation = discountGeoPoint ?: GeoPoint(0.0, 0.0),
                                picture = discountPicture
                            )
                            homeScreenViewModel.addDiscount(newDiscount)
                            onDismissRequest()
                        },
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Add Discount")
                    }
                }
            }
        }
    }
}

@Composable
fun FormInputField(
    label: String,
    value: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun DiscountCard(discount: Discount, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { navController.navigate("detail/${discount.id}") },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DiscountImage(discount.picture)
            DiscountDetails(discount)
            Spacer(Modifier.weight(1f))
            DiscountAmount(discount.discountAmount)
        }
    }
}

@Composable
fun DiscountAmount(discountAmount: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(219,65,22))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "-$discountAmount%",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun DiscountImage(pictureUrl: String) {
    Image(
        painter = rememberAsyncImagePainter(pictureUrl),
        contentDescription = "Discount Image",
        modifier = Modifier
            .size(width = 80.dp, height = 80.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
    Spacer(modifier = Modifier.width(16.dp))
}

@Composable
fun DiscountDetails(discount: Discount) {
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text(
            text = discount.shop,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = discount.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis
        )
    }
}

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
