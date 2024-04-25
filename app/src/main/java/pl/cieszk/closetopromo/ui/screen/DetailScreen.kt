package pl.cieszk.closetopromo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import pl.cieszk.closetopromo.data.model.Discount
import pl.cieszk.closetopromo.viewModel.DetailScreenViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    itemId: String?,
    navController: NavController,
    viewModel: DetailScreenViewModel = hiltViewModel()
) {
    val discount by viewModel.discount.observeAsState()
    discount?.let { discount ->

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Discount Details") })
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                HeroImageSection(discount = discount)
                Spacer(modifier = Modifier.height(10.dp))
                DiscountInfoSection(discount = discount)
            }
        }
    }
}

@Composable
fun HeroImageSection(discount: Discount) {
    Box(modifier = Modifier.height(250.dp)) {
        Image(
            painter = rememberAsyncImagePainter(discount.picture),
            contentDescription = "Promo Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3F))
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = "-${discount.discountAmount}%",
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.Red, shape = CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                color = Color.White,
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun DiscountInfoSection(discount: Discount) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = discount.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Divider()
        Text(
            text = "Shop: ${discount.shop}",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Text(
            text = "Valid from ${formatDate(discount.dateFrom)} to ${formatDate(discount.dateTo)}",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(20.dp))
        DiscountMap(geolocation = discount.geolocation)
    }
}

@Composable
fun DiscountMap(geolocation: GeoPoint) {
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
            LatLng(
                geolocation.latitude,
                geolocation.longitude
            ), 14f
        )
    }
    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = LatLng(geolocation.latitude, geolocation.longitude)),
            title = "Location",
            snippet = "Discount available here"
        )
    }
}

fun formatDate(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}