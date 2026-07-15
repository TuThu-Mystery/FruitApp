package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ShopDao
import com.example.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        FruitEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderDetailEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun shopDao(): ShopDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fruit_shop_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Seed initial data
                scope.launch(Dispatchers.IO) {
                    INSTANCE?.let { database ->
                        val dao = database.shopDao()
                        
                        // 1. Default Admin Account (admin / admin)
                        val adminId = dao.insertUser(
                            UserEntity(
                                username = "admin",
                                password = "admin",
                                role = "ADMIN",
                                fullName = "Hệ thống Admin",
                                phoneNumber = "0123456789"
                            )
                        ).toInt()

                        // 2. Default User Account (user / user123)
                        val userId = dao.insertUser(
                            UserEntity(
                                username = "user",
                                password = "user123",
                                role = "USER",
                                fullName = "Khách Hàng Việt Nam",
                                phoneNumber = "0987654321"
                            )
                        ).toInt()

                        // 3. Seed Sample Categories created by Admin
                        val cat1Id = dao.insertCategory(
                            CategoryEntity(
                                name = "Trái Cây Nhập Khẩu",
                                imageUrl = "import",
                                adminId = adminId
                            )
                        ).toInt()

                        val cat2Id = dao.insertCategory(
                            CategoryEntity(
                                name = "Trái Cây Nội Địa",
                                imageUrl = "local",
                                adminId = adminId
                            )
                        ).toInt()

                        val cat3Id = dao.insertCategory(
                            CategoryEntity(
                                name = "Trái Cây Sấy Khô",
                                imageUrl = "dry",
                                adminId = adminId
                            )
                        ).toInt()

                        // 4. Seed Sample Fruits in respective Categories
                        dao.insertFruit(
                            FruitEntity(
                                name = "Táo Envy Mỹ",
                                price = 120000.0,
                                quantity = 50,
                                imageUrl = "apple_envy",
                                categoryId = cat1Id
                            )
                        )

                        dao.insertFruit(
                            FruitEntity(
                                name = "Nho Mẫu Đơn Hàn",
                                price = 350000.0,
                                quantity = 20,
                                imageUrl = "grape_shine",
                                categoryId = cat1Id
                            )
                        )

                        dao.insertFruit(
                            FruitEntity(
                                name = "Cherry New Zealand",
                                price = 450000.0,
                                quantity = 15,
                                imageUrl = "cherry",
                                categoryId = cat1Id
                            )
                        )

                        dao.insertFruit(
                            FruitEntity(
                                name = "Sầu Riêng Ri6",
                                price = 150000.0,
                                quantity = 30,
                                imageUrl = "durian",
                                categoryId = cat2Id
                            )
                        )

                        dao.insertFruit(
                            FruitEntity(
                                name = "Măng Cụt Chợ Lách",
                                price = 85000.0,
                                quantity = 5, // Low stock to trigger checkout error easily
                                imageUrl = "mangosteen",
                                categoryId = cat2Id
                            )
                        )

                        dao.insertFruit(
                            FruitEntity(
                                name = "Xoài Cát Hòa Lộc",
                                price = 75000.0,
                                quantity = 100,
                                imageUrl = "mango",
                                categoryId = cat2Id
                            )
                        )

                        dao.insertFruit(
                            FruitEntity(
                                name = "Hạnh Nhân Sấy Bơ",
                                price = 180000.0,
                                quantity = 40,
                                imageUrl = "almond",
                                categoryId = cat3Id
                            )
                        )

                        dao.insertFruit(
                            FruitEntity(
                                name = "Mít Sấy Giòn Tấn Phát",
                                price = 60000.0,
                                quantity = 60,
                                imageUrl = "jackfruit",
                                categoryId = cat3Id
                            )
                        )
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                scope.launch(Dispatchers.IO) {
                    INSTANCE?.let { database ->
                        val dao = database.shopDao()
                        // Ensure admin exists with "admin" password
                        val admin = dao.getUserByUsername("admin")
                        if (admin == null) {
                            dao.insertUser(
                                UserEntity(
                                    username = "admin",
                                    password = "admin",
                                    role = "ADMIN",
                                    fullName = "Hệ thống Admin",
                                    phoneNumber = "0123456789"
                                )
                            )
                        } else if (admin.password != "admin") {
                            dao.updateUser(admin.copy(password = "admin"))
                        }
                        
                        // Ensure default user exists with "user123" password
                        val user = dao.getUserByUsername("user")
                        if (user == null) {
                            dao.insertUser(
                                UserEntity(
                                    username = "user",
                                    password = "user123",
                                    role = "USER",
                                    fullName = "Khách Hàng Việt Nam",
                                    phoneNumber = "0987654321"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
