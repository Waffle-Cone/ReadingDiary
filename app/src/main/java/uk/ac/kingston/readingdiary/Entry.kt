package uk.ac.kingston.readingdiary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Entry(val id: Int, var title: String, var dateTime: LocalDateTime =LocalDateTime.now()){
    private val myDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(" dd MMM yyyy HH:mm")
    fun getDateTime(): String
    {
        return this.dateTime.format(myDateFormatter)
    }
}


@Composable
fun EntryCard(entry: Entry) {
    Surface(color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp))
    {
        Row(modifier = Modifier.padding(24.dp))
        {
            Column(modifier = Modifier
                .weight(1f))
            {
                Text(text = entry.title)
                Text(text = entry.getDateTime())
            }
            ElevatedButton(onClick = {})
            {
                Text( "Edit")
            }
        }
    }
}