-- 建立資料庫與切換
CREATE
DATABASE IF NOT EXISTS g05;
USE
g05;

-- 以下設定: 自增主鍵的起點值，也就是初始值，取值範圍是1 .. 655355 --
set auto_increment_offset=1;
-- 以下設定: 自增主鍵每次遞增的量，其預設值是1，取值範圍是1 .. 65535 --
set auto_increment_increment=1;

-- =========================
-- 1. 店家分類
-- =========================
DROP TABLE IF EXISTS STORE_CATEGORY;
CREATE TABLE STORE_CATEGORY
(
    STORE_CATE_ID INT AUTO_INCREMENT PRIMARY KEY,
    STORE_CATE    VARCHAR(45) NOT NULL
);

INSERT INTO STORE_CATEGORY (STORE_CATE)
VALUES ('中式料理'),
       ('日式料理'),
       ('韓式料理'),
       ('泰式料理'),
       ('義式料理'),
       ('美式料理'),
       ('甜點飲料'),
       ('素食料理');
       
-- =========================
-- 2. 店家
-- =========================
DROP TABLE IF EXISTS STORE;
CREATE TABLE STORE
(
    STOR_ID             INT AUTO_INCREMENT PRIMARY KEY,
    STORE_CATE_ID       INT          NOT NULL,
    STOR_NAME           VARCHAR(255) NOT NULL,
    STOR_DESC           VARCHAR(255) NOT NULL,
    STOR_ADDR           VARCHAR(255) NOT NULL,
    STOR_LONGITUDE      DOUBLE(10,6) NOT NULL,
    STOR_LATITUDE       DOUBLE(10,6) NOT NULL,
    STOR_PHONE          VARCHAR(20)  NOT NULL,
    STOR_WEB            VARCHAR(255),
    STOR_OPEN_TIME      TIME         NOT NULL,
    STOR_CLOSE_TIME     TIME         NOT NULL,
    STOR_WEEKLY_OFF_DAY VARCHAR(7) comment '0:星期日,1:星期一, 2:星期二,3:星期三,4:星期四,5:星期五,6:星期六',
    STOR_DELIVER        TINYINT      NOT NULL comment '1:提供,0:未提供',
    STOR_OPEN           TINYINT      NOT NULL comment '1:營業,0:未營業',
    STOR_PHOTO          LONGBLOB,
    STOR_REPORT_COUNT   TINYINT UNSIGNED DEFAULT 0,
    TOTAL_STAR_NUM      INT DEFAULT 0,
    TOTAL_REVIEWS       INT DEFAULT 0,
    STOR_STATUS 		TINYINT      NOT NULL default 1 comment '1:上架,0:未上架',
    STOR_EMAIL			VARCHAR(255) NOT NULL
);

INSERT INTO STORE (STORE_CATE_ID, STOR_NAME, STOR_DESC, STOR_ADDR, STOR_LONGITUDE, STOR_LATITUDE, STOR_PHONE, STOR_WEB,
                   STOR_OPEN_TIME, STOR_CLOSE_TIME, STOR_WEEKLY_OFF_DAY, STOR_DELIVER, STOR_OPEN, STOR_STATUS, STOR_EMAIL)
VALUES (1, '津田食堂', '招牌牛三寶、紅燒牛肉麵熱銷中', '台中市西區公益路100號', 120.664321, 24.147123, '04-12345678',NULL,
 	   '11:00:00', '21:00:00', '1', 1, 1, 1, 'contact@beefnoodle.com'),
       (1, '阿華餛飩專賣', '手工現包餛飩與家常乾麵，老字號人氣', '台中市北區雙十路二段58號', 120.684210, 24.157421, '04-23456789', NULL,
        '10:30:00', '20:00:00', '6', 1, 1, 1, 'bbb@gmail.com'),
       (1, '幸福私廚', '酸辣湯、滷肉飯、三杯雞皆為自家料理', '新北市板橋區文化路1段200號', 121.462, 25.014, '02-34567890', NULL,
        '18:00:00', '02:00:00', '1', 0, 1, 1, 'ccc@gmail.com'),
       (1, '家鄉味便當', '鹽酥雞、滷味拼盤、蚵仔煎，傳統台味首選', '台中市西區公益路300號', 120.666, 24.146, '04-1234567', NULL,
        '12:00:00', '20:00:00', '2', 1, 1, 1, 'ddd@gmail.com'),
       (2, '築地壽司屋', '選用每日直送海鮮，手握壽司道地正宗', '台南市中西區民生路2段50號', 120.193, 22.997, '06-2345678', NULL,
        '10:00:00', '22:00:00', '3', 1, 1, 1, 'eee@gmail.com'),
       (2, '花見拉麵', '濃郁豚骨湯底搭配手工拉麵，道地九州風味', '高雄市左營區自由路88號', 120.306, 22.683, '07-3456789', NULL, '11:00:00',
        '21:00:00', '1', 1, 1, 1, 'fff@gmail.com'),
       (2, '和風定食屋', '提供多樣日式定食，兼顧營養與美味', '新竹市東區食品路88號', 121.021, 24.800, '03-5566778', NULL, '10:30:00',
        '23:00:00', '0', 1, 1, 1, 'ggg@gmail.com'),
       (2, '月之食堂', '京都風格小館，供應茶泡飯與家常菜', '桃園市中壢區中正路100號', 121.224, 24.957, '03-1234567', NULL, '11:30:00',
        '22:00:00', '7', 1, 1, 1, 'hhh@gmail.com'),
       (3, '弘大燒肉', '正宗韓式炭火燒肉，肉質鮮嫩多汁', '台北市信義區松仁路20號', 121.567, 25.034, '02-87654321', NULL,
        '17:00:00', '01:00:00', '1', 0, 1, 1, 'iii@gmail.com'),
       (3, '首爾部隊鍋', '濃郁湯頭搭配多樣配料，韓國街頭風味再現', '台中市北區學士路10號', 120.684, 24.157, '04-2345678', NULL, '11:00:00',
        '23:00:00', '2', 1, 1, 1, 'jjj@gmail.com'),
       (3, '阿珠媽韓食堂', '家常韓式定食與泡菜煎餅，阿珠媽的味道', '高雄市左營區自由三路168號',120.299876, 22.678901, '07-1234567', NULL,
  	   '10:30:00', '20:30:00', '0', 0, 1, 1, 'ajumma@kfood.tw'),
	   (4, '泰泰小館', '道地泰國家常風味，主打打拋豬與綠咖哩', '台北市中山區林森北路300號',121.525678, 25.058976, '02-25677889', 'https://thaithaikitchen.tw',
	   '11:00:00', '21:30:00', '2', 1, 1, 1, 'thaithai@kitchen.tw'),
	   (4, '曼谷風味館', '精緻裝潢與經典泰式料理，適合聚餐與慶生', '台中市南屯區公益路二段120號',120.642210, 24.149012, '04-22557788', 'https://bangkok-style.com',
	   '12:00:00', '22:00:00', '0', 0, 1, 1, 'contact@bangkok-style.com'),
	   (4, '小象泰食', '平價泰式料理專賣，月亮蝦餅、奶茶超人氣', '高雄市苓雅區三多三路100號',120.313567, 22.620978, '07-3388123', NULL,
	   '10:30:00', '20:00:00', NULL, 1, 1, 1, 'hello@thaielephant.tw'),
	   (5, '帕里歐義廚', '主打手工義大利麵與經典焗烤，來自羅馬的味道', '台北市大安區敦化南路一段230號',121.543876, 25.034900, '02-27008877', 'https://palio-italian.com',
	   '11:30:00', '21:30:00', '1', 1, 1, 1, 'info@palio-italian.com'),
	   (5, '貝拉披薩坊', '義大利傳統窯烤披薩，使用新鮮莫札瑞拉起司', '台南市永康區中正南路88號',120.226543, 23.006789, '06-3124567', NULL,
	   '12:00:00', '22:00:00', NULL, 0, 1, 1, 'bella@pizzaria.com'),
	   (5, '拉斐塔義廚坊', '義式鄉村風餐館，主打松露燉飯與手工千層麵', '台北市士林區天母東路168號',121.530987, 25.120123, '02-28765544', 'https://lafetta-italian.com',
	   '11:30:00', '21:30:00', '1', 1, 1, 1, 'info@lafetta-italian.com'),
	   (6, '老爹漢堡屋', '炭烤漢堡搭配起司與培根，美式經典再現', '台北市信義區松仁路88號',121.564321, 25.035678, '02-27278899', 'https://daddysburgerhouse.com',
	   '11:00:00', '21:00:00', '0', 1, 1, 1, 'contact@daddysburgerhouse.com'),
	   (6, '德州烤肉Bar', '原汁原味美國南方風格，肋排與BBQ最受歡迎', '台中市南屯區黎明路二段150號',120.642111, 24.155432, '04-23568899', NULL,
	   '17:00:00', '23:00:00', '1', 0, 1, 1, 'bbq@texasbar.com'),
	   (6, '漢堡共和國', '多國風味美式漢堡連鎖店，附設兒童餐與奶昔', '高雄市鼓山區美術館路168號',120.286754, 22.654321, '07-3337777', 'https://burgerrepublic.tw',
	   '10:30:00', '22:00:00', NULL, 1, 1, 1, 'info@burgerrepublic.tw'),
	   (7, '糖心手作甜點', '專賣千層蛋糕與法式甜點，使用天然食材製作', '台北市中山區南京東路三段88號',121.538765, 25.052314, '02-25112233', 'https://sugarheartdessert.com',
	   '12:00:00', '21:00:00', '1', 0, 1, 1, 'hello@sugarheartdessert.com'),
	   (7, '微甜飲品屋', '主打低糖健康系飲料，珍珠奶茶與抹茶拿鐵超人氣', '台中市西區健行路210號',120.664321, 24.147123, '04-22223333', NULL,
	   '10:30:00', '22:00:00', '2', 1, 1, 1, 'contact@sweetdrinkbar.tw'),
	   (7, '甜在心烘焙坊', '現烤可麗露與肉桂捲，下午茶首選', '高雄市新興區中正三路99號',120.300001, 22.630000, '07-22119988', 'https://sweetinheart.com',
	   '11:00:00', '20:30:00', NULL, 1, 1, 1, 'service@sweetinheart.com'),
	   (8, '清心蔬食坊', '提供天然食材製作的台式素食，主打麻油猴菇飯', '台北市松山區民生東路五段12號',121.560111, 25.057900, '02-27453322', 'https://chingxinveggie.com',
	   '10:30:00', '20:00:00', '0', 1, 1, 1, 'info@chingxinveggie.com'),
	   (8, '綠芽有機廚房', '主打無蛋無奶純植物飲食，選用在地有機蔬果', '台中市北屯區昌平路一段76號',120.680432, 24.172600, '04-22336677', NULL,
	   '11:00:00', '21:00:00', NULL, 1, 1, 1, 'green@organicveggie.tw'),
	   (8, '覺味蔬食料理', '中西融合創意素食，搭配季節性食材變化菜單', '高雄市三民區建國三路58號',120.303456, 22.639876, '07-23881122', 'https://juewei-veggie.tw',
	   '11:30:00', '20:30:00', '3', 0, 1, 1, 'service@juewei-veggie.tw');
