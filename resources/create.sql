/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `blogs` (
  `url` varchar(100) NOT NULL,
  `description` text,
  `name` text,
  `published` bigint(20) DEFAULT NULL,
  `updated` bigint(20) DEFAULT NULL,
  `locale_country` text,
  `locale_language` text,
  `locale_variant` text,
  PRIMARY KEY (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `blogs_posts` (
  `blog_url` varchar(100) NOT NULL DEFAULT '',
  `post_url` varchar(500) NOT NULL DEFAULT '',
  PRIMARY KEY (`blog_url`,`post_url`),
  KEY `post_url` (`post_url`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `posts` (
  `url` varchar(500) NOT NULL,
  `title` text,
  `content` longtext,
  `published` bigint(20) DEFAULT NULL,
  `author_url` text,
  `location_latitude` decimal(10,0) DEFAULT NULL,
  `location_longitude` decimal(10,0) DEFAULT NULL,
  `location_name` text,
  `location_span` text,
  PRIMARY KEY (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profiles` (
  `url` varchar(100) NOT NULL,
  `gender` tinyint(1) DEFAULT NULL,
  `industry` text,
  `occupation` text,
  `city` text,
  `state` text,
  `country` text,
  `introduction` text,
  `interests` text,
  `movies` text,
  `music` text,
  `books` text,
  `name` text,
  `image_url` text,
  `email` text,
  `web_page_url` text,
  `instant_messaging_service` text,
  `instant_messaging_username` text,
  PRIMARY KEY (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profiles_blogs` (
  `profile_url` varchar(100) NOT NULL DEFAULT '',
  `blog_url` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`profile_url`,`blog_url`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profiles_blogs_followed` (
  `profile_url` varchar(100) NOT NULL DEFAULT '',
  `blog_url` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`profile_url`,`blog_url`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
