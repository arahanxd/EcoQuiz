# EcoQuiz – Climate Action Awareness & Assessment Application
**Academic Mini-Project for Mobile Application Development (MAD Lab)**  
*United Nations Sustainable Development Goal 13: Climate Action (Target 13.3)*  
*"Learn Today, Protect Tomorrow."*

---

## 1. Project Overview & Architecture

EcoQuiz is an offline, educational Android application built completely in **pure Java** and **XML**. It educates users on climate action, global warming, renewable energy, and international environmental treaties through interactive, timed quizzes.

### Architectural Structure:
- **`com.example.ecoquiz.activity`**: Contains all 9 Application Activities (`SplashActivity`, `LoginActivity`, `RegisterActivity`, `HomeActivity`, `QuizActivity`, `ResultActivity`, `PerformanceActivity`, `LeaderboardActivity`, `ProfileActivity`).
- **`com.example.ecoquiz.database`**: `DatabaseHelper` subclassing `SQLiteOpenHelper`, managing the SQLite database (`ecoquiz.db`) with parameterized queries, transactions, and automated seeding.
- **`com.example.ecoquiz.model`**: Lightweight POJO models (`User`, `Question`, `QuizAttempt`, `QuizAnswer`, `DifficultyItem`, `LeaderboardEntry`).
- **`com.example.ecoquiz.adapter`**: RecyclerView & ViewPager2 adapters (`DifficultyPagerAdapter`, `QuizHistoryAdapter`, `LeaderboardAdapter`).
- **`com.example.ecoquiz.util`**: Helper utilities (`SecurityUtils` for salted SHA-256 password hashing, `SessionManager` for SharedPreferences session persistence).

---

## 2. Database Schema (`ecoquiz.db`)

The database consists of 4 relational tables:

```sql
-- TABLE 1: Users
CREATE TABLE Users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    username TEXT UNIQUE NOT NULL,
    email TEXT NOT NULL,
    password_hash TEXT NOT NULL,
    salt TEXT NOT NULL
);

-- TABLE 2: Questions (Pre-populated with 30 original climate questions)
CREATE TABLE Questions (
    question_id INTEGER PRIMARY KEY AUTOINCREMENT,
    question TEXT NOT NULL,
    option_a TEXT NOT NULL,
    option_b TEXT NOT NULL,
    option_c TEXT NOT NULL,
    option_d TEXT NOT NULL,
    correct_option TEXT NOT NULL,
    difficulty TEXT NOT NULL,
    explanation TEXT
);

-- TABLE 3: QuizAttempts
CREATE TABLE QuizAttempts (
    attempt_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    difficulty TEXT NOT NULL,
    correct_answers INTEGER NOT NULL,
    incorrect_answers INTEGER NOT NULL,
    unanswered INTEGER NOT NULL,
    total_score INTEGER NOT NULL,
    attempt_date TEXT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES Users(user_id)
);

-- TABLE 4: QuizAnswers
CREATE TABLE QuizAnswers (
    answer_id INTEGER PRIMARY KEY AUTOINCREMENT,
    attempt_id INTEGER NOT NULL,
    question_id INTEGER NOT NULL,
    selected_option TEXT,
    is_correct INTEGER NOT NULL,
    FOREIGN KEY(attempt_id) REFERENCES QuizAttempts(attempt_id),
    FOREIGN KEY(question_id) REFERENCES Questions(question_id)
);
```

---

## 3. Quiz Difficulties & Question Bank (30 Questions)

| Level | Timer | Questions | Points/Q | Topics |
| :--- | :--- | :--- | :--- | :--- |
| **Easy** | 30s | 10 | 10 | Basic climate awareness, recycling, deforestation, plastic pollution, solar power |
| **Medium** | 20s | 10 | 10 | Greenhouse effect, methane emissions, ocean acidification, carbon footprint, Paris Agreement |
| **Hard** | 10s | 10 | 10 | Methane GWP20, albedo feedback, permafrost thaw cycles, CCS, AMOC, SDG Target 13.3, SF6 |

---

## 4. How to Open, Build & Run in Android Studio

1. **Launch Android Studio** (Hedgehog, Iguana, Jellyfish, Koala, Ladybug, or newer).
2. Click **File -> Open...** and browse to:
   ```
   C:\DSCE\5th Sem\Lab\MAD Lab\Project\EcoQuiz
   ```
3. Let Gradle sync project dependencies.
4. Select an Android Virtual Device (AVD Emulator) running **API 24 to API 34** (e.g. Pixel 7 running Android 13 or 14).
5. Click **Run 'app'** (`Shift + F10`).
6. The app launches directly with `SplashActivity`.

---

## 5. Demonstration Walkthrough for Lab Exam / Viva

1. **User Registration**:
   - Register a user (e.g. Name: `Rahul Kumar`, Username: `rahul123`, Email: `rahul@gmail.com`, Password: `Password@123`).
   - Notice strict regex validation (requires 8+ characters, uppercase letter, number, and special character).
   - Passwords are encrypted in SQLite using random 16-byte cryptographic salts and SHA-256 hashes.
2. **User Login & Session**:
   - Log in with valid credentials.
   - Notice session persistence via SharedPreferences.
3. **Home Dashboard & ViewPager2 Carousel**:
   - Observe personal greeting with user's name.
   - Swipe horizontally through the 3 difficulty cards (Easy, Medium, Hard).
4. **Active Quiz with CountdownTimer**:
   - Select "Play Now" on **Easy** (30s timer) or **Hard** (10s timer).
   - Click an answer option: note that timer stops immediately, the correct option turns green, wrong options turn red, and a detailed climate explanation is revealed.
   - Notice multiple selections are blocked to prevent duplicate scoring.
   - Test timeout: allow the countdown timer to reach 0s to observe automatic unanswered handling.
5. **Results & Scoring**:
   - At question 10, observe the score breakdown, percentage, performance category (e.g., *Excellent!*), and motivational feedback.
6. **Performance Analytics & Privacy**:
   - View your total quizzes, average score, highest score, difficulty progress bars, and recent attempts.
   - Notice records are filtered exclusively for the logged-in user (`WHERE user_id = ?`).
7. **Local Leaderboard**:
   - View the top 10 users ranked by highest score descending.
8. **Session Termination**:
   - Tap Logout; verify that back navigation cannot re-enter protected screens.
