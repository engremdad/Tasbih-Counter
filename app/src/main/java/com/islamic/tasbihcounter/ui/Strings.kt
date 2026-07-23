package com.islamic.tasbihcounter.ui

/**
 * Centralized Bangla UI strings. Keeping all display text in one place makes the
 * app fully Bangla and easy to review or later move into res/values-bn/strings.xml.
 */
object Str {
    // App / navigation
    const val appName = "তাসবিহ কাউন্টার"
    const val navCounter = "গণনা"
    const val navNames = "৯৯ নাম"
    const val navDuas = "দোয়া"
    const val navHistory = "ইতিহাস"
    const val navSettings = "সেটিংস"

    // Counter screen
    const val target = "লক্ষ্য"
    const val reset = "রিসেট"
    const val resetConfirmTitle = "গণনা রিসেট করবেন?"
    const val resetConfirmMsg = "বর্তমান জিকিরের গণনা শূন্যে ফিরিয়ে দেওয়া হবে। আজকের মোট ইতিহাস অক্ষত থাকবে।"
    const val cancel = "বাতিল"
    const val confirm = "নিশ্চিত করুন"
    const val selectDhikr = "জিকির নির্বাচন করুন"
    const val addCustomDhikr = "নিজের জিকির যোগ করুন"
    const val goalReached = "মাশাআল্লাহ! লক্ষ্য পূর্ণ হয়েছে"
    const val voiceListening = "শুনছি… জিকির বলুন"
    const val voiceTap = "কণ্ঠে গণনা"
    const val tapToCount = "গণনা করতে চাপুন"

    // Custom dhikr dialog
    const val dhikrArabic = "আরবি"
    const val dhikrTransliteration = "উচ্চারণ"
    const val dhikrTranslation = "অর্থ"
    const val dhikrTarget = "লক্ষ্য সংখ্যা"
    const val save = "সংরক্ষণ"
    const val delete = "মুছুন"
    const val edit = "সম্পাদনা"
    const val newDhikr = "নতুন জিকির"
    const val editDhikr = "জিকির সম্পাদনা"
    const val presetLabel = "পূর্বনির্ধারিত"

    // Asmaul Husna
    const val namesTitle = "আল্লাহর ৯৯ নাম"
    const val namesSubtitle = "আসমাউল হুসনা"
    const val playAudio = "উচ্চারণ শুনুন"
    const val namesProgress = "অগ্রগতি"
    const val nextName = "পরবর্তী নাম"
    const val resetNames = "শুরু থেকে"

    // Duas
    const val duasTitle = "প্রয়োজনীয় দোয়া"
    const val reference = "সূত্র"

    // History
    const val historyTitle = "দৈনিক ইতিহাস"
    const val streak = "ধারাবাহিকতা"
    const val days = "দিন"
    const val longestStreak = "সর্বোচ্চ ধারাবাহিকতা"
    const val lifetimeTotal = "মোট গণনা"
    const val badges = "অর্জন"
    const val todayTotal = "আজকের মোট"
    const val noHistory = "এখনো কোনো ইতিহাস নেই। জিকির শুরু করুন।"
    const val locked = "লক করা"

    // Settings
    const val settingsTitle = "সেটিংস"
    const val general = "সাধারণ"
    const val haptics = "কম্পন ফিডব্যাক"
    const val hapticsDesc = "প্রতিটি গণনায় মৃদু কম্পন"
    const val sound = "শব্দ"
    const val soundDesc = "গণনায় ক্লিক শব্দ"
    const val keepScreenOn = "স্ক্রিন চালু রাখুন"
    const val keepScreenOnDesc = "গণনার সময় স্ক্রিন বন্ধ হবে না"
    const val voice = "কণ্ঠ গণনা"
    const val voiceDesc = "‘সুবহানাল্লাহ’ বললে স্বয়ংক্রিয় গণনা"
    const val appearance = "থিম ও রূপ"
    const val themeStyle = "থিম"
    const val themeMode = "আলো / অন্ধকার"
    const val dynamicColor = "ডাইনামিক কালার (Android 12+)"
    const val dynamicColorDesc = "ওয়ালপেপার থেকে রং নিন"
    const val reminders = "স্মরণিকা"
    const val enableReminders = "রিমাইন্ডার চালু"
    const val remindersDesc = "সকাল-সন্ধ্যার আজকারের নোটিফিকেশন"
    const val morningTime = "সকালের সময়"
    const val eveningTime = "সন্ধ্যার সময়"
    const val about = "সম্পর্কে"
    const val aboutDesc = "তাসবিহ কাউন্টার — আপনার দৈনন্দিন জিকিরের সঙ্গী।"
    const val permissionNeededMic = "কণ্ঠ গণনার জন্য মাইক্রোফোন অনুমতি প্রয়োজন।"
    const val permissionNeededNotif = "রিমাইন্ডারের জন্য নোটিফিকেশন অনুমতি প্রয়োজন।"
}
