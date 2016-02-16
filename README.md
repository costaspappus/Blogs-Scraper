# Blog-Scraper
A software package for analyzing and downloading Blogger profiles, blogs and posts.

                          February 15, 2016

                         Konstantinos Pappas
                Language and Information Technologies
                        University of Michigan

                  Contact: pappus at umich dot edu

CONTENTS

1. Introduction
2. Code
3. How to use Blog-Scrapper


===========================

## INTRODUCTION

This software package can be used to analyze and download Blogger profiles, blogs and associated posts. The data is broken down into parts (e.g. user gender, blog title etc.) and stored in respective MySQL database tables/columns.

===========================

## CODE

The software is organized as a Maven project with all dependencies listed in the pom.xml file. The resources folder include the `create.sql` file which contains the statements that can create the corresponding MySQL datase schema.


The `src/main/java/dal` package includes the model classes of the Blogger data. Each class in this package corresponds to a MySQL tables in the database.

The `src/main/java/scraper/scr` package contains three classes, each containing an independent main function.

===========================

## HOW TO USE BLOG-SCRAPPER

**STEP 0**

Before running any of the code make sure that the MySQL database is set up by executing the statements in the `create.sql` file.

**STEP 1**

First, execute the `ScrapeSearchPageByLocation.java` file. This will create a text file with Blogger profile urls. Before running this code, make sure you have inserted an appropriate file path on lines 40 and 41.

**STEP 2**

Next, execute the `ScrapeProfiles.java` file. This code will read the profile urls identified in Step 0, download, analyze the profile elements of a Blogger user, and retrieve all associated blog urls. Finally, it will insert this information in the MySQL dataset in the profiles table.

Before running this code, make sure you have inserted appropriate values in lines 26-32.

Note that this program will download 90 profiles per half an hour therefore, please allow adequate time for all the profiles to be downloaded.

**STEP 3**

Finally, execute the `ScrapeBlogs.java` file. This code will read all the blog urls from the MySQL database which do not contain any associated post yet, and download their associated posts.

Before running this code, make sure you have inserted appropriate values in lines 29, 32, and 38.

Specify the max number of posts per blog you need at line 52.






