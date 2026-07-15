package com.example.data.dao

import androidx.room.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): UserEntity?


    // --- Categories ---
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)


    // --- Fruits ---
    @Query("SELECT * FROM fruits")
    fun getAllFruits(): Flow<List<FruitEntity>>

    @Query("SELECT * FROM fruits WHERE categoryId = :categoryId")
    fun getFruitsByCategory(categoryId: Int): Flow<List<FruitEntity>>

    @Query("SELECT * FROM fruits WHERE name LIKE '%' || :query || '%'")
    fun searchFruits(query: String): Flow<List<FruitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFruit(fruit: FruitEntity): Long

    @Update
    suspend fun updateFruit(fruit: FruitEntity)

    @Delete
    suspend fun deleteFruit(fruit: FruitEntity)

    @Query("SELECT * FROM fruits WHERE id = :id")
    suspend fun getFruitById(id: Int): FruitEntity?


    // --- Cart Items ---
    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getCartItemsByUserId(userId: Int): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    suspend fun getCartItemsByUserIdList(userId: Int): List<CartItemEntity>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND fruitId = :fruitId LIMIT 1")
    suspend fun getCartItem(userId: Int, fruitId: Int): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItemEntity)

    @Update
    suspend fun updateCartItem(cartItem: CartItemEntity)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCartByUserId(userId: Int)


    // --- Orders ---
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY orderDate DESC")
    fun getOrdersByUserId(userId: Int): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertOrderDetail(orderDetail: OrderDetailEntity)

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    fun getOrderDetails(orderId: Int): Flow<List<OrderDetailEntity>>

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    suspend fun getOrderDetailsList(orderId: Int): List<OrderDetailEntity>

    @Update
    suspend fun updateOrder(order: OrderEntity)


    // --- Transaction Checkout Flow ---
    @Transaction
    suspend fun checkoutCart(userId: Int) {
        val cartItems = getCartItemsByUserIdList(userId)
        if (cartItems.isEmpty()) {
            throw IllegalArgumentException("Giỏ hàng của bạn đang trống!")
        }

        var totalPrice = 0.0
        val itemsToCheckout = mutableListOf<Pair<CartItemEntity, FruitEntity>>()

        // Step 1: Verification - Retrieve each fruit and check availability
        for (item in cartItems) {
            val fruit = getFruitById(item.fruitId)
                ?: throw IllegalArgumentException("Sản phẩm không tồn tại hoặc đã bị xóa!")
            
            if (fruit.quantity < item.quantity) {
                throw IllegalArgumentException("Sản phẩm '${fruit.name}' chỉ còn ${fruit.quantity} mặt hàng trong kho (bạn yêu cầu: ${item.quantity})")
            }
            totalPrice += fruit.price * item.quantity
            itemsToCheckout.add(Pair(item, fruit))
        }

        // Step 2: Create Order
        val orderId = insertOrder(
            OrderEntity(
                userId = userId,
                orderDate = System.currentTimeMillis(),
                totalPrice = totalPrice,
                status = "COMPLETED"
            )
        ).toInt()

        // Step 3: Insert Details and Subtract Stock
        for ((cartItem, fruit) in itemsToCheckout) {
            // Insert order detail
            insertOrderDetail(
                OrderDetailEntity(
                    orderId = orderId,
                    fruitId = fruit.id,
                    purchasedQuantity = cartItem.quantity,
                    unitPrice = fruit.price
                )
            )

            // Subtract inventory stock
            val updatedFruit = fruit.copy(quantity = fruit.quantity - cartItem.quantity)
            updateFruit(updatedFruit)
        }

        // Step 4: Clear User Cart
        clearCartByUserId(userId)
    }
}
