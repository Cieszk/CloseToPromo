package pl.cieszk.closetopromo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.initialize
import dagger.hilt.android.AndroidEntryPoint
import pl.cieszk.closetopromo.navigation.AppNavGraph

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Firebase.initialize(this)
            AppNavGraph()
        }
    }
}