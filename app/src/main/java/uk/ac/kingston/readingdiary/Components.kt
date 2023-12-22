package uk.ac.kingston.readingdiary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun TitleBar(title: String= "Enter Title", HANDLEBACK: ()->Unit){
    Row(verticalAlignment = Alignment.CenterVertically){
        IconButton(
            onClick = HANDLEBACK,
            modifier = Modifier.size(50.dp)
        ){
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
        }
        Text(text = title,
            style = MaterialTheme.typography.titleLarge)
    }
}