-- =========================
-- 3. 會員
-- =========================
DROP TABLE IF EXISTS MEMBER;
CREATE TABLE MEMBER (
    -- member table
                        MEM_ID	INT auto_increment NOT NULL,
                        MEM_EMAIL	  VARCHAR(45) NOT NULL,
                        MEM_ACCOUNT VARCHAR(45) NOT NULL,
                        MEM_PASSWORD varchar(45) NOT NULL,
                        MEM_NICKNAME varchar(45) NOT NULL,
                        MEM_NAME varchar(45) NOT NULL,
                        MEM_PHONE varchar(10) NOT NULL,
                        MEM_GENDER tinyint NOT NULL comment '0:男,1:女,2:不透露',
                        MEM_CITY varchar(45) NOT NULL,
                        MEM_CITYAREA varchar(45) NOT NULL,
                        MEM_ADDRESS varchar(45) NOT NULL,
                        MEM_CODE varchar(45) NOT NULL,
                        MEM_AVATAR longblob,
                        MEM_TIME datetime NOT NULL,
                        MEM_STATUS tinyint NOT NULL default 1 comment '0:停權,1:正常使用',
                        MEM_NO_SPEAK tinyint NOT NULL default 1 comment '0:禁言,1:解除禁言',
                        MEM_NO_POST tinyint NOT NULL default 1 comment '0:禁止發文,1:解除禁止發文',
                        MEM_NO_GROUP tinyint NOT NULL default 1 comment '0:禁止開團,1:解除禁止開團',
                        MEM_NO_JOINGROUP tinyint NOT NULL default 1 comment '0:禁止跟團,1:解除禁止跟團',
                        TOTAL_STAR_NUM INT NOT NULL,
                        TOTAL_REVIEWS INT NOT NULL,
                        CONSTRAINT MEMBER_MEM_ID_PK PRIMARY KEY (MEM_ID),  -- 會員編號為PK
                        constraint MEMBER_MEM_ACCOUNT_UK UNIQUE (MEM_ACCOUNT)    -- 會員帳號為UK
) ;

INSERT INTO MEMBER (MEM_EMAIL, MEM_ACCOUNT, MEM_PASSWORD, MEM_NICKNAME, MEM_NAME, MEM_PHONE, MEM_GENDER, MEM_CITY,
                    MEM_CITYAREA, MEM_ADDRESS, MEM_CODE, MEM_AVATAR, MEM_TIME, MEM_STATUS, MEM_NO_SPEAK, MEM_NO_POST,
                    MEM_NO_GROUP, MEM_NO_JOINGROUP, TOTAL_STAR_NUM, TOTAL_REVIEWS)
VALUES ('user1@gmail.com', 'user1', 'pass1', '小明', '王小明', '0912345678', 0, '台北市', '大安區', '和平東路1段1號',
        'A001', NULL, '2023-01-01', 1, 0, 0, 0, 0, 25, 10),
       ('user2@gmail.com', 'user2', 'pass2', '小美', '林小美', '0923456789', 1, '新北市', '板橋區', '文化路1段200號',
        'A002', NULL, '2023-01-02', 1, 0, 0, 0, 0, 30, 12),
       ('user3@gmail.com', 'user3', 'pass3', '阿豪', '陳志豪', '0934567890', 0, '台中市', '西區', '公益路300號', 'A003',
        NULL, '2023-01-03', 1, 0, 0, 0, 0, 20, 8),
       ('user4@gmail.com', 'user4', 'pass4', '小安', '張安安', '0945678901', 1, '台南市', '中西區', '民生路2段50號',
        'A004', NULL, '2023-01-04', 1, 0, 0, 0, 0, 15, 7),
       ('user5@gmail.com', 'user5', 'pass5', '阿杰', '李杰', '0956789012', 0, '高雄市', '左營區', '自由路88號', 'A005',
        NULL, '2023-01-05', 1, 0, 0, 0, 0, 10, 5),
       ('user6@gmail.com', 'user6', 'pass6', '小芳', '吳小芳', '0967890123', 1, '新竹市', '東區', '食品路88號', 'A006',
        NULL, '2023-01-06', 1, 0, 0, 0, 0, 12, 6),
       ('user7@gmail.com', 'user7', 'pass7', '小宇', '周宇', '0978901234', 0, '桃園市', '中壢區', '中正路100號', 'A007',
        NULL, '2023-01-07', 1, 0, 0, 0, 0, 18, 9),
       ('user8@gmail.com', 'user8', 'pass8', '阿敏', '曾敏', '0989012345', 1, '台北市', '信義區', '松仁路20號', 'A008',
        NULL, '2023-01-08', 1, 0, 0, 0, 0, 22, 11),
       ('user9@gmail.com', 'user9', 'pass9', '小志', '黃志明', '0990123456', 0, '台中市', '北區', '學士路10號', 'A009',
        NULL, '2023-01-09', 1, 0, 0, 0, 0, 16, 8),
       ('user10@gmail.com', 'user10', 'pass10', '小惠', '蔡惠', '0901234567', 1, '宜蘭縣', '羅東鎮', '中山路25號',
        'A010', NULL, '2023-01-10', 1, 0, 0, 0, 0, 14, 7);

-- =========================
-- 4. 一般商品類別
-- =========================
DROP TABLE IF EXISTS PRODUCT_CATEGORY;
CREATE TABLE PRODUCT_CATEGORY
(
    PROD_CATE_ID INT AUTO_INCREMENT PRIMARY KEY,
    PROD_CATE    VARCHAR(45) NOT NULL
);

INSERT INTO PRODUCT_CATEGORY (PROD_CATE)
VALUES ('中式料理'),
       ('日式料理'),
       ('韓式料理'),
       ('泰式料理'),
       ('義式料理'),
       ('美式料理'),
       ('甜點飲料'),
       ('素食料理');

-- =========================
-- 5. 一般商品
-- =========================
DROP TABLE IF EXISTS PRODUCT;
CREATE TABLE PRODUCT
(
    PROD_ID           INT AUTO_INCREMENT PRIMARY KEY,
    STOR_ID           INT         NOT NULL,
    PROD_CATE_ID      INT         NOT NULL,
    PROD_NAME         VARCHAR(45) NOT NULL,
    PROD_DESC         VARCHAR(45) NOT NULL,
    PROD_PRICE        INT         NOT NULL,
    PROD_UPDATETIME   DATETIME    NOT NULL,
    PROD_STATUS       TINYINT     NOT NULL,
    PROD_PHOTO        LONGBLOB,
    PROD_LAST_UPDATE  DATETIME    NOT NULL,
    PROD_REPORT_COUNT INT DEFAULT 0
);

INSERT INTO PRODUCT (STOR_ID, PROD_CATE_ID, PROD_NAME, PROD_DESC, PROD_PRICE, PROD_UPDATETIME, PROD_STATUS, PROD_PHOTO,
                     PROD_LAST_UPDATE)
