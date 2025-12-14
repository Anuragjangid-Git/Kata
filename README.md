# Sweet Shop Management System

A full-stack application for managing a sweet shop inventory with user authentication, CRUD operations, and purchase/restock functionality.

## 🚀 Features

### Backend (Spring Boot)
- **User Authentication**: JWT-based authentication with registration and login
- **Sweet Management**: Full CRUD operations for sweets
- **Search & Filter**: Search sweets by name, category, and price range
- **Inventory Management**: Purchase and restock functionality
- **Role-Based Access**: Admin and User roles with appropriate permissions
- **Database**: MySQL database with JPA/Hibernate

### Frontend (React.js)
- Modern, responsive single-page application
- User registration and login
- Sweet catalog with search and filter
- Purchase functionality
- Admin panel for managing sweets

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+ and npm (for frontend)

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 4.0.0
- **Database**: MySQL
- **Security**: Spring Security with JWT
- **Build Tool**: Maven

### Frontend
- **Framework**: React.js
- **Language**: JavaScript
- **HTTP Client**: Axios/Fetch API

###📸 Application Screenshots

Below are some screenshots showcasing the key features and user interfaces of the Sweet Shop Management System.

🔐 Login Page
<img width="1915" height="1051" alt="Screenshot 2025-12-14 140853" src="https://github.com/user-attachments/assets/dd619f35-8ed2-49d0-947b-a9dde327744f" />

This page allows users to securely log in using their email and password.

🏠 Home / Dashboard
<img width="1900" height="1032" alt="Screenshot 2025-12-14 140805" src="https://github.com/user-attachments/assets/d961cfef-edb6-44ca-a463-0c95f4609b37" />

The main dashboard where users can browse sweets, search, filter by category and price, and purchase items.

🛠️ Admin Panel
<img width="1898" height="1026" alt="Screenshot 2025-12-14 140817" src="https://github.com/user-attachments/assets/f8e3491d-c181-49a6-88ec-19e2e1ce78e9" />

Accessible only to admins. This panel allows managing the sweet inventory including edit, restock, and delete operations.

✏️ Edit Sweet Page
<img width="1897" height="1030" alt="Screenshot 2025-12-14 140828" src="https://github.com/user-attachments/assets/38e1e089-8dcf-4be6-9250-fed8091299bf" />

A modal/form used by admins to update sweet details such as name, category, price, and quantity.

➕ Add New Sweet Page
<img width="1898" height="1040" alt="Screenshot 2025-12-14 140840" src="https://github.com/user-attachments/assets/5ebbb191-76f2-4ded-aafe-fea8f1b92e7d" />

Admins can add new sweets to the inventory using this form.

## 📦 Installation & Setup

### Backend Setup

1. Clone the repository:
```bash
git clone <repository-url>
cd Kata
```

2. Configure database in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/Kata
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Create the database:
```sql
CREATE DATABASE Kata;
```

4. Build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```

The backend will run on `http://localhost:8080`

### Frontend Setup

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The frontend will run on `http://localhost:3000`

## 📡 API Endpoints

### Authentication (Public)
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user
- `POST /api/v1/auth/signup` - Alternative signup endpoint
- `POST /api/v1/auth/signin` - Alternative signin endpoint

### Sweets (Protected - Requires JWT Token)
- `POST /api/sweets` - Create a new sweet (Authenticated)
- `GET /api/sweets` - Get all sweets (Authenticated)
- `GET /api/sweets/search` - Search sweets (Authenticated)
- `GET /api/sweets/{id}` - Get sweet by ID (Authenticated)
- `PUT /api/sweets/{id}` - Update sweet (Authenticated)
- `DELETE /api/sweets/{id}` - Delete sweet (Admin only)
- `POST /api/sweets/{id}/purchase` - Purchase sweet (Authenticated)
- `POST /api/sweets/{id}/restock` - Restock sweet (Admin only)

### Request Examples

**Register User:**
```json
POST /api/auth/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Login:**
```json
POST /api/auth/login
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Create Sweet:**
```json
POST /api/sweets
Authorization: Bearer <token>
{
  "name": "Chocolate Bar",
  "category": "Chocolate",
  "price": 2.50,
  "quantity": 100
}
```

