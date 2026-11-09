# Cloudflare + AWS (EC2 / RDS for Oracle) 完整部署架構指南

本指南整合 **Cloudflare（前端安全門戶）** 與 **AWS（EC2 後端 + RDS for Oracle 資料庫）**，並同時支援 **Docker / Docker Compose** 與 **Kubernetes (K8s)** 兩種部署模式。

---

## 1. 整體架構圖

```mermaid
flowchart TD
    User["終端使用者 / 瀏覽器"] -->|HTTPS 請求 (Port 443)| CF["Cloudflare (前端門戶)<br>• 自訂網域 (.com)<br>• 免費 SSL/TLS 憑證<br>• CDN 靜態快取 & DDoS 防護"]
    
    subgraph AWS_VPC ["AWS 虛擬私有雲 (VPC)"]
        subgraph Public_Subnet ["公有子網路 (Public Subnet)"]
            EC2["Amazon EC2 / Lightsail 主機<br>• Nginx 反向代理 (Port 80/443)<br>• Spring Boot 容器 (Port 8090)"]
        end
        
        subgraph Private_Subnet ["私有子網路 (Private Subnet)"]
            RDS["Amazon RDS for Oracle<br>• Oracle Database Engine<br>• Port 1521 (僅限 EC2 私網存取)"]
        end
    end

    CF -->|安全加密通道 (Full/Strict SSL)| EC2
    EC2 -->|VPC 內部私有 IP 連線| RDS
```

---

## 2. 核心元件與安全設定

### 2.1 Cloudflare (前端門戶)
1. **網域託管 (Domain & DNS)**:
   - 在 Cloudflare 購買或轉移網域（例如 `yourdomain.com`）。
   - 新增 **A 記錄** 指向 AWS EC2 的彈性公網 IP（Elastic IP），並開啟 **橘色雲朵 (Proxied 代理)**。
2. **SSL/TLS 加密模式**:
   - 設為 **Full** 或 **Full (Strict)**。
   - 可在 Cloudflare 後台產生免費的 **Origin CA Certificate**（有效期限可達 15 年），放置於 EC2 的 Nginx (`/etc/nginx/ssl/`)。
3. **CDN 快取與 DDoS 防護**:
   - Cloudflare 自動快取前端靜態資源 (JS, CSS, 圖片)，大幅減輕 EC2 負載。

---

### 2.2 AWS EC2 (應用主機) & 安全群組 (Security Group)
1. **EC2 安全群組規則 (Inbound)**:
   - **HTTP (80) / HTTPS (443)**：建議僅允許 **Cloudflare IP 範圍**（可參考 [Cloudflare IP 清單](https://www.cloudflare.com/ips/)），避免他人繞過 Cloudflare 直接打 EC2 IP。
   - **SSH (22)**：僅允許您個人的固定公網 IP 存取。
2. **部署模式支援**:
   - **Docker Compose 生產模式**：使用 `docker-compose.prod.yml`（內建 Nginx + Spring Boot）。
   - **Kubernetes 模式**：使用 `k8s/aws-rds-cloud-deployment.yaml`。

---

### 2.3 AWS RDS for Oracle (資料庫)
1. **引擎選擇**：Amazon RDS for Oracle (Standard Edition Two 或 Enterprise Edition)。
2. **網路隔離**：
   - 設定為 **不可公開存取 (Publicly Accessible: No)**。
   - 放置於 VPC 私有子網路 (Private Subnet)。
3. **RDS 安全群組規則 (Inbound)**:
   - **Port 1521**：只允許 EC2 的安全群組 ID (`sg-xxxxxx`) 或 VPC 內部私有 CIDR 存取，杜絕外網直連風險。
4. **連線字串 (JDBC)**:
   ```properties
   jdbc:oracle:thin:@//<rds-endpoint>:1521/<service_name_or_sid>
   ```

---

## 3. 部署實作步驟

### 方案 A：使用 Docker Compose 部署 (推薦於單台 EC2)

1. **上傳專案至 EC2**:
   ```bash
   git clone <your-repository-url>
   cd "Spring no code"
   ```

2. **配置 SSL 憑證 (可選 - 若使用 Cloudflare Strict SSL)**:
   - 建立目錄：`mkdir -p nginx/ssl`
   - 將 Cloudflare 後台產生的憑證存為：
     - `nginx/ssl/cloudflare_origin.crt`
     - `nginx/ssl/cloudflare_origin.key`

3. **啟動生產環境容器**:
   ```bash
   # 設定 RDS 連線環境變數並啟動
   export AWS_RDS_ORACLE_URL="jdbc:oracle:thin:@//your-rds-endpoint.rds.amazonaws.com:1521/ORCL"
   export AWS_RDS_USERNAME="admin"
   export AWS_RDS_PASSWORD="YourRdsPassword123"

   docker-compose -f docker-compose.prod.yml up -d --build
   ```

---

### 方案 B：使用 Kubernetes (K8s / k3s / EKS) 部署

1. **修改設定檔中的 RDS 資訊**:
   - 編輯 [k8s/aws-rds-cloud-deployment.yaml](file:///C:/Spring%20tool%20suite/AI/Spring%20no%20code/k8s/aws-rds-cloud-deployment.yaml) 中的 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` 及 `rules.host` 網域名稱。

2. **一鍵套用 K8s 部署**:
   ```bash
   kubectl apply -f k8s/aws-rds-cloud-deployment.yaml
   ```

3. **查看運作狀態**:
   ```bash
   kubectl get pods -l app=hotel-booking
   kubectl get ingress
   ```

---

## 4. 本地開發與現有部署相容性

- 本地 Docker 開發：繼續使用原始 [docker-compose.yml](file:///C:/Spring%20tool%20suite/AI/Spring%20no%20code/docker-compose.yml)（直連本機 Oracle `host.docker.internal`）。
- 本地 K8s 測試：繼續使用原始 [k8s/app-deployment-ingress.yaml](file:///C:/Spring%20tool%20suite/AI/Spring%20no%20code/k8s/app-deployment-ingress.yaml)。
- 雲端生產部署：無縫切換至 [docker-compose.prod.yml](file:///C:/Spring%20tool%20suite/AI/Spring%20no%20code/docker-compose.prod.yml) 或 [k8s/aws-rds-cloud-deployment.yaml](file:///C:/Spring%20tool%20suite/AI/Spring%20no%20code/k8s/aws-rds-cloud-deployment.yaml)。