VALUES 
		-- 中式料理
	   (1, 1, '招牌牛三寶麵', '自家燉牛三寶', 120, '2025-05-01', 1, NULL, '2025-05-18'),
       (1, 1, '紅燒牛肉麵', '經典好燒湯頭', 80, '2025-05-02', 1, NULL, '2025-05-18'),
       (1, 1, '酸辣湯麵', '酸辣湯底料多實在', 70, '2025-05-03', 1, NULL, '2025-05-18'),
       (1, 1, '餛飩牛湯麵', '手工自家餛飩搭配牛湯', 75, '2025-05-04', 1, NULL, '2025-05-18'),
       (2, 1, '餛飩麵', '手工自家餛飩(沒有牛的成分)', 75, '2025-05-05', 1, NULL, '2025-05-18'),
       (2, 1, '擔擔麵', '微辣乾麵', 50, '2025-05-06', 1, NULL, '2025-05-18'),
       (2, 1, '牛湯餃', '手工自家湯餃搭配牛湯', 70, '2025-05-07', 1, NULL, '2025-05-18'),
       (3, 1, '酸辣湯餃', '手工自家湯餃搭配酸辣湯', 70, '2025-05-08', 1, NULL, '2025-05-18'),
       (3, 1, '紅油炒手', '手工自家炒手，小辣', 75, '2025-05-09', 1, NULL, '2025-05-18'),
       (3, 1, '三杯雞套餐', '九層塔香氣四溢，搭配熱白飯最對味', 110, '2025-05-09', 1, NULL, '2025-05-18'),
       (3, 1, '滷肉飯', '經典台式風味，肥瘦相間的滷肉香氣迷人', 45, '2025-05-09', 1, NULL, '2025-05-18'),
       (4, 1, '鹽酥雞便當', '外酥內嫩的雞肉搭配爽口小菜', 115, '2025-05-09', 1, NULL, '2025-05-18'),
       (4, 1, '滷味拼盤', '多種滷製小點一次滿足', 99, '2025-05-09', 1, NULL, '2025-05-18'),
       (4, 1, '蚵仔煎', '外酥內嫩，佐以甜辣醬，美味無敵', 75, '2025-05-09', 1, NULL, '2025-05-18'),
       -- 日式料理
       (5, 2, '黑鮪魚刺身盛合', '屏東東港直送', 300, '2025-05-10', 1, NULL, '2025-05-18'),
       (5, 2, '鮭魚肚握壽司', '新鮮厚切鮭魚肚', 250, '2025-05-10', 1, NULL, '2025-05-18'),
       (5, 2, '稻荷豆皮壽司', '經典壽司', 80, '2025-05-10', 1, NULL, '2025-05-18'),
       (6, 2, '日式炒烏龍', '使用日本讚岐烏龍', 120, '2025-05-10', 1, NULL, '2025-05-18'),
       (6, 2, '炸厚切豬排', '現炸豬徘﹑厚度2公分', 150, '2025-05-10', 1, NULL, '2025-05-18'),
       (6, 2, '可樂餅', '起司口味', 50, '2025-05-10', 1, NULL, '2025-05-18'),
       (7, 2, '烤牛肉串', '嚴選神戶牛', 100, '2025-05-10', 1, NULL, '2025-05-18'),
       (7, 2, '烤秋刀魚', '秋天限定', 150, '2025-05-10', 1, NULL, '2025-05-18'),
       (7, 2, '壽司綜合拼盤', '新鮮現做，海味十足', 250, '2025-05-10', 1, NULL, '2025-05-18'),
       (7, 2, '照燒雞腿丼', '醬香四溢的雞腿排配上Q彈白飯', 180, '2025-05-10', 1, NULL, '2025-05-18'),
       (8, 2, '味噌拉麵', '濃郁味噌湯底搭配叉燒與溏心蛋', 150, '2025-05-10', 1, NULL, '2025-05-18'),
       (8, 2, '炸蝦天婦羅', '外酥內嫩，搭配特製沾醬', 150, '2025-05-10', 1, NULL, '2025-05-18'),
       (8, 2, '日式咖哩豬排飯', '厚切豬排酥脆不油膩，佐以濃郁咖哩', 180, '2025-05-10', 1, NULL, '2025-05-18'),
       (8, 2, '生魚片定食', '選用當日鮮魚，搭配清爽配菜', 220, '2025-05-10', 1, NULL, '2025-05-18'),
       -- 韓式料理
	   (9, 3, '泡菜鍋', '辣味十足，韓式風味', 200, '2024-06-15 18:45:00', 1, NULL, '2024-06-15 18:45:00'),
	   (9, 3, '韓式炸雞', '外酥內嫩', 180, '2024-06-10 13:15:00', 1, NULL, '2024-06-10 13:15:00'),
	   (9, 3, '部隊鍋', '濃郁泡菜湯底，豐富配料滿足味蕾', 280, '2024-06-10 13:15:00', 1, NULL, '2024-06-10 13:15:00'),
	   (10, 3, '石鍋拌飯', '辣醬拌炒，鍋巴香脆可口', 200, '2024-06-10 13:15:00', 1, NULL, '2024-06-10 13:15:00'),
	   (10, 3, '洋釀炸雞', '洋釀口味，外酥內嫩', 220, '2024-06-10 13:15:00', 1, NULL, '2024-06-10 13:15:00'),
	   (10, 3, '辣炒年糕', '濃濃韓國街頭小吃風味', 180, '2024-06-10 13:15:00', 1, NULL, '2024-06-10 13:15:00'),
	   (11, 3, '泡菜煎餅', '香脆可口，泡菜味濃郁', 210, '2024-06-10 13:15:00', 1, NULL, '2024-06-10 13:15:00'),
	   (11, 3, '蔘雞湯', '濃厚雞湯與糯米暖心滋補', 300, '2024-06-10 13:15:00', 1, NULL, '2024-06-10 13:15:00'),
	   -- 泰式料理
	   (12, 4, '泰式打拋豬飯', '辣香十足的泰式經典便當', 160, '2024-06-13 17:30:00', 1, NULL, '2024-06-13 17:30:00'),
	   (12, 4, '泰式綠咖哩雞', '泰式綠咖哩雞', 160, '2024-06-13 17:30:00', 1, NULL, '2024-06-13 17:30:00'),
	   (12, 4, '月亮蝦餅', '外皮酥脆，蝦肉扎實', 160, '2024-06-13 17:30:00', 1, NULL, '2024-06-13 17:30:00'),
       (13, 4, '涼拌青木瓜絲', '清爽開胃，酸辣剛好', 170, '2024-06-17 12:20:00', 1, NULL, '2024-06-17 12:20:00'),
       (13, 4, '酸辣海鮮湯', '湯頭香辣帶勁，鮮味十足', 170, '2024-06-17 12:20:00', 1, NULL, '2024-06-17 12:20:00'),
       (13, 4, '泰式奶茶', '香甜濃郁，帶點茶香', 170, '2024-06-17 12:20:00', 1, NULL, '2024-06-17 12:20:00'),
       (14, 4, '泰式紅咖哩雞', '微辣', 180, '2024-06-17 12:20:00', 1, NULL, '2024-06-17 12:20:00'),
       (14, 4, '泰式奶茶', '泰國煉乳正統奶茶', 100, '2024-06-17 12:20:00', 1, NULL, '2024-06-17 12:20:00'),
       (14, 4, '摩摩喳喳', '甜而不膩', 150, '2024-06-17 12:20:00', 1, NULL, '2024-06-17 12:20:00'),
       -- 義式料理
	   (15, 5, '經典瑪格麗特披薩', '番茄醬+莫札瑞拉', 250, '2024-06-14 19:00:00', 1, NULL, '2024-06-14 19:00:00'),
	   (15, 5, '奶油蘑菇義大利麵', '香濃奶油醬', 190, '2024-06-12 20:00:00', 1, NULL, '2024-06-12 20:00:00'),
	   (15, 5, '經典肉醬義大利麵', '番茄肉醬慢熬濃郁', 180, '2024-06-12 20:00:00', 1, NULL, '2024-06-12 20:00:00'),
	   (16, 5, '青醬雞肉義大利麵', '濃郁九層塔香氣撲鼻', 210, '2024-06-12 20:00:00', 1, NULL, '2024-06-12 20:00:00'),
	   (16, 5, '奶油培根義大利麵', '濃郁奶香與鹹香培根完美融合', 200, '2024-06-12 20:00:00', 1, NULL, '2024-06-12 20:00:00'),
	   (16, 5, '焗烤海鮮千層麵', '滿滿海味與起司的絕配', 220, '2024-06-12 20:00:00', 1, NULL, '2024-06-12 20:00:00'),
	   (17, 5, '義式燉牛肋', '紅酒慢燉，入口即化', 220, '2024-06-12 20:00:00', 1, NULL, '2024-06-12 20:00:00'),
	   (17, 5, '奶油燻雞義大利麵', '香濃奶油醬', 190, '2024-06-12 20:00:00', 1, NULL, '2024-06-12 20:00:00'),
	   -- 美式料理
	   (18, 6, '牛肉起司漢堡', '手打牛肉配濃郁起司', 250, '2024-06-18 18:00:00', 1, NULL, '2024-06-18 18:00:00'),
	   (18, 6, '培根起司漢堡', '培根與濃郁起司完美融合', 230, '2024-06-15 20:00:00', 1, NULL, '2024-06-15 20:00:00'),
	   (18, 6, '美式炸雞餐', '外酥內嫩，香氣逼人', 230, '2024-06-15 20:00:00', 1, NULL, '2024-06-15 20:00:00'),
	   (19, 6, 'BBQ烤豬肋排', '甜鹹醬汁包覆厚實肋排', 180, '2024-06-15 20:00:00', 1, NULL, '2024-06-15 20:00:00'),
	   (19, 6, '經典薯條', '酥脆爽口，百搭配餐', 130, '2024-06-15 20:00:00', 1, NULL, '2024-06-15 20:00:00'),
	   (19, 6, '墨西哥辣雞捲', '香辣雞肉與蔬菜捲餅組合', 150, '2024-06-15 20:00:00', 1, NULL, '2024-06-15 20:00:00'),
	   (20, 6, '凱薩沙拉', '清爽沙拉佐以濃郁醬汁', 160, '2024-06-15 20:00:00', 1, NULL, '2024-06-15 20:00:00'),
	   (20, 6, '燻雞起司漢堡', '燻雞與濃郁起司完美融合', 230, '2024-06-15 20:00:00', 1, NULL, '2024-06-15 20:00:00'),
	   -- 甜點飲料
	   (21, 7, '珍珠奶茶', '經典台灣飲品', 75, '2024-06-19 09:00:00', 1, NULL, '2024-06-19 09:00:00'),
	   (21, 7, '焦糖奶茶', '焦糖風味', 70, '2024-06-17 15:00:00', 1, NULL, '2024-06-17 15:00:00'),
	   (22, 7, '草莓千層蛋糕', '酸甜草莓與滑順鮮奶油完美層疊', 180, '2024-06-17 15:00:00', 1, NULL, '2024-06-17 15:00:00'),
	   (22, 7, '焦糖布丁', '濃郁蛋香與焦糖完美融合', 80, '2024-06-17 15:00:00', 1, NULL, '2024-06-17 15:00:00'),
	   (22, 7, '芒果冰沙', '夏日清爽消暑首選', 100, '2024-06-17 15:00:00', 1, NULL, '2024-06-17 15:00:00'),
	   (23, 7, '抹茶拿鐵', '香濃抹茶與鮮奶完美比例', 120, '2024-06-17 15:00:00', 1, NULL, '2024-06-17 15:00:00'),
	   (23, 7, '可麗露', '外脆內嫩的法式經典甜點', 50, '2024-06-17 15:00:00', 1, NULL, '2024-06-17 15:00:00'),
	   -- 素食料理
	   (24, 8, '蔬食咖哩飯', '健康蔬菜多多', 110, '2024-06-16 13:00:00', 1, NULL, '2024-06-16 13:00:00'),
	   (24, 8, '涼拌豆皮', '低脂高蛋白', 80, '2024-06-12 11:00:00', 1, NULL, '2024-06-12 11:00:00'),
	   (24, 8, '香菇素燴飯', '多種蔬菜與香菇慢燉入味', 80, '2024-06-12 11:00:00', 1, NULL, '2024-06-12 11:00:00'),
	   (25, 8, '紅燒豆腐', '香氣撲鼻，豆腐入口即化', 80, '2024-06-12 11:00:00', 1, NULL, '2024-06-12 11:00:00'),
	   (25, 8, '素炒米粉', '淡雅醬香，清爽不油膩', 80, '2024-06-12 11:00:00', 1, NULL, '2024-06-12 11:00:00'),
	   (25, 8, '田園蔬菜湯', '自然清甜的湯頭，健康首選', 80, '2024-06-12 11:00:00', 1, NULL, '2024-06-12 11:00:00'),
	   (26, 8, '麻油猴菇飯', '麻油香與猴頭菇完美搭配', 80, '2024-06-12 11:00:00', 1, NULL, '2024-06-12 11:00:00'),
	   (26, 8, '蓮子百合甜湯', '養生滋補的中式甜品', 80, '2024-06-12 11:00:00', 1, NULL, '2024-06-12 11:00:00');
-- =========================
-- 6. 收藏商品清單
-- =========================
DROP TABLE IF EXISTS FAVORITE_LIST;
CREATE TABLE FAVORITE_LIST
(
    MEM_ID  INT NOT NULL,
    PROD_ID INT NOT NULL,
    PRIMARY KEY (MEM_ID, PROD_ID)
);

INSERT INTO FAVORITE_LIST (MEM_ID, PROD_ID)
VALUES (1, 1),
       (1, 2),
       (2, 3),
       (2, 4),
       (3, 5),
       (4, 6),
       (5, 7),
       (6, 8),
       (7, 9),
       (8, 10);

-- =========================
-- 7. 購物車商品
-- =========================
DROP TABLE IF EXISTS SHOPPING_CART;
CREATE TABLE SHOPPING_CART
(
    SHOP_ID INT AUTO_INCREMENT PRIMARY KEY,
    MEM_ID  INT NOT NULL,
    PROD_ID INT NOT NULL,
    PROD_N  INT
);

INSERT INTO SHOPPING_CART (MEM_ID, PROD_ID, PROD_N)
VALUES (1, 1, 3),
       (1, 3, 9),
       (2, 2, 1),
       (2, 4, 1),
       (3, 5, 2),
       (4, 6, 3),
       (5, 7, 2),
       (6, 8, 1),
       (7, 9, 2),
       (8, 10, 2);

-- =========================
-- 8. 會員持有優惠券
-- =========================
DROP TABLE IF EXISTS MEMBER_COUPON;
CREATE TABLE MEMBER_COUPON
(
    MEM_COU_ID INT AUTO_INCREMENT PRIMARY KEY,
    COU_ID     INT     NOT NULL,
    MEM_ID     INT     NOT NULL,
    USE_STATUS TINYINT NOT NULL
);

INSERT INTO MEMBER_COUPON (COU_ID, MEM_ID, USE_STATUS)
VALUES (1, 1, 0),
       (2, 2, 0),
       (3, 3, 1),
       (4, 4, 0),
       (5, 5, 1),
       (6, 6, 0),
       (7, 7, 0),
       (8, 8, 1),
       (9, 9, 0),
       (10, 10, 1);

-- =========================
-- 9. 訂單
-- =========================
DROP TABLE IF EXISTS ORDERS;
CREATE TABLE ORDERS
(
    ORD_ID          INT AUTO_INCREMENT PRIMARY KEY,
    MEM_ID          INT      NOT NULL,
    STOR_ID         INT      NOT NULL,
    ORD_DATE        DATETIME NOT NULL,
    ORD_SUM         INT      NOT NULL,
    PAYMENT_STATUS  TINYINT  NOT NULL,
    PAY_METHOD      TINYINT  NOT NULL,
    DELIVER         TINYINT  NOT NULL,
    ORDER_STATUS    TINYINT  NOT NULL,
    ACT_ID          INT,
    COU_ID          INT,
    CANCEL_REASON   VARCHAR(255),
    COMMENT         VARCHAR(255),
    RATING          TINYINT UNSIGNED,
    PROMO_DISCOUNT  INT,
    COUPON_DISCOUNT INT,
    ACTUAL_PAYMENT  INT,
    ORD_ADDR        VARCHAR(255)
);

