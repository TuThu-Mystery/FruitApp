package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.CartItemUiModel
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.ShopViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

// Helper for currency formatting
fun formatVnd(amount: Double): String {
    val formatter = DecimalFormat("#,###")
    return formatter.format(amount) + "đ"
}

// Helper for date formatting
fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopApp(viewModel: ShopViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Launch feedback message when it arrives
    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                is Screen.Login -> LoginScreen(viewModel)
                is Screen.Register -> RegisterScreen(viewModel)
                is Screen.UserHome -> UserHomeScreen(viewModel)
                is Screen.AdminHome -> AdminHomeScreen(viewModel)
            }
        }
    }
}

// ==================== 1. LOGIN SCREEN ====================
@Composable
fun LoginScreen(viewModel: ShopViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(OrganicBackground, SoftCream)
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Identity Header matching FreshFruit aesthetic
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(SageGreen)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = "Fruit Shop Logo",
                tint = ForestGreen,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "FreshFruit",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreen,
            textAlign = TextAlign.Center,
            letterSpacing = (-0.5).sp
        )

        Text(
            text = "Trái Cây Sạch & Tươi Ngon Mỗi Ngày",
            fontSize = 14.sp,
            color = PolishTextSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Input Fields
        OutlinedTextField(
            value = username,
            onValueChange = { username = it; loginError = null },
            label = { Text("Tên tài khoản") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User Icon", tint = PolishTextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForestGreen,
                unfocusedBorderColor = Color(0xFFE0E5DB),
                focusedLabelColor = ForestGreen,
                unfocusedLabelColor = PolishTextSecondary,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; loginError = null },
            label = { Text("Mật khẩu") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock Icon", tint = PolishTextSecondary) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForestGreen,
                unfocusedBorderColor = Color(0xFFE0E5DB),
                focusedLabelColor = ForestGreen,
                unfocusedLabelColor = PolishTextSecondary,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        if (loginError != null) {
            Text(
                text = loginError ?: "",
                color = PolishAlertRed,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Button(
            onClick = {
                focusManager.clearFocus()
                if (username.isBlank() || password.isBlank()) {
                    loginError = "Vui lòng nhập đầy đủ thông tin!"
                } else {
                    viewModel.login(username, password) { success ->
                        if (!success) {
                            loginError = "Sai tên đăng nhập hoặc mật khẩu!"
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(24.dp) // Sleeker pill shapes
        ) {
            Text("Đăng Nhập", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Chưa có tài khoản? Đăng ký ngay",
            color = ForestGreen,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { viewModel.navigateTo(Screen.Register) }
                .padding(8.dp)
        )
    }
}

// ==================== 2. REGISTER SCREEN ====================
@Composable
fun RegisterScreen(viewModel: ShopViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var regError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(OrganicBackground, SoftCream)
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ĐĂNG KÝ TÀI KHOẢN",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreen,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it; regError = null },
            label = { Text("Tên đăng nhập (username)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForestGreen,
                unfocusedBorderColor = Color(0xFFE0E5DB),
                focusedLabelColor = ForestGreen,
                unfocusedLabelColor = PolishTextSecondary,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; regError = null },
            label = { Text("Mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForestGreen,
                unfocusedBorderColor = Color(0xFFE0E5DB),
                focusedLabelColor = ForestGreen,
                unfocusedLabelColor = PolishTextSecondary,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it; regError = null },
            label = { Text("Họ và tên") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForestGreen,
                unfocusedBorderColor = Color(0xFFE0E5DB),
                focusedLabelColor = ForestGreen,
                unfocusedLabelColor = PolishTextSecondary,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it; regError = null },
            label = { Text("Số điện thoại") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForestGreen,
                unfocusedBorderColor = Color(0xFFE0E5DB),
                focusedLabelColor = ForestGreen,
                unfocusedLabelColor = PolishTextSecondary,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        if (regError != null) {
            Text(
                text = regError ?: "",
                color = PolishAlertRed,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                if (username.isBlank() || password.isBlank() || fullName.isBlank() || phoneNumber.isBlank()) {
                    regError = "Vui lòng nhập đầy đủ các trường thông tin!"
                } else {
                    val newUser = UserEntity(
                        username = username,
                        password = password,
                        role = "USER",
                        fullName = fullName,
                        phoneNumber = phoneNumber
                    )
                    viewModel.register(newUser) { success, msg ->
                        if (success) {
                            viewModel.showFeedback(msg)
                            viewModel.navigateTo(Screen.Login)
                        } else {
                            regError = msg
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Đăng Ký Ngay", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Đã có tài khoản? Đăng nhập",
            color = SunsetOrange,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { viewModel.navigateTo(Screen.Login) }
                .padding(8.dp)
        )
    }
}

// ==================== 3. USER HOME SCREEN ====================
@Composable
fun UserHomeScreen(viewModel: ShopViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Shop, 1: Cart, 2: Orders

    Scaffold(
        bottomBar = {
            // Highly rounded top-corner bottom navigation bar matching Professional Polish
            NavigationBar(
                containerColor = SoftCream,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.Transparent)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Storefront, contentDescription = "Cửa Hàng") },
                    label = { Text("Mua Sắm") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestGreen,
                        selectedTextColor = ForestGreen,
                        unselectedIconColor = PolishTextSecondary,
                        unselectedTextColor = PolishTextSecondary,
                        indicatorColor = SageGreen.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ Hàng") },
                    label = { Text("Giỏ Hàng") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestGreen,
                        selectedTextColor = ForestGreen,
                        unselectedIconColor = PolishTextSecondary,
                        unselectedTextColor = PolishTextSecondary,
                        indicatorColor = SageGreen.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.AutoMirrored.Default.List, contentDescription = "Lịch sử mua hàng") },
                    label = { Text("Lịch Sử") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestGreen,
                        selectedTextColor = ForestGreen,
                        unselectedIconColor = PolishTextSecondary,
                        unselectedTextColor = PolishTextSecondary,
                        indicatorColor = SageGreen.copy(alpha = 0.5f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OrganicBackground)
                .padding(innerPadding)
        ) {
            // User Header Block (Fresh and bright with light background & avatar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(OrganicBackground)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "FreshFruit",
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Hi, ${currentUser?.fullName ?: "Guest"}",
                        color = PolishTextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile Avatar Circle as styled in design HTML
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SageGreen)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = ForestGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.logout() },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = ForestGreen)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Đăng xuất")
                    }
                }
            }

            // Main Tab Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> UserShopTab(viewModel)
                    1 -> UserCartTab(viewModel)
                    2 -> UserOrdersTab(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomCategoryChip(
    selected: Boolean,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        color = if (selected) ForestGreen else SageGreen,
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else ForestGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun UserShopTab(viewModel: ShopViewModel) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val fruits by viewModel.fruits.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OrganicBackground)
    ) {
        // Polished Search bar matching HTML mock bg [#EDF3E8]
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon", tint = PolishTextSecondary) },
            placeholder = { Text("Search fresh fruits...", color = Color(0xFF717971), fontSize = 14.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SoftCream, // PolishContainerBg (0xFFEDF3E8)
                unfocusedContainerColor = SoftCream,
                disabledContainerColor = SoftCream,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            singleLine = true
        )

        // Categories filter row with custom chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, top = 4.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CustomCategoryChip(
                    selected = selectedCategory == null,
                    text = "All",
                    onClick = { viewModel.selectCategory(null) }
                )
            }
            items(categories) { category ->
                CustomCategoryChip(
                    selected = selectedCategory == category.id,
                    text = category.name,
                    onClick = { viewModel.selectCategory(category.id) }
                )
            }
        }

        // Fruits List
        if (fruits.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "No Fruit",
                        tint = PolishTextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Không tìm thấy trái cây nào phù hợp!",
                        color = PolishTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(fruits) { fruit ->
                    FruitGridItem(fruit = fruit, onAddToCart = { viewModel.addToCart(fruit) })
                }
            }
        }
    }
}

@Composable
fun FruitGridItem(fruit: FruitEntity, onAddToCart: () -> Unit) {
    // Dynamic background assignment depending on the fruit's identity
    val pastelBg = when {
        fruit.name.contains("Táo", true) || fruit.name.contains("Cherry", true) || fruit.name.contains("Dâu", true) -> Color(0xFFFBE9E7) // Soft coral
        fruit.name.contains("Nho", true) || fruit.name.contains("Blue", true) -> Color(0xFFE8F3FD) // Soft blue
        fruit.name.contains("Xoài", true) || fruit.name.contains("Mít", true) -> Color(0xFFFDF2D9) // Soft gold
        else -> Color(0xFFF1F5EB) // Soft green/sage
    }

    // Dynamic tag assignment (e.g. Tropical, Local, Berries, Seasonal) as shown in HTML mock
    val categoryLabel = when {
        fruit.name.contains("Táo", true) || fruit.name.contains("Dâu", true) -> "Local"
        fruit.name.contains("Cherry", true) || fruit.name.contains("Nho", true) || fruit.name.contains("Blue", true) -> "Berries"
        fruit.name.contains("Xoài", true) || fruit.name.contains("Sầu Riêng", true) -> "Seasonal"
        else -> "Tropical"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        shape = RoundedCornerShape(28.dp), // Very elegant rounded corner card
        colors = CardDefaults.cardColors(containerColor = PolishSurface),
        border = BorderStroke(1.dp, Color(0xFFE0E5DB)), // Soft light grey-green border
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Premium aspect-ratio visual cover using dynamic colorful background and larger emoji
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(pastelBg),
                contentAlignment = Alignment.Center
            ) {
                val emoji = when {
                    fruit.name.contains("Táo", true) -> "🍎"
                    fruit.name.contains("Nho", true) -> "🍇"
                    fruit.name.contains("Cherry", true) -> "🍒"
                    fruit.name.contains("Sầu Riêng", true) -> "🍈"
                    fruit.name.contains("Măng Cụt", true) -> "🥭"
                    fruit.name.contains("Xoài", true) -> "🥭"
                    fruit.name.contains("Hạnh Nhân", true) -> "🥜"
                    fruit.name.contains("Mít", true) -> "🍍"
                    else -> "🍓"
                }
                Text(text = emoji, fontSize = 52.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle label tag (e.g. Berries, Tropical, Seasonal)
            Text(
                text = categoryLabel.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PolishTextSecondary,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            // Title
            Text(
                text = fruit.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = PolishOnSurface,
                modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatVnd(fruit.price),
                        color = ForestGreen, // Deep elegant green price tag
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Kho: ${fruit.quantity}",
                        fontSize = 11.sp,
                        color = if (fruit.quantity > 5) PolishTextSecondary else PolishAlertRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Sleek custom add button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ForestGreen)
                        .clickable { onAddToCart() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm vào giỏ",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UserCartTab(viewModel: ShopViewModel) {
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val totalPrice by viewModel.cartTotalPrice.collectAsStateWithLifecycle()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Tuyệt Vời", color = Color.White)
                }
            },
            title = { Text("Thanh Toán Thành Công 🎉", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = { Text("Đơn hàng của bạn đã được tiếp nhận. Số lượng hàng tồn kho đã được cập nhật thành công!", color = Color.Black) },
            shape = RoundedCornerShape(28.dp),
            containerColor = PolishSurface,
            titleContentColor = Color.Black,
            textContentColor = Color.Black
        )
    }

    if (showErrorDialog != null) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = null },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishAlertRed),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Đã Hiểu", color = Color.White)
                }
            },
            title = { Text("Hủy Đơn Hàng - Hết Hàng ⚠️", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = { Text(showErrorDialog ?: "", color = Color.Black) },
            shape = RoundedCornerShape(28.dp),
            containerColor = PolishSurface,
            titleContentColor = Color.Black,
            textContentColor = Color.Black
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OrganicBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Giỏ Hàng Của Bạn",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreen,
            letterSpacing = (-0.5).sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.RemoveShoppingCart,
                        contentDescription = "Empty Cart",
                        tint = PolishTextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Giỏ hàng của bạn đang trống!",
                        color = PolishTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { item ->
                    CartItemRow(item = item, viewModel = viewModel)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Total price section
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftCream),
                border = BorderStroke(1.dp, Color(0xFFE0E5DB)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tổng Cộng:", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PolishOnSurface)
                    Text(
                        text = formatVnd(totalPrice),
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen, // Harmonious dark green price tag
                        fontSize = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Purchase Checkout button
            Button(
                onClick = {
                    viewModel.checkout(
                        onSuccess = {
                            showSuccessDialog = true
                        },
                        onError = { errorMsg ->
                            showErrorDialog = errorMsg
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Check Icon", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Xác Nhận Thanh Toán", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItemUiModel, viewModel: ShopViewModel) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var quantityText by remember(item.cartItem.quantity) { mutableStateOf(item.cartItem.quantity.toString()) }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeFromCart(item.cartItem)
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PolishAlertRed),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Xóa", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteConfirmDialog = false
                    quantityText = item.cartItem.quantity.toString()
                }) {
                    Text("Hủy", color = PolishTextSecondary)
                }
            },
            title = { Text("Xác nhận xóa", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = { Text("Bạn muốn xóa sản phẩm khỏi giỏ hàng ko?", color = Color.Black) },
            shape = RoundedCornerShape(28.dp),
            containerColor = PolishSurface,
            titleContentColor = Color.Black,
            textContentColor = Color.Black
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PolishSurface),
        border = BorderStroke(1.dp, Color(0xFFE0E5DB)),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.fruitName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = PolishOnSurface
                )
                Text(
                    text = "${formatVnd(item.fruitPrice)} / sản phẩm",
                    color = ForestGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = "Tối đa trong kho: ${item.maxQuantity}",
                    fontSize = 11.sp,
                    color = PolishTextSecondary,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Polished rounded quantity decrease button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SoftCream)
                        .clickable {
                            if (item.cartItem.quantity - 1 <= 0) {
                                showDeleteConfirmDialog = true
                            } else {
                                viewModel.updateCartQuantity(item.cartItem, -1)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Giảm",
                        tint = ForestGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }

                androidx.compose.foundation.text.BasicTextField(
                    value = quantityText,
                    onValueChange = { newValue ->
                        quantityText = newValue
                        val newQty = newValue.toIntOrNull()
                        if (newQty != null) {
                            if (newQty <= 0) {
                                showDeleteConfirmDialog = true
                            } else if (newQty > item.maxQuantity) {
                                viewModel.setCartQuantity(item.cartItem, newQty) // This will trigger the feedback
                                quantityText = item.maxQuantity.toString() // Reset to max visually
                            } else {
                                viewModel.setCartQuantity(item.cartItem, newQty)
                            }
                        }
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    ),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    modifier = Modifier.width(36.dp)
                )

                // Polished rounded quantity increase button
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SoftCream)
                        .clickable { viewModel.updateCartQuantity(item.cartItem, 1) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tăng",
                        tint = ForestGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = { showDeleteConfirmDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = PolishAlertRed),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun UserOrdersTab(viewModel: ShopViewModel) {
    val orders by viewModel.userOrders.collectAsStateWithLifecycle()
    val details by viewModel.currentOrderDetails.collectAsStateWithLifecycle()
    val fruits by viewModel.fruits.collectAsStateWithLifecycle()
    var selectedOrder by remember { mutableStateOf<OrderEntity?>(null) }

    if (selectedOrder != null) {
        Dialog(onDismissRequest = { selectedOrder = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E5DB)),
                colors = CardDefaults.cardColors(containerColor = PolishSurface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Chi Tiết Đơn Hàng",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ForestGreen,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text("Mã Đơn: #${selectedOrder?.id}", fontSize = 13.sp, color = PolishTextSecondary)
                    Text("Ngày: ${selectedOrder?.let { formatDate(it.orderDate) }}", fontSize = 13.sp, color = PolishTextSecondary)

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE0E5DB))

                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(details) { detail ->
                            val matchedFruit = fruits.find { it.id == detail.fruitId }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    matchedFruit?.name ?: "Sản phẩm #${detail.fruitId}",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = PolishOnSurface
                                )
                                Text(
                                    "${detail.purchasedQuantity} x ${formatVnd(detail.unitPrice)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ForestGreen
                                )
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE0E5DB))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tổng cộng:", fontWeight = FontWeight.Bold, color = PolishOnSurface)
                        Text(
                            text = formatVnd(selectedOrder?.totalPrice ?: 0.0),
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { selectedOrder = null },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Đóng", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OrganicBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Lịch Sử Mua Hàng",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreen,
            letterSpacing = (-0.5).sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.List,
                        contentDescription = "No orders",
                        tint = PolishTextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Bạn chưa thực hiện đơn hàng nào!", color = PolishTextSecondary, fontWeight = FontWeight.Medium)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(orders) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedOrder = order
                                viewModel.loadOrderDetails(order.id)
                            },
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color(0xFFE0E5DB)),
                        colors = CardDefaults.cardColors(containerColor = PolishSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Đơn hàng #${order.id}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PolishOnSurface)
                                Text(formatDate(order.orderDate), fontSize = 12.sp, color = PolishTextSecondary)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    formatVnd(order.totalPrice),
                                    fontWeight = FontWeight.Bold,
                                    color = ForestGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SageGreen)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = order.status,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== 4. ADMIN HOME SCREEN ====================
@Composable
fun AdminHomeScreen(viewModel: ShopViewModel) {
    var selectedAdminSection by remember { mutableIntStateOf(0) } // 0: User, 1: Category, 2: Fruit, 3: Orders

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SoftCream,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.Transparent)
            ) {
                NavigationBarItem(
                    selected = selectedAdminSection == 0,
                    onClick = { selectedAdminSection = 0 },
                    icon = { Icon(Icons.Default.People, contentDescription = "Users") },
                    label = { Text("Users") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestGreen,
                        selectedTextColor = ForestGreen,
                        unselectedIconColor = PolishTextSecondary,
                        unselectedTextColor = PolishTextSecondary,
                        indicatorColor = SageGreen.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = selectedAdminSection == 1,
                    onClick = { selectedAdminSection = 1 },
                    icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
                    label = { Text("Categories") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestGreen,
                        selectedTextColor = ForestGreen,
                        unselectedIconColor = PolishTextSecondary,
                        unselectedTextColor = PolishTextSecondary,
                        indicatorColor = SageGreen.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = selectedAdminSection == 2,
                    onClick = { selectedAdminSection = 2 },
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Fruits") },
                    label = { Text("Fruits") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestGreen,
                        selectedTextColor = ForestGreen,
                        unselectedIconColor = PolishTextSecondary,
                        unselectedTextColor = PolishTextSecondary,
                        indicatorColor = SageGreen.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = selectedAdminSection == 3,
                    onClick = { selectedAdminSection = 3 },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Orders") },
                    label = { Text("Orders") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ForestGreen,
                        selectedTextColor = ForestGreen,
                        unselectedIconColor = PolishTextSecondary,
                        unselectedTextColor = PolishTextSecondary,
                        indicatorColor = SageGreen.copy(alpha = 0.5f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OrganicBackground)
                .padding(innerPadding)
        ) {
            // Admin Banner Header matching the brand identity of the design HTML
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(OrganicBackground)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "FreshFruit Admin",
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Chúc một ngày làm việc năng suất! 🛠️",
                        color = PolishTextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SageGreen)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = ForestGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { viewModel.logout() },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = ForestGreen)
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            }

            // Tabs Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (selectedAdminSection) {
                    0 -> AdminUserSection(viewModel)
                    1 -> AdminCategorySection(viewModel)
                    2 -> AdminFruitSection(viewModel)
                    3 -> AdminOrdersSection(viewModel)
                }
            }
        }
    }
}

// --- ADMIN USER SECTION ---
@Composable
fun AdminUserSection(viewModel: ShopViewModel) {
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    var showAddUserDialog by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<UserEntity?>(null) }

    if (showAddUserDialog || editingUser != null) {
        var username by remember { mutableStateOf(editingUser?.username ?: "") }
        var password by remember { mutableStateOf(editingUser?.password ?: "") }
        var fullName by remember { mutableStateOf(editingUser?.fullName ?: "") }
        var phoneNumber by remember { mutableStateOf(editingUser?.phoneNumber ?: "") }
        var role by remember { mutableStateOf(editingUser?.role ?: "USER") }
        var dialogError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddUserDialog = false; editingUser = null },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(24.dp),
                    onClick = {
                        if (username.isBlank() || password.isBlank() || fullName.isBlank() || phoneNumber.isBlank()) {
                            dialogError = "Vui lòng điền đủ các trường!"
                        } else {
                            if (editingUser == null) {
                                viewModel.addAdminUser(
                                    UserEntity(
                                        username = username,
                                        password = password,
                                        role = role,
                                        fullName = fullName,
                                        phoneNumber = phoneNumber
                                    )
                                ) { success, msg ->
                                    if (success) {
                                        viewModel.showFeedback(msg)
                                        showAddUserDialog = false
                                    } else {
                                        dialogError = msg
                                    }
                                }
                            } else {
                                viewModel.updateAdminUser(
                                    editingUser!!.copy(
                                        username = username,
                                        password = password,
                                        role = role,
                                        fullName = fullName,
                                        phoneNumber = phoneNumber
                                    )
                                )
                                editingUser = null
                            }
                        }
                    }
                ) {
                    Text("Lưu", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddUserDialog = false; editingUser = null },
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E5DB))
                ) {
                    Text("Hủy", color = PolishTextSecondary)
                }
            },
            title = { Text(if (editingUser == null) "Thêm User Mới" else "Chỉnh Sửa User", fontWeight = FontWeight.Bold, color = Color.Black) },
            shape = RoundedCornerShape(28.dp),
            containerColor = PolishSurface,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Tên đăng nhập") },
                        enabled = editingUser == null,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = Color(0xFFE0E5DB),
                            focusedLabelColor = ForestGreen,
                            unfocusedLabelColor = PolishTextSecondary,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = Color(0xFFE0E5DB),
                            focusedLabelColor = ForestGreen,
                            unfocusedLabelColor = PolishTextSecondary,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Họ và tên") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = Color(0xFFE0E5DB),
                            focusedLabelColor = ForestGreen,
                            unfocusedLabelColor = PolishTextSecondary,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Số điện thoại") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = Color(0xFFE0E5DB),
                            focusedLabelColor = ForestGreen,
                            unfocusedLabelColor = PolishTextSecondary,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Phân quyền:", fontWeight = FontWeight.Bold, color = PolishOnSurface)
                        RadioButton(
                            selected = role == "USER", 
                            onClick = { role = "USER" },
                            colors = RadioButtonDefaults.colors(selectedColor = ForestGreen)
                        )
                        Text("USER", color = PolishOnSurface)
                        RadioButton(
                            selected = role == "ADMIN", 
                            onClick = { role = "ADMIN" },
                            colors = RadioButtonDefaults.colors(selectedColor = ForestGreen)
                        )
                        Text("ADMIN", color = PolishOnSurface)
                    }

                    if (dialogError != null) {
                        Text(dialogError ?: "", color = PolishAlertRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.background(OrganicBackground),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddUserDialog = true },
                containerColor = ForestGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add User")
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OrganicBackground)
                .padding(inner)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Quản Lý Thành Viên",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(users) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color(0xFFE0E5DB)),
                        colors = CardDefaults.cardColors(containerColor = PolishSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PolishOnSurface)
                                Text("@${user.username} | ${user.role}", fontSize = 13.sp, color = PolishTextSecondary)
                                Text("SĐT: ${user.phoneNumber}", fontSize = 12.sp, color = PolishTextSecondary)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = { editingUser = user }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit User", tint = ForestGreen)
                                }
                                IconButton(onClick = { viewModel.deleteAdminUser(user) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete User", tint = PolishAlertRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- ADMIN CATEGORY SECTION ---
@Composable
fun AdminCategorySection(viewModel: ShopViewModel) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryEntity?>(null) }

    if (showAddCategoryDialog || editingCategory != null) {
        var name by remember { mutableStateOf(editingCategory?.name ?: "") }
        var imageUrl by remember { mutableStateOf(editingCategory?.imageUrl ?: "") }
        var dialogError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false; editingCategory = null },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(24.dp),
                    onClick = {
                        if (name.isBlank()) {
                            dialogError = "Tên danh mục không được trống!"
                        } else {
                            val adminId = currentUser?.id ?: 1
                            if (editingCategory == null) {
                                viewModel.addAdminCategory(
                                    CategoryEntity(
                                        name = name,
                                        imageUrl = imageUrl.ifBlank { "default" },
                                        adminId = adminId
                                    )
                                )
                                showAddCategoryDialog = false
                            } else {
                                viewModel.updateAdminCategory(
                                    editingCategory!!.copy(
                                        name = name,
                                        imageUrl = imageUrl.ifBlank { "default" }
                                    )
                                )
                                editingCategory = null
                            }
                        }
                    }
                ) {
                    Text("Lưu", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddCategoryDialog = false; editingCategory = null },
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E5DB))
                ) {
                    Text("Hủy", color = PolishTextSecondary)
                }
            },
            title = { Text(if (editingCategory == null) "Thêm Danh Mục Mới" else "Sửa Danh Mục", fontWeight = FontWeight.Bold, color = Color.Black) },
            shape = RoundedCornerShape(28.dp),
            containerColor = PolishSurface,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tên danh mục") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = Color(0xFFE0E5DB),
                            focusedLabelColor = ForestGreen,
                            unfocusedLabelColor = PolishTextSecondary,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("Ảnh danh mục (Tên nhãn hoặc link)") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = Color(0xFFE0E5DB),
                            focusedLabelColor = ForestGreen,
                            unfocusedLabelColor = PolishTextSecondary,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    if (dialogError != null) {
                        Text(dialogError ?: "", color = PolishAlertRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.background(OrganicBackground),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCategoryDialog = true },
                containerColor = ForestGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OrganicBackground)
                .padding(inner)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Quản Lý Danh Mục",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categories) { category ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color(0xFFE0E5DB)),
                        colors = CardDefaults.cardColors(containerColor = PolishSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SageGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Folder, contentDescription = "Category Icon", tint = ForestGreen)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(category.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PolishOnSurface)
                            }

                            Row {
                                IconButton(onClick = { editingCategory = category }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Category", tint = ForestGreen)
                                }
                                IconButton(onClick = { viewModel.deleteAdminCategory(category) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Category", tint = PolishAlertRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- ADMIN FRUIT SECTION ---
@Composable
fun AdminFruitSection(viewModel: ShopViewModel) {
    val fruits by viewModel.fruits.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var showAddFruitDialog by remember { mutableStateOf(false) }
    var editingFruit by remember { mutableStateOf<FruitEntity?>(null) }

    if (showAddFruitDialog || editingFruit != null) {
        var name by remember { mutableStateOf(editingFruit?.name ?: "") }
        var priceInput by remember { mutableStateOf(editingFruit?.price?.toString() ?: "") }
        var quantityInput by remember { mutableStateOf(editingFruit?.quantity?.toString() ?: "") }
        var categoryId by remember { mutableStateOf(editingFruit?.categoryId ?: categories.firstOrNull()?.id ?: 1) }
        var imageUrl by remember { mutableStateOf(editingFruit?.imageUrl ?: "") }
        var dialogError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddFruitDialog = false; editingFruit = null },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    shape = RoundedCornerShape(24.dp),
                    onClick = {
                        val price = priceInput.toDoubleOrNull()
                        val quantity = quantityInput.toIntOrNull()
                        if (name.isBlank() || price == null || quantity == null) {
                            dialogError = "Vui lòng nhập thông tin hợp lệ!"
                        } else {
                            if (editingFruit == null) {
                                viewModel.addAdminFruit(
                                    FruitEntity(
                                        name = name,
                                        price = price,
                                        quantity = quantity,
                                        imageUrl = imageUrl.ifBlank { "default_fruit" },
                                        categoryId = categoryId
                                    )
                                )
                                showAddFruitDialog = false
                            } else {
                                viewModel.updateAdminFruit(
                                    editingFruit!!.copy(
                                        name = name,
                                        price = price,
                                        quantity = quantity,
                                        imageUrl = imageUrl.ifBlank { "default_fruit" },
                                        categoryId = categoryId
                                    )
                                )
                                editingFruit = null
                            }
                        }
                    }
                ) {
                    Text("Lưu", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddFruitDialog = false; editingFruit = null },
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E5DB))
                ) {
                    Text("Hủy", color = PolishTextSecondary)
                }
            },
            title = { Text(if (editingFruit == null) "Thêm Trái Cây Mới" else "Sửa Trái Cây", fontWeight = FontWeight.Bold, color = Color.Black) },
            shape = RoundedCornerShape(28.dp),
            containerColor = PolishSurface,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tên trái cây") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = Color(0xFFE0E5DB),
                            focusedLabelColor = ForestGreen,
                            unfocusedLabelColor = PolishTextSecondary,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it },
                        label = { Text("Đơn giá (Double)") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = Color(0xFFE0E5DB),
                            focusedLabelColor = ForestGreen,
                            unfocusedLabelColor = PolishTextSecondary,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    OutlinedTextField(
                        value = quantityInput,
                        onValueChange = { quantityInput = it },
                        label = { Text("Số lượng tồn kho (Int)") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = Color(0xFFE0E5DB),
                            focusedLabelColor = ForestGreen,
                            unfocusedLabelColor = PolishTextSecondary,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("Ảnh trái cây (nhãn)") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = Color(0xFFE0E5DB),
                            focusedLabelColor = ForestGreen,
                            unfocusedLabelColor = PolishTextSecondary,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    // Simple category selector dropdown simulated with row buttons using custom category chip
                    Text("Danh mục:", fontWeight = FontWeight.Bold, color = PolishOnSurface, modifier = Modifier.padding(top = 4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { category ->
                            CustomCategoryChip(
                                selected = categoryId == category.id,
                                onClick = { categoryId = category.id },
                                text = category.name
                            )
                        }
                    }

                    if (dialogError != null) {
                        Text(dialogError ?: "", color = PolishAlertRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.background(OrganicBackground),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddFruitDialog = true },
                containerColor = ForestGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Fruit")
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(OrganicBackground)
                .padding(inner)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Quản Lý Trái Cây",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(fruits) { fruit ->
                    val matchedCat = categories.find { it.id == fruit.categoryId }?.name ?: "Chưa phân loại"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color(0xFFE0E5DB)),
                        colors = CardDefaults.cardColors(containerColor = PolishSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(fruit.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PolishOnSurface)
                                Text("Phân loại: $matchedCat", fontSize = 12.sp, color = PolishTextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Giá: ${formatVnd(fruit.price)}", fontSize = 13.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                                    Text("Tồn kho: ${fruit.quantity}", fontSize = 13.sp, color = if (fruit.quantity > 5) PolishTextSecondary else PolishAlertRed, fontWeight = FontWeight.Bold)
                                }
                            }

                            Row {
                                IconButton(onClick = { editingFruit = fruit }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Fruit", tint = ForestGreen)
                                }
                                IconButton(onClick = { viewModel.deleteAdminFruit(fruit) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Fruit", tint = PolishAlertRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- ADMIN ORDERS SECTION ---
@Composable
fun AdminOrdersSection(viewModel: ShopViewModel) {
    val orders by viewModel.allOrders.collectAsStateWithLifecycle()
    val details by viewModel.currentOrderDetails.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    var selectedAdminOrder by remember { mutableStateOf<OrderEntity?>(null) }

    if (selectedAdminOrder != null) {
        Dialog(onDismissRequest = { selectedAdminOrder = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E5DB)),
                colors = CardDefaults.cardColors(containerColor = PolishSurface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Chi Tiết Đơn Hàng Admin",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = ForestGreen,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val customer = users.find { it.id == selectedAdminOrder?.userId }
                    Text("Khách hàng: ${customer?.fullName ?: "Không xác định"}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PolishOnSurface)
                    Text("SĐT: ${customer?.phoneNumber ?: ""}", fontSize = 13.sp, color = PolishTextSecondary)
                    Text("Mã Đơn: #${selectedAdminOrder?.id}", fontSize = 13.sp, color = PolishTextSecondary)
                    Text("Ngày Đặt: ${selectedAdminOrder?.let { formatDate(it.orderDate) }}", fontSize = 13.sp, color = PolishTextSecondary)

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE0E5DB))

                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(details) { detail ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Sản phẩm ID: #${detail.fruitId}",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = PolishOnSurface
                                )
                                Text(
                                    "${detail.purchasedQuantity} x ${formatVnd(detail.unitPrice)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ForestGreen
                                )
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE0E5DB))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tổng thu nhập:", fontWeight = FontWeight.Bold, color = PolishOnSurface)
                        Text(
                            text = formatVnd(selectedAdminOrder?.totalPrice ?: 0.0),
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { selectedAdminOrder = null },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Đóng", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OrganicBackground)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Tất Cả Đơn Hàng Hệ Thống",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreen,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Hệ thống chưa ghi nhận đơn hàng nào!", color = PolishTextSecondary, fontWeight = FontWeight.Medium)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(orders) { order ->
                    val customer = users.find { it.id == order.userId }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedAdminOrder = order
                                viewModel.loadOrderDetails(order.id)
                            },
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color(0xFFE0E5DB)),
                        colors = CardDefaults.cardColors(containerColor = PolishSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Đơn #${order.id} | Khách: ${customer?.fullName ?: "Khách Lạ"}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PolishOnSurface)
                                Text(formatDate(order.orderDate), fontSize = 12.sp, color = PolishTextSecondary)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(formatVnd(order.totalPrice), fontWeight = FontWeight.Bold, color = ForestGreen)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SageGreen)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(order.status, fontSize = 11.sp, color = ForestGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
