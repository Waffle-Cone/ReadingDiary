package uk.ac.kingston.readingdiary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Entry(val creationOrder:Int,var id: Int, var title: String, var pageFrom: Int =0, var pageTo: Int =0, var rating: Int = 0, var comment: String = "", var dateTime: LocalDateTime =LocalDateTime.now()){
    private val myDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
    fun getDateTime(): String
    {
        return this.dateTime.format(myDateFormatter)
    }
    fun resetID(id: Int)
    {
        this.id = id
    }
}


@Composable
fun EntryCard(
    entry: Entry,
    GOTOVIEWSCREEN: ()-> Unit,
    onViewSelect: (Entry) -> Unit
              ) {
    Surface(color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize())
    {
        Row(modifier = Modifier.padding(24.dp))
        {
            Column(modifier = Modifier
                .weight(1f))
            {
                Text(text = "Title: ${entry.title}")
                Text(text = "Date: ${entry.getDateTime()}")
                Row {
                    Text(text = "Pg.${entry.pageFrom} - Pg.${entry.pageTo}")
                }
                HeartRating(
                    modifier = Modifier
                        .size(20.dp)
                        .absolutePadding(0.dp, 10.dp),
                    rating = entry.rating,
                    isClickable = false
                ){
                    entry.rating = it
                }
            }
            ElevatedButton(onClick = {
                onViewSelect(entry)
                GOTOVIEWSCREEN()
            }
            )
            {
                Text( "View")
            }
        }
    }
}