INSERT INTO ORDERS (MEM_ID, STOR_ID, ORD_DATE, ORD_SUM, PAYMENT_STATUS, PAY_METHOD, DELIVER, ORDER_STATUS, ACT_ID,
                    COU_ID, CANCEL_REASON, COMMENT, RATING, PROMO_DISCOUNT, COUPON_DISCOUNT, ACTUAL_PAYMENT, ORD_ADDR)
VALUES (1, 1, '2025-05-18 12:00:00', 120, 1, 0, 1, 2, NULL, 1, NULL, '好吃', 5, 10, 20, 90,
        '台北市大安區和平東路1段1號'),
       (2, 2, '2025-05-18 13:00:00', 100, 1, 1, 0, 2, NULL, 2, NULL, '便宜又好吃', 4, 0, 10, 90,
        '新北市板橋區文化路1段200號'),
       (3, 3, '2025-05-18 14:00:00', 180, 1, 2, 1, 2, NULL, 3, NULL, '湯頭濃郁', 5, 20, 0, 160,
        '台中市西區公益路300號'),
       (4, 4, '2025-05-18 15:00:00', 120, 0, 0, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
       (5, 5, '2025-05-18 16:00:00', 70, 1, 1, 0, 2, NULL, 1, NULL, '冰沙很棒', 5, 10, 0, 60,
        '台南市中西區民生路2段50號'),
       (6, 6, '2025-05-18 17:00:00', 150, 1, 2, 1, 2, NULL, 2, NULL, '健康美味', 4, 0, 10, 140,
        '高雄市左營區自由路88號'),
       (7, 7, '2025-05-18 18:00:00', 90, 0, 0, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
       (8, 8, '2025-05-18 19:00:00', 200, 1, 1, 0, 2, NULL, 3, NULL, '披薩好吃', 5, 20, 0, 180,
        '桃園市中壢區中正路100號'),
       (9, 9, '2025-05-18 20:00:00', 100, 1, 2, 1, 2, NULL, 1, NULL, '烤肉很香', 4, 10, 0, 90,
        '台北市信義區松仁路20號'),
       (10, 10, '2025-05-18 21:00:00', 250, 1, 0, 1, 2, NULL, 2, NULL, '火鍋湯底棒', 5, 0, 10, 240,
        '台中市北區學士路10號');

-- =========================
-- 10. 訂單明細
-- =========================
DROP TABLE IF EXISTS ORDER_DETAILS;
CREATE TABLE ORDER_DETAILS
(
    ORD_DET_ID INT AUTO_INCREMENT PRIMARY KEY,
    ORD_ID     INT NOT NULL,
    PROD_ID    INT NOT NULL,
    PROD_QTY   INT NOT NULL,
    PROD_PRICE INT NOT NULL,
    ORD_DESC   VARCHAR(255)
);

INSERT INTO ORDER_DETAILS (ORD_ID, PROD_ID, PROD_QTY, PROD_PRICE, ORD_DESC)
VALUES (1, 1, 2, 60, '多加蛋'),
       (2, 2, 1, 50, NULL),
       (3, 3, 1, 180, '不要蔥'),
       (4, 4, 1, 120, NULL),
       (5, 5, 1, 70, NULL),
       (6, 6, 1, 150, NULL),
       (7, 7, 1, 90, NULL),
       (8, 8, 1, 200, '加辣'),
       (9, 9, 1, 100, NULL),
       (10, 10, 1, 250, NULL);

-- =========================
-- 11. 優惠券
-- =========================
DROP TABLE IF EXISTS COUPON;
CREATE TABLE COUPON
(
    COU_ID      INT AUTO_INCREMENT PRIMARY KEY,
    STOR_ID     INT          NOT NULL,
    COU_TYPE    VARCHAR(255) NOT NULL,
    COU_DES     VARCHAR(255) NOT NULL,
    COU_MIN_ORD INT          NOT NULL,
    COU_DATE    DATETIME     NOT NULL
);

INSERT INTO COUPON (STOR_ID, COU_TYPE, COU_DES, COU_MIN_ORD, COU_DATE)
VALUES (1, '新開幕', '買500送500', 500, '2025-07-01 00:00:00'),
       (2, '生日券', '生日當月憑證折100元', 200, '2025-08-01 00:00:00'),
       (3, '會員專屬', '會員限定95折', 0, '2025-06-20 00:00:00'),
       (4, '滿額折扣', '滿300折30', 300, '2025-07-15 00:00:00'),
       (5, '飲品半價', '指定飲品半價', 50, '2025-07-20 00:00:00'),
       (6, '甜點優惠', '甜點第二件6折', 100, '2025-07-25 00:00:00'),
       (7, '外送折扣', '外送滿200免運', 200, '2025-08-01 00:00:00'),
       (8, '學生專屬', '學生證享9折', 0, '2025-08-10 00:00:00'),
       (9, '新會員禮', '新會員首單折50', 0, '2025-08-15 00:00:00'),
       (10, '老客戶回饋', '老客戶滿500折100', 500, '2025-08-20 00:00:00');

-- =========================
-- 12. 全站活動
-- =========================
DROP TABLE IF EXISTS ACTIVITY;
CREATE TABLE ACTIVITY
(
    ACT_ID          INT AUTO_INCREMENT PRIMARY KEY,
    STOR_ID         INT          NOT NULL,
    ACT_CATE        VARCHAR(50)  NOT NULL,
    ACT_NAME        VARCHAR(50)  NOT NULL,
    ACT_CONTENT     VARCHAR(255) NOT NULL,
    ACT_LAUNCHTIME  DATETIME     NOT NULL,
    ACT_STARTTIME   DATETIME     NOT NULL,
    ACT_ENDTIME     DATETIME     NOT NULL,
    ACT_STATUS      TINYINT      NOT NULL,
    ACT_DISCOUNT    TINYINT      NOT NULL,
    ACT_DISCOUNT_VALUE DOUBLE(10,2) NOT NULL,
    ACT_PHOTO       LONGBLOB,
    ACT_LAST_UPDATE DATETIME     NOT NULL,
    ISGLOBAL TINYINT(1) DEFAULT 1
);

INSERT INTO ACTIVITY (STOR_ID, ACT_CATE, ACT_NAME, ACT_CONTENT, ACT_LAUNCHTIME, ACT_STARTTIME, ACT_ENDTIME, ACT_STATUS, ACT_DISCOUNT, ACT_DISCOUNT_VALUE, ACT_PHOTO, ACT_LAST_UPDATE) VALUES
                                                                                                                                                                                          (1, '優惠活動', '夏日祭典', '飲料折5元', '2025-07-01 00:00:00', '2025-07-01 08:00:00', '2025-08-31 22:00:00', 0, 1, 5, NULL, '2025-05-17 21:00:00'),
                                                                                                                                                                                          (2, '新品上市', '草莓季限定', '全新草莓甜點登場，試吃享9折優惠', '2025-07-05 10:00:00', '2025-07-06 09:00:00', '2025-08-10 21:00:00', 0, 1, 0.9, NULL, '2025-05-17 21:00:00'),
                                                                                                                                                                                          (3, '限時優惠', '開幕慶', '開幕期間指定品項買一送一', '2025-06-15 00:00:00', '2025-06-16 10:00:00', '2025-07-15 23:00:00', 1, 1, 0.5, NULL, '2025-05-17 21:00:00'),
                                                                                                                                                                                          (4, '會員日', '會員消費享85折', '會員專屬消費享85折', '2025-08-01 00:00:00', '2025-08-01 00:00:00', '2025-08-31 23:59:59', 0, 1, 0.85, NULL, '2025-05-18 21:00:00'),
                                                                                                                                                                                          (5, '飲品日', '指定飲品買一送一', '指定飲品買一送一，限時搶購', '2025-08-05 00:00:00', '2025-08-05 10:00:00', '2025-08-05 22:00:00', 0, 1, 0.5, NULL, '2025-05-18 21:00:00'),
                                                                                                                                                                                          (6, '素食推廣', '蔬食餐點8折', '素食餐點全面8折', '2025-08-10 00:00:00', '2025-08-10 11:00:00', '2025-08-20 21:00:00', 0, 1, 0.8, NULL, '2025-05-18 21:00:00'),
                                                                                                                                                                                          (7, '速食優惠', '指定套餐第二件半價', '速食套餐第二件半價', '2025-08-15 00:00:00', '2025-08-15 12:00:00', '2025-08-31 21:00:00', 0, 1, 0.5, NULL, '2025-05-18 21:00:00'),
                                                                                                                                                                                          (8, '義式美食節', '義大利麵9折', '義大利麵全面9折', '2025-08-20 00:00:00', '2025-08-20 11:30:00', '2025-08-31 22:00:00', 0, 1, 0.9, NULL, '2025-05-18 21:00:00'),
                                                                                                                                                                                          (9, '燒烤日', '燒烤餐點買三送一', '燒烤餐點買三送一', '2025-08-25 00:00:00', '2025-08-25 17:00:00', '2025-08-31 23:00:00', 0, 1, 0.75, NULL, '2025-05-18 21:00:00'),
                                                                                                                                                                                          (10, '火鍋季', '火鍋套餐折50元', '火鍋套餐現折50元', '2025-08-30 00:00:00', '2025-08-30 11:00:00', '2025-09-10 23:00:00', 0, 1, 50, NULL, '2025-05-18 21:00:00');


-- =========================
-- 13. 團購商品類別
-- =========================
DROP TABLE IF EXISTS GB_PROD_CATEGORY;
CREATE TABLE GB_PROD_CATEGORY
(
    GB_CATE_ID   INT AUTO_INCREMENT PRIMARY KEY,
    GB_CATE_NAME VARCHAR(45) NOT NULL
);

INSERT INTO GB_PROD_CATEGORY (GB_CATE_NAME)
VALUES ('團購飲料'),
       ('團購甜點'),
       ('團購便當'),
       ('團購麵食'),
       ('團購燒烤'),
       ('團購火鍋'),
       ('團購素食'),
       ('團購速食'),
       ('團購異國'),
       ('團購零食');

-- =========================
-- 14. 團購商品
-- =========================
DROP TABLE IF EXISTS GROUP_PRODUCTS;
CREATE TABLE GROUP_PRODUCTS
(
    GB_PROD_ID            INT AUTO_INCREMENT PRIMARY KEY,
    STOR_ID               INT         NOT NULL,
    GB_CATE_ID            INT         NOT NULL,
    GB_PROD_NAME          VARCHAR(45) NOT NULL,
    GB_PROD_DESCRIPTION   VARCHAR(45) NOT NULL,
    GB_PROD_QUANTITY      INT         NOT NULL,
    GB_PROD_STATUS        TINYINT     NOT NULL,
    UPDATE_AT             DATETIME    NOT NULL,
    GB_PROD_SPECIFICATION VARCHAR(45) NOT NULL,
    PROD_PHOTO            LONGBLOB,
    GB_PROD_EVALUATE      VARCHAR(45),
    GB_PROD_REPORT_COUNT  TINYINT UNSIGNED NOT NULL
);

INSERT INTO GROUP_PRODUCTS (STOR_ID, GB_CATE_ID, GB_PROD_NAME, GB_PROD_DESCRIPTION, GB_PROD_QUANTITY, GB_PROD_STATUS,
                            UPDATE_AT, GB_PROD_SPECIFICATION, PROD_PHOTO, GB_PROD_EVALUATE, GB_PROD_REPORT_COUNT)
VALUES (1, 1, '珍珠奶茶', '人氣飲品', 100, 1, '2025-05-18', '700ml', NULL, '超好喝', 0),
       (2, 2, '草莓蛋糕', '新鮮草莓製作', 50, 1, '2025-05-18', '6吋', NULL, '甜而不膩', 0),
       (3, 3, '雞腿便當', '酥炸雞腿', 80, 1, '2025-05-18', '大份', NULL, '份量十足', 0),
       (4, 4, '牛肉拉麵', '手工拉麵', 60, 1, '2025-05-18', '一碗', NULL, '湯頭濃郁', 0),
       (5, 5, '烤雞翅', '炭火烤製', 120, 1, '2025-05-18', '每份5支', NULL, '香氣十足', 0),
       (6, 6, '麻辣鍋', '麻辣湯底', 40, 1, '2025-05-18', '2人份', NULL, '辣得過癮', 0),
       (7, 7, '蔬食便當', '全素健康', 70, 1, '2025-05-18', '一份', NULL, '營養均衡', 0),
       (8, 8, '炸雞桶', '家庭號', 30, 1, '2025-05-18', '10塊', NULL, '超值分享', 0),
       (9, 9, '韓式炸雞', '韓式醬料', 90, 1, '2025-05-18', '6塊', NULL, '道地口味', 0),
       (10, 10, '手工餅乾', '多種口味', 200, 1, '2025-05-18', '每包200g', NULL, '酥脆可口', 0);

-- =========================
-- 15. 團購商品促銷
-- =========================
DROP TABLE IF EXISTS GB_PROMOTION;
CREATE TABLE GB_PROMOTION
(
    GB_PROMO_ID   INT AUTO_INCREMENT PRIMARY KEY,
    GB_PROD_ID    INT      NOT NULL,
    GB_MIN_QTY    INT      NOT NULL,
    PROMT_START   DATETIME NOT NULL,
    PROMT_END     DATETIME NOT NULL,
    GB_PROD_SALES INT      NOT NULL,
    GB_PROD_SPE   INT      NOT NULL
);

INSERT INTO GB_PROMOTION (GB_PROD_ID, GB_MIN_QTY, PROMT_START, PROMT_END, GB_PROD_SALES, GB_PROD_SPE)
VALUES (1, 10, '2025-05-20', '2025-05-25', 50, 45),
       (2, 8, '2025-05-21', '2025-05-26', 30, 100),
       (3, 6, '2025-05-22', '2025-05-27', 40, 70),
       (4, 5, '2025-05-23', '2025-05-28', 20, 150),
       (5, 10, '2025-05-24', '2025-05-29', 60, 80),
       (6, 4, '2025-05-25', '2025-05-30', 15, 180),
       (7, 5, '2025-05-26', '2025-05-31', 25, 60),
       (8, 3, '2025-05-27', '2025-06-01', 10, 120),
       (9, 8, '2025-05-28', '2025-06-02', 35, 90),
       (10, 12, '2025-05-29', '2025-06-03', 80, 50);

-- =========================
-- 16. 團購案
-- =========================
DROP TABLE IF EXISTS GROUP_BUYING_CASES;
CREATE TABLE GROUP_BUYING_CASES
(
    GB_ID                        INT AUTO_INCREMENT PRIMARY KEY,
    STOR_ID                      INT          NOT NULL,
    GB_PROD_ID                   INT          NOT NULL,
    MEM_ID                       INT          NOT NULL,
    GB_START_TIME                DATETIME     NOT NULL,
    GB_END_TIME                  DATETIME     NOT NULL,
    GB_TITLE                     VARCHAR(45)  NOT NULL,
    GB_DESCRIPTION               VARCHAR(255) NOT NULL,
    GB_STATUS                    TINYINT      NOT NULL,
    GB_CREATE_AT                 DATETIME     NOT NULL,
    GB_MIN_PRODUCT_QUANTITY      INT          NOT NULL,
    CANCEL_REASON                VARCHAR(65),
    CUMULATIVE_PURCHASE_QUANTITY INT          NOT NULL
);

INSERT INTO GROUP_BUYING_CASES (STOR_ID, GB_PROD_ID, MEM_ID, GB_START_TIME, GB_END_TIME, GB_TITLE, GB_DESCRIPTION,
                                GB_STATUS, GB_CREATE_AT, GB_MIN_PRODUCT_QUANTITY, CANCEL_REASON,
                                CUMULATIVE_PURCHASE_QUANTITY)
VALUES (1, 1, 1, '2025-05-20 10:00:00', '2025-05-25 22:00:00', '飲料團購', '揪團喝珍奶', 1, '2025-05-18 09:00:00', 10,
        NULL, 5),
       (2, 2, 2, '2025-05-21 11:00:00', '2025-05-26 20:00:00', '甜點團購', '草莓蛋糕限時團', 1, '2025-05-18 09:30:00',
        8, NULL, 3),
       (3, 3, 3, '2025-05-22 12:00:00', '2025-05-27 21:00:00', '便當團購', '午餐便當團', 1, '2025-05-18 10:00:00', 6,
        NULL, 2),
       (4, 4, 4, '2025-05-23 13:00:00', '2025-05-28 19:00:00', '拉麵團購', '牛肉拉麵開團', 1, '2025-05-18 10:30:00', 5,
        NULL, 1),
       (5, 5, 5, '2025-05-24 14:00:00', '2025-05-29 18:00:00', '烤雞翅團購', '烤雞翅限量團', 1, '2025-05-18 11:00:00',
        10, NULL, 4),
       (6, 6, 6, '2025-05-25 15:00:00', '2025-05-30 17:00:00', '麻辣鍋團購', '麻辣鍋揪團', 1, '2025-05-18 11:30:00', 4,
        NULL, 3),
       (7, 7, 7, '2025-05-26 16:00:00', '2025-05-31 16:00:00', '蔬食便當團購', '健康蔬食', 1, '2025-05-18 12:00:00', 5,
        NULL, 2),
       (8, 8, 8, '2025-05-27 17:00:00', '2025-06-01 15:00:00', '炸雞桶團購', '超值炸雞桶', 1, '2025-05-18 12:30:00', 3,
        NULL, 1),
       (9, 9, 9, '2025-05-28 18:00:00', '2025-06-02 14:00:00', '韓式炸雞團購', '韓式道地炸雞', 1, '2025-05-18 13:00:00',
        8, NULL, 2),
       (10, 10, 10, '2025-05-29 19:00:00', '2025-06-03 13:00:00', '手工餅乾團購', '多口味餅乾', 1,
        '2025-05-18 13:30:00', 12, NULL, 5);

-- =========================
-- 17. 團購參與者
-- =========================
DROP TABLE IF EXISTS PARTICIPANTS;
CREATE TABLE PARTICIPANTS
(
    PAR_ID                INT AUTO_INCREMENT PRIMARY KEY,
    MEM_ID                INT            NOT NULL,
    GB_ID                 INT            NOT NULL,
    PAR_PHONE             VARCHAR(10)    NOT NULL,
    PAR_NAME              VARCHAR(45)    NOT NULL,
    PAR_ADDRESS           VARCHAR(45)    NOT NULL,
    PAR_LONGITUDE         DECIMAL(10, 6) NOT NULL,
    PAR_LATITUDE          DECIMAL(10, 6) NOT NULL,
    IS_LEADER             TINYINT        NOT NULL,
    PAR_PURCHASE_QUANTITY INT            NOT NULL,
    PAYMENT_STATUS        TINYINT        NOT NULL
);

INSERT INTO PARTICIPANTS (MEM_ID, GB_ID, PAR_PHONE, PAR_NAME, PAR_ADDRESS, PAR_LONGITUDE, PAR_LATITUDE, IS_LEADER,
                          PAR_PURCHASE_QUANTITY, PAYMENT_STATUS)
VALUES (1, 1, '0912345678', '王小明', '台北市大安區', 121.543, 25.026, 1, 2, 1),
       (2, 1, '0923456789', '林小美', '台北市中山區', 121.525, 25.052, 0, 1, 1),
       (3, 2, '0934567890', '陳志豪', '台中市西區', 120.666, 24.146, 1, 2, 1),
       (4, 2, '0945678901', '張安安', '台南市中西區', 120.193, 22.997, 0, 1, 1),
       (5, 3, '0956789012', '李杰', '高雄市左營區', 120.306, 22.683, 1, 1, 1),
       (6, 3, '0967890123', '吳小芳', '新竹市東區', 121.021, 24.800, 0, 1, 1),
       (7, 4, '0978901234', '周宇', '桃園市中壢區', 121.224, 24.957, 1, 1, 1),
       (8, 4, '0989012345', '曾敏', '台北市信義區', 121.567, 25.034, 0, 1, 1),
       (9, 5, '0990123456', '黃志明', '台中市北區', 120.684, 24.157, 1, 1, 1),
       (10, 5, '0901234567', '蔡惠', '宜蘭縣羅東鎮', 121.771, 24.667, 0, 1, 1);

-- =========================
-- 18. 團購訂單
-- =========================
DROP TABLE IF EXISTS GROUP_ORDERS;
CREATE TABLE GROUP_ORDERS
(
    GB_OR_ID        INT AUTO_INCREMENT PRIMARY KEY,
    GB_ID           INT            NOT NULL,
    STOR_ID         INT            NOT NULL,
    GB_PROD_ID      INT            NOT NULL,
    -- PAR_ID          INT            NOT NULL,
    JOIN_TIME       DATETIME       NOT NULL,
    AMOUNT          INT            NOT NULL,
    QUANTITY        INT            NOT NULL,
    PAY_METHOD      TINYINT        NOT NULL,
    ORDER_STATUS    TINYINT        NOT NULL,
    PAYMENT_STATUS  TINYINT        NOT NULL,
    SHIPPING_STATUS TINYINT        NOT NULL,
    PAR_NAME        VARCHAR(45)    NOT NULL,
    PAR_ADDRESS     VARCHAR(45)    NOT NULL,
    PAR_LONGITUDE   DECIMAL(10, 6) NOT NULL,
    PAR_LATITUDE    DECIMAL(10, 6) NOT NULL,
    PAR_PHONE       VARCHAR(10)    NOT NULL,
    DELIVERY_METHOD TINYINT        NOT NULL,
    COMMENT         VARCHAR(255),
    RATING          TINYINT UNSIGNED NOT NULL
);

INSERT INTO GROUP_ORDERS (GB_ID, STOR_ID, GB_PROD_ID, JOIN_TIME, AMOUNT, QUANTITY, PAY_METHOD, ORDER_STATUS,
                          PAYMENT_STATUS, SHIPPING_STATUS, PAR_NAME, PAR_ADDRESS, PAR_LONGITUDE, PAR_LATITUDE,
                          PAR_PHONE, DELIVERY_METHOD, COMMENT, RATING)
VALUES (1, 1, 1, '2025-05-20 10:30:00', 120, 2, 0, 2, 1, 1, '王小明', '台北市大安區', 121.543211, 25.033964,
        '0912345678', 0, '好喝', 5),
       (1, 1, 1, '2025-05-20 11:00:00', 60, 1, 1, 2, 1, 1, '林小美', '台北市中山區', 121.525000, 25.052361,
        '0923456789', 1, '方便', 4),
       (2, 2, 2, '2025-05-21 11:30:00', 200, 2, 2, 2, 1, 1, '陳志豪', '台中市西區', 120.666123, 24.149860,
        '0934567890', 0, '甜點好吃', 5),
       (2, 2, 2, '2025-05-21 12:00:00', 100, 1, 0, 2, 1, 1, '張安安', '台南市中西區', 120.193742, 22.997110,
        '0945678901', 1, '新鮮', 4),
       (3, 3, 3, '2025-05-22 12:30:00', 70, 1, 1, 1, 1, 1, '李杰', '高雄市左營區', 120.306839, 22.688130,
        '0956789012', 0, '便當好吃', 5),
       (3, 3, 3, '2025-05-22 13:00:00', 70, 1, 2, 1, 1, 1, '吳小芳', '新竹市東區', 121.021622, 24.805226,
        '0967890123', 1, '份量足', 4),
       (4, 4, 4, '2025-05-23 13:30:00', 150, 1, 0, 2, 1, 1, '周宇', '桃園市中壢區', 121.224926, 24.965425,
        '0978901234', 0, '拉麵湯濃', 5),
       (4, 4, 4, '2025-05-23 14:00:00', 150, 1, 1, 2, 1, 1, '曾敏', '台北市信義區', 121.567294, 25.033141,
        '0989012345', 1, '推薦', 4),
       (5, 5, 5, '2025-05-24 14:30:00', 80, 1, 2, 2, 1, 1, '黃志明', '台中市北區', 120.684265, 24.158577,
        '0990123456', 0, '雞翅香', 5),
       (5, 5, 5, '2025-05-24 15:00:00', 80, 1, 0, 2, 1, 1, '蔡惠', '宜蘭縣羅東鎮', 121.771133, 24.677683,
        '0901234567', 1, '很棒', 4);


-- =========================
-- 19. 收藏團購清單
-- =========================
DROP TABLE IF EXISTS GROUP_BUYING_COLLECTION_LIST;
CREATE TABLE GROUP_BUYING_COLLECTION_LIST
(
    GB_ID     INT      NOT NULL,
    MEM_ID    INT      NOT NULL,
    CREATE_AT DATETIME NOT NULL,
    PRIMARY KEY (GB_ID, MEM_ID)
);

INSERT INTO GROUP_BUYING_COLLECTION_LIST (GB_ID, MEM_ID, CREATE_AT)
VALUES (1, 1, '2025-05-18 10:00:00'),
       (2, 2, '2025-05-18 10:10:00'),
       (3, 3, '2025-05-18 10:20:00'),
       (4, 4, '2025-05-18 10:30:00'),
       (5, 5, '2025-05-18 10:40:00'),
       (6, 6, '2025-05-18 10:50:00'),
       (7, 7, '2025-05-18 11:00:00'),
       (8, 8, '2025-05-18 11:10:00'),
       (9, 9, '2025-05-18 11:20:00'),
       (10, 10, '2025-05-18 11:30:00');

-- =========================
-- 20. 團購檢舉單
-- =========================
DROP TABLE IF EXISTS GROUP_PURCHASE_REPORT;
CREATE TABLE GROUP_PURCHASE_REPORT
(
    REPORT_ID     INT AUTO_INCREMENT PRIMARY KEY,
    MEM_ID        INT          NOT NULL,
    GB_ID         INT          NOT NULL,
    REPORT_REASON VARCHAR(255) NOT NULL,
    REPORT_DETAIL VARCHAR(1000),
    REPORT_STATUS TINYINT      NOT NULL,
    CREATE_AT     DATETIME     NOT NULL,
    UPDATE_AT     DATETIME     NOT NULL
);

INSERT INTO GROUP_PURCHASE_REPORT (MEM_ID, GB_ID, REPORT_REASON, REPORT_DETAIL, REPORT_STATUS, CREATE_AT, UPDATE_AT)
VALUES (1, 1, '價格異常', '團購價格與店面不符', 0, '2025-05-18 12:00:00', '2025-05-18 12:10:00'),
       (2, 2, '內容不實', '商品描述與實際不符', 1, '2025-05-18 12:20:00', '2025-05-18 12:30:00'),
       (3, 3, '遲遲未出貨', '超過預定出貨日', 0, '2025-05-18 12:40:00', '2025-05-18 12:50:00'),
       (4, 4, '聯絡不上團主', '無法溝通', 2, '2025-05-18 13:00:00', '2025-05-18 13:10:00'),
       (5, 5, '品質不佳', '食物不新鮮', 1, '2025-05-18 13:20:00', '2025-05-18 13:30:00'),
       (6, 6, '數量短缺', '實際收到數量不足', 0, '2025-05-18 13:40:00', '2025-05-18 13:50:00'),
       (7, 7, '配送延誤', '配送時間嚴重延遲', 1, '2025-05-18 14:00:00', '2025-05-18 14:10:00'),
       (8, 8, '態度不佳', '團主態度不友善', 2, '2025-05-18 14:20:00', '2025-05-18 14:30:00'),
       (9, 9, '金額計算錯誤', '付款金額有誤', 1, '2025-05-18 14:40:00', '2025-05-18 14:50:00'),
       (10, 10, '其他', '其他問題', 0, '2025-05-18 15:00:00', '2025-05-18 15:10:00');

-- =========================
-- 21. 社群-貼文類別
-- =========================
DROP TABLE IF EXISTS POST_CATEGORY;
CREATE TABLE POST_CATEGORY
(
    POST_CATE_ID INT AUTO_INCREMENT PRIMARY KEY,
    POST_CATE    VARCHAR(45) NOT NULL
);

INSERT INTO POST_CATEGORY (POST_CATE)
VALUES ('食記分享'),
       ('日記'),
       ('店家優惠'),
       ('團購心得'),
       ('美食討論'),
       ('省錢攻略'),
       ('健康飲食'),
       ('快速外帶'),
       ('素食專區'),
       ('飲品推薦');

-- =========================
-- 22. 社群-貼文
-- =========================
DROP TABLE IF EXISTS POST;
CREATE TABLE POST
(
    POST_ID      INT  AUTO_INCREMENT,
    MEM_ID       INT            NOT NULL,
    POST_CATE_ID INT            NOT NULL,
    POST_DATE    DATETIME       NOT NULL,
    POST_STATUS  TINYINT        NOT NULL,
    EDITDATE     DATETIME       NOT NULL,
    POST_TITLE   VARCHAR(100)   NOT NULL,
    POST_CONTENT LONGTEXT       NOT NULL,
    LIKE_COUNT   INT            NOT NULL,
    VIEWS        INT            NOT NULL,
    PRIMARY KEY (POST_ID)
);

INSERT INTO POST (MEM_ID, POST_CATE_ID, POST_DATE, POST_STATUS, EDITDATE, POST_TITLE, POST_CONTENT, LIKE_COUNT, VIEWS)
VALUES (1, 1, '2023-10-26 10:00:00', 0, '2023-10-26 10:00:00', '我的早餐推薦', '這家早餐店超好吃！', 100, 1000),
       (2, 2, '2023-10-27 14:30:00', 1, '2023-10-27 14:30:00', '今天的午餐', '便當好便宜又好吃', 200, 2000),
       (3, 3, '2023-10-28 09:15:00', 2, '2023-10-28 09:15:00', '店家優惠情報', '快來搶限時優惠券', 300, 3000),
       (4, 4, '2023-10-29 11:00:00', 0, '2023-10-29 11:00:00', '團購心得分享', '大家一起團購超省錢', 150, 1200),
       (5, 5, '2023-10-30 16:45:00', 1, '2023-10-30 16:45:00', '美食討論', '你最愛哪家拉麵？', 180, 1600),
       (6, 6, '2023-10-31 18:00:00', 2, '2023-10-31 18:00:00', '省錢攻略', '怎麼用優惠券最划算', 220, 1800),
       (7, 7, '2023-11-01 09:00:00', 0, '2023-11-01 09:00:00', '健康飲食小撇步', '多吃蔬菜少油炸', 90, 900),
       (8, 8, '2023-11-02 12:00:00', 1, '2023-11-02 12:00:00', '快速外帶體驗', '外帶真的很方便', 130, 1100),
       (9, 9, '2023-11-03 15:00:00', 2, '2023-11-03 15:00:00', '素食專區推薦', '素食也能吃得開心', 140, 1050),
       (10, 10, '2023-11-04 17:00:00', 0, '2023-11-04 17:00:00', '飲品推薦', '這家芒果冰沙超消暑', 175, 1300);

-- =========================
-- 23. 收藏貼文
-- =========================
DROP TABLE IF EXISTS FAVORITE_POST;
CREATE TABLE FAVORITE_POST
(
    POST_ID INT NOT NULL,
    MEM_ID  INT NOT NULL,
    PRIMARY KEY (POST_ID, MEM_ID)
);

INSERT INTO FAVORITE_POST (POST_ID, MEM_ID)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5),
       (6, 6),
       (7, 7),
       (8, 8),
       (9, 9),
       (10, 10);

-- =========================
-- 24. 檢舉貼文
-- =========================
DROP TABLE IF EXISTS REPORT_POST;
CREATE TABLE REPORT_POST
(
    REP_POST_ID     INT AUTO_INCREMENT,
    POST_ID         INT          NOT NULL,
    MEM_ID          INT          NOT NULL,
    REP_POST_DATE   DATETIME     NOT NULL,
    REP_POST_REASON VARCHAR(255) NOT NULL,
    REP_POST_STATUS TINYINT      NOT NULL,
    primary key (REP_POST_ID)
);

INSERT INTO REPORT_POST (POST_ID, MEM_ID, REP_POST_DATE, REP_POST_REASON, REP_POST_STATUS)
VALUES (1, 2, '2023-10-29 11:00:00', '內容不當', 0),
       (2, 3, '2023-10-30 16:45:00', '廣告內容', 1),
       (3, 4, '2023-10-31 12:00:00', '抄襲', 2),
       (4, 5, '2023-11-01 13:00:00', '謾罵', 0),
       (5, 6, '2023-11-02 14:00:00', '不實資訊', 1),
       (6, 7, '2023-11-03 15:00:00', '重複貼文', 2),
       (7, 8, '2023-11-04 16:00:00', '引戰', 0),
       (8, 9, '2023-11-05 17:00:00', '其他', 1),
       (9, 10, '2023-11-06 18:00:00', '內容不適合', 2),
       (10, 1, '2023-11-07 19:00:00', '無關主題', 0);

-- =========================
-- 25. 留言
-- =========================
DROP TABLE IF EXISTS MESSAGE;
CREATE TABLE MESSAGE
(
    MES_ID      INT AUTO_INCREMENT PRIMARY KEY,
    POST_ID     INT          NOT NULL,
    MEM_ID      INT          NOT NULL,
    MES_DATE    DATETIME     NOT NULL,
    MES_CONTENT VARCHAR(255) NOT NULL
);

INSERT INTO MESSAGE (POST_ID, MEM_ID, MES_DATE, MES_CONTENT)
VALUES (1, 2, '2023-10-26 11:00:00', 'Great post!'),
       (2, 3, '2023-10-27 12:30:00', 'I agree.'),
       (3, 4, '2023-10-28 15:00:00', 'Interesting.'),
       (4, 5, '2023-10-29 16:00:00', 'Thanks for sharing!'),
       (5, 6, '2023-10-30 17:00:00', '好棒的資訊'),
       (6, 7, '2023-10-31 18:00:00', '受用無窮'),
       (7, 8, '2023-11-01 19:00:00', '很有幫助'),
       (8, 9, '2023-11-02 20:00:00', '下次也想試試'),
       (9, 10, '2023-11-03 21:00:00', '推推'),
       (10, 1, '2023-11-04 22:00:00', '感謝推薦');

-- =========================
-- 26. 檢舉留言
-- =========================
DROP TABLE IF EXISTS REPORT_MESSAGE;
CREATE TABLE REPORT_MESSAGE
(
    REP_MES_ID     INT AUTO_INCREMENT PRIMARY KEY,
    MES_ID         INT          NOT NULL,
    MEM_ID         INT          NOT NULL,
    REP_MES_DATE   DATETIME     NOT NULL,
    REP_MES_REASON VARCHAR(255) NOT NULL,
    REP_MES_STATUS TINYINT      NOT NULL
);

INSERT INTO REPORT_MESSAGE (MES_ID, MEM_ID, REP_MES_DATE, REP_MES_REASON, REP_MES_STATUS)
VALUES (1, 2, '2023-10-27 13:00:00', 'Inappropriate language', 0),
       (2, 3, '2023-10-28 17:00:00', 'Spam', 1),
       (3, 4, '2023-10-29 18:00:00', 'Off-topic', 0),
       (4, 5, '2023-10-30 19:00:00', 'Personal attack', 1),
       (5, 6, '2023-10-31 20:00:00', '廣告', 0),
       (6, 7, '2023-11-01 21:00:00', '洗板', 1),
       (7, 8, '2023-11-02 22:00:00', '侮辱', 0),
       (8, 9, '2023-11-03 23:00:00', '引戰', 1),
       (9, 10, '2023-11-04 09:00:00', '重複留言', 0),
       (10, 1, '2023-11-05 10:00:00', '內容不實', 1);

-- =========================
-- 27. 私訊
-- =========================
DROP TABLE IF EXISTS DIRECT_MESSAGE;
create table DIRECT_MESSAGE (
                                DM_ID INT auto_increment NOT NULL,
                                MEM_ID INT NOT NULL,
                                SMGR_ID INT,
                                MESS_CONTENT longtext NOT NULL,
                                MESS_TIME datetime NOT NULL,
                                MESS_DIRECTION tinyint NOT NULL,
                                constraint DIRECT_MESSAGE_DMID_PK primary key (DM_ID)

);

INSERT INTO DIRECT_MESSAGE (MEM_ID, SMGR_ID, MESS_CONTENT, MESS_TIME, MESS_DIRECTION)
VALUES (1, 1, '您好，請問優惠券怎麼領？', '2025-05-01 10:00:00', 1),
       (2, 2, '商品有問題可以協助嗎？', '2025-05-01 10:10:00', 0),
       (3, 3, '感謝客服協助', '2025-05-01 10:20:00', 1),
       (4, 4, '請問團購如何參加？', '2025-05-01 10:30:00', 0),
       (5, 5, '如何檢舉不實資訊？', '2025-05-01 10:40:00', 1),
       (6, 6, '我的訂單還沒到', '2025-05-01 10:50:00', 0),
       (7, 7, '想詢問合作事宜', '2025-05-01 11:00:00', 1),
       (8, 8, '請問如何成為店家？', '2025-05-01 11:10:00', 0),
       (9, 9, '有推薦的活動嗎？', '2025-05-01 11:20:00', 1),
       (10, 10, '平台有什麼新功能？', '2025-05-01 11:30:00', 0);

-- =========================
-- 28. 後台管理功能
-- =========================
DROP TABLE IF EXISTS SERVERMANAGEFUNCTION;
CREATE TABLE SERVERMANAGEFUNCTION
(
    SMGEFUNC_ID INT AUTO_INCREMENT PRIMARY KEY,
    SMGEFUNC    VARCHAR(45) NOT NULL
);

INSERT INTO SERVERMANAGEFUNCTION (SMGEFUNC)
VALUES ('用戶管理'),
       ('店家管理'),
       ('商品管理'),
       ('訂單管理'),
       ('優惠管理'),
       ('團購管理'),
       ('社群管理'),
       ('檢舉處理'),
       ('數據分析'),
       ('權限設定');

-- =========================
-- 29. 後台管理員
-- =========================
DROP TABLE IF EXISTS SERVERMANAGER;
CREATE TABLE SERVERMANAGER
(
    SMGR_ID       INT AUTO_INCREMENT PRIMARY KEY,
    SMGR_EMAIL    VARCHAR(45)  NOT NULL,
    SMGR_ACCOUNT  VARCHAR(45)  NOT NULL UNIQUE,
    SMGR_PASSWORD VARCHAR(128) NOT NULL,
    SMGR_NAME     VARCHAR(45)  NOT NULL,
    SMGR_PHONE    VARCHAR(10)  NOT NULL,
    SMGR_STATUS   TINYINT      NOT NULL DEFAULT 1
);

INSERT INTO SERVERMANAGER (SMGR_EMAIL, SMGR_ACCOUNT, SMGR_PASSWORD, SMGR_NAME, SMGR_PHONE, SMGR_STATUS)
VALUES ('admin1@foodmap.com', 'admin1', 'pass1', '管理員一', '0900000001', 1),
       ('admin2@foodmap.com', 'admin2', 'pass2', '管理員二', '0900000002', 1),
       ('admin3@foodmap.com', 'admin3', 'pass3', '管理員三', '0900000003', 1),
       ('admin4@foodmap.com', 'admin4', 'pass4', '管理員四', '0900000004', 1),
       ('admin5@foodmap.com', 'admin5', 'pass5', '管理員五', '0900000005', 1),
       ('admin6@foodmap.com', 'admin6', 'pass6', '管理員六', '0900000006', 1),
       ('admin7@foodmap.com', 'admin7', 'pass7', '管理員七', '0900000007', 1),
       ('admin8@foodmap.com', 'admin8', 'pass8', '管理員八', '0900000008', 1),
       ('admin9@foodmap.com', 'admin9', 'pass9', '管理員九', '0900000009', 1),
       ('admin10@foodmap.com', 'admin10', 'pass10', '管理員十', '0900000010', 1);

-- =========================
-- 30. 後台管理員權限
-- =========================
DROP TABLE IF EXISTS SERVERMANAGERAUTH;
CREATE TABLE SERVERMANAGERAUTH
(
    SMGEFUNC_ID INT NOT NULL,
    SMGR_ID     INT NOT NULL,
    PRIMARY KEY (SMGEFUNC_ID, SMGR_ID)
);

INSERT INTO SERVERMANAGERAUTH (SMGEFUNC_ID, SMGR_ID)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5),
       (6, 6),
       (7, 7),
       (8, 8),
       (9, 9),
       (10, 10);

