-- MySQL dump 10.13  Distrib 5.6.23, for Win32 (x86)
--
-- Host: 127.0.0.1    Database: travel_db
-- ------------------------------------------------------
-- Server version	5.7.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `travel_db`
--

/*!40000 DROP DATABASE IF EXISTS `travel_db`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `travel_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `travel_db`;

--
-- Table structure for table `banner`
--

DROP TABLE IF EXISTS `banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `banner` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `subTitle` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `desc` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `img` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thumb` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `link` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `isFixed` tinyint(4) DEFAULT '0' COMMENT '1=固定数据，管理员不可删改;0=管理员新增可编辑删除',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banner`
--

LOCK TABLES `banner` WRITE;
/*!40000 ALTER TABLE `banner` DISABLE KEYS */;
INSERT INTO `banner` VALUES (1,'奔赴雪山之巅','翻越层叠山峦，邂逅圣洁雪山风光，感受高原独有的辽阔与宁静。','/banner/big/banner1.jpeg','/banner/thumb/thumb1.jpg','/spot','启用',1),(2,'江南水乡寻梦','小桥流水白墙黛瓦，在古镇慢时光里，邂逅温柔江南烟火。','/banner/big/banner2.jpg','/banner/thumb/thumb2.jpg','/spot','启用',1),(3,'海岸落日浪漫','静待海边黄昏，落日铺满海面，听海风诉说温柔故事。','/banner/big/banner3.jpg','/banner/thumb/thumb3.jpg','/spot','启用',1),(4,'青山碧水画卷','群山环绕碧水潺潺，沉浸式走进山水国画般的自然风光。','/banner/big/banner4.jpeg','/banner/thumb/thumb4.jpeg','/spot','启用',1),(12,'哈哈','哈哈哈哈哈','/banner/big/banner5.jpg','/banner/thumb/thumb5.jpg','/spot','禁用',0);
/*!40000 ALTER TABLE `banner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `guideId` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `avatar` varchar(100) DEFAULT NULL,
  `content` varchar(500) NOT NULL,
  `rating` decimal(3,1) DEFAULT NULL,
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_guide` (`guideId`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (2,2,1,'系统管理员','/admin-avatar.jpeg','kyky',5.0,'2026-11-13 21:32:26'),(3,1,19,'66','/assets/images/avatar1.jpeg','hhh',5.0,'2026-11-13 21:49:58'),(4,5,21,'777','/assets/images/avatar1.jpeg','好好好',5.0,'2026-11-16 20:42:51'),(5,6,21,'777','/assets/images/avatar1.jpeg','嗯嗯',2.0,'2026-11-16 20:43:19'),(6,2,22,'666','/assets/images/avatar4.jpeg','ky',5.0,'2026-11-16 21:56:15'),(7,33,24,'555','/assets/images/avatar7.jpeg','哈哈哈哈',5.0,'2026-11-17 13:08:43'),(8,6,25,'小黄','/assets/images/avatar1.jpeg','很好看',5.0,'2026-11-21 13:22:06'),(9,5,25,'小黄','/assets/images/avatar1.jpeg','可以可以',4.0,'2026-11-21 13:22:18');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `favorite` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `targetType` varchar(20) NOT NULL,
  `targetId` int(11) NOT NULL,
  `targetName` varchar(200) DEFAULT NULL,
  `targetImg` varchar(255) DEFAULT NULL,
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`userId`,`targetType`,`targetId`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
INSERT INTO `favorite` VALUES (1,1,'hotel',2,'张家界山间客栈','/h2.jpeg','2026-11-13 20:44:38'),(2,1,'guide',1,'重庆洪崖洞三日游','/assets/images/guide1.jpeg','2026-11-13 20:44:51'),(3,1,'spot',1,'张家界','/spot1.jpeg','2026-11-13 20:45:26'),(4,18,'spot',1,'张家界','/spot1.jpeg','2026-11-13 21:09:02'),(5,1,'guide',6,'辽宁大连滨海路二日游','/assets/images/guide6.jpeg','2026-11-13 23:00:17'),(6,21,'spot',2,'大理洱海','/spot2.jpeg','2026-11-16 20:42:37'),(7,21,'guide',5,'浙江乌镇西栅夜景二日游','/assets/images/guide5.jpeg','2026-11-16 20:42:42'),(8,21,'hotel',2,'张家界山间客栈','/h2.jpeg','2026-11-16 20:43:58'),(9,22,'guide',2,'桂林阳朔二日游攻略','/assets/images/guide2.jpeg','2026-11-16 21:56:11'),(10,24,'guide',33,'哈哈','/assets/images/guide9.png','2026-11-17 13:08:39'),(11,24,'hotel',33,'哈哈','/h1.jpeg','2026-11-17 13:09:46'),(12,25,'spot',1,'张家界','/spot1.jpeg','2026-11-21 13:21:48'),(13,25,'guide',6,'辽宁大连滨海路二日游','/assets/images/guide6.jpeg','2026-11-21 13:22:00'),(15,25,'hotel',2,'张家界山间客栈','/h2.jpeg','2026-11-21 13:26:03');
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `guide`
--

DROP TABLE IF EXISTS `guide`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `guide` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `author` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `views` int(11) NOT NULL DEFAULT '0',
  `rating` decimal(3,1) NOT NULL DEFAULT '0.0',
  `date` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `img` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `isFixed` tinyint(4) DEFAULT '0',
  `isRecommend` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1推荐到首页',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guide`
--

LOCK TABLES `guide` WRITE;
/*!40000 ALTER TABLE `guide` DISABLE KEYS */;
INSERT INTO `guide` VALUES (2,'桂林阳朔二日游攻略','小陈',1572,4.7,'2026-11-16 20:57:23.426','/assets/images/guide2.jpeg','/assets/images/avatar2.jpeg','自然风光','桂林阳朔二日游：D1抵达阳朔，乘坐竹筏游览漓江，欣赏两岸喀斯特峰林山水画卷，傍晚逛西街；D2骑行十里画廊，打卡遇龙河，感受山水田园风光，品尝桂林米粉、啤酒鱼。',1,1),(3,'西安古城三日游','小林',1227,4.6,'2025-06-10 09:50:30','/assets/images/guide3.jpeg','/assets/images/avatar3.jpeg','城市人文','西安古城三日游：D1参观大雁塔、大唐不夜城，感受盛唐夜景；D2游览秦始皇兵马俑博物馆，感受千年历史；D3骑行西安城墙，逛回民街，品尝肉夹馍、羊肉泡馍等陕西特色美食。',1,0),(4,'贵州荔波小七孔二日游','小周',986,4.5,'2025-06-12 16:05:42','/assets/images/guide4.jpeg','/assets/images/avatar4.jpeg','自然风光','贵州荔波小七孔二日游：D1游览小七孔古桥、卧龙潭，欣赏碧绿的山水湖泊；D2游览68级跌水瀑布、翠谷瀑布，徒步感受贵州原始山水，体验喀斯特秘境。',1,0),(5,'浙江乌镇西栅夜景二日游','小吴',877,4.7,'2026-11-13 21:31:19.074','/assets/images/guide5.jpeg','/assets/images/avatar5.jpeg','古城古镇','浙江乌镇西栅二日游：D1下午入园，漫步西栅水乡，傍晚看落日，夜晚欣赏临水古街灯火；D2早起看清晨安静的水乡，逛木心美术馆，品尝乌米饭、定胜糕。',1,1),(6,'辽宁大连滨海路二日游','小郑',628,4.4,'2026-11-13 21:31:45.073','/assets/images/guide6.jpeg','/assets/images/avatar6.jpeg','滨海休闲','大连滨海路二日游：D1沿滨海路自驾或徒步，打卡星海广场、燕窝岭，看海岸线风光；D2前往棒棰岛，看碧海蓝天，品尝海鲜大餐，感受海滨城市浪漫。',1,1),(7,'甘肃敦煌鸣沙山月牙泉三日游','小冯',512,4.8,'2025-06-20 20:15:50','/assets/images/guide7.jpeg','/assets/images/avatar7.jpeg','自然风光','敦煌鸣沙山月牙泉三日游：D1抵达敦煌，傍晚前往鸣沙山，骑骆驼看沙漠日落；D2参观莫高窟，感受千年壁画艺术；D3打卡月牙泉，欣赏沙漠绿洲奇观，品尝西北特色面食。',1,0),(8,'广西德天瀑布二日游','小何',431,4.3,'2025-06-22 14:42:08','/assets/images/guide8.jpeg','/assets/images/avatar8.jpeg','自然风光','广西德天瀑布二日游：D1前往德天跨国大瀑布，乘坐竹筏近距离观赏瀑布水景；D2游览明仕田园，乘坐竹筏欣赏喀斯特田园风光，品尝广西壮族特色美食。',1,0),(1,'重庆洪崖洞三日游','小李',2362,4.8,'2026-11-13 21:24:34.451','/assets/images/guide1.jpeg','/assets/images/avatar1.jpeg','城市人文','重庆洪崖洞三日游行程推荐：D1抵达重庆，打卡解放碑，傍晚游览洪崖洞，欣赏灯火璀璨的吊脚楼夜景；D2乘坐长江索道，前往磁器口古镇品尝特色小吃；D3打卡鹅岭二厂，感受山城独特的城市地貌，品尝火锅、小面等重庆美食。',1,0);
/*!40000 ALTER TABLE `guide` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel`
--

DROP TABLE IF EXISTS `hotel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hotel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `spotId` int(11) DEFAULT NULL,
  `nearSpot` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `distance` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `typeId` int(11) DEFAULT NULL,
  `address` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `priceRange` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `score` decimal(2,1) DEFAULT NULL,
  `desc` text COLLATE utf8mb4_unicode_ci,
  `img` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel`
--

LOCK TABLES `hotel` WRITE;
/*!40000 ALTER TABLE `hotel` DISABLE KEYS */;
INSERT INTO `hotel` VALUES (1,'张家界云天酒店',1,'张家界','0.3公里','酒店',1,'湖南张家界武陵源景区入口旁','260-480',4.7,'山景客房，景区接送，自助早餐，免费WiFi，停车场','/h1.jpeg'),(2,'张家界山间客栈',1,'张家界','0.5公里','客栈',2,'张家界天子山脚下','160-320',3.2,'农家土菜，观景露台，行李寄存，安静山景小院','/h2.jpeg'),(3,'洱海澜庭酒店',2,'大理洱海','0.2公里','酒店',1,'云南大理洱海生态廊道','220-420',2.5,'一线湖景，露台下午茶，免费骑行自行车，24h前台','/h3.jpeg'),(4,'大理洱语民宿',2,'大理洱海','0.4公里','民宿',3,'大理才村码头','180-360',4.9,'白族小院，观海落地窗，手工茶体验，庭院打卡','/h4.jpeg'),(5,'九寨云顶酒店',3,'九寨沟','0.6公里','酒店',1,'四川阿坝九寨沟口','280-520',3.8,'景区接送，自助晚餐，地暖，免费停车','/h5.jpeg'),(6,'九寨沟彩林度假村',3,'九寨沟','1.2公里','度假村',4,'九寨沟漳扎镇','320-580',1.8,'藏式风情，温泉泡池，藏餐体验，森林景观','/h6.jpeg'),(7,'鼓浪屿海屿酒店',4,'厦门鼓浪屿','0.3公里','酒店',1,'鼓浪屿龙头路','260-480',4.2,'海岛风情，临海客房，庭院下午茶，免费WiFi','/h7.jpeg'),(8,'鼓浪屿花屿民宿',4,'厦门鼓浪屿','0.7公里','民宿',3,'鼓浪屿内厝澳','200-380',2.9,'复古南洋风，花园小院，近沙滩，安静不嘈杂','/h8.jpeg'),(9,'黄山云顶酒店',5,'黄山','0.8公里','酒店',1,'黄山市汤口镇','300-550',3.5,'登山向导，山景房，自助早餐，行李寄存','/h9.jpeg'),(10,'黄山徽韵民宿',5,'黄山','0.4公里','民宿',3,'黄山脚下徽派古村','220-420',4.5,'徽派古建筑，农家菜，庭院品茶，写生友好','/h10.jpeg'),(11,'西湖雅园酒店',6,'杭州西湖','0.5公里','酒店',1,'杭州市西湖区杨公堤28号','280-520',1.2,'湖景房，私家花园，中式下午茶，游船服务，健身房','/h11.jpeg'),(12,'西湖荷风民宿',6,'杭州西湖','0.6公里','民宿',3,'杭州西湖边龙井村','200-380',4.0,'江南庭院，龙井品茶，近苏堤，安静清幽','/h12.jpeg'),(13,'西安长安酒店',7,'西安城墙','0.3公里','酒店',1,'西安城墙南门附近','240-460',3.0,'古城景观，近钟鼓楼，中式装修，24h前台','/h13.jpeg'),(14,'西安城墙青旅',7,'西安城墙','0.2公里','青旅',5,'西安城墙永宁门旁','80-220',2.2,'多人间床位，公共客厅，行李寄存，结伴旅行交流','/h14.jpeg'),(15,'迪士尼童话酒店',8,'上海迪士尼乐园','度假区内','酒店',1,'上海浦东迪士尼度假区','420-780',4.6,'主题亲子房，乐园接送，卡通早餐，儿童游乐区','/h15.jpeg'),(16,'迪星民宿',8,'上海迪士尼乐园','0.6公里','民宿',3,'迪士尼周边度假区','260-480',3.6,'亲子卡通房间，免费接送乐园，自助洗衣','/h16.jpeg'),(17,'广州塔观景酒店',9,'广州塔','0.4公里','酒店',1,'广州海珠区珠江新城','300-560',2.7,'落地窗俯瞰广州塔，健身房，自助早餐，近珠江夜游码头','/h17.jpeg'),(18,'珠江夕照民宿',9,'广州塔','0.5公里','民宿',3,'广州琶洲珠江边','220-420',4.3,'江景阳台，城市夜景，交通便利，地铁直达','/h18.jpeg'),(19,'宏村望山酒店',10,'宏村','0.2公里','酒店',1,'安徽黄山宏村景区入口','260-480',1.5,'徽派建筑，南湖观景，农家徽菜，免费停车','/h19.jpeg'),(20,'宏村徽墨民宿',10,'宏村','景区内','民宿',3,'宏村景区内古民居','200-380',3.9,'百年古宅，庭院池塘，写生配套，安静古村氛围','/h20.jpeg'),(21,'紫禁城皇家酒店',11,'故宫博物院','0.3公里','酒店',1,'北京市东城区景山前街8号','360-680',4.8,'免费WiFi,24小时前台,室内游泳池,健身中心,中西餐厅','/h21.jpeg'),(22,'京城古巷民宿',11,'故宫博物院','0.5公里','民宿',3,'北京南锣鼓巷附近','240-460',3.3,'老北京四合院，胡同文化体验，近天安门广场','/h22.jpeg'),(23,'方特欢乐酒店',12,'芜湖方特东方神画','园区旁','酒店',1,'安徽芜湖鸠江区方特园区旁','220-420',2.1,'亲子主题房，乐园接送，自助早餐，儿童游乐区','/h23.jpeg'),(24,'方特梦遇客栈',12,'芜湖方特东方神画','0.6公里','客栈',2,'芜湖方特周边','160-320',3.7,'国风主题房间，平价住宿，免费停车，乐园向导','/h24.jpeg');
/*!40000 ALTER TABLE `hotel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel_type`
--

DROP TABLE IF EXISTS `hotel_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hotel_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel_type`
--

LOCK TABLES `hotel_type` WRITE;
/*!40000 ALTER TABLE `hotel_type` DISABLE KEYS */;
INSERT INTO `hotel_type` VALUES (1,'酒店'),(2,'客栈'),(3,'民宿'),(4,'度假村'),(5,'青旅');
/*!40000 ALTER TABLE `hotel_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderNo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `userId` int(11) NOT NULL,
  `ticketId` int(11) DEFAULT NULL,
  `orderType` int(11) NOT NULL DEFAULT '1',
  `hotelId` int(11) DEFAULT NULL,
  `count` int(11) NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP,
  `travelDate` date DEFAULT NULL,
  `totalPrice` decimal(10,2) DEFAULT NULL,
  `person_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '出行人姓名',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系手机号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `orderNo` (`orderNo`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES (43,'DD1794048474718',11,7,1,NULL,9,'2','2026-11-07 18:47:55','2026-11-17',990.00,'jj','13211012101'),(48,'DD1794574479251',18,3,1,NULL,2,'3','2026-11-13 20:54:43','2026-11-15',120.00,'hh','13211111111'),(49,'DD1794574516166',18,NULL,2,2,2,'1','2026-11-13 20:55:19','2026-11-15',320.00,'111','13211111111'),(50,'DD1794833047075',21,NULL,2,2,6,'1','2026-11-16 20:44:10','2026-11-19',960.00,'777','13223222123'),(51,'DD1794833073878',21,1,1,NULL,2,'3','2026-11-16 20:44:36','2026-11-19',240.00,'哈哈','13221123212'),(52,'DD1795001148833',21,33,1,NULL,1,'1','2026-11-18 19:25:54','2026-11-21',123.00,'哈哈','13212321232'),(53,'DD1795238771374',25,NULL,2,2,6,'1','2026-11-21 13:26:15','2026-11-24',960.00,'user','13670450915'),(54,'DD1795238799084',25,3,1,NULL,2,'3','2026-11-21 13:26:43','2026-11-24',120.00,'小黄','13222212322'),(55,'DD1795238825412',25,2,1,NULL,2,'2','2026-11-21 13:27:09','2026-11-24',120.00,'小黄','13221232211'),(56,'DD1795243602260',25,NULL,2,5,6,'1','2026-11-21 14:46:46','2026-11-21',1680.00,'user','13210121012');
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spot`
--

DROP TABLE IF EXISTS `spot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `area` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `openTime` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `playTime` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `level` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tags` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `categoryId` int(11) DEFAULT NULL,
  `categoryName` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `desc` text COLLATE utf8mb4_unicode_ci,
  `img` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '景点详情页长篇介绍',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spot`
--

LOCK TABLES `spot` WRITE;
/*!40000 ALTER TABLE `spot` DISABLE KEYS */;
INSERT INTO `spot` VALUES (1,'张家界','湖南·张家界',120.00,'07:00 - 18:00','1-2天','5A级景区','自然风光,推荐景点,拍照打卡,亲子出游',NULL,1,'自然风光','奇峰三千，秀水八百，国家5A级景区','/spot1.jpeg','1','张家界位于湖南西北部，是世界自然遗产、国家5A级旅游景区。这里以独特的石英砂岩峰林地貌闻名于世，三千奇峰拔地而起，八百溪流蜿蜒曲折。景区内云雾缭绕，一步一景，核心景点包含袁家界、天子山、金鞭溪，《阿凡达》的悬浮山原型便取自此处，是国内不可多得的山岳型自然风光胜地。'),(2,'大理洱海','云南·大理',80.00,'全天开放','1天','4A级景区','自然风光,拍照打卡,亲子出游',NULL,1,'自然风光','苍山伴洱海，风花雪月的浪漫之地','/spot2.jpeg','1','洱海坐落于大理苍山脚下，是云南第二大高原淡水湖。湖水澄澈蔚蓝，环湖分布着众多古村落与网红打卡点。你可以沿环湖公路骑行，欣赏日出、落日与苍山倒影，感受风花雪月的大理浪漫。喜洲古镇、双廊、生态廊道都是热门打卡地，适合慢节奏休闲旅行。'),(3,'九寨沟','四川·阿坝',169.00,'08:30 - 17:00','1天','5A级景区','自然风光,拍照打卡',NULL,1,'自然风光','彩林碧水，被誉为人间童话世界','/spot3.jpeg','1','九寨沟位于四川阿坝州，是世界自然遗产、国家5A级景区。以翠海、叠瀑、彩林、雪峰、藏情“五绝”著称。湖水富含矿物质，在光线折射下呈现蓝、绿、青等缤纷色彩，被称作“童话世界”。秋季彩林漫山，与海子相互映衬，景色震撼，是国内顶级自然风光目的地。'),(4,'厦门鼓浪屿','福建·厦门',90.00,'全天开放','1天','5A级景区','历史文化,拍照打卡,亲子出游',NULL,2,'历史文化','海岛风情，万国建筑，文艺打卡胜地','/spot4.jpeg','1','鼓浪屿是厦门的海上文艺小岛，世界文化遗产。岛上没有机动车，遍布南洋风格与欧式老别墅，被称为万国建筑博览。漫步在蜿蜒小巷，随处可见老榕树、三角梅，钢琴博物馆藏有众多古董钢琴。适合悠闲漫步，感受海岛慢生活，文艺氛围感十足。'),(5,'黄山','安徽·黄山市',190.00,'06:30 - 16:30','1-2天','5A级景区','自然风光,徒步登山,拍照打卡',NULL,1,'自然风光','黄山，中国十大名山之一，以奇松、怪石、云海、温泉“四绝”著称于世，兼具自然与深厚文化底蕴','/spot5.jpeg','1','黄山是中国十大名山之一，世界文化与自然双重遗产，以奇松、怪石、云海、温泉“四绝”闻名天下。迎客松是黄山标志性景观。山峰险峻雄奇，雨后云海翻涌，日出、晚霞景色壮丽。四季景色各不相同，春有山花、夏有清凉、秋有红叶、冬有雾凇。'),(6,'杭州西湖','浙江杭州',0.00,'全天开放','半天-1天','5A级景区','历史文化,免费景点,亲子出游',NULL,2,'历史文化','西湖位于浙江省杭州市，是首批国家重点风景名胜区之一，有“人间天堂”的美誉，有“淡妆浓抹总相宜”的绝美风姿','/spot6.jpeg','1','杭州西湖是世界文化遗产，免费开放的城市湖泊。湖山相依，三面环山一面城，有苏堤春晓、雷峰夕照、三潭印月等西湖十景。自古有“上有天堂，下有苏杭”的美誉，文人墨客留下无数诗词。四季风光各有韵味，适合环湖散步、游船，感受江南温婉意境。'),(7,'西安城墙','陕西·西安',54.00,'08:00 - 22:00','2-3小时','5A级景区','历史文化,夜景推荐,拍照打卡',NULL,2,'历史文化','中国现存规模最大、保存最完整的古代城垣，见证千年古都变迁','/spot7.png','1','西安城墙是中国现存规模最大、保存最完整的古代城垣，始建于明代。城墙环绕西安老城，可骑行或者步行在城墙之上，俯瞰古城街巷。傍晚时分，夕阳洒在青砖上，氛围感浓厚，是感受西安千年古都历史底蕴的必打卡景点。'),(8,'上海迪士尼乐园','上海',399.00,'08:30 - 20:30','1天','5A级景区','主题乐园,亲子出游,夜景推荐',NULL,3,'主题公园','国内知名主题乐园，梦幻童话场景，适合亲子游玩','/spot8.png','1','上海迪士尼度假区是国内首座迪士尼主题乐园。园内拥有六大主题园区，有经典城堡、花车巡游、夜间烟花秀。适合亲子游玩、年轻人打卡，沉浸式体验童话世界，众多热门项目刺激有趣，是国内热门的主题度假目的地。'),(9,'广州塔','广东·广州',150.00,'09:30 - 22:30','2-3小时','4A级景区','城市地标,夜景推荐,拍照打卡',NULL,4,'城市地标','广州城市地标，又称小蛮腰，登高可俯瞰城市夜景','/spot9.png','1','广州塔昵称“小蛮腰”，是广州城市地标。塔身造型纤细灵动，登高可以俯瞰整个珠江两岸城市风光。塔内有高空摩天轮、极速云霄等游乐项目，夜晚灯光绚丽，搭配珠江夜游，感受广州现代化都市夜景。'),(10,'宏村','安徽·黄山',94.00,'07:30 - 17:30','半天','5A级景区','乡村民俗,拍照打卡,历史文化',NULL,5,'乡村民俗','徽派古村落代表，白墙黛瓦，水墨田园','/spot10.png','1','宏村是世界文化遗产，徽派古村落代表。整个村子依水而建，牛形水系布局，白墙黑瓦马头墙倒映在南湖之中，是国画般的江南古村。村内保留大量明清古民居，美术写生圣地，清晨薄雾笼罩古村，景色静谧优美。'),(11,'故宫博物院','北京',60.00,'08:30 - 17:00','3-4小时','5A级景区','历史文化,亲子出游,拍照打卡',NULL,2,'历史文化','明清皇家宫殿，世界文化遗产，拥有海量珍贵文物','/spot11.png','1','北京故宫旧称紫禁城，明清两代皇家宫殿，世界文化遗产。红墙黄瓦，宫殿宏伟，馆藏文物百万件。太和殿、乾清宫、御花园等建筑气势磅礴，行走其间感受明清皇家历史底蕴，是国内最具代表性的历史文化景点。'),(12,'芜湖方特东方神画','安徽·芜湖',280.00,'09:30 - 17:30','1天','4A级景区','主题乐园,亲子出游',NULL,3,'主题公园','以中华传统文化神话故事为主题，沉浸式体验','/spot12.jpeg','1','芜湖方特东方神画是大型国风主题乐园。将中国神话传说、历史故事融入游乐项目，采用4D特效、舞台表演等形式，讲述女娲补天、牛郎织女等传统故事。既有刺激过山车，也有适合全家观赏的国风演艺，国风氛围感很强。'),(30,'哈哈','哈哈',123.00,'哈哈','哈哈','5A级景区','哈哈哈',NULL,1,'自然风光','哈哈哈','http://localhost:8080/uploads/553705df05954f40b601607d811381bd.jpeg','0','哈哈哈哈哈哈哈哈哈哈哈');
/*!40000 ALTER TABLE `spot` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spot_category`
--

DROP TABLE IF EXISTS `spot_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spot_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort` int(11) DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spot_category`
--

LOCK TABLES `spot_category` WRITE;
/*!40000 ALTER TABLE `spot_category` DISABLE KEYS */;
INSERT INTO `spot_category` VALUES (1,'自然风光',1,'启用'),(2,'历史文化',2,'启用'),(3,'主题公园',3,'启用'),(4,'城市地标',4,'启用'),(5,'乡村民俗',5,'启用'),(15,'哈哈',6,'禁用');
/*!40000 ALTER TABLE `spot_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket`
--

DROP TABLE IF EXISTS `ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ticket` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `spot` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `priceNow` decimal(10,2) DEFAULT NULL,
  `priceOld` decimal(10,2) DEFAULT NULL,
  `validDesc` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `desc` text COLLATE utf8mb4_unicode_ci,
  `stock` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket`
--

LOCK TABLES `ticket` WRITE;
/*!40000 ALTER TABLE `ticket` DISABLE KEYS */;
INSERT INTO `ticket` VALUES (1,'张家界-成人票','张家界','成人票',120.00,150.00,'购买后7天内有效','张家界标准成人票，含景区观光车，不含索道费用，凭身份证刷证入园，适合普通游客游览核心景区。',900),(2,'张家界-学生票','张家界','学生票',60.00,150.00,'购买后7天内有效，需出示学生证','张家界学生优惠票，含景区观光车，不含索道费用，入园时需要核验本人有效学生证。',458),(3,'张家界-老人票','张家界','老人票',60.00,150.00,'购买后7天内有效，凭身份证入园','60周岁以上老人专属优惠票，含景区观光车，不含索道，凭本人身份证直接核验入园。',300),(4,'大理洱海-环湖联票','大理洱海','联票',80.00,100.00,'购买后3天内有效','包含洱海游船+多个沿岸小景点门票，3天内可多次使用，适合环湖自驾、骑行游览。',1200),(5,'九寨沟-成人票','九寨沟','成人票',169.00,220.00,'购买后2天内有效，可二次入园','含景区观光大巴车票，不含景区内二次消费，门票支持二次入园，深度游览九寨沟海子景观。',650),(6,'九寨沟-学生票','九寨沟','学生票',110.00,160.00,'购买后2天内有效，需出示学生证','九寨沟学生优惠票，含观光大巴车票，入园核验学生证，支持二次进沟游览。',300),(7,'九寨沟-老人票','九寨沟','老人票',110.00,160.00,'购买后2天内有效，凭身份证入园','60周岁以上老人优惠票，含观光大巴车票，凭身份证入园，支持二次进沟。',270),(8,'厦门鼓浪屿-成人票','厦门鼓浪屿','成人票',90.00,110.00,'仅限当日有效','包含轮渡船票+五大核心景点门票，当日一次性使用，乘船登岛后游览菽庄花园、日光岩等景点。',900),(9,'厦门鼓浪屿-学生票','厦门鼓浪屿','学生票',50.00,80.00,'仅限当日有效，需出示学生证','学生优惠票，含轮渡船票+五大核心景点，登岛入园需要核验本人学生证。',450),(10,'厦门鼓浪屿-老人票','厦门鼓浪屿','老人票',50.00,80.00,'仅限当日有效，凭身份证入园','60周岁以上老人优惠票，含轮渡船票+五大景点，凭身份证核验登岛。',330),(11,'黄山-成人票','黄山','成人票',190.00,230.00,'购买后2天内有效，可二次进山','黄山景区大门票，2天内可二次进山游览，不含索道、缆车费用，观赏奇松怪石云海。',790),(12,'黄山-学生票','黄山','学生票',115.00,160.00,'购买后2天内有效，需出示学生证','黄山学生优惠大门票，2天可二次进山，入园核验学生证，不含索道缆车。',397),(13,'黄山-老人票','黄山','老人票',115.00,160.00,'购买后2天内有效，凭身份证入园','60周岁以上老人优惠大门票，2天可二次进山，凭身份证入园，不含索道缆车。',310),(14,'杭州西湖-游船联票','杭州西湖','联票',40.00,60.00,'仅限当日有效','包含环湖游船+三潭印月上岛门票，当日一次性使用，乘船观赏西湖十景，打卡人民币背景。',1800),(15,'西安城墙-成人票','西安城墙','成人票',54.00,70.00,'购买后24小时内有效，可多次上下城墙','西安明城墙门票，24小时内可多次上下城墙，白天看古城风貌，夜晚观赏城墙灯光夜景。',1100),(16,'西安城墙-学生票','西安城墙','学生票',27.00,35.00,'购买后24小时内有效，需出示学生证','西安城墙学生优惠票，24小时可多次上下城墙，入园核验学生证。',520),(17,'西安城墙-老人票','西安城墙','老人票',27.00,35.00,'购买后24小时内有效，凭身份证入园','60周岁以上老人优惠票，24小时可多次上下城墙，凭身份证核验入园。',410),(18,'上海迪士尼乐园-成人票','上海迪士尼乐园','成人票',399.00,459.00,'仅限游玩当日有效','乐园单日通玩门票，园内所有游乐项目免费体验，不含尊享卡、餐饮、纪念品等额外消费。',450),(19,'上海迪士尼乐园-儿童票','上海迪士尼乐园','儿童票',299.00,349.00,'仅限游玩当日有效，1.0-1.4米儿童使用','1.0-1.4米儿童专属单日通玩票，全部游乐项目免费体验，入园需要核对儿童身高。',360),(20,'上海迪士尼乐园-老人票','上海迪士尼乐园','老人票',299.00,349.00,'仅限游玩当日有效，65周岁以上老人','65周岁以上老人单日通玩门票，园内游乐项目免费体验，入园核验身份证年龄。',220),(21,'广州塔-成人票','广州塔','成人票',150.00,198.00,'仅限游玩当日有效','广州塔108层观光厅门票，打卡城市高空景观，不含摩天轮、速降等游乐项目。',560),(22,'广州塔-儿童票','广州塔','儿童票',90.00,120.00,'仅限游玩当日有效，1.2-1.5米儿童使用','1.2-1.5米儿童观光票，可上至108层观光厅，不含塔顶游乐项目。',280),(23,'广州塔-老人票','广州塔','老人票',90.00,120.00,'仅限游玩当日有效，60周岁以上老人','60周岁以上老人观光票，可上至108层观光厅，凭身份证核验入园。',210),(24,'宏村-成人票','宏村','成人票',94.00,110.00,'购买后3天内可多次进出','宏村古村落大门票，3天内可以反复进出景区，参观徽派古民居、南湖、月沼等经典景点。',720),(25,'宏村-学生票','宏村','学生票',47.00,55.00,'购买后3天内可多次进出，需出示学生证','宏村学生优惠票，3天内多次进出古村，参观徽派古建筑，入园核验学生证。',350),(26,'宏村-老人票','宏村','老人票',47.00,55.00,'购买后3天内可多次进出，凭身份证入园','60周岁以上老人优惠票，3天内多次进出宏村古村落，凭身份证核验入园。',290),(27,'故宫博物院-成人票','故宫博物院','成人票',60.00,80.00,'购买后30天内有效','故宫博物院标准成人票，含基本参观项目，不包含珍宝馆、钟表馆等特殊展厅，凭身份证入园。',994),(28,'故宫博物院-学生票','故宫博物院','学生票',40.00,60.00,'购买后30天内有效，需出示学生证','故宫博物院学生优惠票，含基础参观项目，不含珍宝馆、钟表馆，入园核验本人学生证。',490),(29,'故宫博物院-老人票','故宫博物院','老人票',40.00,60.00,'购买后30天内有效，凭身份证入园','60周岁以上老人优惠票，含基础参观项目，不含珍宝馆、钟表馆，凭身份证核验入园。',380),(30,'芜湖方特东方神画-成人票','芜湖方特东方神画','成人票',280.00,320.00,'仅限游玩当日有效','方特东方神画单日通玩票，园内全部游乐项目免费体验，感受中国神话主题沉浸式项目。',380),(31,'芜湖方特东方神画-儿童票','芜湖方特东方神画','儿童票',190.00,230.00,'仅限游玩当日有效，1.1-1.4米儿童使用','1.1-1.4米儿童单日通玩票，园内所有游乐项目免费体验，入园核对儿童身高。',210),(32,'芜湖方特东方神画-老人票','芜湖方特东方神画','老人票',190.00,230.00,'仅限游玩当日有效，65周岁以上老人','65周岁以上老人单日通玩票，园内游乐项目免费体验，入园核验身份证年龄。',160);
/*!40000 ALTER TABLE `ticket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1启用 0禁用',
  `lastLoginTime` datetime DEFAULT NULL,
  `loginCount` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','e10adc3949ba59abbe56e057f20f883e','系统管理员','13800001111','管理员','/admin-avatar.jpeg',1,'2026-11-21 13:19:47',21),(23,'888','d6b90d9091f28ff09a975078c40743f9','888','13212321232','普通用户','/assets/images/avatar1.jpeg',0,'2026-11-17 13:02:22',1),(22,'666','801ff3162e60810dbe325662a12a2d67','666','13221232123','普通用户','/assets/images/avatar4.jpeg',1,'2026-11-16 21:51:47',1),(21,'777','eca8939694a9e2d57236d47634f77b6a','777','13223222123','普通用户','/assets/images/avatar1.jpeg',1,'2026-11-18 18:40:51',6),(24,'555','64a0166bff491fe0447776ff13cba356','555','13212321232','普通用户','/assets/images/avatar7.jpeg',1,'2026-11-17 13:03:32',1),(25,'user','3e6c7d141e32189c917761138b026b74','小黄','13210121012','普通用户','/assets/images/avatar1.jpeg',1,'2026-11-21 13:28:48',2);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-21 15:19:58
