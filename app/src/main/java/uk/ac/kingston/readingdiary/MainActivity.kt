package uk.ac.kingston.readingdiary

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.ac.kingston.readingdiary.ui.theme.ReadingDiaryTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReadingDiaryTheme {
                // A surface container using the 'background' color from the theme

                    /*val y = DateTimeFormatter.ofPattern(" dd MMM yyyy HH:mm")
                    val x = LocalDateTime.now().format(y)
                    //Text(text = x.toString())

                    val repo = Database();
                    val entry1 = Entry(1,"Book1");
                    val entry2 = Entry(2,"Book2");
                    repo.addEntry(entry1);
                    repo.addEntry(entry2);
                    //Text(text = repo.getEntryById(1)?.getDateTime() ?: "Entry/Date not found")
                    EntryList(repo)*/
                    MyApp( modifier = Modifier.fillMaxSize())
                }
            }
        }
    }


@Composable
private fun MyApp(modifier: Modifier = Modifier) {
    val entries by remember { mutableStateOf(Database()) }
    var shouldShowAddScreen by rememberSaveable { mutableStateOf(false) }
    val SHOWADDSCREEN = {shouldShowAddScreen = true}
    val HIDEADDSCREEN = {shouldShowAddScreen = false}

    Surface(modifier,
            color = MaterialTheme.colorScheme.background
    ){
        if (shouldShowAddScreen){
            AddEntryScreen(entries = entries,HIDEADDSCREEN)
        }
        else{
            MainScreen(entries,SHOWADDSCREEN)
        }
    }
}

@Composable
fun MainScreen(
    entries: Database,
    GOTOADDSCREEN: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ){
            IconButton(
                onClick = GOTOADDSCREEN,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = null)
            }
        }
        Row {
            EntryList(entries = entries)
        }
    }
}