-- =========================
-- 31. 平台撥款及佣金紀錄
-- =========================
DROP TABLE IF EXISTS APPROPRIATION_COMM_RECORD;
CREATE TABLE APPROPRIATION_COMM_RECORD
(
    COMM_PAYOUT_ID    INT AUTO_INCREMENT PRIMARY KEY,
    ORDER_TYPE        TINYINT  NOT NULL,
    ORDER_REF_ID      INT      NOT NULL,
    STOR_ID           INT,
    MEM_ID            INT,
    PAYOUT_ROLE       TINYINT  NOT NULL,
    PAYOUT_AMOUNT     DECIMAL(10, 2),
    COMMISSION_AMOUNT DECIMAL(10, 2),
    PAYOUT_STATUS     TINYINT  NOT NULL,
    COMMISSION_STATUS TINYINT  NOT NULL,
    CREATE_AT         DATETIME NOT NULL,
    UPDATE_AT         DATETIME NOT NULL,
    PAYOUT_TIME       DATETIME,
    PAYOUT_MONTH      VARCHAR(6)
);

INSERT INTO APPROPRIATION_COMM_RECORD (ORDER_TYPE, ORDER_REF_ID, STOR_ID, MEM_ID, PAYOUT_ROLE, PAYOUT_AMOUNT,
                                       COMMISSION_AMOUNT, PAYOUT_STATUS, COMMISSION_STATUS, CREATE_AT, UPDATE_AT,
                                       PAYOUT_TIME, PAYOUT_MONTH)
