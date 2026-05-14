旅行行程規劃系統

## 功能
- 會員註冊／登入／忘記密碼
- 新增／編輯／刪除行程
- 每日景點管理
- 匯出 PDF
- 登入失敗鎖定保護

## 技術
- Java 21
- Spring Boot 4
- Spring Security
- Thymeleaf
- MariaDB
- iText PDF

## 安裝步驟

1. Clone 專案
```bash
git clone https://github.com/ethanh2167-star/travel-planner
```

2. 複製設定檔
```bash
cp src/main/resources/application-example.properties src/main/resources/application.properties
```

3. 修改 `application.properties` 填入你的資料庫和 Mail 設定

4. 建立資料庫
```sql
CREATE DATABASE travel_planner;
```

5. 執行專案
```bash
./mvnw spring-boot:run
```

## 預設帳號
首次執行後請自行註冊帳號。
