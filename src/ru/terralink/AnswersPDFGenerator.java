package ru.terralink;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aksndr on 08.01.2016.
 */
public class AnswersPDFGenerator {

    public AnswersPDFGenerator() {}


    private List<HashMap<String, String>> answers = new ArrayList<>();
    private HashMap<String, String> params = new HashMap<>();

    private static Font chapterFont;
    private static Font paragraphFont;
    private static Font normalFont;
    private static Font chapterFontN;
    private static Font paragraphFontN;
    private static Font normalFontN;


    public Map<String, Object> init(){


        try {
            final BaseFont bf = BaseFont.createFont("c:/Windows/Fonts/tahoma.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            chapterFont = new Font(bf, 14,Font.BOLD);
            paragraphFont = new Font(bf, 12,Font.BOLD);
            normalFont = new Font(bf, 8,Font.BOLD);
            chapterFontN = new Font(bf, 14,Font.NORMAL);
            paragraphFontN = new Font(bf, 12,Font.NORMAL);
            normalFontN = new Font(bf, 8,Font.NORMAL);

        } catch (DocumentException |IOException e) {
            return failed(e.toString());
        }

        return succeed();
    }

    public Map<String, Object> setParams(HashMap<String, String> params){
        this.params = params;
        return succeed();
    }

    public Map<String, Object> addAnswer(HashMap<String, String> answer){
        answers.add(answer);
        return succeed();
    }

    public Map<String, Object> generatePdf(){
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, bos);
            document.open();
            document.add(createChapter());
            document.add(new Paragraph(" "));
            document.add(createTable());
            //document.newPage();
            document.close();
            bos.close();
        } catch (DocumentException | IOException e) {
            return failed(e.toString());
        }
        return succeed(bos.toByteArray());
    }
    public Map<String, Object> generateQuestPdf(){
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, bos);
            document.open();
            createQuestPdf(document);
            document.close();
            bos.close();
        } catch (DocumentException | IOException e) {
            return failed(e.toString());
        }
        return succeed(bos.toByteArray());
    }
    private void createQuestPdf(Document document) throws DocumentException {
        Anchor anchor = new Anchor("ОПРОСНЫЙ ЛИСТ\n" + "для заочного голосования", chapterFont);
        Paragraph title = new Paragraph(anchor);
        title.setAlignment(Element.ALIGN_CENTER);
        Chapter chapter = new Chapter(title, 1);
        chapter.setNumberDepth(0);
        chapter.add(new Paragraph(" ", paragraphFont));
        String tmpStr = (params.get("subject") == null) ? "" : params.get("subject");
        chapter.add(new Paragraph("Предмет закупки: " + tmpStr, paragraphFont));
        tmpStr = (params.get("event") == null) ? "" : params.get("event");
        chapter.add(new Paragraph("Вид мероприятия: " + tmpStr, paragraphFont));
        tmpStr = (params.get("initiator") == null) ? "" : params.get("initiator");
        chapter.add(new Paragraph("Инициатор: " + tmpStr, paragraphFont));
        chapter.add(new Paragraph("Голосующий: " ,paragraphFont));

        document.add(chapter);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("ВОПРОСЫ ДЛЯ ГОЛОСОВАНИЯ:",chapterFont));
        document.add(new Paragraph(" "));
        int cnt = 1;
        for (HashMap<String,String> answer : answers) {
            String tmpstr = Integer.toString(cnt++)+". "+answer.get("QUESTION");
            document.add(new Paragraph(tmpstr, paragraphFontN));
        }
        document.add(new Paragraph(" "));
        document.add(new Paragraph("РЕШИЛИ:",chapterFont));
        document.add(new Paragraph(" "));
        cnt = 1;
        for (HashMap<String,String> answer : answers) {
            String tmpstr = Integer.toString(cnt++)+". "+answer.get("SOLUTION");
            document.add(new Paragraph(tmpstr, paragraphFontN));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("ИНФОРМАЦИЯ (КОММЕНТАРИЙ) ПО ВОПРОСУ: ", paragraphFontN));
            tmpstr = answer.get("POSITION");
            document.add(new Paragraph(tmpstr, paragraphFontN));
            document.add(new Paragraph(" "));
            document.add(createQuesTable());
        }
    }

    private Element createChapter() throws UnsupportedEncodingException {
        Anchor anchor = new Anchor("Опросный лист", chapterFont);
        Paragraph title = new Paragraph(anchor);
        title.setAlignment(Element.ALIGN_CENTER);
        Chapter chapter = new Chapter(title, 1);
        chapter.setNumberDepth(0);
        chapter.add(new Paragraph(" ", paragraphFont));
        chapter.add(new Paragraph("Предмет закупки: " + params.get("subject"), paragraphFont));
        chapter.add(new Paragraph("Вид мероприятия: " + params.get("event"), paragraphFont));
        chapter.add(new Paragraph("Инициатор: " + params.get("initiator"), paragraphFont));
        chapter.add(new Paragraph("Голосующий: " + params.get("voter"),paragraphFont));

        return chapter;
    }

    private Element createTable() throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        PdfPCell cell0 = new PdfPCell(new Paragraph(NUMBER, normalFont));
        cell0.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell0.setColspan(1);
        PdfPCell cell1 = new PdfPCell(new Paragraph(QUESTION, normalFont));
        cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        PdfPCell cell2 = new PdfPCell(new Paragraph(POSITION, normalFont));
        cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        PdfPCell cell3 = new PdfPCell(new Paragraph(SOLUTION, normalFont));
        cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
        PdfPCell cell4 = new PdfPCell(new Paragraph(DECISION, normalFont));
        cell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
        PdfPCell cell5 = new PdfPCell(new Paragraph(COMMENT, normalFont));
        cell5.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell0);
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);
        int cnt = 1;
        for (HashMap<String,String> answer : answers) {

            table.addCell(new PdfPCell(new Paragraph(Integer.toString(cnt), normalFont)));
            table.addCell(new PdfPCell(new Paragraph(answer.get("QUESTION"), normalFont)));
            table.addCell(new PdfPCell(new Paragraph(answer.get("POSITION"), normalFont)));
            table.addCell(new PdfPCell(new Paragraph(answer.get("SOLUTION"), normalFont)));
            table.addCell(new PdfPCell(new Paragraph(answer.get("DECISION"), normalFont)));
            table.addCell(new PdfPCell(new Paragraph(answer.get("DECISIONCOMMENT"), normalFont)));
            cnt++;
        }
        float[] columnWidths = new float[]{5f, 30f, 30f, 30f, 15f, 30f};
        table.setWidths(columnWidths);
        return table;
    }
    private Element createQuesTable() throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        PdfPCell cell0 = new PdfPCell(new Paragraph("За", normalFont));
        cell0.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell0.setColspan(1);
        PdfPCell cell1 = new PdfPCell(new Paragraph("Против", normalFont));
        cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        PdfPCell cell2 = new PdfPCell(new Paragraph("Воздержался", normalFont));
        cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        PdfPCell cell3 = new PdfPCell(new Paragraph("Дата", normalFont));
        cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
        PdfPCell cell4 = new PdfPCell(new Paragraph("Подпись", normalFont));
        cell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
        PdfPCell cell5 = new PdfPCell(new Paragraph("Ф.И.О.", normalFont));
        cell5.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell0);
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);
        for (int i = 0;i<6; i++) {
            PdfPCell blank = new PdfPCell(new Phrase(""));
            blank.setFixedHeight(36f);
            table.addCell(blank);
        }
        PdfPCell blank = new PdfPCell(new Phrase(""));
        blank.setColspan(6);
        blank.setFixedHeight(36f);
        table.addCell(blank);

        float[] columnWidths = new float[]{10f, 10f, 12f, 18f, 20f, 35f};
        table.setWidths(columnWidths);
        return table;
    }


    private static Map<String, Object> succeed(){
        Map<String, Object> result = new HashMap<>();
        result.put("ok", true);
        return result;
    }

    private static Map<String, Object> succeed(Object value){
        Map<String, Object> result = new HashMap<>();
        result.put("ok", true);
        result.put("value", value);
        return result;
    }

    private static Map<String, Object> failed(String errMsg){
        Map<String, Object> result = new HashMap<>();
        result.put("ok", false);
        result.put("errMsg", errMsg);
        return result;
    }

    private static final String COMMENT = "Комментарий";
    private static final String POSITION = "Пояснение";
    private static final String SOLUTION = "Проект решения";
    private static final String DECISION = "Решение";
    private static final String QUESTION = "Вопрос";
    private static final String NUMBER = "№";
}
