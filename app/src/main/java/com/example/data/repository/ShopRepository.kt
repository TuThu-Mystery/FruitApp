package com.example.data.repository

import com.example.data.dao.ShopDao
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

class ShopRepository(private val shopDao: ShopDao) {

    // --- Users ---
    val allUsers: Flow<List<UserEntity>> = shopDao.getAllUsers()
    
    suspend fun getUserByUsername(username: String): UserEntity? = 
        shopDao.getUserByUsername(username)

    suspend fun insertUser(user: UserEntity): Long = 
        shopDao.insertUser(user)

    suspend fun updateUser(user: UserEntity) = 
        shopDao.updateUser(user)

    suspend fun deleteUser(user: UserEntity) = 
        shopDao.deleteUser(user)

    suspend fun getUserById(id: Int): UserEntity? = 
        shopDao.getUserById(id)

    // --- Categories ---
    val allCategories: Flow<List<CategoryEntity>> = shopDao.getAllCategories()

    suspend fun insertCategory(category: CategoryEntity): Long = 
        shopDao.insertCategory(category)

    suspend fun updateCategory(category: CategoryEntity) = 
        shopDao.updateCategory(category)

    suspend fun deleteCategory(category: CategoryEntity) = 
        shopDao.deleteCategory(category)

    // --- Fruits ---
    val allFruits: Flow<List<FruitEntity>> = shopDao.getAllFruits()

    fun getFruitsByCategory(categoryId: Int): Flow<List<FruitEntity>> = 
        shopDao.getFruitsByCategory(categoryId)

    fun searchFruits(query: String): Flow<List<FruitEntity>> = 
        shopDao.searchFruits(query)

    suspend fun insertFruit(fruit: FruitEntity): Long = 
        shopDao.insertFruit(fruit)

    suspend fun updateFruit(fruit: FruitEntity) = 
        shopDao.updateFruit(fruit)

    suspend fun deleteFruit(fruit: FruitEntity) = 
        shopDao.deleteFruit(fruit)

    suspend fun getFruitById(id: Int): FruitEntity? = 
        shopDao.getFruitById(id)

    // --- Cart Items ---
    fun getCartItemsByUserId(userId: Int): Flow<List<CartItemEntity>> = 
        shopDao.getCartItemsByUserId(userId)

    suspend fun getCartItem(userId: Int, fruitId: Int): CartItemEntity? = 
        shopDao.getCartItem(userId, fruitId)

    suspend fun insertCartItem(cartItem: CartItemEntity) = 
        shopDao.insertCartItem(cartItem)

    suspend fun updateCartItem(cartItem: CartItemEntity) = 
        shopDao.updateCartItem(cartItem)

    suspend fun deleteCartItem(cartItem: CartItemEntity) = 
        shopDao.deleteCartItem(cartItem)

    suspend fun clearCartByUserId(userId: Int) = 
        shopDao.clearCartByUserId(userId)

    // --- Orders ---
    val allOrders: Flow<List<OrderEntity>> = shopDao.getAllOrders()

    fun getOrdersByUserId(userId: Int): Flow<List<OrderEntity>> = 
        shopDao.getOrdersByUserId(userId)

    fun getOrderDetails(orderId: Int): Flow<List<OrderDetailEntity>> = 
        shopDao.getOrderDetails(orderId)

    suspend fun getOrderDetailsList(orderId: Int): List<OrderDetailEntity> = 
        shopDao.getOrderDetailsList(orderId)

    suspend fun updateOrder(order: OrderEntity) = 
        shopDao.updateOrder(order)

    // --- Transaction Checkout Flow ---
    suspend fun checkoutCart(userId: Int) {
        shopDao.checkoutCart(userId)
    }
}
