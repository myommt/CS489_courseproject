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
-- Table structure for table `surgerylocations`
--

DROP TABLE IF EXISTS `surgerylocations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `surgerylocations` (
  `surgerylocation_id` int NOT NULL AUTO_INCREMENT,
  `contactNumber` varchar(255) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `addressId` int DEFAULT NULL,
  `address_id` int DEFAULT NULL,
  PRIMARY KEY (`surgerylocation_id`),
  KEY `FKiho355rigtlpw7pixo5sdu6n` (`addressId`),
  KEY `FKeg4qcf5vd8bgycqaqmssj8qfo` (`address_id`),
  CONSTRAINT `FKeg4qcf5vd8bgycqaqmssj8qfo` FOREIGN KEY (`address_id`) REFERENCES `addresses` (`address_id`),
  CONSTRAINT `FKiho355rigtlpw7pixo5sdu6n` FOREIGN KEY (`addressId`) REFERENCES `addresses` (`address_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `surgerylocations`
--

LOCK TABLES `surgerylocations` WRITE;
/*!40000 ALTER TABLE `surgerylocations` DISABLE KEYS */;
INSERT INTO `surgerylocations` VALUES (1,'(641) 555-1234','Main Dental Clinic',1,54),(2,'(515) 555-5678','Downtown Dental Care',2,56),(3,'(319) 555-9012','University Dental Center',3,48),(4,'(319) 753-2093','Burlington Dental',NULL,57);
/*!40000 ALTER TABLE `surgerylocations` ENABLE KEYS */;
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
