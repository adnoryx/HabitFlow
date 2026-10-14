# HabitFlow

HabitFlow is an offline, gamified habit tracker built to help you stay consistent with your daily routines, earn XP, level up, and protect your streaks.

## Core Concept

HabitFlow treats habit tracking like an RPG. Every routine you complete awards Experience Points (XP). As you collect XP, your player level increases from Level 1 up to Level 100 and beyond.

## How to Use the App

### 1. Today Screen
This is your main daily dashboard.
* **Date Navigator:** Tap any day of the current week to view your schedule. Use the `<` and `>` arrows to browse past or future weeks. Tap **Today** to jump back to the current day.
* **Adding a Routine:** Tap the **+ Add Routine** button. Enter the title, optional description, time of day (Morning, Afternoon, Evening, or All Day), and XP reward value.
* **Checking off Routines:** Tap the circle checkbox next to any habit to mark it complete. 
  * Note: You can only check off routines for the current day to ensure authentic streak tracking. Past and future days are view-only.
* **Grouping & Filters:** Use the category chips (All Routines, Morning, Afternoon, Evening) to filter your list. When viewing All Routines, habits are grouped into clear time-of-day sections.

### 2. Day Freezes & Freeze Quests
Life happens, and sometimes you miss a day. Day Freezes protect your streak when you cannot complete your routines.

* **Day Freeze Rule:** You can store multiple Day Freezes, but you can use at most **1 Day Freeze per calendar day**.
* **Activating a Freeze:** Tap the **Use Day Freeze** banner inside the Daily Target Ring card on the Today screen. Confirm the prompt to consume 1 freeze and safeguard your streak for the day.
* **Freeze Quests:** Located right below the Daily Target Ring. Complete quests (such as holding a 7-day streak, completing all daily routines, or reaching Level 5) to earn extra Day Freezes and XP bonuses. Tap **Claim Reward** when a quest is complete.

### 3. Routines Screen
The Routines tab lists all your active routines.
* Tap the three dots menu on any habit card to **Edit** its details or **Delete** it.

### 4. Trophies Screen
Track your RPG progression and view your unlocked achievements.
* **Level Progress:** Displays your current Level, rank, and XP required for the next level.
* **Player Stats:** Shows your total XP, best overall streak, and available Day Freezes.
* **Achievements:** A list of milestones unlocked as you hit level and streak goals.

### 5. Settings & Offline Data Transfer
Access Settings by tapping the gear icon `⚙️` in the top right corner.
* **Language Selector:** Choose from 9 supported languages (English, Slovak, Czech, German, Spanish, French, Polish, Ukrainian, Italian). The app updates instantly.
* **Offline JSON Backup:** Since HabitFlow stores all data locally in a Room SQLite database on your device with zero cloud servers:
  * Tap **Export Data** to save your entire progress to a local `.json` file.
  * Tap **Import Data** to restore your data from a `.json` backup file onto any device.
* **Onboarding Guide:** Replay the interactive guide anytime.
