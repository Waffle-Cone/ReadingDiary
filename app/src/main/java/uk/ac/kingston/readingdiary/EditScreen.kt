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
import androidx.compose.ui.unit.dp

@Composable
fun EditScreen(
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
    var newEntry by remember { mutableStateOf<Entry>(Entry(id,title)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        //
        TitleBar("Edit Entry",GOTOMAINSCREEN)
        Divider(
            thickness = 3.dp,
            color = MaterialTheme.colorScheme.inverseSurface
        )
        Spacer(modifier = Modifier.absolutePadding(0.dp,10.dp))
        //edit fields
        TextField(
            value = title,
            onValueChange = {title = it},
            label ={ Text("Book Title") },
            supportingText = {Text(text = "Required", color = MaterialTheme.colorScheme.primary)},
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )
        selectedEntry?.let { DateTimePicker(dateTime, it) }

        var noPageError: Boolean= true;
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
                    onValueChange = {
                        if(it.contains(".")||it.contains("-"))
                        {
                            // I dont want decimal page number or negative pg numbers
                        }
                        else{ readFrom = it }},
                    label = { Text("From") },
                    supportingText = {Text(text = "Required", color = MaterialTheme.colorScheme.primary)},
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
                    onValueChange = {
                        if(it.contains(".")||it.contains("-"))
                        {
                            // I dont want decimal page number or negative pg numbers
                        }
                        else{readTo = it}},
                    label = { Text("To") },
                    supportingText = {Text(text = "Required", color = MaterialTheme.colorScheme.primary)},
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }
            //Check if number entered is a number
            var isNumber: Boolean = true // user might type a . or - first which would crash the app if it were converted
            try {
                readFrom.toInt()
                readTo.toInt()
            }catch (e:Exception)
            {
                isNumber = false
                noPageError = false

            }
            // when isNumber = true then safe to cast to double for checks
            if(isNumber) {
                if (readFrom.toInt() < 0 || readTo.toInt() < 0) {
                    Text(
                        text = "Page numbers cannot be negative!",
                        color = MaterialTheme.colorScheme.error)
                    noPageError = false
                }

                if (readFrom.toInt() > readTo.toInt()) {
                    Text(
                        text = """"Page To" must be bigger than or equal to "Page From"""",
                        color = MaterialTheme.colorScheme.error
                    )
                    noPageError = false
                } else {
                    noPageError = true
                }
            }
        }

        //heart rating
        Column {
            Text(
                text = "Rating",
                modifier = Modifier.absolutePadding(10.dp)
            )

            HeartRating(
                modifier = Modifier.size(50.dp),
                rating = rating
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


        //Button tray / submit functionality
        if(title.isNotEmpty() && noPageError)
        {
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                IconButton(
                    onClick = GOTOMAINSCREEN,
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                }
                IconButton(
                    onClick = { shouldSubmit = true },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.Done, contentDescription = null)
                }
            }
        }
        // baking and sending the entry out for delivery to the database
        if(shouldSubmit)
        {
            newEntry.title = title
            newEntry.pageFrom = readFrom.toInt()
            newEntry.pageTo = readTo.toInt()
            newEntry.rating = rating
            newEntry.comment = comments
            selectedEntry?.let { entries.updateItem(it.id,newEntry) }
            GOTOMAINSCREEN();
        }
    }
}