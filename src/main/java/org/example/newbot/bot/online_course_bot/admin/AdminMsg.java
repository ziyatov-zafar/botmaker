package org.example.newbot.bot.online_course_bot.admin;

import org.example.newbot.model.online_course_entities.Course;

import static org.example.newbot.bot.StaticVariable.formatPrice;

public class AdminMsg {
    public String menu = "\uD83D\uDD39 *Asosiy Menyu* \uD83D\uDD39";
    public String emptyCourse = "📭 Hozircha hech qanday kurs mavjud emas.\n\n➕ Yangi kurs qo‘shish uchun \"Kurs qo‘shish\" tugmasini bosing.";
    public String courseList = "📚 Barcha kurslar ro‘yxati:\n\nQuyidagi menyudan o‘zingizga keraklisini tanlang.";
    public String getNewCourseName = "📚 Iltimos, yangi kurs nomini kiriting:";
    public String unexpectedErrorForAddNewCourseName = "❗️Kutilmagan xatolik yuz berdi. Iltimos, kurs nomini qaytadan kiriting.";

    public String successAddCourseName = "✅ Kurs nomi muvaffaqiyatli saqlandi!\n\n✏️ Endi esa kurs tavsifini kiriting.";
    public String busyCourseName = "❗️ Bu kurs nomi allaqachon band.\n🔁 Iltimos, boshqa nom kiriting.";

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
    public String failedAddCourse = "❌ Operatsiya bekor qilindi";
    public String wrongBtn = "❗️ Iltimos, tugmalardan foydalaning!";

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
                course.getGroupLink() != null ? course.getGroupLink() : "Guruh biriktirilmagan",
                course.getTeacherUrl() != null ? course.getTeacherUrl() : "O'qituvchi biriktirilmagan",
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

}
