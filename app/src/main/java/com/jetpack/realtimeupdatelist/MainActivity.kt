package com.jetpack.realtimeupdatelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.jetpack.realtimeupdatelist.model.RealTimeUpdateItem
import com.jetpack.realtimeupdatelist.ui.theme.RealTimeUpdateListTheme
import com.jetpack.realtimeupdatelist.viewmodel.RealTimeUpdateViewModel

class MainActivity : ComponentActivity() {
    private val realTimeUpdateViewModel: RealTimeUpdateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RealTimeUpdateListTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Real Time Update Item",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        }
                    ) {
                        RealTimeUpdateList(realTimeUpdateViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun RealTimeUpdateList(viewModel: RealTimeUpdateViewModel) {
    val realTimeUpdateItem = viewModel.realTimeUpdateItem.collectAsState()

    LazyColumn {
        itemsIndexed(realTimeUpdateItem.value) { index, item ->
            RealTimeUpdateItemCard(
                realTimeUpdateItem = item,
                onDownloadClick = {
                    viewModel.onDownloadRealTimeUpdateItemClicked(item.id, index)
                }
            )
        }
    }
}

@Composable
fun RealTimeUpdateItemCard(
    realTimeUpdateItem: RealTimeUpdateItem,
    onDownloadClick: () -> Unit
) {
    val title = remember { realTimeUpdateItem.title }
    val isDownloaded = realTimeUpdateItem.downloadProgress == 100
    val animatedProgress: Float by animateFloatAsState(targetValue = realTimeUpdateItem.downloadProgress / 100f)
    val black = Color.Black
    val white = Color.White
    val green = Color(
        ContextCompat.getColor(
            LocalContext.current,
            R.color.opacity_green
        )
    )
    Card(
        backgroundColor = white,
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row {
            Text(
                text = title,
                modifier = Modifier.padding(16.dp),
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .height(56.dp)
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            ) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.CenterEnd),
                    progress = animatedProgress,
                    color = if (isDownloaded) green else black
                )
                if (isDownloaded) {
                    Icon(
                        modifier = Modifier
                            .size(36.dp)
                            .padding(8.dp)
                            .align(Alignment.CenterEnd),
                        painter = painterResource(id = R.drawable.ic_baseline_done_24),
                        tint = green,
                        contentDescription = "Success Icon"
                    )
                } else {
                    IconButton(
                        onClick = onDownloadClick,
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.CenterEnd),
                        content = {
                            Icon(
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.Center),
                                painter = painterResource(id = R.drawable.ic_download),
                                tint = black,
                                contentDescription = "Download Icon"
                            )
                        }
                    )
                }
            }
        }
    }
}






















