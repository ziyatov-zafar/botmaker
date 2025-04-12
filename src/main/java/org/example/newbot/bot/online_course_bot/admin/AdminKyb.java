package org.example.newbot.bot.online_course_bot.admin;

import org.example.newbot.bot.*;
import org.example.newbot.bot.online_course_bot.OnlineCourseConstVariables;
import org.example.newbot.bot.online_course_bot.OnlineCourseConstVariables.*;
import org.example.newbot.model.online_course_entities.Course;
import org.example.newbot.model.online_course_entities.Lesson;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static org.example.newbot.bot.online_course_bot.OnlineCourseConstVariables.*;

public class AdminKyb extends Kyb {
    public ReplyKeyboardMarkup menu = setKeyboards(OnlineCourseConstVariables.menu, 2);
    public ReplyKeyboardMarkup leaveBtn = setKeyboards(new String[]{OnlineCourseConstVariables.leaveBtn}, 1);
    public ReplyKeyboardMarkup isSuccessDelete = setKeyboards(new String[]{confirm, cancel}, 2);
    public ReplyKeyboardMarkup isSuccess = setKeyboards(new String[]{confirm, cancel}, 2);
    public ReplyKeyboardMarkup toBack = setKeyboards(new String[]{toBackBtn}, 1);

    public ReplyKeyboardMarkup courseList(List<Course> courses, boolean empty) {
        if (empty) {
            KeyboardRow row = new KeyboardRow();
            row.add(addCourse);
            List<KeyboardRow> rows = new ArrayList<>();
            rows.add(row);
            return addBackAndMainBtn(rows);
        }
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            row.add(course.getName());
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        row = new KeyboardRow();
        row.add(addCourse);
        rows.add(row);
        return addBackAndMainBtn(rows);
    }

    public ReplyKeyboardMarkup addBackAndMainBtn(List<KeyboardRow> rows) {
        KeyboardRow row = new KeyboardRow();
        row.add(OnlineCourseConstVariables.backBtn);
        row.add(mainBtn);
        rows.add(row);
        return markup(rows);
    }

    public ReplyKeyboardMarkup backBtn() {
        KeyboardRow row = new KeyboardRow();
        row.add(OnlineCourseConstVariables.backBtn);
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row);
        return markup(rows);
    }

    public ReplyKeyboardMarkup courseCrud() {
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < courseCrudBtn.length; i++) {
            row.add(courseCrudBtn[i]);
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        return addBackAndMainBtn(rows);
    }

    /* public ReplyKeyboardMarkup editCourse(Boolean hasGroupLink, Boolean hasTeacherLink) {
         List<KeyboardRow> rows = new ArrayList<>();

         // Kurs nomini tahrirlash
         KeyboardRow row1 = new KeyboardRow();
         row1.add(editCourseBtn[0]);
         rows.add(row1);

         // Kurs tavsifini tahrirlash
         KeyboardRow row2 = new KeyboardRow();
         row2.add(editCourseBtn[1]);
         rows.add(row2);

         // Kurs narxini tahrirlash
         KeyboardRow row3 = new KeyboardRow();
         row3.add(editCourseBtn[2]);
         rows.add(row3);

         // Agar guruh linki mavjud bo'lmasa, guruhni qo'shish
         if (!hasGroupLink) {
             KeyboardRow row4 = new KeyboardRow();
             row4.add(editCourseBtn[3]);  // Guruhni qo'shish
             rows.add(row4);
         } else {
             // Agar guruh linki mavjud bo'lsa, guruhni tahrirlash va olib tashlash
             KeyboardRow row4 = new KeyboardRow();
             row4.add(editCourseBtn[4]);  // Guruhni o'zgartirish
             row4.add(editCourseBtn[5]);  // Guruhni olib tashlash
             rows.add(row4);
         }

         // Agar o'qituvchi linki mavjud bo'lmasa, o'qituvchini qo'shish
         if (!hasTeacherLink) {
             KeyboardRow row5 = new KeyboardRow();
             row5.add(editCourseBtn[6]);  // O'qituvchini qo'shish
             rows.add(row5);
         } else {
             // Agar o'qituvchi linki mavjud bo'lsa, o'qituvchini tahrirlash va olib tashlash
             KeyboardRow row5 = new KeyboardRow();
             row5.add(editCourseBtn[7]);  // O'qituvchini o'zgartirish
             row5.add(editCourseBtn[8]);  // O'qituvchini olib tashlash
             rows.add(row5);
         }
         return addBackAndMainBtn(rows);
     }*/
    public ReplyKeyboardMarkup editCourse(Boolean hasGroupLink, Boolean hasTeacherLink) {
        List<KeyboardRow> rows = new ArrayList<>();

        // Kurs nomi va tavsifi va narxini tahrirlash — 2 qatorga joylash
        KeyboardRow row1 = new KeyboardRow();
        row1.add(editCourseBtn[0]); // Kurs nomi
        row1.add(editCourseBtn[1]); // Kurs tavsifi
        rows.add(row1);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(editCourseBtn[2]); // Kurs narxi
        rows.add(row2);

        // Guruh linki bo'yicha tugmalar
        if (!hasGroupLink) {
            KeyboardRow row3 = new KeyboardRow();
            row3.add(editCourseBtn[3]); // Guruhni qo‘shish
            rows.add(row3);
        } else {
            KeyboardRow row3 = new KeyboardRow();
            row3.add(editCourseBtn[4]); // Guruhni o‘zgartirish
            row3.add(editCourseBtn[5]); // Guruhni olib tashlash
            rows.add(row3);
        }

        // O'qituvchi bo'yicha tugmalar
        if (!hasTeacherLink) {
            KeyboardRow row4 = new KeyboardRow();
            row4.add(editCourseBtn[6]); // O‘qituvchini qo‘shish
            rows.add(row4);
        } else {
            KeyboardRow row4 = new KeyboardRow();
            row4.add(editCourseBtn[7]); // O‘qituvchini o‘zgartirish
            row4.add(editCourseBtn[8]); // O‘qituvchini olib tashlash
            rows.add(row4);
        }

        return addBackAndMainBtn(rows);
    }


    public ReplyKeyboardMarkup setLessons(List<Lesson> lessons, boolean empty) {
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        if (empty) {
            row.add(addLesson);
            rows.add(row);
            return addBackAndMainBtn(rows);
        }
        int c = 0;
        for (Lesson lesson : lessons) {
            c++;
            row.add(lesson.getName());
            if (c == 2) {
                rows.add(row);
                row = new KeyboardRow();
                c = 0;
            }
        }
        rows.add(row);
        row = new KeyboardRow();
        row.add(addLesson);
        rows.add(row);
        return addBackAndMainBtn(rows);
    }

    public ReplyKeyboardMarkup lessonCrud() {
        KeyboardRow row = new KeyboardRow();
        List<KeyboardRow> rows = new ArrayList<>();
        for (int i = 0; i < lessonCrudBtn.length; i++) {
            row.add(lessonCrudBtn[i]);
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
        return addBackAndMainBtn(rows);
    }
}
