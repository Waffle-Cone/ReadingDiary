package uk.ac.kingston.readingdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.SortByAlpha
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uk.ac.kingston.readingdiary.ui.theme.ReadingDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReadingDiaryTheme {
                    MyApp( modifier = Modifier.fillMaxSize())
                }
            }
        }
    }


@Composable
private fun MyApp(modifier: Modifier = Modifier) {
    val entries  by remember {mutableStateOf(Database())}
    var selectedEntry by remember { mutableStateOf<Entry?>(null) }
    
    var shouldShowWelcome by rememberSaveable { mutableStateOf(true) }

    var shouldShowAddScreen by rememberSaveable { mutableStateOf(false) }
    val SHOWADDSCREEN = {shouldShowAddScreen = true}
    val HIDEADDSCREEN = {shouldShowAddScreen = false}
    var ONUPDATE: ()->Unit ={}

    var shouldShowEditScreen by rememberSaveable { mutableStateOf(false) }
    val SHOWEDITSCREEN = {shouldShowEditScreen = true}
    val HIDEEDITSCREEN = {shouldShowEditScreen = false}

    var shouldShowViewScreen by rememberSaveable { mutableStateOf(false) }
    val SHOWVIEWSCREEN = {shouldShowViewScreen = true}
    val HIDEVIEWSCREEN = {shouldShowViewScreen = false}


    Surface(modifier,
            color = MaterialTheme.colorScheme.background
    ){
        if(!shouldShowWelcome) {
            if (shouldShowAddScreen) {
                AddEntryScreen(entries, HIDEADDSCREEN)
            } else if (shouldShowEditScreen) {
                selectedEntry?.let { EditScreen(it, entries, HIDEEDITSCREEN,ONUPDATE) }
            } else if (shouldShowViewScreen) {
                selectedEntry?.let { ViewEntry(it, entries, HIDEVIEWSCREEN) }
            } else {
                MainScreen(
                    entrySelected = {
                        selectedEntry = it
                    },
                    entries, ONUPDATE = {ONUPDATE = it},SHOWADDSCREEN, SHOWEDITSCREEN, SHOWVIEWSCREEN
                )
            }
        }else{
            WelcomeScreen(ONGoToScheduleClicked = {shouldShowWelcome = false})
        }

    }
}

@Composable
fun WelcomeScreen(
    ONGoToScheduleClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text ("welcome to your Reading Diary")
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = ONGoToScheduleClicked){
            Text("Go to Diary")
        }
    }
}

@Composable
fun MainScreen(
    entrySelected: (Entry) -> Unit,
    entries: Database,
    ONUPDATE: (()->Unit)->Unit,
    GOTOADDSCREEN: () -> Unit,
    GOTOEDITSCREEN: () -> Unit,
    GOTOVIEWSCREEN: () -> Unit
){

    var sortBy by rememberSaveable { mutableStateOf<String?>(null) }

    var isSorting by rememberSaveable { mutableStateOf(false) }

    val ONCREATIONSORT = {entries.sortByCreation()}
    val ONDATESORT = { entries.sortByDate() }
    val ONTITLESORT = { entries.sortByTitle() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    )
    {
        Row {
            Row(
                modifier = Modifier
                    .weight(.5f)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start
            ) 
            {
                var expanded by remember { mutableStateOf(false) }
                IconButton(
                    onClick = {expanded = true},
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.Sort, contentDescription = "sort")
                }
                DropdownMenu(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    expanded = expanded,
                    onDismissRequest = {expanded = false}
                    ){
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ){
                        Row(
                            modifier= Modifier
                                .clickable {
                                    ONCREATIONSORT()
                                    sortBy = null
                                    isSorting = false
                                    expanded = false
                                }
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 14.dp),
                                imageVector = Icons.Rounded.Create,
                                contentDescription = "ID"
                            )
                            Text(text = "Created")
                        }

                        Row(
                            modifier= Modifier
                                .clickable {
                                    ONDATESORT()
                                    isSorting = true
                                    sortBy = "Date"
                                    expanded = false
                                }
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 14.dp),
                                imageVector = Icons.Rounded.DateRange,
                                contentDescription = "sortByDate"
                            )
                            Text(text = "Date")
                        }
                        Row(
                            modifier= Modifier
                                .clickable {

                                    ONTITLESORT()
                                    isSorting = true
                                    sortBy = "Title"
                                    expanded = false
                                }
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 14.dp),
                                imageVector = Icons.Rounded.SortByAlpha,
                                contentDescription = "sortByAlphabeticalOrder"
                            )
                            Text(text = "Title")
                        }
                    }

                }
            }
            Row(
                modifier = Modifier
                    .weight(.5f)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = GOTOADDSCREEN,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.AddCircle, contentDescription = "Add entry")
                }
            }
        }



        Divider(
            thickness = 3.dp,
            color = MaterialTheme.colorScheme.inverseSurface
        )
        Spacer(modifier = Modifier.absolutePadding(0.dp,10.dp))

        
        
        if(entries.getAllEntries().isEmpty())
        {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                IconButton(
                    onClick = GOTOADDSCREEN,
                    modifier = Modifier.size(50.dp),

                    ) {
                    Icon(
                        imageVector = Icons.Rounded.AddCircle,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp))
                }
                Text(text = "Add First Entry")
            }
        }else {
            Row {
                EntryListView(
                    entries,
                    sortBy = sortBy,
                    isSorting,
                    GOTOEDITSCREEN,
                    GOTOVIEWSCREEN,
                    ONUPDATE= {ONUPDATE(it)},
                    onEntrySelect = {
                        entrySelected(it)
                    })
            }

        }
    }
}