**Search Sweets:**
```
GET /api/sweets/search?name=chocolate&category=Chocolate&minPrice=1.00&maxPrice=5.00
Authorization: Bearer <token>
```

## 🧪 Testing

Run backend tests:
```bash
mvn test
```

Run frontend tests:
```bash
cd frontend
npm test
```

## 📝 Project Structure

```
Kata/
├── src/
│   ├── main/
│   │   ├── java/com/backend/Kata/
│   │   │   ├── config/          # Security and JWT configuration
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   ├── entities/         # JPA entities
│   │   │   ├── exception/        # Exception handlers
│   │   │   ├── repository/       # Data access layer
│   │   │   └── services/         # Business logic
│   │   └── resources/
│   │       └── application.properties
│   └── test/                      # Test files
├── frontend/                      # React frontend application
├── pom.xml                        # Maven configuration
└── README.md
```

## 🤖 My AI Usage

### AI Tools Used

Throughout this project, I utilized several AI tools to enhance my development workflow:

1. **ChatGPT (OpenAI)**
   - Used for generating initial boilerplate code for entities, DTOs, and controllers
   - Assisted in debugging security configuration issues
   - Helped with Spring Security API changes and best practices
   - Generated test cases and test structure

2. **GitHub Copilot**
   - Assisted with code completion and suggestions
   - Helped with writing repetitive code patterns
   - Suggested improvements for code structure

### How AI Was Used

#### Backend Development
- **Entity and Repository Creation**: Used ChatGPT to generate the initial structure for the `Sweet` entity and `SweetRepository` with custom search queries. I then customized the code to fit our specific requirements.
- **Security Configuration**: When encountering issues with `DaoAuthenticationProvider` in Spring Security 7.x, I used AI to understand the API changes and implement a custom `AuthenticationProvider` solution.
- **Service Layer**: AI assisted in generating the service interface and implementation structure, which I then refined with business logic and error handling.
- **Exception Handling**: Used AI to create a comprehensive global exception handler following Spring Boot best practices.

#### Frontend Development
- **Component Structure**: AI helped generate the initial React component structure and routing setup.
- **API Integration**: Assisted in creating axios configuration and API service functions.
- **State Management**: Helped design the state management approach for user authentication and sweet data.

#### Testing
- **Test Structure**: AI generated the initial test class structure and common test patterns.
- **Mock Data**: Assisted in creating realistic test data and mock objects.

### Reflection on AI Impact

**Positive Impacts:**
1. **Speed**: AI significantly accelerated development by generating boilerplate code, allowing me to focus on business logic and problem-solving.
2. **Learning**: When encountering new APIs or frameworks, AI provided explanations and examples that helped me understand concepts faster.
3. **Code Quality**: AI suggestions often followed best practices and helped maintain consistency across the codebase.
4. **Debugging**: AI was invaluable in identifying and fixing bugs, especially with complex Spring Security configurations.

**Challenges:**
1. **Over-reliance**: Initially, I found myself relying too heavily on AI suggestions without fully understanding the code. I had to consciously review and understand all AI-generated code.
2. **Context Understanding**: Sometimes AI suggestions didn't perfectly match our specific requirements, requiring manual adjustments.
3. **Version Compatibility**: AI suggestions sometimes referenced older API versions, requiring research into current best practices.

**Best Practices Learned:**
1. Always review and understand AI-generated code before committing
2. Use AI as a starting point, not a final solution
3. Test AI-generated code thoroughly
4. Document AI usage transparently in commit messages
5. Combine AI assistance with manual research for complex topics

**Overall Assessment:**
AI tools were instrumental in this project, particularly for boilerplate generation, debugging, and learning new frameworks. However, they served as powerful assistants rather than replacements for understanding and decision-making. The combination of AI assistance with manual review, testing, and understanding resulted in a robust, well-structured application.

## 👥 Roles

- **USER**: Can view sweets, search, and purchase
- **ADMIN**: All user permissions plus create, update, delete sweets, and restock inventory

## 🔒 Security

- JWT token-based authentication
- Password encryption using BCrypt
- Role-based access control (RBAC)
- CSRF protection disabled for API (stateless sessions)
- Input validation on all endpoints

## 📄 License

This project is part of a coding kata exercise.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the frontend framework
- All AI tools that assisted in development (documented above)

