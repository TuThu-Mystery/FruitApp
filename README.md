# FreshFruit - Cửa hàng trái cây trực tuyến 🍏🍊

**FreshFruit** là một ứng dụng di động Android thương mại điện tử, được phát triển bằng ngôn ngữ **Kotlin** và giao diện hiện đại **Jetpack Compose**. Ứng dụng cung cấp giải pháp mua sắm trái cây tươi trực tuyến một cách nhanh chóng, tiện lợi cho cả khách hàng và quản trị viên.

## 🌟 Tính năng chính

### 🧑‍💼 Dành cho Khách hàng (Users)
- **Đăng nhập & Đăng ký**: Hỗ trợ tạo tài khoản và xác thực người dùng an toàn.
- **Duyệt sản phẩm**: Xem danh sách các loại trái cây theo danh mục, hiển thị thông tin chi tiết và tình trạng tồn kho.
- **Giỏ hàng thông minh**: Thêm, sửa, xóa sản phẩm trong giỏ hàng. Cảnh báo thông minh khi hết hàng hoặc nhập số lượng quá mức tồn kho.
- **Thanh toán**: Mô phỏng quá trình đặt hàng và thanh toán nhanh chóng.
- **Lịch sử mua hàng**: Theo dõi các đơn hàng đã mua và trạng thái của chúng.

### 👑 Dành cho Quản trị viên (Admin)
- **Quản lý Sản phẩm & Danh mục**: Thêm mới, chỉnh sửa, xóa trái cây và các danh mục sản phẩm (CRUD).
- **Quản lý Đơn hàng**: Theo dõi và quản lý tất cả các đơn hàng từ khách hàng.
- **Quản lý Người dùng**: Xem và quản lý thông tin người dùng trong hệ thống.

## 📸 Giao diện ứng dụng

Dưới đây là một số hình ảnh giao diện chính của ứng dụng:

| Đăng nhập | Đăng ký |
| :---: | :---: |
| <img src="picture/login.png" width="300" /> | <img src="picture/register.png" width="300" /> |

| Trang chủ mua sắm | Giỏ hàng | Lịch sử mua hàng |
| :---: | :---: | :---: |
| <img src="picture/home.png" width="200" /> | <img src="picture/cart.png" width="200" /> | <img src="picture/history.png" width="200" /> |

| Admin - Quản lý Trái Cây | Admin - Quản lý Danh Mục | Admin - Quản lý Đơn Hàng | Admin - Quản lý Thành viên |
| :---: | :---: | :---: | :---: |
| <img src="picture/admin_fruits.png" width="200" /> | <img src="picture/admin_categories.png" width="200" /> | <img src="picture/admin_orders.png" width="200" /> | <img src="picture/admin_users.png" width="200" /> |

## 🛠 Công nghệ sử dụng
- **Ngôn ngữ**: Kotlin
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Kiến trúc**: MVVM (Model-View-ViewModel) với Coroutines & Flow
- **Cơ sở dữ liệu cục bộ**: Room Database (SQLite) với Kotlin Symbol Processing (KSP).
- **Điều hướng**: Jetpack Navigation Compose

## 🚀 Hướng dẫn cài đặt & Chạy ứng dụng

1. **Yêu cầu hệ thống**: Android Studio (phiên bản mới nhất khuyến nghị), JDK 17+.
2. Tải mã nguồn về máy.
3. Mở thư mục dự án bằng **Android Studio**.
4. Đợi Gradle đồng bộ và tải các thư viện cần thiết.
5. Chạy ứng dụng trên Emulator hoặc thiết bị di động thật (hỗ trợ Android 8.0+).

### 🔑 Tài khoản mặc định để thử nghiệm
Khi khởi chạy lần đầu, ứng dụng tự động khởi tạo cơ sở dữ liệu mẫu với các tài khoản sau (nếu chưa có):
- **Tài khoản Admin**:
  - Tên đăng nhập: `admin`
  - Mật khẩu: `admin`
- **Tài khoản Khách hàng**:
  - Tên đăng nhập: `user`
  - Mật khẩu: `user123`

## 📱 Cấu trúc thư mục chính
- `ui/`: Chứa các màn hình giao diện Jetpack Compose (`ShopUi.kt`) và cấu hình màu sắc/theme.
- `ui/viewmodel/`: Chứa `ShopViewModel.kt` quản lý trạng thái, logic ứng dụng.
- `data/`: Chứa các lớp Entity, DAO và Room Database (`AppDatabase.kt`).

