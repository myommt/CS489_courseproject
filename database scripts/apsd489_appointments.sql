CREATE DATABASE  IF NOT EXISTS `apsd489` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `apsd489`;
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: apsd489
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `appointment_id` int NOT NULL AUTO_INCREMENT,
  `appointmentDateTime` datetime(6) NOT NULL,
  `appointmentStatus` varchar(20) NOT NULL,
  `appointmentType` varchar(50) NOT NULL,
  `dentist_id` int NOT NULL,
  `patient_id` int NOT NULL,
  `surgerylocation` int NOT NULL,
  PRIMARY KEY (`appointment_id`),
  KEY `FKqsnula0nj86s67otibu2j2hsv` (`dentist_id`),
  KEY `FK8exap5wmg8kmb1g1rx3by21yt` (`patient_id`),
  KEY `FKn58n9pp64gemhmj1m89431img` (`surgerylocation`),
  CONSTRAINT `FK8exap5wmg8kmb1g1rx3by21yt` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`),
  CONSTRAINT `FKn58n9pp64gemhmj1m89431img` FOREIGN KEY (`surgerylocation`) REFERENCES `surgerylocations` (`surgerylocation_id`),
  CONSTRAINT `FKqsnula0nj86s67otibu2j2hsv` FOREIGN KEY (`dentist_id`) REFERENCES `dentists` (`dentist_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (1,'2025-10-23 16:00:00.000000','COMPLETED','ONLINE',1,1,1),(2,'2025-10-30 15:30:00.000000','CANCELLED','ONLINE',1,1,2),(3,'2025-11-07 16:00:00.000000','CHECKOUT','ONLINE',1,1,3),(4,'2025-10-26 01:13:00.000000','CONFIRMED','Call-in',3,3,1),(5,'2025-10-21 14:00:00.000000','Confirmed','Call-in',3,3,2),(6,'2025-10-21 09:31:00.000000','Confirmed','Call-in',3,1,2),(7,'2025-10-21 10:45:00.000000','SCHEDULED','ONLINE',3,1,2),(8,'2025-10-25 14:05:00.000000','CHECKOUT','ONLINE',3,1,2),(9,'2025-11-08 12:00:00.000000','CHECKOUT','ONLINE',4,3,1),(10,'2025-10-25 11:25:00.000000','CONFIRMED','ONLINE',4,3,1),(11,'2025-10-29 14:00:00.000000','CANCELLED','ONLINE',1,1,1),(12,'2025-10-27 14:36:00.000000','SCHEDULED','Call-in',3,1,4),(13,'2025-10-28 14:37:00.000000','SCHEDULED','Call-in',3,1,2),(14,'2025-10-29 14:38:00.000000','SCHEDULED','ONLINE',3,1,1),(15,'2025-11-03 14:39:00.000000','SCHEDULED','ONLINE',3,1,4),(16,'2025-10-23 14:44:00.000000','SCHEDULED','ONLINE',3,1,3),(17,'2025-10-29 14:58:00.000000','CONFIRMED','ONLINE',3,1,2);
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-21 18:07:47
