package com.xz.schoolnavinfo.presentation.campus.stuff.pub

import android.app.Activity
import android.content.Intent
import android.view.Gravity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
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
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.createImagePickerIntent
import com.esafirm.imagepicker.model.Image
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.utils.JsonUtils
import com.xz.schoolnavinfo.presentation.LocalAppNavigator
import com.xz.schoolnavinfo.presentation.Routes
import com.xz.schoolnavinfo.presentation.common.components.CustomTopBar
import com.xz.schoolnavinfo.presentation.common.components.LoadingDialog
import com.xz.schoolnavinfo.presentation.common.components.LocationBox
import com.xz.schoolnavinfo.presentation.common.components.MyButton
import com.xz.schoolnavinfo.presentation.theme.AppColors
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar

@Composable
fun StuffPublishScreen(
    publishStuffViewModel: PublishStuffViewModel = hiltViewModel()
) {
    val navigator = LocalAppNavigator.current
    PublishStuffContent(
        publishStuffViewModel = publishStuffViewModel,
    )
    LaunchedEffect(Unit) {
        publishStuffViewModel.netOver.collectLatest {
            navigator.popBack()
        }
    }
    val locationData = navigator.getLocationData()
    if (!locationData.isNullOrEmpty()) {
        publishStuffViewModel.setLocation(JsonUtils.fromJson(locationData))
    }
}

@Composable
fun PublishStuffContent(
    publishStuffViewModel: PublishStuffViewModel,
) {
    val navigator = LocalAppNavigator.current
    val appColors = AppColors.current
    val statusBarPadding = WindowInsets.systemBars.asPaddingValues()
    val (showDateDialog, setShowDateDialog) = rememberSaveable { mutableStateOf(false) }
    val uiState by publishStuffViewModel.uiState.collectAsStateWithLifecycle()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = ImagePicker.getImages(result.data)
            data?.let {
                publishStuffViewModel.setImage(it.first())
            }
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
            LoadingDialog(uiState.loading)

            PublishStuffTopBar(
                onBack = { navigator.popBack() },
                isEmpty = publishStuffViewModel.isContentEmpty(),
                onSend = { publishStuffViewModel.publishStuff() }
            )

            PublishStuffBody(
                desc = uiState.desc,
                address = uiState.address,
                date = uiState.happenTime,
                image = uiState.image,
                showDateDialog = showDateDialog,
                setShowDateDialog = setShowDateDialog,
                imagePickerLauncher = imagePickerLauncher,
                onDescChange = { publishStuffViewModel.setDesc(it) },
                onTypeChange = { publishStuffViewModel.setType(it) },
                onLocationSelect = { navigator.navigate(Routes.LocationSelect) },
                onDateDialog = { setShowDateDialog(true) },
                onDateSelected = { publishStuffViewModel.setHappenTime(it) }
            )
        }
    }
}

@Composable
fun PublishStuffBody(
    desc: String,
    address: String,
    date: String,
    image: Image?,
    showDateDialog: Boolean,
    setShowDateDialog: (Boolean) -> Unit,
    imagePickerLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    onDescChange: (String) -> Unit,
    onTypeChange: (Boolean) -> Unit,
    onLocationSelect: () -> Unit,
    onDateDialog: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    Spacer(Modifier.height(2.dp))
    StuffType(onTypeChange = onTypeChange)
    Spacer(Modifier.height(2.dp))
    ImageAndDesc(
        imagePickerLauncher = imagePickerLauncher,
        image = image,
        desc = desc,
        onDescChange = onDescChange
    )
    Spacer(Modifier.height(8.dp))
    LocationAndDate(
        address = address,
        date = date,
        onLocationSelect = onLocationSelect,
        onDateDialog = onDateDialog
    )
    DateSelectDialog(
        onDateSelected = onDateSelected,
        showDateDialog = showDateDialog,
        setShowDateDialog = setShowDateDialog
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectDialog(
    showDateDialog: Boolean,
    setShowDateDialog: (Boolean) -> Unit,
    onDateSelected: (String) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    var showTimeDialog by remember { mutableStateOf(false) }
    var selectedDateText by remember { mutableStateOf("") }
    var selectedTimeText by remember { mutableStateOf("") }
    val selectedDateAndTimeText by remember {
        derivedStateOf {
            "$selectedDateText $selectedTimeText"
        }
    }

    if (showDateDialog) {
        DatePickerDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    setShowDateDialog(false)
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
                TextButton(onClick = { setShowDateDialog(false) }) {
                    selectedDateText = ""
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimeDialog) {
        TimePickerDialog(
            onConfirm = { hour, minute ->
                showTimeDialog = false
                selectedTimeText = "$hour:$minute:00"
                onDateSelected(selectedDateAndTimeText)
            },
            onDismiss = {
                selectedDateText = ""
                selectedTimeText = ""
                showTimeDialog = false
            }
        )
    }
}

@Composable
fun LocationAndDate(
    address: String = "",
    date: String = "",
    onLocationSelect: () -> Unit,
    onDateDialog: () -> Unit,
) {
    val appColors = AppColors.current
    Box(Modifier.padding(start = 10.dp)) {
        LocationBox(address.ifBlank { "选择地址.." }) {
            onLocationSelect()
        }
    }
    Spacer(Modifier.height(2.dp))
    Row {
        Box(
            modifier = Modifier
                .padding(start = 10.dp, top = 5.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(appColors.greyMedium.copy(.5f))
                    .clickable(interactionSource = null, indication = null) {
                        onDateDialog()
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
                    text = date.ifBlank { "选择日期.." },
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = appColors.greyHeavy
                    )
                )
            }
        }
    }
}

@Composable
fun ImageAndDesc(
    imagePickerLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    image: Image?,
    desc: String,
    onDescChange: (String) -> Unit
) {
    val appColors = AppColors.current
    val context = LocalContext.current
    val isSystemInDarkTheme = isSystemInDarkTheme()

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
            if (image != null) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    painter = rememberAsyncImagePainter(image.path),
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

        Box(
            modifier = Modifier
                .padding(start = 5.dp, end = 10.dp)
                .border(2.dp, appColors.greyMedium.copy(.5f), RoundedCornerShape(10.dp))
        ) {
            BasicTextField(
                value = desc,
                onValueChange = {
                    onDescChange(it)
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
                    if (desc.isBlank()) {
                        Text(
                            text = "描述...",
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
}

@Composable
fun StuffType(
    onTypeChange: (Boolean) -> Unit
) {

    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("拾物", "寻物")
    val appColors = AppColors.current
    SingleChoiceSegmentedButtonRow(
        Modifier
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
                    onTypeChange(index == 1)
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
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = appColors.bgPrimary,
                    inactiveContainerColor = appColors.bgPrimary,

                    activeBorderColor = appColors.primary,
                    inactiveBorderColor = appColors.greyMedium.copy(.5f)
                )
            )
        }
    }
}

@Composable
private fun PublishStuffTopBar(
    onBack: () -> Unit,
    isEmpty: Boolean,
    onSend: () -> Unit
) {
    val context = LocalContext.current
    CustomTopBar(
        title = "物品",
        onBack = onBack,
        rightContent = {
            MyButton(text = "发布") {
                if (isEmpty) {
                    StyleableToast.Builder(context)
                        .text("图片、描述、日期、地址不能为空")
                        .textColor(Color.White.toArgb())
                        .backgroundColor(Color(0xFF0091EA).toArgb())
                        .cornerRadius(36)
                        .gravity(Gravity.TOP)
                        .show()
                } else {
                    onSend()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
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


