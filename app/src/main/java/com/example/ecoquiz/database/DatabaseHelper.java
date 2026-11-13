package com.example.ecoquiz.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ecoquiz.model.LeaderboardEntry;
import com.example.ecoquiz.model.Question;
import com.example.ecoquiz.model.QuizAnswer;
import com.example.ecoquiz.model.QuizAttempt;
import com.example.ecoquiz.model.User;
import com.example.ecoquiz.util.SecurityUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * DatabaseHelper manages the SQLite database ecoquiz.db.
 * Implements SQLiteOpenHelper to create and upgrade 4 tables:
 * 1. Users
 * 2. Questions
 * 3. QuizAttempts
 * 4. QuizAnswers
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ecoquiz.db";
    public static final int DATABASE_VERSION = 1;

    // TABLE 1: Users
    public static final String TABLE_USERS = "Users";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_USERNAME = "username";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PASSWORD_HASH = "password_hash";
    public static final String COL_USER_SALT = "salt";

    // TABLE 2: Questions
    public static final String TABLE_QUESTIONS = "Questions";
    public static final String COL_QUESTION_ID = "question_id";
    public static final String COL_QUESTION_TEXT = "question";
    public static final String COL_OPTION_A = "option_a";
    public static final String COL_OPTION_B = "option_b";
    public static final String COL_OPTION_C = "option_c";
    public static final String COL_OPTION_D = "option_d";
    public static final String COL_CORRECT_OPTION = "correct_option";
    public static final String COL_DIFFICULTY = "difficulty";
    public static final String COL_EXPLANATION = "explanation";

    // TABLE 3: QuizAttempts
    public static final String TABLE_QUIZ_ATTEMPTS = "QuizAttempts";
    public static final String COL_ATTEMPT_ID = "attempt_id";
    public static final String COL_ATTEMPT_USER_ID = "user_id";
    public static final String COL_ATTEMPT_DIFFICULTY = "difficulty";
    public static final String COL_ATTEMPT_CORRECT = "correct_answers";
    public static final String COL_ATTEMPT_INCORRECT = "incorrect_answers";
    public static final String COL_ATTEMPT_UNANSWERED = "unanswered";
    public static final String COL_ATTEMPT_TOTAL_SCORE = "total_score";
    public static final String COL_ATTEMPT_DATE = "attempt_date";

    // TABLE 4: QuizAnswers
    public static final String TABLE_QUIZ_ANSWERS = "QuizAnswers";
    public static final String COL_ANSWER_ID = "answer_id";
    public static final String COL_ANSWER_ATTEMPT_ID = "attempt_id";
    public static final String COL_ANSWER_QUESTION_ID = "question_id";
    public static final String COL_ANSWER_SELECTED_OPTION = "selected_option";
    public static final String COL_ANSWER_IS_CORRECT = "is_correct";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Table 1: Users
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_NAME + " TEXT NOT NULL, "
                + COL_USER_USERNAME + " TEXT UNIQUE NOT NULL, "
                + COL_USER_EMAIL + " TEXT NOT NULL, "
                + COL_USER_PASSWORD_HASH + " TEXT NOT NULL, "
                + COL_USER_SALT + " TEXT NOT NULL);";
        db.execSQL(createUsersTable);

        // Create Table 2: Questions
        String createQuestionsTable = "CREATE TABLE " + TABLE_QUESTIONS + " ("
                + COL_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_QUESTION_TEXT + " TEXT NOT NULL, "
                + COL_OPTION_A + " TEXT NOT NULL, "
                + COL_OPTION_B + " TEXT NOT NULL, "
                + COL_OPTION_C + " TEXT NOT NULL, "
                + COL_OPTION_D + " TEXT NOT NULL, "
                + COL_CORRECT_OPTION + " TEXT NOT NULL, "
                + COL_DIFFICULTY + " TEXT NOT NULL, "
                + COL_EXPLANATION + " TEXT);";
        db.execSQL(createQuestionsTable);

        // Create Table 3: QuizAttempts
        String createAttemptsTable = "CREATE TABLE " + TABLE_QUIZ_ATTEMPTS + " ("
                + COL_ATTEMPT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ATTEMPT_USER_ID + " INTEGER NOT NULL, "
                + COL_ATTEMPT_DIFFICULTY + " TEXT NOT NULL, "
                + COL_ATTEMPT_CORRECT + " INTEGER NOT NULL, "
                + COL_ATTEMPT_INCORRECT + " INTEGER NOT NULL, "
                + COL_ATTEMPT_UNANSWERED + " INTEGER NOT NULL, "
                + COL_ATTEMPT_TOTAL_SCORE + " INTEGER NOT NULL, "
                + COL_ATTEMPT_DATE + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COL_ATTEMPT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "));";
        db.execSQL(createAttemptsTable);

        // Create Table 4: QuizAnswers
        String createAnswersTable = "CREATE TABLE " + TABLE_QUIZ_ANSWERS + " ("
                + COL_ANSWER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ANSWER_ATTEMPT_ID + " INTEGER NOT NULL, "
                + COL_ANSWER_QUESTION_ID + " INTEGER NOT NULL, "
                + COL_ANSWER_SELECTED_OPTION + " TEXT, "
                + COL_ANSWER_IS_CORRECT + " INTEGER NOT NULL, "
                + "FOREIGN KEY(" + COL_ANSWER_ATTEMPT_ID + ") REFERENCES " + TABLE_QUIZ_ATTEMPTS + "(" + COL_ATTEMPT_ID + "), "
                + "FOREIGN KEY(" + COL_ANSWER_QUESTION_ID + ") REFERENCES " + TABLE_QUESTIONS + "(" + COL_QUESTION_ID + "));";
        db.execSQL(createAnswersTable);

        // Seed initial 30 Questions (10 Easy, 10 Medium, 10 Hard)
        seedQuestions(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_ANSWERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_ATTEMPTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // =========================================================================
    // USER OPERATIONS
    // =========================================================================

    /**
     * Registers a new user with salted SHA-256 password hash.
     * @return User ID if successful, -1 otherwise.
     */
    public long registerUser(String name, String username, String email, String plainPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        String salt = SecurityUtils.generateSalt();
        String hash = SecurityUtils.hashPassword(plainPassword, salt);

        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name.trim());
        values.put(COL_USER_USERNAME, username.trim().toLowerCase(Locale.ROOT));
        values.put(COL_USER_EMAIL, email.trim().toLowerCase(Locale.ROOT));
        values.put(COL_USER_PASSWORD_HASH, hash);
        values.put(COL_USER_SALT, salt);

        try {
            return db.insert(TABLE_USERS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Checks if a given username already exists.
     */
    public boolean isUsernameTaken(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS,
                    new String[]{COL_USER_ID},
                    COL_USER_USERNAME + " = ?",
                    new String[]{username.trim().toLowerCase(Locale.ROOT)},
                    null, null, null);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    /**
     * Authenticates a user with username and password.
     * @return User object if authentication succeeds, null otherwise.
     */
    public User authenticateUser(String username, String plainPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS,
                    null,
                    COL_USER_USERNAME + " = ?",
                    new String[]{username.trim().toLowerCase(Locale.ROOT)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(COL_USER_ID);
                int nameIndex = cursor.getColumnIndexOrThrow(COL_USER_NAME);
                int uNameIndex = cursor.getColumnIndexOrThrow(COL_USER_USERNAME);
                int emailIndex = cursor.getColumnIndexOrThrow(COL_USER_EMAIL);
                int hashIndex = cursor.getColumnIndexOrThrow(COL_USER_PASSWORD_HASH);
                int saltIndex = cursor.getColumnIndexOrThrow(COL_USER_SALT);

                int userId = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String uName = cursor.getString(uNameIndex);
                String email = cursor.getString(emailIndex);
                String storedHash = cursor.getString(hashIndex);
                String salt = cursor.getString(saltIndex);

                if (SecurityUtils.verifyPassword(plainPassword, salt, storedHash)) {
                    return new User(userId, name, uName, email, storedHash, salt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    /**
     * Retrieves user profile details by ID.
     */
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS,
                    null,
                    COL_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD_HASH)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_SALT))
                );
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    // =========================================================================
    // QUESTION RETRIEVAL
    // =========================================================================

    /**
     * Retrieves exactly 10 questions for the specified difficulty, randomized.
     * @param difficulty "Easy", "Medium", or "Hard"
     * @return List of 10 Question objects.
     */
    public List<Question> getQuestionsByDifficulty(String difficulty) {
        List<Question> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_QUESTIONS,
                    null,
                    COL_DIFFICULTY + " = ?",
                    new String[]{difficulty},
                    null, null, "RANDOM()", "10");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Question q = new Question();
                    q.setQuestionId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUESTION_ID)));
                    q.setQuestion(cursor.getString(cursor.getColumnIndexOrThrow(COL_QUESTION_TEXT)));
                    q.setOptionA(cursor.getString(cursor.getColumnIndexOrThrow(COL_OPTION_A)));
                    q.setOptionB(cursor.getString(cursor.getColumnIndexOrThrow(COL_OPTION_B)));
                    q.setOptionC(cursor.getString(cursor.getColumnIndexOrThrow(COL_OPTION_C)));
                    q.setOptionD(cursor.getString(cursor.getColumnIndexOrThrow(COL_OPTION_D)));
                    q.setCorrectOption(cursor.getString(cursor.getColumnIndexOrThrow(COL_CORRECT_OPTION)));
                    q.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(COL_DIFFICULTY)));
                    q.setExplanation(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPLANATION)));
                    list.add(q);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        // Shuffle in memory as secondary guarantee
        Collections.shuffle(list);
        return list;
    }

    // =========================================================================
    // QUIZ ATTEMPT & ANSWERS STORAGE
    // =========================================================================

    /**
     * Inserts a completed quiz attempt and its detailed answers.
     * @return Attempt ID or -1 on failure.
     */
    public long insertQuizAttempt(QuizAttempt attempt, List<QuizAnswer> answers) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long attemptId = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(COL_ATTEMPT_USER_ID, attempt.getUserId());
            values.put(COL_ATTEMPT_DIFFICULTY, attempt.getDifficulty());
            values.put(COL_ATTEMPT_CORRECT, attempt.getCorrectAnswers());
            values.put(COL_ATTEMPT_INCORRECT, attempt.getIncorrectAnswers());
            values.put(COL_ATTEMPT_UNANSWERED, attempt.getUnanswered());
            values.put(COL_ATTEMPT_TOTAL_SCORE, attempt.getTotalScore());

            String dateStr = attempt.getAttemptDate();
            if (dateStr == null || dateStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                dateStr = sdf.format(new Date());
            }
            values.put(COL_ATTEMPT_DATE, dateStr);

            attemptId = db.insert(TABLE_QUIZ_ATTEMPTS, null, values);

            if (attemptId != -1 && answers != null) {
                for (QuizAnswer ans : answers) {
                    ContentValues aVal = new ContentValues();
                    aVal.put(COL_ANSWER_ATTEMPT_ID, attemptId);
                    aVal.put(COL_ANSWER_QUESTION_ID, ans.getQuestionId());
                    aVal.put(COL_ANSWER_SELECTED_OPTION, ans.getSelectedOption());
                    aVal.put(COL_ANSWER_IS_CORRECT, ans.getIsCorrect());
                    db.insert(TABLE_QUIZ_ANSWERS, null, aVal);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return attemptId;
    }

    // =========================================================================
    // STATS & PERFORMANCE ANALYSIS
    // =========================================================================

    /**
     * Gets the total number of quiz attempts for a specific user.
     */
    public int getTotalAttempts(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_QUIZ_ATTEMPTS + " WHERE " + COL_ATTEMPT_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return 0;
    }

    /**
     * Gets the highest score achieved by a user across all attempts.
     */
    public int getHighestScore(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT MAX(" + COL_ATTEMPT_TOTAL_SCORE + ") FROM " + TABLE_QUIZ_ATTEMPTS + " WHERE " + COL_ATTEMPT_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return 0;
    }

    /**
     * Gets the average score achieved by a user across all attempts.
     */
    public double getAverageScore(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT AVG(" + COL_ATTEMPT_TOTAL_SCORE + ") FROM " + TABLE_QUIZ_ATTEMPTS + " WHERE " + COL_ATTEMPT_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return 0.0;
    }

    /**
     * Gets total correct answers answered by user across all attempts.
     */
    public int getTotalCorrectAnswers(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT SUM(" + COL_ATTEMPT_CORRECT + ") FROM " + TABLE_QUIZ_ATTEMPTS + " WHERE " + COL_ATTEMPT_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return 0;
    }

    /**
     * Gets average score by difficulty for a specific user.
     */
    public double getAverageScoreByDifficulty(int userId, String difficulty) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT AVG(" + COL_ATTEMPT_TOTAL_SCORE + ") FROM " + TABLE_QUIZ_ATTEMPTS + " WHERE " + COL_ATTEMPT_USER_ID + " = ? AND " + COL_ATTEMPT_DIFFICULTY + " = ?",
                    new String[]{String.valueOf(userId), difficulty});
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getDouble(0);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return 0.0;
    }

    /**
     * Retrieves recent quiz attempts for a user (most recent first).
     */
    public List<QuizAttempt> getUserAttempts(int userId) {
        List<QuizAttempt> attempts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_QUIZ_ATTEMPTS,
                    null,
                    COL_ATTEMPT_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null, null,
                    COL_ATTEMPT_ID + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    QuizAttempt a = new QuizAttempt();
                    a.setAttemptId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ATTEMPT_ID)));
                    a.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ATTEMPT_USER_ID)));
                    a.setDifficulty(cursor.getString(cursor.getColumnIndexOrThrow(COL_ATTEMPT_DIFFICULTY)));
                    a.setCorrectAnswers(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ATTEMPT_CORRECT)));
                    a.setIncorrectAnswers(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ATTEMPT_INCORRECT)));
                    a.setUnanswered(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ATTEMPT_UNANSWERED)));
                    a.setTotalScore(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ATTEMPT_TOTAL_SCORE)));
                    a.setAttemptDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_ATTEMPT_DATE)));
                    attempts.add(a);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return attempts;
    }

    // =========================================================================
    // LEADERBOARD
    // =========================================================================

    /**
     * Retrieves top 10 users ranked by highest score descending.
     * Ties broken by earliest attempt date or username.
     */
    public List<LeaderboardEntry> getTopLeaderboard() {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Query: Join Users and QuizAttempts, group by user, find max score, order desc limit 10
        String query = "SELECT u." + COL_USER_USERNAME + ", MAX(a." + COL_ATTEMPT_TOTAL_SCORE + ") AS best_score, "
                + "MAX(a." + COL_ATTEMPT_DATE + ") AS last_date "
                + "FROM " + TABLE_USERS + " u "
                + "INNER JOIN " + TABLE_QUIZ_ATTEMPTS + " a ON u." + COL_USER_ID + " = a." + COL_ATTEMPT_USER_ID + " "
                + "GROUP BY u." + COL_USER_ID + ", u." + COL_USER_USERNAME + " "
                + "ORDER BY best_score DESC, last_date ASC, u." + COL_USER_USERNAME + " ASC "
                + "LIMIT 10;";

        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                int rank = 1;
                do {
                    String username = cursor.getString(0);
                    int score = cursor.getInt(1);
                    String date = cursor.getString(2);
                    leaderboard.add(new LeaderboardEntry(rank++, username, score, date));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return leaderboard;
    }

    // =========================================================================
    // SEED QUESTIONS: 10 EASY, 10 MEDIUM, 10 HARD (30 TOTAL)
    // =========================================================================

    private void seedQuestions(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            // --- 10 EASY QUESTIONS ---
        insertSeedQuestion(db,
                "What is the primary gas responsible for human-induced global warming?",
                "Oxygen", "Carbon Dioxide (CO2)", "Argon", "Helium",
                "B", "Easy",
                "Carbon dioxide emitted by burning fossil fuels accounts for the largest share of human-driven global warming.");

        insertSeedQuestion(db,
                "Which of the following is a clean and renewable source of energy?",
                "Coal", "Solar Energy", "Petroleum", "Lignite",
                "B", "Easy",
                "Solar energy comes directly from sunlight and does not deplete or produce direct greenhouse gas emissions.");

        insertSeedQuestion(db,
                "What do the 'Three Rs' of environmental conservation stand for?",
                "Recycle, Rebuild, Restore", "Reduce, Reuse, Recycle", "Read, React, Remember", "Recover, Reform, Renew",
                "B", "Easy",
                "Reduce, Reuse, and Recycle is the globally recognized waste management hierarchy to protect natural resources.");

        insertSeedQuestion(db,
                "Which human activity contributes most to global deforestation?",
                "Urban gardening", "Agricultural land expansion", "Installing solar panels", "Bicycle transportation",
                "B", "Easy",
                "Clearing land for large-scale cattle ranching and commercial agriculture drives roughly 80% of global deforestation.");

        insertSeedQuestion(db,
                "What is the main environmental problem caused by single-use plastic bags?",
                "They dissolve too fast in water", "They take centuries to decompose and pollute oceans", "They absorb too much carbon", "They generate ozone",
                "B", "Easy",
                "Plastic bags persist in nature for 400+ years, breaking down into harmful microplastics that poison marine ecosystems.");

        insertSeedQuestion(db,
                "Which United Nations Sustainable Development Goal (SDG) specifically addresses 'Climate Action'?",
                "SDG 1", "SDG 7", "SDG 13", "SDG 17",
                "C", "Easy",
                "SDG 13 urges urgent action to combat climate change and its devastating impacts globally.");

        insertSeedQuestion(db,
                "What natural process allows trees and plants to absorb carbon dioxide from the air?",
                "Photosynthesis", "Combustion", "Precipitation", "Evaporation",
                "A", "Easy",
                "During photosynthesis, green plants absorb CO2 and water using sunlight, releasing life-sustaining oxygen.");

        insertSeedQuestion(db,
                "Which household habit helps significantly reduce personal carbon footprint?",
                "Leaving appliances on standby", "Turning off lights when not in use", "Running water while brushing teeth", "Using plastic cutlery",
                "B", "Easy",
                "Conserving electricity directly reduces the fossil fuels burned at power stations to generate energy.");

        insertSeedQuestion(db,
                "What is 'Global Warming'?",
                "Seasonal drop in regional temperatures", "Long-term heating of Earth's climate system due to human activities", "Temporary weather variations", "Cooling of ocean surfaces",
                "B", "Easy",
                "Global warming describes the long-term increase in Earth's average surface temperature primarily due to greenhouse gases.");

        insertSeedQuestion(db,
                "Which of these vehicles produces zero tailpipe carbon emissions?",
                "Diesel truck", "Gasoline sedan", "Electric bicycle", "Two-stroke scooter",
                "C", "Easy",
                "Electric bicycles and battery electric vehicles generate zero direct exhaust emissions while in operation.");

        // --- 10 MEDIUM QUESTIONS ---
        insertSeedQuestion(db,
                "How does the natural 'Greenhouse Effect' work on planet Earth?",
                "It cools the Earth by reflecting all solar radiation", "Atmospheric gases trap reflected heat radiated from Earth's surface", "It destroys the ozone layer", "It generates oxygen in the upper atmosphere",
                "B", "Medium",
                "Greenhouse gases trap infrared heat radiated from the planet's surface, keeping Earth warm enough to support life.");

        insertSeedQuestion(db,
                "Which greenhouse gas is emitted in large quantities by livestock digestion and rice paddies?",
                "Nitrous oxide", "Methane (CH4)", "Carbon monoxide", "Sulfur dioxide",
                "B", "Medium",
                "Methane is emitted by enteric fermentation in cattle and anaerobic decomposition in flooded rice paddies.");

        insertSeedQuestion(db,
                "What is the critical target of the 2015 Paris Agreement regarding global temperature rise?",
                "Limit warming well below 2.0°C and pursue 1.5°C above pre-industrial levels", "Permit warming up to 4.0°C", "Cool the planet by 3.0°C below baseline", "Freeze ocean levels immediately",
                "A", "Medium",
                "The Paris Agreement legally commits countries to hold warming well below 2°C, aiming for 1.5°C to avert catastrophe.");

        insertSeedQuestion(db,
                "What does 'Climate Adaptation' mean in environmental policy?",
                "Stopping all emissions immediately", "Adjusting ecological and social systems to actual or expected climate impacts", "Building more coal plants", "Ignoring changing weather patterns",
                "B", "Medium",
                "Climate adaptation involves building resilience, such as flood barriers and drought-resistant crops, against climate impacts.");

        insertSeedQuestion(db,
                "Why are oceans becoming more acidic as atmospheric CO2 increases?",
                "Plastic waste releases acidic toxins", "Oceans absorb excess CO2 forming carbonic acid", "Acid rain from volcanoes", "Overfishing increases ocean salt",
                "B", "Medium",
                "Oceans absorb ~25% of human CO2 emissions, reacting with seawater to produce carbonic acid (H2CO3).");

        insertSeedQuestion(db,
                "What is a 'Carbon Footprint'?",
                "The total amount of greenhouse gases emitted by a person, organization, or product", "The physical mark left by boots on coal mines", "Amount of coal stored in deep soil", "The shadow cast by smoke stacks",
                "A", "Medium",
                "A carbon footprint measures total direct and indirect greenhouse gas emissions expressed in equivalent tons of CO2.");

        insertSeedQuestion(db,
                "Which energy source generates power by harnessing heat stored within the Earth's core?",
                "Hydroelectric power", "Geothermal energy", "Biomass energy", "Tidal power",
                "B", "Medium",
                "Geothermal energy taps natural thermal reservoirs beneath the Earth's crust to generate steam and electricity.");

        insertSeedQuestion(db,
                "What is the primary danger associated with the rapid melting of polar ice caps and glaciers?",
                "Depletion of ocean salt", "Global sea-level rise threatening coastal cities", "Increase in Earth's gravity", "Excessive cloud formation",
                "B", "Medium",
                "Melting terrestrial ice sheets adds huge volumes of water to oceans, causing catastrophic sea-level rise and coastal flooding.");

        insertSeedQuestion(db,
                "Which international treaty successfully phased out chlorofluorocarbons (CFCs) to protect the ozone layer?",
                "Kyoto Protocol", "Montreal Protocol", "Paris Agreement", "Geneva Convention",
                "B", "Medium",
                "Adopted in 1987, the Montreal Protocol is considered the most successful environmental treaty for healing the ozone layer.");

        insertSeedQuestion(db,
                "What is 'Sustainable Development' as defined by the Brundtland Commission?",
                "Meeting present needs without compromising the ability of future generations to meet theirs", "Consuming resources at maximum speed for rapid economic growth", "Halting all industrial production permanently", "Relying purely on imported goods",
                "A", "Medium",
                "Sustainable development balances economic, environmental, and social well-being for both present and future generations.");

        // --- 10 HARD QUESTIONS ---
        insertSeedQuestion(db,
                "What is the Global Warming Potential (GWP20) of Methane compared to Carbon Dioxide over a 20-year timescale?",
                "Roughly 2 times", "Roughly 84 times", "Identical (1 time)", "Roughly 500 times",
                "B", "Hard",
                "Methane is a potent short-lived climate pollutant with a GWP approximately 84 times higher than CO2 over 20 years.");

        insertSeedQuestion(db,
                "What is the 'Albedo Effect' and how does Arctic ice loss accelerate climate warming?",
                "Ice absorbs all sunlight; open water reflects it", "White ice reflects solar energy; darker open ocean absorbs more heat, causing positive feedback", "Ice produces greenhouse gases as it melts", "Cold water cools the stratosphere",
                "B", "Hard",
                "Albedo refers to surface reflectivity; as bright ice melts into dark seawater, more heat is absorbed, triggering further melting.");

        insertSeedQuestion(db,
                "Which international body provides rigorous, objective scientific assessments on climate change to global governments?",
                "IPCC (Intergovernmental Panel on Climate Change)", "WHO (World Health Organization)", "WTO (World Trade Organization)", "IMF (International Monetary Fund)",
                "A", "Hard",
                "The IPCC was established in 1988 by WMO and UNEP to assess peer-reviewed science related to climate change.");

        insertSeedQuestion(db,
                "What is the primary mechanism of 'Carbon Capture and Storage' (CCS)?",
                "Filtering atmospheric air with household fans", "Capturing CO2 at industrial emission points, transporting it, and sequestering it deep underground", "Planting grass along highways", "Converting CO2 into petroleum fuels",
                "B", "Hard",
                "CCS traps concentrated industrial CO2 emissions and injects them into deep geological formations for permanent storage.");

        insertSeedQuestion(db,
                "What does achieving 'Net-Zero Emissions' (Carbon Neutrality) scientifically mean?",
                "Emitting zero carbon with no industrial activity", "Balancing anthropogenic greenhouse gas emissions with equivalent removals over a specified period", "Purchasing unlimited unverified offsets", "Limiting vehicle use on weekends",
                "B", "Hard",
                "Net-zero means all residual human GHG emissions are counterbalanced by equal active anthropogenic removals from the atmosphere.");

        insertSeedQuestion(db,
                "Why are permafrost thaw cycles considered a catastrophic climate 'tipping point'?",
                "They increase mountain snowfall", "They release massive trapped volumes of methane and carbon dioxide previously locked in frozen soil", "They eliminate fresh drinking water", "They cause volcanic eruptions",
                "B", "Hard",
                "Permafrost stores about twice as much carbon as the atmosphere; thawing releases massive methane emissions in an unstoppable feedback loop.");

        insertSeedQuestion(db,
                "Under UN SDG 13, which specific target focuses on improving education, awareness-raising, and human capacity on climate change?",
                "Target 13.1", "Target 13.2", "Target 13.3", "Target 13.a",
                "C", "Hard",
                "Target 13.3 explicitly directs nations to improve education, awareness-raising, and human and institutional capacity on climate mitigation.");

        insertSeedQuestion(db,
                "What role does the Atlantic Meridional Overturning Circulation (AMOC) play in the global climate system?",
                "It transports warm surface waters to the North Atlantic and deep cold water southward, regulating global climate", "It creates desert monsoons in South America", "It distributes volcanic ash across the globe", "It stores ozone over Antarctica",
                "A", "Hard",
                "AMOC is a major conveyor belt ocean current that redistributes enormous heat energy from the tropics to the North Atlantic.");

        insertSeedQuestion(db,
                "Which fluorinated gas has one of the highest atmospheric lifetimes (over 3,200 years) and a GWP exceeding 23,000?",
                "Sulfur Hexafluoride (SF6)", "Carbon Monoxide (CO)", "Ozone (O3)", "Methane (CH4)",
                "A", "Hard",
                "SF6 is an inorganic, colorless, odorless, extremely potent greenhouse gas used mainly as an electrical insulator in switchgears.");

        insertSeedQuestion(db,
                "What is 'Nationally Determined Contributions' (NDCs) under the Paris Climate Agreement?",
                "Financial penalties levied on polluting states", "Non-binding suggestions by NGOs", "National climate action plans outlining each country's post-2020 greenhouse gas reduction commitments", "Subsidies provided to oil companies",
                "C", "Hard",
                "NDCs are national commitments submitted every 5 years by signatories detailing domestic targets for reducing carbon emissions.");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void insertSeedQuestion(SQLiteDatabase db, String q, String a, String b, String c, String d,
                                    String correct, String diff, String exp) {
        ContentValues values = new ContentValues();
        values.put(COL_QUESTION_TEXT, q);
        values.put(COL_OPTION_A, a);
        values.put(COL_OPTION_B, b);
        values.put(COL_OPTION_C, c);
        values.put(COL_OPTION_D, d);
        values.put(COL_CORRECT_OPTION, correct);
        values.put(COL_DIFFICULTY, diff);
        values.put(COL_EXPLANATION, exp);
        db.insert(TABLE_QUESTIONS, null, values);
    }
}
