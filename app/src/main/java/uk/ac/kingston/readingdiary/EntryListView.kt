package uk.ac.kingston.readingdiary

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryListView(
    entries: Database,
    GOTOEDITSCREEN: ()-> Unit,
    GOTOVIEWSCREEN: () -> Unit,
    onEntrySelect: (Entry) -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirm by rememberSaveable { mutableStateOf(false) }
    val hideConfirm = {showConfirm = false}
    var selectedEntry by remember { mutableStateOf<Entry?>(null) }

    var searchText by remember { mutableStateOf("") }
    var searchActive by remember { mutableStateOf(false) }
    var searchHistory = remember { mutableStateListOf<String>() }

    Column {
        SearchBar(
            query = searchText,
            onQueryChange = { searchText = it },
            onSearch = {
                searchHistory.add(searchText)
                searchActive = false
            },
            active = searchActive,
            onActiveChange = { searchActive = it },
            placeholder = {
                Text(text = "Search")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search" )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    Icon(
                        modifier = Modifier.clickable {
                            searchText = ""
                        },
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "clear"
                    )
                }
            }
        ) {
            if (searchText.isNotEmpty()) {
                for (entry in entries.searchEntries(searchText)) {
                    Row(
                        modifier = Modifier
                            .padding(15.dp)
                            .clickable {
                                searchText = entry.title
                                searchHistory.add(entry.title)
                                searchActive = false
                            }
                    ) {
                        Icon(
                            modifier= Modifier.padding(end = 10.dp),
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "search Entry"
                        )
                        Text(text = "${entry.title}")
                    }
                }
                Spacer(modifier = Modifier.absolutePadding(bottom = 10.dp))
            }
            for (text in searchHistory) {
                Row(
                    modifier = Modifier
                        .padding(15.dp)
                        .clickable {
                            searchText = text
                            searchActive = false
                        }
                ) {
                    Icon(
                        modifier= Modifier.padding(end = 10.dp),
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "History Icon"
                    )
                    Text(text = "${text}")
                }
            }

        }

        LazyColumn(
            modifier.padding(vertical = 5.dp),
            state = rememberLazyListState()
        )
        {
            items(items = entries.searchEntries(searchText))
            { entry -> // this is wrong after deleting a few entries
                val dismissState = rememberDismissState(
                    confirmValueChange = {
                        if (it == DismissValue.DismissedToStart) // from right to left DELETE
                        {

                            selectedEntry =
                                entries.getEntryById(entry.id) // directly setting the selectedEntry to the entry from the lazyList gives me the wrong one!!!!! BUT the ID is correct :)
                            Log.i(
                                "tag1",
                                "Selected title: ${entries.getEntryById(entry.id)?.title}"
                            )
                            Log.i("tag1", "Selected id: ${entry.id}")
                            Log.i("tag1", "  INSIDE SLIDE ${entries.getAllEntries().toString()}")
                            showConfirm = true
                        }
                        if (it == DismissValue.DismissedToEnd) // from left to right EdIT
                        {
                            onEntrySelect(entry) // o7 safe journey up to main activity then to app
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
                        EntryCard(entry, GOTOVIEWSCREEN,
                            onViewSelect = {
                                onEntrySelect(it) // SEND it UP!!!
                            })
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

    // delete confirmation modal
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text(text = "Delete Confirmation") },
            text = { Text(text = "Delete Book: ${selectedEntry?.title}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedEntry?.id?.let { entries.deleteEntry(it, hideConfirm) }
                    },
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
}