```
app/src/main/java/com/example/
├── MainActivity.kt                  # Điểm khởi chạy ứng dụng
├── data/
│   ├── dao/
│   │   └── ShopDao.kt               # Các truy vấn Room Database (DAO)
│   ├── database/
│   │   └── AppDatabase.kt           # Khai báo Room Database & seeder dữ liệu
│   ├── entity/
│   │   └── Entities.kt              # Các lớp thực thể (User, Category, Fruit, Order…)
│   └── repository/
│       └── ShopRepository.kt        # Lớp trung gian giữa ViewModel và DAO
└── ui/
    ├── ShopUi.kt                    # Toàn bộ màn hình Jetpack Compose
    ├── theme/
    │   ├── Color.kt                 # Bảng màu Material Design 3
    │   ├── Theme.kt                 # Cấu hình theme sáng/tối
    │   └── Type.kt                  # Typography
    └── viewmodel/
        └── ShopViewModel.kt         # ViewModel + StateFlow quản lý UI state
```

## 🗄 Sơ đồ cơ sở dữ liệu

Ứng dụng sử dụng **Room Database** với các bảng sau:

| Bảng | Mô tả | Quan hệ |
| :--- | :--- | :--- |
| `users` | Thông tin tài khoản (role: `ADMIN` / `USER`) | — |
| `categories` | Danh mục trái cây | FK → `users.id` (admin tạo) |
| `fruits` | Thông tin sản phẩm trái cây | FK → `categories.id` |
| `cart_items` | Sản phẩm trong giỏ hàng | FK → `users.id`, `fruits.id` |
| `orders` | Đơn hàng đã đặt | FK → `users.id` |
| `order_items` | Chi tiết từng sản phẩm trong đơn hàng | FK → `orders.id`, `fruits.id` |

> Tất cả các khóa ngoại đều được cấu hình `onDelete = CASCADE` để đảm bảo tính toàn vẹn dữ liệu.

## 🏗 Kiến trúc MVVM

```
┌─────────────────────────────────────────┐
│              UI Layer                   │
│   ShopUi.kt (Jetpack Compose Screens)   │
│   Quan sát StateFlow từ ViewModel       │
└──────────────┬──────────────────────────┘
               │ collect / LaunchedEffect
┌──────────────▼──────────────────────────┐
│           ViewModel Layer               │
│   ShopViewModel.kt                      │
│   Xử lý sự kiện UI, gọi Repository     │
│   Quản lý UiState qua StateFlow/Flow    │
└──────────────┬──────────────────────────┘
               │ suspend functions / Flow
┌──────────────▼──────────────────────────┐
│          Repository Layer               │
│   ShopRepository.kt                     │
│   Trung gian, tổng hợp nguồn dữ liệu   │
└──────────────┬──────────────────────────┘
               │ Room DAO calls
┌──────────────▼──────────────────────────┐
│           Data Layer                    │
│   ShopDao.kt + AppDatabase (Room/SQLite)│
└─────────────────────────────────────────┘
```

## 📦 Các thư viện chính

| Thư viện | Phiên bản | Mục đích |
| :--- | :--- | :--- |
| Jetpack Compose BOM | 2024.09.00 | UI framework declarative |
| Room | 2.7.0 | ORM / cơ sở dữ liệu cục bộ |
| Navigation Compose | 2.8.9 | Điều hướng giữa các màn hình |
| Lifecycle ViewModel | 2.8.7 | Quản lý vòng đời & trạng thái |
| Kotlin Coroutines | 1.10.2 | Lập trình bất đồng bộ |
| Coil Compose | 2.7.0 | Tải & hiển thị hình ảnh |
| KSP | 2.3.5 | Kotlin Symbol Processing cho Room |
| DataStore Preferences | 1.1.7 | Lưu trữ cài đặt nhẹ |

## 🧪 Kiểm thử

Dự án bao gồm các loại kiểm thử sau:

| Loại | File | Mô tả |
| :--- | :--- | :--- |
| Unit Test | `ExampleUnitTest.kt` | Kiểm thử đơn vị cơ bản |
| Instrumented Test | `ExampleInstrumentedTest.kt` | Kiểm thử trên thiết bị/emulator |
| Robolectric Test | `ExampleRobolectricTest.kt` | Kiểm thử không cần emulator |
| Screenshot Test | `GreetingScreenshotTest.kt` | Kiểm thử giao diện bằng ảnh chụp màn hình |

Chạy unit test:
```bash
./gradlew test
```
Chạy instrumented test:
```bash
./gradlew connectedAndroidTest
```

## ⚙️ Yêu cầu hệ thống

| Yêu cầu | Chi tiết |
| :--- | :--- |
| Android Studio | Meerkat (2024.3) trở lên |
| JDK | 17+ |
| Android SDK | API 26 (Android 8.0) trở lên |
| Gradle | 9.1.1 |
| Kotlin | 2.2.10 |

## 🤝 Đóng góp

Mọi đóng góp đều được hoan nghênh! Vui lòng:
1. Fork dự án.
2. Tạo branch tính năng mới (`git checkout -b feature/ten-tinh-nang`).
3. Commit thay đổi (`git commit -m 'feat: thêm tính năng XYZ'`).
4. Push lên branch (`git push origin feature/ten-tinh-nang`).
5. Mở Pull Request.

---
*Phát triển bởi AI Studio.*
