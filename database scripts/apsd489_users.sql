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
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `accountNonExpired` bit(1) NOT NULL,
  `accountNonLocked` bit(1) NOT NULL,
  `credentialsNonExpired` bit(1) NOT NULL,
  `email` varchar(255) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `firstName` varchar(255) NOT NULL,
  `lastName` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `dentist_id` int DEFAULT NULL,
  `patient_id` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  UNIQUE KEY `UKc065dbd8q3wbcj746u2r9b2x7` (`dentist_id`),
  UNIQUE KEY `UK3nyemjrjidm9fenwmb23uc9os` (`patient_id`),
  CONSTRAINT `FK9tgv5fna8xoaonx2n1lom2ok0` FOREIGN KEY (`dentist_id`) REFERENCES `dentists` (`dentist_id`),
  CONSTRAINT `FKa98w1ceplk9u7a6x4w9xn29jd` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`)
) ENGINE=InnoDB AUTO_INCREMENT=356 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,_binary '',_binary '',_binary '','admin@dentalsurgery.com',_binary '','System','Administrator','$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq','admin',NULL,NULL),(2,_binary '',_binary '',_binary '','admin2@dentalsurgery.com',_binary '','System','Administrator','$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq','admin2',NULL,NULL),(3,_binary '',_binary '',_binary '','test@dentalsurgery.com',_binary '','Test','User','$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq','testuser',1,NULL),(4,_binary '',_binary '',_binary '','jim.b@gmail.com',_binary '','Jim','Brown','$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq','jim.b@gmail.com',NULL,1),(5,_binary '',_binary '',_binary '','tony.smith@southwest.dentists.org',_binary '','Tony','Smith','$2a$10$.XjD86rqkylsO4FQ5eG/pu204QrVdCpM1ixVkA0Rli61GPC07tFFq','tony.smith@southwest.dentists.org',NULL,2);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
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
