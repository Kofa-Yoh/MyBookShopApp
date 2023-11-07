<h2 align="center">
    Book Shop Website
    <img alt="In progress" src="https://img.shields.io/badge/In%20progress-%23585858">
</h2>
<p align="center">
    <img alt="Java" src="https://img.shields.io/badge/Java-%23c74634">
    <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-%23589133">
    <img alt="Thymeleaf" src="https://img.shields.io/badge/Thymeleaf-%23005f0f">
    <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-%23699eca">
</p>

Educational project for the Spring course <img src="https://www.vectorlogo.zone/logos/springio/springio-icon.svg" alt="Spring Framework" width="15" height="15"/> on Skillbox.

The project represents the implementation of the website backend and the modification of the frontend. The site provides the ability to view lists of books, rate a book, user authorization, phone and email verification, user purchases of books.

> [!NOTE]
>The frontend files were provided by the course developers, but they were modified for some tasks.

In these readme: <a href="#structure">The main parts of the project (Structure)</a>,
<a href="#screenshots">Screenshorts</a>,
<a href="#stack">Technologies Stack</a>

### Structure 
*``indicating the main files``

#### Book Lists ``BookRepository.java``*
* Sorted by rating
* Sorted by publication date
* Sorted and filtered by user purchases
* Filtered by author
* Filtered by genre
* Filtered by tag
* Filtered by searching word

#### Book view
* Description, Authors, Tags, Price ``BooksController.java``
* Rating ``BookAssessmentService.java``
* Download links ``ResourceStorage.java``
* Reviews ``BookReviewService.java``
* Review assessment
* Buttons for postpone and buy the book ``Book2UserService.java``

#### User sign up, log in, log out ``SecurityConfig.java, AuthUserController.java``
* Via email or phone number using JWT
* Via facebook account using OAuth2
* Phone number verification with Twilio
* Email verification with Gmail

#### User profile
* User data changing
* User transactions and top up the account ``PaymentService``
* Book purchase with Robokassa

### Screenshots
<details><summary>Here</summary>
  
* Main page with recommended books  
![books](https://github.com/Kofa-Yoh/MyBookShopApp/assets/117309392/00827619-381f-4482-abe7-161b3cfd17a2)
* Book page
![book](https://github.com/Kofa-Yoh/MyBookShopApp/assets/117309392/da625815-28b7-492d-aa34-8c3c2a7c94a8)
* Cart page
![cart](https://github.com/Kofa-Yoh/MyBookShopApp/assets/117309392/4bafbc1d-4e26-46a5-badf-fa6d4730aabb)
* Profile page
![profile](https://github.com/Kofa-Yoh/MyBookShopApp/assets/117309392/6d284865-2a3f-4f9c-b72e-993d9b0a7e12)
* Transactions Section
![transactions](https://github.com/Kofa-Yoh/MyBookShopApp/assets/117309392/72895e0c-df3b-451b-bfd6-590fbad8c072)

</details>

### Stack

* Java 17
* Spring boot 3.2
* Spring MVC
* Spring Security, JWT, OAuth2
* Spring Data JPA
* Hibernate
* PostgreSQL
* JUnit, Selenium
* Maven
* Open API 3
* Thymeleaf
* HTML
* CSS
* JQuery
