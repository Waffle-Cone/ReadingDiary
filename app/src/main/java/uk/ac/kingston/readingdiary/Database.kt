package uk.ac.kingston.readingdiary

import android.util.Log
import androidx.compose.runtime.mutableStateListOf

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
        return entryList.find {it.id == id}
    }
    fun updateItem(id: Int, newItem: Entry): Boolean
    {
        val existingEntry = getEntryById(id)
        if (existingEntry != null){
            existingEntry.title = newItem.title
            existingEntry.dateTime = newItem.dateTime
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
            onDelete()
            Log.i("tag1","${entryList.size}")

            if(entryList.isNotEmpty())
            {
                resetList() // make this list work please for the other deletes
            }
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

    /**
     *   Basically I have to re shuffle the entire entryList so that the selection IDs work.
     *   THIS fixes the error of when i have three items and delete the center one. I wouldnt be able to properly delete the others
     *   without refreshing the page
     */

    fun resetList(newList: MutableList<Entry> = mutableListOf()){
        for (entry in entryList)
        {
            newList.add(entry)

        }
        for(i in 0 until newList.size){
            Log.i("tag1"," ${i} ENTRYLIST ${entryList.toString()}")
            entryList.remove(newList.get(i))

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

