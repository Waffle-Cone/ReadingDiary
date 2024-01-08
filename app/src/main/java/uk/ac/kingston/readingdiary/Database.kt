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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime

class Database{
    private val entryList = mutableListOf<Entry>()
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
fun EntryList(
    entries: Database,
    modifier: Modifier = Modifier,
    ONITEMDELETE: () -> Unit,
    ONITEMKEEP: () -> Unit,
){
    var showConfirm by rememberSaveable{ mutableStateOf(false) }
    var selectedEntry by remember{mutableStateOf<Entry?>(null)}


    if(showConfirm)
    {
        AlertDialog(
            onDismissRequest = {showConfirm = false},
            title = { Text(text = "hello")},
            text = { Text(text = "dgdag")},
            confirmButton ={
                TextButton(
                    onClick = { ONITEMDELETE()},
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {

            }
        )
    }

    LazyColumn (
        modifier.padding(vertical = 5.dp),
        state = rememberLazyListState()
    )
    {
        items(items = entries.getAllEntries())
        { entry ->
            val state = rememberDismissState(
                confirmValueChange = {
                    if(it == DismissValue.DismissedToStart) // left to right
                    {
                        //entries.deleteEntry(entry.id)
                        selectedEntry = entry
                        showConfirm = true
                    }
                    true
                }
            )
            SwipeToDismiss(
                directions= listOf<DismissDirection>(DismissDirection.EndToStart).toSet(),
                state = state,
                background = {
                        var color: Color = Color.Transparent

                         if(state.dismissDirection == DismissDirection.EndToStart)
                         {
                            color = Color.Red
                         }
                        else if(state.dismissDirection == DismissDirection.StartToEnd)
                        {
                            color = Color.Green
                        }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color),
                        ){
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.align(Alignment.CenterEnd)
                            )
                    }
                } ,
                dismissContent = {
                    EntryCard(entry = entry)
            })
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