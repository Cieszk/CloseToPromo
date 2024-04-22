package pl.cieszk.closetopromo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import pl.cieszk.closetopromo.data.model.Discount
import pl.cieszk.closetopromo.viewModel.HomeScreenViewModel

@Composable
fun HomeScreen(
    navController: NavController
) {
    val homeScreenViewModel: HomeScreenViewModel = viewModel()

    val discounts = homeScreenViewModel.discounts.observeAsState(initial = emptyList()).value

    LazyColumn {
        items(discounts) { discount ->
            DiscountCard(discount)
        }
    }

}

@Composable
fun DiscountCard(discount: Discount) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
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
                Text(discount.shop, fontWeight = FontWeight.Bold)
                Text(discount.title, style = MaterialTheme.typography.bodyMedium)
                Text(discount.description, color = Color.Gray)
            }
            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Text("${discount.discountAmount} %", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}