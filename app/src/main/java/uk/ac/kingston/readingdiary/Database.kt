package uk.ac.kingston.readingdiary

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
            Log.i("tag1","Entry deleted successfully")
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

@Composable
fun EntryList(entries: Database,modifier: Modifier = Modifier){
    LazyColumn (modifier.padding(vertical = 5.dp)){
        items(items = entries.getAllEntries())
        {
            entry -> EntryCard(entry = entry)
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
    var title by remember { mutableStateOf("")}
    var dateTime by remember { mutableStateOf(LocalDateTime.now()) };
    var newEntry by remember { mutableStateOf<Entry>(Entry(id,title)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        //
        TitleBar("Add Book Entry",GOTOMAINSCREEN)
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

        if(title.isNotEmpty())
        {
            // Button tray
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                IconButton(
                    onClick = GOTOMAINSCREEN,
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                }
                IconButton(
                    onClick = { shouldSubmit = true },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = null)
                }
            }
        }

        if(shouldSubmit)
        {
            newEntry.title = title
            entries.addEntry(newEntry)
            GOTOMAINSCREEN();
        }
    }
}