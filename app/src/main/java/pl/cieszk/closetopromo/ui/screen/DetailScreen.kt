package pl.cieszk.closetopromo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import pl.cieszk.closetopromo.data.model.Discount
import pl.cieszk.closetopromo.viewModel.DetailScreenViewModel
import pl.cieszk.closetopromo.viewModel.HomeScreenViewModel

@Composable
fun DetailScreen(
    itemId: String?,
    navController: NavController,
    viewModel: DetailScreenViewModel = hiltViewModel()
) {
    val discount by viewModel.discount.observeAsState()

    discount?.let {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = rememberAsyncImagePainter(discount!!.picture),
                contentDescription = "Discount Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = "${discount!!.discountAmount}%",
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.Gray, shape = CircleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = discount!!.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = discount!!.shop,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = discount!!.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "From: ${formatTimestamp(discount!!.dateFrom)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "To: ${formatTimestamp(discount!!.dateTo)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = "Location: ${formatGeopoint(discount!!.geolocation)}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun formatTimestamp(timestamp: Timestamp?): String {
    return timestamp?.toDate().toString()
}

private fun formatGeopoint(geoPoint: GeoPoint?): String {
    return "Lat: ${geoPoint?.latitude}, Lon: ${geoPoint?.longitude}"
}