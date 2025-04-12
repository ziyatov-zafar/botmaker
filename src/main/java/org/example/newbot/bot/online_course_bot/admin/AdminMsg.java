package org.example.newbot.bot.online_course_bot.admin;

import org.example.newbot.model.online_course_entities.Course;
import org.example.newbot.model.online_course_entities.Lesson;

import static org.example.newbot.bot.StaticVariable.formatPrice;

public class AdminMsg {
    public String menu = "\uD83D\uDD39 *Asosiy Menyu* \uD83D\uDD39";
    public String emptyCourse = "📭 Hozircha hech qanday kurs mavjud emas.\n\n➕ Yangi kurs qo‘shish uchun \"Kurs qo‘shish\" tugmasini bosing.";
    public String courseList = "📚 Barcha kurslar ro‘yxati:\n\nQuyidagi menyudan o‘zingizga keraklisini tanlang.";
    public String getNewCourseName = "📚 Iltimos, yangi kurs nomini kiriting:";
    public String unexpectedErrorForAddNewCourseName = "❗️Kutilmagan xatolik yuz berdi. Iltimos, kurs nomini qaytadan kiriting.";

    public String successAddCourseName = "✅ Kurs nomi muvaffaqiyatli saqlandi!\n\n✏️ Endi esa kurs tavsifini kiriting.";
    public String busyCourseName = "❗️ Bu kurs nomi band.\n🔁 Iltimos, boshqa nom kiriting.";

    public String coursePrice = "✅ Kurs tavsifi muvaffaqiyatli saqlandi!\n\n💰 Endi, ushbu kursning narxini kiriting:";


    public String numberFormatErrorForCoursePrice = "❌ Iltimos, kurs narxini faqat son sifatida kiriting!";

    public String addCourseGroupLink = "✅ Kurs narxi muvaffaqiyatli saqlandi!\n\n💰 Endi, ushbu kursning guruh uchun havolasini kiriting:";


    public String leaveBtnClickForGroupLink = """
            ❌ <b>Ushbu kursga Telegram guruh biriktirilmadi</b>
            
            Iltimos, ushbu kursning yordamchi o'qituvchisining Telegram havolasini yuboring.
            Misol uchun: <code>https://t.me/teacher_username</code>""";

    public String addCourseAddedGroupLink = """
            <b>✅ Ushbu kursga Telegram guruh muvaffaqiyatli biriktirildi</b>
            
            Endi, ushbu kursning yordamchi o'qituvchisining Telegram havolasini yuboring.
            Masalan: <code>https://t.me/teacher_username</code>""";
    public String successAddCourse = "✅ Kurs muvaffaqiyatli qo'shildi!";
    public String cancelOperation = "❌ Operatsiya bekor qilindi";
    public String failedAddCourse = cancelOperation;
    public String wrongBtn = "❗️ Iltimos, tugmalardan foydalaning!";
    public String confirmDelete = "🗑 Kurs muvoffaqiyatli o‘chirildi. Rahmat!";
    public String removeGroupFromCourse = "🚫 Ushbu kursdan guruh muvaffaqiyatli olib tashlandi.";
    public String removeTeacherFromCourse = "🚫 Ushbu kursdan o'qituvchi muvaffaqiyatli olib tashlandi.";
    public String editedCourseName = "✅ Kurs nomi muvaffaqiyatli saqlandi!";
    public String editedCourseDescription = "✅ Kurs tavsifi muvaffaqiyatli saqlandi!";
    public String editedCourseGroup = "✅ Kurs guruhi muvaffaqiyatli saqlandi!";
    public String editedCourseTeacher = "✅ Kurs o'qituvchisi muvaffaqiyatli saqlandi!";
    public String isPresentVideo = "🎥 Ushbu dars uchun yana video bormi?";
    public String addedLesson = "✅ Ushbu dars muvaffaqiyatli qo'shildi!";


    public String editCourseMsg(Course course) {
        return """
                %s
                
                🔄 Quyidagilardan qaysi birini o'zgartirmoqchisiz?
                """.formatted(courseInformation(course));
    }


    public String leaveBtnClickForTeacherLink(Course course) {
        return """
                ❌ Ushbu kursga o'qituvchi biriktirilmadi
                \n
                %s
                """.formatted(courseInformation(course));
    }

    public String addCourseAddedTeacherLink(Course course) {
        return """
                ✅ Ushbu guruhga o'qituvchi muvaffaqiyatli biriktirildi
                \n
                %s
                """.formatted(courseInformation(course));
    }


    private String courseInformation(Course course) {
        return """
                📝 <b>Kurs nomi:</b> %s
                📋 <b>Kurs tavsifi:</b> %s
                🌐 <b>Telegram guruhi:</b> %s
                💼 <b>Yordamchi o'qituvchi:</b> %s
                💰 <b>Kurs narxi:</b> %s
                """.formatted(
                course.getName() != null ? course.getName() : "Mavjud emas",
                course.getDescription() != null ? course.getDescription() : "Tavsif kiritilmagan",
                course.getHasGroup() ? course.getGroupLink() : "Guruh biriktirilmagan",
                course.getHasTeacher() ? course.getTeacherUrl() : "O'qituvchi biriktirilmagan",
                course.getPrice() != null ? formatNumber(course.getPrice()) : "Narx kiritilmagan"
        );
    }

