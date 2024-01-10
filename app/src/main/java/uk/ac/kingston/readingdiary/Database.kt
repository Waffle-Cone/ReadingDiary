package uk.ac.kingston.readingdiary

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime

class Database{
    private val entryList =mutableStateListOf<Entry>()// make sure this is a StateList so that things reload properly
    fun addEntry (entry: Entry): Boolean {
        if(getEntryById(entry.id) != null){
            Log.i("tag1","Entry with the ID: ${entry.id} already exists")
            return false
        }
        else{
            entryList.add(entry)
            Log.i("tag1","Item added")
            return true
        }
    }
    fun getEntryById(id: Int): Entry? {
        return entryList.find {it.id == id}
    }
    fun updateItem(entry: Entry): Boolean
    {
        val existingEntry = getEntryById(entry.id)
        if (existingEntry != null){
            existingEntry.dateTime = entry.dateTime
            existingEntry.title = entry.title
            Log.i("tag1","Entry updated successfully")
            return true
        }
        else{
            Log.i("tag1","Entry with id ${entry.id} not found. Cannot update")
            return false
        }
    }
    fun deleteEntry(id: Int): Boolean{
        val existingEntry = getEntryById(id)
        if (existingEntry != null) {
            entryList.remove(existingEntry)
            Log.i("tag1","Item deleted = ${id}")
            return true
        }
        else{
            Log.i("tag1","Entry with $id not found. Cannot delete")
            return false
        }
    }
    fun getAllEntries(): List<Entry> {
        return entryList.toList()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryListView(
    entries: Database,
    GOTOEDITSCREEN: ()-> Unit,
    onEntrySelect: (Entry) -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirm by rememberSaveable { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<Entry?>(null) }

    // delete confirmation modal
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false},
            title = { Text(text = "Delete Confirmation") },
            text = { Text(text = "Delete Book: ${selectedEntry?.title}?") },
            confirmButton = {
                TextButton(
                    onClick = { selectedEntry?.id?.let { entries.deleteEntry(it) } },
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirm = false },
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier.padding(vertical = 5.dp),
        state = rememberLazyListState()
    )
    {
        items(items = entries.getAllEntries())
        { entry ->
            val dismissState = rememberDismissState(
                confirmValueChange = {
                    if (it == DismissValue.DismissedToStart) // from right to left DELETE
                    {
                        selectedEntry = entry
                        showConfirm = true
                    }
                    if (it == DismissValue.DismissedToEnd) // from left to right EdIT
                    {
                        onEntrySelect(entry)
                        GOTOEDITSCREEN()
                    }
                    true
                },
            )
            /**
             * Doing this was a headache because I introduced animations which, as you will see later
             * required me to figure out how to run suspend functions and get my head around what a
             * coroutineScope was -> explained bellow
             */
            SwipeToDismiss(
                state = dismissState,
                background = {
                    var color: Color = Color.Transparent
                    var image: ImageVector = Icons.Rounded.Delete
                    var alignment: Alignment = Alignment.CenterEnd
                    var description: String = "Delete"

                    if (dismissState.dismissDirection == DismissDirection.EndToStart) // iff slide right to left
                    {
                        color = Color.Red
                        image = Icons.Rounded.Delete
                        alignment = Alignment.CenterEnd
                        description = "Delete"
                    } else if (dismissState.dismissDirection == DismissDirection.StartToEnd) // if slide item left to right
                    {
                        color = Color.Green
                        image = Icons.Rounded.Edit
                        alignment = Alignment.CenterStart
                        description = "Edit"

                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color),
                    ) {
                        Icon(
                            imageVector = image,
                            contentDescription = description,
                            modifier = Modifier.align(alignment)
                        )
                    }
                },
                dismissContent = {
                    EntryCard(entry = entry)
                })

            /**
             * from what i understand: the function dismissState.reset() is a "Suspend Function"
             * which makes it act kinda like the asynchronous functions in REACTJS.
             *
             *
             * !!! asynchronous is called coroutineScope in kotlin and the suspend function
             * must be run within this scope
             * this is probably because it is an animation which must be waited upon. Basically
             * the app has to wait until the slide animation is complete before it can "reset" the
             * animation back
             *
             * according to kotlin documentation ->
             * LaunchedEffect: run suspend functions in the scope of a composable
             *
             * this is exactly what i want to do, thus i am using it here
             */
            if (dismissState.currentValue != DismissValue.Default) {
                LaunchedEffect(Unit) {
                    dismissState.reset() // make my entry go back to normal when i cancel delete
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryScreen(
    entries: Database,
    GOTOMAINSCREEN: () -> Unit)
{
    var shouldSubmit by remember { mutableStateOf(false) }
    val id = entries.getAllEntries().size+1
    var title by rememberSaveable { mutableStateOf("")}
    var comments by rememberSaveable { mutableStateOf("")}
    var readFrom by rememberSaveable { mutableStateOf("0") }
    var readTo by rememberSaveable { mutableStateOf("0") }
    var rating by rememberSaveable { mutableStateOf(0) }
    var dateTime by rememberSaveable { mutableStateOf(LocalDateTime.now()) };
    var newEntry by remember { mutableStateOf<Entry>(Entry(id,title)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        //
        TitleBar("Add Book Entry",GOTOMAINSCREEN)
        Divider(
            thickness = 3.dp,
            color = MaterialTheme.colorScheme.inverseSurface
        )
        Spacer(modifier = Modifier.absolutePadding(0.dp,10.dp))
        //edit fields
        TextField(
            value = title,
            onValueChange = {title = it},
            label ={Text("Book Title")},
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )
        DateTimePicker(dateTime,newEntry)

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
            //Check if number entered is a number
            var isNumber: Boolean = true // user might type a . or - first which would crash the app if it were converted
            try {
                readFrom.toDouble()
                readTo.toDouble()
            }catch (e:Exception)
            {
                isNumber = false
                noPageError = false

            }
            // when isNumber = true then safe to cast to double for checks
            if(isNumber) {
                if (readFrom.toDouble() < 0 || readTo.toDouble() < 0) {
                    Text(
                        text = "Page numbers cannot be negative!",
                        color = MaterialTheme.colorScheme.error)
                    noPageError = false
                }

                if (readFrom.toDouble() > readTo.toDouble()) {
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

            StarRating(
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
            label ={Text("Comments")},
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
            newEntry.pageFrom = readFrom.toDouble()
            newEntry.pageTo = readTo.toDouble()
            newEntry.rating = rating
            newEntry.comment = comments
            entries.addEntry(newEntry)
            GOTOMAINSCREEN();
        }
    }
}

@Composable
fun EditScreen(
    selectedEntry: Entry,
    entries: Database,
    GOTOMAINSCREEN: () -> Unit)
{
    var shouldSubmit by remember { mutableStateOf(false) }
    val id = entries.getAllEntries().size+1
    var title by rememberSaveable { mutableStateOf("")}
    var comments by rememberSaveable { mutableStateOf("")}
    var readFrom by rememberSaveable { mutableStateOf("0") }
    var readTo by rememberSaveable { mutableStateOf("0") }
    var rating by rememberSaveable { mutableStateOf(0) }
    var dateTime by rememberSaveable { mutableStateOf(LocalDateTime.now()) };
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
        Text(text = selectedEntry.title)
        //edit fields
        TextField(
            value = title,
            onValueChange = {title = it},
            label ={Text("Book Title")},
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )
        DateTimePicker(dateTime,newEntry)

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
            //Check if number entered is a number
            var isNumber: Boolean = true // user might type a . or - first which would crash the app if it were converted
            try {
                readFrom.toDouble()
                readTo.toDouble()
            }catch (e:Exception)
            {
                isNumber = false
                noPageError = false

            }
            // when isNumber = true then safe to cast to double for checks
            if(isNumber) {
                if (readFrom.toDouble() < 0 || readTo.toDouble() < 0) {
                    Text(
                        text = "Page numbers cannot be negative!",
                        color = MaterialTheme.colorScheme.error)
                    noPageError = false
                }

                if (readFrom.toDouble() > readTo.toDouble()) {
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

            StarRating(
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
            label ={Text("Comments")},
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
            newEntry.pageFrom = readFrom.toDouble()
            newEntry.pageTo = readTo.toDouble()
            newEntry.rating = rating
            newEntry.comment = comments
            entries.addEntry(newEntry)
            GOTOMAINSCREEN();
        }
    }
}