VALUES (0, 1, 1, 1, 0, 100.00, 10.00, 1, 1, '2025-05-18 12:00:00', '2025-05-18 13:00:00', '2025-05-19 12:00:00',
        '202505'),
       (0, 2, 2, 2, 0, 90.00, 9.00, 1, 1, '2025-05-18 13:00:00', '2025-05-18 14:00:00', '2025-05-19 13:00:00',
        '202505'),
       (0, 3, 3, 3, 0, 160.00, 16.00, 1, 1, '2025-05-18 14:00:00', '2025-05-18 15:00:00', '2025-05-19 14:00:00',
        '202505'),
       (0, 4, 4, 4, 0, 0.00, 0.00, 0, 0, '2025-05-18 15:00:00', '2025-05-18 16:00:00', NULL, '202505'),
       (0, 5, 5, 5, 0, 60.00, 6.00, 1, 1, '2025-05-18 16:00:00', '2025-05-18 17:00:00', '2025-05-19 16:00:00',
        '202505'),
       (0, 6, 6, 6, 0, 140.00, 14.00, 1, 1, '2025-05-18 17:00:00', '2025-05-18 18:00:00', '2025-05-19 17:00:00',
        '202505'),
       (0, 7, 7, 7, 0, 0.00, 0.00, 0, 0, '2025-05-18 18:00:00', '2025-05-18 19:00:00', NULL, '202505'),
       (0, 8, 8, 8, 0, 180.00, 18.00, 1, 1, '2025-05-18 19:00:00', '2025-05-18 20:00:00', '2025-05-19 19:00:00',
        '202505'),
       (0, 9, 9, 9, 0, 90.00, 9.00, 1, 1, '2025-05-18 20:00:00', '2025-05-18 21:00:00', '2025-05-19 20:00:00',
        '202505'),
       (0, 10, 10, 10, 0, 240.00, 24.00, 1, 1, '2025-05-18 21:00:00', '2025-05-18 22:00:00', '2025-05-19 21:00:00',
        '202505');

