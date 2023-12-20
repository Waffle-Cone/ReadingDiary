package uk.ac.kingston.readingdiary

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import uk.ac.kingston.readingdiary.ui.theme.ReadingDiaryTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReadingDiaryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val y = DateTimeFormatter.ofPattern(" dd MMM yyyy HH:mm")
                    val x = LocalDateTime.now().format(y)
                    //Text(text = x.toString())

                    val repo = Database();
                    val entry1 = Entry(1,"Book1");

                    repo.addEntry(entry1);
                    //Text(text = repo.getEntryById(1)?.getDateTime() ?: "Entry/Date not found")
                    
                    EntryCard(entry = entry1)
                }
            }
        }
    }
}
