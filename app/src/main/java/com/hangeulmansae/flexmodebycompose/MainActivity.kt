package com.hangeulmansae.flexmodebycompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import com.hangeulmansae.flexmodebycompose.ui.theme.FlexModeComposeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val windowInfoTracker = WindowInfoTracker.getOrCreate(this)
        val uiState = mutableStateOf<UIState>(UIState.Unknown)

        setContent {
            FlexModeComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FlexMode(
                        uiState.value,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(innerPadding)
                    )
                }
            }
        }

        lifecycleScope.launch {
            windowInfoTracker.windowLayoutInfo(this@MainActivity)
                .collect { newLayoutInfo ->
                    val foldingFeature =
                        newLayoutInfo.displayFeatures.filterIsInstance<FoldingFeature>()
                            .firstOrNull()

                    foldingFeature?.let {
                        /**
                         * 만약 접힌 상태라면
                         */
                        if (it.state == FoldingFeature.State.HALF_OPENED) {
                            /**
                             * 힌지의 영역을 알아냄
                             */
                            val hingeBounds = it.bounds

                            /**
                             * 만약 힌지가 세로로 있는 거라면 => 폴드라면
                             */
                            val isVerticalHinge = hingeBounds.height() >= hingeBounds.width()
                            if (isVerticalHinge) {
                                uiState.value = UIState.FoldFolded
                            }
                            /**
                             * 만약 힌지가 가로로 있는 거라면 => 플립이라면
                             */
                            else {
                                // 플립을 현재 테스트 할 기기가 없는 관계로...
                                uiState.value = UIState.FlipFolded
                            }
                        }
                        /**
                         * 만약 아니라면 => 다시 폴드의 다 접었을 떄 쓰는 화면 or 펼쳐진 상태라면 원래대로 복구
                         */
                        else {
                            uiState.value = UIState.Unfolded
                        }
                    }
                }
        }
    }
}

@Composable
fun FlexMode(
    uiState: UIState,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = uiState,
        label = "FlexMode",
        modifier = Modifier.animateContentSize()
    ) { currentUiState ->
        when (currentUiState) {
            UIState.Unknown -> {
                Unfolded(
                    modifier = modifier
                )
            }

            UIState.Unfolded -> {
                Unfolded(
                    modifier = modifier
                )
            }

            UIState.FlipFolded -> {

            }

            UIState.FoldFolded -> {
                Folded(
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun Unfolded(modifier: Modifier = Modifier) {

    Row(modifier = modifier) {
        Text(
            text = "Hello",
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxHeight()
                .background(
                    color = Color.Blue
                )
                .wrapContentHeight()
                .weight(8F,),
            // 글자의 정렬 기준이 여러가지가 있는데 아무 설정 없이 사용하면 단순 Top, Bottom으로 적용이 된다고 함
            // 우리가 아는 가운데 정렬로 하려면 아래를 설정하여 폰트의 패딩을 없애야 함
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false,
                )
            )
        )
        Text(
            text = "World!",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxHeight()
                .background(
                    color = Color.Green
                )
                .wrapContentHeight()
                .weight(2F),
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false,
                )
            )
        )
    }
}

@Composable
fun Folded(modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(
            text = "Hello",
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxHeight()
                .background(
                    color = Color.Blue
                )
                .wrapContentHeight()
                .weight(5F),
            // 글자의 정렬 기준이 여러가지가 있는데 아무 설정 없이 사용하면 단순 Top, Bottom으로 적용이 된다고 함
            // 우리가 아는 가운데 정렬로 하려면 아래를 설정하여 폰트의 패딩을 없애야 함
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false,
                )
            )
        )
        Text(
            text = "World!",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxHeight()
                .background(
                    color = Color.Green
                )
                .wrapContentHeight()
                .weight(5F),
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false,
                )
            )
        )
    }
}

@Preview
@Composable
fun FlexModeText() {
    var isFolded by remember { mutableStateOf(false) }
    Column {

        Button(
            modifier = Modifier.background(
                if (isFolded) Color.Blue
                else Color.Magenta
            ),
            onClick = {
                isFolded = !isFolded
            }
        ) { Text("UI 테스트") }

        if (isFolded) {
            FlexMode(uiState = UIState.FoldFolded)
        } else {
            FlexMode(uiState = UIState.Unknown)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FlexModeComposeTheme {
        Unfolded()
    }
}

sealed interface UIState {
    data object Unfolded : UIState
    data object FoldFolded : UIState
    data object FlipFolded : UIState
    data object Unknown : UIState
}