    private String formatNumber(double number) {
        return formatPrice(number);
    }

    public String aboutCourse(Course course) {
        return """
                📚 Ushbu kursning malumotlari:
                
                %s
                """.formatted(courseInformation(course));
    }

    public String isDeleteMsg(Course course) {
        return courseInformation(course) + "\n\n⚠️Siz ushbu kursni o‘chirmoqchi ekanligingizga ishonchingiz komilmi?";
    }

    public String getCourseNameForEdit(Course course) {
        return String.format("""
                ✏️ Yangi kurs nomini kiriting:
                
                Avvalgi nomi: <code>%s</code>
                """, course.getName());
    }

    public String getCourseDescriptionForEdit(Course course) {
        return String.format("""
                ✏️ Yangi kurs tavsifini kiriting:
                
                Avvalgi tavsif: <code>%s</code>
                """, course.getDescription());

    }

    public String getCoursePriceForEdit(Course course) {
        return String.format("""
                ✏️ Yangi kurs narxini kiriting:
                
                Avvalgi narx: <code>%s</code>
                """, formatNumber(course.getPrice()));

    }

    public String getCourseGroupForEdit(Course course, boolean addGroupBtn) {
        if (addGroupBtn) {
            return String.format("""
                    🔗 %s kursi uchun guruh havolasini yuboring:
                    📝 Masalan: https://t.me/group_username
                    """, course.getName());
        } else {
            return String.format("""
                    ✏️ Yangi kurs guruhining havolasini yuboring:
                    
                    🔄 Avvalgi havola: <code>%s</code>
                    """, course.getGroupLink());
        }
    }

    public String getCourseTeacherForEdit(Course course, boolean addTeacherBtn) {
        if (addTeacherBtn) {
            return String.format("""
                    🔗 %s kursi uchun o'qituvchi havolasini yuboring:
                    📝 Masalan: https://t.me/teacher_username
                    """, course.getName());
        } else {
            return String.format("""
                    ✏️ Yangi kurs o'qituvchisining havolasini yuboring:
                    
                    🔄 Avvalgi havola: <code>%s</code>
                    """, course.getTeacherUrl());
        }
    }

    public String emptyLessonOfCourse(Course course) {
        return String.format("""
                📚 %s kursining darslari mavjud emas.
                
                ➕ Dars qo'shish uchun pastdagi "Dars qo'shish" tugmasini bosing.
                """, course.getName());
    }

    public String getLessonsOfCourse(Course course) {
        return String.format("""
                📚 %s kursining darslar ro'yxati:
                """, course.getName());
    }

    public String addLessonName(Course course) {
        return String.format("""
                ➕ %s kursiga dars qo'shish uchun yangi dars nomini kiriting:
                """, course.getName());
    }

    public String busyLessonName(Course course, String text) {
        return String.format("""
                ⚠️ %s kursining '%s' nomli darsi avvaldan mavjud. 
                Iltimos, boshqa nom kiriting.
                """, course.getName(), text);
    }

    public String addedLessonName() {
        return """
                ✅ Dars nomi muvaffaqiyatli saqlandi.
                ➕ Endi ushbu dars uchun tavsif kiriting.
                """;
    }

    public String addedLessonDesc() {
        return """
                ✅ Dars tavsifi muvaffaqiyatli saqlandi.
                🎥 Endi ushbu darsning vidyosini yuboring.
                """;
    }

    public String getVideoMsg() {
        return """
                🎥 Endi ushbu darsning vidyosini yuboring.
                """;
    }

    public String notVideo() {
        return "⚠️ Iltimos, dars vidyosini video ko‘rinishida yuboring.";
    }

    public String getNewHomeworkForLesson() {
        return """
                📝 Ushbu dars uchun uyga vazifani kiriting:
                """;
    }

    public String isOpen() {
        return """
                🔓 Ushbu darsni ochiq qilishni istaysizmi ?
                💡 Misol uchun, foydalanuvchilar darslaringizni ko‘rayotganda sinov darsi sifatida ba'zi darslaringizni ko‘rishni istaganda, ushbu darsingizdan foydalanishi mumkin.
                """;
    }

    public String isAddLessonMsg(Lesson lesson, Course course) {
        return String.format("""
                📚 Ushbu darsning ma'lumotlari:
                
                %s
                
                🔄 Haqiqatdan ham ushbu darsni qo'shmoqchimisiz?
                """, aboutLesson(lesson, course));
    }


    private String aboutLesson(Lesson lesson, Course course) {
        return String.format("""
                        📚 Dars nomi: %s
                        📝 Dars tavsifi: %s
                        🏠 Dars uyga vazifasi: %s
                        🔐 Dars turi: %s
                        📘 Qaysi kursga tegishli: %s
                        """,
                lesson.getName(),
                lesson.getDescription(),
                lesson.getHasHomework() ? lesson.getHomework() : "Uyga vazifa berilmagan",
                lesson.getFree() ? "Ochiq" : "Yopiq",
                course.getName()
        );
    }

    public String lessonInformation(Lesson lesson, Course course) {
        return String.format("""
                📚 Ushbu darsning ma'lumotlari:
                
                %s
                """, aboutLesson(lesson, course));

    }
}
