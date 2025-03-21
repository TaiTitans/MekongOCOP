
# MekongOCOP

MekongOCOP is a marketplace platform designed to promote and support OCOP (One Commune One Product) products from the Mekong Delta region. The project is built using modern technologies like Spring Boot, Vue.js, Flutter, and PostgreSQL.

![GitHub license](https://img.shields.io/github/license/TaiTitans/MekongOCOP) ![GitHub stars](https://img.shields.io/github/stars/TaiTitans/MekongOCOP) ![GitHub forks](https://img.shields.io/github/forks/TaiTitans/MekongOCOP)

---

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Technologies](#technologies)
- [Installation](#installation)
- [Usage](#usage)
- [Folder Structure](#folder-structure)
- [Contributing](#contributing)
- [License](#license)

---

## Introduction

MekongOCOP aims to digitize and empower local businesses by providing an online platform for showcasing and trading OCOP products. The platform supports features like product catalog management, user-friendly interfaces, online payment, and interactive maps for product search by location.

---

## Features

- **E-commerce Platform**: Designed and developed a comprehensive platform to facilitate the trading of OCOP products. Built a robust Restful API, integrating Redis for caching to enhance performance, and Mailjet for secure OTP authentication. Incorporated VietQR for seamless payment processing and Cloudinary for efficient image storage and management.

- **AI Chatbot and Real-time Communication**: Implemented an AI-powered chatbot using the PhoBERT model to support user interactions and enhance the customer experience. Developed a real-time chat feature using Socket.io, enabling dynamic communication between buyers and sellers.

- **Security and Deployment**: Secured the platform with JWT-based authentication, utilizing AccessToken and RefreshToken mechanisms for enhanced security. Deployed the application using Docker for efficient containerization and PostgreSQL for database management, ensuring scalability and reliability.

---

## Technologies

The platform leverages the following technologies:

- **Backend**: Spring Boot
- **Frontend**: Vue.js and Flutter
- **Database**: PostgreSQL, Redis (for caching)
- **Image Management**: Cloudinary
- **Communication**: Socket.io
- **AI Model**: PhoBERT
- **Authentication**: Mailjet, JWT
- **Containerization**: Docker

---

## Installation

Follow these steps to set up the project locally:

1. **Clone the repository**:

   ```bash
   git clone https://github.com/TaiTitans/MekongOCOP.git
   ```

2. **Navigate to the project directory**:

   ```bash
   cd MekongOCOP
   ```

3. **Set up environment variables**:

   Create a `.env` file in the root directory and configure it with necessary values (e.g., database credentials, API keys).

4. **Build and run the backend**:

   ```bash
   cd backend
   ./mvnw install
   ./mvnw spring-boot:run
   ```

5. **Install and run the frontend**:

   ```bash
   cd frontend
   npm install
   npm run serve
   ```

6. **Access the application**:

   Open `http://localhost:8080` in your browser to view the platform.

---

## Usage

After the installation, you can:

- Register as a user or log in.
- Browse products by category or location.
- Add products to your cart and complete purchases.
- Review and rate products.
- Explore blogs and news for updates.
- Interact with the AI-powered chatbot for support.
- Chat with sellers in real-time.

---

## Folder Structure

```plaintext
MekongOCOP/
├── backend/           # Spring Boot backend code
├── frontend/          # Vue.js frontend code
├── mobile-app/        # Flutter mobile app code
├── docker-compose.yml # Docker configuration
└── README.md          # Project documentation
```

---

## Contributing

We welcome contributions from the community! To contribute:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Commit your changes and push the branch.
4. Open a pull request with a detailed description of your changes.

---

## License

This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for more details.

---

## Contact

For more information or support, feel free to contact us at [taititansofficial@gmail.com](mailto:taititansofficial@gmail.com).

---

## API Documentation

Detailed API documentation can be found [Here](https://documenter.getpostman.com/view/30668997/2sAXqs8iAt)(#).


---

