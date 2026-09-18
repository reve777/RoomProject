# 🏨 Grand Luxury Resort & Hotel - 現代化線上訂房與尊榮會員系統

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![Spring Security](https://img.shields.io/badge/Security-JWT%20%2B%202FA-blue.svg)](https://spring.io/projects/spring-security)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](#)

---

## 📖 專案簡介 (Project Overview)

**Grand Luxury Resort & Hotel** 是一套專為頂級度假酒店打造的高效能、全功能線上訂房與會員管理系統。系統採用 **Spring Boot 3 + Spring Security + JWT + JPA + RESTful API** 架構，結合純原生輕量化模組式 SPA 前端（無繁重框架依賴），實現極速載入與流暢的尊榮用戶體驗。

本系統全面以**正式營運環境規格**進行設計，機敏設定透過 `.env` 環境變數安全隔離，具備**多管道認證**（密碼、訪客、Gmail 驗證碼、Google OAuth 2.0、LINE 掃碼、Google Authenticator 2FA）、**房型多圖探索**、**多身分註冊暗號機制**、**全館訂單生命週期狀態調度**及**即時非同步 Email 通知**。

---

## ✨ 核心特色與功能架構 (Core Features)

### 1. 🛏️ 精選房型探索與預覽 (Room Exploration)
* **多圖藝廊 (Gallery)**：房型支援主圖與多張實景縮圖輪播，點選縮圖即時切換高解析大圖。
* **即時多條件篩選與搜尋**：支援名稱/設施關鍵字即時搜尋、房型類別篩選、容納人數過濾及價格由低至高/由高至低動態排序。
* **智慧日期選取器**：預訂時自動填入今日入住與明日退房，並自動防呆校驗（退房日必須晚於入住日）。

### 2. 🛡️ 權限與多身分註冊體系 (RBAC & Secret Key)
* **雙角色權限**：
  * 👤 **一般貴賓會員 (`ROLE_USER`)**：預設註冊身分，可瀏覽房型、線上下單、檢視個人預約進度。
  * 👑 **系統管理者 (`ROLE_ADMIN`)**：具備全館房型 CRUD、非管理者會員維護、全館訂單總覽與狀態調度權限。
* **管理者註冊專屬暗號機制**：
  * 註冊切換為「系統管理者」時，必須輸入授權特殊驗證暗號（預設：`hk4g4hk4g4`），後端驗證無誤後方能取得 `ROLE_ADMIN` 權限。

### 3. 🔐 多管道登入與雙因子安全防護 (Multi-Auth & 2FA)
* 🔑 **傳統帳密登入**：BCrypt 高強度加密儲存密碼 + JWT Bearer Token 鑑權。
* 👤 **訪客免註冊體驗 (Guest Login)**：一鍵以訪客身分快速登入，享受即時訂房與介面互動體驗。
* ✉️ **Google Mail (Gmail) 6 位數 OTP 登入**：直接寄送 6 位數驗證碼至使用者真實 Gmail，5 分鐘內輸入驗證碼即可免密碼登入。
* 🌐 **Google OAuth 2.0 授權登入**：支援標準 OAuth 2.0 授權跳轉與回調機制。
* 💬 **LINE 掃碼安全登入**：動態生成 LINE 登入 QR Code，手機掃描授權後前端即時輪詢自動登入。
* 📱 **Google Authenticator (2FA TOTP)**：
  * 個人資料頁面可產生專屬 QR Code 或金鑰，使用手機 Google Authenticator App 綁定。
  * 啟用 2FA 後，登入需經動態驗證碼二次校驗，全方位保障帳號安全。

### 4. 📊 訂單生命週期與狀態調度 (Order Management)
系統定義 6 大標準訂單狀態，並實施嚴謹的權限隔離：

| 狀態代碼 | 狀態名稱 | 說明 | 異動權限 |
| :--- | :--- | :--- | :--- |
| `PENDING_PAYMENT` | ⏳ **下單成功未付款** | 會員線上預訂後的初始預設狀態 | 系統預設 |
| `PAID` | 💳 **下單成功已付款** | 完成金流支付 | 金流系統 / 管理者 |
| `CHECKED_IN` | 🏨 **已入住** | 貴賓完成入住辦理 | **管理者專屬** |
| `CHECKED_OUT` | 🚪 **已退房** | 貴賓完成退房手續 | **管理者專屬** |
| `REFUNDED` | ↩️ **已退款** | 款項已原路刷退 | 金流系統 / 管理者 |
| `CANCELLED` | 🔴 **已取消** | 訂單已取消 | 會員(未付款時) / 管理者 |

* **一般使用者權限**：僅可在「我的預訂紀錄」檢視個人所有訂單的即時狀態標籤與備註，**無權限任意修改狀態**。
* **管理者權限**：在「全館訂單總覽」可透過關鍵字/狀態篩選，並使用下拉選單一鍵變更任何訂單狀態。
* **非同步 Email 通知**：每次訂單下單、取消或管理者變更狀態，系統均自動透過 JavaMail 寄發通知信給該會員。

### 5. 🧰 管理者專屬維護專區
* **房型管理**：支援房型名稱、類別、價格、人數、描述、特色設施及**多圖片批次上傳/URL 清單維護**。
* **使用者管理**：管理者可維護非管理者帳號的 Email、真實姓名、電話與 2FA 啟用狀態。

---

## 🏗️ 系統架構與技術棧 (Tech Stack)

### 後端技術 (Backend)
* **核心框架**：Spring Boot 3.3.x、Spring Web MVC、Spring Data JPA
* **安全框架**：Spring Security 6、JWT (jjwt 0.11.5)
* **資料庫**：MySQL 8.x (支援 H2 / 自定義連線)
* **通知模組**：Spring Boot Starter Mail (Gmail SMTP SSL)
* **雙因子驗證**：Google Authenticator TOTP (com.warrenstrange) + ZXing QR Code
* **API 文件**：SpringDoc OpenAPI 3 (Swagger UI)
* **輔助工具**：Lombok、Hibernate Validator、Dotenv-java

### 前端技術 (Frontend)
* **架構**：SPA (Single Page Application) 原生 JavaScript (ES6+) + CSS3 + HTML5
* **模組化組件**：Header、Sidebar Menu、Footer 動態非同步載入
* **UI/UX 設計**：自適應響應式佈局 (RWD)、毛玻璃卡片、現代多彩狀態 Badge、無框架輕量化

---

## ⚙️ 環境設定與機敏資料管理 (.env)

本專案遵循 Twelve-Factor App 安全規範，所有機敏設定（帳號、密碼、金鑰、暗號）皆統一收納於根目錄的 [`.env`](file:///C:/Spring%20tool%20suite/AI/Spring%20no%20code/.env) 檔案中：

```properties
# 伺服器連接埠
SERVER_PORT=8080

# 資料庫連線配置
DB_URL=jdbc:mysql://localhost:3306/hotel_booking_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=your_mysql_password

# JWT 簽章金鑰 (64+ 字元) 與過期時間 (毫秒)
JWT_SECRET=supersecretkeyforhotelbookingsystemthatislongenoughtobespecifiedinenvfile2026
JWT_EXPIRATION=86400000

# Gmail SMTP 郵件通知服務 (使用 16 位應用程式密碼)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=zerokinger@gmail.com
MAIL_PASSWORD=vqgyznauddovegbs

# 管理者專屬註冊特殊驗證暗號
ADMIN_SECRET_KEY=hk4g4hk4g4

# 2FA 應用程式識別名稱
TWO_FACTOR_APP_NAME=GrandLuxuryHotel

# 社群登入 OAuth 2.0 憑證
GOOGLE_CLIENT_ID=your_google_client_id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your_google_client_secret
LINE_CHANNEL_ID=your_line_channel_id
LINE_CHANNEL_SECRET=your_line_channel_secret
```

---

## 🚀 快速啟動指南 (Getting Started)

### 1. 前置需求
* **Java 17 或以上** (建議 JDK 17 / JDK 21)
* **Maven 3.8+** (或直接使用專案內附的 `mvnw`)
* **MySQL 8.0+** (亦可使用預設記憶體庫)

### 2. 下載與啟動服務
開啟終端機 (PowerShell / Terminal)，於專案根目錄執行：

```bash
# 1. 執行 Maven 編譯與依賴下載
./mvnw clean compile

# 2. 啟動 Spring Boot 應用程式
./mvnw spring-boot:run
```

### 3. 開啟系統
啟動成功後，使用瀏覽器開啟：
* **首頁入口**：[http://localhost:8080](http://localhost:8080)
* **Swagger API 互動文件**：[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* **OpenAPI JSON 規格**：[http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🔑 預設測試帳號與權限說明

| 身分類型 | 帳號 (`Username`) | 預設密碼 (`Password`) | 備註說明 |
| :--- | :--- | :--- | :--- |
| **系統管理者** | `admin` | `admin123` | 擁有全館房型、訂單狀態調度與使用者維護權限 |
| **一般貴賓會員** | `testuser` | `password123` | 一般會員權限，可瀏覽房型與預約下單 |
| **訪客體驗** | *(點選「訪客快速登入」)* | *(免密碼)* | 自動建立專屬 Session 體驗預約功能 |
| **新管理者註冊** | *(自行於註冊頁輸入)* | *(自行設定)* | 身分選擇「系統管理者」，暗號輸入 `hk4g4hk4g4` |

---

## 📁 專案目錄結構 (Directory Layout)

```
Spring no code/
├── .env                                # 機敏環境變數配置檔
├── pom.xml                             # Maven 依賴與建置設定
├── README.md                           # 專案說明文件 (本檔案)
├── src/
│   ├── main/
│   │   ├── java/com/booking/
│   │   │   ├── common/                 # 全域共用類別 (ApiResponse, GlobalExceptionHandler 等)
│   │   │   ├── config/                 # 系統配置 (SwaggerConfig, SecurityConfig, AppProperties)
│   │   │   └── modules/
│   │   │       ├── auth/               # 認證授權模組 (JWT, 2FA, OAuth, Email OTP, Controller)
│   │   │       ├── hotel/              # 飯店與訂房模組 (Room, Booking, Status, Service, Controller)
│   │   │       ├── media/              # 檔案上傳與靜態資源模組
│   │   │       ├── notification/       # 郵件通知服務模組 (EmailService)
│   │   │       └── user/               # 使用者與角色管理模組
│   │   └── resources/
│   │       ├── application.yml         # 核心 Spring 配置檔 (引導讀取 .env)
│   │       └── static/                 # 前端靜態資源 (SPA)
│   │           ├── index.html          # 主頁面
│   │           ├── favicon.ico         # 網站圖示
│   │           ├── components/         # 模組化 HTML (header.html, menu.html, footer.html)
│   │           ├── css/
│   │           │   └── style.css       # 系統現代風格樣式表 (含 Badge, Responsive)
│   │           └── js/
│   │               └── app.js          # 前端核心 Controller (API 串接, 狀態切換, 驗證)
```

---

## 📝 授權條款 (License)
本專案採用 **MIT License** 授權開源，歡迎自由使用、學習與二次開發。
