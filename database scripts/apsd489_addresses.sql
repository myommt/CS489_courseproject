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
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addresses` (
  `address_id` int NOT NULL AUTO_INCREMENT,
  `street` varchar(100) NOT NULL,
  `city` varchar(100) DEFAULT NULL,
  `state` varchar(100) DEFAULT NULL,
  `zipcode` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`address_id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` VALUES (1,'123 Main Street','Fairfield','IA','52557'),(2,'456 Oak Avenue','Des Moines','IA','50309'),(3,'789 Cedar Lane','Iowa City','IA','52240'),(29,'123 Main St','Fairfield','IA','52556'),(30,'456 Elm St','Los Angeles','CA','90001'),(37,'300 Jerferrson Avenue','Fairfield','IA','52556'),(38,'1100 Main St','Fairfield','IA','52556'),(39,'Newton Road','Iowa City','Iowa','52240'),(40,'321 Lowe Ave','Mt. Pleasant','IA','48804'),(41,'Newton Road','Iowa City','IA','52240'),(43,'East Lowe Ave','Fairfield','IA','52556'),(44,'849 N Court Street','Fairfield','IA','52556'),(45,'1232 N Court Street','Fairfield','IA','52556'),(46,'122 N Court Street','Fairfield','IA','52556'),(47,'123 Main Street','Healthcare City','HC','12345'),(48,'N Main Stree','Ottumwa','IA','52501'),(49,'123 W Griggs Ave','Fairfield','IA','52556'),(50,'10101 Jefferson Blvd.','Culver City','CA','90232'),(51,'2nd Ave','Mt Pleasant','IA','48804'),(52,'Fairfield Iowa','Fairfield','Iowa','52556'),(53,'Fairfield Iowa','Fairfield','IA','52556'),(54,'112 N Court Street','Fairfield','IA','52556'),(56,'Main Street','Richland','IA','99338'),(57,'West Burlington','Burlington','IA','52655');
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
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
