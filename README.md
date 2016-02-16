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

1. INTRODUCTION

This software package can be used to analyze and download Blogger profiles, blogs and associated posts. The data is broken down into parts (e.g. user gender, blog title etc.) and stored in respective MySQL database tables/columns.

===========================

2. CODE

The software is organized as a Maven project with all dependencies listed in the pom.xml file. The resources folder include the "create.sql" file which contains the statements that can create the corresponding MySQL datase schema.


The src/main/java/dal package includes the model classes of the Blogger data. Each class in this package corresponds to a MySQL tables in the database.

The src/main/java/scraper/scr package contains three classes, each containing an independent main function.

===========================

2. HOW TO USE BLOG-SCRAPPER


