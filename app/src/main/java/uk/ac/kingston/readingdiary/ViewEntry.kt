package uk.ac.kingston.readingdiary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    var selectedEntry by remember{ mutableStateOf(entries.getEntryById(selectedID.id)) }
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
        TitleBar("Viewing: ${title}",GOTOMAINSCREEN)
        Divider(
            thickness = 3.dp,
            color = MaterialTheme.colorScheme.inverseSurface
        )
        //Spacer(modifier = Modifier.absolutePadding(0.dp,5.dp))
        //edit fields

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            myDisplayText(
                text = title,
                title = "",
                bookTitle= true,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            selectedEntry?.let {
                myDisplayText(
                    text = it.getDateTime(),
                    title = "Date"
                )
            }
            myDisplayText(
                text = "pg.${readFrom} - pg.${readTo}",
                title = "Pages Read"
            )

            //heart rating
            Column {
                Text(
                    text = "Rating",
                    style = MaterialTheme.typography.labelMedium
                )
                HeartRating(
                    modifier = Modifier.size(50.dp),
                    rating = rating,
                    isClickable = false
                ) {
                    rating = it;
                }
            }
            myDisplayText(
                text = comments,
                title = "Comments"
            )

        }

    }
}

/**
 * Testing view entry screen
 */
@Preview(showBackground = true)
@Composable
fun PreviewViewScreen(){
    val testBase  by remember {mutableStateOf(Database())}
    var testEntry by remember { mutableStateOf<Entry?>(null) }
    var newEntry by remember { mutableStateOf<Entry>(Entry(0,0,"hello")) }
    var TEST by rememberSaveable { mutableStateOf(false) }
    val SHOWTEST = {TEST = true}
    val HIDETEST = {TEST = false}

    newEntry.title = "Hello"
    newEntry.pageFrom = 7
    newEntry.pageTo = 8
    newEntry.rating = 5
    newEntry.comment = "comments"
    testBase.addEntry(newEntry)
    testEntry = newEntry

    ViewEntry(testEntry!!,testBase,SHOWTEST)
}