-- =========================
-- 外鍵統一於最後設定
-- =========================

ALTER TABLE STORE
    ADD CONSTRAINT FK_STORE_CATEGORY
        FOREIGN KEY (STORE_CATE_ID) REFERENCES STORE_CATEGORY (STORE_CATE_ID);

ALTER TABLE PRODUCT
    ADD CONSTRAINT FK_PRODUCT_STORE
        FOREIGN KEY (STOR_ID) REFERENCES STORE (STOR_ID),
ADD CONSTRAINT FK_PRODUCT_CATEGORY
FOREIGN KEY (PROD_CATE_ID) REFERENCES PRODUCT_CATEGORY(PROD_CATE_ID);

ALTER TABLE FAVORITE_LIST
    ADD CONSTRAINT FK_FAVORITE_LIST_MEMBER
        FOREIGN KEY (MEM_ID) REFERENCES MEMBER (MEM_ID),
ADD CONSTRAINT FK_FAVORITE_LIST_PRODUCT
FOREIGN KEY (PROD_ID) REFERENCES PRODUCT(PROD_ID);

ALTER TABLE SHOPPING_CART
    ADD CONSTRAINT FK_SHOPPING_CART_MEMBER
        FOREIGN KEY (MEM_ID) REFERENCES MEMBER (MEM_ID),
ADD CONSTRAINT FK_SHOPPING_CART_PRODUCT
FOREIGN KEY (PROD_ID) REFERENCES PRODUCT(PROD_ID);

