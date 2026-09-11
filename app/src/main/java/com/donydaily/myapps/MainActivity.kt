package com.donydaily.myapps

import android.os.Bundle
import android.os.Build
import java.util.Locale
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.launch
import com.donydaily.myapps.BuildConfig

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val isDarkTheme = isSystemInDarkTheme()

            // 🔹 Cek apakah perangkat mendukung Dynamic Color (Android 12+)
            val colorScheme = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                }
                isDarkTheme -> darkColorScheme()
                else -> lightColorScheme()
            }

            MaterialTheme(colorScheme = colorScheme) {
                MainAppScreen(isDarkTheme = isDarkTheme)
            }
        }
    }
}

data class NavItem(val id: Int, val title: String, val icon: ImageVector)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainAppScreen(isDarkTheme: Boolean) {
    val listState = rememberLazyListState()
    val hazeState = remember { HazeState() }
    val coroutineScope = rememberCoroutineScope()

    val items = listOf(
        NavItem(0, stringResource(R.string.nav_home), Icons.Default.Home),
        NavItem(1, stringResource(R.string.nav_search), Icons.Default.Search),
        NavItem(2, stringResource(R.string.nav_favorite), Icons.Default.Favorite),
        NavItem(3, stringResource(R.string.nav_profile), Icons.Default.Person),
        NavItem(4, stringResource(R.string.nav_settings), Icons.Default.Settings)
    )

    val pagerState = rememberPagerState(initialPage = 0) { items.size }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .haze(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.background
                    )
                )
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> HomeScreen(listState)
                    1 -> SearchScreen()
                    2 -> FavoriteScreen()
                    3 -> ProfileScreen()
                    4 -> SettingsScreen()
                }
            }
        }

        FrostedTopHeader(
            title = items[pagerState.currentPage].title,
            hazeState = hazeState,
            isDarkTheme = isDarkTheme,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        FrostedFloatingNavBar(
            items = items,
            selectedIndex = pagerState.currentPage,
            hazeState = hazeState,
            isDarkTheme = isDarkTheme,
            onItemSelected = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
        )
    }
}

// ==================== 5 HALAMAN CONTENT ====================

@Composable
fun HomeScreen(listState: androidx.compose.foundation.lazy.LazyListState) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 130.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.welcome_text),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        items(10) { index ->
            ContentCard(
                title = stringResource(R.string.home_card_title, index + 1),
                description = stringResource(R.string.home_card_desc)
            )
        }
    }
}

@Composable
fun SearchScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 130.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text(stringResource(R.string.search_placeholder), color = MaterialTheme.colorScheme.onBackground.copy(0.6f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(0.4f),
                focusedTextColor = MaterialTheme.colorScheme.onBackground
            )
        )
        ContentCard(title = stringResource(R.string.search_popular), description = stringResource(R.string.search_popular_desc))
    }
}

@Composable
fun FavoriteScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 130.dp, bottom = 120.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.fav_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        items(6) { index ->
            ContentCard(
                title = stringResource(R.string.fav_item_title, index + 1),
                description = stringResource(R.string.fav_item_desc)
            )
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 130.dp, start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "Dony Daily",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "dony@example.com",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
        )

        Spacer(modifier = Modifier.height(10.dp))
        ContentCard(title = stringResource(R.string.profile_info_title), description = stringResource(R.string.profile_info_desc))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var currentLang by remember { mutableStateOf(LanguageHelper.getCurrentLanguage()) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // 🔹 1. Hapus "SYSTEM" dari daftar, cukup 2 opsi bahasa saja
    val languages = listOf(
        "id" to "Bahasa Indonesia 🇮🇩",
        "en" to "English 🇬🇧"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 130.dp, start = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.title_language),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { showBottomSheet = true },
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 🔹 2. Teks tombol langsung menampilkan bahasa aktif
                        val selectedText = if (currentLang == "en") "English 🇬🇧" else "Bahasa Indonesia 🇮🇩"

                        Text(
                            text = selectedText,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.UnfoldMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        ContentCard(
            title = stringResource(R.string.title_theme),
            description = stringResource(R.string.desc_theme)
        )
        ContentCard(
            title = stringResource(R.string.title_notifications),
            description = stringResource(R.string.desc_notifications)
        )
        ContentCard(
            title = stringResource(R.string.title_about),
            description = stringResource(R.string.app_version, BuildConfig.VERSION_NAME)
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 40.dp)
            ) {
                Text(
                    text = stringResource(R.string.title_language),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                languages.forEach { (code, label) ->
                    // 🔹 3. Pengecekan status terpilih yang lebih simpel
                    val isSelected = (currentLang == code)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                showBottomSheet = false
                                LanguageHelper.setLanguage(code)
                                currentLang = code
                            },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            Color.Transparent
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContentCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

// ==================== HEADER & NAVBAR DENGAN HAZESTYLE ====================

@Composable
fun FrostedTopHeader(
    title: String,
    hazeState: HazeState,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val tintColor = if (isDarkTheme) Color.Black.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .hazeChild(
                state = hazeState,
                style = HazeStyle(
                    blurRadius = 25.dp,
                    tint = tintColor
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(64.dp)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifikasi",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun FrostedFloatingNavBar(
    items: List<NavItem>,
    selectedIndex: Int,
    hazeState: HazeState,
    isDarkTheme: Boolean,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val navShape = RoundedCornerShape(36.dp)

    val tintColor = if (isDarkTheme) Color.Black.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.5f)
    val borderColor = if (isDarkTheme) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(navShape)
            .hazeChild(
                state = hazeState,
                shape = navShape,
                style = HazeStyle(
                    blurRadius = 30.dp,
                    tint = tintColor
                )
            )
            .border(
                width = 1.5.dp,
                color = borderColor,
                shape = navShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedIndex == index

                val animatedWidth by animateDpAsState(
                    targetValue = if (isSelected) 52.dp else 40.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "nav_width"
                )

                val selectedIconContainerColor = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.primary
                val selectedIconTint = if (isDarkTheme) Color.Black else Color.White
                val unselectedIconTint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)

                Box(
                    modifier = Modifier
                        .height(44.dp)
                        .width(animatedWidth)
                        .clip(CircleShape)
                        .background(if (isSelected) selectedIconContainerColor else Color.Transparent)
                        .clickable { onItemSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) selectedIconTint else unselectedIconTint
                    )
                }
            }
        }
    }
}
