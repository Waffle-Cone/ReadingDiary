package uk.ac.kingston.readingdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
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

    var shouldShowAddScreen by rememberSaveable { mutableStateOf(false) }
    val SHOWADDSCREEN = {shouldShowAddScreen = true}
    val HIDEADDSCREEN = {shouldShowAddScreen = false}

    var shouldShowEditScreen by rememberSaveable { mutableStateOf(false) }
    val SHOWEDITSCREEN = {shouldShowEditScreen = true}
    val HIDEEDITSCREEN = {shouldShowEditScreen = false}

    Surface(modifier,
            color = MaterialTheme.colorScheme.background
    ){
        if (shouldShowAddScreen){
            AddEntryScreen(entries,HIDEADDSCREEN)
        }
        else if(shouldShowEditScreen)
        {
           selectedEntry?.let { EditScreen(it,entries,HIDEEDITSCREEN) }
            //Database().getSelectedEntry()?.let { EditScreen(it,entries,HIDEEDITSCREEN) }
        }
        else{
            MainScreen(
                entrySelected ={
                    selectedEntry = it
                },
                entries,SHOWADDSCREEN,SHOWEDITSCREEN)
        }

    }
}

@Composable
fun MainScreen(
    entrySelected: (Entry) -> Unit,
    entries: Database,
    GOTOADDSCREEN: () -> Unit,
    GOTOEDITSCREEN: () -> Unit
){
    var ONDELETE by remember { mutableStateOf(false) }
    val pleaseReset = {ONDELETE = true}
    val resetOnDelete = {ONDELETE = false}
    

    if(ONDELETE)
    {
        Text(text = "hgvhv")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    )
    {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ){
            IconButton(
                onClick = GOTOADDSCREEN,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(imageVector = Icons.Rounded.AddCircle, contentDescription = null)
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
                EntryListView(entries,GOTOEDITSCREEN,
                    onEntrySelect ={
                        entrySelected(it)
                    })
            }
        }
    }
}

