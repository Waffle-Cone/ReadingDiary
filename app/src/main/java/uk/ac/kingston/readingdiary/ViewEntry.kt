package uk.ac.kingston.readingdiary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ViewEntry(
    selectedID: Entry,
    entries: Database,
    GOTOMAINSCREEN: () -> Unit)
{
    var shouldSubmit by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf(entries.getEntryById(selectedID.id)) }
    val id = entries.getAllEntries().size+1
    var title by rememberSaveable { mutableStateOf( selectedEntry!!.title) }
    var comments by rememberSaveable { mutableStateOf(selectedEntry!!.comment) }
    var readFrom by rememberSaveable { mutableStateOf(selectedEntry!!.pageFrom.toString()) }
    var readTo by rememberSaveable { mutableStateOf(selectedEntry!!.pageTo.toString()) }
    var rating by rememberSaveable { mutableStateOf(selectedEntry!!.rating) }
    var dateTime by rememberSaveable { mutableStateOf(selectedEntry!!.dateTime) };

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        //
        TitleBar("View Entry",GOTOMAINSCREEN)
        Divider(
            thickness = 3.dp,
            color = MaterialTheme.colorScheme.inverseSurface
        )
        Spacer(modifier = Modifier.absolutePadding(0.dp,10.dp))
        //edit fields
        Text(
            text= title,
            style = MaterialTheme.typography.titleLarge
        )
        selectedEntry?.let { Text(text = it.getDateTime()) }

        Column {
            Text(
                text = "Pages Read",
                modifier = Modifier.absolutePadding(10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
            ) {
                TextField(
                    modifier = Modifier.weight(1f),
                    value = readFrom,
                    onValueChange = { readFrom = it },
                    label = { Text("From") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = "-"
                )
                TextField(
                    modifier = Modifier.weight(1f),
                    value = readTo,
                    onValueChange = { readTo = it },
                    label = { Text("To") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }
        }

        //heart rating
        Column {
            Text(
                text = "Rating",
                modifier = Modifier.absolutePadding(10.dp)
            )

            StarRating(
                modifier = Modifier.size(50.dp),
                rating = rating,
                isClickable = false
            ) {
                rating = it;
            }
        }
        //Comments
        TextField(
            value = comments,
            onValueChange = {comments = it},
            label ={ Text("Comments") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth())


    }
}
@Preview(showBackground = true)
@Composable
fun PreviewViewScreen(){
    val testBase  by remember {mutableStateOf(Database())}
    var testEntry by remember { mutableStateOf<Entry?>(null) }
    var newEntry by remember { mutableStateOf<Entry>(Entry(0,"title")) }
    var TEST by rememberSaveable { mutableStateOf(false) }
    val SHOWTEST = {TEST = true}
    val HIDETEST = {TEST = false}

    newEntry.title = "Hello"
    newEntry.pageFrom = 7.0
    newEntry.pageTo = 8.0
    newEntry.rating = 5
    newEntry.comment = "comments"
    testBase.addEntry(newEntry)
    testEntry = newEntry

    ViewEntry(testEntry!!,testBase,SHOWTEST)
}