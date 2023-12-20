package uk.ac.kingston.readingdiary

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class Database {
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
fun entryList(modifier: Modifier = Modifier){
    LazyColumn (modifier.padding(vertical = 5.dp)){
        items(items = names)
        {
                name -> Greeting(name = name)
        }
    }
}