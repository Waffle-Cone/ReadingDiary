package uk.ac.kingston.readingdiary

import android.widget.CalendarView
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.Calendar
import kotlin.time.Duration.Companion.days


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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(dateTime:LocalDateTime){

    val datePickerState = remember {
        DatePickerState(
            yearRange = (2022..2024),
            initialSelectedDateMillis = dateTime.toEpochSecond(ZoneOffset.UTC) *1000,
            initialDisplayMode = DisplayMode.Input,
            initialDisplayedMonthMillis = null
        )
    }

    DatePicker(
        state = datePickerState
    )

    var selectedDate = datePickerState.selectedDateMillis
    val calander = Calendar.getInstance();
    if (selectedDate != null) {
        calander.calendarType
        calander.timeInMillis = selectedDate
      //  calander.add(Calendar.DAY_OF_MONTH,1)

        var year = calander.get(Calendar.YEAR)
        var month = calander.get(Calendar.MONTH)
        var day = calander.get(Calendar.DAY_OF_MONTH)
        var hour = calander.get(Calendar.HOUR)
        var minute = calander.get(Calendar.MINUTE)

        var newDateTime = LocalDateTime.of(year,month,day,hour,minute)
        Text(text = "${newDateTime.toString()}")
    }
    





}
