package org.example.newbot.bot.online_course_bot;

public class OnlineCourseConstVariables {
    public static final String[] menu = {
            "📚 *Kurslar*",
            "👤 *Foydalanuvchilar*",
            "💳 *Buyurtmalar*",
            "📈 *Statistika*",
            "📢 *Xabar yuborish*",
            "⚙️ *Sozlamalar*"};
    public static final String backBtn = "🔙 Orqaga";
    public static final String toBackBtn = "🔙 Orqaga qaytish";
    public static final String mainBtn = "🏠 Asosiy menyu";
    public static final String addCourse = "➕ Kurs qo‘shish";
    public static final String addLesson = "➕ Dars qo‘shish";
    public static final String viewCourseLessons = "📚 Darslar ro‘yxati";
    public static final String viewLessonVideos = "\uD83D\uDCF9 Videolar ro‘yxati";
    public static final String addVideo = "\uD83D\uDCF9 Video qo'shish";
    public static final String editCourse = "✏️ Kursni tahrirlash";
    public static final String editLesson = "✏️ Darsni tahrirlash";
    public static final String deleteCourse = "🗑 Kursni o‘chirish";
    public static final String deleteLesson = "🗑 Darsni o‘chirish";
    public static final String confirm = "✅ Tasdiqlash";
    public static final String cancel = "❌ Bekor qilish";
    public static final String leaveBtn = "\uD83D\uDEAB Tashlab ketish";
    public static final String[] courseCrudBtn = {
            addCourse,
            editCourse,
            deleteCourse,
            viewCourseLessons
    };
    public static final String[] lessonCrudBtn = {
            addLesson,
            editLesson,
            deleteLesson,
            viewLessonVideos
    };
    public static final String[] editCourseBtn = {
            "✏️ Kurs nomini o'zgartirish",
            "✏️ Kurs tavsifini o'zgartirish",
            "💰 Kurs narxini o'zgartirish",
            "➕ Guruh qo'shish",
            "🔗 Guruhni o'zgartirish",
            "❌ Guruhni olib tashlash",
            "➕ O'qituvchi qo'shish",
            "👨‍🏫 O'qituvchini o'zgartirish",
            "❌ O'qituvchini olib tashlash"
    };
    public static String[] editLessonBtn(Boolean isOpen){
        return new String[]{
                "✏️ Dars nomini o'zgartirish",
                "✏️ Dars tavsifini o'zgartirish",
                "✏️ Uyga vazifani o'zgartirish",
                isOpen ? "\uD83D\uDD12 Ushbu darsni yopiq qilish":"\uD83D\uDD13 Ushbu darsni ochiq qilish"
        };
    }
    public static String questionEmoji = "❓";
    //------------------------------------------------------------------------------------------------------
    // user role
    public static String[] userMenuBtn = {
            "📚 Barcha mavjud kurslar",
            "🎓 Mening kurslarim",
            "💸 Yangi kurs sotib olish",
            "🆘 Yordam / Aloqa"
    };
}