ALTER TABLE MEMBER_COUPON
    ADD CONSTRAINT FK_MEMBER_COUPON_COUPON
        FOREIGN KEY (COU_ID) REFERENCES COUPON (COU_ID),
ADD CONSTRAINT FK_MEMBER_COUPON_MEMBER
FOREIGN KEY (MEM_ID) REFERENCES MEMBER(MEM_ID);

ALTER TABLE ORDERS
    ADD CONSTRAINT FK_ORDERS_MEMBER
        FOREIGN KEY (MEM_ID) REFERENCES MEMBER (MEM_ID),
ADD CONSTRAINT FK_ORDERS_STORE
FOREIGN KEY (STOR_ID) REFERENCES STORE(STOR_ID),
ADD CONSTRAINT FK_ORDERS_ACTIVITY
FOREIGN KEY (ACT_ID) REFERENCES ACTIVITY(ACT_ID),
ADD CONSTRAINT FK_ORDERS_COUPON
FOREIGN KEY (COU_ID) REFERENCES COUPON(COU_ID);

ALTER TABLE ORDER_DETAILS
    ADD CONSTRAINT FK_ORDER_DETAILS_ORDERS
        FOREIGN KEY (ORD_ID) REFERENCES ORDERS (ORD_ID),
ADD CONSTRAINT FK_ORDER_DETAILS_PRODUCT
FOREIGN KEY (PROD_ID) REFERENCES PRODUCT(PROD_ID);

ALTER TABLE COUPON
    ADD CONSTRAINT FK_COUPON_STORE
        FOREIGN KEY (STOR_ID) REFERENCES STORE (STOR_ID);

ALTER TABLE ACTIVITY
    ADD CONSTRAINT FK_ACTIVITY_STORE
        FOREIGN KEY (STOR_ID) REFERENCES STORE (STOR_ID);

ALTER TABLE GROUP_PRODUCTS
    ADD CONSTRAINT FK_GROUP_PRODUCTS_STORE
        FOREIGN KEY (STOR_ID) REFERENCES STORE (STOR_ID),
ADD CONSTRAINT FK_GROUP_PRODUCTS_CATEGORY
FOREIGN KEY (GB_CATE_ID) REFERENCES GB_PROD_CATEGORY(GB_CATE_ID);

ALTER TABLE GB_PROMOTION
    ADD CONSTRAINT FK_GB_PROMOTION_PROD
        FOREIGN KEY (GB_PROD_ID) REFERENCES GROUP_PRODUCTS (GB_PROD_ID);

ALTER TABLE GROUP_BUYING_CASES
    ADD CONSTRAINT FK_GBC_STORE
        FOREIGN KEY (STOR_ID) REFERENCES STORE (STOR_ID),
ADD CONSTRAINT FK_GBC_PROD
FOREIGN KEY (GB_PROD_ID) REFERENCES GROUP_PRODUCTS(GB_PROD_ID),
ADD CONSTRAINT FK_GBC_MEMBER
FOREIGN KEY (MEM_ID) REFERENCES MEMBER(MEM_ID);

ALTER TABLE PARTICIPANTS
    ADD CONSTRAINT FK_PARTICIPANTS_MEMBER
        FOREIGN KEY (MEM_ID) REFERENCES MEMBER (MEM_ID),
ADD CONSTRAINT FK_PARTICIPANTS_GB
FOREIGN KEY (GB_ID) REFERENCES GROUP_BUYING_CASES(GB_ID);

ALTER TABLE GROUP_ORDERS
    ADD CONSTRAINT FK_GROUP_ORDERS_GB
        FOREIGN KEY (GB_ID) REFERENCES GROUP_BUYING_CASES (GB_ID),
ADD CONSTRAINT FK_GROUP_ORDERS_STORE
FOREIGN KEY (STOR_ID) REFERENCES STORE(STOR_ID),
ADD CONSTRAINT FK_GROUP_ORDERS_PROD
FOREIGN KEY (GB_PROD_ID) REFERENCES GROUP_PRODUCTS(GB_PROD_ID);

ALTER TABLE GROUP_BUYING_COLLECTION_LIST
    ADD CONSTRAINT FK_GB_COLLECTION_GB
        FOREIGN KEY (GB_ID) REFERENCES GROUP_BUYING_CASES (GB_ID),
ADD CONSTRAINT FK_GB_COLLECTION_MEMBER
FOREIGN KEY (MEM_ID) REFERENCES MEMBER(MEM_ID);

ALTER TABLE GROUP_PURCHASE_REPORT
    ADD CONSTRAINT FK_GPR_MEMBER
        FOREIGN KEY (MEM_ID) REFERENCES MEMBER (MEM_ID),
ADD CONSTRAINT FK_GPR_GB
FOREIGN KEY (GB_ID) REFERENCES GROUP_BUYING_CASES(GB_ID);

ALTER TABLE POST
    ADD CONSTRAINT FK_POST_MEMBER
        FOREIGN KEY (MEM_ID) REFERENCES MEMBER (MEM_ID),
ADD CONSTRAINT FK_POST_CATEGORY
FOREIGN KEY (POST_CATE_ID) REFERENCES POST_CATEGORY(POST_CATE_ID);

ALTER TABLE FAVORITE_POST
    ADD CONSTRAINT FK_FAVORITE_POST_POST
        FOREIGN KEY (POST_ID) REFERENCES POST (POST_ID),
ADD CONSTRAINT FK_FAVORITE_POST_MEMBER
FOREIGN KEY (MEM_ID) REFERENCES MEMBER(MEM_ID);

ALTER TABLE REPORT_POST
    ADD CONSTRAINT FK_REPORT_POST_POST
        FOREIGN KEY (POST_ID) REFERENCES POST (POST_ID),
ADD CONSTRAINT FK_REPORT_POST_MEMBER
FOREIGN KEY (MEM_ID) REFERENCES MEMBER(MEM_ID);

ALTER TABLE MESSAGE
    ADD CONSTRAINT FK_MESSAGE_POST
        FOREIGN KEY (POST_ID) REFERENCES POST (POST_ID),
ADD CONSTRAINT FK_MESSAGE_MEMBER
FOREIGN KEY (MEM_ID) REFERENCES MEMBER(MEM_ID);

ALTER TABLE REPORT_MESSAGE
    ADD CONSTRAINT FK_REPORT_MESSAGE_MES
        FOREIGN KEY (MES_ID) REFERENCES MESSAGE (MES_ID),
ADD CONSTRAINT FK_REPORT_MESSAGE_MEMBER
FOREIGN KEY (MEM_ID) REFERENCES MEMBER(MEM_ID);

ALTER TABLE DIRECT_MESSAGE
    ADD CONSTRAINT FK_DIRECT_MESSAGE_MEMBER
        FOREIGN KEY (MEM_ID) REFERENCES MEMBER (MEM_ID),
    ADD CONSTRAINT FK_DIRECT_MESSAGE_MANAGER
        FOREIGN KEY (SMGR_ID) REFERENCES SERVERMANAGER(SMGR_ID);


ALTER TABLE SERVERMANAGERAUTH
    ADD CONSTRAINT FK_SERVERMANAGERAUTH_FUNC
        FOREIGN KEY (SMGEFUNC_ID) REFERENCES SERVERMANAGEFUNCTION (SMGEFUNC_ID),
ADD CONSTRAINT FK_SERVERMANAGERAUTH_MANAGER
FOREIGN KEY (SMGR_ID) REFERENCES SERVERMANAGER(SMGR_ID);

ALTER TABLE APPROPRIATION_COMM_RECORD
    ADD CONSTRAINT FK_ACR_STORE
        FOREIGN KEY (STOR_ID) REFERENCES STORE (STOR_ID),
ADD CONSTRAINT FK_ACR_MEMBER
FOREIGN KEY (MEM_ID) REFERENCES MEMBER(MEM_ID);

ALTER TABLE group_buying_cases
MODIFY GB_CREATE_AT datetime NULL;
ALTER TABLE appropriation_comm_record
ADD COLUMN SMGR_ID INT;
UPDATE appropriation_comm_record SET SMGR_ID = 1 WHERE COMM_PAYOUT_ID = 1;
UPDATE appropriation_comm_record SET SMGR_ID = 2 WHERE COMM_PAYOUT_ID = 2;
UPDATE appropriation_comm_record SET SMGR_ID = 3 WHERE COMM_PAYOUT_ID = 3;
UPDATE appropriation_comm_record SET SMGR_ID = 4 WHERE COMM_PAYOUT_ID = 4;
UPDATE appropriation_comm_record SET SMGR_ID = 5 WHERE COMM_PAYOUT_ID = 5;
UPDATE appropriation_comm_record SET SMGR_ID = 6 WHERE COMM_PAYOUT_ID = 6;
UPDATE appropriation_comm_record SET SMGR_ID = 7 WHERE COMM_PAYOUT_ID = 7;
UPDATE appropriation_comm_record SET SMGR_ID = 8 WHERE COMM_PAYOUT_ID = 8;
UPDATE appropriation_comm_record SET SMGR_ID = 9 WHERE COMM_PAYOUT_ID = 9;
UPDATE appropriation_comm_record SET SMGR_ID = 10 WHERE COMM_PAYOUT_ID = 10;
ALTER TABLE appropriation_comm_record
MODIFY SMGR_ID INT NOT NULL,
ADD CONSTRAINT fk_acr_smgr
    FOREIGN KEY (SMGR_ID)
    REFERENCES servermanager(SMGR_ID);
    
INSERT INTO SERVERMANAGEFUNCTION (SMGEFUNC)
VALUES 
('discussion_reports'),
('vendor_review'),
('vendor_edit'),
('vendor_reports'),
('vendor_blacklist'),
('product_disable'),
('vendor_permissions'),
('member_search'),
('member_permissions'),
('group_reports'),
('group_status'),
('group_orders'),
('group_payments'),
('group_refunds'),
('group_monthly'),
('order_view'),
('order_payments'),
('service_tickets');
-- 步驟一：關閉安全模式
SET SQL_SAFE_UPDATES = 0;

-- 步驟二：清空現有權限配置
DELETE FROM SERVERMANAGERAUTH;

-- 步驟三：插入新的權限配置
INSERT INTO SERVERMANAGERAUTH (SMGEFUNC_ID, SMGR_ID)
VALUES 
-- 管理員1：超級管理員，擁有所有新權限
(11, 1), (12, 1), (13, 1), (14, 1), (15, 1), (16, 1), (17, 1), (18, 1),
(19, 1), (20, 1), (21, 1), (22, 1), (23, 1), (24, 1), (25, 1), (26, 1), (27, 1), (28, 1),

-- 管理員2：店家管理專員
(12, 2), (13, 2), (14, 2), (15, 2), (16, 2), (17, 2),

-- 管理員3：會員管理專員
(18, 3), (19, 3), (11, 3), (28, 3),

-- 管理員4：訂單管理專員
(26, 4), (27, 4),

-- 管理員5：團購管理專員
(20, 5), (21, 5), (22, 5), (23, 5), (24, 5), (25, 5),

-- 管理員6：客服專員
(28, 6), (11, 6), (18, 6),

-- 管理員7：店家審核專員
(12, 7), (14, 7), (16, 7),

-- 管理員8：會員服務專員
(18, 8), (19, 8), (28, 8),

-- 管理員9：團購監控專員
(20, 9), (21, 9), (22, 9),

-- 管理員10：綜合業務專員
(26, 10), (27, 10), (20, 10), (28, 10);

-- 步驟四：重新開啟安全模式
SET SQL_SAFE_UPDATES = 1;