package uk.ac.kingston.readingdiary

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.Calendar


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
fun DateTimePicker(dateTime: LocalDateTime, entry: Entry){

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
    val myCalendar = Calendar.getInstance();
    if (selectedDate != null) {
        myCalendar.calendarType
        myCalendar.timeInMillis = selectedDate
        //handle month
        //the date picker starts with jan = 0 not 1 so everything is behind bt one
        myCalendar.add(Calendar.MONTH,1)
        var month = myCalendar.get(Calendar.MONTH)

        if(myCalendar.get(Calendar.MONTH).equals(0)) // december is a special case
        {
            month = 12; // set december to its actual month
            myCalendar.add(Calendar.YEAR,-1) // the year is incorrect for december so we must manually subtract a year
        }

        var year = myCalendar.get(Calendar.YEAR)
        var day = myCalendar.get(Calendar.DAY_OF_MONTH)
        //var hour = myCalendar.get(Calendar.HOUR)
       // var minute = myCalendar.get(Calendar.MINUTE)
        var nowTime = LocalTime.now()

        try{
            var newDateTime = LocalDateTime.of(year,month,day,nowTime.hour,nowTime.minute)
            entry.dateTime= LocalDateTime.of(newDateTime.toLocalDate(),nowTime)
        }catch (e: Exception) {
            Text(
                text = "Invalid date Entered, ${month}",
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    
    





}
