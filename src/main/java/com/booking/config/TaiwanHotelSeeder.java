package com.booking.config;

import com.booking.modules.hotel.entity.Room;
import com.booking.modules.hotel.entity.RoomImage;
import com.booking.modules.hotel.entity.RoomStatus;
import com.booking.modules.hotel.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaiwanHotelSeeder {

    private static final Logger log = LoggerFactory.getLogger(TaiwanHotelSeeder.class);

    private final RoomRepository roomRepository;

    public TaiwanHotelSeeder(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public void seedHotelsIfEmpty() {
        if (roomRepository.count() >= 20) {
            log.info("全台飯店資料已存在，跳過初始化。目前共有 {} 間住宿資料。", roomRepository.count());
            return;
        }

        log.info("開始初始化全台灣 19 個縣市、每縣市 5 家精選飯店與特色房型資料...");
        roomRepository.deleteAll();

        List<Room> allHotels = new ArrayList<>();

        // 1. 台北市 (Taipei City)
        allHotels.add(createHotel("台北晶華酒店 (Regent Taipei)", "台北市", "海景尊爵套房", 7800.0, 4,
                "座落於台北中山商圈核心，擁有大片景觀落地窗眺望市景與公園，配備奢華大理石衛浴與專屬管家服務。",
                "市景落地窗, 大理石浴缸, 席夢思名床, 膠囊咖啡機, 頂級沐浴備品, 貴賓廊行政禮遇",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("台北萬豪酒店 (Taipei Marriott)", "台北市", "雙人經典房", 6200.0, 2,
                "位於大直美麗華商圈，客房可飽覽基隆河畔美景與美麗華摩天輪夜景，配備獨立浴缸與高速 Wi-Fi。",
                "美麗華摩天輪景觀, 獨立浴缸, 55吋智慧電視, 高速Wi-Fi, 迎賓水果, 免費自助早餐",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("台北寒舍艾美酒店 (Le Méridien Taipei)", "台北市", "頂級 Villa", 9500.0, 4,
                "信義區現代藝術殿堂級住宿，典雅光影設計與當代藝術典藏，步行直達台北 101 與信義商圈。",
                "信義區天際線, 藝術典藏客房, 頂級雨淋花灑, 迎賓香檳, 行政酒廊, Nespresso咖啡機",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("台北君悅酒店 (Grand Hyatt Taipei)", "台北市", "景觀家庭房", 7200.0, 4,
                "緊鄰台北 101，設有兩張加大雙人床，客房空間寬敞，適合家庭與商務尊爵貴賓入住。",
                "台北101景觀, 兩張雙人床, 親子友善備品, 綠洲戶外泳池, 頂級健身中心, 免費Mini Bar",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("台北圓山大飯店 (The Grand Hotel)", "台北市", "浪漫蜜月房", 5800.0, 2,
                "擁有濃厚宮殿式古典藝術建築與壯麗基隆河夜景，享受傳統文化與現代尊榮交融的經典住宿。",
                "宮殿式東方美學, 觀景陽台, 歷史密道參觀導覽, 專屬茶具組, 頂級羽絨被, 傳統精緻點心",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        // 2. 新北市 (New Taipei City)
        allHotels.add(createHotel("馥蘭朵烏來渡假酒店 (Volando Urai)", "新北市", "海景尊爵套房", 12500.0, 2,
                "隱身於烏來南勢溪畔的頂級溫泉秘境，提供私人碳酸氫鈉泉景觀湯屋與身心靈藝術展演。",
                "私人觀景溫泉湯池, 南勢溪翠綠景緻, 慢活藝術午茶, 頂級迎賓精油, 溪畔景觀露台",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("板橋凱撒大飯店 (Caesar Park Banqiao)", "新北市", "雙人經典房", 4200.0, 2,
                "位於新板特區核心，坐擁頂樓無邊際高空網美泳池與新北百萬都會璀璨夜景。",
                "高空無邊際泳池, 都會全景落地窗, 乾濕分離淋浴, 靜音冰箱, 高速Wi-Fi, 頂級沐浴組",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("淡水福容大飯店 (Fullon Hotel Tamsui)", "新北市", "景觀家庭房", 6800.0, 4,
                "外觀如郵輪般座落漁人碼頭旁，可遠眺淡水夕照與觀音山景，享受情人橋海風拂面。",
                "淡水河夕陽海景, 郵輪造型建築, 情人橋景觀, 兒童賽車館, 黃金美人湯溫泉, 親子備品",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("九份山城景觀莊園 (Jiufen Mountain B&B)", "新北市", "浪漫蜜月房", 4500.0, 2,
                "座落於九份山城最高視角，直擊陰陽海與基隆嶼海天一線美景，夜晚漫步阿妹茶樓紅燈籠街道。",
                "山海全景觀景窗, 九份夜景, 手工特調香氛, 獨立觀景浴缸, 高山蜜香紅茶, 景觀陽台",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("野柳薆悅酒店 (Inhouse Hotel Yehliu)", "新北市", "雙人經典房", 3800.0, 2,
                "鄰近野柳地質公園與海洋世界，享受北海岸絕美奇岩海岬景觀與在地現流海鮮饗宴。",
                "野柳岬海景, 400坪親子遊樂區, 海洋景觀浴缸, 北海岸導覽, 自助海鮮早餐, 停車場",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80"));

        // 3. 基隆市 (Keelung City)
        allHotels.add(createHotel("基隆長榮桂冠酒店 (Evergreen Laurel Keelung)", "基隆市", "海景尊爵套房", 5200.0, 2,
                "坐落於基隆港第一排，盡攬郵輪進出港灣的磅礡景致與基隆虎仔山地標美景。",
                "基隆港第一排海景, 觀景落地窗, 頂級浴缸, 迎賓點心, 港灣室內溫水泳池, 貴賓禮遇",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("雨都海景文旅 (Rain City Harbor Hotel)", "基隆市", "雙人經典房", 3200.0, 2,
                "鄰近基隆廟口夜市與海洋廣場，文青工業風格設計，提供旅人探索雨都港灣的溫暖落腳處。",
                "步行3分鐘到基隆廟口, 文創設計風格, 乾濕分離衛浴, 智慧連網電視, 在地手作點心",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("和平島海風行旅 (Heping Island Sea Breeze)", "基隆市", "景觀家庭房", 4600.0, 4,
                "正對和平島地質公園蔚藍大海，設有寬敞觀海大陽台，隨時聆聽海浪拍岸的療癒樂章。",
                "和平島海景, 觀景陽台, 家庭雙大床, 兒童繪本遊戲角, 潮境海洋導覽, 免費停車",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("潮境海洋景觀旅宿 (Chaojing Ocean View)", "基隆市", "浪漫蜜月房", 3900.0, 2,
                "座落潮境公園飛天掃帚與忘憂谷步道旁，推開窗即是九份山巒與八斗子漁港藍海交融絕景。",
                "忘憂谷海蝕奇景, 雙人景觀泡湯池, 潮境夜景, 手工濾掛咖啡, 免費精緻早餐",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("基隆海灣會館 (Keelung Bay Luxury Hotel)", "基隆市", "頂級 Villa", 7500.0, 6,
                "獨棟私人海景會館，配備戶外景觀觀星露台與私人廚房，盡享基隆港灣夜景與星空。",
                "獨棟海景露台, 頂級家庭劇院, 戶外BBQ烤肉設施, 私人廚房, 景觀浴缸, 專屬停車位",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        // 4. 桃園市 (Taoyuan City)
        allHotels.add(createHotel("桃園大溪笠復威斯汀度假酒店 (The Westin Tashee)", "桃園市", "頂級 Villa", 11800.0, 4,
                "享有南洋峇里島風情戶外景觀泳池與世界知名的天夢之床 (Heavenly Bed)，全台首屈一指的頂級度假勝地。",
                "南洋峇里島風戶外泳池, 天夢之床, 獨立景觀陽台, 兒童俱樂部, 頂級Spa芳療, 專屬池畔酒吧",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("和逸飯店桃園館 (COZZI Blu)", "桃園市", "雙人經典房", 4900.0, 2,
                "全台唯一海洋主題國際飯店，緊鄰 Xpark 水族館與華泰名品城 Outlet，享受海洋探險沉浸式住宿。",
                "海洋主題設計, 船艙風格酒吧, 直通Xpark水族館, 席夢思床墊, 智慧語音助理, 乾濕分離衛浴",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("名人堂花園大飯店 (Fame Hall Garden Hotel)", "桃園市", "景觀家庭房", 6500.0, 4,
                "坐落於龍潭棒球場地標旁，擁有史努比主題主題樂園與廣大生態花園，親子家庭首選。",
                "棒球名人堂景觀, 史努比主題設施, 景觀大陽台, 室內溫水泳池, 三溫暖設施, 免費接駁車",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("桃園喜來登酒店 (Sheraton Taoyuan)", "桃園市", "海景尊爵套房", 5800.0, 2,
                "鄰近桃園國際機場，擁有全台唯一的八角水晶玻璃大教堂與恆溫景觀泳池，尊爵商務與婚宴首選。",
                "水晶教堂景觀, 機場快速接駁, 行政貴賓廳, 大理石浴缸, 蒸氣三溫暖, 高速辦公專區",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("石門水庫福華渡假飯店 (Howard Lake Resort)", "桃園市", "浪漫蜜月房", 3900.0, 2,
                "環抱石門水庫青山綠水湖光山色，享受無邊際湖景景觀池與在地活魚特色料理。",
                "石門水庫湖景, 無邊際湖水景觀池, 獨立陽台, 蒸氣浴, 自助活魚饗宴, 自行車租借",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80"));

        // 5. 新竹市 / 新竹縣 (Hsinchu)
        allHotels.add(createHotel("新竹喜來登大飯店 (Sheraton Hsinchu)", "新竹市", "海景尊爵套房", 6500.0, 4,
                "座落竹北高鐵特區，擁有挑高宏偉大廳、五星級喜來登特色睡眠體驗與波浪室內恆溫泳池。",
                "高鐵特區夜景, 喜來登甜夢之床, 恆溫室內泳池, 喜波波親子樂園, 行政酒廊禮遇, 蒸氣烤箱",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("關西六福莊生態度假旅館 (Leofoo Resort)", "新竹縣", "頂級 Villa", 13500.0, 6,
                "亞洲第一座生態度假旅館，客房陽台推開即能近距離觀賞長頸鹿、白犀牛與斑馬等非洲草食動物。",
                "零距離動物生態景觀, 觀景景觀陽台, 肯亞主題房, 親子動物餵食導覽, 六福村門票優待",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("煙波大飯店新竹湖濱館 (Lakeshore Hotel)", "新竹市", "景觀家庭房", 5800.0, 4,
                "全台十大親子飯店之首，佔地2300坪卡樂次元室內親子樂園與青草湖畔優美風光。",
                "青草湖山光水色, 2300坪卡樂次元樂園, 雙加大雙人床, 溫水兒童戲水池, 頂級家庭備品",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("新竹國賓大飯店 (Ambassador Hsinchu)", "新竹市", "雙人經典房", 4300.0, 2,
                "位於新竹市中心，俯瞰風城繁華景致，配備挑高中庭景觀餐廳與尊榮商務設施。",
                "市區高空全景, 頂級乾濕分離衛浴, 高級羽絨寢具, 恆溫室內泳池, 國賓經典川粵佳餚",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("新竹老爺酒店 (Hotel Royal Hsinchu)", "新竹市", "浪漫蜜月房", 4100.0, 2,
                "鄰近新竹科學園區，結合國際日式精緻服務精神與典雅現代設計，放鬆身心的城市綠洲。",
                "日式精緻禮遇, 獨立深泡浴缸, 迎賓手作麻糬, 三溫暖設施, 免費接駁竹科",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        // 6. 苗栗縣 (Miaoli County)
        allHotels.add(createHotel("泰安觀止溫泉會館 (Onsen Papawaqa)", "苗栗縣", "海景尊爵套房", 8900.0, 2,
                "清水模與鐵木交織的頂級溫泉設計建築，坐擁汶水溪谷山嵐美景與弱鹼性碳酸氫鈉溫泉。",
                "無邊際山嵐戶外露天湯區, 汶水溪谷美景, 清水模極簡設計, 私人原木泡湯池, 頂級在地風味餐",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("樹也 Villa 頂級生態旅宿 (ChooArt Villa)", "苗栗縣", "頂級 Villa", 16800.0, 4,
                "全台最具指標性的綠建築頂級隱世 Villa，被蓊鬱原生樟樹森林所環抱，享受極致私人專屬尊榮。",
                "獨棟私人樟樹森林Villa, 專屬管家客製饗宴, 私人天然溫泉池, 270度山林落地窗, 頂級音響",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("享沐時光莊園渡假酒店 (Shine Mood Resort)", "苗栗縣", "雙人經典房", 5200.0, 2,
                "位於苑裡溫泉鄉，擁有700坪露天風呂與裸湯三溫暖，飽覽沖積扇平原與台灣海峽夕陽壯景。",
                "700坪露天溫泉風呂, 私人溫泉湯池, 苑裡平原夕陽, 頂樓光廊酒吧, 乾濕分離衛浴",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("尚順君樂飯店 (Grand Royal Hotel)", "苗栗縣", "景觀家庭房", 4600.0, 4,
                "全台最大全室內主題育樂世界旁，結合大型購物商城與威秀影城，家庭休閒最佳首選。",
                "尚順育樂世界旁, 寬敞兩大床空間, 兒童友善備品, 歐陸自助百匯早餐, 免費大型室內停車",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("馥藝金鬱金香酒店 (Golden Tulip Aesthetics)", "苗栗縣", "浪漫蜜月房", 3600.0, 2,
                "融合歐式巴洛克莊園建築與竹南萬坪運動公園綠意，優雅浪漫的英倫貴族風尚客房。",
                "巴洛克歐風外觀, 竹南萬坪公園綠景, 雙人獨立浴缸, 浪漫迎賓紅酒, 室內溫水游泳池",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        // 7. 台中市 (Taichung City)
        allHotels.add(createHotel("虹夕諾雅谷關 (Hoshinoya Guguan)", "台中市", "頂級 Villa", 21000.0, 4,
                "星野集團在台首座奢華溫泉度假村，每間客房均設有半露天無循環天然流動山泉溫泉樓中樓樓閣。",
                "樓中樓私人半露天溫泉, 谷關群山疊翠, 日式頂級會席料理, 水之庭園步道, 頂級茶道體驗",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("台中日月千禧酒店 (Millennium Taichung)", "台中市", "海景尊爵套房", 6800.0, 2,
                "位於台中七期精華地段，對望台中國家歌劇院與秋紅谷生態公園，坐享頂級都會名流風華。",
                "七期豪宅璀璨夜景, 歌劇院景觀, 大理石深泡浴缸, 席伊麗名床, 行政樓層貴賓禮遇",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("台中林酒店 (The Lin Hotel Taichung)", "台中市", "雙人經典房", 5500.0, 2,
                "拉斯維加斯奢華風尚設計，配置百萬席夢思床墊、頂級膠囊咖啡機與露天景觀溫水泳池。",
                "拉斯維加斯奢華風格, 露天溫水泳池, 百萬席夢思名床, 森林百匯自助餐, 豪華大理石衛浴",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("裕元花園酒店 (Windsor Hotel Taichung)", "台中市", "景觀家庭房", 5300.0, 4,
                "擁有台中最具規模的健康休閒俱樂部與高空景觀水療池，家庭度假休閒首選。",
                "高空景觀水療池, 雙人加大舒適雙床, 健身俱樂部, 兒童戲水天地, 喆園鮑魚中餐廳",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("台中長榮桂冠酒店 (Evergreen Laurel Taichung)", "台中市", "浪漫蜜月房", 4200.0, 2,
                "經典五星級傳承待客之道，座落台灣大道核心，提供精緻法式甜點與尊爵舒眠體驗。",
                "台灣大道經典五星地標, 獨立景觀泡澡浴缸, 手工法式巧克力禮盒, 戶外椰林泳池, 三溫暖",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        // 8. 彰化縣 (Changhua County)
        allHotels.add(createHotel("鹿港永樂酒店 (UNION HOUSE Lukang)", "彰化縣", "雙人經典房", 4800.0, 2,
                "榮獲全球奢華精品酒店 (SLH) 認證，融合鹿港百年歷史文采與現代尊貴管家服務。",
                "SLH國際奢華認證, 鹿港在地經典早餐(阿振肉包/麵線糊), 日式榻榻米客廳, 人體工學名床",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("澄悅酒店 (Euphoria Hotel Lukang)", "彰化縣", "海景尊爵套房", 4200.0, 2,
                "緊鄰鹿港老街與天后宮，頂樓設有景觀酒吧與空中花園，可俯瞰鹿港小鎮夕照。",
                "鹿港古鎮夕陽景觀, 頂樓空中花園酒吧, 獨立雙人泡澡浴缸, 膠囊咖啡機, 舒適羽絨寢具",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("彰化福泰商務飯店 (Forte Hotel Changhua)", "彰化縣", "景觀家庭房", 3800.0, 4,
                "座落彰化市核心，鄰近八卦山大佛風景區與扇形車庫，為家庭漫遊彰化首選。",
                "八卦山綠意景觀, 寬敞兩大床, 健身中心, 兒童遊戲區, 彰化在地伴手禮迎賓",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("田尾花鄉莊園渡假村 (Tianwei Garden Resort)", "彰化縣", "頂級 Villa", 6900.0, 6,
                "坐落於全台最大公路花園田尾鄉，被萬紫千紅的花海與落羽松林所環繞。",
                "私人花海莊園景觀, 落羽松森林步道, 戶外景觀烤肉露台, 莊園自行車, 芳香精油沐浴",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("彰化桂冠精品莊園 (Changhua Crown Villa)", "彰化縣", "浪漫蜜月房", 3500.0, 2,
                "以天然採光、植生牆與碳酸氫鈉水療按摩池為特色，營造綠意浪漫私密時光。",
                "專屬私密車庫, 植生牆天然造景, 雙人SPA水療按摩池, 4K高畫質影音劇院, 浪漫情境燈光",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80"));

        // 9. 南投縣 (Nantou County)
        allHotels.add(createHotel("日月潭涵碧樓酒店 (The Lalu Sun Moon Lake)", "南投縣", "頂級 Villa", 18800.0, 2,
                "國際建築大師 Kerry Hill 傳世極簡巨作，坐擁日月潭絕色山水水天一色全景與無邊際泳池。",
                "270度日月潭全景觀景陽台, 頂級極簡美學, 無邊際泳池, 專屬管家壁爐, 頂級SPA芳療",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("雲品溫泉酒店日月潭 (Fleur de Chine)", "南投縣", "海景尊爵套房", 11500.0, 4,
                "每間客房均設有天然碳酸氫鈉溫泉大理石浴池與觀景陽台，可直接在房內品味日月潭晨曦與夕陽。",
                "房內私人日月潭天然溫泉, 面湖私人大陽台, 親子水上主題樂園, 頂樓雲月舫酒吧, 丹彤自助百匯",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("清境老英格蘭莊園 (The Old England Manor)", "南投縣", "浪漫蜜月房", 13800.0, 2,
                "都鐸王朝風格古典城堡，重現英國古典貴族奢華莊園，配備法國頂級 Focal 音響與雕花壁爐。",
                "都鐸王朝古典城堡建築, 法國Focal千萬頂級音響, 古典雕花壁爐, 精緻英式三層下午茶",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("溪頭米堤大飯店 (Le Midi Hotel Chitou)", "南投縣", "景觀家庭房", 6200.0, 4,
                "隱身於溪頭千公頃竹林與巨木森林中，吸取百萬芬多精負離子，歐式宮廷宮殿式典雅度假。",
                "溪頭森林芬多精, 歐式宮廷華麗裝潢, 山泉水游泳池, 森林景觀陽台, 養生中醫足療",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("日月潭馥麗溫泉大飯店 (Fuli Hot Spring)", "南投縣", "雙人經典房", 5600.0, 2,
                "結合日式和風木質裝潢與日月潭橄欖石碳酸氫鈉泉，提供旅人深度放鬆的靜謐時光。",
                "橄欖石雙人溫泉湯池, 日月潭環湖自行車, 戶外溫水水療池, 烘焙手作體驗, 精油Spa",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        // 10. 雲林縣 (Yunlin County)
        allHotels.add(createHotel("劍湖山渡假大飯店 (Janfusun Resort Hotel)", "雲林縣", "景觀家庭房", 4800.0, 4,
                "座落於古坑劍湖山世界旁，擁有全台唯一的挑高童話中庭與豐富的親子戶外探險設施。",
                "劍湖山摩天輪景觀, 挑高童話中庭花園, 親子四人兩大床, 兒童攀爬樂園, 古坑精品咖啡體驗",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("三好國際酒店 (Sun Hao International)", "雲林縣", "雙人經典房", 3600.0, 2,
                "位於斗六棒球場旁，為雲林首屈一指的地標級星級酒店，坐擁高空無邊際景觀水池。",
                "斗六市區高空景觀, 露天水療游泳池, 頂級穗玥百匯餐廳, 乾濕分離淋浴, 免費高鐵接駁",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("北港朝聖酒店 (Saint Art Hotel)", "雲林縣", "海景尊爵套房", 3900.0, 2,
                "全台最靠近北港朝天宮的奢華文旅，結合西洋巴洛克風格與台灣傳統宮廟香火文化。",
                "朝天宮宮廟景觀, 西洋古典奢華設計, 獨立大浴缸, 北港麻油迎賓禮盒, 席夢思床墊",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("虎尾春秋布袋戲文創文旅 (Huwei Spring)", "雲林縣", "浪漫蜜月房", 3200.0, 2,
                "全台第一座以布袋戲文化為核心主題的特色設計文旅，展現傳統偶戲精湛工藝美學。",
                "布袋戲特色主題房, 頂樓景觀酒吧, 手作偶戲體驗, 復古鐵件工業風, 免費手沖古坑咖啡",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("雲林風尚人文會館 (Yunlin Style Villa)", "雲林縣", "頂級 Villa", 6500.0, 6,
                "結合在地農村恬靜風光與私人庭園烤肉休憩區，全家出遊放鬆首選。",
                "獨棟私人庭園, 戶外烤肉BBQ設施, 景觀大露台, 舒適雙客廳, 廚房設備齊全, 專屬停車位",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        // 11. 嘉義市 / 嘉義縣 (Chiayi)
        allHotels.add(createHotel("阿里山英迪格酒店 (Hotel Indigo Alishan)", "嘉義縣", "頂級 Villa", 15800.0, 2,
                "全台海拔最高的國際精品度假酒店，頂樓設有景觀無邊際溫水泳池，可躺看阿里山雲海日出與滿天繁星。",
                "阿里山雲海日出景觀, 頂樓無邊際溫水泳池, 鄒族文化編織設計, 私人觀景陽台, 頂級高山茶禮",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("阿里山賓館 (Alishan House)", "嘉義縣", "海景尊爵套房", 8500.0, 4,
                "座落阿里山森林遊樂區核心，百年全檜木古典歷史建築，享受神木群芬多精與晚霞奇景。",
                "百年檜木古色古香客房, 頂樓觀星觀日落平台, 森林神木步道直達, 暖氣空調, 養生自助餐",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("嘉義承億文旅桃城茶樣子 (Tea Way Hotel)", "嘉義市", "浪漫蜜月房", 4500.0, 2,
                "全亞洲首座以阿里山茶葉文化為主題的文創旅宿，頂樓設有高空無邊際網美泳池與酒吧。",
                "頂樓高空無邊際泳池, 阿里山茶湯浴體驗, 茶香特色客房, 頂樓酒吧, 手作泡茶器具組",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("長榮文苑酒店嘉義 (Evergreen Palace)", "嘉義縣", "景觀家庭房", 5200.0, 4,
                "正對故宮南院湖畔，客房大面觀景落地窗直眺故宮南院建築光影與至善湖美景。",
                "故宮南院第一排景觀, 露天戶外泳池, 兒童娛樂室, 兩大床家庭套房, 故宮導覽門票優惠",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("耐斯王子大飯店 (Nice Prince Hotel)", "嘉義市", "雙人經典房", 3900.0, 2,
                "嘉義市首座五星級認證國際觀光飯店，融合鄒族原住民圖騰與現代尊榮舒適寢居。",
                "阿里山鄒族圖騰藝術, 萬國百匯自助早餐, 景觀獨立浴缸, 檜木精油沐浴組, 免費接駁",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        // 12. 台南市 (Tainan City)
        allHotels.add(createHotel("台南晶英酒店 (Silks Place Tainan)", "台南市", "海景尊爵套房", 6900.0, 4,
                "座落台南中西區歷史古蹟文化核心，融合儒學書院典雅氣息與現代五星級奢華管家禮遇。",
                "府城古蹟美景, 戶外景觀恆溫泳池, 晶華名床, 膠囊咖啡機, 專屬府城小吃排隊直送服務",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("台南遠東香格里拉 (Shangri-La Far Eastern)", "台南市", "雙人經典房", 5800.0, 2,
                "台南最高地標圓柱形建築，擁有心型戶外景觀泳池與 360 度俯瞰府城全景超大景觀窗。",
                "府城360度天際線, 心型戶外泳池, 豪華閣行政禮遇, 獨立大理石浴缸, 席夢思名床",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("和逸飯店台南西門館 (COZZI Ximen Tainan)", "台南市", "景觀家庭房", 5400.0, 4,
                "全台唯一 Cartoon Network 飛天小女警、探險活寶與熊熊遇見你聯名主題親子度假天堂。",
                "卡通頻道主題房, 500坪戶外奇趣操場, 嘟嘟小火車, 兒童戲水池, 親子專屬備品禮盒",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("台南大員皇冠假日酒店 (Crowne Plaza Tainan)", "台南市", "頂級 Villa", 8800.0, 4,
                "座落於安平國家風景區旁，坐擁鹽水溪口濕地、紅樹林生態與台江國家公園夕陽絕景。",
                "台江國家公園海景, 安平夕陽景觀陽台, 恆溫室內泳池, 水療三溫暖, 元素餐廳自助餐",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("台糖長榮酒店台南 (Evergreen Plaza Tainan)", "台南市", "浪漫蜜月房", 4200.0, 2,
                "緊鄰台南文化中心與巴克禮紀念公園，榮獲多年特優星級肯定，品味府城人文深度。",
                "巴克禮公園綠景, 雙人大理石浴缸, 戶外景觀泳池, 歐式典雅裝潢, 迎賓府城古早味甜點",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        // 13. 高雄市 (Kaohsiung City)
        allHotels.add(createHotel("高雄洲際酒店 (InterContinental Kaohsiung)", "高雄市", "海景尊爵套房", 7900.0, 2,
                "以高雄港灣光影意象打造的頂級奢華旗艦，引進智慧奢華智能語音控制與 Byredo 頂級奢華香氛。",
                "高雄港灣璀璨海景, Byredo奢華沐浴備品, 小度智能管家, 奢華室內恆溫泳池, 洲際行政俱樂部",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("承億酒店 (TAI Urban Resort)", "高雄市", "頂級 Villa", 9200.0, 4,
                "坐擁全台首創全球唯一高空懸挑無邊際透明泳池，傍晚飽覽西子灣夕陽與高雄港大船入港。",
                "高空懸挑無邊際透明泳池, 西子灣夕陽美景, 頂級高空酒吧, 獨立雙人泡澡浴缸, 港都頂級海鮮餐",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("高雄萬豪酒店 (Kaohsiung Marriott)", "高雄市", "雙人經典房", 5900.0, 2,
                "座落愛河之心旁、直通義享天地時尚購物中心，擁有千坪高規格水療中心與空中禮堂。",
                "愛河之心綠意景緻, 千坪水療中心, 義享天地直通, 席夢思名床, 蒸氣三溫暖, 豪華大理石浴室",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("義大皇家酒店 (E-Da Royal Hotel)", "高雄市", "景觀家庭房", 5100.0, 4,
                "位於義大世界主題樂園與摩天輪旁，配置上千坪夢幻戶外水療戲水池與頂級親子主題房。",
                "義大摩天輪七彩夜景, 戶外水療戲水池, 兒童賽車俱樂部, 兩大床寬敞家庭空間, 義大世界門票優惠",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("高雄漢來大飯店 (Grand Hi-Lai Hotel)", "高雄市", "浪漫蜜月房", 4600.0, 2,
                "座落成功一路愛河灣地標，擁有三麗鷗 Hello Kitty 聯名主題客房與頂級漢來海港百匯。",
                "愛河灣光榮碼頭景觀, 漢來海港自助餐, 雙人大理石浴缸, 頂級羽絨舒眠組, 健身中心",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        // 14. 屏東縣 / 墾丁 (Pingtung / Kenting)
        allHotels.add(createHotel("墾丁夏都沙灘酒店 (Chateau Beach Resort)", "屏東縣", "海景尊爵套房", 8200.0, 4,
                "全台唯一擁有私人綿延2.8公里大灣白沙灘的度假酒店，踏出陽台即直接漫步蔚藍大海與浪花。",
                "私人直通白沙灘, 270度大灣海景, 觀景私人陽台, 戶外無邊際泳池, 獨木舟與帆船水上活動",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("華泰瑞苑墾丁賓館 (GLORIA MANOR)", "屏東縣", "頂級 Villa", 13800.0, 2,
                "大尖石山下的頂級隱世莊園，曾為蔣公行館，坐擁大尖石山傲岸奇景與大尖山無敵海景。",
                "大尖石山與大灣海景, 戶外景觀泳池, 沐林頂級Spa芳療, 瑞士歐舒丹頂級備品, 專屬生態導覽",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("墾丁凱撒大飯店 (Caesar Park Kenting)", "屏東縣", "雙人經典房", 5800.0, 2,
                "小灣沙灘旁的南洋風情五星級渡假村，設有私人椰林露天按摩浴池與專屬沙灘酒吧。",
                "小灣私人沙灘步道, 峇里島露天按摩浴池, 椰林景觀泳池, 浮潛探險, 凱撒百匯自助早餐",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("墾丁悠活渡假村 (Yoho Beach Resort)", "屏東縣", "景觀家庭房", 5200.0, 4,
                "位於萬里桐潮間帶旁，擁有漂漂河、瀑布水療池與兒童巧克力主題專屬客房。",
                "萬里桐潮間帶探索, 漂漂河水上滑水道, 巧克力主題親子房, 兩大床家庭空間, 日落海景沙灘",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("墾丁福華渡假飯店 (Howard Beach Kenting)", "屏東縣", "浪漫蜜月房", 4500.0, 2,
                "緊鄰小灣沙灘與墾丁大街，配置專屬鹿園生態區與蔚藍地中海風情寬敞客房。",
                "小灣沙灘秘密通道, 墾丁大街步行直達, 梅花鹿互動園區, 奧林匹克標準泳池, 景觀大陽台",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        // 15. 宜蘭縣 (Yilan County)
        allHotels.add(createHotel("礁溪老爺酒店 (Hotel Royal Chiaohsi)", "宜蘭縣", "海景尊爵套房", 10800.0, 4,
                "座落五峰旗山腰，遠眺蘭陽平原百萬夜景與龜山島，提供日本頂級溫泉旅館一泊二食極致體驗。",
                "野天風呂無邊際溫泉池, 蘭陽平原夜景, 日式榻榻米客房, 雲天自助百匯, 頂級Spa水療",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("蘭城晶英酒店 (Silks Place Yilan)", "宜蘭縣", "景觀家庭房", 9800.0, 4,
                "全台最強親子飯店！全館配置芬朵奇堡電動車賽道主題樓層與享譽國際的紅樓櫻桃烤鴨饗宴。",
                "芬朵奇堡專屬電動車主題房, 櫻桃霸王鴨預約優先, 兒童帳篷樂園, 新月豪華影城暢影, 溫水泳池",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("礁溪寒沐酒店 (MU JIAO XI HOTEL)", "宜蘭縣", "頂級 Villa", 11200.0, 4,
                "寒舍集團旗下當代藝術度假溫泉旗艦，配備每間客房獨立溫泉湯池與戶外溫泉水滑梯泳池。",
                "獨立溫泉大理石湯池, 戶外溫泉滑水道, 樂未央兒童電競賽車館, 席夢思頂級床墊, 膠囊咖啡",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("宜蘭力麗威斯汀度假酒店 (The Westin Yilan)", "宜蘭縣", "雙人經典房", 7200.0, 2,
                "座落員山溫泉秘境，以東方禪風與日式木格柵建築打造，享受威斯汀天夢之床與員山美人湯。",
                "員山天然碳酸溫泉, 天夢之床(Heavenly Bed), 日式禪風庭園, 露天風呂裸湯, 知味西餐廳",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("綠舞國際觀光飯店 (Dancewoods Resort)", "宜蘭縣", "浪漫蜜月房", 6200.0, 2,
                "全台唯一日式迴遊式庭園觀光飯店，提供日式浴衣體驗、抹茶手作與水豚/羊駝可愛動物互動。",
                "日式迴遊式庭園, 浴衣體驗, 可愛水豚互動園區, 龜山島海景陽台, 私人獨立泡湯池",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        // 16. 花蓮縣 (Hualien County)
        allHotels.add(createHotel("太魯閣晶英酒店 (Silks Place Taroko)", "花蓮縣", "海景尊爵套房", 14500.0, 2,
                "座落太魯閣國家公園立霧溪峽谷深處，頂樓設有無邊際峽谷溫水泳池，躺看鬼斧神工絕壁峭壁。",
                "太魯閣立霧溪峽谷景觀, 頂樓無邊際峽谷泳池, 露天星空電影院, 專屬峽谷步道導覽, 行政交誼廳",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("遠雄悅來大飯店 (Farglory Hotel Hualien)", "花蓮縣", "景觀家庭房", 7600.0, 4,
                "座落壽豐鄉海岸山脈頂端，東臨浩瀚太平洋無敵日出、西眺花東縱谷田園山嵐與海洋公園。",
                "太平洋無敵海景日出, 遠雄海洋公園入園接駁, 兩張豪華加大雙人床, 瑞奇活力館, 室內外泳池",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("花蓮理想大地渡假飯店 (Promisedland Resort)", "花蓮縣", "頂級 Villa", 9600.0, 4,
                "世界百大度假飯店，西班牙高第風格運河環繞，乘坐浪漫貢多拉遊艇穿梭2.2公里運河秘境。",
                "2.2公里應許之河貢多拉遊艇, 西班牙摩爾式建築, 私人露天運河陽台, 萬坪綠野騎馬射箭",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("瑞穗天合國際觀光酒店 (Grand Cosmos Ruisui)", "花蓮縣", "海景尊爵套房", 13200.0, 4,
                "享有「台版霍格華茲」美譽的南歐莊園城堡，佔地2萬坪，配置全台最大黃金溫泉水樂園。",
                "南歐金色城堡建築, 房內黃金湯溫泉浴池, 跑跑卡丁車賽道, 水樂園滑水道, 頂級芳療SPA",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("煙波大飯店花蓮太魯閣 (Lakeshore Taroko)", "花蓮縣", "浪漫蜜月房", 5800.0, 2,
                "緊鄰曼波海灘與太平洋浪花，客房全景落地窗直面蔚藍大海，頂樓無邊際海天一線無敵泳池。",
                "太平洋全景海景窗, 頂樓無邊際泳池, 漫步曼波海灘, 雙人海景大理石浴缸, 在地野菜風味早餐",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80"));

        // 17. 台東縣 (Taitung County)
        allHotels.add(createHotel("知本老爺酒店 (Hotel Royal Chihpen)", "台東縣", "海景尊爵套房", 8500.0, 4,
                "座落知本溫泉區最深處森林峽谷，享受日式天幕風呂露天溫泉與卑南族原住民文化歌舞盛宴。",
                "知本美人湯溫泉, 天幕風呂露天水療, 原住民射箭體驗, 峽谷森林景觀陽台, 露天星空泳池",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("日暉國際渡假村台東池上 (Papago Resort)", "台東縣", "頂級 Villa", 11500.0, 6,
                "座落伯朗大道與金城武樹旁，被池上萬頃金黃稻浪所環抱，擁有獨棟私人南洋泳池 Villa。",
                "獨棟私人泳池Villa, 伯朗大道金黃稻浪美景, 戶外天然溫泉風呂, 鐵馬自行車悠遊, 專屬廚房",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("台東桂田喜來登酒店 (Sheraton Taitung)", "台東縣", "雙人經典房", 4900.0, 2,
                "台東市中心唯一國際連鎖五星級地標，步行即達正氣路觀光夜市與鐵花村音樂聚落。",
                "正氣路夜市第一排, 喜來登特色睡眠床墊, 頂樓景觀酒吧, 三溫暖水療中心, 鐵花村音樂市集導覽",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("鹿鳴溫泉酒店 (Luminous Hot Spring)", "台東縣", "景觀家庭房", 5800.0, 4,
                "座落鹿野高台熱氣球嘉年華山腳下，享受鹿野天然紅葉溫泉與縱谷茶園壯闊美景。",
                "鹿野高台景緻, 房內獨享紅葉碳酸溫泉, 熱氣球升空觀賞, 兩大床家庭房, 縱谷茶香下午茶",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("娜路彎大酒店 (Naruwan Hotel Taitung)", "台東縣", "浪漫蜜月房", 4200.0, 2,
                "以斜背式原住民傳統建築造型著稱，結合戶外鹹水養生泳池與台東在地山海佳餚。",
                "原住民斜背式建築, 戶外鹹水養生泳池, 雙人泡澡大浴缸, 迎賓釋迦果乾, 免費台東機場接駁",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        // 18. 澎湖縣 (Penghu County)
        allHotels.add(createHotel("澎湖福朋喜來登酒店 (Four Points by Sheraton)", "澎湖縣", "海景尊爵套房", 7600.0, 4,
                "澎湖唯一國際五星級連鎖飯店，面對馬公第三漁港，擁有無邊際海景泳池與宜客樂海鮮百匯。",
                "馬公港無邊際海景泳池, 澎湖花火節觀景陽台, 宜客樂海鮮百匯, 港景大理石浴缸, 喜來登甜夢之床",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("澎澄飯店 (Discovery Hotel Penghu)", "澎湖縣", "雙人經典房", 5200.0, 2,
                "直通澎坊 Pier3 免稅店與極限運動攀岩館，全景大片落地窗飽覽馬公港蔚藍海面與遊艇碼頭。",
                "遊艇碼頭海景落地窗, 直通Pier3免稅店, 50米高空無邊際泳池, 極限運動攀岩場, 迎賓仙人掌果汁",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("澎湖綠洲海景旅店 (Oasis Ocean View)", "澎湖縣", "浪漫蜜月房", 4600.0, 2,
                "地中海純白城堡外觀，每間客房均面海並設有私人露台觀景浴缸，享受海風與浪漫夕陽。",
                "地中海純白城堡, 露台海景雙人浴缸, 澎湖日落美景, 迎賓海風下午茶, 澎湖跨海大橋接駁",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("澎湖元泰大飯店 (Yentai Hotel Penghu)", "澎湖縣", "景觀家庭房", 4200.0, 4,
                "全館以進口柚木家具打造溫潤南洋島嶼度假風情，配置寬敞家庭大套房與水療按摩設施。",
                "全柚木南洋度假風, 兩大床家庭房, 水療按摩池, 兒童遊樂區, 澎湖海鮮特色料理",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("和慶海景休閒渡假會館 (Heqing Harbor Villa)", "澎湖縣", "頂級 Villa", 8800.0, 6,
                "獨棟海景渡假莊園，配置私人戶外星空烤肉露台與海景廚房，盡情體驗海島度假生活。",
                "獨棟私人海景Villa, 戶外海景星空BBQ露台, 海景大客廳, 廚房設備齊全, 專屬快艇出海預約",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        // 19. 金門縣 (Kinmen County)
        allHotels.add(createHotel("金門金湖大飯店 (Golden Lake Hotel Kinmen)", "金門縣", "海景尊爵套房", 6200.0, 4,
                "金門首座五星級國際觀光飯店，緊鄰太湖風景區與亞洲最大昇恆昌免稅購物廣場。",
                "金門太湖山水全景, 昇恆昌免稅店直通, 戶外景觀溫水泳池, 頂級高粱酒特調迎賓, 大理石深泡浴缸",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("金門古崗閩南百年古厝莊園 (Gugang Heritage Villa)", "金門縣", "頂級 Villa", 7800.0, 6,
                "保存極為完整的燕尾脊與馬背型閩南傳統三合院古厝，結合現代星級獨立衛浴與古樸天井。",
                "閩南百年燕尾三合院, 傳統天井觀星露台, 現代冷暖空調, 金門道地廣東粥早餐, 旗袍古裝體驗",
                "https://images.unsplash.com/photo-1540541338287-41700207dee6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("金門昇恆昌精品會館 (Everrich Boutique Kinmen)", "金門縣", "雙人經典房", 4500.0, 2,
                "坐落於金湖水岸旁，享受奢華免稅購物與金門歷史文化巡禮之頂級商務度假體驗。",
                "太湖水岸風光, 免稅VIP貴賓通道, 席夢思名床, 乾濕分離淋浴, 精緻高粱牛肉麵套餐",
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1591088398332-8a7791972843?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("金門海福商務飯店 (Haifu Business Hotel)", "金門縣", "景觀家庭房", 3600.0, 4,
                "座落於金城鎮市中心，鄰近模範街、邱良功母節孝坊與後浦老街，漫遊美食最便捷。",
                "金城市區模範街旁, 寬敞兩大床, 乾濕分離衛浴, 智慧連網電視, 在地高粱貢糖迎賓",
                "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=900&auto=format&fit=crop&q=80"));

        allHotels.add(createHotel("金門陸島觀光渡假酒店 (Land Island Resort)", "金門縣", "浪漫蜜月房", 3300.0, 2,
                "結合閩南花崗岩石砌建築與現代渡假設計，被金門田野高粱田與綠意所環抱。",
                "花崗岩石砌特色建築, 雙人獨立浴缸, 高粱田園景色, 金門機場接送, 迎賓手工一條根茶",
                "https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=900&auto=format&fit=crop&q=80",
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=900&auto=format&fit=crop&q=80"));

        roomRepository.saveAll(allHotels);
        log.info("全台灣 19 個縣市、共 {} 間精選頂級飯店與住宿資料初始化完成！", allHotels.size());
    }

    private Room createHotel(String name, String city, String roomType, Double price, Integer capacity,
                             String description, String amenities, String img1, String img2) {
        Room room = Room.builder()
                .name(name)
                .city(city)
                .roomType(roomType)
                .pricePerNight(price)
                .capacity(capacity)
                .description(description)
                .status(RoomStatus.AVAILABLE)
                .amenities(amenities)
                .build();

        room.addImage(RoomImage.builder()
                .imageUrl(img1)
                .fileName("hotel-" + Math.abs(name.hashCode()) + "-1.jpg")
                .isPrimary(true)
                .displayOrder(0)
                .build());

        if (img2 != null) {
            room.addImage(RoomImage.builder()
                    .imageUrl(img2)
                    .fileName("hotel-" + Math.abs(name.hashCode()) + "-2.jpg")
                    .isPrimary(false)
                    .displayOrder(1)
                    .build());
        }

        return room;
    }
}
