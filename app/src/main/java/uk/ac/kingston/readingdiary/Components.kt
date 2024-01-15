package uk.ac.kingston.readingdiary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
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
import androidx.compose.ui.graphics.Color
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
            Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
        }
        Text(text = title,
            style = MaterialTheme.typography.titleLarge)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(
    dateTime: LocalDateTime,
    entry: Entry)
{
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

    val selectedDate = datePickerState.selectedDateMillis

    val myCalendar = Calendar.getInstance()

    if (selectedDate != null) {
        myCalendar.calendarType
        myCalendar.timeInMillis = selectedDate

        //the date picker starts with jan = 0 not 1 so everything is behind
        myCalendar.add(Calendar.MONTH,1)
        var month = myCalendar.get(Calendar.MONTH)

        if(myCalendar.get(Calendar.MONTH).equals(0)) // december is a special case
        {
            month = 12 // set december to its actual month
            myCalendar.add(Calendar.YEAR,-1) // the year is incorrect for december so we must manually subtract a year
        }

        val year = myCalendar.get(Calendar.YEAR)
        val day = myCalendar.get(Calendar.DAY_OF_MONTH)
        val nowTime = LocalTime.now()

        // in try catch in case the date is incorrect
        try{
            val newDateTime = LocalDateTime.of(year,month,day,nowTime.hour,nowTime.minute)
            entry.dateTime= LocalDateTime.of(newDateTime.toLocalDate(),nowTime)
        }catch (e: Exception) {
            Text(
                text = "Invalid date Entered, ${month}",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

// getting the hearts to work
@Composable
fun HeartRating(
    isClickable: Boolean = true,
    modifier: Modifier = Modifier,
    rating: Int = 0,
    stars: Int = 5,
    starsColor: Color = Color.Red,
    onRatingChange: (Int) -> Unit
){
    Row{
        for(i in 1.. stars)
        {
            //i don't want all start to be clickable( the one on display)
            if(isClickable)
            {
                Icon(
                    modifier = modifier.clickable { onRatingChange(i) }, //get the index number and set that to the rating
                    contentDescription = "star rating",
                    tint = starsColor,
                    // print me the filled heart if the index we are on is less than or equal to rating
                    imageVector = if (i <= rating) {
                        Icons.Rounded.Favorite
                    } else {
                        Icons.Rounded.FavoriteBorder // the rest are outlines
                    }
                )
            }else{
                Icon(
                    contentDescription = "star rating",
                    tint = starsColor,
                    imageVector = if (i <= rating) {
                        Icons.Rounded.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    }
                )
            }

        }
    }
}

@Composable
fun MyDisplayText(
  text: String = "",
  title: String = "",
  bookTitle: Boolean = false,
  modifier: Modifier = Modifier
){
    if(bookTitle)
    {
        Column(modifier = modifier) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = text,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
    else{
        Column(modifier = modifier) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


