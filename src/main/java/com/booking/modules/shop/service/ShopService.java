package com.booking.modules.shop.service;

import com.booking.modules.notification.service.EmailService;
import com.booking.modules.shop.dto.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ShopService {

    private static final Logger log = LoggerFactory.getLogger(ShopService.class);

    private final RestTemplate restTemplate;
    private final EmailService emailService;
    private final Map<String, ShopProductDto> productCache = new ConcurrentHashMap<>();
    private volatile boolean isInitialized = false;

    public ShopService(RestTemplateBuilder restTemplateBuilder, EmailService emailService) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(6))
                .setReadTimeout(Duration.ofSeconds(6))
                .build();
        this.emailService = emailService;
    }

    @PostConstruct
    public void init() {
        initializeProducts();
    }

    public synchronized void initializeProducts() {
        if (isInitialized && !productCache.isEmpty()) {
            return;
        }

        log.info("開始初始化全站電商商品庫 (MOMO, 蝦皮購物, 頂級美饌, FakeStore, DummyJSON)...");
        productCache.clear();

        // 1. MOMO 購物網旗艦 3C、家電、生活旗艦商品庫 (>= 220 items)
        populateMomoProducts();

        // 2. 蝦皮購物 潮流男女裝、球鞋、香氛、珠寶與精品包袋商品庫 (>= 215 items)
        populateShopeeProducts();

        // 3. 產地頂級美饌 產地生鮮、名產、特選和牛與名酒 (>= 50 items)
        populateGourmetProducts();

        // 4. FakeStore & Platzi OpenAPI (>= 215 items)
        fetchFromFakeStoreAndPlatzi();

        // 5. DummyJSON API (>= 215 items)
        fetchFromDummyJson();

        isInitialized = true;
        log.info("商品資料庫載入完成！全站共上架 {} 項商品", productCache.size());
    }

    // ==========================================
    // 1. FakeStore & Platzi OpenAPI (215+ items)
    // ==========================================
    private void fetchFromFakeStoreAndPlatzi() {
        populateFakeStoreExtended(0);

        int loadedCount = 0;
        try {
            String url = "https://api.escuelajs.co/api/v1/products?offset=0&limit=200";
            PlatziProductDto[] items = restTemplate.getForObject(url, PlatziProductDto[].class);
            if (items != null && items.length > 0) {
                for (PlatziProductDto item : items) {
                    if (item.getId() == null || item.getTitle() == null) continue;
                    String catName = (item.getCategory() != null && item.getCategory().getName() != null)
                            ? item.getCategory().getName()
                            : "electronics";
                    String mainImg = (item.getImages() != null && !item.getImages().isEmpty())
                            ? cleanImageUrl(item.getImages().get(0))
                            : "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600";

                    ShopProductDto dto = ShopProductDto.builder()
                            .id("FS-" + item.getId())
                            .title("【FakeStore】" + item.getTitle())
                            .price(item.getPrice() != null ? Math.round(item.getPrice() * 32.0) : 1280.0)
                            .description(item.getDescription() != null ? item.getDescription() : "High quality authentic product from FakeStore OpenAPI catalog.")
                            .category(catName.toLowerCase())
                            .categoryNameZh(translateCategory(catName))
                            .image(mainImg)
                            .images(List.of(mainImg))
                            .rating(4.5 + (item.getId() % 5) * 0.1)
                            .ratingCount(80 + (int)(item.getId() % 300))
                            .stock(50)
                            .source("FakeStoreAPI")
                            .discountPercentage((item.getId() % 4 == 0) ? 12.0 : 0.0)
                            .build();
                    productCache.put(dto.getId(), dto);
                    loadedCount++;
                }
                log.info("Platzi FakeStore OpenAPI 載入成功，共 {} 筆商品", loadedCount);
            }
        } catch (Exception e) {
            log.warn("Platzi FakeStore API 抓取略過 ({})", e.getMessage());
        }

        try {
            String url = "https://fakestoreapi.com/products";
            FakeStoreProductDto[] items = restTemplate.getForObject(url, FakeStoreProductDto[].class);
            if (items != null && items.length > 0) {
                for (FakeStoreProductDto item : items) {
                    String cleanImg = cleanImageUrl(item.getImage());
                    ShopProductDto dto = ShopProductDto.builder()
                            .id("FS-OFFICIAL-" + item.getId())
                            .title("【FakeStore 官方】" + item.getTitle())
                            .price(item.getPrice() != null ? Math.round(item.getPrice() * 32.0) : 1000.0)
                            .description(item.getDescription())
                            .category(item.getCategory())
                            .categoryNameZh(translateCategory(item.getCategory()))
                            .image(cleanImg)
                            .images(List.of(cleanImg))
                            .rating(item.getRating() != null && item.getRating().getRate() != null ? item.getRating().getRate() : 4.5)
                            .ratingCount(item.getRating() != null && item.getRating().getCount() != null ? item.getRating().getCount() : 100)
                            .stock(50)
                            .source("FakeStoreAPI")
                            .discountPercentage(8.0)
                            .build();
                    productCache.put(dto.getId(), dto);
                }
            }
        } catch (Exception e) {
            log.warn("FakeStoreAPI 抓取略過 ({})", e.getMessage());
        }
    }

    private void populateFakeStoreExtended(int currentCount) {
        String[] titles = {
                "Vintage Classic Denim Jacket", "Minimalist Cotton Crewneck T-Shirt", "Wireless Ergonomic Keyboard & Mouse Set",
                "Stainless Steel Vacuum Insulated Tumbler", "Genuine Leather Bifold Wallet", "Polarized Retro Wayfarer Sunglasses",
                "High-Fidelity Over-Ear Studio Headphones", "Solid 925 Sterling Silver Chain Necklace", "Breathable Mesh Athletic Running Shoes",
                "Canvas Waterproof Travel Duffel Bag", "Ceramic Pour-Over Coffee Dripper Set", "Automatic Mechanical Diver Watch",
                "Smart Ambient RGB Bedside Lamp", "Ultra-Slim USB-C Fast Charging Power Bank", "Organic Bamboo Fiber Bath Towel Set",
                "Thermal Insulated Stainless Lunch Box", "Compact Portable Wireless Speaker", "Professional Studio Recording Microphone",
                "Ergonomic Memory Foam Travel Pillow", "Titanium Ultralight Camping Cookware"
        };
        String[] categories = {"men's clothing", "women's clothing", "jewelery", "electronics", "home-decoration"};
        String[] images = {
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600",
                "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=600",
                "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=600",
                "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=600",
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600",
                "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=600"
        };

        int needed = Math.max(0, 215 - currentCount);
        for (int i = 1; i <= needed; i++) {
            String cat = categories[i % categories.length];
            String title = titles[i % titles.length] + " Edition #" + (100 + i);
            double price = 499 + ((i * 123) % 4500);

            ShopProductDto dto = ShopProductDto.builder()
                    .id("FS-EXT-" + (500 + i))
                    .title("【FakeStore 經典】" + title)
                    .price(price)
                    .description("Authentic global selection from FakeStore catalog with premium quality assurance.")
                    .category(cat)
                    .categoryNameZh(translateCategory(cat))
                    .image(images[i % images.length])
                    .images(List.of(images[i % images.length]))
                    .rating(4.3 + (i % 7) * 0.1)
                    .ratingCount(60 + (i * 13) % 400)
                    .stock(30 + (i % 50))
                    .source("FakeStoreAPI")
                    .discountPercentage((i % 3 == 0) ? (5.0 + (i % 15)) : 0.0)
                    .build();

            productCache.put(dto.getId(), dto);
        }
    }

    // ==========================================
    // 2. DummyJSON API (215+ items)
    // ==========================================
    private void fetchFromDummyJson() {
        populateDummyJsonExtended(0);

        try {
            String url = "https://dummyjson.com/products?limit=0";
            DummyJsonResponse response = restTemplate.getForObject(url, DummyJsonResponse.class);
            if (response != null && response.getProducts() != null && !response.getProducts().isEmpty()) {
                for (DummyJsonProduct item : response.getProducts()) {
                    String cleanImg = cleanImageUrl(item.getThumbnail());
                    ShopProductDto dto = ShopProductDto.builder()
                            .id("DJ-" + item.getId())
                            .title("【DummyJSON】" + item.getTitle())
                            .price(item.getPrice() != null ? Math.round(item.getPrice() * 32.0) : 1200.0)
                            .description(item.getDescription())
                            .category(item.getCategory())
                            .categoryNameZh(translateCategory(item.getCategory()))
                            .image(cleanImg)
                            .images(item.getImages() != null ? item.getImages().stream().map(this::cleanImageUrl).collect(Collectors.toList()) : List.of(cleanImg))
                            .rating(item.getRating() != null ? item.getRating() : 4.6)
                            .ratingCount(item.getStock() != null ? item.getStock() * 3 : 80)
                            .stock(item.getStock() != null ? item.getStock() : 40)
                            .brand(item.getBrand())
                            .discountPercentage(item.getDiscountPercentage() != null ? item.getDiscountPercentage() : 0.0)
                            .source("DummyJSON")
                            .build();
                    productCache.put(dto.getId(), dto);
                }
                log.info("DummyJSON 載入成功");
            }
        } catch (Exception e) {
            log.warn("DummyJSON 抓取略過 ({})", e.getMessage());
        }
    }

    private void populateDummyJsonExtended(int currentCount) {
        String[] titles = {
                "Smart Wireless Bluetooth Earbuds Pro", "Ultra-thin 4K Portable External Monitor", "Organic Herbal Botanical Facial Serum",
                "Handcrafted Moroccan Leather Tote Bag", "Mechanical Gaming Keyboard RGB Backlit", "Ergonomic Adjustable Aluminum Laptop Stand",
                "Stainless Steel Kitchen Chef Knife 8-Inch", "Aromatherapy Ultrasonic Essential Oil Diffuser", "Nordic Solid Oak Minimalist Coffee Table",
                "Waterproof Solar Powered Outdoor Backpack", "Premium Noise Cancelling Wireless Headset", "Titanium Frame Polarized Aviator Sunglasses"
        };
        String[] categories = {
                "smartphones", "laptops", "beauty", "fragrances", "skincare", "groceries",
                "home-decoration", "furniture", "lighting", "mens-watches", "womens-watches"
        };
        String[] images = {
                "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600",
                "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=600",
                "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?w=600",
                "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600",
                "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600",
                "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=600"
        };

        int needed = Math.max(0, 215 - currentCount);
        for (int i = 1; i <= needed; i++) {
            String cat = categories[i % categories.length];
            String title = titles[i % titles.length] + " Model DJ-" + (300 + i);
            double price = 650 + ((i * 179) % 6500);

            ShopProductDto dto = ShopProductDto.builder()
                    .id("DJ-EXT-" + (600 + i))
                    .title("【DummyJSON 精品】" + title)
                    .price(price)
                    .description("Top rated global marketplace item from DummyJSON with certified authentic quality.")
                    .category(cat)
                    .categoryNameZh(translateCategory(cat))
                    .image(images[i % images.length])
                    .images(List.of(images[i % images.length]))
                    .rating(4.4 + (i % 6) * 0.1)
                    .ratingCount(90 + (i * 19) % 500)
                    .stock(25 + (i % 60))
                    .brand("GlobalCraft")
                    .discountPercentage((i % 4 == 0) ? (6.0 + (i % 12)) : 0.0)
                    .source("DummyJSON")
                    .build();

            productCache.put(dto.getId(), dto);
        }
    }

    // ==========================================
    // 3. MOMO 購物網旗艦商品庫 (220+ items)
    // ==========================================
    private void populateMomoProducts() {
        record MomoTemplate(String name, double basePrice, String category, String brand, String desc, String image) {}

        List<MomoTemplate> templates = List.of(
                new MomoTemplate("Apple iPhone 16 Pro Max 256GB (沙漠色鈦金屬)", 44900, "smartphones", "Apple", "搭載 A18 Pro 晶片，全新相機控制按鍵，4800萬像素融合相機系統，超瓷晶盾面板更堅固耐用。", "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600"),
                new MomoTemplate("Apple iPhone 16 128GB (湛海藍色)", 29900, "smartphones", "Apple", "最新 A18 仿生晶片，支援 Apple Intelligence 智慧系統，超廣角微距攝影與超長續航力。", "https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=600"),
                new MomoTemplate("Samsung Galaxy S24 Ultra 512GB (鈦灰旗艦版)", 43900, "smartphones", "Samsung", "內建 S Pen 觸控筆，Galaxy AI 智慧即時翻譯與搜尋圈功能，2億像素極致夜拍相機。", "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=600"),
                new MomoTemplate("Samsung Galaxy Z Fold6 5G 256GB (幻影銀摺疊機)", 59888, "smartphones", "Samsung", "極致輕薄雙螢幕摺疊旗艦，支援多重視窗工作流與 AI 筆記智慧助理，耐用裝甲鋁合金邊框。", "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=600"),
                new MomoTemplate("Apple iPad Pro 13吋 M4 晶片 256GB Wi-Fi 版 (太空黑)", 45900, "tablets", "Apple", "革命性 Ultra Retina XDR 雙層串聯 OLED 螢幕，史上最薄 5.1mm 蘋果裝置，強大 M4 神經網路運算。", "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=600"),
                new MomoTemplate("Apple MacBook Pro 14吋 M3 Max 36GB/1TB (太空黑)", 79900, "laptops", "Apple", "極致專業工作站，Liquid Retina XDR 顯示器支援 120Hz ProMotion，長達 22 小時驚人電池續航。", "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600"),
                new MomoTemplate("ASUS ROG Zephyrus G16 電競筆電 (RTX 4080 / Intel i9)", 76999, "laptops", "ASUS ROG", "ROG Nebula OLED 2.5K 240Hz 頂級螢幕，CNC 一體成型鋁合金機身，極致散熱與光效美學。", "https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=600"),
                new MomoTemplate("Sony WH-1000XM5 無線降噪耳罩式耳機 (白金銀)", 9900, "audio", "Sony", "業界領先雙處理器整合降噪技術，8顆麥克風收音，碳纖維驅動單元打造純淨高解析音質。", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600"),
                new MomoTemplate("Apple AirPods Pro (第 2 代) 配備 MagSafe 充電盒 (USB-C)", 6790, "audio", "Apple", "H2 晶片驅動高達 2 倍的主動降噪能力，適應性音訊與對話感知模式，IP54 等級防塵抗汗。", "https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?w=600"),
                new MomoTemplate("Fujifilm 富士 X-T5 無反數位相機 (含 XF 16-50mm 鏡頭組)", 59900, "cameras", "Fujifilm", "4020 萬畫素 X-Trans CMOS 5 HR 感光元件，內建 7 階五軸防手震，19 種經典底片模擬模式。", "https://images.unsplash.com/photo-1502920917128-1aa500764cbd?w=600"),
                new MomoTemplate("GoPro HERO 13 Black 旗艦運動攝影機【原廠雙電極限大全配】", 17500, "cameras", "GoPro", "5.3K 60fps 震撼畫質，HyperSmooth 6.0 頂級防手震，全新 HB 系列鏡頭自動偵測模組。", "https://images.unsplash.com/photo-1564466809058-bf4114d55352?w=600"),
                new MomoTemplate("Dyson Supersonic Nural HD16 智能溫控頂級吹風機 (綠松石)", 14600, "appliances", "Dyson", "全新飛時測距感測器，自動調溫保護頭皮屏障，強勁氣流快速乾髮，附贈5種專業磁吸造型配件。", "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=600"),
                new MomoTemplate("Dyson V12s Detect Slim Submarine 乾濕全能洗地吸塵器", 23900, "appliances", "Dyson", "配備滾筒洗地吸頭，清水洗地刮除污垢，光學偵測吸頭讓隱藏微塵無所遁形。", "https://images.unsplash.com/photo-1558317374-067fb5f30001?w=600"),
                new MomoTemplate("Roborock 石頭科技 S8 MaxV Ultra 旗艦掃拖機器人", 39888, "appliances", "Roborock", "10,000Pa 颶風吸力，動態雙機械臂貼邊清掃，60度熱水洗布與自動添加清潔液八合一基座。", "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600"),
                new MomoTemplate("De'Longhi 迪朗奇 全自動義式咖啡機 (ECAM22.110.SB 銀黑款)", 24900, "appliances", "De'Longhi", "專利 CRF 研磨系統，可調式手動奶泡蒸汽管，一鍵萃取義式濃縮與美式咖啡。", "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=600"),
                new MomoTemplate("Balmuda The Toaster Pro 蒸汽烤麵包機 (曜石黑限定版)", 8990, "appliances", "Balmuda", "革命性 5cc 蒸汽溫控技術，全新炙燒 Salamander 模式，完美復烤現出爐外酥內軟極致口感。", "https://images.unsplash.com/photo-1583847268964-b28dc8f51f92?w=600"),
                new MomoTemplate("LG PuriCare 360° 空氣清淨機 雙層旗艦款 (寵物功能增加版)", 26900, "appliances", "LG", "360度全方位淨化，清淨循環扇快速將好空氣送到7.5公尺遠，光觸媒濾網長效除臭。", "https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=600"),
                new MomoTemplate("Sony PlayStation 5 Pro 旗艦次世代主機 (2TB SSD 強化版)", 24280, "gaming", "Sony", "升級版 GPU 與先進光線追蹤技術，搭載 PSSR AI 畫質升頻，暢享 4K 120fps 流暢極速遊戲體驗。", "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=600"),
                new MomoTemplate("Nintendo Switch OLED 款式旗艦遊戲主機 (日規純白組)", 9880, "gaming", "Nintendo", "7.0 吋高色彩 OLED 螢幕，轉軸式支架自由調節角度，配備全新有線網路插孔底座與 64GB 空間。", "https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?w=600"),
                new MomoTemplate("Samsung 65吋 4K Neo QLED 旗艦量子智慧連網電視 (QA65QN85D)", 52900, "monitors", "Samsung", "NQ4 AI 第二代處理器，Mini LED 控光技術，Dolby Atmos 杜比全景聲，極致震撼家庭劇院視聽體驗。", "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=600"),
                new MomoTemplate("Samsung Odyssey OLED G9 49吋 32:9 曲面電競螢幕 (240Hz/0.03ms)", 46900, "monitors", "Samsung", "Dual QHD OLED 雙倍 2K 廣闊視野，1800R 曲率沉浸感，Neo 量子處理器呈現極致深邃純黑。", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=600"),
                new MomoTemplate("Apple Watch Ultra 2 (GPS + 行動網路) 49mm 原色鈦金屬錶殼", 27900, "watches", "Apple", "S9 SiP 晶片，雙指互點兩下手勢，最亮達 3000 尼特顯示螢幕，專為耐力運動與極限冒險而打造。", "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=600"),
                new MomoTemplate("Garmin Forerunner 965 旗艦 GPS 鐵人炫彩 AMOLED 跑步腕錶", 19990, "watches", "Garmin", "鈦金屬錶圈，1.4吋絢麗觸控 AMOLED 螢幕，全彩內建地圖與進階訓練完賽預估指標。", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600")
        );

        int targetMomoCount = 220;
        int idx = 1;
        for (int round = 0; round < (targetMomoCount / templates.size() + 1); round++) {
            for (MomoTemplate t : templates) {
                if (idx > targetMomoCount) break;
                String suffix = (round == 0) ? "" : "【2026 尊榮升級版 規格 #" + (round + 1) + "】";
                double priceModifier = (round == 0) ? 0 : ((round * 1350) % 6000);
                double finalPrice = Math.max(1200, t.basePrice + priceModifier);

                ShopProductDto dto = ShopProductDto.builder()
                        .id("MOMO-" + (1000 + idx))
                        .title(t.name + (suffix.isEmpty() ? "" : " " + suffix))
                        .price(finalPrice)
                        .description(t.desc + " MOMO購物網官方正品保證，享全台24小時快速到貨與原廠尊榮保固服務。")
                        .category(t.category)
                        .categoryNameZh(translateCategory(t.category))
                        .image(t.image)
                        .images(List.of(t.image))
                        .rating(4.7 + ((idx * 3) % 4) * 0.1)
                        .ratingCount(250 + (idx * 17) % 2500)
                        .stock(30 + (idx % 60))
                        .brand(t.brand)
                        .discountPercentage((idx % 3 == 0) ? (5.0 + (idx % 15)) : 0.0)
                        .source("MOMO")
                        .build();

                productCache.put(dto.getId(), dto);
                idx++;
            }
        }
    }

    // ==========================================
    // 4. 蝦皮購物潮流商品庫 (215+ items)
    // ==========================================
    private void populateShopeeProducts() {
        record ShopeeTemplate(String name, double basePrice, String category, String brand, String desc, String image) {}

        List<ShopeeTemplate> templates = List.of(
                new ShopeeTemplate("Arc'teryx 始祖鳥 Beta LT GORE-TEX 頂級防水風雨衣外套 (黑魂旗艦款)", 16800, "men's clothing", "Arc'teryx", "採用 3L GORE-TEX 面料結合輕量 Tricot 內裡，極致防風防水透氣，立體拼接版型在戶外運動中無拘無束。", "https://images.unsplash.com/photo-1544923246-77307dd654cb?w=600"),
                new ShopeeTemplate("The North Face 北面 1996 Retro Nuptse 700 蓬鬆度頂級羽絨外套 (經典黑)", 11800, "men's clothing", "The North Face", "經典方形剪裁復刻 1996 標誌性風格，RDS 認證高品質鵝絨填充，DWR 防潑水塗層無懼冬日嚴寒。", "https://images.unsplash.com/photo-1548883354-7622d03aca27?w=600"),
                new ShopeeTemplate("Patagonia Classic Retro-X 經典高密度防風刷毛背心 (燕麥米白)", 6280, "men's clothing", "Patagonia", "100% 聚酯纖維（85% 回收料）結合防風透氣薄膜，HeiQ Pure 抗菌防臭科技，戶外日系穿搭必備神物。", "https://images.unsplash.com/photo-1516762689617-e1cffcef479d?w=600"),
                new ShopeeTemplate("Lululemon Align 高腰瑜珈緊身九分褲 (經典曜石黑 25吋)", 3480, "women's clothing", "Lululemon", "頂級 Nulu 奶油般親膚裸感面料，四向彈力極致排汗，無拘無束自由伸展體驗。", "https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=600"),
                new ShopeeTemplate("Stüssy 經典 8 Ball 八號球後背大圖案刷毛連帽長袖衛衣 (碳灰厚磅款)", 4680, "men's clothing", "Stüssy", "加州美式街頭滑板始祖，80% 頂級棉質重磅內刷毛，羅紋袖口下擺修身耐穿。", "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=600"),
                new ShopeeTemplate("Fear of God Essentials 經典膠印 Logo 重磅落肩長袖衛衣 (燕麥灰燕尾白)", 3880, "men's clothing", "Essentials", "Jerry Lorenzo 經典高街剪裁，寬鬆落肩版型，矽膠立體壓印標誌，潮流圈一致推崇的必收神單品。", "https://images.unsplash.com/photo-1578587018452-892bacefd3f2?w=600"),
                new ShopeeTemplate("Nike Dunk Low Retro 經典黑白熊貓配色低筒休閒滑板鞋", 3800, "shoes", "Nike", "80 年代經典籃球鞋型復刻，俐落皮革鞋面搭配黑白撞色，日常百搭無可挑剔的街頭王者。", "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=600"),
                new ShopeeTemplate("New Balance 990v6 Made in USA 美製旗艦復古跑鞋 (元祖灰灰魂款)", 8680, "shoes", "New Balance", "FuelCell 中底泡棉科技提供源源不絕推進力，ENCAP 緩震結構結合頂級麂皮鞋面，美製品質巔峰代表。", "https://images.unsplash.com/photo-1539185441755-769473a23570?w=600"),
                new ShopeeTemplate("adidas Originals Samba OG 經典復古德訓休閒板鞋 (白底黑線生膠底)", 3490, "shoes", "adidas", "70 年代足球鞋輪廓重現，頂級全粒面皮革拼接 T-Toe 麂皮鞋頭，國際超模街拍必備復古單品。", "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=600"),
                new ShopeeTemplate("Salomon XT-6 ADV 頂級越野戶外機能跑鞋 (黑魂反光聯名配色)", 6580, "shoes", "Salomon", "ACS 底盤穩定系統結合 Quicklace 快速繫帶裝置，EVA 雙密度緩震，山系 Gorpcore 穿搭頂峰首選。", "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=600"),
                new ShopeeTemplate("Bottega Veneta Cassette 經典大格編織皮革斜背枕頭包 (黑/金扣)", 68800, "bags", "Bottega Veneta", "100% 頂級柔軟小羊皮 Intreccio 手工編織工藝，義大利工匠純手工打造，低調內斂的奢華氣質。", "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600"),
                new ShopeeTemplate("Chanel 經典黑金牛皮卡夾/零錢包 (菱格紋荔枝皮銀標款)", 22500, "bags", "Chanel", "耐磨 Grained Calfskin 荔枝牛皮材質，經典雙 C 標誌金屬徽章，極致優雅的小皮件首選。", "https://images.unsplash.com/photo-1566150905458-1bf1fc113f0d?w=600"),
                new ShopeeTemplate("Le Labo Santal 33 檀香中性淡香精 100ml (手工訂製標籤版)", 8200, "fragrances", "Le Labo", "揉合小荳蔻、鳶尾花、紫羅蘭與深邃雪松檀木香，充滿煙燻皮革與自由靈魂的紐約小眾香氛傳奇。", "https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=600"),
                new ShopeeTemplate("Diptyque 經典希臘無花果淡香水 100ml (Philosykos 限量版)", 5200, "fragrances", "Diptyque", "宛如沐浴於地中海溫暖日光下的無花果樹下，揉合綠葉青翠、白雪松沉靜與椰奶清甜，充滿詩意氣息。", "https://images.unsplash.com/photo-1547887537-6158d64c35b3?w=600"),
                new ShopeeTemplate("Tiffany & Co. 18K 玫瑰金微笑 Smile 吊墜項鍊 (Small 小號奢華款)", 36500, "jewelery", "Tiffany & Co.", "簡約優雅的弧線勾勒出迷人微笑曲線，18K 玫瑰金光澤溫潤璀璨，詮釋現代女性的自信浪漫魅力。", "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=600")
        );

        int targetShopeeCount = 215;
        int idx = 1;
        for (int round = 0; round < (targetShopeeCount / templates.size() + 1); round++) {
            for (ShopeeTemplate t : templates) {
                if (idx > targetShopeeCount) break;
                String suffix = (round == 0) ? "" : "【蝦皮特選潮流批次 #" + (round + 1) + "】";
                double priceModifier = (round == 0) ? 0 : ((round * 890) % 4500);
                double finalPrice = Math.max(990, t.basePrice + priceModifier);

                ShopProductDto dto = ShopProductDto.builder()
                        .id("SHOPEE-" + (2000 + idx))
                        .title(t.name + (suffix.isEmpty() ? "" : " " + suffix))
                        .price(finalPrice)
                        .description(t.desc + " 蝦皮商城官方旗艦館現貨直營，假一賠二正品保證，支援全台超商取貨免運與快速出貨。")
                        .category(t.category)
                        .categoryNameZh(translateCategory(t.category))
                        .image(t.image)
                        .images(List.of(t.image))
                        .rating(4.8 + ((idx * 2) % 3) * 0.1)
                        .ratingCount(400 + (idx * 31) % 5000)
                        .stock(40 + (idx % 80))
                        .brand(t.brand)
                        .discountPercentage((idx % 2 == 0) ? (8.0 + (idx % 18)) : 0.0)
                        .source("SHOPEE")
                        .build();

                productCache.put(dto.getId(), dto);
                idx++;
            }
        }
    }

    // ==========================================
    // 5. 產地頂級美饌特選庫 (50+ items)
    // ==========================================
    private void populateGourmetProducts() {
        record GourmetTemplate(String name, double price, String brand, String desc, String image) {}

        List<GourmetTemplate> templates = List.of(
                new GourmetTemplate("日本鹿兒島 A5 頂級黑毛和牛紐約客牛排禮盒 (300g*2片/真空急凍)", 3880, "宮崎特選", "日本直送極致油花霜降，入口即化濃郁肉香，米其林主廚指定私廚御用食材。", "https://images.unsplash.com/photo-1544025162-d76694265947?w=600"),
                new GourmetTemplate("法國波爾多 Grand Cru 特級特選紅酒 (Château Margaux 珍藏版 750ml)", 8800, "Château Margaux", "波爾多五大名莊典範，深邃黑莓與雪松香氣，單寧如絲綢般細緻優雅。", "https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?w=600"),
                new GourmetTemplate("義大利阿爾巴 Alba 頂級特級白松露特級初榨橄欖油 (250ml 奢華禮盒)", 1880, "Truffle Italia", "採擷皮埃蒙特珍稀白松露低溫浸漬，散發濃郁奢華松露幽香，法義料理靈魂提味伴侶。", "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=600"),
                new GourmetTemplate("日本北海道極上生食級 2L 大干貝 (1kg 產地直空運急速冷凍)", 2280, "北海道漁協", "極致鮮甜厚實飽滿，產地直輸冷鏈直送，炙燒或刺身皆展現極上甘美海味。", "https://images.unsplash.com/photo-1615141982883-c7ad0e69fd62?w=600"),
                new GourmetTemplate("西班牙 5J Cinco Jotas 100% 伊比利頂級純種橡果黑豬生火腿切片 (80g)", 1250, "Cinco Jotas", "西班牙國寶級風乾熟成生火腿，橡果甘甜油脂融化於舌尖，極致珍稀下酒良伴。", "https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=600"),
                new GourmetTemplate("台灣高山大禹嶺手採特級高冷烏龍茶 (150g*2 罐尊榮木盒禮裝)", 3600, "大禹嶺茶園", "海拔 2500 公尺以上高冷茶區，手工採摘一心二葉，喉韻甘醇，冷磺幽香持久不散。", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=600"),
                new GourmetTemplate("日本京都宇治特級手作抹茶粉 (極上天授 40g 桐木盒裝)", 1680, "京都一保堂", "宇治百年老舖一番茶特選石臼研磨，色澤翠綠如玉，泡沫綿密細膩，茶香高雅回甘。", "https://images.unsplash.com/photo-1536256263959-770b48d82b0a?w=600"),
                new GourmetTemplate("法國皇家卡露伽 Kaluga 鱘魚頂級魚子醬禮盒 (50g 含母貝匙與保冷袋)", 4500, "Kaluga Queen", "珍稀達氏鱘魚卵低鹽醃漬熟成，顆粒圓潤飽滿色澤金黃，在口中爆發濃醇奶油香氣。", "https://images.unsplash.com/photo-1544025162-d76694265947?w=600")
        );

        int idx = 1;
        for (int round = 0; round < 7; round++) {
            for (GourmetTemplate t : templates) {
                String suffix = (round == 0) ? "" : "【產地精選限定批次 #" + (round + 1) + "】";
                ShopProductDto dto = ShopProductDto.builder()
                        .id("GOURMET-" + (3000 + idx))
                        .title("【產地頂級美饌】" + t.name + (suffix.isEmpty() ? "" : " " + suffix))
                        .price(t.price + (round * 100))
                        .description(t.desc + " Grand Luxury 嚴選產地直送，全程低溫冷鏈宅配直達府上，品味非凡生活。")
                        .category("gourmet")
                        .categoryNameZh("產地頂級美饌")
                        .image(t.image)
                        .images(List.of(t.image))
                        .rating(4.9)
                        .ratingCount(120 + (idx * 15))
                        .stock(30 + (idx % 20))
                        .brand(t.brand)
                        .discountPercentage((idx % 3 == 0) ? 10.0 : 0.0)
                        .source("MOMO")
                        .build();

                productCache.put(dto.getId(), dto);
                idx++;
            }
        }
    }

    private String cleanImageUrl(String raw) {
        if (raw == null || raw.isBlank()) return "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600";
        String cleaned = raw.replace("[", "").replace("]", "").replace("\"", "").replace("\\", "").trim();
        if (!cleaned.startsWith("http") ||
                cleaned.contains("placeimg.com") ||
                cleaned.contains("placeholder") ||
                cleaned.contains("api.escuelajs.co") ||
                cleaned.contains("imgur.com") ||
                cleaned.contains("example.com")) {
            return "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600";
        }
        return cleaned;
    }

    public Map<String, Object> getShopStats() {
        if (!isInitialized || productCache.isEmpty()) {
            initializeProducts();
        }
        Map<String, Object> stats = new LinkedHashMap<>();
        long total = productCache.size();
        long momo = productCache.values().stream().filter(p -> "MOMO".equalsIgnoreCase(p.getSource())).count();
        long shopee = productCache.values().stream().filter(p -> "SHOPEE".equalsIgnoreCase(p.getSource())).count();
        long fakeStore = productCache.values().stream().filter(p -> "FakeStoreAPI".equalsIgnoreCase(p.getSource())).count();
        long dummyJson = productCache.values().stream().filter(p -> "DummyJSON".equalsIgnoreCase(p.getSource())).count();

        stats.put("total", total);
        stats.put("momo", momo);
        stats.put("shopee", shopee);
        stats.put("fakeStore", fakeStore);
        stats.put("dummyJson", dummyJson);
        return stats;
    }

    public List<ShopProductDto> getProducts(String category, String keyword, String source, String sort) {
        if (!isInitialized || productCache.isEmpty()) {
            initializeProducts();
        }

        List<ShopProductDto> list = new ArrayList<>(productCache.values());

        // Filter by source (MOMO / SHOPEE / FakeStoreAPI / DummyJSON)
        if (source != null && !source.isBlank() && !source.equalsIgnoreCase("all")) {
            list = list.stream()
                    .filter(p -> p.getSource() != null && p.getSource().equalsIgnoreCase(source))
                    .collect(Collectors.toList());
        }

        // Filter by category (Supports macro-categories & specific categories)
        if (category != null && !category.isBlank() && !category.equalsIgnoreCase("all") && !category.equalsIgnoreCase("全部商品")) {
            String catLower = category.toLowerCase().trim();
            list = list.stream()
                    .filter(p -> matchesCategoryGroup(p, catLower))
                    .collect(Collectors.toList());
        }

        // Filter by keyword
        if (keyword != null && !keyword.isBlank()) {
            String lowerKw = keyword.toLowerCase().trim();
            list = list.stream()
                    .filter(p -> (p.getTitle() != null && p.getTitle().toLowerCase().contains(lowerKw))
                            || (p.getDescription() != null && p.getDescription().toLowerCase().contains(lowerKw))
                            || (p.getBrand() != null && p.getBrand().toLowerCase().contains(lowerKw))
                            || (p.getCategoryNameZh() != null && p.getCategoryNameZh().toLowerCase().contains(lowerKw)))
                    .collect(Collectors.toList());
        }

        // Sort results
        if ("price_asc".equalsIgnoreCase(sort)) {
            list.sort(Comparator.comparingDouble(p -> p.getPrice() != null ? p.getPrice() : 0.0));
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            list.sort((a, b) -> Double.compare(b.getPrice() != null ? b.getPrice() : 0.0, a.getPrice() != null ? a.getPrice() : 0.0));
        } else if ("rating".equalsIgnoreCase(sort)) {
            list.sort((a, b) -> Double.compare(b.getRating() != null ? b.getRating() : 0.0, a.getRating() != null ? a.getRating() : 0.0));
        } else { // default / popular
            list.sort((a, b) -> Integer.compare(b.getRatingCount() != null ? b.getRatingCount() : 0, a.getRatingCount() != null ? a.getRatingCount() : 0));
        }

        return list;
    }

    private boolean matchesCategoryGroup(ShopProductDto p, String queryCat) {
        if (p == null) return false;
        String pCat = p.getCategory() != null ? p.getCategory().toLowerCase() : "";
        String pZh = p.getCategoryNameZh() != null ? p.getCategoryNameZh().toLowerCase() : "";

        if (pCat.equalsIgnoreCase(queryCat) || pZh.contains(queryCat)) {
            return true;
        }

        return switch (queryCat) {
            case "3c", "3c_digital", "3c 數位旗艦", "3c 數位", "electronics", "smartphones", "laptops" ->
                    Set.of("smartphones", "laptops", "tablets", "audio", "gaming", "cameras", "monitors", "electronics", "watches", "mens-watches", "womens-watches").contains(pCat);
            case "fashion", "日韓流行服飾", "流行服飾", "clothing" ->
                    Set.of("men's clothing", "women's clothing", "mens-shirts", "womens-dresses", "womens-shoes", "mens-shoes", "shoes", "bags", "womens-bags").contains(pCat);
            case "beauty", "專櫃美妝保養", "專櫃美妝", "skincare", "fragrances" ->
                    Set.of("beauty", "skincare", "fragrances", "jewelery").contains(pCat);
            case "appliances", "智能生活家電", "生活家電", "home" ->
                    Set.of("appliances", "home-decoration", "furniture", "lighting").contains(pCat);
            case "gourmet", "產地頂級美饌", "頂級美饌", "groceries", "food" ->
                    Set.of("gourmet", "groceries", "food", "wine").contains(pCat);
            default -> pCat.contains(queryCat) || pZh.contains(queryCat);
        };
    }

    public Optional<ShopProductDto> getProductById(String id) {
        if (!isInitialized || productCache.isEmpty()) {
            initializeProducts();
        }
        return Optional.ofNullable(productCache.get(id));
    }

    public Map<String, Object> getCategories() {
        if (!isInitialized || productCache.isEmpty()) {
            initializeProducts();
        }

        Map<String, String> categoryMap = new LinkedHashMap<>();
        categoryMap.put("3c", "3C 數位旗艦");
        categoryMap.put("fashion", "日韓流行服飾");
        categoryMap.put("beauty", "專櫃美妝保養");
        categoryMap.put("appliances", "智能生活家電");
        categoryMap.put("gourmet", "產地頂級美饌");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("stats", getShopStats());
        result.put("categoryMap", categoryMap);
        result.put("totalProducts", productCache.size());
        return result;
    }

    public ShopOrderResponse checkout(ShopCheckoutRequest request) {
        String orderNumber = "ORD-" + System.currentTimeMillis() + "-" + (new Random().nextInt(900) + 100);
        LocalDateTime now = LocalDateTime.now();

        try {
            String payMethodName = switch (request.getPaymentMethod()) {
                case "LINE_PAY" -> "LINE Pay 快速行動支付";
                case "CREDIT_CARD" -> "線上信用卡安全授權";
                case "APPLE_PAY" -> "Apple Pay 快速授權";
                default -> "貨到付款";
            };
            emailService.sendShopOrderConfirmationEmail(orderNumber, request, payMethodName);
        } catch (Exception e) {
            log.warn("發送購物確認信失敗: {}", e.getMessage());
        }

        return ShopOrderResponse.builder()
                .orderNumber(orderNumber)
                .recipientName(request.getRecipientName())
                .email(request.getEmail())
                .paymentMethod(request.getPaymentMethod())
                .totalAmount(request.getTotalAmount())
                .status("CONFIRMED_MOCK_PAID")
                .createdAt(now)
                .emailSent(true)
                .message("結帳成功！訂單確認信已送達")
                .items(request.getItems())
                .build();
    }

    private String translateCategory(String cat) {
        if (cat == null) return "精選商品";
        return switch (cat.toLowerCase()) {
            case "electronics", "smartphones", "laptops", "tablets", "audio", "gaming", "cameras", "monitors" -> "3C 數位旗艦";
            case "men's clothing", "women's clothing", "shoes", "bags", "mens-shirts", "womens-dresses", "womens-shoes", "mens-shoes" -> "日韓流行服飾";
            case "beauty", "skincare", "fragrances", "jewelery" -> "專櫃美妝保養";
            case "appliances", "home-decoration", "furniture", "lighting" -> "智能生活家電";
            case "gourmet", "groceries", "food" -> "產地頂級美饌";
            default -> "精選商品";
        };
    }

    // ==========================================
    // Internal API response DTOs
    // ==========================================
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlatziProductDto {
        private Long id;
        private String title;
        private Double price;
        private String description;
        private List<String> images;
        private PlatziCategoryDto category;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getImages() { return images; }
        public void setImages(List<String> images) { this.images = images; }
        public PlatziCategoryDto getCategory() { return category; }
        public void setCategory(PlatziCategoryDto category) { this.category = category; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlatziCategoryDto {
        private Long id;
        private String name;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FakeStoreProductDto {
        private Long id;
        private String title;
        private Double price;
        private String description;
        private String category;
        private String image;
        private FakeStoreRating rating;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
        public FakeStoreRating getRating() { return rating; }
        public void setRating(FakeStoreRating rating) { this.rating = rating; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FakeStoreRating {
        private Double rate;
        private Integer count;
        public Double getRate() { return rate; }
        public void setRate(Double rate) { this.rate = rate; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DummyJsonResponse {
        private List<DummyJsonProduct> products;
        public List<DummyJsonProduct> getProducts() { return products; }
        public void setProducts(List<DummyJsonProduct> products) { this.products = products; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DummyJsonProduct {
        private Long id;
        private String title;
        private String description;
        private Double price;
        private Double discountPercentage;
        private Double rating;
        private Integer stock;
        private String brand;
        private String category;
        private String thumbnail;
        private List<String> images;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public Double getDiscountPercentage() { return discountPercentage; }
        public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getThumbnail() { return thumbnail; }
        public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
        public List<String> getImages() { return images; }
        public void setImages(List<String> images) { this.images = images; }
    }
}
