package com.xz.schoolnavinfo.presentation.campus.stuff.pub

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.createImagePickerIntent
import com.esafirm.imagepicker.model.Image
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.presentation.common.compose.LoadingDialog
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishStuffScreen(
    commonViewModel: CommonViewModel,
    publishStuffViewModel: PublishStuffViewModel = hiltViewModel()
) {
    val appColors = AppColors.current
    //var descText =
    val statusBarPadding = WindowInsets.systemBars.asPaddingValues()

    val stuffInfo = publishStuffViewModel.pubStuff

    val context = LocalContext.current
    val isSystemInDarkTheme = isSystemInDarkTheme()

    val selectImage = publishStuffViewModel.selectImage

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = ImagePicker.getImages(result.data)
            data?.let {
                publishStuffViewModel.onEvent(PublishStuffEvent.ImageChange(it.first()))
            }
        }
    }

    LaunchedEffect(true) {
        commonViewModel.selectLocationFlow.collectLatest {
            publishStuffViewModel.onEvent(PublishStuffEvent.LocationChange(it))
        }
    }

    LaunchedEffect(true) {
        publishStuffViewModel.netOver.collectLatest {
            commonViewModel.onNavEvent(NavEvent.BackPage)
        }
    }

    Box(
        modifier = Modifier
            .background(appColors.bgPrimary)
            .fillMaxSize()
            .padding(statusBarPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            LoadingDialog(publishStuffViewModel.isShowLoading.value)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .clickable { commonViewModel.onNavEvent(NavEvent.BackPage) },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 10.dp),
                        text = "物品",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = appColors.fontPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(Modifier.padding(end = 10.dp)) {
                    Box(
                        modifier = Modifier
                            .shadow(3.dp, RoundedCornerShape(10.dp))
                            .background(appColors.primary)
                            .clip(RoundedCornerShape(10.dp))
                            .width(60.dp)
                            .height(30.dp)
                            .clickable {
                                publishStuffViewModel.onEvent(PublishStuffEvent.PubStuff)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "发布",
                            style = TextStyle(
                                color = appColors.onButtonColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
            //类别
            var selectedIndex by remember { mutableIntStateOf(0) }
            val options = listOf("拾物", "寻物")
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        modifier = Modifier
                            .weight(1f),
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size,
                            baseShape = RoundedCornerShape(10.dp)
                        ),
                        onClick = {
                            publishStuffViewModel.onEvent(PublishStuffEvent.TypeChange(index == 1))
                            selectedIndex = if (selectedIndex == 0) 1 else 0
                        },
                        selected = index == selectedIndex,
                        label = {
                            Text(
                                text = label,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = appColors.fontPrimary
                                )
                            )
                        },
                        //border = BorderStroke(2.dp, appColors.greyMedium),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = appColors.bgPrimary,
                            inactiveContainerColor = appColors.bgPrimary,

                            activeBorderColor = appColors.primary,
                            inactiveBorderColor = appColors.greyMedium.copy(.5f)
                        )
                    )
                }
            }

            //图片内容
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .size(128.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(appColors.greyMedium.copy(.5f))
                        .clickable {
                            val intent = createImagePickerIntent(
                                context, ImagePickerConfig(
                                    limit = 1,
                                    theme = if (isSystemInDarkTheme) R.style.ImagePickerThemeDark else R.style.ImagePickerThemeLight
                                )
                            )
                            imagePickerLauncher.launch(intent)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectImage != null) {
                        Image(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            painter = rememberAsyncImagePainter(selectImage!!.path),
                            contentDescription = null
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .width(26.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(appColors.greyMedium)

                        )
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(26.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(appColors.greyMedium)

                        )
                    }
                }
                //内容
                Box(
                    modifier = Modifier
                        .padding(start = 5.dp, end = 10.dp)
                        .border(2.dp, appColors.greyMedium.copy(.5f), RoundedCornerShape(10.dp))
                ) {
                    BasicTextField(
                        value = stuffInfo.desc,
                        onValueChange = {
                            publishStuffViewModel.onEvent(PublishStuffEvent.DescChange(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 156.dp, max = 128.dp)
                            .padding(5.dp),
                        maxLines = 100,
                        minLines = 1,
                        textStyle = TextStyle(
                            color = appColors.fontPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        decorationBox = {
                            if (stuffInfo.desc.isBlank()) {
                                Text(
                                    text = "描述 ..",
                                    style = TextStyle(
                                        color = appColors.greyHeavy,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                            it()
                        }
                    )

                }
            }
            //位置
            Row {
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(appColors.greyMedium.copy(.5f))
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ) {
                                commonViewModel.onNavEvent(NavEvent.MapLocationSelect)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .size(20.dp),
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = appColors.fontSecondary
                        )
                        Text(
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                                .padding(end = 10.dp),
                            text = stuffInfo.address.ifBlank { "选择位置.." },
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = appColors.greyHeavy
                            )
                        )
                    }
                }
            }
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = Instant.now().toEpochMilli(),
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis <= System.currentTimeMillis()
                    }
                }
            )
            var showDateDialog by remember { mutableStateOf(false) }
            var showTimeDialog by remember { mutableStateOf(false) }
            var selectedDateText by remember { mutableStateOf("") }
            var selectedTimeText by remember { mutableStateOf("") }

            val selectedDateAndTimeText by remember {
                derivedStateOf {
                    "$selectedDateText $selectedTimeText"
                }
            }
            //日期选择
            if (showDateDialog) {
                DatePickerDialog(
                    onDismissRequest = { },
                    confirmButton = {
                        TextButton(onClick = {
                            showDateDialog = false
                            datePickerState.selectedDateMillis?.let {
                                val date = Instant.ofEpochMilli(it)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                selectedDateText = date.toString()
                            }
                            showTimeDialog = true
                        }) {
                            Text("确定")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDateDialog = false }) {
                            selectedDateText = ""
                            Text("取消")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            //时间选择
            if (showTimeDialog) {
                TimePickerDialogCompose(
                    onConfirm = { hour, minute ->
                        showTimeDialog = false
                        selectedTimeText = "$hour:$minute:00"
                        publishStuffViewModel.onEvent(
                            PublishStuffEvent.DateTimeChange(
                                selectedDateAndTimeText
                            )
                        )
                    },
                    onDismiss = {
                        selectedDateText = ""
                        selectedTimeText = ""
                        showTimeDialog = false
                    }
                )
            }
            //日期
            Row {
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(appColors.greyMedium.copy(.5f))
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ) {
                                showDateDialog = true
                                selectedDateText = ""
                                selectedTimeText = ""
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .size(20.dp),
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = appColors.fontSecondary
                        )
                        Text(
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                                .padding(end = 10.dp),
                            text = stuffInfo.happenTime.ifBlank { "选择日期.." },
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = appColors.greyHeavy
                            )
                        )
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogCompose(
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
    is24Hour: Boolean = true,
    initialHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
    initialMinute: Int = Calendar.getInstance().get(Calendar.MINUTE),
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour,
    )

    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.End
            ) {
                TimePicker(state = timePickerState)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    TextButton(onClick = {
                        onConfirm(timePickerState.hour, timePickerState.minute)
                    }) {
                        Text("确定")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDia() {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )
    Column {
        TimePicker(
            state = timePickerState,
        )
        Button(onClick = {

        }) {
            Text("Dismiss picker")
        }
        Button(onClick = {

        }) {
            Text("Confirm selection")
        }
    }
}