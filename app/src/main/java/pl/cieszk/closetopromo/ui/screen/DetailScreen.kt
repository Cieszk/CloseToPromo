package pl.cieszk.closetopromo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import pl.cieszk.closetopromo.data.model.Discount
import pl.cieszk.closetopromo.viewModel.DetailScreenViewModel
import pl.cieszk.closetopromo.viewModel.HomeScreenViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DetailScreen(
    itemId: String?,
    navController: NavController,
    viewModel: DetailScreenViewModel = hiltViewModel()
) {
    val discount by viewModel.discount.observeAsState()

    discount?.let { discount ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 16.dp),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(size = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(size = 16.dp)
                    )
                    .clip(RoundedCornerShape(size = 16.dp))
                    .padding(all = 4.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(discount.picture),
                    contentDescription = "Discount Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Text(
                        text = "-${discount.discountAmount}%",
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.Red, shape = CircleShape)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White,
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = discount.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = discount.shop,
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic
                    )
                }
                Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = discount.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "From: ${formatTimestamp(discount.dateFrom)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Divider(
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )
                    Text(
                        text = "To: ${formatTimestamp(discount.dateTo)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = "Location: ${formatGeopoint(discount.geolocation)}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun formatTimestamp(timestamp: Timestamp?): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return timestamp?.toDate()?.let { sdf.format(it) }.orEmpty()
}

private fun formatGeopoint(geoPoint: GeoPoint?): String {
    return "Lat: ${geoPoint?.latitude}, Lon: ${geoPoint?.longitude}"
}