package com.chaser.drycleaningsystem.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 页面切换动画 - 淡入淡出
 */
@Composable
fun FadeThroughTransition(
    targetState: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = targetState,
        enter = fadeIn(
            animationSpec = tween(300)
        ) + scaleIn(
            animationSpec = tween(300),
            initialScale = 0.95f
        ),
        exit = fadeOut(
            animationSpec = tween(300)
        ) + scaleOut(
            animationSpec = tween(300),
            targetScale = 0.95f
        ),
        content = content
    )
}

/**
 * 页面切换动画 - 滑入淡出
 */
@Composable
fun SlideInTransition(
    targetState: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = targetState,
        enter = slideInHorizontally(
            animationSpec = tween(300),
            initialOffsetX = { it }
        ) + fadeIn(
            animationSpec = tween(300)
        ),
        exit = slideOutHorizontally(
            animationSpec = tween(300),
            targetOffsetX = { -it }
        ) + fadeOut(
            animationSpec = tween(300)
        ),
        content = content
    )
}

/**
 * 页面切换动画 - 向上滑动
 */
@Composable
fun SlideUpTransition(
    targetState: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = targetState,
        enter = slideInVertically(
            animationSpec = tween(300),
            initialOffsetY = { it }
        ) + fadeIn(
            animationSpec = tween(300)
        ),
        exit = slideOutVertically(
            animationSpec = tween(300),
            targetOffsetY = { it }
        ) + fadeOut(
            animationSpec = tween(300)
        ),
        content = content
    )
}

/**
 * 内容展开/折叠动画
 */
@Composable
fun ExpandCollapseAnimation(
    expanded: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(
            animationSpec = tween(200)
        ),
        exit = shrinkVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeOut(
            animationSpec = tween(200)
        ),
        content = content
    )
}

/**
 * 列表项进入动画
 */
@Composable
fun ListItemEnterAnimation(
    visible: Boolean,
    delay: Int = 0,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            animationSpec = tween(300, delayMillis = delay),
            initialOffsetX = { -it / 4 }
        ) + fadeIn(
            animationSpec = tween(300, delayMillis = delay)
        ),
        exit = fadeOut(
            animationSpec = tween(100)
        ),
        content = content
    )
}

/**
 * 缩放动画
 */
@Composable
fun ScaleAnimation(
    targetState: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = targetState,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(
            animationSpec = tween(200)
        ),
        exit = scaleOut(
            animationSpec = tween(200)
        ) + fadeOut(
            animationSpec = tween(200)
        ),
        content = content
    )
}

/**
 * 导航切换动画 - 用于 Navigation Compose
 */
fun navEnterTransition(): EnterTransition =
    slideInHorizontally(
        animationSpec = tween(300),
        initialOffsetX = { it }
    ) + fadeIn(
        animationSpec = tween(300)
    )

/**
 * 导航退出动画 - 用于 Navigation Compose
 */
fun navExitTransition(): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(300),
        targetOffsetX = { -it }
    ) + fadeOut(
        animationSpec = tween(300)
    )

/**
 * 导航返回动画 - 用于 Navigation Compose
 */
fun navPopEnterTransition(): EnterTransition =
    slideInHorizontally(
        animationSpec = tween(300),
        initialOffsetX = { -it }
    ) + fadeIn(
        animationSpec = tween(300)
    )

/**
 * 导航返回退出动画 - 用于 Navigation Compose
 */
fun navPopExitTransition(): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(300),
        targetOffsetX = { it }
    ) + fadeOut(
        animationSpec = tween(300)
    )
