package uk.ac.kingston.readingdiary

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

class Database{
    private var entryList= mutableStateListOf<Entry>()// make sure this is a StateList so that things reload properly
    private var selectedEntry: Entry? =null
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
       // resetList()
        return entryList.find {it.id == id}
    }

    fun getEntryByTitle(title: String): Entry?{
        return entryList.find { it.title.lowercase() == title.lowercase() } // not case sensitive search
    }

    fun searchEntries(search: String): List<Entry>{
        return entryList.filter {it.title.contains(search, ignoreCase = true) }
    }
    fun updateItem(id: Int, newItem: Entry): Boolean
    {
        val existingEntry = getEntryById(id)
        if (existingEntry != null){
            existingEntry.title = newItem.title
            existingEntry.pageFrom = newItem.pageFrom
            existingEntry.pageTo = newItem.pageTo
            existingEntry.rating = newItem.rating
            existingEntry.comment = newItem.comment
            Log.i("tag1","Entry updated successfully")
            return true
        }
        else{
            Log.i("tag1","Entry with id ${id} not found. Cannot update")
            return false
        }
    }
    fun deleteEntry(id: Int, onDelete: ()->Unit): Boolean{
        val existingEntry = getEntryById(id)
        if (existingEntry != null) {
            entryList.remove(existingEntry)
            Log.i("tag1","Item deleted = ${id}")
            Log.i("tag1","${entryList.size}")

            if(entryList.isNotEmpty())
            {
                resetList() // make this list work please for the other deletes
            }
            onDelete()
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

    fun getEntries(): MutableList<Entry>{
        return entryList
    }

    fun sortByTitle()
    {
        entryList = entryList.sortedBy { it.title }.toMutableStateList()
        //resetList()
    }

    fun sortByDate()
    {
       entryList = entryList.sortedByDescending { it.dateTime }.toMutableStateList()
      // resetList()
    }
    fun sortByCreation()
    {
        entryList = entryList.sortedBy { it.creationOrder }.toMutableStateList()
      // resetList()
    }


    /**
     *   Basically I have to re shuffle the entire entryList so that
     *   the selection IDs and Adding works
     */
    fun resetList(newList: MutableList<Entry> = mutableListOf()){
        for (entry in entryList)
        {
            newList.add(entry)

        }
        for(i in 0 until newList.size)
        {
            entryList.remove(newList.get(i))
        }
        for(i in 0 until newList.size){
            Log.i("tag1"," ${i} ENTRYLIST ${entryList.toString()}")
            Log.i("tag1"," BEFORE new entry in newList ${newList.get(i).toString()}")
            newList.get(i).resetID(i)
            Log.i("tag1"," AFTER new entry in newList ${newList.get(i).toString()}")
            Log.i("tag1","  ENTRYLIST SIZE ${entryList.size}")

            entryList.add(newList.get(i))
            Log.i("tag1","  AFTER ENTRYLIST SIZE ${entryList.size}")
            Log.i("tag1"," REAWAKEND ENTRYLIST ${entryList.get(i)}")
            // entryList.add(newList.get(i))
        }
    }
}

