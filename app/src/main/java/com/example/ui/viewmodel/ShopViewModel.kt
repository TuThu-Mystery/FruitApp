package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.*
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartItemUiModel(
    val cartItem: CartItemEntity,
    val fruitName: String,
    val fruitPrice: Double,
    val fruitImageUrl: String,
    val maxQuantity: Int
)

sealed interface Screen {
    object Login : Screen
    object Register : Screen
    object UserHome : Screen
    object AdminHome : Screen
}

@OptIn(ExperimentalCoroutinesApi::class)
class ShopViewModel(private val repository: ShopRepository) : ViewModel() {

    // --- Authentication State ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Login)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // --- Global Message Feedback ---
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    fun showFeedback(message: String) {
        _feedbackMessage.value = message
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    // --- Auth Actions ---
    fun login(usernameInput: String, passwordInput: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(usernameInput)
            if (user != null && user.password == passwordInput) {
                _currentUser.value = user
                _currentScreen.value = if (user.role == "ADMIN") Screen.AdminHome else Screen.UserHome
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun register(user: UserEntity, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val existing = repository.getUserByUsername(user.username)
            if (existing != null) {
                onResult(false, "Tên tài khoản đã tồn tại!")
                return@launch
            }
            val id = repository.insertUser(user)
            if (id > 0) {
                onResult(true, "Đăng ký tài khoản thành công!")
            } else {
                onResult(false, "Đăng ký thất bại. Vui lòng thử lại!")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentScreen.value = Screen.Login
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // --- User Side State & Actions ---
    private val _selectedCategoryId = MutableStateFlow<Int?>(null) // null = "All"
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fruits: StateFlow<List<FruitEntity>> = combine(
        repository.allFruits,
        _selectedCategoryId,
        _searchQuery
    ) { allFruits, categoryId, query ->
        allFruits.filter { fruit ->
            val matchCategory = categoryId == null || fruit.categoryId == categoryId
            val matchQuery = query.isEmpty() || fruit.name.contains(query, ignoreCase = true)
            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCategory(categoryId: Int?) {
        _selectedCategoryId.value = categoryId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- User Cart System (Combined & Reactive) ---
    val cartItems: StateFlow<List<CartItemUiModel>> = _currentUser.flatMapLatest { user ->
        if (user == null) {
            flowOf(emptyList())
        } else {
            combine(
                repository.getCartItemsByUserId(user.id),
                repository.allFruits
            ) { cartEntities, allFruits ->
                cartEntities.mapNotNull { item ->
                    val fruit = allFruits.find { it.id == item.fruitId }
                    if (fruit != null) {
                        CartItemUiModel(
                            cartItem = item,
                            fruitName = fruit.name,
                            fruitPrice = fruit.price,
                            fruitImageUrl = fruit.imageUrl,
                            maxQuantity = fruit.quantity
                        )
                    } else null
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartTotalPrice: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.fruitPrice * it.cartItem.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addToCart(fruit: FruitEntity, quantity: Int = 1) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            if (fruit.quantity <= 0) {
                showFeedback("Sản phẩm '${fruit.name}' đã hết hàng!")
                return@launch
            }
            val existing = repository.getCartItem(user.id, fruit.id)
            if (existing != null) {
                val newQty = existing.quantity + quantity
                if (newQty > fruit.quantity) {
                    showFeedback("Sản phẩm '${fruit.name}' trong kho chỉ còn ${fruit.quantity} mặt hàng!")
                    return@launch
                }
                repository.updateCartItem(existing.copy(quantity = newQty))
            } else {
                if (quantity > fruit.quantity) {
                    showFeedback("Sản phẩm '${fruit.name}' trong kho chỉ còn ${fruit.quantity} mặt hàng!")
                    return@launch
                }
                repository.insertCartItem(
                    CartItemEntity(
                        userId = user.id,
                        fruitId = fruit.id,
                        quantity = quantity
                    )
                )
            }
            showFeedback("Đã thêm '${fruit.name}' vào giỏ hàng!")
        }
    }

    fun updateCartQuantity(cartItem: CartItemEntity, change: Int) {
        viewModelScope.launch {
            val fruit = repository.getFruitById(cartItem.fruitId) ?: return@launch
            val newQty = cartItem.quantity + change
            if (newQty <= 0) {
                repository.deleteCartItem(cartItem)
            } else {
                if (newQty > fruit.quantity) {
                    showFeedback("Sản phẩm '${fruit.name}' trong kho chỉ còn ${fruit.quantity} mặt hàng!")
                    return@launch
                }
                repository.updateCartItem(cartItem.copy(quantity = newQty))
            }
        }
    }

    fun setCartQuantity(cartItem: CartItemEntity, newQty: Int) {
        viewModelScope.launch {
            val fruit = repository.getFruitById(cartItem.fruitId) ?: return@launch
            if (newQty <= 0) {
                repository.deleteCartItem(cartItem)
            } else {
                if (newQty > fruit.quantity) {
                    showFeedback("Sản phẩm '${fruit.name}' trong kho chỉ còn ${fruit.quantity} mặt hàng!")
                    return@launch
                }
                repository.updateCartItem(cartItem.copy(quantity = newQty))
            }
        }
    }

    fun removeFromCart(cartItem: CartItemEntity) {
        viewModelScope.launch {
            repository.deleteCartItem(cartItem)
        }
    }

    // --- Order Checkout (atomic Room Transaction) ---
    fun checkout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            try {
                repository.checkoutCart(user.id)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Đã xảy ra lỗi trong quá trình thanh toán")
            }
        }
    }

    // --- User Orders ---
    val userOrders: StateFlow<List<OrderEntity>> = _currentUser.flatMapLatest { user ->
        if (user == null) flowOf(emptyList()) else repository.getOrdersByUserId(user.id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Admin Side State & Actions ---
    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Admin User CRUD ---
    fun addAdminUser(user: UserEntity, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val existing = repository.getUserByUsername(user.username)
            if (existing != null) {
                onResult(false, "Tên tài khoản đã tồn tại!")
                return@launch
            }
            val id = repository.insertUser(user)
            if (id > 0) onResult(true, "Thêm User thành công!") else onResult(false, "Lỗi thêm User!")
        }
    }

    fun updateAdminUser(user: UserEntity) {
        viewModelScope.launch {
            repository.updateUser(user)
            showFeedback("Đã cập nhật tài khoản thành công!")
            // If updating current logged in user
            if (_currentUser.value?.id == user.id) {
                _currentUser.value = user
            }
        }
    }

    fun deleteAdminUser(user: UserEntity) {
        val current = _currentUser.value ?: return
        if (current.id == user.id) {
            showFeedback("Không được tự xóa tài khoản của chính mình!")
            return
        }
        viewModelScope.launch {
            repository.deleteUser(user)
            showFeedback("Đã xóa tài khoản '${user.fullName}'!")
        }
    }

    // --- Admin Category CRUD ---
    fun addAdminCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.insertCategory(category)
            showFeedback("Đã thêm danh mục '${category.name}' thành công!")
        }
    }

    fun updateAdminCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.updateCategory(category)
            showFeedback("Đã cập nhật danh mục thành công!")
        }
    }

    fun deleteAdminCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            showFeedback("Đã xóa danh mục '${category.name}' và các sản phẩm liên kết!")
        }
    }

    // --- Admin Fruit CRUD ---
    fun addAdminFruit(fruit: FruitEntity) {
        viewModelScope.launch {
            repository.insertFruit(fruit)
            showFeedback("Đã thêm trái cây '${fruit.name}' thành công!")
        }
    }

    fun updateAdminFruit(fruit: FruitEntity) {
        viewModelScope.launch {
            repository.updateFruit(fruit)
            showFeedback("Đã cập nhật trái cây thành công!")
        }
    }

    fun deleteAdminFruit(fruit: FruitEntity) {
        viewModelScope.launch {
            repository.deleteFruit(fruit)
            showFeedback("Đã xóa trái cây '${fruit.name}'!")
        }
    }

    // --- Fetch Details of any Order helper ---
    private val _currentOrderDetails = MutableStateFlow<List<OrderDetailEntity>>(emptyList())
    val currentOrderDetails: StateFlow<List<OrderDetailEntity>> = _currentOrderDetails.asStateFlow()

    fun loadOrderDetails(orderId: Int) {
        viewModelScope.launch {
            _currentOrderDetails.value = repository.getOrderDetailsList(orderId)
        }
    }
}

class ShopViewModelFactory(private val repository: ShopRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShopViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
